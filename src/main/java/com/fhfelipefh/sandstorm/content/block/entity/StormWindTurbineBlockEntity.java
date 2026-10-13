package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class StormWindTurbineBlockEntity extends BlockEntity {
    public static final long TURBINE_CAPACITY = 25000L;
    public static final long TURBINE_MAX_EXTRACT = 250L;
    public static final long BASE_GENERATION = 12L;

    private final EnergyStorageComponent energyStorage;
    private long currentGenerationRate = 0L;

    public StormWindTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.STORM_WIND_TURBINE_BE, pos, state);
        this.energyStorage = new EnergyStorageComponent(TURBINE_CAPACITY, 0L, TURBINE_MAX_EXTRACT);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public long getCurrentGenerationRate() {
        return currentGenerationRate;
    }

    public static long calculateGeneration(Level level, BlockPos pos) {
        if (!level.canSeeSky(pos.above())) {
            return 0L;
        }

        long rate = BASE_GENERATION;
        long altitudeBonus = Math.max(0, Math.min(18, (pos.getY() - 64) / 4));
        rate += altitudeBonus;

        if (level.isThundering()) {
            rate += 25L;
        }

        SandstormWeatherComponent weather = SandstormWeatherHandler.getWeather();
        if (weather != null && weather.isActive()) {
            long stormBonus = 40L + (long) (weather.getIntensity() * 50.0);
            rate += stormBonus;
        }

        return rate;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        long generated = calculateGeneration(level, pos);
        this.currentGenerationRate = generated;

        if (generated > 0) {
            this.energyStorage.receiveEnergy(generated);
            setChanged();
        }

        if (this.energyStorage.getStoredEnergy() > 0) {
            for (Direction direction : Direction.values()) {
                BlockEntity neighborBe = level.getBlockEntity(pos.relative(direction));
                if (neighborBe instanceof SolidStateAccumulatorBlockEntity accumulator) {
                    int space = accumulator.getMaxEnergy() - accumulator.getStoredEnergy();
                    if (space > 0) {
                        long transfer = Math.min(space, Math.min(this.energyStorage.getStoredEnergy(), TURBINE_MAX_EXTRACT));
                        this.energyStorage.extractEnergy(transfer);
                        accumulator.setStoredEnergy((int) (accumulator.getStoredEnergy() + transfer));
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("storedEnergy", this.energyStorage.getStoredEnergy());
        output.putLong("currentGenerationRate", this.currentGenerationRate);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energyStorage.setStoredEnergy(input.getLongOr("storedEnergy", 0L));
        this.currentGenerationRate = input.getLongOr("currentGenerationRate", 0L);
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
