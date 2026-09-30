package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import java.util.Collections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
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

    public void setEnergy(int energy) {
        this.energy = Math.min(this.maxEnergy, Math.max(0, energy));
        setChanged();
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
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
                    notifyBlockUpdate();
                    onProcessCompleted(level, pos);
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
            if (level != null && !level.isClientSide() && (progress % 5 == 0 || progress == 0 || progress == 1)) {
                level.sendBlockUpdated(pos, state, state, 3);
            }
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
        return 0;
    }

    protected void onProcessCompleted(Level level, BlockPos pos) {
        SoundEvent sound = getProcessSound();
        if (sound != null) {
            level.playSound(null, pos, sound, SoundSource.BLOCKS, getProcessSoundVolume(), getProcessSoundPitch());
        }
        if (level instanceof ServerLevel serverLevel) {
            spawnProcessCompletedParticles(serverLevel, pos);
        }
    }

    protected float getProcessSoundVolume() {
        return 0.75f;
    }

    protected float getProcessSoundPitch() {
        return 1.0f;
    }

    protected void spawnProcessCompletedParticles(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, 4, 0.2, 0.1, 0.2, 0.02);
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
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.energy = input.getIntOr("energy", 0);
        this.progress = input.getIntOr("progress", 0);
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
            notifyBlockUpdate();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack item = ContainerHelper.takeItem(items, slot);
        if (!item.isEmpty()) {
            setChanged();
            notifyBlockUpdate();
        }
        return item;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
        notifyBlockUpdate();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
        notifyBlockUpdate();
    }

    public void notifyBlockUpdate() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
