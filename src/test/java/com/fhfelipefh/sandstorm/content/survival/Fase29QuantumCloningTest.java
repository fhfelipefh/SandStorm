package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.clone.CloneNetworkSavedData;
import com.fhfelipefh.sandstorm.content.gui.QuantumSleeperMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase29QuantumCloningTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldRegisterQuantumSleeperPodBlockAndItemKeys() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("quantum_sleeper_pod"));
        ResourceKey<Item> itemKey = SandStormMod.itemKey("quantum_sleeper_pod");
        ResourceKey<Item> matrixKey = SandStormMod.itemKey("quantum_mind_matrix");

        assertNotNull(blockKey);
        assertNotNull(itemKey);
        assertNotNull(matrixKey);
        assertEquals("sandstorm", blockKey.identifier().getNamespace());
        assertEquals("quantum_sleeper_pod", blockKey.identifier().getPath());
        assertEquals("sandstorm", itemKey.identifier().getNamespace());
        assertEquals("quantum_sleeper_pod", itemKey.identifier().getPath());
        assertEquals("sandstorm", matrixKey.identifier().getNamespace());
        assertEquals("quantum_mind_matrix", matrixKey.identifier().getPath());
    }

    @Test
    void shouldTestCloneNetworkSavedDataProximityAndTransfer() {
        CloneNetworkSavedData networkData = new CloneNetworkSavedData();
        UUID playerId = UUID.randomUUID();
        BlockPos podAlpha = new BlockPos(100, 64, 100);
        BlockPos podBeta = new BlockPos(800, 64, 800);
        String dimension = "minecraft:overworld";

        networkData.registerPod(playerId, podAlpha, dimension, "Pod Alpha", true, 1000L);
        networkData.registerPod(playerId, podBeta, dimension, "Pod Beta", true, 1050L);

        Optional<CloneNetworkSavedData.ClonePodRecord> nearestToAlpha = networkData.findNearestReadyPod(playerId, new BlockPos(120, 64, 110), dimension);
        assertTrue(nearestToAlpha.isPresent());
        assertEquals(podAlpha, nearestToAlpha.get().pos());

        Optional<CloneNetworkSavedData.ClonePodRecord> nearestToBeta = networkData.findNearestReadyPod(playerId, new BlockPos(750, 64, 780), dimension);
        assertTrue(nearestToBeta.isPresent());
        assertEquals(podBeta, nearestToBeta.get().pos());

        Optional<CloneNetworkSavedData.ClonePodRecord> transferFromAlpha = networkData.findTargetPodForTransfer(playerId, podAlpha, dimension);
        assertTrue(transferFromAlpha.isPresent());
        assertEquals(podBeta, transferFromAlpha.get().pos());

        networkData.unregisterPod(podBeta, dimension);
        Optional<CloneNetworkSavedData.ClonePodRecord> transferAfterRemoval = networkData.findTargetPodForTransfer(playerId, podAlpha, dimension);
        assertFalse(transferAfterRemoval.isPresent());
    }

    @Test
    void shouldTestQuantumSleeperMenuDataChannels() {
        SimpleContainerData data = new SimpleContainerData(5);
        data.set(0, 50000);
        data.set(1, 100000);
        data.set(2, 50);
        data.set(3, 1);
        data.set(4, 2);

        SimpleContainer container = new SimpleContainer(42);
        Inventory dummyInv = new Inventory(null, null);
        QuantumSleeperMenu menu = new QuantumSleeperMenu(null, 1, dummyInv, null, container, data);

        assertEquals(50000, menu.getStoredEnergy());
        assertEquals(100000, menu.getMaxEnergy());
        assertEquals(50, menu.getBioNutrients());
        assertTrue(menu.hasClone());
        assertEquals(2, menu.getConnectedPodsCount());
        assertEquals(80, menu.getEnergyScaled(160));
        assertEquals(80, menu.getNutrientsScaled(160));
    }

    @Test
    void shouldVerifyPhase29BlockstatesAndModelsExist() {
        File blockstate = new File("src/main/resources/assets/sandstorm/blockstates/quantum_sleeper_pod.json");
        File blockModel = new File("src/main/resources/assets/sandstorm/models/block/quantum_sleeper_pod.json");
        File blockModelActive = new File("src/main/resources/assets/sandstorm/models/block/quantum_sleeper_pod_active.json");
        File blockModelOccupied = new File("src/main/resources/assets/sandstorm/models/block/quantum_sleeper_pod_occupied.json");
        File itemModel = new File("src/main/resources/assets/sandstorm/models/item/quantum_sleeper_pod.json");
        File itemDef = new File("src/main/resources/assets/sandstorm/items/quantum_sleeper_pod.json");
        File matrixModel = new File("src/main/resources/assets/sandstorm/models/item/quantum_mind_matrix.json");
        File matrixDef = new File("src/main/resources/assets/sandstorm/items/quantum_mind_matrix.json");

        assertTrue(blockstate.exists());
        assertTrue(blockModel.exists());
        assertTrue(blockModelActive.exists());
        assertTrue(blockModelOccupied.exists());
        assertTrue(itemModel.exists());
        assertTrue(itemDef.exists());
        assertTrue(matrixModel.exists());
        assertTrue(matrixDef.exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "textures/block/quantum_sleeper_pod_top.png",
            "textures/block/quantum_sleeper_pod_bottom.png",
            "textures/block/quantum_sleeper_pod_side.png",
            "textures/block/quantum_sleeper_pod_front.png",
            "textures/block/quantum_sleeper_pod_front_active.png",
            "textures/block/quantum_sleeper_pod_front_occupied.png",
            "textures/item/quantum_sleeper_pod.png",
            "textures/item/quantum_mind_matrix.png"
    })
    void shouldVerifyPhase29TexturesAreValidPNGs(String textureRelPath) throws IOException {
        File file = new File("src/main/resources/assets/sandstorm/" + textureRelPath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = fis.read(header);
            assertEquals(8, read);
            byte[] expectedPngHeader = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            assertArrayEquals(expectedPngHeader, header);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "quantum_sleeper_pod",
            "quantum_mind_matrix"
    })
    void shouldVerifyPhase29RecipesExist(String recipeName) {
        File recipeFile = new File("src/main/resources/data/sandstorm/recipe/" + recipeName + ".json");
        assertTrue(recipeFile.exists());
        assertTrue(recipeFile.length() > 0);
    }

    @Test
    void shouldVerifyPhase29LootTableExists() {
        File lootTable = new File("src/main/resources/data/sandstorm/loot_table/blocks/quantum_sleeper_pod.json");
        assertTrue(lootTable.exists());
        assertTrue(lootTable.length() > 0);
    }
}
