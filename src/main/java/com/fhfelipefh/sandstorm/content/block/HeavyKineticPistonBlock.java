package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.HeavyKineticPistonBlockEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeavyKineticPistonBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final int ENERGY_PER_PUSH = 100;
    public static final int TRIGGER_EXTEND = 0;
    public static final int TRIGGER_CONTRACT = 1;
    public static final int TRIGGER_DROP = 2;
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateAll(Block.boxZ(16.0, 4.0, 16.0));
    private final boolean isSticky;

    public HeavyKineticPistonBlock(boolean isSticky, Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENDED, false));
        this.isSticky = isSticky;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(EXTENDED)) {
            return SHAPES.get(state.getValue(FACING));
        }
        return Shapes.block();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeavyKineticPistonBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, SandStormBlocks.HEAVY_KINETIC_PISTON_BE, HeavyKineticPistonBlockEntity::serverTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide()) {
            this.checkIfExtend(level, pos, state);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide()) {
            this.checkIfExtend(level, pos, state);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide()) {
            this.checkIfExtend(level, pos, state);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(EXTENDED, false);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HeavyKineticPistonBlockEntity pistonBe) {
                player.sendOverlayMessage(Component.literal("§b⚡ Pistão Cinético: §f" + pistonBe.getEnergy() + " / " + pistonBe.getMaxEnergy() + " FE §7| §eEmpuxo: §f24 blocos"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.REDSTONE)) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof HeavyKineticPistonBlockEntity pistonBe) {
                    if (pistonBe.getEnergy() < pistonBe.getMaxEnergy()) {
                        pistonBe.receiveEnergy(400);
                        stack.shrink(1);
                        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 1.2f);
                        player.sendOverlayMessage(Component.literal("§a+400 FE §7(§b" + pistonBe.getEnergy() + " / " + pistonBe.getMaxEnergy() + " FE§7)"));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
        if (stack.is(Items.REDSTONE_BLOCK)) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof HeavyKineticPistonBlockEntity pistonBe) {
                    if (pistonBe.getEnergy() < pistonBe.getMaxEnergy()) {
                        pistonBe.receiveEnergy(3600);
                        stack.shrink(1);
                        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.6f, 1.5f);
                        player.sendOverlayMessage(Component.literal("§a+3600 FE §7(§b" + pistonBe.getEnergy() + " / " + pistonBe.getMaxEnergy() + " FE§7)"));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    public boolean hasSignal(Level level, BlockPos pos, Direction facing) {
        return this.getNeighborSignal(level, pos, facing);
    }

    public boolean getNeighborSignal(SignalGetter signalGetter, BlockPos pos, Direction facing) {
        for (Direction dir : Direction.values()) {
            if (dir != facing && signalGetter.hasSignal(pos.relative(dir), dir)) {
                return true;
            }
        }
        if (signalGetter.hasSignal(pos, Direction.DOWN)) {
            return true;
        }
        BlockPos above = pos.above();
        for (Direction dir : Direction.values()) {
            if (dir != Direction.DOWN && signalGetter.hasSignal(above.relative(dir), dir)) {
                return true;
            }
        }
        return false;
    }

    public void checkIfExtend(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        boolean hasSignal = this.getNeighborSignal(level, pos, facing);
        boolean isExtended = state.getValue(EXTENDED);
        if (hasSignal && !isExtended) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HeavyKineticPistonBlockEntity pistonBe) {
                if (pistonBe.getEnergy() >= ENERGY_PER_PUSH) {
                    pistonBe.consumeEnergy(ENERGY_PER_PUSH);
                }
            }
            if (new HeavyPistonStructureResolver(level, pos, facing, true).resolve()) {
                level.blockEvent(pos, this, TRIGGER_EXTEND, facing.get3DDataValue());
            }
        } else if (!hasSignal && isExtended) {
            level.blockEvent(pos, this, TRIGGER_CONTRACT, facing.get3DDataValue());
        }
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        Direction direction = Direction.from3DDataValue(param);
        if (!level.isClientSide()) {
            boolean signal = this.getNeighborSignal(level, pos, direction);
            if (signal && (id == TRIGGER_CONTRACT || id == TRIGGER_DROP)) {
                level.setBlock(pos, state.setValue(EXTENDED, true), 2);
                return false;
            }
            if (!signal && id == TRIGGER_EXTEND) {
                return false;
            }
        }
        if (id == TRIGGER_EXTEND) {
            if (!this.moveBlocks(level, pos, direction, true)) {
                return false;
            }
            level.setBlock(pos, state.setValue(EXTENDED, true), 67);
            level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.6f, 0.7f);
            level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(state));
        } else if (id == TRIGGER_CONTRACT || id == TRIGGER_DROP) {
            level.setBlock(pos, state.setValue(EXTENDED, false), 67);
            BlockPos headPos = pos.relative(direction);
            if (this.isSticky) {
                this.moveBlocks(level, pos, direction, false);
            } else {
                level.removeBlock(headPos, false);
            }
            level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.6f, 0.7f);
            level.gameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Context.of(state));
        }
        return true;
    }

    public static boolean isPushable(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing) {
        return isPushable(state, level, pos, moveDir, allowDestroy, pistonFacing, false);
    }

    public static boolean isPushable(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing, boolean canPushObsidian) {
        if (pos.getY() < level.getMinY() || pos.getY() > level.getMaxY() - 1 || !level.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }
        if (state.isAir()) {
            return true;
        }
        if (moveDir == Direction.DOWN && pos.getY() == level.getMinY()) {
            return false;
        }
        if (moveDir == Direction.UP && pos.getY() == level.getMaxY() - 1) {
            return false;
        }
        if (state.is(Blocks.BARRIER) || state.is(Blocks.BEDROCK) || state.is(Blocks.REINFORCED_DEEPSLATE)) {
            return false;
        }
        if (state.is(Blocks.OBSIDIAN) || state.is(Blocks.CRYING_OBSIDIAN) || state.is(Blocks.RESPAWN_ANCHOR)) {
            if (!canPushObsidian) {
                return false;
            }
        } else {
            if (state.hasProperty(BlockStateProperties.EXTENDED)) {
                if (state.getValue(BlockStateProperties.EXTENDED)) {
                    return false;
                }
            } else {
                if (state.getDestroySpeed(level, pos) == -1.0F) {
                    return false;
                }
                PushReaction reaction = state.getPistonPushReaction();
                if (reaction == PushReaction.IMMOVEABLE) {
                    return false;
                }
                if (reaction == PushReaction.POPPED) {
                    return allowDestroy;
                }
                if (reaction == PushReaction.PUSH) {
                    return moveDir == pistonFacing;
                }
            }
        }
        return !state.hasBlockEntity();
    }

    private boolean moveBlocks(Level level, BlockPos pos, Direction direction, boolean extending) {
        BlockPos headPos = pos.relative(direction);
        if (!extending && level.getBlockState(headPos).is(SandStormBlocks.HEAVY_KINETIC_PISTON_HEAD)) {
            level.setBlock(headPos, Blocks.AIR.defaultBlockState(), 276);
        }
        HeavyPistonStructureResolver resolver = new HeavyPistonStructureResolver(level, pos, direction, extending);
        if (!resolver.resolve()) {
            return false;
        }
        Map<BlockPos, BlockState> map = new HashMap<>();
        List<BlockPos> toPush = resolver.getToPush();
        List<BlockState> pushStates = new ArrayList<>();
        for (BlockPos pushPos : toPush) {
            BlockState pushState = level.getBlockState(pushPos);
            pushStates.add(pushState);
            map.put(pushPos, pushState);
        }
        List<BlockPos> toDestroy = resolver.getToDestroy();
        BlockState[] states = new BlockState[toPush.size() + toDestroy.size()];
        Direction moveDir = extending ? direction : direction.getOpposite();
        int stateIndex = 0;
        for (int j = toDestroy.size() - 1; j >= 0; j--) {
            BlockPos destroyPos = toDestroy.get(j);
            BlockState destroyState = level.getBlockState(destroyPos);
            BlockEntity be = level.getBlockEntity(destroyPos);
            dropResources(destroyState, level, destroyPos, be);
            level.setBlock(destroyPos, Blocks.AIR.defaultBlockState(), 18);
            level.gameEvent(GameEvent.BLOCK_DESTROY, destroyPos, GameEvent.Context.of(destroyState));
            states[stateIndex++] = destroyState;
        }
        for (int k = toPush.size() - 1; k >= 0; k--) {
            BlockPos currentPos = toPush.get(k);
            BlockPos targetPos = currentPos.relative(moveDir);
            map.remove(targetPos);
            BlockState movingPiston = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, direction);
            level.setBlock(targetPos, movingPiston, 324);
            level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(targetPos, movingPiston, pushStates.get(k), direction, extending, false));
            states[stateIndex++] = level.getBlockState(currentPos);
        }
        if (extending) {
            BlockState headBase = SandStormBlocks.HEAVY_KINETIC_PISTON_HEAD.defaultBlockState()
                    .setValue(HeavyKineticPistonHeadBlock.FACING, direction)
                    .setValue(HeavyKineticPistonHeadBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            BlockState movingPistonHead = Blocks.MOVING_PISTON.defaultBlockState()
                    .setValue(MovingPistonBlock.FACING, direction)
                    .setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(headPos);
            level.setBlock(headPos, movingPistonHead, 324);
            level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(headPos, movingPistonHead, headBase, direction, true, true));
        }
        BlockState airState = Blocks.AIR.defaultBlockState();
        for (BlockPos airPos : map.keySet()) {
            level.setBlock(airPos, airState, 82);
        }
        for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
            BlockPos entryPos = entry.getKey();
            BlockState entryState = entry.getValue();
            entryState.updateIndirectNeighbourShapes(level, entryPos, 2);
            airState.updateNeighbourShapes(level, entryPos, 2);
            airState.updateIndirectNeighbourShapes(level, entryPos, 2);
        }
        Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, resolver.getPushDirection(), null);
        stateIndex = 0;
        for (int m = toDestroy.size() - 1; m >= 0; m--) {
            BlockState destroyedState = states[stateIndex++];
            BlockPos dPos = toDestroy.get(m);
            if (level instanceof ServerLevel serverLevel) {
                destroyedState.affectNeighborsAfterRemoval(serverLevel, dPos, false);
            }
            destroyedState.updateIndirectNeighbourShapes(level, dPos, 2);
            level.updateNeighborsAt(dPos, destroyedState.getBlock(), orientation);
        }
        for (int n = toPush.size() - 1; n >= 0; n--) {
            BlockPos pPos = toPush.get(n);
            BlockState pState = states[stateIndex++];
            level.updateNeighborsAt(pPos, pState.getBlock(), orientation);
        }
        if (extending) {
            level.updateNeighborsAt(headPos, SandStormBlocks.HEAVY_KINETIC_PISTON_HEAD, orientation);
        }
        return true;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (state.getValue(EXTENDED)) {
            BlockPos headPos = pos.relative(state.getValue(FACING));
            BlockState headState = level.getBlockState(headPos);
            if (headState.is(SandStormBlocks.HEAVY_KINETIC_PISTON_HEAD)) {
                level.destroyBlock(headPos, false);
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(EXTENDED);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
