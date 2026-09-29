package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.SolidStateAccumulatorBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolidStateAccumulatorBlockEntityTest {

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

    private SolidStateAccumulatorBlockEntity createTestEntity() {
        return new SolidStateAccumulatorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
    }

    @Test
    void shouldInitializeWithDefaultValues() {
        SolidStateAccumulatorBlockEntity be = createTestEntity();

        assertEquals(0, be.getStoredEnergy());
        assertEquals(SolidStateAccumulatorBlockEntity.DEFAULT_CAPACITY, be.getMaxEnergy());
        assertEquals(0, be.getMode());
        assertFalse(be.isCharging());
        assertFalse(be.isDischarging());
        assertEquals(2, be.getContainerSize());
        assertTrue(be.isEmpty());
        assertNotNull(be.getDisplayName());
    }

    @Test
    void shouldClampStoredEnergyWithinBounds() {
        SolidStateAccumulatorBlockEntity be = createTestEntity();

        be.setStoredEnergy(250000);
        assertEquals(250000, be.getStoredEnergy());

        be.setStoredEnergy(800000);
        assertEquals(SolidStateAccumulatorBlockEntity.DEFAULT_CAPACITY, be.getStoredEnergy());

        be.setStoredEnergy(-1000);
        assertEquals(0, be.getStoredEnergy());
    }

    @Test
    void shouldCycleModesCorrectly() {
        SolidStateAccumulatorBlockEntity be = createTestEntity();

        assertEquals(0, be.getMode());

        be.cycleMode();
        assertEquals(1, be.getMode());

        be.cycleMode();
        assertEquals(2, be.getMode());

        be.cycleMode();
        assertEquals(0, be.getMode());

        be.setMode(5);
        assertEquals(2, be.getMode());
    }

    @Test
    void shouldManageInventoryAndAutomationSides() {
        SolidStateAccumulatorBlockEntity be = createTestEntity();

        int[] upSlots = be.getSlotsForFace(Direction.UP);
        assertEquals(1, upSlots.length);
        assertEquals(0, upSlots[0]);

        int[] downSlots = be.getSlotsForFace(Direction.DOWN);
        assertEquals(1, downSlots.length);
        assertEquals(1, downSlots[0]);

        int[] sideSlots = be.getSlotsForFace(Direction.EAST);
        assertEquals(1, sideSlots.length);
        assertEquals(1, sideSlots[0]);

        assertTrue(be.canPlaceItemThroughFace(0, new ItemStack(Items.REDSTONE), Direction.UP));
        assertTrue(be.canPlaceItemThroughFace(0, new ItemStack(Items.REDSTONE_BLOCK), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(0, new ItemStack(Items.DIRT), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(1, new ItemStack(Items.REDSTONE), Direction.UP));

        assertTrue(be.canTakeItemThroughFace(1, new ItemStack(Items.BUCKET), Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(0, new ItemStack(Items.REDSTONE), Direction.DOWN));

        be.setItem(0, new ItemStack(Items.REDSTONE, 10));
        assertFalse(be.isEmpty());
        assertEquals(10, be.getItem(0).getCount());

        ItemStack removed = be.removeItem(0, 4);
        assertEquals(4, removed.getCount());
        assertEquals(6, be.getItem(0).getCount());

        ItemStack taken = be.removeItemNoUpdate(0);
        assertEquals(6, taken.getCount());
        assertTrue(be.getItem(0).isEmpty());

        be.setItem(1, new ItemStack(Items.IRON_INGOT, 5));
        be.clearContent();
        assertTrue(be.isEmpty());
    }

    @Test
    void shouldSynchronizeContainerData() {
        SolidStateAccumulatorBlockEntity be = createTestEntity();
        be.setStoredEnergy(350000);
        be.setMode(2);

        ContainerData data = be.getDataAccess();
        assertEquals(7, data.getCount());
        int lowEnergy = data.get(0);
        int highEnergy = data.get(1);
        int reconstructedEnergy = (highEnergy << 16) | lowEnergy;
        assertEquals(350000, reconstructedEnergy);
        assertEquals(2, data.get(4));

        data.set(4, 1);
        assertEquals(1, be.getMode());
    }
}
