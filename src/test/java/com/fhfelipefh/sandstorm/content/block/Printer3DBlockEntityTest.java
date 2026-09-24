package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.Printer3DBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Printer3DBlockEntityTest {

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
        Printer3DBlockEntity be = new Printer3DBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(4, be.getContainerSize());
        assertEquals(0, be.getEnergy());
        assertEquals(0, be.getProgress());
        assertFalse(be.isProcessing());
        assertNotNull(be.getDisplayName());
        assertTrue(be.isEmpty());
    }

    @Test
    void shouldRecognizeFuelValues() {
        assertEquals(0, BaseMachineBlockEntity.getFuelEnergy(ItemStack.EMPTY));
        assertEquals(400, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.REDSTONE)));
        assertEquals(3600, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.REDSTONE_BLOCK)));
        assertEquals(0, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.DIAMOND)));
        assertEquals(0, BaseMachineBlockEntity.getFuelEnergy(new ItemStack(Items.IRON_INGOT)));
    }

    @Test
    void shouldValidateSlotFacesAndAutomation() {
        Printer3DBlockEntity be = new Printer3DBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        int[] topSlots = be.getSlotsForFace(Direction.UP);
        assertEquals(3, topSlots.length);
        assertEquals(0, topSlots[0]);
        assertEquals(1, topSlots[1]);
        assertEquals(3, topSlots[2]);

        int[] bottomSlots = be.getSlotsForFace(Direction.DOWN);
        assertEquals(2, bottomSlots.length);
        assertEquals(2, bottomSlots[0]);
        assertEquals(3, bottomSlots[1]);

        int[] sideSlots = be.getSlotsForFace(Direction.NORTH);
        assertEquals(4, sideSlots.length);
        assertEquals(1, sideSlots[0]);
        assertEquals(0, sideSlots[1]);
        assertEquals(3, sideSlots[2]);
        assertEquals(2, sideSlots[3]);

        assertFalse(be.canPlaceItemThroughFace(2, ItemStack.EMPTY, Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(0, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(be.canPlaceItemThroughFace(3, ItemStack.EMPTY, Direction.NORTH));
        assertTrue(be.canTakeItemThroughFace(2, ItemStack.EMPTY, Direction.DOWN));
        assertTrue(be.canTakeItemThroughFace(2, ItemStack.EMPTY, Direction.NORTH));
        assertFalse(be.canTakeItemThroughFace(0, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(1, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(3, ItemStack.EMPTY, Direction.DOWN));
    }
}
