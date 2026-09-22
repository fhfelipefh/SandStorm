package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.ThermalGeneratorBlock;
import com.fhfelipefh.sandstorm.content.block.ThermalGeneratorManager;
import com.fhfelipefh.sandstorm.content.gui.ThermalGeneratorMenu;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collections;
import java.util.Iterator;

public class ThermalGeneratorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int MAX_LAVA_MB = 4000;
    public static final int TICKS_PER_BUCKET = 4000;
    public static final long GENERATION_RATE = 60L;
    public static final long CAPACITY = 100000L;
    public static final long MAX_TRANSFER = 500L;

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    private int lavaAmount = 0;
    private int burnTime = 0;
    private int maxBurnTime = TICKS_PER_BUCKET;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) energyStorage.getStoredEnergy();
                case 1 -> (int) energyStorage.getCapacity();
                case 2 -> burnTime;
                case 3 -> maxBurnTime;
                case 4 -> burnTime > 0 ? 1 : 0;
                case 5 -> lavaAmount;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energyStorage.setStoredEnergy(value);
                case 2 -> burnTime = value;
                case 3 -> maxBurnTime = value;
                case 5 -> lavaAmount = value;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    private final Storage<FluidVariant> fluidStorage = new Storage<>() {
        @Override
        public boolean supportsInsertion() {
            return true;
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (!resource.isOf(Fluids.LAVA) || maxAmount <= 0) {
                return 0;
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            int spaceMb = MAX_LAVA_MB - lavaAmount;
            if (spaceMb <= 0) {
                return 0;
            }
            long spaceDroplets = spaceMb * dropletsPerMb;
            long acceptedDroplets = Math.min(maxAmount, spaceDroplets);
            int acceptedMb = (int) (acceptedDroplets / dropletsPerMb);
            if (acceptedMb > 0) {
                transaction.addCloseCallback((txn, result) -> {
                    if (result.wasCommitted()) {
                        lavaAmount += acceptedMb;
                        setChanged();
                    }
                });
                return acceptedMb * dropletsPerMb;
            }
            return 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            return Collections.emptyIterator();
        }
    };

    public ThermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.THERMAL_GENERATOR_BE, pos, state);
    }

    public ThermalGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return fluidStorage;
    }

    public int getLavaAmount() {
        return lavaAmount;
    }

    public void setLavaAmount(int lavaAmount) {
        this.lavaAmount = Math.clamp(lavaAmount, 0, MAX_LAVA_MB);
        setChanged();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = Math.max(0, burnTime);
        setChanged();
    }

    public int getMaxBurnTime() {
        return maxBurnTime;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean wasBurning = burnTime > 0;
        boolean changed = false;

        processBucketSlots();

        if (burnTime <= 0 && lavaAmount >= 1000) {
            lavaAmount -= 1000;
            burnTime = TICKS_PER_BUCKET;
            maxBurnTime = TICKS_PER_BUCKET;
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
            if (state.hasProperty(ThermalGeneratorBlock.LIT) && state.getValue(ThermalGeneratorBlock.LIT) != isBurning) {
                level.setBlock(pos, state.setValue(ThermalGeneratorBlock.LIT, isBurning), 3);
            }
        }

        ThermalGeneratorManager.registerGenerator(level.dimension(), pos, burnTime);

        if (changed) {
            setChanged();
        }
    }

    private void processBucketSlots() {
        ItemStack input = items.get(0);
        ItemStack output = items.get(1);

        if (input.is(Items.LAVA_BUCKET) && lavaAmount + 1000 <= MAX_LAVA_MB) {
            if (output.isEmpty()) {
                input.shrink(1);
                items.set(1, new ItemStack(Items.BUCKET));
                lavaAmount += 1000;
                setChanged();
            } else if (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize()) {
                input.shrink(1);
                output.grow(1);
                lavaAmount += 1000;
                setChanged();
            }
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            ThermalGeneratorManager.unregisterGenerator(level.dimension(), worldPosition);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putLong("storedEnergy", this.energyStorage.getStoredEnergy());
        output.putInt("lavaAmount", this.lavaAmount);
        output.putInt("burnTime", this.burnTime);
        output.putInt("maxBurnTime", this.maxBurnTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.energyStorage.setStoredEnergy(input.getLongOr("storedEnergy", 0L));
        this.lavaAmount = input.getIntOr("lavaAmount", 0);
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", TICKS_PER_BUCKET);
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
        return slot == 0 && stack.is(Items.LAVA_BUCKET);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 1 && stack.is(Items.BUCKET);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.thermal_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new ThermalGeneratorMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
