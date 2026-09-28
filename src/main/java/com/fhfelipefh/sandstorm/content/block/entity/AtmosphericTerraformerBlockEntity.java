package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import java.util.Collections;
import java.util.Iterator;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import com.fhfelipefh.sandstorm.content.gui.AtmosphericTerraformerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class AtmosphericTerraformerBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int CONTAINER_SIZE = 7;
    public static final int SLOT_WATER_IN = 0;
    public static final int SLOT_WATER_OUT = 1;
    public static final int SLOT_MINERAL = 2;
    public static final int SLOT_SEEDS = 3;
    public static final int SLOT_SAPLINGS = 4;
    public static final int SLOT_FUEL = 5;
    public static final int SLOT_UPGRADE = 6;

    public static final int TIER1_RADIUS = 24;
    public static final int TIER2_RADIUS = 48;
    public static final int TIER3_RADIUS = 80;

    public static final long TIER1_CAPACITY = 250000L;
    public static final long TIER2_CAPACITY = 500000L;
    public static final long TIER3_CAPACITY = 1000000L;

    public static final int TIER1_WATER_CAPACITY = 50000;
    public static final int TIER2_WATER_CAPACITY = 100000;
    public static final int TIER3_WATER_CAPACITY = 200000;

    public static final int TIER1_ENERGY_COST = 40;
    public static final int TIER2_ENERGY_COST = 100;
    public static final int TIER3_ENERGY_COST = 250;

    public static final int TIER1_WATER_COST = 2;
    public static final int TIER2_WATER_COST = 4;
    public static final int TIER3_WATER_COST = 8;

    private static final int[] SLOTS_TOP = new int[]{SLOT_WATER_IN, SLOT_MINERAL, SLOT_SEEDS, SLOT_SAPLINGS, SLOT_FUEL};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_WATER_OUT};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_MINERAL, SLOT_SEEDS, SLOT_SAPLINGS, SLOT_FUEL, SLOT_UPGRADE};

    public static final int DISSIPATION_DURATION = 300;

    private final NonNullList<ItemStack> items;
    private final EnergyStorageComponent energyStorage;
    private int tier = 1;
    private int waterAmount = 0;
    private int mineralUnits = 0;
    private int seedUnits = 0;
    private boolean active = false;
    private boolean lightningEnabled = false;
    private int dissipatingTicks = 0;
    private int soundCooldown = 0;
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) (energyStorage.getStoredEnergy() & 0xFFFF);
                case 1 -> (int) ((energyStorage.getStoredEnergy() >> 16) & 0xFFFF);
                case 2 -> (int) (getMaxEnergy() & 0xFFFF);
                case 3 -> (int) ((getMaxEnergy() >> 16) & 0xFFFF);
                case 4 -> waterAmount & 0xFFFF;
                case 5 -> (waterAmount >> 16) & 0xFFFF;
                case 6 -> getMaxWater() & 0xFFFF;
                case 7 -> (getMaxWater() >> 16) & 0xFFFF;
                case 8 -> mineralUnits;
                case 9 -> seedUnits;
                case 10 -> getSaplingCount();
                case 11 -> tier;
                case 12 -> active ? 1 : 0;
                case 13 -> lightningEnabled ? 1 : 0;
                case 14 -> dissipatingTicks;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 11 -> setTier(value);
                case 13 -> setLightningEnabled(value == 1);
            }
        }

        @Override
        public int getCount() {
            return 15;
        }
    };

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.sandstorm.atmospheric_terraformer");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new AtmosphericTerraformerMenu(syncId, playerInventory, this, this.dataAccess);
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
            long maxDroplets = (long) getMaxWater() * dropletsPerMb;
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
                    return getResource().isBlank();
                }

                @Override
                public FluidVariant getResource() {
                    return FluidVariant.of(Fluids.WATER);
                }

                @Override
                public long getAmount() {
                    long dropletsPerMb = FluidConstants.BUCKET / 1000;
                    return (long) waterAmount * dropletsPerMb;
                }

                @Override
                public long getCapacity() {
                    long dropletsPerMb = FluidConstants.BUCKET / 1000;
                    return (long) getMaxWater() * dropletsPerMb;
                }
            }).iterator();
        }
    };

    public Storage<FluidVariant> getFluidStorage(Direction direction) {
        return fluidStorage;
    }

    public AtmosphericTerraformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        this.energyStorage = new EnergyStorageComponent(TIER1_CAPACITY, 2500L, 2500L);
    }

    public AtmosphericTerraformerBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.ATMOSPHERIC_TERRAFORMER_BE, pos, state);
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = Mth.clamp(tier, 1, 3);
        long cap = getMaxEnergy();
        long transfer = this.tier >= 3 ? 10000L : this.tier >= 2 ? 5000L : 2500L;
        energyStorage.setCapacity(cap);
        if (waterAmount > getMaxWater()) {
            waterAmount = getMaxWater();
        }
        setChanged();
        notifyBlockUpdate();
    }

    public void cycleTier() {
        int nextTier = (this.tier % 3) + 1;
        setTier(nextTier);
    }

    public int getRadius() {
        return switch (tier) {
            case 2 -> TIER2_RADIUS;
            case 3 -> TIER3_RADIUS;
            default -> TIER1_RADIUS;
        };
    }

    public long getMaxEnergy() {
        return switch (tier) {
            case 2 -> TIER2_CAPACITY;
            case 3 -> TIER3_CAPACITY;
            default -> TIER1_CAPACITY;
        };
    }

    public int getMaxWater() {
        return switch (tier) {
            case 2 -> TIER2_WATER_CAPACITY;
            case 3 -> TIER3_WATER_CAPACITY;
            default -> TIER1_WATER_CAPACITY;
        };
    }

    public int getEnergyCostPerTick() {
        return switch (tier) {
            case 2 -> TIER2_ENERGY_COST;
            case 3 -> TIER3_ENERGY_COST;
            default -> TIER1_ENERGY_COST;
        };
    }

    public int getWaterCostPerTick() {
        return switch (tier) {
            case 2 -> TIER2_WATER_COST;
            case 3 -> TIER3_WATER_COST;
            default -> TIER1_WATER_COST;
        };
    }

    public int getConversionsPerTick() {
        return switch (tier) {
            case 2 -> 2;
            case 3 -> 4;
            default -> 1;
        };
    }

    public long getEnergy() {
        return energyStorage.getStoredEnergy();
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(int waterAmount) {
        this.waterAmount = Mth.clamp(waterAmount, 0, getMaxWater());
        setChanged();
        notifyBlockUpdate();
    }

    public int addWater(int amount) {
        if (amount <= 0) {
            return 0;
        }
        int space = getMaxWater() - waterAmount;
        int accepted = Math.min(amount, space);
        waterAmount += accepted;
        setChanged();
        notifyBlockUpdate();
        return accepted;
    }

    public int getMineralUnits() {
        return mineralUnits;
    }

    public void setMineralUnits(int units) {
        this.mineralUnits = Math.max(0, units);
        setChanged();
        notifyBlockUpdate();
    }

    public void addMineralUnits(int units) {
        if (units > 0) {
            this.mineralUnits += units;
            setChanged();
            notifyBlockUpdate();
        }
    }

    public int getSeedUnits() {
        return seedUnits;
    }

    public void setSeedUnits(int units) {
        this.seedUnits = Math.max(0, units);
        setChanged();
        notifyBlockUpdate();
    }

    public void addSeedUnits(int units) {
        if (units > 0) {
            this.seedUnits += units;
            setChanged();
            notifyBlockUpdate();
        }
    }

    public int getSaplingCount() {
        ItemStack stack = items.get(SLOT_SAPLINGS);
        return stack.isEmpty() ? 0 : stack.getCount();
    }

    public boolean isActive() {
        return active;
    }

    public boolean isLightningEnabled() {
        return lightningEnabled;
    }

    public void setLightningEnabled(boolean enabled) {
        this.lightningEnabled = enabled;
        setChanged();
        notifyBlockUpdate();
    }

    public void toggleLightning() {
        setLightningEnabled(!this.lightningEnabled);
    }

    public boolean isDissipating() {
        return dissipatingTicks > 0;
    }

    public int getDissipatingTicks() {
        return dissipatingTicks;
    }

    public void setDissipatingTicks(int ticks) {
        this.dissipatingTicks = ticks;
        setChanged();
    }

    public void triggerDissipation(ServerLevel serverLevel) {
        if (active || dissipatingTicks > 0) {
            int remainingFade = Math.max(dissipatingTicks, DISSIPATION_DURATION);
            this.dissipatingTicks = remainingFade;
            serverLevel.getWeatherData().setRaining(true);
            serverLevel.getWeatherData().setRainTime(remainingFade);
            serverLevel.getWeatherData().setClearWeatherTime(0);
            serverLevel.getWeatherData().setThundering(false);
            serverLevel.getWeatherData().setThunderTime(0);
        }
    }

    public void setupTier(int targetTier) {
        setTier(targetTier);
        energyStorage.setStoredEnergy(getMaxEnergy());
        this.waterAmount = getMaxWater();
        this.mineralUnits = 1024;
        this.seedUnits = 512;
        this.items.set(SLOT_WATER_IN, new ItemStack(Items.WATER_BUCKET, 1));
        this.items.set(SLOT_WATER_OUT, ItemStack.EMPTY);
        this.items.set(SLOT_MINERAL, new ItemStack(SandStormItems.MINERAL_SALT, 64));
        this.items.set(SLOT_SEEDS, new ItemStack(SandStormItems.XENO_GRASS_SEEDS, 64));
        this.items.set(SLOT_SAPLINGS, new ItemStack(Items.OAK_SAPLING, 64));
        this.items.set(SLOT_FUEL, new ItemStack(Items.REDSTONE_BLOCK, 64));
        if (targetTier >= 3) {
            this.items.set(SLOT_UPGRADE, new ItemStack(SandStormItems.QUANTUM_MIND_MATRIX, 1));
        } else if (targetTier >= 2) {
            this.items.set(SLOT_UPGRADE, new ItemStack(SandStormItems.CIRCUIT_BOARD, 1));
        } else {
            this.items.set(SLOT_UPGRADE, ItemStack.EMPTY);
        }
        setChanged();
        notifyBlockUpdate();
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        handleWaterInput();
        handleMineralInput();
        handleSeedInput();
        handleFuelInput();
        handleUpgradeModule();
        handleWptCharge(level, pos);

        boolean hasEnergy = energyStorage.hasEnergy(getEnergyCostPerTick());
        boolean hasWater = waterAmount > 0;
        boolean hasMinerals = mineralUnits > 0 || !items.get(SLOT_MINERAL).isEmpty();

        boolean canOperate = hasEnergy && hasWater && hasMinerals;

        if (canOperate) {
            dissipatingTicks = 0;
            energyStorage.extractEnergy(getEnergyCostPerTick());
            waterAmount = Math.max(0, waterAmount - getWaterCostPerTick());

            if (!active) {
                active = true;
                notifyBlockUpdate();
            }

            maintainRainWeather(serverLevel);
            spawnMachineAtmosphere(serverLevel, pos);
            performTerraformingConversions(serverLevel, pos);

            if (lightningEnabled && serverLevel.getRandom().nextInt(100) == 0) {
                spawnLightningInRadius(serverLevel, pos);
            }

            if (soundCooldown <= 0) {
                serverLevel.playSound(null, pos, SandStormSoundEvents.TERRAFORMER_HUM, SoundSource.BLOCKS, 0.35f, 1.0f);
                serverLevel.playSound(null, pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.45f, 1.0f);
                soundCooldown = 40;
            } else {
                soundCooldown--;
            }
            setChanged();
        } else {
            if (active) {
                active = false;
                dissipatingTicks = DISSIPATION_DURATION;
                notifyBlockUpdate();
                setChanged();
            }

            if (dissipatingTicks > 0) {
                dissipatingTicks--;
                maintainDissipationWeather(serverLevel);
                spawnDissipatingAtmosphere(serverLevel, pos);
                setChanged();
            }
        }
    }

    private void handleWaterInput() {
        ItemStack input = items.get(SLOT_WATER_IN);
        if (input.isEmpty() || waterAmount >= getMaxWater()) {
            return;
        }
        ItemStack output = items.get(SLOT_WATER_OUT);

        if (input.is(Items.WATER_BUCKET)) {
            if (waterAmount + 1000 <= getMaxWater() && canAcceptOutput(output, Items.BUCKET)) {
                waterAmount += 1000;
                input.shrink(1);
                addOutput(output, Items.BUCKET);
                setChanged();
            }
        } else if (input.is(SandStormItems.POTABLE_WATER_BOTTLE)) {
            if (waterAmount + 250 <= getMaxWater() && canAcceptOutput(output, Items.GLASS_BOTTLE)) {
                waterAmount += 250;
                input.shrink(1);
                addOutput(output, Items.GLASS_BOTTLE);
                setChanged();
            }
        } else if (input.is(SandStormItems.BRACKISH_WATER_BOTTLE)) {
            if (waterAmount + 150 <= getMaxWater() && canAcceptOutput(output, Items.GLASS_BOTTLE)) {
                waterAmount += 150;
                input.shrink(1);
                addOutput(output, Items.GLASS_BOTTLE);
                setChanged();
            }
        } else if (input.is(Items.POTION)) {
            if (waterAmount + 250 <= getMaxWater() && canAcceptOutput(output, Items.GLASS_BOTTLE)) {
                waterAmount += 250;
                input.shrink(1);
                addOutput(output, Items.GLASS_BOTTLE);
                setChanged();
            }
        }
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

    private void handleMineralInput() {
        if (mineralUnits >= 64) {
            return;
        }
        ItemStack stack = items.get(SLOT_MINERAL);
        if (stack.isEmpty()) {
            return;
        }
        if (stack.is(SandStormItems.MINERAL_SALT)) {
            stack.shrink(1);
            mineralUnits += 16;
            setChanged();
        } else if (stack.is(Items.BONE_MEAL)) {
            stack.shrink(1);
            mineralUnits += 8;
            setChanged();
        } else if (stack.is(SandStormItems.RAW_LITHIUM_SALTS)) {
            stack.shrink(1);
            mineralUnits += 24;
            setChanged();
        }
    }

    private void handleSeedInput() {
        if (seedUnits >= 32) {
            return;
        }
        ItemStack stack = items.get(SLOT_SEEDS);
        if (stack.isEmpty()) {
            return;
        }
        if (isSeedItem(stack)) {
            stack.shrink(1);
            seedUnits += 8;
            setChanged();
        }
    }

    private void handleFuelInput() {
        if (energyStorage.isFull()) {
            return;
        }
        ItemStack stack = items.get(SLOT_FUEL);
        if (stack.isEmpty()) {
            return;
        }
        if (stack.is(SandStormItems.ELECTRIC_COMPONENT)) {
            long needed = energyStorage.getCapacity() - energyStorage.getStoredEnergy();
            long added = Math.min(needed, 50000L);
            if (added > 0) {
                energyStorage.receiveEnergy(added);
                stack.shrink(1);
                setChanged();
            }
        } else if (stack.is(Items.REDSTONE)) {
            energyStorage.receiveEnergy(1000L);
            stack.shrink(1);
            setChanged();
        } else if (stack.is(Items.REDSTONE_BLOCK)) {
            energyStorage.receiveEnergy(9000L);
            stack.shrink(1);
            setChanged();
        }
    }

    private void handleUpgradeModule() {
        ItemStack stack = items.get(SLOT_UPGRADE);
        if (stack.isEmpty()) {
            return;
        }
        if (stack.is(SandStormItems.QUANTUM_MIND_MATRIX) || stack.is(SandStormItems.SUPERCONDUCTOR_TOROID)) {
            if (tier < 3) {
                setTier(3);
            }
        } else if (stack.is(SandStormItems.CIRCUIT_BOARD) || stack.is(SandStormItems.TECH_DISC)) {
            if (tier < 2) {
                setTier(2);
            }
        }
    }

    private void handleWptCharge(Level level, BlockPos pos) {
        if (level.getGameTime() % 20 != 0 || energyStorage.isFull()) {
            return;
        }
        long wpt = WirelessSolarReceiverManager.getPrimaryWptChargeAt(level, pos);
        if (wpt > 0) {
            energyStorage.receiveEnergy(wpt * 15L);
            setChanged();
        }
    }

    private void maintainRainWeather(ServerLevel serverLevel) {
        if (serverLevel.getGameTime() % 60 == 0) {
            serverLevel.getWeatherData().setRaining(true);
            serverLevel.getWeatherData().setRainTime(160);
            serverLevel.getWeatherData().setClearWeatherTime(0);
            if (lightningEnabled) {
                serverLevel.getWeatherData().setThundering(true);
                serverLevel.getWeatherData().setThunderTime(160);
            } else {
                serverLevel.getWeatherData().setThundering(false);
                serverLevel.getWeatherData().setThunderTime(0);
            }
        }
    }

    private void maintainDissipationWeather(ServerLevel serverLevel) {
        serverLevel.getWeatherData().setRaining(true);
        serverLevel.getWeatherData().setRainTime(dissipatingTicks);
        serverLevel.getWeatherData().setClearWeatherTime(0);
        serverLevel.getWeatherData().setThundering(false);
        serverLevel.getWeatherData().setThunderTime(0);
    }

    private void spawnDissipatingAtmosphere(ServerLevel serverLevel, BlockPos pos) {
        float ratio = (float) dissipatingTicks / DISSIPATION_DURATION;
        if (serverLevel.getRandom().nextFloat() > ratio) {
            return;
        }
        RandomSource random = serverLevel.getRandom();
        int radius = getRadius();
        int ox = random.nextInt(radius * 2 + 1) - radius;
        int oz = random.nextInt(radius * 2 + 1) - radius;
        if (ox * ox + oz * oz > radius * radius) {
            return;
        }
        int px = pos.getX() + ox;
        int pz = pos.getZ() + oz;
        int py = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, px, pz);
        serverLevel.sendParticles(ParticleTypes.FALLING_WATER, px + 0.5, py + 3.0, pz + 0.5, 1, 0.4, 0.8, 0.4, 0.0);
        serverLevel.sendParticles(ParticleTypes.RAIN, px + 0.5, py + 3.5, pz + 0.5, 1, 0.4, 0.8, 0.4, 0.0);
    }

    private void spawnLightningInRadius(ServerLevel serverLevel, BlockPos pos) {
        RandomSource random = serverLevel.getRandom();
        int radius = getRadius();
        int ox = random.nextInt(radius * 2 + 1) - radius;
        int oz = random.nextInt(radius * 2 + 1) - radius;
        if (ox * ox + oz * oz > radius * radius) {
            return;
        }
        int lx = pos.getX() + ox;
        int lz = pos.getZ() + oz;
        int ly = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, lx, lz);
        BlockPos strikePos = new BlockPos(lx, ly, lz);
        if (serverLevel.isLoaded(strikePos)) {
            LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.NATURAL);
            if (bolt != null) {
                bolt.setVisualOnly(false);
                bolt.snapTo(strikePos.getX() + 0.5, strikePos.getY(), strikePos.getZ() + 0.5);
                serverLevel.addFreshEntity(bolt);
            }
        }
    }

    private void spawnMachineAtmosphere(ServerLevel serverLevel, BlockPos pos) {
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 3, 0.2, 0.4, 0.2, 0.05);
        serverLevel.sendParticles(ParticleTypes.GLOW, pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5, 2, 0.1, 0.3, 0.1, 0.02);

        RandomSource random = serverLevel.getRandom();
        int radius = getRadius();
        for (int i = 0; i < 3; i++) {
            int ox = random.nextInt(radius * 2 + 1) - radius;
            int oz = random.nextInt(radius * 2 + 1) - radius;
            if (ox * ox + oz * oz > radius * radius) {
                continue;
            }
            int px = pos.getX() + ox;
            int pz = pos.getZ() + oz;
            int py = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, px, pz);
            serverLevel.sendParticles(ParticleTypes.FALLING_WATER, px + 0.5, py + 3.0, pz + 0.5, 2, 0.4, 0.8, 0.4, 0.0);
            serverLevel.sendParticles(ParticleTypes.RAIN, px + 0.5, py + 3.5, pz + 0.5, 2, 0.4, 0.8, 0.4, 0.0);
            if (seedUnits > 0 || !items.get(SLOT_SEEDS).isEmpty()) {
                serverLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, px + 0.5, py + 1.2, pz + 0.5, 1, 0.3, 0.3, 0.3, 0.0);
            }
        }
    }

    private void performTerraformingConversions(ServerLevel serverLevel, BlockPos pos) {
        RandomSource random = serverLevel.getRandom();
        int radius = getRadius();
        int conversionsCount = getConversionsPerTick();

        for (int i = 0; i < conversionsCount; i++) {
            int ox = random.nextInt(radius * 2 + 1) - radius;
            int oz = random.nextInt(radius * 2 + 1) - radius;
            if (ox * ox + oz * oz > radius * radius) {
                continue;
            }

            int targetX = pos.getX() + ox;
            int targetZ = pos.getZ() + oz;
            int surfaceY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, targetX, targetZ);
            BlockPos targetPos = new BlockPos(targetX, surfaceY - 1, targetZ);

            if (!serverLevel.isLoaded(targetPos)) {
                continue;
            }

            BlockState targetState = serverLevel.getBlockState(targetPos);

            if (isAridBlock(targetState)) {
                if (consumeMineralUnit()) {
                    serverLevel.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                    serverLevel.sendParticles(ParticleTypes.COMPOSTER, targetPos.getX() + 0.5, targetPos.getY() + 1.1, targetPos.getZ() + 0.5, 6, 0.3, 0.1, 0.3, 0.05);
                    serverLevel.sendParticles(ParticleTypes.FALLING_WATER, targetPos.getX() + 0.5, targetPos.getY() + 1.2, targetPos.getZ() + 0.5, 3, 0.2, 0.1, 0.2, 0.01);
                    serverLevel.playSound(null, targetPos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 0.25f, 1.0f);
                }
            } else if (isDirtBlock(targetState)) {
                if (consumeSeedUnit()) {
                    serverLevel.setBlock(targetPos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, targetPos.getX() + 0.5, targetPos.getY() + 1.1, targetPos.getZ() + 0.5, 8, 0.3, 0.1, 0.3, 0.05);
                    serverLevel.playSound(null, targetPos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.3f, 1.0f);
                }
            } else if (isGrassBlock(targetState)) {
                BlockPos abovePos = targetPos.above();
                if (serverLevel.getBlockState(abovePos).isAir()) {
                    if (hasSaplings()) {
                        BlockState saplingState = consumeSapling();
                        if (saplingState != null) {
                            serverLevel.setBlock(abovePos, saplingState, 3);
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, abovePos.getX() + 0.5, abovePos.getY() + 0.5, abovePos.getZ() + 0.5, 6, 0.2, 0.2, 0.2, 0.05);
                            serverLevel.sendParticles(ParticleTypes.GLOW, abovePos.getX() + 0.5, abovePos.getY() + 0.6, abovePos.getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.02);
                            serverLevel.playSound(null, abovePos, SoundEvents.CHERRY_SAPLING_PLACE, SoundSource.BLOCKS, 0.4f, 1.0f);
                        }
                    } else if (consumeSeedUnit()) {
                        BlockState foliageState = getRandomFoliage(random);
                        serverLevel.setBlock(abovePos, foliageState, 3);
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, abovePos.getX() + 0.5, abovePos.getY() + 0.3, abovePos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.05);
                        serverLevel.playSound(null, abovePos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.3f, 1.0f);
                    }
                }
            }
        }
    }

    private boolean consumeMineralUnit() {
        if (mineralUnits > 0) {
            mineralUnits--;
            return true;
        }
        ItemStack stack = items.get(SLOT_MINERAL);
        if (!stack.isEmpty()) {
            handleMineralInput();
            if (mineralUnits > 0) {
                mineralUnits--;
                return true;
            }
        }
        return false;
    }

    private boolean consumeSeedUnit() {
        if (seedUnits > 0) {
            seedUnits--;
            return true;
        }
        ItemStack stack = items.get(SLOT_SEEDS);
        if (!stack.isEmpty()) {
            handleSeedInput();
            if (seedUnits > 0) {
                seedUnits--;
                return true;
            }
        }
        return false;
    }

    private boolean hasSaplings() {
        ItemStack stack = items.get(SLOT_SAPLINGS);
        return !stack.isEmpty() && isSaplingItem(stack);
    }

    private BlockState consumeSapling() {
        ItemStack stack = items.get(SLOT_SAPLINGS);
        if (stack.isEmpty() || !isSaplingItem(stack)) {
            return null;
        }
        BlockState state = getSaplingState(stack);
        stack.shrink(1);
        setChanged();
        return state;
    }

    private BlockState getRandomFoliage(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 60) {
            return Blocks.SHORT_GRASS.defaultBlockState();
        } else if (roll < 75) {
            return Blocks.FERN.defaultBlockState();
        } else if (roll < 85) {
            return Blocks.DANDELION.defaultBlockState();
        } else if (roll < 95) {
            return Blocks.POPPY.defaultBlockState();
        } else {
            return Blocks.CORNFLOWER.defaultBlockState();
        }
    }

    public static boolean isAridBlock(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.SAND
                || block == Blocks.RED_SAND
                || block == Blocks.SANDSTONE
                || block == Blocks.SMOOTH_SANDSTONE
                || block == Blocks.CUT_SANDSTONE
                || block == Blocks.RED_SANDSTONE
                || block == Blocks.SUSPICIOUS_SAND) {
            return true;
        }
        var id = BuiltInRegistries.BLOCK.getKey(block);
        return id != null && "sandstorm".equals(id.getNamespace()) && "salinized_sand".equals(id.getPath());
    }

    public static boolean isDirtBlock(BlockState state) {
        return state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT);
    }

    public static boolean isGrassBlock(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK);
    }

    public static boolean isSeedItem(ItemStack stack) {
        if (stack.is(Items.WHEAT_SEEDS)
                || stack.is(Items.BEETROOT_SEEDS)
                || stack.is(Items.MELON_SEEDS)
                || stack.is(Items.PUMPKIN_SEEDS)
                || stack.is(Items.TORCHFLOWER_SEEDS)
                || stack.is(Items.PITCHER_POD)) {
            return true;
        }
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && "sandstorm".equals(id.getNamespace())
                && ("xeno_grass_seeds".equals(id.getPath()) || "ancient_seed".equals(id.getPath()));
    }

    public static boolean isSaplingItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.is(Items.MANGROVE_PROPAGULE)) {
            return true;
        }
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            return block instanceof SaplingBlock
                    || block == Blocks.AZALEA
                    || block == Blocks.FLOWERING_AZALEA
                    || block == Blocks.BAMBOO_SAPLING;
        }
        return false;
    }

    public static BlockState getSaplingState(ItemStack stack) {
        if (stack.is(Items.MANGROVE_PROPAGULE)) {
            return Blocks.MANGROVE_PROPAGULE.defaultBlockState();
        }
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof SaplingBlock
                    || block == Blocks.AZALEA
                    || block == Blocks.FLOWERING_AZALEA
                    || block == Blocks.BAMBOO_SAPLING) {
                return block.defaultBlockState();
            }
        }
        return Blocks.OAK_SAPLING.defaultBlockState();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("tier", this.tier);
        output.putLong("storedEnergy", this.energyStorage.getStoredEnergy());
        output.putInt("waterAmount", this.waterAmount);
        output.putInt("mineralUnits", this.mineralUnits);
        output.putInt("seedUnits", this.seedUnits);
        output.putBoolean("active", this.active);
        output.putBoolean("lightningEnabled", this.lightningEnabled);
        output.putInt("dissipatingTicks", this.dissipatingTicks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(this.items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.tier = input.getIntOr("tier", 1);
        setTier(this.tier);
        this.energyStorage.setStoredEnergy(input.getLongOr("storedEnergy", 0L));
        this.waterAmount = input.getIntOr("waterAmount", 0);
        this.mineralUnits = input.getIntOr("mineralUnits", 0);
        this.seedUnits = input.getIntOr("seedUnits", 0);
        this.active = input.getBooleanOr("active", false);
        this.lightningEnabled = input.getBooleanOr("lightningEnabled", false);
        this.dissipatingTicks = input.getIntOr("dissipatingTicks", 0);
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
                    || stack.is(SandStormItems.BRACKISH_WATER_BOTTLE)
                    || stack.is(Items.POTION);
        } else if (slot == SLOT_MINERAL) {
            return stack.is(SandStormItems.MINERAL_SALT)
                    || stack.is(Items.BONE_MEAL)
                    || stack.is(SandStormItems.RAW_LITHIUM_SALTS);
        } else if (slot == SLOT_SEEDS) {
            return isSeedItem(stack);
        } else if (slot == SLOT_SAPLINGS) {
            return isSaplingItem(stack);
        } else if (slot == SLOT_FUEL) {
            return stack.is(SandStormItems.ELECTRIC_COMPONENT)
                    || stack.is(Items.REDSTONE)
                    || stack.is(Items.REDSTONE_BLOCK);
        } else if (slot == SLOT_UPGRADE) {
            return stack.is(SandStormItems.CIRCUIT_BOARD)
                    || stack.is(SandStormItems.TECH_DISC)
                    || stack.is(SandStormItems.QUANTUM_MIND_MATRIX)
                    || stack.is(SandStormItems.SUPERCONDUCTOR_TOROID);
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
