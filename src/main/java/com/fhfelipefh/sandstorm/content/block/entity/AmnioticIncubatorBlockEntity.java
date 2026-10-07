package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.AmnioticIncubatorBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.AmnioticIncubatorMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class AmnioticIncubatorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int CONTAINER_SIZE = 3;
    public static final int SLOT_WATER_IN = 0;
    public static final int SLOT_WATER_OUT = 1;
    public static final int SLOT_BIOMASS = 2;

    public static final long MAX_ENERGY = 250000L;
    public static final long ENERGY_COST_PER_TICK = 20L;
    public static final int MAX_WATER = 10000;
    public static final int WATER_COST_PER_INCUBATION = 1000;
    public static final int MAX_BIOMASS = 64;
    public static final int DEFAULT_MAX_PROGRESS = 3600;

    public static final String[] SUPPORTED_SPECIES = new String[]{"cow", "sheep", "wolf", "polar_bear", "frog", "rabbit"};

    private static final int[] SLOTS_TOP = new int[]{SLOT_WATER_IN, SLOT_BIOMASS};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_WATER_OUT};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_WATER_IN, SLOT_WATER_OUT, SLOT_BIOMASS};

    private final NonNullList<ItemStack> items;
    private final EnergyStorageComponent energyStorage;
    private int waterAmount = 0;
    private int biomassUnits = 0;
    private int progressTicks = 0;
    private int maxProgressTicks = DEFAULT_MAX_PROGRESS;
    private String selectedSpecies = "cow";
    private boolean autoEcologicalMode = false;
    private int targetPopulationQuota = 4;
    private boolean active = false;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) (energyStorage.getStoredEnergy() & 0xFFFF);
                case 1 -> (int) ((energyStorage.getStoredEnergy() >> 16) & 0xFFFF);
                case 2 -> (int) (MAX_ENERGY & 0xFFFF);
                case 3 -> (int) ((MAX_ENERGY >> 16) & 0xFFFF);
                case 4 -> waterAmount;
                case 5 -> MAX_WATER;
                case 6 -> biomassUnits;
                case 7 -> MAX_BIOMASS;
                case 8 -> progressTicks;
                case 9 -> maxProgressTicks;
                case 10 -> getSpeciesIndex();
                case 11 -> autoEcologicalMode ? 1 : 0;
                case 12 -> targetPopulationQuota;
                case 13 -> active ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 10 -> setSpeciesByIndex(value);
                case 11 -> setAutoEcologicalMode(value == 1);
                case 12 -> setTargetPopulationQuota(value);
            }
        }

        @Override
        public int getCount() {
            return 14;
        }
    };

    public AmnioticIncubatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        this.energyStorage = new EnergyStorageComponent(MAX_ENERGY, 2500L, 2500L);
    }

    public AmnioticIncubatorBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.AMNIOTIC_INCUBATOR_BE, pos, state);
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
                    notifyBlockUpdate();
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
                    return waterAmount <= 0;
                }

                @Override
                public FluidVariant getResource() {
                    return waterAmount > 0 ? new FluidVariantImpl(Fluids.WATER, DataComponentPatch.EMPTY) : new FluidVariantImpl(Fluids.EMPTY, DataComponentPatch.EMPTY);
                }

                @Override
                public long getAmount() {
                    long dropletsPerMb = FluidConstants.BUCKET / 1000;
                    return (long) waterAmount * dropletsPerMb;
                }

                @Override
                public long getCapacity() {
                    long dropletsPerMb = FluidConstants.BUCKET / 1000;
                    return (long) MAX_WATER * dropletsPerMb;
                }
            }).iterator();
        }
    };

    public Storage<FluidVariant> getFluidStorage(Direction direction) {
        return fluidStorage;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.sandstorm.amniotic_incubator");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new AmnioticIncubatorMenu(syncId, playerInventory, this, this.dataAccess);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(int waterAmount) {
        this.waterAmount = Math.max(0, Math.min(MAX_WATER, waterAmount));
        setChanged();
        notifyBlockUpdate();
    }

    public int getBiomassUnits() {
        return biomassUnits;
    }

    public void setBiomassUnits(int biomassUnits) {
        this.biomassUnits = Math.max(0, Math.min(MAX_BIOMASS, biomassUnits));
        setChanged();
        notifyBlockUpdate();
    }

    public int getProgressTicks() {
        return progressTicks;
    }

    public int getMaxProgressTicks() {
        return maxProgressTicks;
    }

    public void setMaxProgressTicks(int ticks) {
        this.maxProgressTicks = Math.max(200, ticks);
        setChanged();
    }

    public String getSelectedSpecies() {
        return selectedSpecies;
    }

    public void setSelectedSpecies(String species) {
        this.selectedSpecies = species != null ? species : "cow";
        setChanged();
        notifyBlockUpdate();
    }

    public int getSpeciesIndex() {
        for (int i = 0; i < SUPPORTED_SPECIES.length; i++) {
            if (SUPPORTED_SPECIES[i].equalsIgnoreCase(this.selectedSpecies)) {
                return i;
            }
        }
        return 0;
    }

    public void setSpeciesByIndex(int index) {
        if (index >= 0 && index < SUPPORTED_SPECIES.length) {
            setSelectedSpecies(SUPPORTED_SPECIES[index]);
        }
    }

    public void cycleSpecies() {
        int currentIndex = getSpeciesIndex();
        int nextIndex = (currentIndex + 1) % SUPPORTED_SPECIES.length;
        setSpeciesByIndex(nextIndex);
    }

    public boolean isAutoEcologicalMode() {
        return autoEcologicalMode;
    }

    public void setAutoEcologicalMode(boolean autoEcologicalMode) {
        this.autoEcologicalMode = autoEcologicalMode;
        setChanged();
        notifyBlockUpdate();
    }

    public int getTargetPopulationQuota() {
        return targetPopulationQuota;
    }

    public void setTargetPopulationQuota(int targetPopulationQuota) {
        this.targetPopulationQuota = Math.max(1, Math.min(32, targetPopulationQuota));
        setChanged();
        notifyBlockUpdate();
    }

    public void cycleTargetPopulationQuota() {
        int next = switch (this.targetPopulationQuota) {
            case 2 -> 4;
            case 4 -> 6;
            case 6 -> 8;
            case 8 -> 12;
            case 12 -> 16;
            default -> 2;
        };
        setTargetPopulationQuota(next);
    }

    public void toggleManualIncubation() {
        if (this.active) {
            this.active = false;
        } else {
            if (this.waterAmount >= WATER_COST_PER_INCUBATION && this.biomassUnits >= 1 && energyStorage.hasEnergy(ENERGY_COST_PER_TICK)) {
                this.active = true;
            }
        }
        setChanged();
        notifyBlockUpdate();
    }

    public boolean isActive() {
        return active;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        handleWaterInput();
        handleBiomassInput();

        if (this.autoEcologicalMode && !this.active && serverLevel.getGameTime() % 100L == 0L) {
            checkAutoIncubation(serverLevel, pos);
        }

        boolean previouslyActive = this.active;

        if (this.active) {
            boolean hasEnergy = energyStorage.hasEnergy(ENERGY_COST_PER_TICK);
            boolean hasResources = this.waterAmount >= WATER_COST_PER_INCUBATION && this.biomassUnits >= 1;

            if (hasEnergy && hasResources) {
                energyStorage.extractEnergy(ENERGY_COST_PER_TICK);
                this.progressTicks++;

                if (serverLevel.getGameTime() % 15L == 0L) {
                    spawnIncubationParticles(serverLevel, pos);
                }

                if (this.progressTicks >= this.maxProgressTicks) {
                    completeIncubation(serverLevel, pos, state);
                }
            } else {
                this.active = false;
            }
        }

        if (previouslyActive != this.active) {
            if (state.hasProperty(AmnioticIncubatorBlock.ACTIVE) && state.getValue(AmnioticIncubatorBlock.ACTIVE) != this.active) {
                level.setBlock(pos, state.setValue(AmnioticIncubatorBlock.ACTIVE, this.active), 3);
            }
            setChanged();
        }
    }

    private void handleWaterInput() {
        ItemStack input = items.get(SLOT_WATER_IN);
        if (input.isEmpty() || waterAmount >= MAX_WATER) {
            return;
        }
        ItemStack output = items.get(SLOT_WATER_OUT);

        if (input.is(Items.WATER_BUCKET)) {
            if (waterAmount + 1000 <= MAX_WATER && canAcceptOutput(output, Items.BUCKET)) {
                waterAmount += 1000;
                input.shrink(1);
                addOutput(output, Items.BUCKET);
                setChanged();
            }
        } else if (input.is(SandStormItems.POTABLE_WATER_BOTTLE)) {
            if (waterAmount + 250 <= MAX_WATER && canAcceptOutput(output, Items.GLASS_BOTTLE)) {
                waterAmount += 250;
                input.shrink(1);
                addOutput(output, Items.GLASS_BOTTLE);
                setChanged();
            }
        } else if (input.is(SandStormItems.BRACKISH_WATER_BOTTLE)) {
            if (waterAmount + 150 <= MAX_WATER && canAcceptOutput(output, Items.GLASS_BOTTLE)) {
                waterAmount += 150;
                input.shrink(1);
                addOutput(output, Items.GLASS_BOTTLE);
                setChanged();
            }
        }
    }

    private void handleBiomassInput() {
        if (this.biomassUnits >= MAX_BIOMASS) {
            return;
        }
        ItemStack stack = items.get(SLOT_BIOMASS);
        if (stack.isEmpty()) {
            return;
        }

        int gained = getBiomassValue(stack);
        if (gained > 0) {
            stack.shrink(1);
            this.biomassUnits = Math.min(MAX_BIOMASS, this.biomassUnits + gained);
            setChanged();
        }
    }

    private int getBiomassValue(ItemStack stack) {
        if (stack.is(SandStormItems.XENO_GRASS_SEEDS) || stack.is(SandStormItems.ANCIENT_SEED)) {
            return 8;
        }
        if (stack.is(Items.WHEAT) || stack.is(Items.CARROT) || stack.is(Items.POTATO) || stack.is(Items.BEETROOT) || stack.is(Items.APPLE)) {
            return 4;
        }
        if (stack.is(Items.WHEAT_SEEDS) || stack.is(Items.PUMPKIN_SEEDS) || stack.is(Items.MELON_SEEDS) || stack.is(Items.BEETROOT_SEEDS)) {
            return 2;
        }
        if (stack.is(Items.KELP) || stack.is(Items.SEAGRASS) || stack.is(Items.DRIED_KELP)) {
            return 3;
        }
        return 0;
    }

    private boolean canAcceptOutput(ItemStack existing, Item item) {
        if (existing.isEmpty()) {
            return true;
        }
        return existing.is(item) && existing.getCount() < existing.getMaxStackSize();
    }

    private void addOutput(ItemStack existing, Item item) {
        if (existing.isEmpty()) {
            items.set(SLOT_WATER_OUT, new ItemStack(item, 1));
        } else if (existing.is(item)) {
            existing.grow(1);
        }
    }

    private void checkAutoIncubation(ServerLevel serverLevel, BlockPos pos) {
        AABB area = new AABB(pos).inflate(32);
        List<Animal> localFauna = serverLevel.getEntitiesOfClass(Animal.class, area);
        long matchingCount = localFauna.stream().filter(a -> matchesSpecies(a, this.selectedSpecies)).count();

        if (matchingCount < this.targetPopulationQuota) {
            if (this.waterAmount >= WATER_COST_PER_INCUBATION && this.biomassUnits >= 1 && energyStorage.hasEnergy(ENERGY_COST_PER_TICK)) {
                this.active = true;
                setChanged();
            }
        }
    }

    private boolean matchesSpecies(Animal animal, String species) {
        return switch (species.toLowerCase()) {
            case "sheep" -> animal.getType() == EntityTypes.SHEEP;
            case "wolf" -> animal.getType() == EntityTypes.WOLF;
            case "polar_bear" -> animal.getType() == EntityTypes.POLAR_BEAR;
            case "frog" -> animal.getType() == EntityTypes.FROG;
            case "rabbit" -> animal.getType() == EntityTypes.RABBIT;
            default -> animal.getType() == EntityTypes.COW;
        };
    }

    private void spawnIncubationParticles(ServerLevel serverLevel, BlockPos pos) {
        serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 4, 0.2, 0.3, 0.2, 0.02);
        serverLevel.sendParticles(ParticleTypes.GLOW, pos.getX() + 0.5, pos.getY() + 1.4, pos.getZ() + 0.5, 2, 0.1, 0.2, 0.1, 0.01);
    }

    private void completeIncubation(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        this.waterAmount = Math.max(0, this.waterAmount - WATER_COST_PER_INCUBATION);
        this.biomassUnits = Math.max(0, this.biomassUnits - 1);
        this.progressTicks = 0;
        this.active = false;

        Direction facing = state.hasProperty(AmnioticIncubatorBlock.FACING) ? state.getValue(AmnioticIncubatorBlock.FACING) : Direction.NORTH;
        BlockPos spawnPos = pos.relative(facing);

        AgeableMob juvenile = createJuvenileEntity(serverLevel, this.selectedSpecies);
        if (juvenile != null) {
            juvenile.setBaby(true);
            juvenile.setPersistenceRequired();
            juvenile.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, facing.toYRot(), 0.0f);
            serverLevel.addFreshEntity(juvenile);
            serverLevel.playSound(null, spawnPos, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 1.0f, 0.8f);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, spawnPos.getX() + 0.5, spawnPos.getY() + 0.6, spawnPos.getZ() + 0.5, 12, 0.4, 0.4, 0.4, 0.05);
        }
        setChanged();
        notifyBlockUpdate();
    }

    private AgeableMob createJuvenileEntity(ServerLevel serverLevel, String species) {
        return switch (species.toLowerCase()) {
            case "sheep" -> EntityTypes.SHEEP.create(serverLevel, EntitySpawnReason.BREEDING);
            case "wolf" -> EntityTypes.WOLF.create(serverLevel, EntitySpawnReason.BREEDING);
            case "polar_bear" -> EntityTypes.POLAR_BEAR.create(serverLevel, EntitySpawnReason.BREEDING);
            case "frog" -> EntityTypes.FROG.create(serverLevel, EntitySpawnReason.BREEDING);
            case "rabbit" -> EntityTypes.RABBIT.create(serverLevel, EntitySpawnReason.BREEDING);
            default -> EntityTypes.COW.create(serverLevel, EntitySpawnReason.BREEDING);
        };
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putLong("storedEnergy", energyStorage.getStoredEnergy());
        output.putInt("waterAmount", this.waterAmount);
        output.putInt("biomassUnits", this.biomassUnits);
        output.putInt("progressTicks", this.progressTicks);
        output.putInt("maxProgressTicks", this.maxProgressTicks);
        output.putString("selectedSpecies", this.selectedSpecies);
        output.putBoolean("autoEcologicalMode", this.autoEcologicalMode);
        output.putInt("targetPopulationQuota", this.targetPopulationQuota);
        output.putBoolean("active", this.active);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        energyStorage.setStoredEnergy(input.getLongOr("storedEnergy", 0L));
        this.waterAmount = input.getIntOr("waterAmount", 0);
        this.biomassUnits = input.getIntOr("biomassUnits", 0);
        this.progressTicks = input.getIntOr("progressTicks", 0);
        this.maxProgressTicks = input.getIntOr("maxProgressTicks", DEFAULT_MAX_PROGRESS);
        this.selectedSpecies = input.getStringOr("selectedSpecies", "cow");
        this.autoEcologicalMode = input.getBooleanOr("autoEcologicalMode", false);
        this.targetPopulationQuota = input.getIntOr("targetPopulationQuota", 4);
        this.active = input.getBooleanOr("active", false);
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
            notifyBlockUpdate();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(items, slot);
        if (!result.isEmpty()) {
            setChanged();
            notifyBlockUpdate();
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize(stack)) {
            stack.setCount(getMaxStackSize(stack));
        }
        setChanged();
        notifyBlockUpdate();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
        notifyBlockUpdate();
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
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        if (slot == SLOT_WATER_IN) {
            return stack.is(Items.WATER_BUCKET)
                    || stack.is(SandStormItems.POTABLE_WATER_BOTTLE)
                    || stack.is(SandStormItems.BRACKISH_WATER_BOTTLE);
        } else if (slot == SLOT_BIOMASS) {
            return getBiomassValue(stack) > 0;
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_WATER_OUT;
    }

    public void notifyBlockUpdate() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
