package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.HydroponicChamberBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.HydroponicChamberMenu;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HydroponicChamberTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldInitializeWithDefaultValues() {
        HydroponicChamberBlockEntity chamber = new HydroponicChamberBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(5, chamber.getContainerSize());
        assertEquals(0, chamber.getEnergy());
        assertEquals(0, chamber.getProgress());
        assertEquals(120, chamber.getMaxProgress());
        assertFalse(chamber.isProcessing());
        assertFalse(chamber.isInsideDome());
        assertNotNull(chamber.getDisplayName());
        assertTrue(chamber.isEmpty());
    }

    @Test
    void shouldValidateHopperPlacementRules() {
        HydroponicChamberBlockEntity chamber = new HydroponicChamberBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        int[] topSlots = chamber.getSlotsForFace(Direction.UP);
        assertEquals(3, topSlots.length);
        assertEquals(0, topSlots[0]);
        assertEquals(1, topSlots[1]);
        assertEquals(2, topSlots[2]);

        int[] bottomSlots = chamber.getSlotsForFace(Direction.DOWN);
        assertEquals(2, bottomSlots.length);
        assertEquals(3, bottomSlots[0]);
        assertEquals(4, bottomSlots[1]);

        int[] sideSlots = chamber.getSlotsForFace(Direction.NORTH);
        assertEquals(5, sideSlots.length);

        assertFalse(chamber.canPlaceItemThroughFace(3, ItemStack.EMPTY, Direction.UP));
        assertFalse(chamber.canPlaceItemThroughFace(4, ItemStack.EMPTY, Direction.UP));
        assertTrue(chamber.canTakeItemThroughFace(3, ItemStack.EMPTY, Direction.DOWN));
        assertTrue(chamber.canTakeItemThroughFace(4, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(chamber.canTakeItemThroughFace(0, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(chamber.canTakeItemThroughFace(1, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(chamber.canTakeItemThroughFace(2, ItemStack.EMPTY, Direction.DOWN));
    }

    @Test
    void shouldValidateProcessingPreconditions() {
        HydroponicChamberBlockEntity chamber = new HydroponicChamberBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertFalse(chamber.isProcessing());
        assertFalse(chamber.isInsideDome());
    }

    @Test
    void shouldScaleProgressAndEnergyInMenu() {
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 5000);
        data.set(1, 10000);
        data.set(2, 60);
        data.set(3, 120);
        data.set(4, 1);
        data.set(5, 1);
        HydroponicChamberMenu menu = new HydroponicChamberMenu(null, 0, null, new SimpleContainer(5), data);

        assertEquals(5000, menu.getEnergy());
        assertEquals(10000, menu.getMaxEnergy());
        assertEquals(60, menu.getProgress());
        assertEquals(120, menu.getMaxProgress());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isProcessing());
        assertEquals(25, menu.getEnergyScaled(50));
        assertEquals(12, menu.getProgressScaled(24));
    }
}
