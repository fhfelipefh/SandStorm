package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.MorphingFluidTransitionBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
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
    private BlockState targetState = SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState();

    public MorphingFluidTransitionBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.MORPHING_FLUID_TRANSITION_BE, pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MorphingFluidTransitionBlockEntity entity) {
        entity.lifeTicks++;
        entity.progress = Math.min(1.0f, entity.progress + 0.05f);
        if (level.getRandom().nextFloat() < 0.35f) {
            double px = pos.getX() + 0.1 + level.getRandom().nextDouble() * 0.8;
            double py = pos.getY() + (entity.liquefying ? Math.max(0.1, 1.0 - entity.lifeTicks * 0.045) : Math.min(0.9, entity.lifeTicks * 0.045));
            double pz = pos.getZ() + 0.1 + level.getRandom().nextDouble() * 0.8;
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.04, 0.0);
            level.addParticle(ParticleTypes.DRIPPING_WATER, px, py, pz, 0.0, -0.05, 0.0);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MorphingFluidTransitionBlockEntity entity) {
        entity.lifeTicks++;
        if (entity.liquefying) {
            int stage = Math.min(4, entity.lifeTicks / 4);
            if (state.hasProperty(MorphingFluidTransitionBlock.STAGE) && state.getValue(MorphingFluidTransitionBlock.STAGE) != stage) {
                level.setBlock(pos, state.setValue(MorphingFluidTransitionBlock.STAGE, stage), 3);
            }
            if (entity.lifeTicks >= 20) {
                level.removeBlock(pos, false);
            }
        } else {
            int stage = Math.max(0, 4 - (entity.lifeTicks / 4));
            if (state.hasProperty(MorphingFluidTransitionBlock.STAGE) && state.getValue(MorphingFluidTransitionBlock.STAGE) != stage) {
                level.setBlock(pos, state.setValue(MorphingFluidTransitionBlock.STAGE, stage), 3);
            }
            if (entity.lifeTicks >= 20) {
                BlockState target = entity.targetState != null ? entity.targetState : SandStormBlocks.MORPHING_ALLOY_BLOCK.defaultBlockState();
                level.setBlock(pos, target, 3);
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

    public BlockState getTargetState() {
        return this.targetState;
    }

    public void setTargetState(BlockState targetState) {
        this.targetState = targetState;
        setChanged();
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
