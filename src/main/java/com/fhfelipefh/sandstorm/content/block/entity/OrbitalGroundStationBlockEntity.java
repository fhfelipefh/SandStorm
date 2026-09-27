package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.OrbitalGroundStationBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.OrbitalGroundStationMenu;
import com.fhfelipefh.sandstorm.content.satellite.SatelliteNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class OrbitalGroundStationBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int MAX_ENERGY = 100000;
    private static final int[] SLOTS = new int[]{0};

    private int storedEnergy = MAX_ENERGY;
    private int satelliteCount = 0;
    private boolean weatherActive = false;
    private boolean solarActive = false;
    private boolean sarActive = false;
    private boolean lanceActive = false;
    private int secondsToStorm = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy;
                case 1 -> MAX_ENERGY;
                case 2 -> satelliteCount;
                case 3 -> weatherActive ? 1 : 0;
                case 4 -> solarActive ? 1 : 0;
                case 5 -> sarActive ? 1 : 0;
                case 6 -> lanceActive ? 1 : 0;
                case 7 -> secondsToStorm;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                storedEnergy = value;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public OrbitalGroundStationBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.ORBITAL_GROUND_STATION_BE, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (storedEnergy < MAX_ENERGY) {
            storedEnergy = Math.min(MAX_ENERGY, storedEnergy + 100);
            setChanged();
        }

        if (level.getGameTime() % 20 == 0 && level instanceof ServerLevel serverLevel) {
            this.satelliteCount = SatelliteNetworkManager.getActiveSatelliteCount(serverLevel);
            this.weatherActive = SatelliteNetworkManager.isWeatherReconActive(serverLevel);
            this.solarActive = SatelliteNetworkManager.isSolarReflectorActive(serverLevel);
            this.sarActive = SatelliteNetworkManager.isSarGeologicalActive(serverLevel);
            this.lanceActive = SatelliteNetworkManager.isKineticLanceActive(serverLevel);
            this.secondsToStorm = (int) (SatelliteNetworkManager.getTicksUntilNextSandstorm(serverLevel) / 20L);

            boolean shouldBeActive = this.storedEnergy > 0 && this.satelliteCount > 0;
            if (state.getValue(OrbitalGroundStationBlock.ACTIVE) != shouldBeActive) {
                level.setBlock(pos, state.setValue(OrbitalGroundStationBlock.ACTIVE, shouldBeActive), 3);
            }
            setChanged();
        }
    }

    public boolean triggerKineticStrike(BlockPos targetPos, Player caller) {
        if (this.level instanceof ServerLevel serverLevel && storedEnergy >= 20000) {
            storedEnergy -= 20000;
            setChanged();
            return SatelliteNetworkManager.triggerKineticStrike(serverLevel, targetPos, caller);
        }
        return false;
    }

    public int getStoredEnergy() {
        return storedEnergy;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.clamp(energy, 0, MAX_ENERGY);
        setChanged();
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.orbital_ground_station");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new OrbitalGroundStationMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.getFirst().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    public void setEnergy(int energy) {
        this.storedEnergy = energy;
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("storedEnergy", this.storedEnergy);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        ContainerHelper.loadAllItems(input, this.items);
    }
}
