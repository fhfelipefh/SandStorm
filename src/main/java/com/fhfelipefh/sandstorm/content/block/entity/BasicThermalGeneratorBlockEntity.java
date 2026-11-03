package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.BasicThermalGeneratorBlock;
import com.fhfelipefh.sandstorm.content.block.BasicThermalGeneratorManager;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.BasicThermalGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public class BasicThermalGeneratorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final long GENERATION_RATE = 16L;
    public static final long CAPACITY = 20000L;
    public static final long MAX_TRANSFER = 200L;

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(CAPACITY, 0L, MAX_TRANSFER);
    private int burnTime = 0;
    private int maxBurnTime = 1600;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) energyStorage.getStoredEnergy();
                case 1 -> (int) energyStorage.getCapacity();
                case 2 -> burnTime;
                case 3 -> maxBurnTime;
                case 4 -> burnTime > 0 ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energyStorage.setStoredEnergy(value);
                case 2 -> burnTime = value;
                case 3 -> maxBurnTime = value;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public BasicThermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.BASIC_THERMAL_GENERATOR_BE, pos, state);
    }

    public BasicThermalGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

    public static int getSolidFuelBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        CookingFuel fuel = stack.get(DataComponents.COOKING_FUEL);
        if (fuel != null) {
            try {
                int duration = ResolvableInt.getFromItem(stack, DataComponents.COOKING_FUEL, CookingFuel::burnTime, null, 0);
                if (duration > 0) {
                    return duration;
                }
            } catch (Exception ignored) {
            }
        }
        if (stack.is(ItemTags.LOGS) || stack.is(ItemTags.PLANKS)
                || stack.is(Items.OAK_PLANKS) || stack.is(Items.SPRUCE_PLANKS)
                || stack.is(Items.BIRCH_PLANKS) || stack.is(Items.JUNGLE_PLANKS)
                || stack.is(Items.ACACIA_PLANKS) || stack.is(Items.DARK_OAK_PLANKS)
                || stack.is(Items.MANGROVE_PLANKS) || stack.is(Items.CHERRY_PLANKS)
                || stack.is(Items.BAMBOO_PLANKS)) {
            return 300;
        }
        if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
            return 1600;
        }
        if (stack.is(Items.COAL_BLOCK)) {
            return 16000;
        }
        if (stack.is(Items.STICK)) {
            return 100;
        }
        return 0;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean wasLit = state.getValue(BasicThermalGeneratorBlock.LIT);
        boolean dirty = false;

        if (burnTime > 0) {
            burnTime--;
            energyStorage.receiveEnergy(GENERATION_RATE);
            dirty = true;
        }

        if (burnTime == 0 && energyStorage.getStoredEnergy() < energyStorage.getCapacity()) {
            ItemStack fuelStack = items.get(0);
            int fuelDuration = getSolidFuelBurnDuration(fuelStack);
            if (fuelDuration > 0) {
                burnTime = fuelDuration;
                maxBurnTime = fuelDuration;
                fuelStack.shrink(1);
                dirty = true;
            }
        }

        boolean isLit = burnTime > 0;
        if (wasLit != isLit) {
            level.setBlock(pos, state.setValue(BasicThermalGeneratorBlock.LIT, isLit), 3);
            dirty = true;
        }

        BasicThermalGeneratorManager.updateBurnTime(level.dimension(), pos, burnTime);

        if (dirty) {
            setChanged();
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 0 && getSolidFuelBurnDuration(stack) > 0;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
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
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
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
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.basic_thermal_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new BasicThermalGeneratorMenu(syncId, playerInventory, this, this.dataAccess);
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
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
        this.energyStorage.setStoredEnergy(input.getLongOr("storedEnergy", 0L));
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", 1600);
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

