package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SupercriticalHeatExchangerBlock;
import com.fhfelipefh.sandstorm.content.gui.SupercriticalHeatExchangerMenu;
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

public class SupercriticalHeatExchangerBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int MAX_ENERGY = 1000000;
    public static final int MAX_WATER = 8000;
    public static final int BASE_GEN_RATE = 2500;
    public static final int FIN_GEN_RATE = 5000;
    public static final int LITHIUM_GEN_RATE = 10000;

    public static final int SLOT_WATER_IN = 0;
    public static final int SLOT_WATER_OUT = 1;
    public static final int SLOT_THERMAL_CORE = 2;
    public static final int SLOT_BATTERY = 3;

    private static final int[] SLOTS_TOP = new int[]{SLOT_WATER_IN, SLOT_THERMAL_CORE};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_WATER_OUT, SLOT_BATTERY};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_WATER_IN, SLOT_WATER_OUT, SLOT_THERMAL_CORE, SLOT_BATTERY};

    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private int storedEnergy = 0;
    private int waterAmount = 0;
    private int currentGenRate = 0;
    private int steamPressure = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEnergy & 0xFFFF;
                case 1 -> (storedEnergy >> 16) & 0xFFFF;
                case 2 -> MAX_ENERGY & 0xFFFF;
                case 3 -> (MAX_ENERGY >> 16) & 0xFFFF;
                case 4 -> waterAmount;
                case 5 -> currentGenRate;
                case 6 -> steamPressure;
                case 7 -> isOperating() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> storedEnergy = (storedEnergy & ~0xFFFF) | (value & 0xFFFF);
                case 1 -> storedEnergy = (storedEnergy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> waterAmount = value;
                case 5 -> currentGenRate = value;
                case 6 -> steamPressure = value;
            }
        }

        @Override
        public int getCount() {
            return 8;
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
            if (!resource.isOf(Fluids.WATER) || maxAmount <= 0) {
                return 0;
            }
            long dropletsPerMb = FluidConstants.BUCKET / 1000;
            int spaceMb = MAX_WATER - waterAmount;
            if (spaceMb <= 0) {
                return 0;
            }
            long spaceDroplets = (long) spaceMb * dropletsPerMb;
            long acceptedDroplets = Math.min(maxAmount, spaceDroplets);
            int acceptedMb = (int) (acceptedDroplets / dropletsPerMb);
            if (acceptedMb > 0) {
                transaction.addCloseCallback((txn, result) -> {
                    if (result.wasCommitted()) {
                        waterAmount += acceptedMb;
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

    public SupercriticalHeatExchangerBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.SUPERCRITICAL_HEAT_EXCHANGER_BE, pos, state);
    }

    public SupercriticalHeatExchangerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        handleWaterBucket();

        boolean operating = this.waterAmount > 0 && this.storedEnergy < MAX_ENERGY;
        if (operating) {
            int rate = calculateGenerationRate();
            this.currentGenRate = rate;
            this.storedEnergy = Math.min(MAX_ENERGY, this.storedEnergy + rate);
            this.waterAmount = Math.max(0, this.waterAmount - 5);
            this.steamPressure = Math.min(100, (this.currentGenRate * 100) / LITHIUM_GEN_RATE);

            if (level instanceof ServerLevel serverLevel && serverLevel.getRandom().nextFloat() < 0.25f) {
                serverLevel.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 2, 0.1, 0.2, 0.1, 0.05);
                serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 1, 0.1, 0.1, 0.1, 0.02);
            }

            if (level.getGameTime() % 40 == 0) {
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.6f, 1.4f);
            }
        } else {
            this.currentGenRate = 0;
            this.steamPressure = Math.max(0, this.steamPressure - 2);
        }

        boolean active = operating;
        if (state.hasProperty(SupercriticalHeatExchangerBlock.LIT) && state.getValue(SupercriticalHeatExchangerBlock.LIT) != active) {
            level.setBlock(pos, state.setValue(SupercriticalHeatExchangerBlock.LIT, active), 3);
            setChanged();
        }
    }

    private void handleWaterBucket() {
        ItemStack in = this.items.get(SLOT_WATER_IN);
        if (!in.isEmpty() && in.is(Items.WATER_BUCKET)) {
            if (this.waterAmount + 1000 <= MAX_WATER) {
                ItemStack out = this.items.get(SLOT_WATER_OUT);
                if (out.isEmpty()) {
                    this.items.set(SLOT_WATER_OUT, new ItemStack(Items.BUCKET));
                    in.shrink(1);
                    this.waterAmount += 1000;
                    setChanged();
                } else if (out.is(Items.BUCKET) && out.getCount() < out.getMaxStackSize()) {
                    out.grow(1);
                    in.shrink(1);
                    this.waterAmount += 1000;
                    setChanged();
                }
            }
        }
    }

    private int calculateGenerationRate() {
        ItemStack core = this.items.get(SLOT_THERMAL_CORE);
        if (!core.isEmpty()) {
            if (core.is(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE)) {
                return LITHIUM_GEN_RATE;
            }
            if (core.is(SandStormItems.THERMAL_RADIATOR_FIN)) {
                return FIN_GEN_RATE;
            }
        }
        return BASE_GEN_RATE;
    }

    public boolean isOperating() {
        return this.waterAmount > 0 && this.storedEnergy < MAX_ENERGY;
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.max(0, Math.min(MAX_ENERGY, energy));
        setChanged();
    }

    public int getWaterAmount() {
        return this.waterAmount;
    }

    public void setWaterAmount(int water) {
        this.waterAmount = Math.max(0, Math.min(MAX_WATER, water));
        setChanged();
    }

    public int getCurrentGenRate() {
        return this.currentGenRate;
    }

    public int getSteamPressure() {
        return this.steamPressure;
    }

    public Storage<FluidVariant> getFluidStorage(Direction direction) {
        return this.fluidStorage;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public int getContainerSize() {
        return 4;
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
        if (slot == SLOT_WATER_IN) {
            return stack.is(Items.WATER_BUCKET);
        }
        if (slot == SLOT_THERMAL_CORE) {
            return stack.is(SandStormItems.THERMAL_RADIATOR_FIN) || stack.is(SandStormItems.SUPERHEATED_LITHIUM_CAPSULE);
        }
        return slot == SLOT_BATTERY;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == SLOT_WATER_OUT || slot == SLOT_BATTERY;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.sandstorm.supercritical_heat_exchanger");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SupercriticalHeatExchangerMenu(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("storedEnergy", this.storedEnergy);
        output.putInt("waterAmount", this.waterAmount);
        output.putInt("currentGenRate", this.currentGenRate);
        output.putInt("steamPressure", this.steamPressure);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.storedEnergy = input.getIntOr("storedEnergy", 0);
        this.waterAmount = input.getIntOr("waterAmount", 0);
        this.currentGenRate = input.getIntOr("currentGenRate", 0);
        this.steamPressure = input.getIntOr("steamPressure", 0);
    }
}
