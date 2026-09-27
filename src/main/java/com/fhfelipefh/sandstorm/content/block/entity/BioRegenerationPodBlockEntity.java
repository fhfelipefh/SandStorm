package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.BioRegenerationPodBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.BioRegenerationPodMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.PlayerSuitSavedData;
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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BioRegenerationPodBlockEntity extends BaseMachineBlockEntity {
    public static final int SLOT_WATER = 0;
    public static final int SLOT_STABILIZER = 1;
    public static final int SLOT_BATTERY = 2;
    public static final int SLOT_OUTPUT = 3;

    private static final int[] SLOTS_TOP = new int[]{SLOT_WATER, SLOT_STABILIZER};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_OUTPUT, SLOT_BATTERY};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_WATER, SLOT_STABILIZER, SLOT_BATTERY};

    private int fluidAmount = 0;
    private final int maxFluid = 4000;
    private int healTimer = 0;
    private int heartRate = 72;
    private int healthPercent = 100;
    private boolean occupied = false;

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

    private final ContainerData podData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> maxEnergy;
                case 2 -> fluidAmount;
                case 3 -> maxFluid;
                case 4 -> wptConnected ? 1 : 0;
                case 5 -> occupied ? 1 : 0;
                case 6 -> heartRate;
                case 7 -> healthPercent;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = value;
                case 1 -> maxEnergy = value;
                case 2 -> fluidAmount = value;
                case 4 -> wptConnected = (value == 1);
                case 5 -> occupied = (value == 1);
                case 6 -> heartRate = value;
                case 7 -> healthPercent = value;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public BioRegenerationPodBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.BIO_REGENERATION_POD_BE, pos, state);
    }

    public BioRegenerationPodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 4, 100);
        this.energyCostPerTick = 20;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BioRegenerationPodBlockEntity be) {
        be.tickServerPod((ServerLevel) level, pos, state);
    }

    private void tickServerPod(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        super.serverTick(serverLevel, pos, state);
        processFluidRefill();

        List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(Player.class, new AABB(pos).inflate(0.6));
        Player patient = null;
        for (Player p : nearbyPlayers) {
            if (p.isPassenger() || p.getBoundingBox().intersects(new AABB(pos))) {
                patient = p;
                break;
            }
        }

        boolean isCurrentlyOccupied = (patient != null);
        if (this.occupied != isCurrentlyOccupied) {
            this.occupied = isCurrentlyOccupied;
            serverLevel.setBlock(pos, state.setValue(BioRegenerationPodBlock.OCCUPIED, occupied), Block.UPDATE_ALL);
            setChanged();
        }

        if (patient != null) {
            this.healthPercent = Math.max(1, (int) ((patient.getHealth() / patient.getMaxHealth()) * 100));
            this.heartRate = 60 + (int) ((1.0f - (patient.getHealth() / patient.getMaxHealth())) * 50);

            healTimer++;
            if (healTimer >= 10) {
                healTimer = 0;
                applyTreatment(patient, serverLevel, pos);
            }
        } else {
            this.healthPercent = 100;
            this.heartRate = 72;
            healTimer = 0;
        }
    }

    private void processFluidRefill() {
        if (fluidAmount >= maxFluid) {
            return;
        }

        ItemStack waterStack = items.get(SLOT_WATER);
        if (waterStack.isEmpty()) {
            return;
        }

        ItemStack outputStack = items.get(SLOT_OUTPUT);
        boolean isPotable = waterStack.is(SandStormItems.POTABLE_WATER_BOTTLE);
        boolean isBrackish = waterStack.is(SandStormItems.BRACKISH_WATER_BOTTLE);
        boolean isVanillaWater = waterStack.is(Items.POTION) || waterStack.is(Items.WATER_BUCKET);

        if (isPotable || isBrackish || isVanillaWater) {
            ItemStack returnBottle = waterStack.is(Items.WATER_BUCKET) ? new ItemStack(Items.BUCKET) : new ItemStack(Items.GLASS_BOTTLE);
            if (outputStack.isEmpty() || (ItemStack.isSameItemSameComponents(outputStack, returnBottle) && outputStack.getCount() < outputStack.getMaxStackSize())) {
                waterStack.shrink(1);
                if (outputStack.isEmpty()) {
                    items.set(SLOT_OUTPUT, returnBottle);
                } else {
                    outputStack.grow(1);
                }
                fluidAmount = Math.min(maxFluid, fluidAmount + 1000);
                setChanged();
            }
        }
    }

    private void applyTreatment(Player patient, ServerLevel serverLevel, BlockPos pos) {
        if (this.energy < 20) {
            return;
        }

        this.energy -= 20;

        float healAmount = 2.5f;
        ItemStack stabilizer = items.get(SLOT_STABILIZER);
        if (!stabilizer.isEmpty()) {
            if (stabilizer.is(SandStormItems.CHITOSAN_EXTRACT) || stabilizer.is(SandStormItems.BIOFOAM_CARTRIDGE)) {
                healAmount = 5.0f;
                stabilizer.shrink(1);
            } else if (stabilizer.is(SandStormItems.TREHALOSE_SUGAR) || stabilizer.is(SandStormItems.RADIOPROTECTIVE_MELANIN)) {
                healAmount = 4.0f;
                stabilizer.shrink(1);
            }
        }

        patient.heal(healAmount);
        patient.clearFire();
        patient.setAirSupply(patient.getMaxAirSupply());

        patient.removeEffect(MobEffects.POISON);
        patient.removeEffect(MobEffects.WITHER);
        patient.removeEffect(MobEffects.SLOWNESS);
        patient.removeEffect(MobEffects.NAUSEA);
        patient.removeEffect(MobEffects.BLINDNESS);
        patient.removeEffect(MobEffects.HUNGER);
        patient.removeEffect(MobEffects.WEAKNESS);

        if (patient.getFoodData().needsFood()) {
            patient.getFoodData().eat(1, 0.5f);
        }

        PlayerSuitSavedData.get(serverLevel).setSuitData(patient.getUUID(), 50000L, 37.0);

        if (fluidAmount > 0) {
            fluidAmount = Math.max(0, fluidAmount - 20);
        }

        if (serverLevel.getRandom().nextInt(3) == 0) {
            serverLevel.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.5f, 1.2f);
        }

        setChanged();
    }

    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return fluidStorage;
    }

    public ContainerData getPodData() {
        return podData;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public void setFluidAmount(int fluidAmount) {
        this.fluidAmount = Math.min(maxFluid, Math.max(0, fluidAmount));
        setChanged();
    }

    public int getMaxFluid() {
        return maxFluid;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
        setChanged();
    }

    @Override
    public boolean canProcess() {
        return occupied || (fluidAmount < maxFluid && !items.get(SLOT_WATER).isEmpty());
    }

    @Override
    public void processRecipe() {
        processFluidRefill();
    }

    @Override
    protected int getBatterySlotIndex() {
        return SLOT_BATTERY;
    }

    @Override
    protected SoundEvent getProcessSound() {
        return SoundEvents.BEACON_AMBIENT;
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
            return BaseMachineBlockEntity.getFuelEnergy(stack) > 0;
        }
        if (slot == SLOT_WATER) {
            return stack.is(SandStormItems.POTABLE_WATER_BOTTLE) || stack.is(SandStormItems.BRACKISH_WATER_BOTTLE) || stack.is(Items.WATER_BUCKET);
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.bio_regeneration_pod");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new BioRegenerationPodMenu(syncId, playerInventory, this, this.podData);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("FluidAmount", fluidAmount);
        output.putBoolean("Occupied", occupied);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidAmount = input.getIntOr("FluidAmount", 0);
        this.occupied = input.getBooleanOr("Occupied", false);
    }
}
