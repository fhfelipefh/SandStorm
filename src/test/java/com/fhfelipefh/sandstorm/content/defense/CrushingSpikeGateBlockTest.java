package com.fhfelipefh.sandstorm.content.defense;

import com.fhfelipefh.sandstorm.content.block.CrushingSpikeGateBlock;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrushingSpikeGateBlockTest {

    private static CrushingSpikeGateBlock gate;

    @BeforeAll
    static void init() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("crushing_spike_gate"));
        gate = new CrushingSpikeGateBlock(BlockBehaviour.Properties.of().setId(blockKey));
    }

    @Test
    void shouldProvideDirectionalOutlineShapes() {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (boolean open : List.of(false, true)) {
                BlockState state = gate.defaultBlockState()
                        .setValue(CrushingSpikeGateBlock.FACING, direction)
                        .setValue(CrushingSpikeGateBlock.OPEN, open);

                VoxelShape shape = state.getShape(null, BlockPos.ZERO, CollisionContext.empty());
                assertFalse(shape.isEmpty());
                assertEquals(1.0, shape.max(Direction.Axis.Y), 0.001);
                assertEquals(0.0, shape.min(Direction.Axis.Y), 0.001);
            }
        }
    }

    @Test
    void shouldProvidePassableCollisionWhenOpen() {
        BlockState openState = gate.defaultBlockState()
                        .setValue(CrushingSpikeGateBlock.FACING, Direction.NORTH)
                        .setValue(CrushingSpikeGateBlock.OPEN, true);

        VoxelShape collision = openState.getCollisionShape(null, BlockPos.ZERO, CollisionContext.empty());
        assertFalse(collision.isEmpty());

        boolean centerBlocked = false;
        Vec3 center = new Vec3(0.5, 0.5, 0.5);
        for (AABB box : collision.toAabbs()) {
            if (box.contains(center)) {
                centerBlocked = true;
                break;
            }
        }
        assertFalse(centerBlocked);

        BlockState closedState = gate.defaultBlockState()
                        .setValue(CrushingSpikeGateBlock.FACING, Direction.NORTH)
                        .setValue(CrushingSpikeGateBlock.OPEN, false);

        VoxelShape closedCollision = closedState.getCollisionShape(null, BlockPos.ZERO, CollisionContext.empty());
        boolean closedCenterBlocked = false;
        for (AABB box : closedCollision.toAabbs()) {
            if (box.contains(center)) {
                closedCenterBlocked = true;
                break;
            }
        }
        assertTrue(closedCenterBlocked);
    }

    @Test
    void shouldPassInteractionWhenHoldingBlockItemForStacking() {
        ItemStack stack = new ItemStack(Items.STONE);
        assertTrue(stack.getItem() instanceof BlockItem);

        BlockState state = gate.defaultBlockState();
        BlockHitResult hitResult = new BlockHitResult(new Vec3(0.5, 1.0, 0.5), Direction.UP, BlockPos.ZERO, false);

        InteractionResult result = state.useItemOn(stack, null, null, InteractionHand.MAIN_HAND, hitResult);
        assertEquals(InteractionResult.PASS, result);
    }
}
