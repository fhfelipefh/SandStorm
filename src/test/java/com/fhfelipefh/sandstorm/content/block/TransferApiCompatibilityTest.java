package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.NaniteFabricatorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.Printer3DBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferApiCompatibilityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldCreateContainerStorageForCoreMachines() {
        Printer3DBlockEntity printer = new Printer3DBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        Storage<ItemVariant> printerStorage = ContainerStorage.of(printer, Direction.UP);
        assertNotNull(printerStorage);
        assertTrue(printerStorage.supportsInsertion());

        NaniteFabricatorBlockEntity fabricator = new NaniteFabricatorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        Storage<ItemVariant> fabricatorStorage = ContainerStorage.of(fabricator, Direction.UP);
        assertNotNull(fabricatorStorage);
        assertTrue(fabricatorStorage.supportsInsertion());

        DesalinationFilterBlockEntity filter = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        Storage<ItemVariant> filterItemStorage = ContainerStorage.of(filter, Direction.UP);
        assertNotNull(filterItemStorage);
        assertTrue(filterItemStorage.supportsInsertion());
    }

    @Test
    void shouldExposeFluidStorageForDesalinationFilter() {
        DesalinationFilterBlockEntity filter = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        Storage<FluidVariant> upStorage = filter.getFluidStorage(Direction.UP);
        assertNotNull(upStorage);
        assertTrue(upStorage.supportsInsertion());
        assertFalse(upStorage.supportsExtraction());

        Storage<FluidVariant> downStorage = filter.getFluidStorage(Direction.DOWN);
        assertNotNull(downStorage);
        assertFalse(downStorage.supportsInsertion());
        assertTrue(downStorage.supportsExtraction());

        Storage<FluidVariant> sideStorage = filter.getFluidStorage(Direction.NORTH);
        assertNotNull(sideStorage);
        assertTrue(sideStorage.supportsInsertion());
        assertTrue(sideStorage.supportsExtraction());
    }

    @Test
    void shouldInsertAndExtractWaterViaFluidStorageWithTransactions() {
        DesalinationFilterBlockEntity filter = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        Storage<FluidVariant> upStorage = filter.getFluidStorage(Direction.UP);
        assertNotNull(upStorage);

        FluidVariant waterVariant = new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY);
        FluidVariant lavaVariant = new FluidVariantImpl(Fluids.LAVA, DataComponentPatch.EMPTY);

        try (Transaction tx = Transaction.openOuter()) {
            long inserted = upStorage.insert(waterVariant, FluidConstants.BUCKET, tx);
            assertEquals(FluidConstants.BUCKET, inserted);
            tx.commit();
        }
        assertEquals(1000, filter.getWaterInput());

        try (Transaction tx = Transaction.openOuter()) {
            long inserted = upStorage.insert(waterVariant, FluidConstants.BUCKET, tx);
            assertEquals(FluidConstants.BUCKET, inserted);
        }
        assertEquals(1000, filter.getWaterInput());

        long lavaInserted;
        try (Transaction tx = Transaction.openOuter()) {
            lavaInserted = upStorage.insert(lavaVariant, FluidConstants.BUCKET, tx);
            tx.commit();
        }
        assertEquals(0, lavaInserted);

        Storage<FluidVariant> downStorage = filter.getFluidStorage(Direction.DOWN);
        assertNotNull(downStorage);
        assertEquals(0, filter.getWaterOutput());

        long extractedEmpty;
        try (Transaction tx = Transaction.openOuter()) {
            extractedEmpty = downStorage.extract(waterVariant, FluidConstants.BUCKET, tx);
            tx.commit();
        }
        assertEquals(0, extractedEmpty);

        filter.addWaterInput(3000);
        assertEquals(4000, filter.getWaterInput());
    }

    @Test
    void shouldSupportOutputExtractionAndRollback() {
        DesalinationFilterBlockEntity filter = new DesalinationFilterBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        filter.setWaterOutput(2000);

        FluidVariant waterVariant = new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY);

        Storage<FluidVariant> downStorage = filter.getFluidStorage(Direction.DOWN);
        assertNotNull(downStorage);

        try (Transaction tx = Transaction.openOuter()) {
            long extracted = downStorage.extract(waterVariant, FluidConstants.BUCKET, tx);
            assertEquals(FluidConstants.BUCKET, extracted);
        }
        assertEquals(2000, filter.getWaterOutput());

        try (Transaction tx = Transaction.openOuter()) {
            long extracted = downStorage.extract(waterVariant, FluidConstants.BUCKET, tx);
            assertEquals(FluidConstants.BUCKET, extracted);
            tx.commit();
        }
        assertEquals(1000, filter.getWaterOutput());
    }
}
