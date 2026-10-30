package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.HeavyKineticPistonBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class HeavyKineticPistonBlockEntity extends BlockEntity {
    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(5000, 500, 500);
    private float cachedWptCharge = -1.0f;

    public HeavyKineticPistonBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.HEAVY_KINETIC_PISTON_BE, pos, state);
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, HeavyKineticPistonBlockEntity be) {
        if (be.cachedWptCharge < 0.0f || (level.getGameTime() + pos.hashCode()) % 20 == 0) {
            be.cachedWptCharge = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
        }
        if (be.cachedWptCharge > 0.0f && be.getEnergy() < be.getMaxEnergy()) {
            int toAdd = Math.round(be.cachedWptCharge * 20.0f);
            be.receiveEnergy(Math.max(1, toAdd));
        }
        if (be.getEnergy() < be.getMaxEnergy()) {
            for (Direction dir : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                if (neighbor instanceof SolidStateAccumulatorBlockEntity acc && acc.getStoredEnergy() > 0) {
                    int needed = be.getMaxEnergy() - be.getEnergy();
                    int drain = Math.min(needed, Math.min(100, acc.getStoredEnergy()));
                    acc.setStoredEnergy(acc.getStoredEnergy() - drain);
                    be.receiveEnergy(drain);
                    if (be.getEnergy() >= be.getMaxEnergy()) {
                        break;
                    }
                }
            }
        }
        if (!state.getValue(HeavyKineticPistonBlock.EXTENDED) && be.getEnergy() >= HeavyKineticPistonBlock.ENERGY_PER_PUSH) {
            if (state.getBlock() instanceof HeavyKineticPistonBlock pistonBlock) {
                if (level.getGameTime() % 4 == 0 && pistonBlock.hasSignal(level, pos, state.getValue(HeavyKineticPistonBlock.FACING))) {
                    pistonBlock.checkIfExtend(level, pos, state);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", (int) this.energyStorage.getStoredEnergy());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energyStorage.setStoredEnergy(input.getIntOr("storedEnergy", 0));
    }
}
