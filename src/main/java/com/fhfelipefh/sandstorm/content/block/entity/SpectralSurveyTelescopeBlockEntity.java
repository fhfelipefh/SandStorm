package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SpectralSurveyTelescopeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SpectralSurveyTelescopeBlockEntity extends BlockEntity {
    public static final int MAX_ENERGY = 50000;
    private int storedEnergy = MAX_ENERGY;

    public SpectralSurveyTelescopeBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.SPECTRAL_SURVEY_TELESCOPE_BE, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (storedEnergy < MAX_ENERGY) {
            storedEnergy = Math.min(MAX_ENERGY, storedEnergy + 50);
            setChanged();
        }

        if (level.getGameTime() % 40 == 0 && level instanceof ServerLevel serverLevel) {
            boolean hasSky = level.canSeeSky(pos.above());
            if (hasSky && state.getValue(SpectralSurveyTelescopeBlock.ACTIVE)) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                        2, 0.1, 0.2, 0.1, 0.02);
            }

            if (state.getValue(SpectralSurveyTelescopeBlock.ACTIVE) != hasSky) {
                level.setBlock(pos, state.setValue(SpectralSurveyTelescopeBlock.ACTIVE, hasSky), 3);
            }
        }
    }

    public int getStoredEnergy() {
        return storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.clamp(energy, 0, MAX_ENERGY);
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", this.storedEnergy);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
    }
}
