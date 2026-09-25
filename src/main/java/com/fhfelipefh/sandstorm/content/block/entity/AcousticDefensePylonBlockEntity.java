package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.AcousticDefensePylonBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.defense.AcousticDefenseTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class AcousticDefensePylonBlockEntity extends BlockEntity {
    public static final int MAX_ENERGY = 50000;
    public static final int UPKEEP_COST = 10;
    public static final double DAMPING_RADIUS = 32.0;

    private int storedEnergy = MAX_ENERGY;
    private boolean active = false;

    public AcousticDefensePylonBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.ACOUSTIC_DEFENSE_PYLON_BE, pos, state);
    }

    public AcousticDefensePylonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        boolean previouslyActive = this.active;

        if (this.storedEnergy >= UPKEEP_COST) {
            this.storedEnergy -= UPKEEP_COST;
            this.active = true;
            AcousticDefenseTracker.registerPylon(level.dimension(), pos, DAMPING_RADIUS);

            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 20L == 0L) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 6, 0.5, 0.1, 0.5, 0.02);
            }
        } else {
            this.active = false;
            AcousticDefenseTracker.unregisterPylon(level.dimension(), pos);
        }

        if (previouslyActive != this.active) {
            if (state.hasProperty(AcousticDefensePylonBlock.ACTIVE) && state.getValue(AcousticDefensePylonBlock.ACTIVE) != this.active) {
                level.setBlock(pos, state.setValue(AcousticDefensePylonBlock.ACTIVE, this.active), 3);
            }
            setChanged();
        }
    }

    public void cleanup() {
        if (this.level != null && !this.level.isClientSide()) {
            AcousticDefenseTracker.unregisterPylon(this.level.dimension(), getBlockPos());
        }
    }

    @Override
    public void setRemoved() {
        cleanup();
        super.setRemoved();
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.max(0, Math.min(MAX_ENERGY, energy));
        setChanged();
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putBoolean("active", this.active);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.active = input.getBooleanOr("active", false);
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }
}
