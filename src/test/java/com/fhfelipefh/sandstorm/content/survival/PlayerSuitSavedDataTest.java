package com.fhfelipefh.sandstorm.content.survival;

import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerSuitSavedDataTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldStoreAndRetrievePlayerSuitData() {
        PlayerSuitSavedData data = new PlayerSuitSavedData();
        UUID playerUuid = UUID.randomUUID();

        data.setSuitData(playerUuid, 100000L, 37.0);
        PlayerSuitSavedData.Entry entry = data.getSuitData(playerUuid);

        assertNotNull(entry);
        assertEquals(playerUuid, entry.uuid());
        assertEquals(100000L, entry.energy());
        assertEquals(37.0, entry.temperature(), 0.001);
    }

    @Test
    void shouldPersistFullBatteryState() {
        PlayerSuitSavedData data = new PlayerSuitSavedData();
        UUID playerUuid = UUID.randomUUID();
        long fullBattery = 100000L;
        double ambientTemp = 48.5;

        data.setSuitData(playerUuid, fullBattery, ambientTemp);
        PlayerSuitSavedData.Entry entry = data.getSuitData(playerUuid);

        assertNotNull(entry);
        assertEquals(100000L, entry.energy());
        assertEquals(48.5, entry.temperature(), 0.001);
    }

    @Test
    void shouldSerializeAndDeserializeViaCodec() {
        PlayerSuitSavedData original = new PlayerSuitSavedData();
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();

        original.setSuitData(player1, 100000L, 37.0);
        original.setSuitData(player2, 50000L, 42.5);

        var encoded = PlayerSuitSavedData.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();
        PlayerSuitSavedData restored = PlayerSuitSavedData.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        PlayerSuitSavedData.Entry restored1 = restored.getSuitData(player1);
        assertNotNull(restored1);
        assertEquals(100000L, restored1.energy());
        assertEquals(37.0, restored1.temperature(), 0.001);

        PlayerSuitSavedData.Entry restored2 = restored.getSuitData(player2);
        assertNotNull(restored2);
        assertEquals(50000L, restored2.energy());
        assertEquals(42.5, restored2.temperature(), 0.001);
    }

    @Test
    void shouldHandleMultiplePlayersIndependently() {
        PlayerSuitSavedData data = new PlayerSuitSavedData();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();

        data.setSuitData(p1, 100000L, 37.0);
        data.setSuitData(p2, 0L, 20.0);
        data.setSuitData(p3, 75000L, 50.0);

        assertEquals(100000L, data.getSuitData(p1).energy());
        assertEquals(0L, data.getSuitData(p2).energy());
        assertEquals(75000L, data.getSuitData(p3).energy());

        assertEquals(3, data.getEntries().size());
        assertEquals(3, data.getAllSuitData().size());
    }

    @Test
    void shouldRemovePlayerSuitData() {
        PlayerSuitSavedData data = new PlayerSuitSavedData();
        UUID playerUuid = UUID.randomUUID();

        data.setSuitData(playerUuid, 100000L, 37.0);
        assertNotNull(data.getSuitData(playerUuid));

        data.removeSuitData(playerUuid);
        assertNull(data.getSuitData(playerUuid));
        assertTrue(data.getEntries().isEmpty());
    }

    @Test
    void shouldConvertFromAndToEntries() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        List<PlayerSuitSavedData.Entry> entries = List.of(
                new PlayerSuitSavedData.Entry(p1, 100000L, 36.5),
                new PlayerSuitSavedData.Entry(p2, 25000L, 15.0)
        );

        PlayerSuitSavedData data = PlayerSuitSavedData.fromEntries(entries);
        assertEquals(2, data.getEntries().size());
        assertEquals(100000L, data.getSuitData(p1).energy());
        assertEquals(25000L, data.getSuitData(p2).energy());
    }
}
