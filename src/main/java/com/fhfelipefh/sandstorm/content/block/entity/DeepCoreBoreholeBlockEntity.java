package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.DeepCoreBoreholeBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.gui.DeepCoreBoreholeMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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

public class DeepCoreBoreholeBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int MAX_ENERGY = 500000;
    public static final int ENERGY_COST_PER_TICK = 250;
    public static final int MAX_FLUID = 8000;
    public static final int CYCLE_TICKS = 100;

    public static final int SLOT_DRILL_BIT = 0;
    public static final int SLOT_COOLANT_IN = 1;
    public static final int SLOT_COOLANT_OUT = 2;
    public static final int OUTPUT_START = 3;
    public static final int OUTPUT_COUNT = 9;

    private static final int[] SLOTS_TOP = new int[]{SLOT_DRILL_BIT, SLOT_COOLANT_IN};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_COOLANT_OUT, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    private static final int[] SLOTS_SIDES = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    private final NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
    private int storedEnergy = MAX_ENERGY;
    private int fluidAmount = 0;
    private int currentDepth = 0;
    private int progress = 0;
    private int temperature = 300;
    private int pressure = 1;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy & 0xFFFF;
                case 1 -> (storedEnergy >> 16) & 0xFFFF;
                case 2 -> MAX_ENERGY & 0xFFFF;
                case 3 -> (MAX_ENERGY >> 16) & 0xFFFF;
                case 4 -> progress;
                case 5 -> CYCLE_TICKS;
                case 6 -> currentDepth;
                case 7 -> temperature;
                case 8 -> pressure;
                case 9 -> fluidAmount;
                case 10 -> isDrilling() ? 1 : 0;
                case 11 -> hasDrillBit() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> storedEnergy = (storedEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 1 -> storedEnergy = (storedEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> progress = value;
                case 6 -> currentDepth = value;
                case 7 -> temperature = value;
                case 8 -> pressure = value;
                case 9 -> fluidAmount = value;
            }
        }

        @Override
        public int getCount() {
            return 12;
        }
    };

    private final Storage<FluidVariant> fluidStorage = new Storage<>() {
        @Override
        public boolean supportsInsertion() {
            return true;
        }

        @Override
        public boolean supportsExtraction() {
            return true;
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (!resource.isOf(Fluids.WATER) || maxAmount <= 0) {
                return 0;
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            int spaceMb = MAX_FLUID - fluidAmount;
            if (spaceMb <= 0) {
                return 0;
            }
            long spaceDroplets = (long) spaceMb * dropletsPerMb;
            long acceptedDroplets = Math.min(maxAmount, spaceDroplets);
            int acceptedMb = (int) (acceptedDroplets / dropletsPerMb);
            if (acceptedMb > 0) {
                transaction.addCloseCallback((txn, result) -> {
                    if (result.wasCommitted()) {
                        fluidAmount += acceptedMb;
                        setChanged();
                    }
                });
                return (long) acceptedMb * dropletsPerMb;
            }
            return 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (fluidAmount <= 0 || maxAmount <= 0 || !resource.isOf(Fluids.WATER)) {
                return 0;
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            long availableDroplets = (long) fluidAmount * dropletsPerMb;
            long extractedDroplets = Math.min(availableDroplets, maxAmount);
            int mbToExtract = (int) (extractedDroplets / dropletsPerMb);
            if (mbToExtract > 0) {
                transaction.addCloseCallback((txn, result) -> {
                    if (result.wasCommitted()) {
                        fluidAmount -= mbToExtract;
                        setChanged();
                    }
                });
                return (long) mbToExtract * dropletsPerMb;
            }
            return 0;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            if (fluidAmount <= 0) {
                return Collections.emptyIterator();
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            StorageView<FluidVariant> view = new StorageView<>() {
                @Override
                public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                    return fluidStorage.extract(resource, maxAmount, transaction);
                }

                @Override
                public boolean isResourceBlank() {
                    return fluidAmount <= 0;
                }

                @Override
                public FluidVariant getResource() {
                    return FluidVariant.of(Fluids.WATER);
                }

                @Override
                public long getAmount() {
                    return (long) fluidAmount * dropletsPerMb;
                }

                @Override
                public long getCapacity() {
                    return (long) MAX_FLUID * dropletsPerMb;
                }
            };
            return List.of(view).iterator();
        }
    };

    public DeepCoreBoreholeBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.DEEP_CORE_BOREHOLE_BE, pos, state);
    }

    public DeepCoreBoreholeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.currentDepth = pos.getY();
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        handleBucketInput();

        long wptCharge = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
        if (wptCharge > 0 && this.storedEnergy < MAX_ENERGY) {
            int toAdd = (int) Math.min(1000, wptCharge * 10);
            this.storedEnergy = Math.min(MAX_ENERGY, this.storedEnergy + Math.max(1, toAdd));
        }

        boolean canOperate = hasDrillBit() && this.storedEnergy >= ENERGY_COST_PER_TICK && hasOutputSpace();
        if (canOperate) {
            this.storedEnergy -= ENERGY_COST_PER_TICK;
            this.progress++;

            if (this.fluidAmount >= 2) {
                this.fluidAmount -= 2;
            }

            if (this.temperature < 1800) {
                this.temperature = Math.min(1800, 300 + (pos.getY() - this.currentDepth) * 15);
            }
            this.pressure = Math.min(12, 1 + (pos.getY() - this.currentDepth) / 10);

            if (this.progress >= CYCLE_TICKS) {
                this.progress = 0;
                performDrillingCycle(level, pos);
            }

            if (level instanceof ServerLevel serverLevel && serverLevel.getRandom().nextFloat() < 0.25f) {
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 2, 0.1, 0.2, 0.1, 0.02);
                serverLevel.sendParticles(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 1, 0.2, 0.1, 0.2, 0.0);
            }
        } else {
            if (this.progress > 0) {
                this.progress--;
            }
            if (this.temperature > 300) {
                this.temperature = Math.max(300, this.temperature - 2);
            }
        }

        boolean active = canOperate;
        if (state.hasProperty(DeepCoreBoreholeBlock.LIT) && state.getValue(DeepCoreBoreholeBlock.LIT) != active) {
            level.setBlock(pos, state.setValue(DeepCoreBoreholeBlock.LIT, active), 3);
            setChanged();
        }
    }

    private void handleBucketInput() {
        ItemStack bucketIn = this.items.get(SLOT_COOLANT_IN);
        if (!bucketIn.isEmpty() && bucketIn.is(Items.WATER_BUCKET)) {
            if (this.fluidAmount + 1000 <= MAX_FLUID) {
                ItemStack bucketOut = this.items.get(SLOT_COOLANT_OUT);
                if (bucketOut.isEmpty()) {
                    this.items.set(SLOT_COOLANT_OUT, new ItemStack(Items.BUCKET));
                    bucketIn.shrink(1);
                    this.fluidAmount += 1000;
                    setChanged();
                } else if (bucketOut.is(Items.BUCKET) && bucketOut.getCount() < bucketOut.getMaxStackSize()) {
                    bucketOut.grow(1);
                    bucketIn.shrink(1);
                    this.fluidAmount += 1000;
                    setChanged();
                }
            }
        }
    }

    private void performDrillingCycle(Level level, BlockPos pos) {
        if (this.currentDepth > -64) {
            this.currentDepth--;
        }

        ItemStack mineral = rollMantleMineral(level.getRandom());
        depositOutput(mineral);

        if (this.currentDepth <= -40 && level.getRandom().nextFloat() < 0.20f) {
            depositOutput(new ItemStack(SandStormItems.MANTLE_ALLOY_INGOT, 1));
        }

        level.playSound(null, pos, SoundEvents.WARDEN_DIG, SoundSource.BLOCKS, 1.2f, 0.7f);
        setChanged();
    }

    private ItemStack rollMantleMineral(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 45) {
            return new ItemStack(SandStormItems.RAW_LITHIUM_SALTS, 2 + random.nextInt(3));
        } else if (roll < 70) {
            return new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 1 + random.nextInt(2));
        } else if (roll < 85) {
            return new ItemStack(SandStormItems.RAW_SILICON, 2);
        } else if (roll < 95) {
            return new ItemStack(SandStormItems.SCRAP_METAL, 1 + random.nextInt(2));
        } else {
            return new ItemStack(Items.DIAMOND, 1);
        }
    }

    private void depositOutput(ItemStack stack) {
        for (int i = OUTPUT_START; i < OUTPUT_START + OUTPUT_COUNT; i++) {
            ItemStack slotStack = this.items.get(i);
            if (slotStack.isEmpty()) {
                this.items.set(i, stack);
                setChanged();
                return;
            }
            if (ItemStack.isSameItemSameComponents(slotStack, stack) && slotStack.getCount() + stack.getCount() <= slotStack.getMaxStackSize()) {
                slotStack.grow(stack.getCount());
                setChanged();
                return;
            }
        }
    }

    private boolean hasOutputSpace() {
        for (int i = OUTPUT_START; i < OUTPUT_START + OUTPUT_COUNT; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDrillBit() {
        ItemStack drill = this.items.get(SLOT_DRILL_BIT);
        return !drill.isEmpty() && drill.is(SandStormItems.GEOTHERMAL_CORE_DRILL_BIT);
    }

    public boolean isDrilling() {
        return hasDrillBit() && this.storedEnergy >= ENERGY_COST_PER_TICK && hasOutputSpace();
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.max(0, Math.min(MAX_ENERGY, energy));
        setChanged();
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public void setFluidAmount(int fluid) {
        this.fluidAmount = Math.max(0, Math.min(MAX_FLUID, fluid));
        setChanged();
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setChanged();
    }

    public int getCurrentDepth() {
        return this.currentDepth;
    }

    public void setCurrentDepth(int depth) {
        this.currentDepth = depth;
        setChanged();
    }

    public int getTemperature() {
        return this.temperature;
    }

    public int getPressure() {
        return this.pressure;
    }

    public Storage<FluidVariant> getFluidStorage(Direction direction) {
        return this.fluidStorage;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public int getContainerSize() {
        return 12;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
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
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
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
        if (side == Direction.UP) {
            return SLOTS_TOP;
        } else if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (slot == SLOT_DRILL_BIT) {
            return stack.is(SandStormItems.GEOTHERMAL_CORE_DRILL_BIT);
        }
        if (slot == SLOT_COOLANT_IN) {
            return stack.is(Items.WATER_BUCKET);
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == SLOT_COOLANT_OUT || slot >= OUTPUT_START;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.deep_core_borehole");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new DeepCoreBoreholeMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("fluidAmount", this.fluidAmount);
        output.putInt("currentDepth", this.currentDepth);
        output.putInt("progress", this.progress);
        output.putInt("temperature", this.temperature);
        output.putInt("pressure", this.pressure);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.fluidAmount = input.getIntOr("fluidAmount", 0);
        this.currentDepth = input.getIntOr("currentDepth", 0);
        this.progress = input.getIntOr("progress", 0);
        this.temperature = input.getIntOr("temperature", 300);
        this.pressure = input.getIntOr("pressure", 1);
    }
}
