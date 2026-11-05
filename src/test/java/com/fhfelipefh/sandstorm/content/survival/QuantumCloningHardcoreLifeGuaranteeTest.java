package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
import com.fhfelipefh.sandstorm.content.clone.CloneNetworkSavedData;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuantumCloningHardcoreLifeGuaranteeTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldGuaranteeMultipleLivesProgressionThroughCloningPods() {
        CloneNetworkSavedData networkData = new CloneNetworkSavedData();
        UUID playerId = UUID.randomUUID();
        BlockPos pod1 = new BlockPos(100, 64, 100);
        BlockPos pod2 = new BlockPos(200, 64, 200);
        BlockPos pod3 = new BlockPos(300, 64, 300);
        BlockPos pod4 = new BlockPos(400, 64, 400);
        String dimension = "minecraft:overworld";

        networkData.registerPod(playerId, pod1, dimension, "Pod 1 - Alpha", true, 1000L);
        networkData.registerPod(playerId, pod2, dimension, "Pod 2 - Beta", true, 1000L);
        networkData.registerPod(playerId, pod3, dimension, "Pod 3 - Gamma", true, 1000L);
        networkData.registerPod(playerId, pod4, dimension, "Pod 4 - Delta", true, 1000L);

        BlockPos playerDeathPos = new BlockPos(105, 64, 105);

        Optional<CloneNetworkSavedData.ClonePodRecord> life2 = networkData.findNearestReadyPod(playerId, playerDeathPos, dimension);
        assertTrue(life2.isPresent());
        assertEquals(pod1, life2.get().pos());
        networkData.updateCloneStatus(playerId, pod1, dimension, false, 1100L);

        Optional<CloneNetworkSavedData.ClonePodRecord> life3 = networkData.findNearestReadyPod(playerId, playerDeathPos, dimension);
        assertTrue(life3.isPresent());
        assertEquals(pod2, life3.get().pos());
        networkData.updateCloneStatus(playerId, pod2, dimension, false, 1200L);

        Optional<CloneNetworkSavedData.ClonePodRecord> life4 = networkData.findNearestReadyPod(playerId, playerDeathPos, dimension);
        assertTrue(life4.isPresent());
        assertEquals(pod3, life4.get().pos());
        networkData.updateCloneStatus(playerId, pod3, dimension, false, 1300L);

        Optional<CloneNetworkSavedData.ClonePodRecord> life5 = networkData.findNearestReadyPod(playerId, playerDeathPos, dimension);
        assertTrue(life5.isPresent());
        assertEquals(pod4, life5.get().pos());
        networkData.updateCloneStatus(playerId, pod4, dimension, false, 1400L);

        Optional<CloneNetworkSavedData.ClonePodRecord> exhausted = networkData.findNearestReadyPod(playerId, playerDeathPos, dimension);
        assertFalse(exhausted.isPresent());

        networkData.updateCloneStatus(playerId, pod1, dimension, true, 1500L);
        Optional<CloneNetworkSavedData.ClonePodRecord> life6 = networkData.findNearestReadyPod(playerId, playerDeathPos, dimension);
        assertTrue(life6.isPresent());
        assertEquals(pod1, life6.get().pos());
    }

    @Test
    void shouldFallbackToReadyPodAcrossDimensionsInHardcore() {
        CloneNetworkSavedData networkData = new CloneNetworkSavedData();
        UUID playerId = UUID.randomUUID();
        BlockPos overworldPod = new BlockPos(50, 70, 50);
        networkData.registerPod(playerId, overworldPod, "minecraft:overworld", "Overworld Safehouse", true, 500L);

        BlockPos netherDeathPos = new BlockPos(1000, 30, 1000);
        Optional<CloneNetworkSavedData.ClonePodRecord> crossDimLife = networkData.findNearestReadyPod(playerId, netherDeathPos, "minecraft:the_nether");
        if (crossDimLife.isEmpty()) {
            crossDimLife = networkData.findAnyReadyPod(playerId);
        }

        assertTrue(crossDimLife.isPresent());
        assertEquals(overworldPod, crossDimLife.get().pos());
        assertEquals("minecraft:overworld", crossDimLife.get().dimensionId());
    }

    @Test
    void shouldPreferLocalDimensionPodOverCrossDimensionPod() {
        CloneNetworkSavedData networkData = new CloneNetworkSavedData();
        UUID playerId = UUID.randomUUID();
        BlockPos overworldPod = new BlockPos(50, 70, 50);
        BlockPos netherPod = new BlockPos(20, 40, 20);

        networkData.registerPod(playerId, overworldPod, "minecraft:overworld", "Overworld Safehouse", true, 500L);
        networkData.registerPod(playerId, netherPod, "minecraft:the_nether", "Nether Bunker", true, 600L);

        BlockPos deathInNether = new BlockPos(25, 40, 25);
        Optional<CloneNetworkSavedData.ClonePodRecord> chosenPod = networkData.findNearestReadyPod(playerId, deathInNether, "minecraft:the_nether");

        assertTrue(chosenPod.isPresent());
        assertEquals(netherPod, chosenPod.get().pos());
        assertEquals("minecraft:the_nether", chosenPod.get().dimensionId());
    }

    @Test
    void shouldVerifyQuantumSleeperPodBlockEntityConsumesCloneState() {
        BlockPos pos = new BlockPos(0, 64, 0);
        QuantumSleeperPodBlockEntity podBe = new QuantumSleeperPodBlockEntity(BlockEntityTypes.BARREL, pos, Blocks.BARREL.defaultBlockState());

        podBe.setHasClone(true);
        assertTrue(podBe.hasClone());

        podBe.setHasClone(false);
        assertFalse(podBe.hasClone());
    }

    @Test
    void shouldRescuePlayerUsingUnassignedPodWithoutPriorBinding() {
        CloneNetworkSavedData networkData = new CloneNetworkSavedData();
        UUID unassigned = CloneNetworkSavedData.UNASSIGNED_OWNER;
        BlockPos podPos = new BlockPos(88, 160, 44);
        networkData.registerPod(unassigned, podPos, "minecraft:overworld", "Showcase Pod", true, 1000L);

        UUID dyingPlayer = UUID.randomUUID();
        Optional<CloneNetworkSavedData.ClonePodRecord> found = networkData.findNearestReadyPod(dyingPlayer, new BlockPos(0, 160, 0), "minecraft:overworld");
        assertTrue(found.isPresent());
        assertEquals(podPos, found.get().pos());

        Optional<CloneNetworkSavedData.ClonePodRecord> any = networkData.findAnyReadyPod(dyingPlayer);
        assertTrue(any.isPresent());
        assertEquals(podPos, any.get().pos());
    }

    @Test
    void shouldRescuePlayerUsingOtherPlayersReadyPodAsFallback() {
        CloneNetworkSavedData networkData = new CloneNetworkSavedData();
        UUID playerA = UUID.randomUUID();
        BlockPos podPos = new BlockPos(150, 64, -200);
        networkData.registerPod(playerA, podPos, "minecraft:overworld", "Player A Chamber", true, 2000L);

        UUID playerB = UUID.randomUUID();
        Optional<CloneNetworkSavedData.ClonePodRecord> fallbackNearest = networkData.findNearestReadyPod(playerB, new BlockPos(140, 64, -190), "minecraft:overworld");
        assertTrue(fallbackNearest.isPresent());
        assertEquals(podPos, fallbackNearest.get().pos());

        Optional<CloneNetworkSavedData.ClonePodRecord> fallbackAny = networkData.findAnyReadyPod(playerB);
        assertTrue(fallbackAny.isPresent());
        assertEquals(podPos, fallbackAny.get().pos());
    }

    @Test
    void shouldVerifyHardcoreMapPreservationArchitectureInSource() throws IOException {
        Path handlerPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "survival", "FusedSpaceSuitHandler.java");
        String content = Files.readString(handlerPath);

        assertTrue(content.contains("ServerLivingEntityEvents.ALLOW_DEATH"));
        assertTrue(content.contains("tryActivateQuantumClone"));
        assertTrue(content.contains("newPlayer.setGameMode(GameType.SURVIVAL)"));
        assertTrue(content.contains("return false;"));
        int methodIndex = content.indexOf("boolean tryActivateQuantumClone");
        int methodEndIndex = content.indexOf("public static void dropNonSuitInventory", methodIndex);
        String methodBody = content.substring(methodIndex, methodEndIndex);
        assertFalse(methodBody.contains("isSandStormWorld"));
    }
}
