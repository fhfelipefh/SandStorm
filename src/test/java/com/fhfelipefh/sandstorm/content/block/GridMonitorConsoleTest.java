package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.gui.GridMonitorConsoleMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GridMonitorConsoleTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testBlockAndEntityRegistration() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("grid_monitor_console"));
        assertNotNull(blockKey);
        assertEquals("grid_monitor_console", blockKey.identifier().getPath());

        ResourceKey<BlockEntityType<?>> beKey = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, SandStormMod.id("grid_monitor_console"));
        assertNotNull(beKey);
        assertEquals("grid_monitor_console", beKey.identifier().getPath());

        assertNotNull(GridMonitorConsoleBlock.FACING);
    }

    @Test
    void testMenuTelemetryDataSynchronization() {
        SimpleContainerData data = new SimpleContainerData(12);
        data.set(0, 4);
        data.set(1, 160);
        data.set(2, 2);
        data.set(3, 120);
        data.set(4, 3);
        data.set(5, 2);

        int stored = 450000;
        data.set(6, stored & 0xFFFF);
        data.set(7, (stored >> 16) & 0xFFFF);

        int cap = 1000000;
        data.set(8, cap & 0xFFFF);
        data.set(9, (cap >> 16) & 0xFFFF);

        data.set(10, 1);
        data.set(11, 100);

        Inventory dummyInventory = new Inventory(null, null);
        GridMonitorConsoleMenu menu = new GridMonitorConsoleMenu(null, 1, dummyInventory, data);

        assertEquals(4, menu.getSolarCount());
        assertEquals(160, menu.getSolarGenRate());
        assertEquals(2, menu.getThermalCount());
        assertEquals(120, menu.getThermalGenRate());
        assertEquals(3, menu.getRelayCount());
        assertEquals(2, menu.getAccumulatorCount());
        assertEquals(stored, menu.getTotalStoredEnergy());
        assertEquals(cap, menu.getTotalCapacity());
        assertEquals(1, menu.getGridStatus());
        assertEquals(100, menu.getLocalCoverageCharge());
        assertTrue(menu.stillValid(null));
    }
}
