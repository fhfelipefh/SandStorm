package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.ThermalGeneratorBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThermalGeneratorBlockEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldInitializeWithDefaultValues() {
        ThermalGeneratorBlockEntity be = new ThermalGeneratorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(2, be.getContainerSize());
        assertTrue(be.isEmpty());
        assertEquals(0, be.getLavaAmount());
        assertEquals(0, be.getBurnTime());
        assertEquals(ThermalGeneratorBlockEntity.TICKS_PER_BUCKET, be.getMaxBurnTime());
        assertEquals(0L, be.getEnergyStorage().getStoredEnergy());
        assertEquals(ThermalGeneratorBlockEntity.CAPACITY, be.getEnergyStorage().getCapacity());
        assertNotNull(be.getDisplayName());
    }

    @Test
    void shouldValidateHopperAndSlotAutomation() {
        ThermalGeneratorBlockEntity be = new ThermalGeneratorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        int[] upSlots = be.getSlotsForFace(Direction.UP);
        assertEquals(1, upSlots.length);
        assertEquals(0, upSlots[0]);

        int[] downSlots = be.getSlotsForFace(Direction.DOWN);
        assertEquals(1, downSlots.length);
        assertEquals(1, downSlots[0]);

        int[] sideSlots = be.getSlotsForFace(Direction.NORTH);
        assertEquals(1, sideSlots.length);
        assertEquals(1, sideSlots[0]);

        assertTrue(be.canPlaceItemThroughFace(0, new ItemStack(Items.LAVA_BUCKET), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(0, new ItemStack(Items.WATER_BUCKET), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(1, new ItemStack(Items.BUCKET), Direction.UP));

        assertTrue(be.canTakeItemThroughFace(1, new ItemStack(Items.BUCKET), Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(0, new ItemStack(Items.LAVA_BUCKET), Direction.DOWN));
    }

    @Test
    void shouldManageLavaBufferAndClamping() {
        ThermalGeneratorBlockEntity be = new ThermalGeneratorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        be.setLavaAmount(1500);
        assertEquals(1500, be.getLavaAmount());

        be.setLavaAmount(8000);
        assertEquals(ThermalGeneratorBlockEntity.MAX_LAVA_MB, be.getLavaAmount());

        be.setLavaAmount(-500);
        assertEquals(0, be.getLavaAmount());
    }

    @Test
    void shouldInsertLavaViaFluidStorageWithTransactions() {
        ThermalGeneratorBlockEntity be = new ThermalGeneratorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        Storage<FluidVariant> fluidStorage = be.getFluidStorage(Direction.UP);
        assertNotNull(fluidStorage);
        assertTrue(fluidStorage.supportsInsertion());
        assertFalse(fluidStorage.supportsExtraction());

        FluidVariant water = new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY);
        FluidVariant lava = new FluidVariantImpl(Fluids.LAVA, DataComponentPatch.EMPTY);

        try (Transaction tx = Transaction.openOuter()) {
            long inserted = fluidStorage.insert(water, FluidConstants.BUCKET, tx);
            assertEquals(0L, inserted);
            tx.commit();
        }
        assertEquals(0, be.getLavaAmount());

        try (Transaction tx = Transaction.openOuter()) {
            long inserted = fluidStorage.insert(lava, FluidConstants.BUCKET, tx);
            assertEquals(FluidConstants.BUCKET, inserted);
        }
        assertEquals(0, be.getLavaAmount());

        try (Transaction tx = Transaction.openOuter()) {
            long inserted = fluidStorage.insert(lava, FluidConstants.BUCKET, tx);
            assertEquals(FluidConstants.BUCKET, inserted);
            tx.commit();
        }
        assertEquals(1000, be.getLavaAmount());

        try (Transaction tx = Transaction.openOuter()) {
            long inserted = fluidStorage.insert(lava, FluidConstants.BUCKET * 5, tx);
            assertEquals(FluidConstants.BUCKET * 3, inserted);
            tx.commit();
        }
        assertEquals(4000, be.getLavaAmount());
    }

    @Test
    void shouldHandleEnergyBufferingAndExtraction() {
        ThermalGeneratorBlockEntity be = new ThermalGeneratorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        be.setBurnTime(200);
        assertEquals(200, be.getBurnTime());

        long received = be.getEnergyStorage().receiveEnergy(ThermalGeneratorBlockEntity.GENERATION_RATE);
        assertEquals(60L, received);
        assertEquals(60L, be.getEnergyStorage().getStoredEnergy());

        long extracted = be.getEnergyStorage().extractEnergy(40L);
        assertEquals(40L, extracted);
        assertEquals(20L, be.getEnergyStorage().getStoredEnergy());
    }

    @Test
    void shouldManageItemSlotStorage() {
        ThermalGeneratorBlockEntity be = new ThermalGeneratorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        ItemStack lavaBucket = new ItemStack(Items.LAVA_BUCKET);
        be.setItem(0, lavaBucket);
        assertFalse(be.isEmpty());
        assertEquals(lavaBucket, be.getItem(0));

        ItemStack removed = be.removeItem(0, 1);
        assertEquals(Items.LAVA_BUCKET, removed.getItem());
        assertTrue(be.isEmpty());

        be.setItem(1, new ItemStack(Items.BUCKET));
        be.clearContent();
        assertTrue(be.isEmpty());
    }
}
