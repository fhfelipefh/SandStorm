package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MorphingFluidTransitionBlockEntity extends BlockEntity {

    private float progress = 0.0f;
    private boolean liquefying = true;
    private int lifeTicks = 0;

    public MorphingFluidTransitionBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.MORPHING_FLUID_TRANSITION_BE, pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MorphingFluidTransitionBlockEntity entity) {
        entity.lifeTicks++;
        if (entity.liquefying) {
            entity.progress = Math.min(1.0f, entity.progress + 0.05f);
        } else {
            entity.progress = Math.min(1.0f, entity.progress + 0.05f);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MorphingFluidTransitionBlockEntity entity) {
        entity.lifeTicks++;
        if (entity.lifeTicks >= 20) {
            if (entity.liquefying) {
                level.removeBlock(pos, false);
            } else {
                level.setBlock(pos, SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState(), 3);
            }
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        setChanged();
    }

    public boolean isLiquefying() {
        return this.liquefying;
    }

    public void setLiquefying(boolean liquefying) {
        this.liquefying = liquefying;
        setChanged();
    }

    public int getLifeTicks() {
        return this.lifeTicks;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putFloat("Progress", this.progress);
        output.putBoolean("Liquefying", this.liquefying);
        output.putInt("LifeTicks", this.lifeTicks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.progress = input.getFloatOr("Progress", 0.0f);
        this.liquefying = input.getBooleanOr("Liquefying", true);
        this.lifeTicks = input.getIntOr("LifeTicks", 0);
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
