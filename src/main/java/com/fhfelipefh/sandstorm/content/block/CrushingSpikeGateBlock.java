package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;


import com.fhfelipefh.sandstorm.content.block.entity.CrushingSpikeGateBlockEntity;

public class CrushingSpikeGateBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private static final VoxelShape POST_LEFT_Z = Block.box(0, 0, 6, 3, 16, 10);
    private static final VoxelShape POST_RIGHT_Z = Block.box(13, 0, 6, 16, 16, 10);
    private static final VoxelShape TOP_HOUSING_Z = Block.box(0, 12, 5, 16, 16, 11);
    private static final VoxelShape FLOOR_TRACK_Z = Block.box(0, 0, 6, 16, 2, 10);
    private static final VoxelShape BODY_CLOSED_Z = Block.box(3, 2, 6, 13, 12, 10);

    private static final VoxelShape SHAPE_CLOSED_Z = Shapes.or(POST_LEFT_Z, POST_RIGHT_Z, TOP_HOUSING_Z, FLOOR_TRACK_Z, BODY_CLOSED_Z);
    private static final VoxelShape SHAPE_OPEN_OUTLINE_Z = Shapes.or(POST_LEFT_Z, POST_RIGHT_Z, TOP_HOUSING_Z, FLOOR_TRACK_Z);
    private static final VoxelShape SHAPE_COLLISION_OPEN_Z = Shapes.or(POST_LEFT_Z, POST_RIGHT_Z, TOP_HOUSING_Z);

    private static final VoxelShape POST_LEFT_X = Block.box(6, 0, 0, 10, 16, 3);
    private static final VoxelShape POST_RIGHT_X = Block.box(6, 0, 13, 10, 16, 16);
    private static final VoxelShape TOP_HOUSING_X = Block.box(5, 12, 0, 11, 16, 16);
    private static final VoxelShape FLOOR_TRACK_X = Block.box(6, 0, 0, 10, 2, 16);
    private static final VoxelShape BODY_CLOSED_X = Block.box(6, 2, 3, 10, 12, 13);

    private static final VoxelShape SHAPE_CLOSED_X = Shapes.or(POST_LEFT_X, POST_RIGHT_X, TOP_HOUSING_X, FLOOR_TRACK_X, BODY_CLOSED_X);
    private static final VoxelShape SHAPE_OPEN_OUTLINE_X = Shapes.or(POST_LEFT_X, POST_RIGHT_X, TOP_HOUSING_X, FLOOR_TRACK_X);
    private static final VoxelShape SHAPE_COLLISION_OPEN_X = Shapes.or(POST_LEFT_X, POST_RIGHT_X, TOP_HOUSING_X);

    public CrushingSpikeGateBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction facing;
        if (clickedFace.getAxis().isHorizontal()) {
            facing = clickedFace.getOpposite();
        } else {
            facing = context.getHorizontalDirection().getOpposite();
        }
        boolean hasSignal = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return defaultBlockState().setValue(FACING, facing).setValue(OPEN, !hasSignal);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean open = state.getValue(OPEN);
        Direction facing = state.getValue(FACING);
        if (facing.getAxis() == Direction.Axis.X) {
            return open ? SHAPE_OPEN_OUTLINE_X : SHAPE_CLOSED_X;
        }
        return open ? SHAPE_OPEN_OUTLINE_Z : SHAPE_CLOSED_Z;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean open = state.getValue(OPEN);
        Direction facing = state.getValue(FACING);
        if (facing.getAxis() == Direction.Axis.X) {
            return open ? SHAPE_COLLISION_OPEN_X : SHAPE_CLOSED_X;
        }
        return open ? SHAPE_COLLISION_OPEN_Z : SHAPE_CLOSED_Z;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof BlockItem) {
            return InteractionResult.PASS;
        }
        toggleGate(state, level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        toggleGate(state, level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (level.isClientSide()) {
            return;
        }

        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal == state.getValue(OPEN)) {
            toggleGate(state, level, pos);
        }
    }

    private void toggleGate(BlockState state, Level level, BlockPos pos) {
        boolean willOpen = !state.getValue(OPEN);
        level.setBlock(pos, state.setValue(OPEN, willOpen), 3);

        if (willOpen) {
            level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 0.9f);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrushingSpikeGateBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, p, st, be) -> {
            if (be instanceof CrushingSpikeGateBlockEntity entity) {
                CrushingSpikeGateBlockEntity.tick(lvl, p, st, entity);
            }
        };
    }
}
