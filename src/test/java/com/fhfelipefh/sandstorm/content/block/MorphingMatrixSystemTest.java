package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.MorphingMatrixCoreBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.MorphingMatrixCoreMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MorphingMatrixSystemTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void testMorphingFluidTransitionProperties() {
        assertNotNull(MorphingFluidTransitionBlock.STAGE);
        assertEquals(5, MorphingFluidTransitionBlock.STAGE.getPossibleValues().size());
        assertTrue(MorphingFluidTransitionBlock.STAGE.getPossibleValues().contains(0));
        assertTrue(MorphingFluidTransitionBlock.STAGE.getPossibleValues().contains(4));
    }

    @Test
    void testMorphingMatrixCoreMenuSyncAndValidation() {
        Inventory inventory = new Inventory(null, null);

        SimpleContainer container = new SimpleContainer(1);
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 3);
        data.set(1, 100);
        data.set(2, 0);
        data.set(3, 450);
        data.set(4, 0);
        data.set(5, 1);

        MorphingMatrixCoreMenu menu = new MorphingMatrixCoreMenu(null, 1, inventory, null, container, data);

        assertEquals(3, menu.getState());
        assertEquals(100, menu.getReserveBlocks());
        assertEquals(450, menu.getSavedCount());
        assertTrue(menu.isHologramActive());
        assertTrue(menu.getReserveScaled(100) > 0);

        assertFalse(menu.getSlot(0).mayPlace(ItemStack.EMPTY));
        assertFalse(menu.getSlot(0).mayPlace(new ItemStack(Items.DIRT)));
    }

    @Test
    void testMorphingBlockStatePreservationInBlueprint() {
        MorphingMatrixCoreBlockEntity entity = new MorphingMatrixCoreBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        Map<BlockPos, BlockState> structure = new HashMap<>();
        BlockPos floorPos = new BlockPos(0, 0, 0);
        BlockPos windowPos = new BlockPos(1, 4, 1);
        BlockPos doorLowerPos = new BlockPos(2, 1, 0);
        BlockPos doorUpperPos = new BlockPos(2, 2, 0);

        BlockState floorState = Blocks.IRON_BLOCK.defaultBlockState();
        BlockState windowState = Blocks.GLASS.defaultBlockState();
        BlockState doorLower = Blocks.IRON_DOOR.defaultBlockState()
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.FACING, Direction.NORTH)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT);
        BlockState doorUpper = Blocks.IRON_DOOR.defaultBlockState()
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(DoorBlock.FACING, Direction.NORTH)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT);

        structure.put(floorPos, floorState);
        structure.put(windowPos, windowState);
        structure.put(doorLowerPos, doorLower);
        structure.put(doorUpperPos, doorUpper);

        entity.saveBlueprint(structure, BlockPos.ZERO);

        assertEquals(4, entity.getSavedRelativePositions().size());
        assertEquals(floorState, entity.getSavedBlockStates().get(floorPos));
        assertEquals(windowState, entity.getSavedBlockStates().get(windowPos));
        assertEquals(doorLower, entity.getSavedBlockStates().get(doorLowerPos));
        assertEquals(doorUpper, entity.getSavedBlockStates().get(doorUpperPos));
    }

    @Test
    void testMorphingResourceKeys() {
        ResourceKey<Block> coreKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("morphing_matrix_core"));
        assertNotNull(coreKey);
        assertEquals("morphing_matrix_core", coreKey.identifier().getPath());

        ResourceKey<Block> alloyKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("morphing_alloy_block"));
        assertNotNull(alloyKey);
        assertEquals("morphing_alloy_block", alloyKey.identifier().getPath());

        ResourceKey<Block> doorKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("morphing_alloy_door"));
        assertNotNull(doorKey);
        assertEquals("morphing_alloy_door", doorKey.identifier().getPath());

        ResourceKey<Block> windowKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("morphing_alloy_window"));
        assertNotNull(windowKey);
        assertEquals("morphing_alloy_window", windowKey.identifier().getPath());

        ResourceKey<Block> transitionKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("morphing_fluid_transition"));
        assertNotNull(transitionKey);
        assertEquals("morphing_fluid_transition", transitionKey.identifier().getPath());
    }
}
