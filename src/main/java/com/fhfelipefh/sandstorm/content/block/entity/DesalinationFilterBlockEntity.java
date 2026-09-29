package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.AquiferBeetleEntity;
import com.fhfelipefh.sandstorm.content.gui.DesalinationFilterMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DesalinationFilterBlockEntity extends BaseMachineBlockEntity {
    private static final int[] SLOTS_TOP = new int[]{0, 3, 4};
    private static final int[] SLOTS_BOTTOM = new int[]{1, 2, 3};
    private static final int[] SLOTS_SIDES = new int[]{0, 3, 1, 2, 4};

    private int waterInput = 0;
    private int waterOutput = 0;
    private static final int MAX_WATER = 4000;

    private final Storage<FluidVariant> inputFluidStorage = new Storage<>() {
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
            long currentDroplets = (long) waterInput * dropletsPerMb;
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
            int prevWater = waterInput;
            waterInput += addedMb;
            transaction.addCloseCallback((tx, result) -> {
                if (result.wasAborted()) {
                    waterInput = prevWater;
                } else {
                    setChanged();
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
            return Collections.<StorageView<FluidVariant>>singletonList(new StorageView<>() {
                @Override
                public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                    return 0;
                }

                @Override
                public boolean isResourceBlank() {
                    return waterInput <= 0;
                }

                @Override
                public FluidVariant getResource() {
                    return waterInput > 0 ? new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY) : new FluidVariantImpl(Fluids.EMPTY, DataComponentPatch.EMPTY);
                }

                @Override
                public long getAmount() {
                    return (long) waterInput * (FluidConstants.BUCKET / 1000);
                }

                @Override
                public long getCapacity() {
                    return (long) MAX_WATER * (FluidConstants.BUCKET / 1000);
                }
            }).iterator();
        }
    };

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
            long currentDroplets = (long) waterOutput * dropletsPerMb;
            if (currentDroplets <= 0) {
                return 0;
            }
            long toExtract = Math.min(maxAmount, currentDroplets);
            int drainedMb = (int) (toExtract / dropletsPerMb);
            if (drainedMb <= 0) {
                return 0;
            }
            long actualDrained = (long) drainedMb * dropletsPerMb;
            int prevWater = waterOutput;
            waterOutput -= drainedMb;
            transaction.addCloseCallback((tx, result) -> {
                if (result.wasAborted()) {
                    waterOutput = prevWater;
                } else {
                    setChanged();
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
                    return waterOutput <= 0;
                }

                @Override
                public FluidVariant getResource() {
                    return waterOutput > 0 ? new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY) : new FluidVariantImpl(Fluids.EMPTY, DataComponentPatch.EMPTY);
                }

                @Override
                public long getAmount() {
                    return (long) waterOutput * (FluidConstants.BUCKET / 1000);
                }

                @Override
                public long getCapacity() {
                    return (long) MAX_WATER * (FluidConstants.BUCKET / 1000);
                }
            }).iterator();
        }
    };

    private final Storage<FluidVariant> combinedFluidStorage = new CombinedStorage<>(List.of(inputFluidStorage, outputFluidStorage));

    private final ContainerData desalinationDataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> maxEnergy;
                case 2 -> progress;
                case 3 -> maxProgress;
                case 4 -> wptConnected ? 1 : 0;
                case 5 -> isProcessing() ? 1 : 0;
                case 6 -> waterInput;
                case 7 -> waterOutput;
                case 8 -> MAX_WATER;
                case 9 -> hasFilterCartridge() ? 1 : 0;
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
                case 6 -> waterInput = value;
                case 7 -> waterOutput = value;
            }
        }

        @Override
        public int getCount() {
            return 10;
        }
    };

    public DesalinationFilterBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.DESALINATION_FILTER_BE, pos, state);
    }

    public DesalinationFilterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 5, 80);
    }

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        if (side == Direction.UP) {
            return inputFluidStorage;
        }
        if (side == Direction.DOWN) {
            return outputFluidStorage;
        }
        return combinedFluidStorage;
    }

    public boolean addWaterInput(int amount) {
        if (amount <= 0 || waterInput >= MAX_WATER) {
            return false;
        }
        waterInput = Math.min(MAX_WATER, waterInput + amount);
        setChanged();
        return true;
    }

    public int getWaterInput() {
        return waterInput;
    }

    public int getWaterOutput() {
        return waterOutput;
    }

    public void setWaterOutput(int waterOutput) {
        this.waterOutput = Math.clamp(waterOutput, 0, MAX_WATER);
        setChanged();
    }

    public void setWaterInput(int waterInput) {
        this.waterInput = Math.clamp(waterInput, 0, MAX_WATER);
        setChanged();
    }

    public int getMaxWater() {
        return MAX_WATER;
    }

    public int drainWaterOutput(int amount) {
        if (amount <= 0 || waterOutput <= 0) {
            return 0;
        }
        int drained = Math.min(waterOutput, amount);
        waterOutput -= drained;
        setChanged();
        return drained;
    }

    public boolean hasFilterCartridge() {
        return items.size() > 4 && !items.get(4).isEmpty() && items.get(4).is(SandStormItems.FILTER_CARTRIDGE);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level != null && !level.isClientSide()) {
            if (level.getGameTime() % 20 == 0 && waterInput + 1000 <= MAX_WATER) {
                BlockState aboveState = level.getBlockState(pos.above());
                if (aboveState.is(Blocks.WATER)) {
                    if (aboveState.getFluidState().isSource()) {
                        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
                    }
                    addWaterInput(1000);
                }
            }

            if (level.getGameTime() % 40 == 0 && waterInput + 25 <= MAX_WATER) {
                for (int i = 1; i <= 5; i++) {
                    BlockPos checkPos = pos.above(i);
                    BlockState checkState = level.getBlockState(checkPos);
                    if (checkState.is(Blocks.POINTED_DRIPSTONE)) {
                        Direction tipDir = checkState.getValue(PointedDripstoneBlock.TIP_DIRECTION);
                        if (tipDir == Direction.DOWN) {
                            addWaterInput(25);
                            break;
                        }
                    } else if (!checkState.isAir()) {
                        break;
                    }
                }
            }

            if (canProcess() && hasFilterCartridge() && progress < maxProgress) {
                progress++;
                if (progress % 20 == 0) {
                    AquiferBeetleEntity.alertNearbyBeetles(level, pos, 24.0);
                }
            }
        }

        super.serverTick(level, pos, state);
    }

    @Override
    protected boolean canProcess() {
        boolean hasWater = waterInput >= 250 || items.get(0).is(SandStormItems.BRACKISH_WATER_BOTTLE);
        if (!hasWater) {
            return false;
        }
        ItemStack outWater = items.get(1);
        if (!outWater.isEmpty() && (!outWater.is(SandStormItems.POTABLE_WATER_BOTTLE) || outWater.getCount() >= outWater.getMaxStackSize())) {
            return false;
        }
        ItemStack outSalt = items.get(2);
        if (!outSalt.isEmpty() && (!outSalt.is(SandStormItems.MINERAL_SALT) || outSalt.getCount() + 2 > outSalt.getMaxStackSize())) {
            return false;
        }
        return true;
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }
        if (waterInput >= 250) {
            waterInput -= 250;
        } else {
            items.get(0).shrink(1);
        }

        ItemStack outWater = items.get(1);
        if (outWater.isEmpty()) {
            items.set(1, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE));
        } else {
            outWater.grow(1);
        }

        ItemStack outSalt = items.get(2);
        if (outSalt.isEmpty()) {
            items.set(2, new ItemStack(SandStormItems.MINERAL_SALT, 2));
        } else {
            outSalt.grow(2);
        }

        waterOutput = Math.min(MAX_WATER, waterOutput + 250);

        if (hasFilterCartridge()) {
            ItemStack cartridge = items.get(4);
            cartridge.setDamageValue(cartridge.getDamageValue() + 1);
            if (cartridge.getDamageValue() >= cartridge.getMaxDamage()) {
                items.set(4, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = super.removeItem(slot, amount);
        if (slot == 1 && !result.isEmpty()) {
            waterOutput = Math.max(0, waterOutput - 250 * result.getCount());
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 1) {
            ItemStack current = items.get(slot);
            if (!current.isEmpty()) {
                waterOutput = Math.max(0, waterOutput - 250 * current.getCount());
            }
        }
        return super.removeItemNoUpdate(slot);
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SandStormSoundEvents.DESALINATION_PROCESS;
    }

    @Override
    protected int getBatterySlotIndex() {
        return 3;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.desalination_filter");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new DesalinationFilterMenu(syncId, playerInventory, this, this.desalinationDataAccess);
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
        if (stack.isEmpty()) {
            return false;
        }
        if (slot == 0) {
            return stack.is(SandStormItems.BRACKISH_WATER_BOTTLE) || stack.is(Items.WATER_BUCKET);
        }
        if (slot == 3) {
            return getFuelEnergy(stack) > 0;
        }
        if (slot == 4) {
            return stack.is(SandStormItems.FILTER_CARTRIDGE);
        }
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN) {
            return slot == 3 && getFuelEnergy(stack) > 0;
        }
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot == 1 || slot == 2;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 1 || slot == 2;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("waterInput", this.waterInput);
        output.putInt("waterOutput", this.waterOutput);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.waterInput = input.getIntOr("waterInput", 0);
        this.waterOutput = input.getIntOr("waterOutput", 0);
    }
}
