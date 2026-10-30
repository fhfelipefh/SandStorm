package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.CompactSlabPistonBlockEntity;
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
import net.minecraft.world.item.BlockItem;
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
import net.minecraft.world.level.block.SlabBlock;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CompactSlabPistonBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final BooleanProperty TOP_SLAB = BooleanProperty.create("top_slab");
    public static final int ENERGY_PER_PUSH = 35;
    public static final int MAX_PUSH_DEPTH = 7;
    private static final VoxelShape BASE_SHAPE_HALF = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private static final VoxelShape BASE_SHAPE_FULL = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape TOP_SLAB_SHAPE = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, VoxelShape> EXTENDED_SHAPES_HALF = Shapes.rotateAll(Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 4.0));
    private final boolean isSticky;

    public CompactSlabPistonBlock(boolean isSticky, Properties properties) {
        super(properties);
        this.isSticky = isSticky;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENDED, false).setValue(TOP_SLAB, false));
    }

    public boolean isSticky() {
        return this.isSticky;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CompactSlabPistonBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, SandStormBlocks.COMPACT_SLAB_PISTON_BE, CompactSlabPistonBlockEntity::serverTick);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean hasTopSlab = state.getValue(TOP_SLAB);
        if (state.getValue(EXTENDED)) {
            VoxelShape base = EXTENDED_SHAPES_HALF.get(state.getValue(FACING));
            return hasTopSlab ? Shapes.or(base, TOP_SLAB_SHAPE) : base;
        }
        return hasTopSlab ? BASE_SHAPE_FULL : BASE_SHAPE_HALF;
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
        if (!oldState.is(state.getBlock()) && !level.isClientSide() && level.getBlockEntity(pos) == null) {
            this.checkIfExtend(level, pos, state);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing).setValue(EXTENDED, false).setValue(TOP_SLAB, false);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown() && state.getValue(TOP_SLAB)) {
                level.setBlock(pos, state.setValue(TOP_SLAB, false), 3);
                popResource(level, pos, new ItemStack(Items.SMOOTH_STONE_SLAB));
                level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CompactSlabPistonBlockEntity pistonBe) {
                String slabStatus = state.getValue(TOP_SLAB) ? "§aAcoplada" : "§7Livre";
                player.sendOverlayMessage(Component.literal("§e⚡ Pistão Meia-Laje: §f" + pistonBe.getEnergy() + " / " + pistonBe.getMaxEnergy() + " FE §7| §bEmpuxo: §f7 blocos §7| §6Laje: " + slabStatus));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(TOP_SLAB) && (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SlabBlock)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(TOP_SLAB, true), 3);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                player.sendOverlayMessage(Component.literal("§aLaje acoplada com sucesso sobre o pistão!"));
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(Items.REDSTONE)) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof CompactSlabPistonBlockEntity pistonBe) {
                    if (pistonBe.getEnergy() < pistonBe.getMaxEnergy()) {
                        pistonBe.receiveEnergy(200);
                        stack.shrink(1);
                        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 1.2f);
                        player.sendOverlayMessage(Component.literal("§a+200 FE §7(§e" + pistonBe.getEnergy() + " / " + pistonBe.getMaxEnergy() + " FE§7)"));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private void checkIfExtend(Level level, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        boolean hasSignal = this.getNeighborSignal(level, pos, direction);
        boolean isExtended = state.getValue(EXTENDED);
        if (hasSignal && !isExtended) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CompactSlabPistonBlockEntity pistonBe) {
                if (pistonBe.getEnergy() >= ENERGY_PER_PUSH) {
                    pistonBe.consumeEnergy(ENERGY_PER_PUSH);
                }
            }
            if (new HeavyPistonStructureResolver(level, pos, direction, true, MAX_PUSH_DEPTH, false).resolve()) {
                level.blockEvent(pos, this, 0, direction.get3DDataValue());
            }
        } else if (!hasSignal && isExtended) {
            level.blockEvent(pos, this, 1, direction.get3DDataValue());
        }
    }

    private boolean getNeighborSignal(SignalGetter level, BlockPos pos, Direction direction) {
        for (Direction dir : Direction.values()) {
            if (dir != direction && level.hasSignal(pos.relative(dir), dir)) {
                return true;
            }
        }
        if (level.hasSignal(pos, Direction.DOWN)) {
            return true;
        }
        BlockPos upPos = pos.above();
        for (Direction dir : Direction.values()) {
            if (dir != Direction.DOWN && level.hasSignal(upPos.relative(dir), dir)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        Direction direction = state.getValue(FACING);
        if (!level.isClientSide()) {
            boolean hasSignal = this.getNeighborSignal(level, pos, direction);
            if (hasSignal && (id == 1)) {
                level.setBlock(pos, state.setValue(EXTENDED, true), 2);
                return false;
            }
            if (!hasSignal && (id == 0)) {
                return false;
            }
        }
        if (id == 0) {
            if (!this.moveBlocks(level, pos, direction, true)) {
                return false;
            }
            level.setBlock(pos, state.setValue(EXTENDED, true), 67);
            level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, 1.2f);
            level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(state));
        } else if (id == 1) {
            BlockPos headPos = pos.relative(direction);
            BlockState checkState = level.getBlockState(headPos);
            if (checkState.is(SandStormBlocks.COMPACT_SLAB_PISTON_HEAD)) {
                level.setBlock(headPos, Blocks.AIR.defaultBlockState(), 18);
            }
            if (this.isSticky) {
                BlockPos pullPos = pos.relative(direction, 2);
                BlockState pullState = level.getBlockState(pullPos);
                if (!pullState.isAir() && HeavyKineticPistonBlock.isPushable(pullState, level, pullPos, direction.getOpposite(), false, direction, false)) {
                    this.moveBlocks(level, pos, direction, false);
                }
            } else {
                level.removeBlock(headPos, false);
            }
            level.setBlock(pos, state.setValue(EXTENDED, false), 67);
            level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5f, 1.2f);
            level.gameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Context.of(state));
        }
        return true;
    }

    private boolean moveBlocks(Level level, BlockPos pos, Direction direction, boolean extending) {
        BlockPos headPos = pos.relative(direction);
        if (!extending && level.getBlockState(headPos).is(SandStormBlocks.COMPACT_SLAB_PISTON_HEAD)) {
            level.setBlock(headPos, Blocks.AIR.defaultBlockState(), 276);
        }
        HeavyPistonStructureResolver resolver = new HeavyPistonStructureResolver(level, pos, direction, extending, MAX_PUSH_DEPTH, false);
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
            BlockState headBase = SandStormBlocks.COMPACT_SLAB_PISTON_HEAD.defaultBlockState()
                    .setValue(CompactSlabPistonHeadBlock.FACING, direction)
                    .setValue(CompactSlabPistonHeadBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
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
            level.updateNeighborsAt(headPos, SandStormBlocks.COMPACT_SLAB_PISTON_HEAD, orientation);
        }
        return true;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (state.getValue(EXTENDED)) {
            BlockPos headPos = pos.relative(state.getValue(FACING));
            BlockState headState = level.getBlockState(headPos);
            if (headState.is(SandStormBlocks.COMPACT_SLAB_PISTON_HEAD) && headState.getValue(FACING) == state.getValue(FACING)) {
                level.destroyBlock(headPos, false);
            }
        }
        if (state.getValue(TOP_SLAB)) {
            popResource(level, pos, new ItemStack(Items.SMOOTH_STONE_SLAB));
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
        builder.add(FACING, EXTENDED, TOP_SLAB);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
