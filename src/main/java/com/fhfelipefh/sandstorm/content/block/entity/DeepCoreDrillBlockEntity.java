package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.DeepCoreDrillBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.DeepCoreDrillMenu;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DeepCoreDrillBlockEntity extends BaseMachineBlockEntity {
    public static final int MAX_FLUID = 4000;
    public static final int ENERGY_CAPACITY = 50000;
    public static final int ENERGY_CONSUMPTION = 50;
    public static final int CYCLE_TIME = 200;

    private int fossilFluid = 0;

    private final ContainerData drillDataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy & 0xFFFF;
                case 1 -> (energy >> 16) & 0xFFFF;
                case 2 -> maxEnergy & 0xFFFF;
                case 3 -> (maxEnergy >> 16) & 0xFFFF;
                case 4 -> progress;
                case 5 -> maxProgress;
                case 6 -> fossilFluid;
                case 7 -> isProcessing() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = (energy & ~0xFFFF) | (value & 0xFFFF);
                case 1 -> energy = (energy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 2 -> maxEnergy = (maxEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 3 -> maxEnergy = (maxEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> progress = value;
                case 5 -> maxProgress = value;
                case 6 -> fossilFluid = value;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    private final SnapshotParticipant<Integer> fluidParticipant = new SnapshotParticipant<>() {
        @Override
        protected Integer createSnapshot() {
            return fossilFluid;
        }

        @Override
        protected void readSnapshot(Integer snapshot) {
            fossilFluid = snapshot;
        }

        @Override
        protected void onFinalCommit() {
            setChanged();
        }
    };

    private final Storage<FluidVariant> fluidStorage = new Storage<>() {
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
            if (fossilFluid <= 0 || maxAmount <= 0) {
                return 0;
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            long availableDroplets = (long) fossilFluid * dropletsPerMb;
            long extractedDroplets = Math.min(availableDroplets, maxAmount);
            int mbToExtract = (int) (extractedDroplets / dropletsPerMb);
            if (mbToExtract <= 0) {
                return 0;
            }
            fluidParticipant.updateSnapshots(transaction);
            fossilFluid -= mbToExtract;
            return (long) mbToExtract * dropletsPerMb;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            if (fossilFluid <= 0) {
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
                    return fossilFluid <= 0;
                }

                @Override
                public FluidVariant getResource() {
                    return FluidVariant.of(Fluids.LAVA);
                }

                @Override
                public long getAmount() {
                    return (long) fossilFluid * dropletsPerMb;
                }

                @Override
                public long getCapacity() {
                    return (long) MAX_FLUID * dropletsPerMb;
                }
            };
            return List.of(view).iterator();
        }
    };

    public DeepCoreDrillBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.DEEP_CORE_DRILL_BE, pos, state);
    }

    public DeepCoreDrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 8, CYCLE_TIME);
        this.maxEnergy = ENERGY_CAPACITY;
        this.energyCostPerTick = ENERGY_CONSUMPTION;
    }

    public Storage<FluidVariant> getFluidStorage(Direction direction) {
        return fluidStorage;
    }

    public int getFluidAmount() {
        return fossilFluid;
    }

    public void setFluidAmount(int amount) {
        this.fossilFluid = Math.max(0, Math.min(MAX_FLUID, amount));
        setChanged();
    }

    public ContainerData getContainerData() {
        return drillDataAccess;
    }

    public int getStoredEnergy() {
        return energy;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DeepCoreDrillBlockEntity drill) {
        drill.serverTick(level, pos, state);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        handleBucketInteraction();

        boolean currentBlockDrilling = state.getValue(DeepCoreDrillBlock.DRILLING);
        boolean active = isProcessing();
        if (currentBlockDrilling != active) {
            level.setBlock(pos, state.setValue(DeepCoreDrillBlock.DRILLING, active), 3);
        }
    }

    @Override
    protected boolean canProcess() {
        return fossilFluid < MAX_FLUID || hasOutputSpace();
    }

    private boolean hasOutputSpace() {
        for (int i = 2; i <= 7; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void processRecipe() {
        if (fossilFluid + 250 <= MAX_FLUID) {
            fossilFluid += 250;
        }
        if (level != null) {
            ItemStack reward = rollMantleMineral(level.getRandom());
            depositMineral(reward);
        }
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.HEAVY_CORE_HIT;
    }

    @Override
    protected int getBatterySlotIndex() {
        return -1;
    }

    private ItemStack rollMantleMineral(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 30) {
            return new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 1 + random.nextInt(2));
        } else if (roll < 55) {
            return new ItemStack(Items.RAW_IRON, 1 + random.nextInt(2));
        } else if (roll < 75) {
            return new ItemStack(Items.RAW_COPPER, 1 + random.nextInt(3));
        } else if (roll < 88) {
            return new ItemStack(SandStormItems.MINERAL_SALT, 2);
        } else if (roll < 97) {
            return new ItemStack(SandStormItems.SCRAP_METAL, 1);
        } else {
            return new ItemStack(Items.DIAMOND, 1);
        }
    }

    private void depositMineral(ItemStack reward) {
        for (int i = 2; i <= 7; i++) {
            ItemStack slotStack = items.get(i);
            if (slotStack.isEmpty()) {
                items.set(i, reward);
                return;
            }
            if (ItemStack.isSameItemSameComponents(slotStack, reward) && slotStack.getCount() + reward.getCount() <= slotStack.getMaxStackSize()) {
                slotStack.grow(reward.getCount());
                return;
            }
        }
    }

    private void handleBucketInteraction() {
        ItemStack inSlot = items.get(0);
        ItemStack outSlot = items.get(1);

        if (inSlot.is(Items.BUCKET) && fossilFluid >= 1000) {
            if (outSlot.isEmpty()) {
                inSlot.shrink(1);
                items.set(1, new ItemStack(SandStormItems.PRESSURIZED_FOSSIL_FLUID_BUCKET));
                fossilFluid -= 1000;
                setChanged();
            } else if (outSlot.is(SandStormItems.PRESSURIZED_FOSSIL_FLUID_BUCKET) && outSlot.getCount() < outSlot.getMaxStackSize()) {
                inSlot.shrink(1);
                outSlot.grow(1);
                fossilFluid -= 1000;
                setChanged();
            }
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return new int[]{0};
        }
        if (side == Direction.DOWN) {
            return new int[]{1, 2, 3, 4, 5, 6, 7};
        }
        return new int[]{2, 3, 4, 5, 6, 7};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 0 && stack.is(Items.BUCKET);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 1;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.deep_core_drill");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new DeepCoreDrillMenu(SandStormMenus.DEEP_CORE_DRILL_MENU, syncId, playerInventory, this, drillDataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("fossilFluid", this.fossilFluid);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fossilFluid = input.getIntOr("fossilFluid", 0);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("FossilFluid", fossilFluid);
        return tag;
    }
}
