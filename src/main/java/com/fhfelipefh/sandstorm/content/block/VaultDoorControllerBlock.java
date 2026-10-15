package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.VaultDoorControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

public class VaultDoorControllerBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public VaultDoorControllerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED);
    }

    public static BlockPos getRelativePos(BlockPos controllerPos, Direction facing, int col, int row) {
        int colOffset = col - 1;
        Direction horizontalOffsetDir = colOffset < 0 ? facing.getCounterClockWise() : facing.getClockWise();
        int dist = Math.abs(colOffset);
        return controllerPos.relative(horizontalOffsetDir, dist).above(row);
    }

    public static boolean isStructurePowered(Level level, BlockPos controllerPos, Direction facing) {
        if (level.hasNeighborSignal(controllerPos)) {
            return true;
        }
        for (int col = 0; col <= 2; col++) {
            for (int row = 0; row <= 2; row++) {
                if (col == 1 && row == 0) {
                    continue;
                }
                BlockPos dummyPos = getRelativePos(controllerPos, facing, col, row);
                if (level.hasNeighborSignal(dummyPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos basePos = context.getClickedPos();
        Level level = context.getLevel();
        for (int col = 0; col <= 2; col++) {
            for (int row = 0; row <= 2; row++) {
                if (col == 1 && row == 0) {
                    continue;
                }
                BlockPos targetPos = getRelativePos(basePos, facing, col, row);
                if (!level.getBlockState(targetPos).canBeReplaced(context) || !level.isInWorldBounds(targetPos)) {
                    return null;
                }
            }
        }
        boolean powered = isStructurePowered(level, basePos, facing);
        return defaultBlockState().setValue(FACING, facing).setValue(OPEN, powered).setValue(POWERED, powered);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING);
            for (int col = 0; col <= 2; col++) {
                for (int row = 0; row <= 2; row++) {
                    if (col == 1 && row == 0) {
                        continue;
                    }
                    BlockPos dummyPos = getRelativePos(pos, facing, col, row);
                    BlockState dummyState = SandStormBlocks.VAULT_DOOR_DUMMY.defaultBlockState()
                            .setValue(VaultDoorDummyBlock.FACING, facing)
                            .setValue(VaultDoorDummyBlock.COL, col)
                            .setValue(VaultDoorDummyBlock.ROW, row);
                    level.setBlock(dummyPos, dummyState, 3);
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        Direction facing = state.getValue(FACING);
        for (int col = 0; col <= 2; col++) {
            for (int row = 0; row <= 2; row++) {
                if (col == 1 && row == 0) {
                    continue;
                }
                BlockPos dummyPos = getRelativePos(pos, facing, col, row);
                BlockState dummyState = level.getBlockState(dummyPos);
                if (dummyState.is(SandStormBlocks.VAULT_DOOR_DUMMY)) {
                    level.destroyBlock(dummyPos, false);
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            boolean open = !state.getValue(OPEN);
            level.setBlock(pos, state.setValue(OPEN, open), 3);
            playToggleSound(level, pos, open);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING);
            boolean powered = isStructurePowered(level, pos, facing);
            if (powered != state.getValue(POWERED)) {
                boolean open = powered;
                level.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, open), 3);
                playToggleSound(level, pos, open);
            }
        }
    }

    public void playToggleSound(Level level, BlockPos pos, boolean open) {
        float pitch = open ? 0.85f : 0.65f;
        level.playSound(null, pos, SoundEvents.VAULT_OPEN_SHUTTER, SoundSource.BLOCKS, 1.5f, pitch);
        level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, pitch);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? Shapes.empty() : Shapes.block();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VaultDoorControllerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, blockState, blockEntity) -> {
            if (blockEntity instanceof VaultDoorControllerBlockEntity door) {
                VaultDoorControllerBlockEntity.tick(lvl, pos, blockState, door);
            }
        };
    }
}
