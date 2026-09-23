package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.WirelessChargerComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorManager;
import com.fhfelipefh.sandstorm.content.block.ThermalGeneratorManager;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.block.WptRelayTowerManager;
import com.fhfelipefh.sandstorm.content.gui.GridMonitorConsoleMenu;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Map;

public class GridMonitorConsoleBlockEntity extends BlockEntity implements MenuProvider {
    private static final WirelessChargerComponent CHARGER_TIER1 = new WirelessChargerComponent(1);
    private static final WirelessChargerComponent CHARGER_TIER2 = new WirelessChargerComponent(2);

    private int solarCount = 0;
    private int solarGenRate = 0;
    private int thermalCount = 0;
    private int thermalGenRate = 0;
    private int relayCount = 0;
    private int accumulatorCount = 0;
    private int totalStoredEnergy = 0;
    private int totalCapacity = 0;
    private int gridStatus = 0;
    private int localCoverageCharge = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> solarCount;
                case 1 -> solarGenRate;
                case 2 -> thermalCount;
                case 3 -> thermalGenRate;
                case 4 -> relayCount;
                case 5 -> accumulatorCount;
                case 6 -> totalStoredEnergy & 0xFFFF;
                case 7 -> (totalStoredEnergy >> 16) & 0xFFFF;
                case 8 -> totalCapacity & 0xFFFF;
                case 9 -> (totalCapacity >> 16) & 0xFFFF;
                case 10 -> gridStatus;
                case 11 -> localCoverageCharge;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> solarCount = value;
                case 1 -> solarGenRate = value;
                case 2 -> thermalCount = value;
                case 3 -> thermalGenRate = value;
                case 4 -> relayCount = value;
                case 5 -> accumulatorCount = value;
                case 6 -> totalStoredEnergy = (totalStoredEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 7 -> totalStoredEnergy = (totalStoredEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 8 -> totalCapacity = (totalCapacity & ~0xFFFF) | (value & 0xFFFF);
                case 9 -> totalCapacity = (totalCapacity & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 10 -> gridStatus = value;
                case 11 -> localCoverageCharge = value;
            }
        }

        @Override
        public int getCount() {
            return 12;
        }
    };

    public GridMonitorConsoleBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.GRID_MONITOR_CONSOLE_BE, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GridMonitorConsoleBlockEntity entity) {
        if (level.getGameTime() % 20L != 0L) {
            return;
        }

        Map<BlockPos, Integer> solarMap = WirelessSolarReceiverManager.getReceivers(level.dimension());
        entity.solarCount = solarMap.size();

        double weather = SandstormWeatherHandler.getWeather().getSolarEfficiencyMultiplier();
        boolean isDay = level.getSkyDarken() < 4;
        int skyDarken = level.getSkyDarken();

        int totalSolar = 0;
        for (Map.Entry<BlockPos, Integer> entry : solarMap.entrySet()) {
            BlockPos rPos = entry.getKey();
            int tier = entry.getValue();
            boolean canSeeSky = !level.isLoaded(rPos) || level.canSeeSky(rPos.above());
            WirelessChargerComponent charger = tier >= 2 ? CHARGER_TIER2 : CHARGER_TIER1;
            totalSolar += (int) charger.calculateTransferRate(canSeeSky, isDay, skyDarken, weather);
        }
        entity.solarGenRate = totalSolar;

        Map<BlockPos, Integer> thermalMap = ThermalGeneratorManager.getGenerators(level.dimension());
        int activeThermal = 0;
        for (int burnTime : thermalMap.values()) {
            if (burnTime > 0) {
                activeThermal++;
            }
        }
        entity.thermalCount = activeThermal;
        entity.thermalGenRate = (int) (activeThermal * ThermalGeneratorManager.TRANSFER_RATE_PER_TICK);

        entity.relayCount = WptRelayTowerManager.getTowers(level.dimension()).size();
        entity.accumulatorCount = SolidStateAccumulatorManager.getAccumulators(level.dimension()).size();
        entity.totalStoredEnergy = (int) Math.min(Integer.MAX_VALUE, SolidStateAccumulatorManager.getTotalStoredEnergy(level.dimension()));
        entity.totalCapacity = (int) Math.min(Integer.MAX_VALUE, SolidStateAccumulatorManager.getTotalCapacity(level.dimension()));

        entity.localCoverageCharge = (int) WirelessSolarReceiverManager.getWptChargeAt(level, pos);

        int totalGen = entity.solarGenRate + entity.thermalGenRate;
        int dischargingAccCount = SolidStateAccumulatorManager.getDischargingAccumulatorCount(level.dimension());

        if (totalGen == 0 && (entity.totalStoredEnergy == 0 || dischargingAccCount == 0)) {
            entity.gridStatus = 3;
        } else if (totalGen == 0 && dischargingAccCount > 0) {
            entity.gridStatus = 2;
        } else if (totalGen > 0 && entity.totalStoredEnergy >= entity.totalCapacity && entity.totalCapacity > 0) {
            entity.gridStatus = 1;
        } else {
            entity.gridStatus = 0;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("solarCount", this.solarCount);
        output.putInt("solarGenRate", this.solarGenRate);
        output.putInt("thermalCount", this.thermalCount);
        output.putInt("thermalGenRate", this.thermalGenRate);
        output.putInt("relayCount", this.relayCount);
        output.putInt("accumulatorCount", this.accumulatorCount);
        output.putInt("totalStoredEnergy", this.totalStoredEnergy);
        output.putInt("totalCapacity", this.totalCapacity);
        output.putInt("gridStatus", this.gridStatus);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.solarCount = input.getIntOr("solarCount", 0);
        this.solarGenRate = input.getIntOr("solarGenRate", 0);
        this.thermalCount = input.getIntOr("thermalCount", 0);
        this.thermalGenRate = input.getIntOr("thermalGenRate", 0);
        this.relayCount = input.getIntOr("relayCount", 0);
        this.accumulatorCount = input.getIntOr("accumulatorCount", 0);
        this.totalStoredEnergy = input.getIntOr("totalStoredEnergy", 0);
        this.totalCapacity = input.getIntOr("totalCapacity", 0);
        this.gridStatus = input.getIntOr("gridStatus", 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.grid_monitor_console");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new GridMonitorConsoleMenu(syncId, playerInventory, this.dataAccess);
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }
}
