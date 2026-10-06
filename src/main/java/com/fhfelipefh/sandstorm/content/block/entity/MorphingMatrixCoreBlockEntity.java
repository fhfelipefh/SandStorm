package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.MorphingAlloyBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MorphingMatrixCoreBlockEntity extends BlockEntity {

    public static final int RADIUS = 32;
    public static final int MAX_RESERVE = 262144;

    public enum MatrixState {
        IDLE_SOLID,
        IDLE_LIQUID,
        LIQUEFYING,
        SOLIDIFYING,
        REGENERATING
    }

    private MatrixState state = MatrixState.IDLE_SOLID;
    private int reserveBlocks = 0;
    private boolean hologramActive = false;
    private final List<BlockPos> savedRelativePositions = new ArrayList<>();
    private int operationIndex = 0;
    private int tickCounter = 0;

    public MorphingMatrixCoreBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.MORPHING_MATRIX_CORE_BE, pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MorphingMatrixCoreBlockEntity entity) {
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MorphingMatrixCoreBlockEntity entity) {
        entity.tickCounter++;
        if (entity.state == MatrixState.LIQUEFYING) {
            entity.processLiquefaction((ServerLevel) level);
        } else if (entity.state == MatrixState.SOLIDIFYING) {
            entity.processSolidification((ServerLevel) level);
        } else if (entity.state == MatrixState.IDLE_SOLID && entity.tickCounter % 20 == 0) {
            entity.processAutoRegeneration((ServerLevel) level);
        }
    }

    public int scanAndSaveStructure(ServerLevel serverLevel) {
        this.savedRelativePositions.clear();
        BlockPos corePos = getBlockPos();
        BlockPos minPos = corePos.offset(-RADIUS, -RADIUS, -RADIUS);
        BlockPos maxPos = corePos.offset(RADIUS, RADIUS, RADIUS);

        for (BlockPos current : BlockPos.betweenClosed(minPos, maxPos)) {
            if (current.equals(corePos)) {
                continue;
            }
            BlockState blockState = serverLevel.getBlockState(current);
            if (blockState.getBlock() instanceof MorphingAlloyBlock) {
                this.savedRelativePositions.add(current.subtract(corePos));
            }
        }

        this.savedRelativePositions.sort(Comparator.comparingInt(BlockPos::getY));
        this.state = MatrixState.IDLE_SOLID;
        setChanged();
        serverLevel.sendBlockUpdated(corePos, getBlockState(), getBlockState(), 3);
        return this.savedRelativePositions.size();
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
        BlockPos corePos = getBlockPos();

        while (this.operationIndex >= 0 && processedThisTick < 16) {
            BlockPos relPos = this.savedRelativePositions.get(this.operationIndex);
            BlockPos worldPos = corePos.offset(relPos);
            BlockState current = serverLevel.getBlockState(worldPos);

            if (current.getBlock() instanceof MorphingAlloyBlock) {
                serverLevel.setBlock(worldPos, SandStormBlocks.MORPHING_FLUID_TRANSITION.defaultBlockState(), 3);
                BlockEntity be = serverLevel.getBlockEntity(worldPos);
                if (be instanceof MorphingFluidTransitionBlockEntity transitionBe) {
                    transitionBe.setLiquefying(true);
                }
                this.reserveBlocks = Math.min(MAX_RESERVE, this.reserveBlocks + 1);
                processedThisTick++;
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
        BlockPos corePos = getBlockPos();

        while (this.operationIndex < this.savedRelativePositions.size() && processedThisTick < 16) {
            BlockPos relPos = this.savedRelativePositions.get(this.operationIndex);
            BlockPos worldPos = corePos.offset(relPos);
            BlockState current = serverLevel.getBlockState(worldPos);

            if (current.isAir() || current.canBeReplaced()) {
                if (this.reserveBlocks > 0) {
                    this.reserveBlocks--;
                    serverLevel.setBlock(worldPos, SandStormBlocks.MORPHING_FLUID_TRANSITION.defaultBlockState(), 3);
                    BlockEntity be = serverLevel.getBlockEntity(worldPos);
                    if (be instanceof MorphingFluidTransitionBlockEntity transitionBe) {
                        transitionBe.setLiquefying(false);
                    }
                    processedThisTick++;
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
        if (this.reserveBlocks <= 0 || this.savedRelativePositions.isEmpty()) {
            return;
        }

        BlockPos corePos = getBlockPos();
        for (BlockPos relPos : this.savedRelativePositions) {
            BlockPos worldPos = corePos.offset(relPos);
            BlockState current = serverLevel.getBlockState(worldPos);

            if (current.isAir() || current.canBeReplaced()) {
                this.reserveBlocks--;
                serverLevel.setBlock(worldPos, SandStormBlocks.MORPHING_FLUID_TRANSITION.defaultBlockState(), 3);
                BlockEntity be = serverLevel.getBlockEntity(worldPos);
                if (be instanceof MorphingFluidTransitionBlockEntity transitionBe) {
                    transitionBe.setLiquefying(false);
                }
                serverLevel.playSound(null, worldPos, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.BLOCKS, 0.8f, 1.4f);
                setChanged();
                break;
            }
        }
    }

    public void dropStoredContents(ServerLevel serverLevel, BlockPos pos) {
        int remaining = this.reserveBlocks;
        while (remaining > 0) {
            int stackCount = Math.min(64, remaining);
            ItemStack dropStack = new ItemStack(SandStormBlocks.MORPHING_ALLOY_BLOCK, stackCount);
            Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), dropStack);
            remaining -= stackCount;
        }
        this.reserveBlocks = 0;
        setChanged();
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
        this.reserveBlocks = Math.max(0, Math.min(MAX_RESERVE, reserveBlocks));
        setChanged();
    }

    public void addReserveBlocks(int amount) {
        this.reserveBlocks = Math.max(0, Math.min(MAX_RESERVE, this.reserveBlocks + amount));
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

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("State", this.state.name());
        output.putInt("ReserveBlocks", this.reserveBlocks);
        output.putBoolean("HologramActive", this.hologramActive);
        output.putInt("OperationIndex", this.operationIndex);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.savedRelativePositions.size(); i++) {
            if (i > 0) {
                sb.append(';');
            }
            sb.append(this.savedRelativePositions.get(i).asLong());
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

        this.savedRelativePositions.clear();
        String savedPositionsStr = input.getStringOr("SavedPositions", "");
        if (!savedPositionsStr.isEmpty()) {
            String[] tokens = savedPositionsStr.split(";");
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    try {
                        this.savedRelativePositions.add(BlockPos.of(Long.parseLong(token)));
                    } catch (NumberFormatException ignored) {
                    }
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
