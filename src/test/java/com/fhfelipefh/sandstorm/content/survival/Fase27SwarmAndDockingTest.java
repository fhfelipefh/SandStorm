package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSwarmManager;
import com.fhfelipefh.sandstorm.content.gui.CyborgDockingStationMenu;
import com.fhfelipefh.sandstorm.content.gui.CyborgTelemetryMenu;
import com.fhfelipefh.sandstorm.content.item.CyborgUpgradeItem;
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
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase27SwarmAndDockingTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldRegisterCyborgDockingStationBlockAndEntityKeys() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("cyborg_docking_station"));
        ResourceKey<Item> itemKey = SandStormMod.itemKey("cyborg_docking_station");
        assertNotNull(blockKey);
        assertNotNull(itemKey);
        assertEquals("sandstorm", blockKey.identifier().getNamespace());
        assertEquals("cyborg_docking_station", blockKey.identifier().getPath());
        assertEquals("sandstorm", itemKey.identifier().getNamespace());
        assertEquals("cyborg_docking_station", itemKey.identifier().getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "acid_chitin_plating",
            "cryo_trehalose_cell",
            "long_range_lidar_lens",
            "piezo_hover_thruster"
    })
    void shouldRegisterCyborgUpgradeItemKeys(String itemPath) {
        ResourceKey<Item> key = SandStormMod.itemKey(itemPath);
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(itemPath, key.identifier().getPath());
    }

    @Test
    void shouldValidateCyborgUpgradeTypesAndBitmasks() {
        assertEquals("acid_chitin_plating", CyborgUpgradeItem.CyborgUpgradeType.ACID_CHITIN_PLATING.getId());
        assertEquals(Rarity.RARE, CyborgUpgradeItem.CyborgUpgradeType.ACID_CHITIN_PLATING.getRarity());
        assertEquals(1, CyborgUpgradeItem.CyborgUpgradeType.ACID_CHITIN_PLATING.getMaskBit());
        assertEquals(1, CyborgUpgradeItem.CyborgUpgradeType.ACID_CHITIN_PLATING.getBitmask());

        assertEquals("cryo_trehalose_cell", CyborgUpgradeItem.CyborgUpgradeType.CRYO_TREHALOSE_CELL.getId());
        assertEquals(Rarity.RARE, CyborgUpgradeItem.CyborgUpgradeType.CRYO_TREHALOSE_CELL.getRarity());
        assertEquals(2, CyborgUpgradeItem.CyborgUpgradeType.CRYO_TREHALOSE_CELL.getMaskBit());
        assertEquals(2, CyborgUpgradeItem.CyborgUpgradeType.CRYO_TREHALOSE_CELL.getBitmask());

        assertEquals("long_range_lidar_lens", CyborgUpgradeItem.CyborgUpgradeType.LONG_RANGE_LIDAR_LENS.getId());
        assertEquals(Rarity.UNCOMMON, CyborgUpgradeItem.CyborgUpgradeType.LONG_RANGE_LIDAR_LENS.getRarity());
        assertEquals(4, CyborgUpgradeItem.CyborgUpgradeType.LONG_RANGE_LIDAR_LENS.getMaskBit());
        assertEquals(4, CyborgUpgradeItem.CyborgUpgradeType.LONG_RANGE_LIDAR_LENS.getBitmask());

        assertEquals("piezo_hover_thruster", CyborgUpgradeItem.CyborgUpgradeType.PIEZO_HOVER_THRUSTER.getId());
        assertEquals(Rarity.EPIC, CyborgUpgradeItem.CyborgUpgradeType.PIEZO_HOVER_THRUSTER.getRarity());
        assertEquals(8, CyborgUpgradeItem.CyborgUpgradeType.PIEZO_HOVER_THRUSTER.getMaskBit());
        assertEquals(8, CyborgUpgradeItem.CyborgUpgradeType.PIEZO_HOVER_THRUSTER.getBitmask());
    }

    @Test
    void shouldTestCyborgSwarmManagerVoxelMutex() {
        CyborgSwarmManager manager = CyborgSwarmManager.getInstance();
        UUID cyborg1 = UUID.randomUUID();
        UUID cyborg2 = UUID.randomUUID();
        BlockPos target = new BlockPos(100, 64, 200);

        manager.releaseAll(cyborg1);
        manager.releaseAll(cyborg2);

        assertTrue(manager.tryReserveBlock(cyborg1, target));
        assertTrue(manager.isBlockReserved(target));
        assertFalse(manager.tryReserveBlock(cyborg2, target));

        BlockPos secondaryTarget = new BlockPos(101, 64, 200);
        assertTrue(manager.tryReserveBlock(cyborg2, secondaryTarget));

        manager.releaseBlock(cyborg1, target);
        assertFalse(manager.isBlockReserved(target));
        assertTrue(manager.tryReserveBlock(cyborg2, target));

        manager.releaseAll(cyborg2);
        assertFalse(manager.isBlockReserved(target));
        assertFalse(manager.isBlockReserved(secondaryTarget));
    }

    @Test
    void shouldTestCyborgSwarmManagerDockRegistry() {
        CyborgSwarmManager manager = CyborgSwarmManager.getInstance();
        BlockPos dockPos = new BlockPos(300, 64, 300);

        manager.registerDock(Level.OVERWORLD, dockPos);
        BlockPos nearest = manager.findNearestAvailableDock(Level.OVERWORLD, new BlockPos(305, 64, 305), 128.0);
        assertNotNull(nearest);
        assertEquals(dockPos, nearest);

        manager.unregisterDock(Level.OVERWORLD, dockPos);
        BlockPos afterUnregister = manager.findNearestAvailableDock(Level.OVERWORLD, new BlockPos(305, 64, 305), 128.0);
        assertNull(afterUnregister);
    }

    @Test
    void shouldTestCyborgTelemetryMenuUpgradeChannels() {
        SimpleContainer container = new SimpleContainer(CyborgTelemetryMenu.CYBORG_SLOTS);
        SimpleContainerData data = new SimpleContainerData(9);
        data.set(8, 1 | 4);

        CyborgTelemetryMenu menu = new CyborgTelemetryMenu(null, 1, new Inventory(null, null), container, data);

        assertEquals(5, menu.getUpgradesMask());
        assertTrue(menu.hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.ACID_CHITIN_PLATING));
        assertFalse(menu.hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.CRYO_TREHALOSE_CELL));
        assertTrue(menu.hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.LONG_RANGE_LIDAR_LENS));
        assertFalse(menu.hasUpgrade(CyborgUpgradeItem.CyborgUpgradeType.PIEZO_HOVER_THRUSTER));
    }

    @Test
    void shouldTestCyborgDockingStationMenuDataChannels() {
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 1);
        data.set(1, 40000);
        data.set(2, 50000);
        data.set(3, 12000);
        data.set(4, 50000);
        data.set(5, 80);

        CyborgDockingStationMenu menu = new CyborgDockingStationMenu(null, 1, new Inventory(null, null), null, data);

        assertTrue(menu.isDocked());
        assertEquals(40000, menu.getDockEnergy());
        assertEquals(50000, menu.getMaxDockEnergy());
        assertEquals(12000, menu.getCyborgEnergy());
        assertEquals(50000, menu.getCyborgMaxEnergy());
        assertEquals(80, menu.getCyborgIntegrity());

        assertEquals(80, menu.getDockEnergyScaled(100));
        assertEquals(24, menu.getCyborgEnergyScaled(100));
        assertEquals(80, menu.getCyborgIntegrityScaled(100));
        assertEquals(36, menu.slots.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cyborg_docking_station.json",
            "acid_chitin_plating.json",
            "cryo_trehalose_cell.json",
            "long_range_lidar_lens.json",
            "piezo_hover_thruster.json"
    })
    void shouldVerifyPhase27ItemDefinitionsExist(String filename) {
        File file = new File("src/main/resources/assets/sandstorm/items/" + filename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cyborg_docking_station.json",
            "acid_chitin_plating.json",
            "cryo_trehalose_cell.json",
            "long_range_lidar_lens.json",
            "piezo_hover_thruster.json"
    })
    void shouldVerifyPhase27ItemModelsExist(String filename) {
        File file = new File("src/main/resources/assets/sandstorm/models/item/" + filename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void shouldVerifyPhase27BlockModelsAndBlockstatesExist() {
        File blockstate = new File("src/main/resources/assets/sandstorm/blockstates/cyborg_docking_station.json");
        assertTrue(blockstate.exists());
        assertTrue(blockstate.length() > 0);

        File modelNormal = new File("src/main/resources/assets/sandstorm/models/block/cyborg_docking_station.json");
        assertTrue(modelNormal.exists());
        assertTrue(modelNormal.length() > 0);

        File modelActive = new File("src/main/resources/assets/sandstorm/models/block/cyborg_docking_station_active.json");
        assertTrue(modelActive.exists());
        assertTrue(modelActive.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cyborg_docking_station.json",
            "acid_chitin_plating.json",
            "cryo_trehalose_cell.json",
            "long_range_lidar_lens.json",
            "piezo_hover_thruster.json"
    })
    void shouldVerifyPhase27RecipesExist(String filename) {
        File file = new File("src/main/resources/data/sandstorm/recipe/" + filename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void shouldVerifyPhase27LootTableExists() {
        File lootTable = new File("src/main/resources/data/sandstorm/loot_table/blocks/cyborg_docking_station.json");
        assertTrue(lootTable.exists());
        assertTrue(lootTable.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/main/resources/assets/sandstorm/textures/block/cyborg_docking_station_top.png",
            "src/main/resources/assets/sandstorm/textures/block/cyborg_docking_station_top_active.png",
            "src/main/resources/assets/sandstorm/textures/block/cyborg_docking_station_side.png",
            "src/main/resources/assets/sandstorm/textures/block/cyborg_docking_station_bottom.png",
            "src/main/resources/assets/sandstorm/textures/item/acid_chitin_plating.png",
            "src/main/resources/assets/sandstorm/textures/item/cryo_trehalose_cell.png",
            "src/main/resources/assets/sandstorm/textures/item/long_range_lidar_lens.png",
            "src/main/resources/assets/sandstorm/textures/item/piezo_hover_thruster.png"
    })
    void shouldVerifyPhase27TexturesHaveValidPngHeader(String texturePath) throws IOException {
        File file = new File(texturePath);
        assertTrue(file.exists());
        byte[] header = new byte[8];
        try (FileInputStream fis = new FileInputStream(file)) {
            assertEquals(8, fis.read(header));
        }
        byte[] expected = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        assertArrayEquals(expected, header);
    }
}
