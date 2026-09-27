package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DewCondenserBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.DewCondenserMenu;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
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

class DewCondenserBlockEntityTest {

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

    private DewCondenserBlockEntity createTestEntity() {
        return new DewCondenserBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
    }

    @Test
    void shouldInitializeWithDefaultValues() {
        DewCondenserBlockEntity entity = createTestEntity();

        assertEquals(0, entity.getWaterAmount());
        assertEquals(3000, entity.getMaxWater());
        assertEquals(0, entity.getProgress());
        assertEquals(200, entity.getMaxProgress());
        assertEquals(2, entity.getContainerSize());
        assertTrue(entity.isEmpty());
    }

    @Test
    void shouldCalculateWaterLevelCorrectly() {
        DewCondenserBlockEntity entity = createTestEntity();

        entity.setWaterAmount(0);
        assertEquals(0, entity.calculateWaterLevel());

        entity.setWaterAmount(500);
        assertEquals(1, entity.calculateWaterLevel());

        entity.setWaterAmount(1500);
        assertEquals(2, entity.calculateWaterLevel());

        entity.setWaterAmount(2800);
        assertEquals(3, entity.calculateWaterLevel());
    }

    @Test
    void shouldReportFullStatusWhenWaterReachesCapacity() {
        DewCondenserBlockEntity entity = createTestEntity();

        entity.setWaterAmount(3000);
        assertEquals(3, entity.getStatus());
    }

    @Test
    void shouldExposeFluidStorageOnlyOnDownFace() {
        DewCondenserBlockEntity entity = createTestEntity();
        entity.setWaterAmount(1000);

        Storage<FluidVariant> downStorage = entity.getFluidStorage(Direction.DOWN);
        assertNotNull(downStorage);
        assertTrue(downStorage.supportsExtraction());
        assertFalse(downStorage.supportsInsertion());

        Storage<FluidVariant> upStorage = entity.getFluidStorage(Direction.UP);
        assertFalse(upStorage.supportsExtraction());
        assertFalse(upStorage.supportsInsertion());

        Storage<FluidVariant> northStorage = entity.getFluidStorage(Direction.NORTH);
        assertFalse(northStorage.supportsExtraction());
        assertFalse(northStorage.supportsInsertion());
    }

    @Test
    void shouldExtractFluidFromDownFaceViaTransferApi() {
        DewCondenserBlockEntity entity = createTestEntity();
        entity.setWaterAmount(1000);

        Storage<FluidVariant> downStorage = entity.getFluidStorage(Direction.DOWN);
        long movedDroplets;
        try (Transaction tx = Transaction.openOuter()) {
            movedDroplets = downStorage.extract(new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY), FluidConstants.BUCKET, tx);
            tx.commit();
        }

        assertEquals(FluidConstants.BUCKET, movedDroplets);
        assertEquals(0, entity.getWaterAmount());
    }

    @Test
    void shouldManageItemSlotRulesAndFaceDistribution() {
        DewCondenserBlockEntity entity = createTestEntity();

        assertTrue(entity.canPlaceItem(0, new ItemStack(Items.GLASS_BOTTLE)));
        assertTrue(entity.canPlaceItem(0, new ItemStack(Items.BUCKET)));
        assertFalse(entity.canPlaceItem(0, new ItemStack(Items.SAND)));
        assertFalse(entity.canPlaceItem(1, new ItemStack(Items.WATER_BUCKET)));

        assertTrue(entity.canTakeItem(entity, 1, new ItemStack(Items.WATER_BUCKET)));
        assertFalse(entity.canTakeItem(entity, 0, new ItemStack(Items.GLASS_BOTTLE)));

        int[] topSlots = entity.getSlotsForFace(Direction.UP);
        assertEquals(1, topSlots.length);
        assertEquals(0, topSlots[0]);

        int[] bottomSlots = entity.getSlotsForFace(Direction.DOWN);
        assertEquals(1, bottomSlots.length);
        assertEquals(1, bottomSlots[0]);
    }

    @Test
    void shouldSupportMenuDataAndPercentageCalculations() {
        SimpleContainer container = new SimpleContainer(2);
        SimpleContainerData data = new SimpleContainerData(5);
        data.set(0, 1500);
        data.set(1, 3000);
        data.set(2, 100);
        data.set(3, 200);
        data.set(4, 2);

        DewCondenserMenu menu = new DewCondenserMenu(null, 1, new Inventory(null, null), container, data);

        assertEquals(1500, menu.getWater());
        assertEquals(3000, menu.getMaxWater());
        assertEquals(100, menu.getProgress());
        assertEquals(200, menu.getMaxProgress());
        assertEquals(2, menu.getStatus());
        assertEquals(0.5f, menu.getWaterPercentage(), 0.001f);
        assertEquals(0.5f, menu.getProgressPercentage(), 0.001f);
    }

    @Test
    void shouldDrainWaterDirectly() {
        DewCondenserBlockEntity entity = createTestEntity();
        entity.setWaterAmount(1000);

        int drained = entity.drainWater(250);
        assertEquals(250, drained);
        assertEquals(750, entity.getWaterAmount());

        int drainedAll = entity.drainWater(1000);
        assertEquals(750, drainedAll);
        assertEquals(0, entity.getWaterAmount());
    }
}
