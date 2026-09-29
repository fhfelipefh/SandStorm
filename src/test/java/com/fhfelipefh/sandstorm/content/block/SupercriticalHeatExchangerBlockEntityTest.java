package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.SupercriticalHeatExchangerBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupercriticalHeatExchangerBlockEntityTest {

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

        assertNotNull(SandStormItems.POTABLE_WATER_BOTTLE);

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    private SupercriticalHeatExchangerBlockEntity createTestEntity() {
        return new SupercriticalHeatExchangerBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
    }

    @Test
    void shouldInitializeWithDefaultValues() {
        SupercriticalHeatExchangerBlockEntity be = createTestEntity();

        assertEquals(4, be.getContainerSize());
        assertTrue(be.isEmpty());
        assertEquals(0, be.getWaterAmount());
        assertEquals(SupercriticalHeatExchangerBlockEntity.MAX_WATER, be.getMaxWater());
        assertEquals(0, be.getSteamPressure());
        assertEquals(0, be.getStoredEnergy());
        assertEquals(SupercriticalHeatExchangerBlockEntity.MAX_ENERGY, be.getMaxEnergy());
        assertFalse(be.isOperating());
        assertNotNull(be.getDisplayName());
    }

    @Test
    void shouldManageWaterClamping() {
        SupercriticalHeatExchangerBlockEntity be = createTestEntity();

        be.setWaterAmount(4500);
        assertEquals(4500, be.getWaterAmount());

        be.setWaterAmount(12000);
        assertEquals(SupercriticalHeatExchangerBlockEntity.MAX_WATER, be.getWaterAmount());

        be.setWaterAmount(-100);
        assertEquals(0, be.getWaterAmount());
    }

    @Test
    void shouldManageSteamPressureClamping() {
        SupercriticalHeatExchangerBlockEntity be = createTestEntity();

        be.setSteamPressure(45);
        assertEquals(45, be.getSteamPressure());

        be.setSteamPressure(150);
        assertEquals(100, be.getSteamPressure());

        be.setSteamPressure(-10);
        assertEquals(0, be.getSteamPressure());
    }

    @Test
    void shouldValidateSlotPlacementsAndFiltering() {
        SupercriticalHeatExchangerBlockEntity be = createTestEntity();

        assertTrue(be.canPlaceItem(0, new ItemStack(Items.WATER_BUCKET)));
        assertFalse(be.canPlaceItem(0, new ItemStack(Items.LAVA_BUCKET)));
        assertFalse(be.canPlaceItem(0, new ItemStack(Items.BUCKET)));

        assertFalse(be.canPlaceItem(1, new ItemStack(Items.BUCKET)));

        assertTrue(be.canPlaceItem(2, new ItemStack(SandStormItems.THERMAL_RADIATOR_FIN)));
        assertTrue(be.canPlaceItem(2, new ItemStack(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE)));
        assertFalse(be.canPlaceItem(2, new ItemStack(Items.IRON_INGOT)));

        assertTrue(be.canPlaceItem(3, new ItemStack(Items.REDSTONE)));
        assertTrue(be.canPlaceItem(3, new ItemStack(Items.REDSTONE_BLOCK)));
        assertFalse(be.canPlaceItem(3, new ItemStack(Items.COAL)));
    }

    @Test
    void shouldValidateAutomationFaceAccess() {
        SupercriticalHeatExchangerBlockEntity be = createTestEntity();

        int[] upSlots = be.getSlotsForFace(Direction.UP);
        assertEquals(2, upSlots.length);
        assertEquals(0, upSlots[0]);
        assertEquals(2, upSlots[1]);

        int[] downSlots = be.getSlotsForFace(Direction.DOWN);
        assertEquals(2, downSlots.length);
        assertEquals(1, downSlots[0]);
        assertEquals(3, downSlots[1]);

        int[] sideSlots = be.getSlotsForFace(Direction.NORTH);
        assertEquals(4, sideSlots.length);

        assertTrue(be.canTakeItemThroughFace(1, new ItemStack(Items.BUCKET), Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(0, new ItemStack(Items.WATER_BUCKET), Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(2, new ItemStack(SandStormItems.THERMAL_RADIATOR_FIN), Direction.DOWN));

        assertTrue(be.canPlaceItemThroughFace(0, new ItemStack(Items.WATER_BUCKET), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(1, new ItemStack(Items.BUCKET), Direction.UP));
    }

    @Test
    void shouldManageEnergyStorageAndOperatingState() {
        SupercriticalHeatExchangerBlockEntity be = createTestEntity();

        be.setStoredEnergy(500000);
        assertEquals(500000, be.getStoredEnergy());

        be.setWaterAmount(2000);
        assertTrue(be.isOperating());

        be.setStoredEnergy(SupercriticalHeatExchangerBlockEntity.MAX_ENERGY);
        assertEquals(SupercriticalHeatExchangerBlockEntity.MAX_ENERGY, be.getStoredEnergy());
        assertFalse(be.isOperating());

        be.setStoredEnergy(1500000);
        assertEquals(SupercriticalHeatExchangerBlockEntity.MAX_ENERGY, be.getStoredEnergy());

        be.setStoredEnergy(-500);
        assertEquals(0, be.getStoredEnergy());
    }

    @Test
    void shouldInsertWaterViaFluidStorage() {
        SupercriticalHeatExchangerBlockEntity be = createTestEntity();
        Storage<FluidVariant> storage = be.getFluidStorage(Direction.UP);
        assertNotNull(storage);
        assertTrue(storage.supportsInsertion());
        assertFalse(storage.supportsExtraction());

        FluidVariant water = new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY);
        FluidVariant lava = new FluidVariantImpl(Fluids.LAVA, DataComponentPatch.EMPTY);

        long inserted;
        try (Transaction txn = Transaction.openOuter()) {
            inserted = storage.insert(water, FluidConstants.BUCKET * 2, txn);
            txn.commit();
        }

        assertEquals(FluidConstants.BUCKET * 2, inserted);
        assertEquals(2000, be.getWaterAmount());

        long lavaInsert;
        try (Transaction txn = Transaction.openOuter()) {
            lavaInsert = storage.insert(lava, FluidConstants.BUCKET, txn);
            txn.commit();
        }
        assertEquals(0, lavaInsert);
        assertEquals(2000, be.getWaterAmount());
    }

    @Test
    void shouldSynchronizeContainerData() {
        SupercriticalHeatExchangerBlockEntity be = createTestEntity();
        be.setSteamPressure(80);
        be.setWaterAmount(4000);
        be.setStoredEnergy(50000);

        ContainerData data = be.getDataAccess();
        assertEquals(8, data.getCount());

        int lowEnergy = data.get(0);
        int highEnergy = data.get(1);
        int reconstructedEnergy = (highEnergy << 16) | lowEnergy;
        assertEquals(50000, reconstructedEnergy);

        assertEquals(4000, data.get(4));
        assertEquals(80, data.get(6));
        assertEquals(1, data.get(7));
    }
}
