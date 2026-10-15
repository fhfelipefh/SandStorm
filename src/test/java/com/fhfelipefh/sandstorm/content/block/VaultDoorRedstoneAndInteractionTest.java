package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.VaultDoorControllerBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VaultDoorRedstoneAndInteractionTest {

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
    }

    @Test
    void shouldVerifyBijectiveMappingBetweenControllerAndAllDummyPositions() {
        BlockPos controllerPos = new BlockPos(100, 64, -200);
        Direction[] facings = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction facing : facings) {
            for (int col = 0; col <= 2; col++) {
                for (int row = 0; row <= 2; row++) {
                    BlockPos dummyPos = VaultDoorControllerBlock.getRelativePos(controllerPos, facing, col, row);
                    BlockState dummyState = SandStormBlocks.VAULT_DOOR_DUMMY.defaultBlockState()
                            .setValue(VaultDoorDummyBlock.FACING, facing)
                            .setValue(VaultDoorDummyBlock.COL, col)
                            .setValue(VaultDoorDummyBlock.ROW, row);

                    BlockPos calculatedControllerPos = VaultDoorDummyBlock.getControllerPos(dummyState, dummyPos);
                    assertEquals(controllerPos, calculatedControllerPos);
                }
            }
        }
    }

    @Test
    void shouldProduceNineUniqueCoplanarPositionsPerFacing() {
        BlockPos controllerPos = new BlockPos(0, 10, 0);
        Direction[] facings = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction facing : facings) {
            Set<BlockPos> uniquePositions = new HashSet<>();
            for (int col = 0; col <= 2; col++) {
                for (int row = 0; row <= 2; row++) {
                    BlockPos pos = VaultDoorControllerBlock.getRelativePos(controllerPos, facing, col, row);
                    uniquePositions.add(pos);
                    assertEquals(10 + row, pos.getY());
                }
            }
            assertEquals(9, uniquePositions.size());

            for (BlockPos p : uniquePositions) {
                if (facing.getAxis() == Direction.Axis.Z) {
                    assertEquals(controllerPos.getZ(), p.getZ());
                } else {
                    assertEquals(controllerPos.getX(), p.getX());
                }
            }
        }
    }

    @Test
    void shouldValidateControllerAndDummyDefaultBlockStates() {
        BlockState controllerState = SandStormBlocks.VAULT_DOOR_CONTROLLER.defaultBlockState();
        assertNotNull(controllerState);
        assertEquals(Direction.NORTH, controllerState.getValue(VaultDoorControllerBlock.FACING));
        assertFalse(controllerState.getValue(VaultDoorControllerBlock.OPEN));
        assertFalse(controllerState.getValue(VaultDoorControllerBlock.POWERED));

        BlockState dummyState = SandStormBlocks.VAULT_DOOR_DUMMY.defaultBlockState();
        assertNotNull(dummyState);
        assertEquals(Direction.NORTH, dummyState.getValue(VaultDoorDummyBlock.FACING));
        assertEquals(1, dummyState.getValue(VaultDoorDummyBlock.COL));
        assertEquals(1, dummyState.getValue(VaultDoorDummyBlock.ROW));
    }

    @Test
    void shouldVerifyControllerOriginIdentityAtColOneRowZero() {
        BlockPos controllerPos = new BlockPos(42, 64, -77);
        Direction[] facings = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction facing : facings) {
            BlockPos relativeOrigin = VaultDoorControllerBlock.getRelativePos(controllerPos, facing, 1, 0);
            assertEquals(controllerPos, relativeOrigin);
        }
    }

    @Test
    void shouldReturnEmptyCollisionWhenOpenAndBlockCollisionWhenClosed() {
        BlockState closedState = SandStormBlocks.VAULT_DOOR_CONTROLLER.defaultBlockState()
                .setValue(VaultDoorControllerBlock.OPEN, false);
        BlockState openState = SandStormBlocks.VAULT_DOOR_CONTROLLER.defaultBlockState()
                .setValue(VaultDoorControllerBlock.OPEN, true);

        assertEquals(Shapes.block(), SandStormBlocks.VAULT_DOOR_CONTROLLER.getCollisionShape(closedState, null, BlockPos.ZERO, CollisionContext.empty()));
        assertEquals(Shapes.empty(), SandStormBlocks.VAULT_DOOR_CONTROLLER.getCollisionShape(openState, null, BlockPos.ZERO, CollisionContext.empty()));
    }

    @Test
    void shouldAdvanceAndInterpolateAnimationProgressCorrectly() {
        BlockPos pos = new BlockPos(0, 60, 0);
        BlockState closedState = SandStormBlocks.VAULT_DOOR_CONTROLLER.defaultBlockState()
                .setValue(VaultDoorControllerBlock.OPEN, false);
        VaultDoorControllerBlockEntity entity = new VaultDoorControllerBlockEntity(pos, closedState);

        assertEquals(0.0f, entity.getProgress(0.0f));
        assertEquals(0.0f, entity.getProgress(1.0f));

        BlockState openState = closedState.setValue(VaultDoorControllerBlock.OPEN, true);
        for (int tick = 1; tick <= 60; tick++) {
            VaultDoorControllerBlockEntity.tick(null, pos, openState, entity);
            float expected = (float) tick / 60.0f;
            assertEquals(expected, entity.getProgress(1.0f), 0.001f);
        }

        assertEquals(1.0f, entity.getProgress(1.0f), 0.001f);
        VaultDoorControllerBlockEntity.tick(null, pos, openState, entity);
        assertEquals(1.0f, entity.getProgress(1.0f), 0.001f);

        for (int tick = 1; tick <= 60; tick++) {
            VaultDoorControllerBlockEntity.tick(null, pos, closedState, entity);
            float expected = 1.0f - ((float) tick / 60.0f);
            assertEquals(expected, entity.getProgress(1.0f), 0.001f);
        }
        assertEquals(0.0f, entity.getProgress(1.0f), 0.001f);
    }

    @Test
    void shouldRemainBoundedUnderRepeatedStateTogglesWithoutFlickering() {
        BlockPos pos = new BlockPos(10, 60, 10);
        BlockState closedState = SandStormBlocks.VAULT_DOOR_CONTROLLER.defaultBlockState()
                .setValue(VaultDoorControllerBlock.OPEN, false);
        BlockState openState = closedState.setValue(VaultDoorControllerBlock.OPEN, true);
        VaultDoorControllerBlockEntity entity = new VaultDoorControllerBlockEntity(pos, closedState);

        for (int i = 0; i < 200; i++) {
            BlockState currentState = (i % 2 == 0) ? openState : closedState;
            VaultDoorControllerBlockEntity.tick(null, pos, currentState, entity);
            float progress = entity.getProgress(0.5f);
            assertTrue(progress >= 0.0f);
            assertTrue(progress <= 1.0f);
        }
    }
}
