package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.LithoPlasmaExtractorBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.gui.LithoPlasmaExtractorMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LithoPlasmaExtractorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int MAX_ENERGY = 250000;
    public static final int ENERGY_COST_PER_TICK = 150;
    public static final int CYCLE_TICKS = 120;

    public static final int SLOT_SALT_IN = 0;
    public static final int SLOT_CANISTER_IN = 1;
    public static final int SLOT_BATTERY = 2;
    public static final int SLOT_LITHIUM_OUT = 3;
    public static final int SLOT_ALLOY_OUT = 4;
    public static final int SLOT_BYPRODUCT_OUT = 5;

    private static final int[] SLOTS_TOP = new int[]{SLOT_SALT_IN, SLOT_CANISTER_IN};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_LITHIUM_OUT, SLOT_ALLOY_OUT, SLOT_BYPRODUCT_OUT};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_SALT_IN, SLOT_CANISTER_IN, SLOT_BATTERY, SLOT_LITHIUM_OUT, SLOT_ALLOY_OUT, SLOT_BYPRODUCT_OUT};

    private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int storedEnergy = MAX_ENERGY;
    private int progress = 0;
    private int plasmaConcentration = 0;

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
                case 6 -> plasmaConcentration;
                case 7 -> isExtracting() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> storedEnergy = (storedEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 1 -> storedEnergy = (storedEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> progress = value;
                case 6 -> plasmaConcentration = value;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public LithoPlasmaExtractorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.LITHO_PLASMA_EXTRACTOR_BE, pos, state);
    }

    public LithoPlasmaExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        ItemStack batteryStack = this.items.get(SLOT_BATTERY);
        int fuelValue = BaseMachineBlockEntity.getFuelEnergy(batteryStack);
        if (fuelValue > 0 && this.storedEnergy + fuelValue <= MAX_ENERGY) {
            this.storedEnergy += fuelValue;
            batteryStack.shrink(1);
            setChanged();
        }

        long wptCharge = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
        if (wptCharge > 0 && this.storedEnergy < MAX_ENERGY) {
            int toAdd = (int) Math.min(500, wptCharge * 10);
            this.storedEnergy = Math.min(MAX_ENERGY, this.storedEnergy + Math.max(1, toAdd));
        }

        boolean canProcess = hasInputMaterials() && this.storedEnergy >= ENERGY_COST_PER_TICK && hasOutputRoom();
        if (canProcess) {
            this.storedEnergy -= ENERGY_COST_PER_TICK;
            this.progress++;
            this.plasmaConcentration = Math.min(100, (this.progress * 100) / CYCLE_TICKS);

            if (this.progress >= CYCLE_TICKS) {
                this.progress = 0;
                processCentrifugeExtraction(level, pos);
            }

            if (level instanceof ServerLevel serverLevel && serverLevel.getRandom().nextFloat() < 0.35f) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 2, 0.2, 0.1, 0.2, 0.05);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.1, 0.1, 0.1, 0.02);
            }
        } else {
            if (this.progress > 0) {
                this.progress--;
                this.plasmaConcentration = Math.max(0, (this.progress * 100) / CYCLE_TICKS);
            }
        }

        boolean active = canProcess;
        if (state.hasProperty(LithoPlasmaExtractorBlock.LIT) && state.getValue(LithoPlasmaExtractorBlock.LIT) != active) {
            level.setBlock(pos, state.setValue(LithoPlasmaExtractorBlock.LIT, active), 3);
            setChanged();
        }
    }

    private boolean hasInputMaterials() {
        ItemStack salt = this.items.get(SLOT_SALT_IN);
        ItemStack canister = this.items.get(SLOT_CANISTER_IN);
        boolean validSalt = !salt.isEmpty() && salt.is(SandStormItems.RAW_LITHIUM_SALTS) && salt.getCount() >= 2;
        boolean validCanister = !canister.isEmpty() && (canister.is(SandStormItems.BIO_COOLANT_CANISTER) || canister.is(SandStormItems.EMPTY_CARTRIDGE));
        return validSalt && validCanister;
    }

    private boolean hasOutputRoom() {
        ItemStack lithiumSlot = this.items.get(SLOT_LITHIUM_OUT);
        return lithiumSlot.isEmpty() || (lithiumSlot.is(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE) && lithiumSlot.getCount() < lithiumSlot.getMaxStackSize());
    }

    private void processCentrifugeExtraction(Level level, BlockPos pos) {
        ItemStack salt = this.items.get(SLOT_SALT_IN);
        ItemStack canister = this.items.get(SLOT_CANISTER_IN);

        salt.shrink(2);
        if (salt.isEmpty()) {
            this.items.set(SLOT_SALT_IN, ItemStack.EMPTY);
        }

        canister.shrink(1);
        if (canister.isEmpty()) {
            this.items.set(SLOT_CANISTER_IN, ItemStack.EMPTY);
        }

        ItemStack lithiumCapsule = new ItemStack(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE, 1);
        depositSlot(SLOT_LITHIUM_OUT, lithiumCapsule);

        if (level.getRandom().nextFloat() < 0.25f) {
            depositSlot(SLOT_ALLOY_OUT, new ItemStack(SandStormItems.MANTLE_ALLOY_INGOT, 1));
        }

        if (level.getRandom().nextFloat() < 0.40f) {
            depositSlot(SLOT_BYPRODUCT_OUT, new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 1));
        }

        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.2f, 1.6f);
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.8f, 1.3f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 12, 0.25, 0.15, 0.25, 0.05);
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 6, 0.15, 0.15, 0.15, 0.03);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 6, 0.2, 0.1, 0.2, 0.02);
        }
        setChanged();
    }

    private void depositSlot(int slot, ItemStack stack) {
        ItemStack current = this.items.get(slot);
        if (current.isEmpty()) {
            this.items.set(slot, stack);
            setChanged();
        } else if (ItemStack.isSameItemSameComponents(current, stack) && current.getCount() + stack.getCount() <= current.getMaxStackSize()) {
            current.grow(stack.getCount());
            setChanged();
        }
    }

    public boolean isExtracting() {
        return hasInputMaterials() && this.storedEnergy >= ENERGY_COST_PER_TICK && hasOutputRoom();
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.max(0, Math.min(MAX_ENERGY, energy));
        setChanged();
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setChanged();
    }

    public int getPlasmaConcentration() {
        return this.plasmaConcentration;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public int getContainerSize() {
        return 6;
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
        if (slot == SLOT_SALT_IN) {
            return stack.is(SandStormItems.RAW_LITHIUM_SALTS);
        }
        if (slot == SLOT_CANISTER_IN) {
            return stack.is(SandStormItems.BIO_COOLANT_CANISTER) || stack.is(SandStormItems.EMPTY_CARTRIDGE);
        }
        if (slot == SLOT_BATTERY) {
            return BaseMachineBlockEntity.getFuelEnergy(stack) > 0;
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot >= SLOT_LITHIUM_OUT || slot == SLOT_BATTERY;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.litho_plasma_extractor");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new LithoPlasmaExtractorMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("progress", this.progress);
        output.putInt("plasmaConcentration", this.plasmaConcentration);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.storedEnergy = input.getIntOr("storedEnergy", MAX_ENERGY);
        this.progress = input.getIntOr("progress", 0);
        this.plasmaConcentration = input.getIntOr("plasmaConcentration", 0);
    }
}
