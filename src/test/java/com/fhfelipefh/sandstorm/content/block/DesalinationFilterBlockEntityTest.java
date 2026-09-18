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
        assertEquals(4, be.getContainerSize());
        assertEquals(0, be.getEnergy());
        assertEquals(0, be.getProgress());
        assertFalse(be.isProcessing());
        assertNotNull(be.getDisplayName());
        assertTrue(be.isEmpty());
    }

    @Test
    void shouldValidateHopperPlacementRules() {
        DesalinationFilterBlockEntity be = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        int[] topSlots = be.getSlotsForFace(Direction.UP);
        assertEquals(1, topSlots.length);
        assertEquals(0, topSlots[0]);

        int[] bottomSlots = be.getSlotsForFace(Direction.DOWN);
        assertEquals(2, bottomSlots.length);
        assertEquals(1, bottomSlots[0]);
        assertEquals(2, bottomSlots[1]);

        int[] sideSlots = be.getSlotsForFace(Direction.WEST);
        assertEquals(1, sideSlots.length);
        assertEquals(3, sideSlots[0]);

        assertFalse(be.canPlaceItemThroughFace(1, ItemStack.EMPTY, Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(2, ItemStack.EMPTY, Direction.UP));
        assertTrue(be.canTakeItemThroughFace(1, ItemStack.EMPTY, Direction.DOWN));
        assertTrue(be.canTakeItemThroughFace(2, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(0, ItemStack.EMPTY, Direction.DOWN));
    }
}
