package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class IrrigationHoseBlock extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Map.of(
            Direction.NORTH, NORTH,
            Direction.SOUTH, SOUTH,
            Direction.EAST, EAST,
            Direction.WEST, WEST,
            Direction.UP, UP
    );

    private static final VoxelShape CORE_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 2.0, 10.0);
    private static final VoxelShape NORTH_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 2.0, 6.0);
    private static final VoxelShape SOUTH_SHAPE = Block.box(6.0, 0.0, 10.0, 10.0, 2.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.box(0.0, 0.0, 6.0, 6.0, 2.0, 10.0);
    private static final VoxelShape EAST_SHAPE = Block.box(10.0, 0.0, 6.0, 16.0, 2.0, 10.0);
    private static final VoxelShape UP_SHAPE = Block.box(6.0, 2.0, 6.0, 10.0, 16.0, 10.0);
    private static final VoxelShape COLLISION_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 1.0, 10.0);

    public IrrigationHoseBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP);
    }

    public static boolean canConnect(LevelReader level, BlockPos pos, Direction direction) {
        BlockPos targetPos = pos.relative(direction);
        BlockState targetState = level.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        if (targetBlock instanceof IrrigationHoseBlock
                || targetBlock instanceof IrrigationSprinklerBlock
                || targetBlock instanceof IrrigationPumpBlock) {
            return true;
        }
        if (direction != Direction.UP && direction != Direction.DOWN) {
            BlockState aboveTarget = level.getBlockState(targetPos.above());
            if (aboveTarget.getBlock() instanceof IrrigationHoseBlock
                    || aboveTarget.getBlock() instanceof IrrigationSprinklerBlock) {
                return true;
            }
            BlockState belowTarget = level.getBlockState(targetPos.below());
            if (belowTarget.getBlock() instanceof IrrigationHoseBlock
                    || belowTarget.getBlock() instanceof IrrigationSprinklerBlock) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return defaultBlockState()
                .setValue(NORTH, canConnect(level, pos, Direction.NORTH))
                .setValue(SOUTH, canConnect(level, pos, Direction.SOUTH))
                .setValue(EAST, canConnect(level, pos, Direction.EAST))
                .setValue(WEST, canConnect(level, pos, Direction.WEST))
                .setValue(UP, canConnect(level, pos, Direction.UP));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        BooleanProperty prop = PROPERTY_BY_DIRECTION.get(direction);
        if (prop != null) {
            return state.setValue(prop, canConnect(level, pos, direction));
        }
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE_SHAPE;
        if (state.getValue(NORTH)) {
            shape = Shapes.or(shape, NORTH_SHAPE);
        }
        if (state.getValue(SOUTH)) {
            shape = Shapes.or(shape, SOUTH_SHAPE);
        }
        if (state.getValue(EAST)) {
            shape = Shapes.or(shape, EAST_SHAPE);
        }
        if (state.getValue(WEST)) {
            shape = Shapes.or(shape, WEST_SHAPE);
        }
        if (state.getValue(UP)) {
            shape = Shapes.or(shape, UP_SHAPE);
        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }
}
