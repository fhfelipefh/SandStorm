package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.DewCondenserBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.DewCondenserMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import java.util.Collections;
import java.util.Iterator;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.core.component.DataComponentPatch;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class DewCondenserBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int MAX_WATER = 3000;
    public static final int MAX_PROGRESS = 200;

    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{1};
    private static final int[] SLOTS_SIDES = new int[]{0, 1};

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int waterAmount = 0;
    private int progress = 0;

    private final Storage<FluidVariant> outputFluidStorage = new Storage<>() {
        @Override
        public boolean supportsInsertion() {
            return false;
        }

        @Override
        public boolean supportsExtraction() {
            return true;
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (!resource.isOf(Fluids.WATER) || maxAmount <= 0) {
                return 0;
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            long currentDroplets = (long) waterAmount * dropletsPerMb;
            if (currentDroplets <= 0) {
                return 0;
            }
            long toExtract = Math.min(maxAmount, currentDroplets);
            int drainedMb = (int) (toExtract / dropletsPerMb);
            if (drainedMb <= 0) {
                return 0;
            }
            long actualDrained = (long) drainedMb * dropletsPerMb;
            int prevWater = waterAmount;
            waterAmount -= drainedMb;
            transaction.addCloseCallback((tx, result) -> {
                if (result.wasAborted()) {
                    waterAmount = prevWater;
                } else {
                    setChanged();
                    if (level != null && !level.isClientSide()) {
                        updateWaterLevelState(level, worldPosition, getBlockState());
                    }
                }
            });
            return actualDrained;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            return Collections.<StorageView<FluidVariant>>singletonList(new StorageView<>() {
                @Override
                public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                    return outputFluidStorage.extract(resource, maxAmount, transaction);
                }

                @Override
                public boolean isResourceBlank() {
                    return waterAmount <= 0;
                }

                @Override
                public FluidVariant getResource() {
                    return waterAmount > 0 ? new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY) : new FluidVariantImpl(Fluids.EMPTY, DataComponentPatch.EMPTY);
                }

                @Override
                public long getAmount() {
                    return (long) waterAmount * (FluidConstants.BUCKET / 1000);
                }

                @Override
                public long getCapacity() {
                    return (long) MAX_WATER * (FluidConstants.BUCKET / 1000);
                }
            }).iterator();
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> waterAmount;
                case 1 -> MAX_WATER;
                case 2 -> progress;
                case 3 -> MAX_PROGRESS;
                case 4 -> getStatus();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> waterAmount = value;
                case 2 -> progress = value;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public DewCondenserBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.DEW_CONDENSER_BE, pos, state);
    }

    public DewCondenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        if (side == Direction.DOWN || side == null) {
            return outputFluidStorage;
        }
        return Storage.empty();
    }

    public int getWaterAmount() {
        return this.waterAmount;
    }

    public void setWaterAmount(int waterAmount) {
        this.waterAmount = Math.clamp(waterAmount, 0, MAX_WATER);
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            updateWaterLevelState(this.level, this.worldPosition, getBlockState());
        }
    }

    public int getMaxWater() {
        return MAX_WATER;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return MAX_PROGRESS;
    }

    public int drainWater(int amount) {
        if (amount <= 0 || this.waterAmount <= 0) {
            return 0;
        }
        int drained = Math.min(this.waterAmount, amount);
        this.waterAmount -= drained;
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            updateWaterLevelState(this.level, this.worldPosition, getBlockState());
        }
        return drained;
    }

    public int getStatus() {
        if (this.waterAmount >= MAX_WATER) {
            return 3;
        }
        if (this.level == null || !this.level.canSeeSky(this.worldPosition)) {
            return 0;
        }
        boolean isNight = this.level.getSkyDarken() >= 4;
        if (isNight) {
            return 2;
        }
        return 1;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean isFull = this.waterAmount >= MAX_WATER;
        boolean canSeeSky = level.canSeeSky(pos);
        boolean isNight = level.getSkyDarken() >= 4;

        if (!isFull && canSeeSky && isNight) {
            this.progress++;
            if (this.progress >= MAX_PROGRESS) {
                this.progress = 0;
                this.waterAmount = Math.min(MAX_WATER, this.waterAmount + 250);
                updateWaterLevelState(level, pos, state);
                level.playSound(null, pos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER, SoundSource.BLOCKS, 0.5f, 1.2f);
                setChanged();
            }
        } else if (this.progress > 0 && (!canSeeSky || !isNight)) {
            this.progress = Math.max(0, this.progress - 1);
        }

        processItemFilling();

        if (this.waterAmount > 0 && level.getGameTime() % 10 == 0) {
            autoPushWaterDown(level, pos);
        }
    }

    private void updateWaterLevelState(Level level, BlockPos pos, BlockState state) {
        int targetLevel = calculateWaterLevel();
        if (state.getValue(DewCondenserBlock.WATER_LEVEL) != targetLevel) {
            level.setBlock(pos, state.setValue(DewCondenserBlock.WATER_LEVEL, targetLevel), 3);
        }
    }

    public int calculateWaterLevel() {
        if (this.waterAmount <= 0) {
            return 0;
        }
        if (this.waterAmount < 1000) {
            return 1;
        }
        if (this.waterAmount < 2500) {
            return 2;
        }
        return 3;
    }

    private void processItemFilling() {
        ItemStack inputStack = this.items.get(0);
        if (inputStack.isEmpty()) {
            return;
        }

        if (inputStack.is(Items.GLASS_BOTTLE) && this.waterAmount >= 250) {
            ItemStack outputStack = this.items.get(1);
            if (outputStack.isEmpty()) {
                inputStack.shrink(1);
                this.items.set(1, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE));
                this.waterAmount -= 250;
                if (this.level != null) {
                    updateWaterLevelState(this.level, this.worldPosition, getBlockState());
                }
                setChanged();
            } else if (outputStack.is(SandStormItems.POTABLE_WATER_BOTTLE) && outputStack.getCount() < outputStack.getMaxStackSize()) {
                inputStack.shrink(1);
                outputStack.grow(1);
                this.waterAmount -= 250;
                if (this.level != null) {
                    updateWaterLevelState(this.level, this.worldPosition, getBlockState());
                }
                setChanged();
            }
        } else if (inputStack.is(Items.BUCKET) && this.waterAmount >= 1000) {
            ItemStack outputStack = this.items.get(1);
            if (outputStack.isEmpty()) {
                inputStack.shrink(1);
                this.items.set(1, new ItemStack(Items.WATER_BUCKET));
                this.waterAmount -= 1000;
                if (this.level != null) {
                    updateWaterLevelState(this.level, this.worldPosition, getBlockState());
                }
                setChanged();
            }
        }
    }

    private void autoPushWaterDown(Level level, BlockPos pos) {
        Storage<FluidVariant> targetStorage = FluidStorage.SIDED.find(level, pos.below(), Direction.UP);
        if (targetStorage != null) {
            long moved = StorageUtil.move(this.outputFluidStorage, targetStorage, filter -> true, 250L * (FluidConstants.BUCKET / 1000), null);
            if (moved > 0) {
                updateWaterLevelState(level, pos, getBlockState());
                setChanged();
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.dew_condenser");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new DewCondenserMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : this.items) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack item = ContainerHelper.removeItem(this.items, slot, amount);
        if (!item.isEmpty()) {
            setChanged();
        }
        return item;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
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
        this.items.clear();
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        if (side == Direction.UP) {
            return SLOTS_TOP;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(Items.GLASS_BOTTLE) || stack.is(Items.BUCKET);
        }
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 0 && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot == 1;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("waterAmount", this.waterAmount);
        output.putInt("progress", this.progress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.waterAmount = input.getIntOr("waterAmount", 0);
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
}
