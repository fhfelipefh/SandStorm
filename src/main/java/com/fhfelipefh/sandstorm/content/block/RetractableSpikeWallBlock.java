package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class RetractableSpikeWallBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RetractableSpikeWallBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean hasSignal = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(POWERED, hasSignal);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (level.isClientSide()) {
            return;
        }

        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
            if (hasSignal) {
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.8f, 1.3f);
                if (level instanceof ServerLevel serverLevel) {
                    Direction dir = state.getValue(FACING);
                    AABB strikeBox = new AABB(pos).expandTowards(dir.getStepX() * 1.5, dir.getStepY() * 1.5, dir.getStepZ() * 1.5);
                    List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, strikeBox);
                    for (LivingEntity target : targets) {
                        target.hurtServer(serverLevel, serverLevel.damageSources().generic(), 14.0f);
                        target.push(dir.getStepX() * 1.2, dir.getStepY() * 0.8 + 0.3, dir.getStepZ() * 1.2);
                    }
                }
            } else {
                level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.6f, 1.2f);
            }
        }
    }
}
