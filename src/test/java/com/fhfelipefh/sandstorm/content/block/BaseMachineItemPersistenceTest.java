package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.NaniteFabricatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.Printer3DBlockEntity;
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

class BaseMachineItemPersistenceTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldInitializePrinter3DWithCorrectSlotsAndState() {
        Printer3DBlockEntity be = new Printer3DBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(4, be.getContainerSize());
        assertTrue(be.isEmpty());
        assertEquals(0, be.getEnergy());
        assertEquals(0, be.getProgress());
        assertFalse(be.isProcessing());
        assertNotNull(be.getDisplayName());
    }

    @Test
    void shouldInitializeNaniteFabricatorWithCorrectSlotsAndState() {
        NaniteFabricatorBlockEntity be = new NaniteFabricatorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(4, be.getContainerSize());
        assertTrue(be.isEmpty());
        assertEquals(0, be.getEnergy());
        assertEquals(0, be.getProgress());
        assertFalse(be.isProcessing());
        assertNotNull(be.getDisplayName());
    }

    @Test
    void shouldInitializeDesalinationFilterWithCorrectSlotsAndState() {
        DesalinationFilterBlockEntity be = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(5, be.getContainerSize());
        assertTrue(be.isEmpty());
        assertEquals(0, be.getEnergy());
        assertEquals(0, be.getProgress());
        assertFalse(be.isProcessing());
        assertNotNull(be.getDisplayName());
    }

    @Test
    void shouldVerifyAllMachineSlotConfigurations() {
        Printer3DBlockEntity printer = new Printer3DBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        NaniteFabricatorBlockEntity fabricator = new NaniteFabricatorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        DesalinationFilterBlockEntity filter = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        assertEquals(3, printer.getSlotsForFace(Direction.UP).length);
        assertEquals(2, printer.getSlotsForFace(Direction.DOWN).length);
        assertEquals(4, printer.getSlotsForFace(Direction.NORTH).length);

        assertEquals(3, fabricator.getSlotsForFace(Direction.UP).length);
        assertEquals(2, fabricator.getSlotsForFace(Direction.DOWN).length);
        assertEquals(4, fabricator.getSlotsForFace(Direction.NORTH).length);

        assertEquals(3, filter.getSlotsForFace(Direction.UP).length);
        assertEquals(3, filter.getSlotsForFace(Direction.DOWN).length);
        assertEquals(5, filter.getSlotsForFace(Direction.NORTH).length);
    }

    @Test
    void shouldClearContentSafelyAcrossAllMachines() {
        Printer3DBlockEntity printer = new Printer3DBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        NaniteFabricatorBlockEntity fabricator = new NaniteFabricatorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        DesalinationFilterBlockEntity filter = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        printer.clearContent();
        fabricator.clearContent();
        filter.clearContent();

        assertTrue(printer.isEmpty());
        assertTrue(fabricator.isEmpty());
        assertTrue(filter.isEmpty());
        assertEquals(ItemStack.EMPTY, printer.getItem(0));
        assertEquals(ItemStack.EMPTY, fabricator.getItem(0));
        assertEquals(ItemStack.EMPTY, filter.getItem(0));
    }
}
