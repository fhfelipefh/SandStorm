package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
import com.fhfelipefh.sandstorm.content.clone.CloneNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class QuantumSleeperPodBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private static final VoxelShape UPPER_ROOF = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);

    private static final VoxelShape REAR_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    private static final VoxelShape REAR_SOUTH = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    private static final VoxelShape REAR_WEST = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    private static final VoxelShape REAR_EAST = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    private static final VoxelShape SIDE_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    private static final VoxelShape SIDE_SOUTH = Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SIDE_WEST = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);
    private static final VoxelShape SIDE_EAST = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    private static final VoxelShape SIDES_X = Shapes.or(SIDE_WEST, SIDE_EAST);
    private static final VoxelShape SIDES_Z = Shapes.or(SIDE_NORTH, SIDE_SOUTH);

    public QuantumSleeperPodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(ACTIVE, true)
                .setValue(OCCUPIED, false)
                .setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, ACTIVE, OCCUPIED, OPEN);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxY() && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(ACTIVE, true)
                    .setValue(OCCUPIED, false)
                    .setValue(OPEN, false);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y) {
            if (half == DoubleBlockHalf.LOWER && direction == Direction.UP) {
                if (!neighborState.is(this) || neighborState.getValue(HALF) != DoubleBlockHalf.UPPER) {
                    return Blocks.AIR.defaultBlockState();
                }
            } else if (half == DoubleBlockHalf.UPPER && direction == Direction.DOWN) {
                if (!neighborState.is(this) || neighborState.getValue(HALF) != DoubleBlockHalf.LOWER) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(HALF) != half) {
                level.destroyBlock(otherPos, !player.isCreative());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof QuantumSleeperPodBlockEntity podBe) {
                Containers.dropContents(level, pos, podBe);
                if (level.getServer() != null) {
                    CloneNetworkSavedData.get(level.getServer()).unregisterPod(pos, level.dimension().identifier().toString());
                }
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(OPEN)) {
            return Shapes.block();
        }
        Direction facing = state.getValue(FACING);
        DoubleBlockHalf half = state.getValue(HALF);
        Direction rearDir = facing.getOpposite();
        VoxelShape rear = switch (rearDir) {
            case NORTH -> REAR_NORTH;
            case SOUTH -> REAR_SOUTH;
            case WEST -> REAR_WEST;
            case EAST -> REAR_EAST;
            default -> REAR_NORTH;
        };
        VoxelShape sides = (facing == Direction.NORTH || facing == Direction.SOUTH) ? SIDES_X : SIDES_Z;
        VoxelShape walls = Shapes.or(rear, sides);
        if (half == DoubleBlockHalf.UPPER) {
            return Shapes.or(UPPER_ROOF, walls);
        }
        return walls;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new QuantumSleeperPodBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof QuantumSleeperPodBlockEntity podBe) {
                QuantumSleeperPodBlockEntity.serverTick(lvl, pos, st, podBe);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos basePos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockState baseState = level.getBlockState(basePos);
        if (!baseState.is(this)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(basePos);
        if (!(be instanceof QuantumSleeperPodBlockEntity podBe)) {
            return InteractionResult.PASS;
        }

        if (podBe.getOwnerUuid() == null) {
            podBe.setOwnerUuid(player.getUUID());
        }

        if (player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(podBe);
            }
            return InteractionResult.CONSUME;
        }

        boolean newOpen = !baseState.getValue(OPEN);
        level.setBlock(basePos, baseState.setValue(OPEN, newOpen), 3);
        BlockPos upperPos = basePos.above();
        BlockState upperState = level.getBlockState(upperPos);
        if (upperState.is(this)) {
            level.setBlock(upperPos, upperState.setValue(OPEN, newOpen), 3);
        }

        float soundPitch = newOpen ? 1.2f : 0.9f;
        level.playSound(null, basePos, newOpen ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0f, soundPitch);
        level.playSound(null, basePos, newOpen ? SoundEvents.PISTON_EXTEND : SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.6f, soundPitch + 0.3f);

        if (newOpen && baseState.getValue(OCCUPIED) && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, basePos.getX() + 0.5, basePos.getY() + 1.0, basePos.getZ() + 0.5, 12, 0.25, 0.5, 0.25, 0.02);
        }

        return InteractionResult.SUCCESS;
    }
}
