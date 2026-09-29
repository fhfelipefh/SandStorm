package com.fhfelipefh.sandstorm.content.clone;

import com.fhfelipefh.sandstorm.content.clone.CloneNetworkSavedData.ClonePodRecord;
import com.fhfelipefh.sandstorm.content.clone.CloneNetworkSavedData.PlayerEntry;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CloneNetworkSavedDataTest {

    @Test
    void shouldConstructAndExposeClonePodRecordProperties() {
        BlockPos pos = new BlockPos(120, 64, -250);
        ClonePodRecord record = new ClonePodRecord(pos, "minecraft:overworld", "Alpha Sanctuary", true, 5000L);

        assertEquals(pos, record.pos());
        assertEquals("minecraft:overworld", record.dimensionId());
        assertEquals("Alpha Sanctuary", record.name());
        assertTrue(record.hasClone());
        assertEquals(5000L, record.lastSeen());
        assertNotNull(ClonePodRecord.CODEC);
    }

    @Test
    void shouldConstructPlayerEntryAndExposeCodec() {
        UUID owner = UUID.randomUUID();
        ClonePodRecord record = new ClonePodRecord(new BlockPos(10, 64, 10), "minecraft:overworld", "Base 1", true, 100L);
        PlayerEntry entry = new PlayerEntry(owner, List.of(record));

        assertEquals(owner, entry.uuid());
        assertEquals(1, entry.pods().size());
        assertEquals("Base 1", entry.pods().get(0).name());
        assertNotNull(PlayerEntry.CODEC);
    }

    @Test
    void shouldHandleEntriesRoundTripSerialization() {
        CloneNetworkSavedData original = new CloneNetworkSavedData();
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();

        original.registerPod(player1, new BlockPos(0, 64, 0), "minecraft:overworld", "Central Hub", true, 1000L);
        original.registerPod(player1, new BlockPos(100, 64, 100), "minecraft:the_nether", "Nether Outpost", false, 1200L);
        original.registerPod(player2, new BlockPos(500, 70, 500), "minecraft:overworld", "Mining Station", true, 2000L);

        List<PlayerEntry> entries = original.getEntries();
        assertEquals(2, entries.size());

        CloneNetworkSavedData restored = CloneNetworkSavedData.fromEntries(entries);
        assertEquals(2, restored.getEntries().size());

        Optional<ClonePodRecord> nearest1 = restored.findNearestReadyPod(player1, new BlockPos(10, 64, 10), "minecraft:overworld");
        assertTrue(nearest1.isPresent());
        assertEquals("Central Hub", nearest1.get().name());

        Optional<ClonePodRecord> nearest2 = restored.findNearestReadyPod(player2, new BlockPos(510, 70, 510), "minecraft:overworld");
        assertTrue(nearest2.isPresent());
        assertEquals("Mining Station", nearest2.get().name());
    }

    @Test
    void shouldIgnoreNullInputsGracefully() {
        CloneNetworkSavedData data = new CloneNetworkSavedData();
        UUID owner = UUID.randomUUID();
        BlockPos pos = new BlockPos(10, 64, 10);

        data.registerPod(null, pos, "minecraft:overworld", "Test", true, 100L);
        data.registerPod(owner, null, "minecraft:overworld", "Test", true, 100L);
        data.registerPod(owner, pos, null, "Test", true, 100L);

        assertTrue(data.getEntries().isEmpty());
        assertTrue(data.getPodsForPlayer(null).isEmpty());
        assertTrue(data.getPodsForPlayer(owner).isEmpty());
        assertFalse(data.findNearestReadyPod(null, pos, "minecraft:overworld").isPresent());
        assertFalse(data.findNearestReadyPod(owner, null, "minecraft:overworld").isPresent());
        assertFalse(data.findNearestReadyPod(owner, pos, null).isPresent());

        data.updateCloneStatus(null, pos, "minecraft:overworld", true, 200L);
        data.updateCloneStatus(owner, null, "minecraft:overworld", true, 200L);
        data.updateCloneStatus(owner, pos, null, true, 200L);
        data.unregisterPod(null, "minecraft:overworld");
        data.unregisterPod(pos, null);
    }

    @Test
    void shouldReplaceExistingPodWhenRegisteringAtSamePositionAndDimension() {
        CloneNetworkSavedData data = new CloneNetworkSavedData();
        UUID owner = UUID.randomUUID();
        BlockPos pos = new BlockPos(50, 64, 50);

        data.registerPod(owner, pos, "minecraft:overworld", "Initial Pod", false, 1000L);
        data.registerPod(owner, pos, "minecraft:overworld", "Upgraded Pod", true, 2000L);

        List<PlayerEntry> entries = data.getEntries();
        assertEquals(1, entries.size());
        assertEquals(1, entries.get(0).pods().size());

        ClonePodRecord record = entries.get(0).pods().get(0);
        assertEquals("Upgraded Pod", record.name());
        assertTrue(record.hasClone());
        assertEquals(2000L, record.lastSeen());
        assertEquals(1, data.getPodsForPlayer(owner).size());
    }

    @Test
    void shouldUpdateCloneStatusCorrectly() {
        CloneNetworkSavedData data = new CloneNetworkSavedData();
        UUID owner = UUID.randomUUID();
        BlockPos pos = new BlockPos(30, 64, 30);

        data.registerPod(owner, pos, "minecraft:overworld", "Standby Pod", false, 100L);
        assertFalse(data.findNearestReadyPod(owner, new BlockPos(30, 64, 30), "minecraft:overworld").isPresent());

        data.updateCloneStatus(owner, pos, "minecraft:overworld", true, 500L);
        Optional<ClonePodRecord> readyPod = data.findNearestReadyPod(owner, new BlockPos(30, 64, 30), "minecraft:overworld");
        assertTrue(readyPod.isPresent());
        assertTrue(readyPod.get().hasClone());
        assertEquals(500L, readyPod.get().lastSeen());
    }

    @Test
    void shouldUnregisterPodAcrossDimensions() {
        CloneNetworkSavedData data = new CloneNetworkSavedData();
        UUID owner = UUID.randomUUID();
        BlockPos pos = new BlockPos(40, 64, 40);

        data.registerPod(owner, pos, "minecraft:overworld", "Overworld Pod", true, 100L);
        data.registerPod(owner, pos, "minecraft:the_nether", "Nether Pod", true, 100L);

        data.unregisterPod(pos, "minecraft:overworld");
        assertFalse(data.findNearestReadyPod(owner, pos, "minecraft:overworld").isPresent());
        assertTrue(data.findNearestReadyPod(owner, pos, "minecraft:the_nether").isPresent());
    }
}
