package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class CrushingSpikeGateBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private static final VoxelShape SHAPE_CLOSED = Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_OPEN = Block.box(0, 12, 0, 16, 16, 16);

    public CrushingSpikeGateBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(OPEN, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? SHAPE_OPEN : SHAPE_CLOSED;
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
        if (hasSignal != state.getValue(OPEN)) {
            toggleGate(state, level, pos);
        }
    }

    private void toggleGate(BlockState state, Level level, BlockPos pos) {
        boolean willOpen = !state.getValue(OPEN);
        level.setBlock(pos, state.setValue(OPEN, willOpen), 3);

        if (willOpen) {
            level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.8f, 1.0f);
        } else {
            level.playSound(null, pos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.8f, 0.8f);
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                AABB aabb = new AABB(pos);
                List<LivingEntity> victims = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb);
                for (LivingEntity victim : victims) {
                    victim.hurtServer(serverLevel, serverLevel.damageSources().generic(), 12.0f);
                }
            }
        }
    }
}
