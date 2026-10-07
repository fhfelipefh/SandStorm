package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class ExcavatorVehicleEntity extends PathfinderMob {
    public static final long DEFAULT_BATTERY_CAPACITY = 50000L;
    public static final long TRAVEL_ENERGY_COST = 1L;
    public static final long EXCAVATION_ENERGY_COST = 10L;

    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(DEFAULT_BATTERY_CAPACITY, 500L, 500L);

    public ExcavatorVehicleEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        energyStorage.setStoredEnergy(DEFAULT_BATTERY_CAPACITY);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.STEP_HEIGHT, 1.25);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putLong("StoredEnergy", this.energyStorage.getStoredEnergy());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.energyStorage.setStoredEnergy(input.getLongOr("StoredEnergy", DEFAULT_BATTERY_CAPACITY));
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public float getSeismicVibrationOutput() {
        return 4.5f;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALL)) {
            return true;
        }
        return super.isInvulnerableTo(level, source);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5f);
        this.yRotO = this.getYRot();
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.yBodyRot;
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa * 0.65f, 0.0, player.zza > 0 ? player.zza : player.zza * 0.45f);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        boolean hasPower = this.energyStorage.hasEnergy(TRAVEL_ENERGY_COST);
        float baseSpeed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
        return hasPower ? baseSpeed : baseSpeed * 0.25f;
    }

    @Override
    public boolean showVehicleHealth() {
        return false;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return new Vec3(0.0, 0.85 * scale, -0.15 * scale);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive()) {
            LivingEntity passenger = this.getControllingPassenger();
            if (this.isVehicle() && passenger instanceof Player) {
                if (travelVector.lengthSqr() > 0.001) {
                    if (this.energyStorage.hasEnergy(TRAVEL_ENERGY_COST)) {
                        this.energyStorage.extractEnergy(TRAVEL_ENERGY_COST);
                    }
                    performExcavationAhead();
                }
            }
        }
        super.travel(travelVector);
    }

    private void performExcavationAhead() {
        if (this.level().isClientSide()) {
            return;
        }
        if (!this.energyStorage.hasEnergy(EXCAVATION_ENERGY_COST)) {
            return;
        }
        Vec3 look = this.getLookAngle();
        BlockPos drillCenter = BlockPos.containing(this.getX() + look.x * 2.0, this.getY() + 0.5, this.getZ() + look.z * 2.0);
        for (int dy = 0; dy <= 1; dy++) {
            BlockPos targetPos = drillCenter.above(dy);
            BlockState state = this.level().getBlockState(targetPos);
            if (!state.isAir() && canExcavate(state, targetPos)) {
                this.energyStorage.extractEnergy(EXCAVATION_ENERGY_COST);
                this.level().destroyBlock(targetPos, true, this);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                            targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                            12, 0.25, 0.25, 0.25, 0.15);
                }
                this.level().playSound(null, targetPos, SandStormSoundEvents.EXCAVATOR_ENGINE, SoundSource.PLAYERS, 0.6f, 1.3f);
            }
        }
    }

    public boolean canExcavate(BlockState state, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(this.level(), pos);
        if (destroySpeed < 0.0f) {
            return false;
        }
        return destroySpeed <= 3.5f;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (!this.isVehicle()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.excavator_mounted",
                            NumberFormat.compact(energyStorage.getStoredEnergy()),
                            NumberFormat.compact(energyStorage.getCapacity())
                    ), true);
                }
                this.level().playSound(null, this.blockPosition(), SandStormSoundEvents.EXCAVATOR_ENGINE, SoundSource.PLAYERS, 0.8f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
}
