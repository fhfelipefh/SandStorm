package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TitaniumSpikeWallBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE_UP = Block.box(0, 0, 0, 16, 6, 16);
    private static final VoxelShape SHAPE_DOWN = Block.box(0, 10, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 10, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 6);
    private static final VoxelShape SHAPE_WEST = Block.box(10, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 0, 0, 6, 16, 16);

    public TitaniumSpikeWallBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return switch (direction) {
            case DOWN -> SHAPE_DOWN;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_UP;
        };
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        applySpikeDamage(level, pos, state, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        applySpikeDamage(level, pos, state, entity);
    }

    private void applySpikeDamage(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living) {
            living.hurtServer(serverLevel, serverLevel.damageSources().cactus(), 6.0f);
            living.makeStuckInBlock(state, new Vec3(0.65, 0.65, 0.65));
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
        }
    }
}
