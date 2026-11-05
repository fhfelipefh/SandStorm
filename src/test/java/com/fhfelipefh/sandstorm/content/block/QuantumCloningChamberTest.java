package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuantumCloningChamberTest {

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
        frozenField.set(BuiltInRegistries.MENU, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());
    }

    @Test
    void shouldHaveExpectedDefaultStateAndProperties() {
        BlockState defaultState = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState();
        assertNotNull(defaultState);
        assertEquals(Direction.NORTH, defaultState.getValue(QuantumSleeperPodBlock.FACING));
        assertEquals(DoubleBlockHalf.LOWER, defaultState.getValue(QuantumSleeperPodBlock.HALF));
        assertTrue(defaultState.getValue(QuantumSleeperPodBlock.ACTIVE));
        assertFalse(defaultState.getValue(QuantumSleeperPodBlock.OCCUPIED));
        assertFalse(defaultState.getValue(QuantumSleeperPodBlock.OPEN));
    }

    @Test
    void shouldSupportDoubleBlockHalvesAndDoorOpening() {
        BlockState lowerClosed = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState()
                .setValue(QuantumSleeperPodBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(QuantumSleeperPodBlock.OPEN, false);
        BlockState upperOpen = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState()
                .setValue(QuantumSleeperPodBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(QuantumSleeperPodBlock.OPEN, true);

        assertEquals(DoubleBlockHalf.LOWER, lowerClosed.getValue(QuantumSleeperPodBlock.HALF));
        assertFalse(lowerClosed.getValue(QuantumSleeperPodBlock.OPEN));

        assertEquals(DoubleBlockHalf.UPPER, upperOpen.getValue(QuantumSleeperPodBlock.HALF));
        assertTrue(upperOpen.getValue(QuantumSleeperPodBlock.OPEN));
    }

    @Test
    void shouldDifferentiateCollisionShapeBetweenOpenAndClosed() {
        BlockState closedState = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState()
                .setValue(QuantumSleeperPodBlock.OPEN, false);
        BlockState openState = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState()
                .setValue(QuantumSleeperPodBlock.OPEN, true);

        VoxelShape closedShape = closedState.getCollisionShape(null, BlockPos.ZERO, CollisionContext.empty());
        VoxelShape openShape = openState.getCollisionShape(null, BlockPos.ZERO, CollisionContext.empty());

        assertNotNull(closedShape);
        assertNotNull(openShape);
        assertNotEquals(closedShape, openShape);
        assertTrue(openShape.bounds().maxY > 0.0);
    }

    @Test
    void shouldEnforceSolidRearWallAndOpenDoorwayForEveryFacing() {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            BlockState state = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState()
                    .setValue(QuantumSleeperPodBlock.FACING, facing)
                    .setValue(QuantumSleeperPodBlock.OPEN, true);
            VoxelShape shape = state.getCollisionShape(null, BlockPos.ZERO, CollisionContext.empty());

            double doorX = 0.5;
            double doorZ = 0.5;
            double rearX = 0.5;
            double rearZ = 0.5;

            switch (facing) {
                case NORTH -> {
                    doorZ = 0.05;
                    rearZ = 0.95;
                }
                case SOUTH -> {
                    doorZ = 0.95;
                    rearZ = 0.05;
                }
                case WEST -> {
                    doorX = 0.05;
                    rearX = 0.95;
                }
                case EAST -> {
                    doorX = 0.95;
                    rearX = 0.05;
                }
            }

            double checkDoorX = doorX;
            double checkDoorZ = doorZ;
            double checkRearX = rearX;
            double checkRearZ = rearZ;

            boolean doorSolid = shape.toAabbs().stream().anyMatch((AABB box) -> box.contains(checkDoorX, 0.5, checkDoorZ));
            boolean rearSolid = shape.toAabbs().stream().anyMatch((AABB box) -> box.contains(checkRearX, 0.5, checkRearZ));

            assertFalse(doorSolid, "Doorway must be open for facing " + facing);
            assertTrue(rearSolid, "Rear spine wall must be solid for facing " + facing);
        }
    }

    @Test
    void shouldOnlyCreateBlockEntityOnLowerHalf() {
        BlockState lowerState = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState()
                .setValue(QuantumSleeperPodBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState upperState = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState()
                .setValue(QuantumSleeperPodBlock.HALF, DoubleBlockHalf.UPPER);

        BlockEntity lowerBe = SandStormBlocks.QUANTUM_SLEEPER_POD.newBlockEntity(BlockPos.ZERO, lowerState);
        BlockEntity upperBe = SandStormBlocks.QUANTUM_SLEEPER_POD.newBlockEntity(BlockPos.ZERO.above(), upperState);

        assertNotNull(lowerBe);
        assertNull(upperBe);
    }

    @Test
    void shouldBeRenderedByCustomBlockEntityRenderer() {
        BlockState defaultState = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState();
        assertEquals(RenderShape.INVISIBLE, defaultState.getRenderShape());
    }

    @Test
    void shouldManageBioNutrientsAndEnergyForEmergencyRegeneration() {
        BlockState lowerState = SandStormBlocks.QUANTUM_SLEEPER_POD.defaultBlockState()
                .setValue(QuantumSleeperPodBlock.HALF, DoubleBlockHalf.LOWER);
        QuantumSleeperPodBlockEntity pod = new QuantumSleeperPodBlockEntity(BlockPos.ZERO, lowerState);

        pod.setBioNutrients(100);
        pod.setStoredEnergy(50000);
        pod.setHasClone(false);

        assertEquals(100, pod.getBioNutrients());
        assertEquals(50000, pod.getStoredEnergy());
        assertFalse(pod.hasClone());

        pod.setBioNutrients(pod.getBioNutrients() - 3);
        pod.setStoredEnergy(pod.getStoredEnergy() - 500);

        assertEquals(97, pod.getBioNutrients());
        assertEquals(49500, pod.getStoredEnergy());
    }
}
