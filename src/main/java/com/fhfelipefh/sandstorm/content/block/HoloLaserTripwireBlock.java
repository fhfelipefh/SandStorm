package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class HoloLaserTripwireBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final VoxelShape SHAPE_NORTH = Block.box(5, 4, 12, 11, 12, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(5, 4, 0, 11, 12, 4);
    private static final VoxelShape SHAPE_WEST = Block.box(12, 4, 5, 16, 12, 11);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 4, 5, 4, 12, 11);

    public HoloLaserTripwireBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return direction == state.getValue(FACING) ? getSignal(state, level, pos, direction) : 0;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!state.is(oldState.getBlock())) {
            level.scheduleTick(pos, this, 4);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        boolean detected = false;

        for (int i = 1; i <= 12; i++) {
            BlockPos checkPos = pos.relative(facing, i);
            if (level.getBlockState(checkPos).isSolidRender()) {
                break;
            }
            AABB beamBox = new AABB(checkPos).deflate(0.3);
            List<LivingEntity> crossed = level.getEntitiesOfClass(LivingEntity.class, beamBox);
            if (!crossed.isEmpty()) {
                detected = true;
                break;
            }
        }

        boolean wasPowered = state.getValue(POWERED);
        if (detected != wasPowered) {
            level.setBlock(pos, state.setValue(POWERED, detected), 3);
            level.playSound(null, pos, detected ? SoundEvents.TRIPWIRE_CLICK_ON : SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4f, 1.4f);
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.relative(facing.getOpposite()), this);
        }

        level.scheduleTick(pos, this, 4);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        double step = 0.5 + (random.nextDouble() * 3.5);
        double px = pos.getX() + 0.5 + (facing.getStepX() * step);
        double py = pos.getY() + 0.5;
        double pz = pos.getZ() + 0.5 + (facing.getStepZ() * step);
        level.addParticle(DustParticleOptions.REDSTONE, px, py, pz, 0.0, 0.0, 0.0);
    }
}
