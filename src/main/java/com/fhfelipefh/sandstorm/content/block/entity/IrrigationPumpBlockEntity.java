package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.IrrigationHoseBlock;
import com.fhfelipefh.sandstorm.content.block.IrrigationPumpBlock;
import com.fhfelipefh.sandstorm.content.block.IrrigationSprinklerBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.gui.IrrigationPumpMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class IrrigationPumpBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int CONTAINER_SIZE = 4;
    public static final int SLOT_WATER_IN = 0;
    public static final int SLOT_WATER_OUT = 1;
    public static final int SLOT_NUTRIENTS = 2;
    public static final int SLOT_BATTERY = 3;

    public static final int MAX_ENERGY = 20000;
    public static final int MAX_WATER = 10000;
    public static final int MAX_NUTRIENTS = 1000;

    private static final int[] SLOTS_TOP = new int[]{SLOT_WATER_IN, SLOT_NUTRIENTS, SLOT_BATTERY};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_WATER_OUT};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_WATER_IN, SLOT_NUTRIENTS, SLOT_BATTERY, SLOT_WATER_OUT};

    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private int energy = 0;
    private int waterAmount = 0;
    private int nutrients = 0;
    private int connectedSprinklers = 0;
    private int pressureRadius = 0;
    private boolean active = false;
    private int tickCounter = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> MAX_ENERGY;
                case 2 -> waterAmount;
                case 3 -> MAX_WATER;
                case 4 -> nutrients;
                case 5 -> MAX_NUTRIENTS;
                case 6 -> connectedSprinklers;
                case 7 -> pressureRadius;
                case 8 -> active ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = value;
                case 2 -> waterAmount = value;
                case 4 -> nutrients = value;
            }
        }

        @Override
        public int getCount() {
            return 9;
        }
    };

    public IrrigationPumpBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.IRRIGATION_PUMP_BE, pos, state);
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

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
            if (!resource.isOf(Fluids.WATER) || maxAmount <= 0) {
                return 0;
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            long currentDroplets = (long) waterAmount * dropletsPerMb;
            long maxDroplets = (long) MAX_WATER * dropletsPerMb;
            long space = maxDroplets - currentDroplets;
            if (space <= 0) {
                return 0;
            }
            long toInsert = Math.min(maxAmount, space);
            int addedMb = (int) (toInsert / dropletsPerMb);
            if (addedMb <= 0) {
                return 0;
            }
            long actualInserted = (long) addedMb * dropletsPerMb;
            int prevWater = waterAmount;
            waterAmount += addedMb;
            transaction.addCloseCallback((tx, result) -> {
                if (result.wasAborted()) {
                    waterAmount = prevWater;
                } else {
                    setChanged();
                    if (level != null && !level.isClientSide()) {
                        BlockState current = getBlockState();
                        level.sendBlockUpdated(worldPosition, current, current, 3);
                    }
                }
            });
            return actualInserted;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            if (waterAmount <= 0) {
                return Collections.emptyIterator();
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            StorageView<FluidVariant> view = new StorageView<>() {
                @Override
                public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                    return 0;
                }

                @Override
                public boolean isResourceBlank() {
                    return waterAmount <= 0;
                }

                @Override
                public FluidVariant getResource() {
                    return FluidVariant.of(Fluids.WATER);
                }

                @Override
                public long getAmount() {
                    return (long) waterAmount * dropletsPerMb;
                }

                @Override
                public long getCapacity() {
                    return (long) MAX_WATER * dropletsPerMb;
                }
            };
            return List.of(view).iterator();
        }
    };

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return fluidStorage;
    }

    public int getEnergy() {
        return energy;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getNutrients() {
        return nutrients;
    }

    public int getConnectedSprinklers() {
        return connectedSprinklers;
    }

    public int getPressureRadius() {
        return pressureRadius;
    }

    public boolean isActive() {
        return active;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IrrigationPumpBlockEntity be) {
        be.tickCounter++;
        boolean changed = false;

        if (be.energy < MAX_ENERGY) {
            float wpt = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
            if (wpt > 0.0f) {
                int toAdd = Math.round(wpt * 12.0f);
                be.energy = Math.min(MAX_ENERGY, be.energy + Math.max(1, toAdd));
                changed = true;
            }
        }

        if (be.handleBatteryInput()) {
            changed = true;
        }

        if (be.handleWaterInput()) {
            changed = true;
        }

        if (be.handleNutrientInput()) {
            changed = true;
        }

        if (be.tickCounter % 20 == 0) {
            if (be.updateIrrigationNetwork(level, pos)) {
                changed = true;
            }
        }

        if (changed) {
            be.setChanged();
        }
    }

    private boolean handleBatteryInput() {
        if (energy >= MAX_ENERGY) {
            return false;
        }
        ItemStack batteryStack = items.get(SLOT_BATTERY);
        if (batteryStack.isEmpty()) {
            return false;
        }
        int fuelVal = getFuelEnergy(batteryStack);
        if (fuelVal > 0 && energy + fuelVal <= MAX_ENERGY) {
            energy += fuelVal;
            batteryStack.shrink(1);
            return true;
        }
        return false;
    }

    private static int getFuelEnergy(ItemStack stack) {
        if (stack.is(Items.REDSTONE)) {
            return 400;
        }
        if (stack.is(Items.REDSTONE_BLOCK)) {
            return 3600;
        }
        if (stack.is(SandStormItems.ELECTRIC_COMPONENT)) {
            return 8000;
        }
        return 0;
    }

    private boolean handleWaterInput() {
        if (waterAmount >= MAX_WATER) {
            return false;
        }
        ItemStack inStack = items.get(SLOT_WATER_IN);
        if (inStack.isEmpty()) {
            return false;
        }
        ItemStack outStack = items.get(SLOT_WATER_OUT);

        if (inStack.is(Items.WATER_BUCKET)) {
            if (outStack.isEmpty() || (outStack.is(Items.BUCKET) && outStack.getCount() < outStack.getMaxStackSize())) {
                if (waterAmount + 1000 <= MAX_WATER) {
                    waterAmount += 1000;
                    inStack.shrink(1);
                    if (outStack.isEmpty()) {
                        items.set(SLOT_WATER_OUT, new ItemStack(Items.BUCKET));
                    } else {
                        outStack.grow(1);
                    }
                    return true;
                }
            }
        } else if (inStack.is(Items.POTION)) {
            if (outStack.isEmpty() || (outStack.is(Items.GLASS_BOTTLE) && outStack.getCount() < outStack.getMaxStackSize())) {
                if (waterAmount + 250 <= MAX_WATER) {
                    waterAmount += 250;
                    inStack.shrink(1);
                    if (outStack.isEmpty()) {
                        items.set(SLOT_WATER_OUT, new ItemStack(Items.GLASS_BOTTLE));
                    } else {
                        outStack.grow(1);
                    }
                    return true;
                }
            }
        } else if (inStack.is(Items.ICE)) {
            if (waterAmount + 1000 <= MAX_WATER) {
                waterAmount += 1000;
                inStack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private boolean handleNutrientInput() {
        if (nutrients >= MAX_NUTRIENTS) {
            return false;
        }
        ItemStack stack = items.get(SLOT_NUTRIENTS);
        if (stack.isEmpty()) {
            return false;
        }

        int add = 0;
        if (stack.is(SandStormItems.MINERAL_SALT)) {
            add = 25;
        } else if (stack.is(SandStormItems.RAW_LITHIUM_SALTS)) {
            add = 50;
        } else if (stack.is(SandStormItems.NUTRIENT_BOMB)) {
            add = 100;
        } else if (stack.is(Items.BONE_MEAL)) {
            add = 15;
        } else if (stack.is(SandStormItems.ACID_CHITIN_PLATING)) {
            add = 30;
        } else if (stack.is(SandStormItems.XENO_GRASS_SEEDS)) {
            add = 20;
        } else if (stack.is(Items.WHEAT_SEEDS) || stack.is(Items.BEETROOT_SEEDS)) {
            add = 10;
        }

        if (add > 0 && nutrients + add <= MAX_NUTRIENTS) {
            nutrients += add;
            stack.shrink(1);
            return true;
        }
        return false;
    }

    private boolean updateIrrigationNetwork(Level level, BlockPos pos) {
        List<IrrigationSprinklerBlockEntity> sprinklers = findConnectedSprinklers(level, pos);
        int count = sprinklers.size();
        this.connectedSprinklers = count;

        boolean wasActive = this.active;
        if (count > 0) {
            int radius = Math.max(3, (int) Math.round(15.0 / Math.sqrt(count)));
            this.pressureRadius = radius;

            int energyCost = Math.min(45, count * 3);
            int waterCost = count * 2;
            int nutrientCost = Math.max(1, count / 3);

            if (this.energy >= energyCost && this.waterAmount >= waterCost && this.nutrients >= nutrientCost) {
                this.energy -= energyCost;
                this.waterAmount -= waterCost;
                this.nutrients -= nutrientCost;
                this.active = true;

                for (IrrigationSprinklerBlockEntity sprinkler : sprinklers) {
                    sprinkler.setActive(true, radius);
                }
            } else {
                this.active = false;
                for (IrrigationSprinklerBlockEntity sprinkler : sprinklers) {
                    sprinkler.setActive(false, 0);
                }
            }
        } else {
            this.active = false;
            this.pressureRadius = 0;
        }

        if (wasActive != this.active) {
            BlockState currentState = getBlockState();
            if (currentState.hasProperty(IrrigationPumpBlock.ACTIVE)) {
                level.setBlock(pos, currentState.setValue(IrrigationPumpBlock.ACTIVE, this.active), 3);
            }
            return true;
        }
        return false;
    }

    private static List<IrrigationSprinklerBlockEntity> findConnectedSprinklers(Level level, BlockPos startPos) {
        List<IrrigationSprinklerBlockEntity> foundSprinklers = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        visited.add(startPos);
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = startPos.relative(dir);
            if (isValidNetworkNode(level, neighbor)) {
                queue.add(neighbor);
                visited.add(neighbor);
            }
        }

        int maxSearch = 128;
        while (!queue.isEmpty() && visited.size() <= maxSearch) {
            BlockPos current = queue.poll();
            BlockState state = level.getBlockState(current);
            Block block = state.getBlock();

            if (block instanceof IrrigationSprinklerBlock) {
                if (level.getBlockEntity(current) instanceof IrrigationSprinklerBlockEntity sprinkler) {
                    foundSprinklers.add(sprinkler);
                }
                continue;
            }

            if (block instanceof IrrigationHoseBlock) {
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = current.relative(dir);
                    if (!visited.contains(neighbor) && isValidNetworkNode(level, neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
                for (Direction horiz : Direction.Plane.HORIZONTAL) {
                    BlockPos stepUp = current.relative(horiz).above();
                    if (!visited.contains(stepUp) && isValidNetworkNode(level, stepUp)) {
                        visited.add(stepUp);
                        queue.add(stepUp);
                    }
                    BlockPos stepDown = current.relative(horiz).below();
                    if (!visited.contains(stepDown) && isValidNetworkNode(level, stepDown)) {
                        visited.add(stepDown);
                        queue.add(stepDown);
                    }
                }
            }
        }

        return foundSprinklers;
    }

    private static boolean isValidNetworkNode(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        Block block = level.getBlockState(pos).getBlock();
        return block instanceof IrrigationHoseBlock || block instanceof IrrigationSprinklerBlock;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("energy", this.energy);
        output.putInt("waterAmount", this.waterAmount);
        output.putInt("nutrients", this.nutrients);
        output.putInt("connectedSprinklers", this.connectedSprinklers);
        output.putInt("pressureRadius", this.pressureRadius);
        output.putBoolean("active", this.active);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.energy = input.getIntOr("energy", 0);
        this.waterAmount = input.getIntOr("waterAmount", 0);
        this.nutrients = input.getIntOr("nutrients", 0);
        this.connectedSprinklers = input.getIntOr("connectedSprinklers", 0);
        this.pressureRadius = input.getIntOr("pressureRadius", 0);
        this.active = input.getBooleanOr("active", false);
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
        ItemStack res = ContainerHelper.removeItem(items, slot, amount);
        if (!res.isEmpty()) {
            setChanged();
        }
        return res;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack res = ContainerHelper.takeItem(items, slot);
        if (!res.isEmpty()) {
            setChanged();
        }
        return res;
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
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return SLOTS_TOP;
        } else if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        } else {
            return SLOTS_SIDES;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (slot == SLOT_WATER_OUT) {
            return false;
        }
        if (slot == SLOT_WATER_IN) {
            return stack.is(Items.WATER_BUCKET) || stack.is(Items.POTION) || stack.is(Items.ICE);
        }
        if (slot == SLOT_BATTERY) {
            return getFuelEnergy(stack) > 0;
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == SLOT_WATER_OUT;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.irrigation_pump");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new IrrigationPumpMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
