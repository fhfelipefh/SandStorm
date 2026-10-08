package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MegazordEntity extends PathfinderMob {
    public static final long DEFAULT_BATTERY_CAPACITY = 100000L;
    public static final long OVERDRIVE_BATTERY_CAPACITY = 250000L;
    public static final double SHOCKWAVE_RADIUS = 16.0;
    public static final double OVERDRIVE_SHOCKWAVE_RADIUS = 24.0;
    public static final float DEFAULT_SHOCKWAVE_DAMAGE = 25.0f;
    public static final float OVERDRIVE_SHOCKWAVE_DAMAGE = 45.0f;

    private static final EntityDataAccessor<Boolean> DATA_FLIGHT_MODULE = SynchedEntityData.defineId(MegazordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SUBMERSIBLE_MODULE = SynchedEntityData.defineId(MegazordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_OVERDRIVE_MODULE = SynchedEntityData.defineId(MegazordEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(MegazordEntity.class, EntityDataSerializers.INT);

    private final EnergyStorageComponent energyStorage = new EnergyStorageComponent(DEFAULT_BATTERY_CAPACITY, 2000L, 2000L);

    public MegazordEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        energyStorage.setStoredEnergy(DEFAULT_BATTERY_CAPACITY);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.FLYING_SPEED, 0.50)
                .add(Attributes.ARMOR, 25.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.STEP_HEIGHT, 2.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLIGHT_MODULE, false);
        builder.define(DATA_SUBMERSIBLE_MODULE, false);
        builder.define(DATA_OVERDRIVE_MODULE, false);
        builder.define(DATA_VARIANT, 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("FlightModule", this.hasFlightModule());
        output.putBoolean("SubmersibleModule", this.hasSubmersibleModule());
        output.putBoolean("OverdriveModule", this.hasOverdriveModule());
        output.putLong("StoredEnergy", this.energyStorage.getStoredEnergy());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setFlightModule(input.getBooleanOr("FlightModule", false));
        this.setSubmersibleModule(input.getBooleanOr("SubmersibleModule", false));
        this.setOverdriveModule(input.getBooleanOr("OverdriveModule", false));
        this.energyStorage.setStoredEnergy(input.getLongOr("StoredEnergy", DEFAULT_BATTERY_CAPACITY));
    }

    public boolean hasFlightModule() {
        return this.entityData.get(DATA_FLIGHT_MODULE);
    }

    public void setFlightModule(boolean flight) {
        this.entityData.set(DATA_FLIGHT_MODULE, flight);
        updateVariant();
    }

    public boolean hasSubmersibleModule() {
        return this.entityData.get(DATA_SUBMERSIBLE_MODULE);
    }

    public void setSubmersibleModule(boolean sub) {
        this.entityData.set(DATA_SUBMERSIBLE_MODULE, sub);
        updateVariant();
    }

    public boolean hasOverdriveModule() {
        return this.entityData.get(DATA_OVERDRIVE_MODULE);
    }

    public void setOverdriveModule(boolean overdrive) {
        this.entityData.set(DATA_OVERDRIVE_MODULE, overdrive);
        if (overdrive) {
            this.energyStorage.setCapacity(OVERDRIVE_BATTERY_CAPACITY);
        } else {
            this.energyStorage.setCapacity(DEFAULT_BATTERY_CAPACITY);
        }
        updateVariant();
    }

    public MegazordVariant getVariant() {
        return MegazordVariant.fromIndex(this.entityData.get(DATA_VARIANT));
    }

    private void updateVariant() {
        MegazordVariant variant = MegazordVariant.resolve(hasFlightModule(), hasSubmersibleModule());
        this.entityData.set(DATA_VARIANT, variant.getIndex());
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public float getSeismicVibrationOutput() {
        return hasOverdriveModule() ? 8.5f : 6.0f;
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
        return new Vec3(player.xxa * 0.6f, 0.0, player.zza > 0 ? player.zza : player.zza * 0.35f);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.85f;
        if (hasOverdriveModule()) {
            speed *= 1.35f;
        }
        if (hasFlightModule() && !this.onGround()) {
            speed *= 1.5f;
        }
        return speed;
    }

    @Override
    public boolean showVehicleHealth() {
        return false;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return new Vec3(0.0, 3.85 * scale, 0.35 * scale);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive()) {
            LivingEntity passenger = this.getControllingPassenger();
            if (this.isVehicle() && passenger instanceof Player player) {
                boolean inWater = this.isInWater() || this.isEyeInFluid(FluidTags.WATER);
                if (this.onGround() && player.isJumping() && !inWater && energyStorage.hasEnergy(20L)) {
                    this.setDeltaMovement(this.getDeltaMovement().x, hasOverdriveModule() ? 1.25 : 1.05, this.getDeltaMovement().z);
                    energyStorage.extractEnergy(20L);
                }
                if (inWater && hasSubmersibleModule()) {
                    handleSubmersibleTravel(player, travelVector);
                    return;
                }
                if (!inWater && hasFlightModule() && energyStorage.hasEnergy(5L)) {
                    handleFlightTravel(player, travelVector);
                    return;
                }
                this.setNoGravity(false);
                if (travelVector.lengthSqr() > 0.001) {
                    if (this.onGround() && this.tickCount % 14 == 0) {
                        this.level().playSound(null, this.blockPosition(), SandStormSoundEvents.MEGAZORD_STEP, SoundSource.PLAYERS, 1.0f, 0.85f);
                    }
                    energyStorage.extractEnergy(2L);
                }
            }
        }
        super.travel(travelVector);
    }

    private void handleSubmersibleTravel(Player player, Vec3 travelVector) {
        this.setNoGravity(true);
        player.setAirSupply(player.getMaxAirSupply());
        player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 40, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 40, 0, false, false, false));

        Vec3 look = player.getLookAngle();
        double targetSpeed = (hasOverdriveModule() ? 0.38 : 0.28);
        Vec3 currentMovement = this.getDeltaMovement();

        double mx = currentMovement.x * 0.85;
        double my = currentMovement.y * 0.85;
        double mz = currentMovement.z * 0.85;

        if (player.zza > 0) {
            mx += look.x * targetSpeed * 0.35;
            my += look.y * targetSpeed * 0.35;
            mz += look.z * targetSpeed * 0.35;
            energyStorage.extractEnergy(4L);
        } else if (player.zza < 0) {
            mx -= look.x * targetSpeed * 0.15;
            my -= look.y * targetSpeed * 0.15;
            mz -= look.z * targetSpeed * 0.15;
            energyStorage.extractEnergy(3L);
        }

        if (player.xxa != 0) {
            Vec3 side = new Vec3(-look.z, 0.0, look.x).normalize();
            mx += side.x * player.xxa * targetSpeed * 0.2;
            mz += side.z * player.xxa * targetSpeed * 0.2;
            energyStorage.extractEnergy(2L);
        }

        this.setDeltaMovement(mx, my, mz);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void handleFlightTravel(Player player, Vec3 travelVector) {
        this.setNoGravity(true);
        Vec3 look = player.getLookAngle();
        double speed = (hasOverdriveModule() ? 0.42 : 0.32);
        Vec3 motion = this.getDeltaMovement();

        double mx = motion.x * 0.88;
        double my = motion.y * 0.88;
        double mz = motion.z * 0.88;

        if (player.zza > 0) {
            mx += look.x * speed * 0.35;
            my += look.y * speed * 0.35;
            mz += look.z * speed * 0.35;
            energyStorage.extractEnergy(6L);
        } else if (player.zza < 0) {
            mx -= look.x * speed * 0.15;
            my -= look.y * speed * 0.15;
            mz -= look.z * speed * 0.15;
            energyStorage.extractEnergy(4L);
        }

        if (player.xxa != 0) {
            Vec3 side = new Vec3(-look.z, 0.0, look.x).normalize();
            mx += side.x * player.xxa * speed * 0.2;
            mz += side.z * player.xxa * speed * 0.2;
            energyStorage.extractEnergy(3L);
        }

        this.setDeltaMovement(mx, my, mz);
        this.move(MoverType.SELF, this.getDeltaMovement());

        Level level = this.level();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && (player.zza != 0 || player.xxa != 0)) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY() + 0.3, this.getZ(), 2, 0.3, 0.1, 0.3, 0.05);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            boolean inWater = this.isInWater() || this.isEyeInFluid(FluidTags.WATER);
            if (inWater && hasSubmersibleModule()) {
                player.setAirSupply(player.getMaxAirSupply());
                player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 40, 0, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 40, 0, false, false, false));
            }
        }
    }

    public boolean triggerSonicShockwave() {
        long cost = hasOverdriveModule() ? 1500L : 2000L;
        if (!energyStorage.hasEnergy(cost)) {
            return false;
        }

        energyStorage.extractEnergy(cost);
        Level level = this.level();

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            double radius = hasOverdriveModule() ? OVERDRIVE_SHOCKWAVE_RADIUS : SHOCKWAVE_RADIUS;
            float damage = hasOverdriveModule() ? OVERDRIVE_SHOCKWAVE_DAMAGE : DEFAULT_SHOCKWAVE_DAMAGE;
            AABB area = this.getBoundingBox().inflate(radius);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, area, entity ->
                    entity != this && !(entity instanceof Player) && (entity instanceof Enemy || entity instanceof SandwormEntity)
            );

            for (LivingEntity target : targets) {
                target.hurtServer(serverLevel, this.damageSources().sonicBoom(this), damage);
                Vec3 push = target.position().subtract(this.position()).normalize().scale(hasOverdriveModule() ? 2.5 : 1.8);
                target.setDeltaMovement(push.x, 0.6, push.z);
            }

            serverLevel.playSound(null, this.blockPosition(), SandStormSoundEvents.MEGAZORD_SHOCKWAVE, SoundSource.PLAYERS, 1.5f, hasOverdriveModule() ? 1.25f : 1.0f);
        }

        return true;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN)) {
            return true;
        }
        return super.isInvulnerableTo(level, source);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (this.isVehicle() && this.getControllingPassenger() == player) {
            triggerSonicShockwave();
            return InteractionResult.SUCCESS;
        }

        if (!this.isVehicle()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(SandStormItems.MEGAZORD_FLIGHT_MODULE)) {
                if (hasFlightModule()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.megazord_module_already_installed"), true);
                    }
                    return InteractionResult.CONSUME;
                }
                setFlightModule(true);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                playUpgradeSoundAndMessage(player, "telemetry.sandstorm.megazord_flight_installed");
                return InteractionResult.SUCCESS;
            }

            if (stack.is(SandStormItems.MEGAZORD_SUBMERSIBLE_HULL)) {
                if (hasSubmersibleModule()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.megazord_module_already_installed"), true);
                    }
                    return InteractionResult.CONSUME;
                }
                setSubmersibleModule(true);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                playUpgradeSoundAndMessage(player, "telemetry.sandstorm.megazord_submersible_installed");
                return InteractionResult.SUCCESS;
            }

            if (stack.is(SandStormItems.MEGAZORD_TACTICAL_OVERDRIVE)) {
                if (hasOverdriveModule()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable("telemetry.sandstorm.megazord_module_already_installed"), true);
                    }
                    return InteractionResult.CONSUME;
                }
                setOverdriveModule(true);
                this.energyStorage.receiveEnergy(OVERDRIVE_BATTERY_CAPACITY);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                playUpgradeSoundAndMessage(player, "telemetry.sandstorm.megazord_overdrive_installed");
                return InteractionResult.SUCCESS;
            }

            if (player.isShiftKeyDown()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.megazord_diagnostics",
                            getVariant().getId(),
                            NumberFormat.compact(energyStorage.getStoredEnergy()),
                            NumberFormat.compact(energyStorage.getCapacity()),
                            hasFlightModule() ? "§aOK" : "§7OFF",
                            hasSubmersibleModule() ? "§bOK" : "§7OFF",
                            hasOverdriveModule() ? "§eOK" : "§7OFF"
                    ));
                }
                return InteractionResult.SUCCESS;
            }

            if (!this.level().isClientSide()) {
                player.startRiding(this);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable(
                            "telemetry.sandstorm.megazord_cockpit",
                            NumberFormat.compact(energyStorage.getStoredEnergy()),
                            NumberFormat.compact(energyStorage.getCapacity())
                    ), true);
                }
                this.level().playSound(null, this.blockPosition(), SandStormSoundEvents.MEGAZORD_STEP, SoundSource.PLAYERS, 1.0f, 0.7f);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private void playUpgradeSoundAndMessage(Player player, String messageKey) {
        Level level = this.level();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1.0f, 1.2f);
            serverLevel.playSound(null, this.blockPosition(), SandStormSoundEvents.ASSEMBLY_CONSTRUCT, SoundSource.PLAYERS, 0.8f, 1.0f);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY() + 1.5, this.getZ(), 20, 0.6, 0.8, 0.6, 0.1);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable(messageKey), true);
            }
        }
    }
}
