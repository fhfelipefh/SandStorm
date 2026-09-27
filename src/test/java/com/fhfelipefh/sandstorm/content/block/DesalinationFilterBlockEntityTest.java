package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DesalinationFilterBlockEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldInitializeWithDefaultValues() {
        DesalinationFilterBlockEntity be = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(5, be.getContainerSize());
        assertEquals(0, be.getEnergy());
        assertEquals(0, be.getProgress());
        assertFalse(be.isProcessing());
        assertNotNull(be.getDisplayName());
        assertTrue(be.isEmpty());
        assertEquals(0, be.getWaterInput());
        assertEquals(0, be.getWaterOutput());
        assertEquals(4000, be.getMaxWater());
        assertFalse(be.hasFilterCartridge());
    }

    @Test
    void shouldValidateHopperPlacementRules() {
        DesalinationFilterBlockEntity be = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        int[] topSlots = be.getSlotsForFace(Direction.UP);
        assertEquals(3, topSlots.length);
        assertEquals(0, topSlots[0]);
        assertEquals(3, topSlots[1]);
        assertEquals(4, topSlots[2]);

        int[] bottomSlots = be.getSlotsForFace(Direction.DOWN);
        assertEquals(3, bottomSlots.length);
        assertEquals(1, bottomSlots[0]);
        assertEquals(2, bottomSlots[1]);
        assertEquals(3, bottomSlots[2]);

        int[] sideSlots = be.getSlotsForFace(Direction.WEST);
        assertEquals(5, sideSlots.length);
        assertEquals(0, sideSlots[0]);
        assertEquals(3, sideSlots[1]);
        assertEquals(1, sideSlots[2]);
        assertEquals(2, sideSlots[3]);
        assertEquals(4, sideSlots[4]);

        assertFalse(be.canPlaceItemThroughFace(1, ItemStack.EMPTY, Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(2, ItemStack.EMPTY, Direction.UP));
        assertTrue(be.canTakeItemThroughFace(1, ItemStack.EMPTY, Direction.DOWN));
        assertTrue(be.canTakeItemThroughFace(2, ItemStack.EMPTY, Direction.DOWN));
        assertTrue(be.canTakeItemThroughFace(1, ItemStack.EMPTY, Direction.WEST));
        assertTrue(be.canTakeItemThroughFace(2, ItemStack.EMPTY, Direction.WEST));
        assertFalse(be.canTakeItemThroughFace(0, ItemStack.EMPTY, Direction.DOWN));
    }

    @Test
    void shouldManageFluidBuffers() {
        DesalinationFilterBlockEntity be = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertTrue(be.addWaterInput(1000));
        assertEquals(1000, be.getWaterInput());
        assertTrue(be.addWaterInput(3000));
        assertEquals(4000, be.getWaterInput());
        assertFalse(be.addWaterInput(500));
        assertEquals(4000, be.getWaterInput());
        assertEquals(0, be.drainWaterOutput(1000));
    }
}
