package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VaultDoorDummyBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty COL = IntegerProperty.create("col", 0, 2);
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 2);

    public VaultDoorDummyBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(COL, 1)
                .setValue(ROW, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COL, ROW);
    }

    public static BlockPos getControllerPos(BlockState state, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        int colOffset = state.getValue(COL) - 1;
        int row = state.getValue(ROW);
        Direction reverseHorizontal = colOffset < 0 ? facing.getClockWise() : facing.getCounterClockWise();
        int dist = Math.abs(colOffset);
        return pos.relative(reverseHorizontal, dist).below(row);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockPos controllerPos = getControllerPos(state, pos);
        BlockState controllerState = level.getBlockState(controllerPos);
        if (controllerState.getBlock() instanceof VaultDoorControllerBlock) {
            boolean open = controllerState.getValue(VaultDoorControllerBlock.OPEN);
            if (open && state.getValue(COL) == 1) {
                return Shapes.empty();
            }
        }
        return Shapes.block();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos controllerPos = getControllerPos(state, pos);
        BlockState controllerState = level.getBlockState(controllerPos);
        if (controllerState.getBlock() instanceof VaultDoorControllerBlock) {
            return controllerState.useWithoutItem(level, player, hitResult.withPosition(controllerPos));
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide()) {
            BlockPos controllerPos = getControllerPos(state, pos);
            BlockState controllerState = level.getBlockState(controllerPos);
            if (controllerState.getBlock() instanceof VaultDoorControllerBlock doorBlock) {
                Direction facing = controllerState.getValue(VaultDoorControllerBlock.FACING);
                boolean powered = VaultDoorControllerBlock.isStructurePowered(level, controllerPos, facing);
                if (powered != controllerState.getValue(VaultDoorControllerBlock.POWERED)) {
                    level.setBlock(controllerPos, controllerState.setValue(VaultDoorControllerBlock.POWERED, powered).setValue(VaultDoorControllerBlock.OPEN, powered), 3);
                    doorBlock.playToggleSound(level, controllerPos, powered);
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos controllerPos = getControllerPos(state, pos);
        BlockState controllerState = level.getBlockState(controllerPos);
        if (controllerState.getBlock() instanceof VaultDoorControllerBlock) {
            level.destroyBlock(controllerPos, !player.isCreative());
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
