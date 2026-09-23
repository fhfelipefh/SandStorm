package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorBlock;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorManager;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.gui.SolidStateAccumulatorMenu;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SolidStateAccumulatorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int DEFAULT_CAPACITY = 500000;
    public static final int CHARGE_PER_TICK = 250;
    public static final int DISCHARGE_DRAIN_PER_TICK = 50;

    private int storedEnergy = 0;
    private int maxEnergy = DEFAULT_CAPACITY;
    private int mode = 0;
    private boolean isDischarging = false;
    private boolean isCharging = false;
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy & 0xFFFF;
                case 1 -> (storedEnergy >> 16) & 0xFFFF;
                case 2 -> maxEnergy & 0xFFFF;
                case 3 -> (maxEnergy >> 16) & 0xFFFF;
                case 4 -> mode;
                case 5 -> isDischarging ? 1 : 0;
                case 6 -> isCharging ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> storedEnergy = (storedEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 1 -> storedEnergy = (storedEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 2 -> maxEnergy = (maxEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 3 -> maxEnergy = (maxEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> mode = value;
                case 5 -> isDischarging = (value == 1);
                case 6 -> isCharging = (value == 1);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public SolidStateAccumulatorBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.SOLID_STATE_ACCUMULATOR_BE, pos, state);
    }

    public int getStoredEnergy() {
        return storedEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = Math.floorMod(mode, 3);
        setChanged();
    }

    public void cycleMode() {
        setMode((mode + 1) % 3);
    }

    public boolean isDischarging() {
        return isDischarging;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolidStateAccumulatorBlockEntity entity) {
        boolean changed = false;

        ItemStack inputStack = entity.items.get(0);
        if (!inputStack.isEmpty()) {
            int fuel = BaseMachineBlockEntity.getFuelEnergy(inputStack);
            if (fuel > 0 && entity.storedEnergy + fuel <= entity.maxEnergy) {
                entity.storedEnergy += fuel;
                inputStack.shrink(1);
                changed = true;
            }
        }

        long primaryCharge = WirelessSolarReceiverManager.getPrimaryWptChargeAt(level, pos);

        if (entity.mode == 0) {
            if (primaryCharge > 0L && entity.storedEnergy < entity.maxEnergy) {
                int toAdd = (int) Math.min(CHARGE_PER_TICK, primaryCharge * 5L);
                entity.storedEnergy = Math.min(entity.maxEnergy, entity.storedEnergy + Math.max(1, toAdd));
                entity.isCharging = true;
                entity.isDischarging = false;
                changed = true;
            } else if (primaryCharge == 0L && entity.storedEnergy > 0) {
                entity.storedEnergy = Math.max(0, entity.storedEnergy - DISCHARGE_DRAIN_PER_TICK);
                entity.isDischarging = true;
                entity.isCharging = false;
                changed = true;
            } else {
                entity.isCharging = false;
                entity.isDischarging = false;
            }
        } else if (entity.mode == 1) {
            if (primaryCharge > 0L && entity.storedEnergy < entity.maxEnergy) {
                int toAdd = (int) Math.min(CHARGE_PER_TICK, primaryCharge * 5L);
                entity.storedEnergy = Math.min(entity.maxEnergy, entity.storedEnergy + Math.max(1, toAdd));
                entity.isCharging = true;
                entity.isDischarging = false;
                changed = true;
            } else {
                entity.isCharging = false;
                entity.isDischarging = false;
            }
        } else if (entity.mode == 2) {
            if (entity.storedEnergy > 0) {
                entity.storedEnergy = Math.max(0, entity.storedEnergy - DISCHARGE_DRAIN_PER_TICK);
                entity.isDischarging = true;
                entity.isCharging = false;
                changed = true;
            } else {
                entity.isCharging = false;
                entity.isDischarging = false;
            }
        }

        SolidStateAccumulatorManager.registerAccumulator(
                level.dimension(),
                pos,
                entity.storedEnergy,
                entity.maxEnergy,
                entity.isDischarging,
                entity.mode
        );

        int targetChargeLevel = (int) Math.round(((double) entity.storedEnergy / Math.max(1, entity.maxEnergy)) * 4.0);
        targetChargeLevel = Math.clamp(targetChargeLevel, 0, 4);

        if (state.hasProperty(SolidStateAccumulatorBlock.CHARGE_LEVEL) && state.hasProperty(SolidStateAccumulatorBlock.MODE)) {
            if (state.getValue(SolidStateAccumulatorBlock.CHARGE_LEVEL) != targetChargeLevel || state.getValue(SolidStateAccumulatorBlock.MODE) != entity.mode) {
                level.setBlock(pos, state.setValue(SolidStateAccumulatorBlock.CHARGE_LEVEL, targetChargeLevel).setValue(SolidStateAccumulatorBlock.MODE, entity.mode), 3);
            }
        }

        if (changed) {
            entity.setChanged();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("maxEnergy", this.maxEnergy);
        output.putInt("mode", this.mode);
        output.putBoolean("isDischarging", this.isDischarging);
        output.putBoolean("isCharging", this.isCharging);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.storedEnergy = input.getIntOr("storedEnergy", 0);
        this.maxEnergy = input.getIntOr("maxEnergy", DEFAULT_CAPACITY);
        this.mode = input.getIntOr("mode", 0);
        this.isDischarging = input.getBooleanOr("isDischarging", false);
        this.isCharging = input.getBooleanOr("isCharging", false);
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
    public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            SolidStateAccumulatorManager.unregisterAccumulator(level.dimension(), worldPosition);
        }
        super.setRemoved();
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
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
        ItemStack result = ContainerHelper.takeItem(items, slot);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
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

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return new int[]{0};
        }
        return new int[]{1};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 0 && BaseMachineBlockEntity.getFuelEnergy(stack) > 0;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 1;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.solid_state_accumulator");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SolidStateAccumulatorMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
