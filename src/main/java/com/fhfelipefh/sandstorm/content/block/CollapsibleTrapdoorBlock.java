package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollapsibleTrapdoorBlock extends Block {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_CLOSED = Block.box(0, 13, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_OPEN_NORTH = Block.box(0, 0, 0, 16, 16, 3);
    private static final VoxelShape SHAPE_OPEN_SOUTH = Block.box(0, 0, 13, 16, 16, 16);
    private static final VoxelShape SHAPE_OPEN_WEST = Block.box(0, 0, 0, 3, 16, 16);
    private static final VoxelShape SHAPE_OPEN_EAST = Block.box(13, 0, 0, 16, 16, 16);

    public CollapsibleTrapdoorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(OPEN, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing).setValue(OPEN, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(OPEN)) {
            return SHAPE_CLOSED;
        }
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_OPEN_SOUTH;
            case WEST -> SHAPE_OPEN_WEST;
            case EAST -> SHAPE_OPEN_EAST;
            default -> SHAPE_OPEN_NORTH;
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) {
            return Shapes.empty();
        }
        return SHAPE_CLOSED;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        if (type == PathComputationType.LAND) {
            return true;
        }
        return super.isPathfindable(state, type);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (!state.getValue(OPEN) && entity instanceof LivingEntity) {
            triggerCollapse(level, pos, state);
        }
    }

    private void triggerCollapse(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(OPEN, true), 3);
        level.playSound(
                null,
                pos,
                SoundEvents.IRON_TRAPDOOR_OPEN,
                SoundSource.BLOCKS,
                1.0f,
                1.2f
        );
        level.scheduleTick(pos, this, 50);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(OPEN)) {
            level.setBlock(pos, state.setValue(OPEN, false), 3);
            level.playSound(
                    null,
                    pos,
                    SoundEvents.IRON_TRAPDOOR_CLOSE,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
            );
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        boolean currentlyOpen = state.getValue(OPEN);
        level.setBlock(pos, state.setValue(OPEN, !currentlyOpen), 3);
        level.playSound(
                null,
                pos,
                currentlyOpen ? SoundEvents.IRON_TRAPDOOR_CLOSE : SoundEvents.IRON_TRAPDOOR_OPEN,
                SoundSource.BLOCKS,
                1.0f,
                1.0f
        );
        if (!currentlyOpen) {
            level.scheduleTick(pos, this, 50);
        }
        return InteractionResult.SUCCESS;
    }
}
