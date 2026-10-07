package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.config.SandStormConfig;
import com.fhfelipefh.sandstorm.content.block.CryogenicAtmosphericChillerBlock;
import com.fhfelipefh.sandstorm.content.block.CryogenicChillerManager;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.CryogenicAtmosphericChillerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CryogenicAtmosphericChillerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CONTAINER_SIZE = 0;
    public static final int DATA_COUNT = 8;
    public static final long MAX_ENERGY = 500000L;
    public static final long ENERGY_COST_PER_TICK = 50L;
    public static final int DEFAULT_RADIUS = 64;

    private final EnergyStorageComponent energyStorage;
    private int radius = DEFAULT_RADIUS;
    private boolean active = false;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            long energy = energyStorage.getStoredEnergy();
            long energyCost = SandStormConfig.getCryogenicChillerEnergyCost();
            return switch (index) {
                case 0 -> (int) (energy & 0xFFFF);
                case 1 -> (int) ((energy >> 16) & 0xFFFF);
                case 2 -> (int) (MAX_ENERGY & 0xFFFF);
                case 3 -> (int) ((MAX_ENERGY >> 16) & 0xFFFF);
                case 4 -> (int) (energyCost & 0xFFFF);
                case 5 -> (int) ((energyCost >> 16) & 0xFFFF);
                case 6 -> radius;
                case 7 -> active ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public CryogenicAtmosphericChillerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.energyStorage = new EnergyStorageComponent(MAX_ENERGY, 5000L, 5000L);
        this.radius = SandStormConfig.getCryogenicChillerRadius();
    }

    public CryogenicAtmosphericChillerBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.CRYOGENIC_ATMOSPHERIC_CHILLER_BE, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.sandstorm.cryogenic_atmospheric_chiller");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new CryogenicAtmosphericChillerMenu(syncId, playerInventory, this, this.dataAccess);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = Math.max(8, Math.min(512, radius));
        setChanged();
    }

    public boolean isActive() {
        return active;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        long energyCost = SandStormConfig.getCryogenicChillerEnergyCost();
        boolean hasEnergy = energyStorage.hasEnergy(energyCost);
        boolean previouslyActive = this.active;

        if (hasEnergy) {
            energyStorage.extractEnergy(energyCost);
            this.active = true;
            CryogenicChillerManager.registerChiller(serverLevel.dimension(), pos, this.radius);

            if (serverLevel.getGameTime() % 20L == 0L) {
                spawnColdParticles(serverLevel, pos);
                maintainThermalEquilibrium(serverLevel, pos);
            }
        } else {
            this.active = false;
            CryogenicChillerManager.unregisterChiller(serverLevel.dimension(), pos);
        }

        if (previouslyActive != this.active) {
            if (state.hasProperty(CryogenicAtmosphericChillerBlock.ACTIVE) && state.getValue(CryogenicAtmosphericChillerBlock.ACTIVE) != this.active) {
                level.setBlock(pos, state.setValue(CryogenicAtmosphericChillerBlock.ACTIVE, this.active), 3);
            }
            setChanged();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null && !level.isClientSide()) {
            CryogenicChillerManager.unregisterChiller(level.dimension(), worldPosition);
        }
    }

    private void spawnColdParticles(ServerLevel serverLevel, BlockPos pos) {
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 8, 0.4, 0.4, 0.4, 0.02);
        serverLevel.sendParticles(ParticleTypes.FALLING_WATER, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 4, 0.3, 0.3, 0.3, 0.01);
    }

    private void maintainThermalEquilibrium(ServerLevel serverLevel, BlockPos pos) {
        AABB area = new AABB(pos).inflate(this.radius);
        List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(Player.class, area);
        for (Player player : nearbyPlayers) {
            if (player.isOnFire()) {
                player.clearFire();
            }
        }

        int scanRadius = Math.min(this.radius, 16);
        int ox = serverLevel.getRandom().nextInt(scanRadius * 2 + 1) - scanRadius;
        int oz = serverLevel.getRandom().nextInt(scanRadius * 2 + 1) - scanRadius;
        BlockPos checkPos = pos.offset(ox, 0, oz);
        if (serverLevel.isLoaded(checkPos)) {
            BlockState checkState = serverLevel.getBlockState(checkPos);
            if (checkState.is(Blocks.WATER)) {
                serverLevel.setBlock(checkPos, Blocks.ICE.defaultBlockState(), 3);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("storedEnergy", energyStorage.getStoredEnergy());
        output.putBoolean("active", this.active);
        output.putInt("radius", this.radius);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyStorage.setStoredEnergy(input.getLongOr("storedEnergy", 0L));
        this.active = input.getBooleanOr("active", false);
        this.radius = input.getIntOr("radius", SandStormConfig.getCryogenicChillerRadius());
    }
}
