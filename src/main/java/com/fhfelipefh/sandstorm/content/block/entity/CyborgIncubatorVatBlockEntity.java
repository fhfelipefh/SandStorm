package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.CyborgIncubatorVatBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.CyborgIncubatorMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collections;
import java.util.Iterator;

public class CyborgIncubatorVatBlockEntity extends BaseMachineBlockEntity {
    public static final int SLOT_CHASSIS = 0;
    public static final int SLOT_MYOMER = 1;
    public static final int SLOT_NEURAL = 2;
    public static final int SLOT_COOLANT = 3;
    public static final int SLOT_BATTERY = 4;
    public static final int SLOT_OUTPUT = 5;

    private static final int[] SLOTS_TOP = new int[]{SLOT_CHASSIS, SLOT_MYOMER, SLOT_NEURAL, SLOT_COOLANT};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_OUTPUT, SLOT_BATTERY};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_CHASSIS, SLOT_MYOMER, SLOT_NEURAL, SLOT_COOLANT, SLOT_BATTERY};

    private int fluidAmount = 0;
    private final int maxFluid = 8000;
    private int currentStage = 0;
    private int tissueCompatibility = 0;

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
            int spaceMb = maxFluid - fluidAmount;
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
            return 0;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            return Collections.emptyIterator();
        }
    };

    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energy;
                case 3 -> maxEnergy;
                case 4 -> fluidAmount;
                case 5 -> maxFluid;
                case 6 -> currentStage;
                case 7 -> tissueCompatibility;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int val) {
            switch (index) {
                case 0 -> progress = val;
                case 1 -> maxProgress = val;
                case 2 -> energy = val;
                case 3 -> maxEnergy = val;
                case 4 -> fluidAmount = val;
                case 6 -> currentStage = val;
                case 7 -> tissueCompatibility = val;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public CyborgIncubatorVatBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.CYBORG_INCUBATOR_VAT_BE, pos, state);
    }

    public CyborgIncubatorVatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 6, 200);
        this.maxEnergy = 25000;
        this.energyCostPerTick = 25;
    }

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return fluidStorage;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public void setFluidAmount(int amount) {
        this.fluidAmount = Math.clamp(amount, 0, maxFluid);
        setChanged();
    }

    public int getMaxFluid() {
        return maxFluid;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public int getTissueCompatibility() {
        return tissueCompatibility;
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    private int computeGestationStage() {
        boolean hasChassis = items.get(SLOT_CHASSIS).is(SandStormItems.BIOMECHANICAL_CHASSIS_FRAME);
        boolean hasMyomer = items.get(SLOT_MYOMER).is(SandStormItems.SYNTHETIC_MYOMER_BUNDLE);
        boolean hasNeural = items.get(SLOT_NEURAL).is(SandStormItems.BIO_NEURAL_CORE);
        boolean hasCoolant = items.get(SLOT_COOLANT).is(SandStormItems.BIO_COOLANT_CANISTER)
                || items.get(SLOT_COOLANT).is(SandStormItems.OSMOLYTE_GLYCEROL);

        if (hasChassis && hasMyomer && hasNeural && hasCoolant) {
            return 4;
        }
        if (hasChassis && hasMyomer && hasNeural) {
            return 3;
        }
        if (hasChassis && hasMyomer) {
            return 2;
        }
        if (hasChassis) {
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean canProcess() {
        if (computeGestationStage() < 4) {
            return false;
        }
        if (fluidAmount < 1000) {
            return false;
        }
        if (energy < energyCostPerTick) {
            return false;
        }
        ItemStack out = items.get(SLOT_OUTPUT);
        if (out.isEmpty()) {
            return true;
        }
        return out.is(SandStormItems.ASSEMBLED_CYBORG_FRAME) && out.getCount() < out.getMaxStackSize();
    }

    @Override
    protected void processRecipe() {
        if (!canProcess()) {
            return;
        }
        items.get(SLOT_CHASSIS).shrink(1);
        items.get(SLOT_MYOMER).shrink(1);
        items.get(SLOT_NEURAL).shrink(1);
        items.get(SLOT_COOLANT).shrink(1);
        fluidAmount = Math.max(0, fluidAmount - 1000);

        ItemStack out = items.get(SLOT_OUTPUT);
        if (out.isEmpty()) {
            items.set(SLOT_OUTPUT, new ItemStack(SandStormItems.ASSEMBLED_CYBORG_FRAME));
        } else {
            out.grow(1);
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
    }

    public void tickServer(ServerLevel level, BlockPos pos, BlockState state) {
        handleFluidInputs();
        super.serverTick(level, pos, state);

        int stage = computeGestationStage();
        this.currentStage = stage;

        boolean active = canProcess();
        if (active) {
            this.tissueCompatibility = 95 + (int) ((progress / (double) maxProgress) * 5.0);
        } else {
            this.tissueCompatibility = stage > 0 ? 90 + stage : 0;
        }

        if (state.getValue(CyborgIncubatorVatBlock.POWERED) != active || state.getValue(CyborgIncubatorVatBlock.STAGE) != stage) {
            level.setBlock(pos, state.setValue(CyborgIncubatorVatBlock.POWERED, active).setValue(CyborgIncubatorVatBlock.STAGE, stage), 3);
        }
    }

    private void handleFluidInputs() {
        ItemStack coolantSlot = items.get(SLOT_COOLANT);
        if (coolantSlot.is(Items.WATER_BUCKET) && fluidAmount + 1000 <= maxFluid) {
            fluidAmount += 1000;
            items.set(SLOT_COOLANT, new ItemStack(Items.BUCKET));
            setChanged();
        } else if (coolantSlot.is(SandStormItems.POTABLE_WATER_BOTTLE) && fluidAmount + 250 <= maxFluid) {
            fluidAmount += 250;
            items.set(SLOT_COOLANT, new ItemStack(Items.GLASS_BOTTLE));
            setChanged();
        }
    }

    @Override
    protected int getBatterySlotIndex() {
        return SLOT_BATTERY;
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.BREWING_STAND_BREW;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return SLOTS_TOP;
        }
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        if (slot == SLOT_OUTPUT) {
            return false;
        }
        if (slot == SLOT_BATTERY) {
            return stack.is(Items.REDSTONE) || stack.is(Items.REDSTONE_BLOCK);
        }
        if (slot == SLOT_CHASSIS) {
            return stack.is(SandStormItems.BIOMECHANICAL_CHASSIS_FRAME);
        }
        if (slot == SLOT_MYOMER) {
            return stack.is(SandStormItems.SYNTHETIC_MYOMER_BUNDLE);
        }
        if (slot == SLOT_NEURAL) {
            return stack.is(SandStormItems.BIO_NEURAL_CORE);
        }
        if (slot == SLOT_COOLANT) {
            return stack.is(SandStormItems.BIO_COOLANT_CANISTER)
                    || stack.is(SandStormItems.OSMOLYTE_GLYCEROL)
                    || stack.is(Items.WATER_BUCKET)
                    || stack.is(SandStormItems.POTABLE_WATER_BOTTLE);
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_OUTPUT || slot == SLOT_BATTERY;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.cyborg_incubator_vat");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CyborgIncubatorMenu(containerId, playerInventory, this, this.containerData);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidAmount = input.getIntOr("FluidAmount", 0);
        this.currentStage = input.getIntOr("CurrentStage", 0);
        this.tissueCompatibility = input.getIntOr("TissueCompatibility", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("FluidAmount", this.fluidAmount);
        output.putInt("CurrentStage", this.currentStage);
        output.putInt("TissueCompatibility", this.tissueCompatibility);
    }
}
