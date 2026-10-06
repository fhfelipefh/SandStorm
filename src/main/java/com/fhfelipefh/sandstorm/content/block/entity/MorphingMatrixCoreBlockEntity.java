package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.MorphingAlloyBlock;
import com.fhfelipefh.sandstorm.content.block.MorphingAlloyDoorBlock;
import com.fhfelipefh.sandstorm.content.block.MorphingAlloyWindowBlock;
import com.fhfelipefh.sandstorm.content.block.MorphingFluidTransitionBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.MorphingMatrixCoreMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MorphingMatrixCoreBlockEntity extends BlockEntity implements MenuProvider, Container {

    public static final int RADIUS = 32;
    public static final int MAX_RESERVE = 2048;

    public enum MatrixState {
        IDLE_SOLID,
        SCANNING,
        LIQUEFYING,
        IDLE_LIQUID,
        SOLIDIFYING
    }

    private MatrixState state = MatrixState.IDLE_SOLID;
    private int reserveBlocks = 0;
    private boolean hologramActive = false;
    private final List<BlockPos> savedRelativePositions = new ArrayList<>();
    private final Map<BlockPos, BlockState> savedBlockStates = new HashMap<>();
    private int operationIndex = 0;
    private int tickCounter = 0;
    private int regenCursor = 0;
    private ItemStack inputSlot = ItemStack.EMPTY;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> state.ordinal();
                case 1 -> reserveBlocks & 0xFFFF;
                case 2 -> (reserveBlocks >> 16) & 0xFFFF;
                case 3 -> savedRelativePositions.size() & 0xFFFF;
                case 4 -> (savedRelativePositions.size() >> 16) & 0xFFFF;
                case 5 -> hologramActive ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 5) {
                hologramActive = (value == 1);
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public MorphingMatrixCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MorphingMatrixCoreBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.MORPHING_MATRIX_CORE_BE, pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MorphingMatrixCoreBlockEntity entity) {
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MorphingMatrixCoreBlockEntity entity) {
        entity.tickCounter++;

        if (!entity.inputSlot.isEmpty() && entity.reserveBlocks < MAX_RESERVE) {
            ItemStack stack = entity.inputSlot;
            boolean canFeed = stack.is(SandStormBlocks.MORPHING_ALLOY_BLOCK.asItem())
                    || stack.is(SandStormBlocks.MORPHING_ALLOY_DOOR.asItem())
                    || stack.is(SandStormBlocks.MORPHING_ALLOY_WINDOW.asItem())
                    || stack.is(SandStormItems.NANITE_GALLIUM_COMPOSITE)
                    || stack.is(SandStormItems.GALLIUM_INGOT);
            if (canFeed) {
                entity.reserveBlocks = Math.min(MAX_RESERVE, entity.reserveBlocks + 1);
                stack.shrink(1);
                entity.setChanged();
            }
        }

        if (entity.state == MatrixState.LIQUEFYING) {
            entity.processLiquefaction((ServerLevel) level);
        } else if (entity.state == MatrixState.SOLIDIFYING) {
            entity.processSolidification((ServerLevel) level);
        } else if (entity.state == MatrixState.IDLE_SOLID && entity.tickCounter % 20 == 0) {
            entity.processAutoRegeneration((ServerLevel) level);
        }
    }

    public static boolean isMorphingBlock(BlockState state) {
        return state.getBlock() instanceof MorphingAlloyBlock
                || state.getBlock() instanceof MorphingAlloyDoorBlock
                || state.getBlock() instanceof MorphingAlloyWindowBlock;
    }

    public int scanAndSaveStructure(ServerLevel serverLevel) {
        this.savedRelativePositions.clear();
        this.savedBlockStates.clear();
        BlockPos corePos = getBlockPos();
        BlockPos minPos = corePos.offset(-RADIUS, -RADIUS, -RADIUS);
        BlockPos maxPos = corePos.offset(RADIUS, RADIUS, RADIUS);

        for (BlockPos current : BlockPos.betweenClosed(minPos, maxPos)) {
            if (current.equals(corePos)) {
                continue;
            }
            BlockState blockState = serverLevel.getBlockState(current);
            if (isMorphingBlock(blockState)) {
                BlockPos relPos = current.subtract(corePos);
                this.savedRelativePositions.add(relPos);
                this.savedBlockStates.put(relPos, blockState);
            }
        }

        this.savedRelativePositions.sort(Comparator.comparingInt(BlockPos::getY));
        this.state = MatrixState.IDLE_SOLID;
        setChanged();
        serverLevel.sendBlockUpdated(corePos, getBlockState(), getBlockState(), 3);
        return this.savedRelativePositions.size();
    }

    public void saveBlueprint(Map<BlockPos, BlockState> structure, BlockPos corePos) {
        this.savedRelativePositions.clear();
        this.savedBlockStates.clear();
        for (Map.Entry<BlockPos, BlockState> entry : structure.entrySet()) {
            BlockPos relPos = entry.getKey().subtract(corePos);
            this.savedRelativePositions.add(relPos);
            this.savedBlockStates.put(relPos, entry.getValue());
        }
        this.savedRelativePositions.sort(Comparator.comparingInt(BlockPos::getY));
        setChanged();
    }

    public void startLiquefaction() {
        if (this.savedRelativePositions.isEmpty()) {
            return;
        }
        this.state = MatrixState.LIQUEFYING;
        this.operationIndex = this.savedRelativePositions.size() - 1;
        setChanged();
    }

    public void startSolidification() {
        if (this.savedRelativePositions.isEmpty()) {
            return;
        }
        this.state = MatrixState.SOLIDIFYING;
        this.operationIndex = 0;
        setChanged();
    }

    private void processLiquefaction(ServerLevel serverLevel) {
        int processedThisTick = 0;
        int checkedThisTick = 0;
        BlockPos corePos = getBlockPos();

        while (this.operationIndex >= 0 && processedThisTick < 16 && checkedThisTick < 128) {
            checkedThisTick++;
            BlockPos relPos = this.savedRelativePositions.get(this.operationIndex);
            BlockPos worldPos = corePos.offset(relPos);
            if (serverLevel.isLoaded(worldPos)) {
                BlockState current = serverLevel.getBlockState(worldPos);
                if (isMorphingBlock(current)) {
                    serverLevel.setBlock(worldPos, SandStormBlocks.MORPHING_FLUID_TRANSITION.defaultBlockState().setValue(MorphingFluidTransitionBlock.STAGE, 0), 3);
                    BlockEntity be = serverLevel.getBlockEntity(worldPos);
                    if (be instanceof MorphingFluidTransitionBlockEntity transitionBe) {
                        transitionBe.setLiquefying(true);
                    }
                    this.reserveBlocks = Math.min(MAX_RESERVE, this.reserveBlocks + 1);
                    processedThisTick++;
                }
            }
            this.operationIndex--;
        }

        if (this.operationIndex < 0) {
            this.state = MatrixState.IDLE_LIQUID;
            serverLevel.playSound(null, corePos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 0.8f);
        }
        setChanged();
    }

    private void processSolidification(ServerLevel serverLevel) {
        int processedThisTick = 0;
        int checkedThisTick = 0;
        BlockPos corePos = getBlockPos();

        while (this.operationIndex < this.savedRelativePositions.size() && processedThisTick < 16 && checkedThisTick < 128) {
            checkedThisTick++;
            BlockPos relPos = this.savedRelativePositions.get(this.operationIndex);
            BlockPos worldPos = corePos.offset(relPos);
            if (serverLevel.isLoaded(worldPos)) {
                BlockState current = serverLevel.getBlockState(worldPos);
                if (current.isAir() || current.canBeReplaced()) {
                    if (this.reserveBlocks > 0) {
                        this.reserveBlocks--;
                        BlockState target = this.savedBlockStates.getOrDefault(relPos, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState());
                        serverLevel.setBlock(worldPos, SandStormBlocks.MORPHING_FLUID_TRANSITION.defaultBlockState().setValue(MorphingFluidTransitionBlock.STAGE, 4), 3);
                        BlockEntity be = serverLevel.getBlockEntity(worldPos);
                        if (be instanceof MorphingFluidTransitionBlockEntity transitionBe) {
                            transitionBe.setLiquefying(false);
                            transitionBe.setTargetState(target);
                        }
                        processedThisTick++;
                    }
                }
            }
            this.operationIndex++;
        }

        if (this.operationIndex >= this.savedRelativePositions.size()) {
            this.state = MatrixState.IDLE_SOLID;
            serverLevel.playSound(null, corePos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
        setChanged();
    }

    private void processAutoRegeneration(ServerLevel serverLevel) {
        if (this.savedRelativePositions.isEmpty() || this.reserveBlocks <= 0) {
            return;
        }

        int total = this.savedRelativePositions.size();
        int checksToPerform = Math.min(32, total);
        BlockPos corePos = getBlockPos();

        for (int i = 0; i < checksToPerform; i++) {
            if (this.regenCursor >= total) {
                this.regenCursor = 0;
            }

            BlockPos relPos = this.savedRelativePositions.get(this.regenCursor);
            this.regenCursor++;

            BlockPos worldPos = corePos.offset(relPos);
            if (!serverLevel.isLoaded(worldPos)) {
                continue;
            }

            BlockState current = serverLevel.getBlockState(worldPos);
            if (current.isAir() || current.canBeReplaced()) {
                this.reserveBlocks--;
                BlockState target = this.savedBlockStates.getOrDefault(relPos, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState());
                serverLevel.setBlock(worldPos, SandStormBlocks.MORPHING_FLUID_TRANSITION.defaultBlockState().setValue(MorphingFluidTransitionBlock.STAGE, 4), 3);
                BlockEntity be = serverLevel.getBlockEntity(worldPos);
                if (be instanceof MorphingFluidTransitionBlockEntity transitionBe) {
                    transitionBe.setLiquefying(false);
                    transitionBe.setTargetState(target);
                }
                serverLevel.playSound(null, worldPos, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.BLOCKS, 0.5f, 1.5f);
                setChanged();
                break;
            }
        }
    }

    public void dropStoredContents(ServerLevel serverLevel, BlockPos pos) {
        int blocksToDrop = this.reserveBlocks;
        while (blocksToDrop > 0) {
            int stackCount = Math.min(64, blocksToDrop);
            ItemStack dropStack = new ItemStack(SandStormBlocks.MORPHING_ALLOY_BLOCK, stackCount);
            Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), dropStack);
            blocksToDrop -= stackCount;
        }
        if (!this.inputSlot.isEmpty()) {
            Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), this.inputSlot);
            this.inputSlot = ItemStack.EMPTY;
        }
        this.reserveBlocks = 0;
    }

    public MatrixState getState() {
        return this.state;
    }

    public void setState(MatrixState state) {
        this.state = state;
        setChanged();
    }

    public int getReserveBlocks() {
        return this.reserveBlocks;
    }

    public void setReserveBlocks(int reserveBlocks) {
        this.reserveBlocks = Math.min(MAX_RESERVE, Math.max(0, reserveBlocks));
        setChanged();
    }

    public boolean isHologramActive() {
        return this.hologramActive;
    }

    public void setHologramActive(boolean hologramActive) {
        this.hologramActive = hologramActive;
        setChanged();
    }

    public List<BlockPos> getSavedRelativePositions() {
        return this.savedRelativePositions;
    }

    public Map<BlockPos, BlockState> getSavedBlockStates() {
        return this.savedBlockStates;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.sandstorm.morphing_matrix_core.title");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new MorphingMatrixCoreMenu(syncId, playerInventory, this, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.inputSlot.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? this.inputSlot : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !this.inputSlot.isEmpty()) {
            ItemStack split = this.inputSlot.split(amount);
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack stack = this.inputSlot;
            this.inputSlot = ItemStack.EMPTY;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            this.inputSlot = stack;
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.inputSlot = ItemStack.EMPTY;
        setChanged();
    }

    private static int encodeState(BlockState state) {
        if (state.getBlock() instanceof MorphingAlloyWindowBlock) {
            return 1;
        }
        if (state.getBlock() instanceof MorphingAlloyDoorBlock) {
            int half = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? 1 : 0;
            int facing = state.getValue(DoorBlock.FACING).get2DDataValue();
            int hinge = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT ? 1 : 0;
            return 2 + (half << 3) + (hinge << 2) + facing;
        }
        return 0;
    }

    private static BlockState decodeState(int code) {
        if (code == 1) {
            return SandStormBlocks.MORPHING_ALLOY_WINDOW.defaultBlockState();
        }
        if (code >= 2) {
            int diff = code - 2;
            DoubleBlockHalf half = ((diff >> 3) & 1) == 1 ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER;
            DoorHingeSide hinge = ((diff >> 2) & 1) == 1 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
            Direction facing = Direction.from2DDataValue(diff & 3);
            return SandStormBlocks.MORPHING_ALLOY_DOOR.defaultBlockState()
                    .setValue(DoorBlock.HALF, half)
                    .setValue(DoorBlock.HINGE, hinge)
                    .setValue(DoorBlock.FACING, facing);
        }
        return SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("State", this.state.name());
        output.putInt("ReserveBlocks", this.reserveBlocks);
        output.putBoolean("HologramActive", this.hologramActive);
        output.putInt("OperationIndex", this.operationIndex);
        output.putInt("RegenCursor", this.regenCursor);

        StringBuilder sb = new StringBuilder(this.savedRelativePositions.size() * 22);
        for (int i = 0; i < this.savedRelativePositions.size(); i++) {
            if (i > 0) {
                sb.append(';');
            }
            BlockPos pos = this.savedRelativePositions.get(i);
            sb.append(pos.asLong());
            BlockState bState = this.savedBlockStates.get(pos);
            if (bState != null) {
                int code = encodeState(bState);
                if (code > 0) {
                    sb.append(':').append(code);
                }
            }
        }
        output.putString("SavedPositions", sb.toString());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String stateStr = input.getStringOr("State", "");
        if (!stateStr.isEmpty()) {
            try {
                this.state = MatrixState.valueOf(stateStr);
            } catch (IllegalArgumentException e) {
                this.state = MatrixState.IDLE_SOLID;
            }
        }
        this.reserveBlocks = input.getIntOr("ReserveBlocks", 0);
        this.hologramActive = input.getBooleanOr("HologramActive", false);
        this.operationIndex = input.getIntOr("OperationIndex", 0);
        this.regenCursor = input.getIntOr("RegenCursor", 0);

        this.savedRelativePositions.clear();
        this.savedBlockStates.clear();
        String savedPositionsStr = input.getStringOr("SavedPositions", "");
        if (!savedPositionsStr.isEmpty()) {
            int len = savedPositionsStr.length();
            int start = 0;
            for (int i = 0; i <= len; i++) {
                if (i == len || savedPositionsStr.charAt(i) == ';') {
                    if (i > start) {
                        String entry = savedPositionsStr.substring(start, i);
                        int colonIdx = entry.indexOf(':');
                        try {
                            if (colonIdx >= 0) {
                                long val = Long.parseLong(entry.substring(0, colonIdx));
                                int code = Integer.parseInt(entry.substring(colonIdx + 1));
                                BlockPos rel = BlockPos.of(val);
                                this.savedRelativePositions.add(rel);
                                this.savedBlockStates.put(rel, decodeState(code));
                            } else {
                                long val = Long.parseLong(entry);
                                BlockPos rel = BlockPos.of(val);
                                this.savedRelativePositions.add(rel);
                                this.savedBlockStates.put(rel, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState());
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    start = i + 1;
                }
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }
}
