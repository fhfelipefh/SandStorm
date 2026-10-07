package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.block.CryogenicAtmosphericChillerBlock;
import com.fhfelipefh.sandstorm.content.block.CryogenicChillerManager;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CryogenicAtmosphericChillerBlockEntity extends BlockEntity {
    public static final long MAX_ENERGY = 500000L;
    public static final long ENERGY_COST_PER_TICK = 50L;
    public static final int DEFAULT_RADIUS = 48;

    private final EnergyStorageComponent energyStorage;
    private int radius = DEFAULT_RADIUS;
    private boolean active = false;

    public CryogenicAtmosphericChillerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.energyStorage = new EnergyStorageComponent(MAX_ENERGY, 5000L, 5000L);
    }

    public CryogenicAtmosphericChillerBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.CRYOGENIC_ATMOSPHERIC_CHILLER_BE, pos, state);
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = Math.max(8, Math.min(80, radius));
        setChanged();
    }

    public boolean isActive() {
        return active;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        boolean hasEnergy = energyStorage.hasEnergy(ENERGY_COST_PER_TICK);
        boolean previouslyActive = this.active;

        if (hasEnergy) {
            energyStorage.extractEnergy(ENERGY_COST_PER_TICK);
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
        this.radius = input.getIntOr("radius", DEFAULT_RADIUS);
    }
}
