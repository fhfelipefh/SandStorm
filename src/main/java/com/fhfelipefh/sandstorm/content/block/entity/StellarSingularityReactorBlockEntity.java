package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.StellarSingularityReactorBlock;
import com.fhfelipefh.sandstorm.content.block.WptRelayTowerManager;
import com.fhfelipefh.sandstorm.content.gui.StellarSingularityReactorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class StellarSingularityReactorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final long CAPACITY = 100000000L;
    public static final long GENERATION_RATE = 25000L;
    public static final int TICKS_PER_STAR = 72000;
    public static final long MAX_TRANSFER_RATE = 100000L;
    public static final long WPT_BROADCAST_RATE = 10000L;

    private static final int[] SLOTS = new int[]{0};

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(CAPACITY, MAX_TRANSFER_RATE);
    private int burnTime = 0;
    private int maxBurnTime = TICKS_PER_STAR;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) (energyStorage.getStoredEnergy() & 0xFFFF);
                case 1 -> (int) ((energyStorage.getStoredEnergy() >> 16) & 0xFFFF);
                case 2 -> (int) (CAPACITY & 0xFFFF);
                case 3 -> (int) ((CAPACITY >> 16) & 0xFFFF);
                case 4 -> burnTime & 0xFFFF;
                case 5 -> (burnTime >> 16) & 0xFFFF;
                case 6 -> maxBurnTime & 0xFFFF;
                case 7 -> (maxBurnTime >> 16) & 0xFFFF;
                case 8 -> burnTime > 0 ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energyStorage.setStoredEnergy((energyStorage.getStoredEnergy() & ~0xFFFFL) | (value & 0xFFFFL));
                case 1 -> energyStorage.setStoredEnergy((energyStorage.getStoredEnergy() & 0xFFFFL) | (((long) (value & 0xFFFF)) << 16));
                case 4 -> burnTime = (burnTime & ~0xFFFF) | (value & 0xFFFF);
                case 5 -> burnTime = (burnTime & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 6 -> maxBurnTime = (maxBurnTime & ~0xFFFF) | (value & 0xFFFF);
                case 7 -> maxBurnTime = (maxBurnTime & 0xFFFF) | ((value & 0xFFFF) << 16);
            }
        }

        @Override
        public int getCount() {
            return 9;
        }
    };

    public StellarSingularityReactorBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.STELLAR_SINGULARITY_REACTOR_BE, pos, state);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getMaxBurnTime() {
        return maxBurnTime;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean wasBurning = burnTime > 0;
        boolean changed = false;

        ItemStack fuelStack = items.get(0);
        if (burnTime <= 0 && fuelStack.is(Items.NETHER_STAR)) {
            fuelStack.shrink(1);
            burnTime = TICKS_PER_STAR;
            maxBurnTime = TICKS_PER_STAR;
            changed = true;
        }

        if (burnTime > 0) {
            burnTime--;
            energyStorage.receiveEnergy(GENERATION_RATE);
            changed = true;
        }

        boolean isBurning = burnTime > 0;
        if (wasBurning != isBurning) {
            changed = true;
            if (state.hasProperty(StellarSingularityReactorBlock.LIT) && state.getValue(StellarSingularityReactorBlock.LIT) != isBurning) {
                level.setBlock(pos, state.setValue(StellarSingularityReactorBlock.LIT, isBurning), 3);
            }
        }

        if (isBurning || energyStorage.getStoredEnergy() > 0) {
            WptRelayTowerManager.registerTower(level.dimension(), pos, WPT_BROADCAST_RATE);
        } else {
            WptRelayTowerManager.unregisterTower(level.dimension(), pos);
        }

        if (energyStorage.getStoredEnergy() > 0) {
            for (Direction dir : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                if (neighbor instanceof ThermalGeneratorBlockEntity thermalGen) {
                    energyStorage.transferTo(thermalGen.getEnergyStorage(), MAX_TRANSFER_RATE);
                } else if (neighbor instanceof AmnioticIncubatorBlockEntity incubator) {
                    energyStorage.transferTo(incubator.getEnergyStorage(), MAX_TRANSFER_RATE);
                } else if (neighbor instanceof AtmosphericTerraformerBlockEntity terraformer) {
                    energyStorage.transferTo(terraformer.getEnergyStorage(), MAX_TRANSFER_RATE);
                } else if (neighbor instanceof CryogenicAtmosphericChillerBlockEntity chiller) {
                    energyStorage.transferTo(chiller.getEnergyStorage(), MAX_TRANSFER_RATE);
                } else if (neighbor instanceof StormWindTurbineBlockEntity turbine) {
                    energyStorage.transferTo(turbine.getEnergyStorage(), MAX_TRANSFER_RATE);
                }
            }
        }

        if (changed) {
            setChanged();
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            WptRelayTowerManager.unregisterTower(level.dimension(), worldPosition);
        }
        super.setRemoved();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 0 && stack.is(Items.NETHER_STAR);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? items.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0) {
            ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
            if (!result.isEmpty()) {
                setChanged();
            }
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return slot == 0 ? ContainerHelper.takeItem(this.items, slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            this.items.set(0, stack);
            if (stack.getCount() > this.getMaxStackSize()) {
                stack.setCount(this.getMaxStackSize());
            }
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && stack.is(Items.NETHER_STAR);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.stellar_singularity_reactor");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new StellarSingularityReactorMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putLong("storedEnergy", this.energyStorage.getStoredEnergy());
        output.putInt("burnTime", this.burnTime);
        output.putInt("maxBurnTime", this.maxBurnTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.energyStorage.setStoredEnergy(input.getLongOr("storedEnergy", 0L));
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", TICKS_PER_STAR);
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
