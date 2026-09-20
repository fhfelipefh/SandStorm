package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class BaseMachineBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    protected final NonNullList<ItemStack> items;
    protected int energy = 0;
    protected int maxEnergy = 10000;
    protected int progress = 0;
    protected int maxProgress = 100;
    protected boolean wptConnected = false;
    protected float cachedWptCharge = -1.0f;
    protected int energyCostPerTick = 10;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> maxEnergy;
                case 2 -> progress;
                case 3 -> maxProgress;
                case 4 -> wptConnected ? 1 : 0;
                case 5 -> isProcessing() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = value;
                case 1 -> maxEnergy = value;
                case 2 -> progress = value;
                case 3 -> maxProgress = value;
                case 4 -> wptConnected = (value == 1);
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    protected BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int containerSize, int maxProgress) {
        super(type, pos, state);
        this.items = NonNullList.withSize(containerSize, ItemStack.EMPTY);
        this.maxProgress = maxProgress;
    }

    public boolean isProcessing() {
        return progress > 0;
    }

    public int getEnergy() {
        return energy;
    }

    public int getProgress() {
        return progress;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean changed = false;

        if (cachedWptCharge < 0.0f || (level.getGameTime() + pos.hashCode()) % 20 == 0) {
            cachedWptCharge = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
            wptConnected = (cachedWptCharge > 0.0f);
        }

        if (cachedWptCharge > 0.0f && energy < maxEnergy) {
            int toAdd = Math.round(cachedWptCharge * 15.0f);
            energy = Math.min(maxEnergy, energy + Math.max(1, toAdd));
            changed = true;
        }

        int batterySlot = getBatterySlotIndex();
        if (batterySlot >= 0 && batterySlot < items.size()) {
            ItemStack batteryStack = items.get(batterySlot);
            int fuelValue = getFuelEnergy(batteryStack);
            if (fuelValue > 0 && energy + fuelValue <= maxEnergy) {
                energy += fuelValue;
                batteryStack.shrink(1);
                changed = true;
            }
        }

        if (canProcess()) {
            if (energy >= energyCostPerTick) {
                energy -= energyCostPerTick;
                progress++;
                changed = true;

                if (progress >= maxProgress) {
                    processRecipe();
                    progress = 0;
                    SoundEvent sound = getProcessSound();
                    if (sound != null) {
                        level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.25f, 1.0f);
                    }
                }
            }
        } else {
            if (progress > 0) {
                progress = Math.max(0, progress - 2);
                changed = true;
            }
        }

        if (changed) {
            setChanged();
        }
    }

    public static int getFuelEnergy(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        if (stack.is(Items.REDSTONE)) {
            return 400;
        }
        if (stack.is(Items.REDSTONE_BLOCK)) {
            return 3600;
        }
        if (stack.is(SandStormItems.RAW_SILICON)) {
            return 250;
        }
        if (stack.is(SandStormItems.SILICON_WAFER)) {
            return 1000;
        }
        if (stack.is(SandStormItems.TECH_DISC)) {
            return 5000;
        }
        return 0;
    }

    protected abstract boolean canProcess();

    protected abstract void processRecipe();

    protected abstract SoundEvent getProcessSound();

    protected abstract int getBatterySlotIndex();

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("energy", this.energy);
        output.putInt("progress", this.progress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.energy = input.getIntOr("energy", 0);
        this.progress = input.getIntOr("progress", 0);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
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
        ItemStack item = ContainerHelper.takeItem(items, slot);
        if (!item.isEmpty()) {
            setChanged();
        }
        return item;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize(stack)) {
            stack.setCount(getMaxStackSize(stack));
        }
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
}
