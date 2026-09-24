package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SandMaglevRailBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    private static final double SPEED_BOOST = 1.8;

    public SandMaglevRailBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis = context.getHorizontalDirection().getAxis();
        return defaultBlockState().setValue(AXIS, axis);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (level.isClientSide()) {
            return;
        }

        Direction.Axis axis = state.getValue(AXIS);
        Vec3 motion = entity.getDeltaMovement();

        double boostX = axis == Direction.Axis.X ? applyBoost(motion.x) : motion.x;
        double boostZ = axis == Direction.Axis.Z ? applyBoost(motion.z) : motion.z;

        entity.setDeltaMovement(boostX, motion.y, boostZ);
    }

    private static double applyBoost(double velocity) {
        if (Math.abs(velocity) < 0.01) {
            return velocity;
        }
        return velocity * SPEED_BOOST;
    }
}
