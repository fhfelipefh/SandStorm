package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.component.WirelessChargerComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverBlock;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WirelessSolarReceiverBlockEntity extends BlockEntity {
    public static final long TIER1_CAPACITY = 50000L;
    public static final long TIER2_CAPACITY = 100000L;
    public static final long TIER1_MAX_RECEIVE = 50L;
    public static final long TIER2_MAX_RECEIVE = 100L;
    public static final long TIER1_MAX_EXTRACT = 500L;
    public static final long TIER2_MAX_EXTRACT = 1000L;

    private int tier;
    private WirelessChargerComponent charger;
    private EnergyStorageComponent energyStorage;

    public WirelessSolarReceiverBlockEntity(BlockPos pos, BlockState state, int tier) {
        this(SandStormBlocks.WIRELESS_SOLAR_RECEIVER_BE, pos, state, tier);
    }

    public WirelessSolarReceiverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = Math.max(1, tier);
        this.charger = new WirelessChargerComponent(this.tier);
        long capacity = this.tier >= 2 ? TIER2_CAPACITY : TIER1_CAPACITY;
        long maxReceive = this.tier >= 2 ? TIER2_MAX_RECEIVE : TIER1_MAX_RECEIVE;
        long maxExtract = this.tier >= 2 ? TIER2_MAX_EXTRACT : TIER1_MAX_EXTRACT;
        this.energyStorage = new EnergyStorageComponent(capacity, maxReceive, maxExtract);
    }

    public WirelessSolarReceiverBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, state.getBlock() instanceof WirelessSolarReceiverBlock receiver ? receiver.getTier() : 1);
    }

    public int getTier() {
        return tier;
    }

    public WirelessChargerComponent getCharger() {
        return charger;
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean canSeeSky = level.canSeeSky(pos.above());
        boolean isDay = level.getSkyDarken() < 4;
        int skyDarken = level.getSkyDarken();
        double weather = SandstormWeatherHandler.getWeather().getSolarEfficiencyMultiplier();

        long transferRate = charger.calculateTransferRate(canSeeSky, isDay, skyDarken, weather);
        if (transferRate > 0) {
            energyStorage.receiveEnergy(transferRate);
            setChanged();
        }

        WirelessSolarReceiverManager.registerReceiver(level.dimension(), pos, tier);
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            WirelessSolarReceiverManager.unregisterReceiver(level.dimension(), worldPosition);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("tier", this.tier);
        output.putLong("storedEnergy", this.energyStorage.getStoredEnergy());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.tier = input.getIntOr("tier", this.tier);
        this.charger = new WirelessChargerComponent(this.tier);
        long capacity = this.tier >= 2 ? TIER2_CAPACITY : TIER1_CAPACITY;
        long maxReceive = this.tier >= 2 ? TIER2_MAX_RECEIVE : TIER1_MAX_RECEIVE;
        long maxExtract = this.tier >= 2 ? TIER2_MAX_EXTRACT : TIER1_MAX_EXTRACT;
        this.energyStorage = new EnergyStorageComponent(capacity, maxReceive, maxExtract);
        this.energyStorage.setStoredEnergy(input.getLongOr("storedEnergy", 0L));
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
