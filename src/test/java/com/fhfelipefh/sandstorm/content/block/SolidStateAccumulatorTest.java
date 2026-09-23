package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.gui.SolidStateAccumulatorMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolidStateAccumulatorTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void setUp() {
        SolidStateAccumulatorManager.clear();
    }

    @Test
    void testManagerRegistrationAndAggregation() {
        BlockPos pos1 = new BlockPos(10, 64, 10);
        BlockPos pos2 = new BlockPos(20, 64, 20);

        SolidStateAccumulatorManager.registerAccumulator(Level.OVERWORLD, pos1, 200000, 500000, false, 0);
        SolidStateAccumulatorManager.registerAccumulator(Level.OVERWORLD, pos2, 350000, 500000, true, 2);

        assertEquals(2, SolidStateAccumulatorManager.getAccumulators(Level.OVERWORLD).size());
        assertEquals(550000L, SolidStateAccumulatorManager.getTotalStoredEnergy(Level.OVERWORLD));
        assertEquals(1000000L, SolidStateAccumulatorManager.getTotalCapacity(Level.OVERWORLD));
        assertEquals(1, SolidStateAccumulatorManager.getDischargingAccumulatorCount(Level.OVERWORLD));

        SolidStateAccumulatorManager.unregisterAccumulator(Level.OVERWORLD, pos1);
        assertEquals(1, SolidStateAccumulatorManager.getAccumulators(Level.OVERWORLD).size());
        assertEquals(350000L, SolidStateAccumulatorManager.getTotalStoredEnergy(Level.OVERWORLD));
    }

    @Test
    void testDischargeRadius48Blocks() {
        BlockPos pos = new BlockPos(0, 64, 0);
        SolidStateAccumulatorManager.registerAccumulator(Level.OVERWORLD, pos, 100000, 500000, true, 2);

        BlockPos inRange = new BlockPos(40, 64, 0);
        assertEquals(SolidStateAccumulatorManager.DISCHARGE_TRANSFER_RATE, SolidStateAccumulatorManager.getWptChargeAt(Level.OVERWORLD, inRange));

        BlockPos outOfRange = new BlockPos(50, 64, 0);
        assertEquals(0L, SolidStateAccumulatorManager.getWptChargeAt(Level.OVERWORLD, outOfRange));
    }

    @Test
    void testBlockStateProperties() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("solid_state_accumulator"));
        assertNotNull(blockKey);
        assertEquals("solid_state_accumulator", blockKey.identifier().getPath());

        ResourceKey<BlockEntityType<?>> beKey = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, SandStormMod.id("solid_state_accumulator"));
        assertNotNull(beKey);
        assertEquals("solid_state_accumulator", beKey.identifier().getPath());

        assertNotNull(SolidStateAccumulatorBlock.CHARGE_LEVEL);
        assertNotNull(SolidStateAccumulatorBlock.MODE);
        assertNotNull(SolidStateAccumulatorBlock.FACING);
    }

    @Test
    void testMenuDataSynchronization() {
        SimpleContainerData data = new SimpleContainerData(7);
        int stored = 350000;
        data.set(0, stored & 0xFFFF);
        data.set(1, (stored >> 16) & 0xFFFF);

        int max = 500000;
        data.set(2, max & 0xFFFF);
        data.set(3, (max >> 16) & 0xFFFF);

        data.set(4, 1);
        data.set(5, 1);
        data.set(6, 1);

        Inventory dummyInventory = new Inventory(null, null);
        SolidStateAccumulatorMenu menu = new SolidStateAccumulatorMenu(null, 1, dummyInventory, new SimpleContainer(2), data);

        assertEquals(stored, menu.getStoredEnergy());
        assertEquals(max, menu.getMaxEnergy());
        assertEquals(1, menu.getMode());
        assertTrue(menu.isDischarging());
        assertTrue(menu.isCharging());
        assertEquals(0.7f, menu.getChargePercentage(), 0.001f);
        assertTrue(menu.stillValid(null));
    }
}
