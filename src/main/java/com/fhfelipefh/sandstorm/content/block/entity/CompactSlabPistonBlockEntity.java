package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CompactSlabPistonBlockEntity extends BlockEntity {
    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(2000, 300, 300);
    private float cachedWptCharge = -1.0f;

    public CompactSlabPistonBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.COMPACT_SLAB_PISTON_BE, pos, state);
    }

    public int getEnergy() {
        return (int) this.energyStorage.getStoredEnergy();
    }

    public int getMaxEnergy() {
        return (int) this.energyStorage.getCapacity();
    }

    public void setEnergy(int amount) {
        this.energyStorage.setStoredEnergy(amount);
        setChanged();
    }

    public boolean consumeEnergy(int amount) {
        if (this.energyStorage.getStoredEnergy() >= amount) {
            this.energyStorage.extractEnergy(amount);
            setChanged();
            return true;
        }
        return false;
    }

    public int receiveEnergy(int amount) {
        int accepted = (int) this.energyStorage.receiveEnergy(amount);
        if (accepted > 0) {
            setChanged();
        }
        return accepted;
    }

    public EnergyStorageComponent getEnergyStorage() {
        return this.energyStorage;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CompactSlabPistonBlockEntity be) {
        if (be.cachedWptCharge < 0.0f || (level.getGameTime() + pos.hashCode()) % 20 == 0) {
            be.cachedWptCharge = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
        }
        if (be.cachedWptCharge > 0.0f && be.getEnergy() < be.getMaxEnergy()) {
            int trickle = Math.min((int) (be.cachedWptCharge * 10.0f), be.getMaxEnergy() - be.getEnergy());
            if (trickle > 0) {
                be.receiveEnergy(trickle);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("Energy", this.energyStorage.getStoredEnergy());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        long energy = input.getLongOr("Energy", 0L);
        this.energyStorage.setStoredEnergy(energy);
    }
}
