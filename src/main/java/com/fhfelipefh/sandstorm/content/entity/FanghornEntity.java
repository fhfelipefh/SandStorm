package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class FanghornEntity extends PathfinderMob implements Enemy, DeactivatableAndroid, RangedAttackMob {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(FanghornEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHARGING_HORNS = SynchedEntityData.defineId(FanghornEntity.class, EntityDataSerializers.BOOLEAN);

    public FanghornEntity(EntityType<? extends FanghornEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.MOVEMENT_SPEED, 0.29)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_CHARGING_HORNS, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.15, 40, 16.0f));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false));
        this.goalSelector.addGoal(3, new DesertExplorationWanderGoal(this, 0.95));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean isDeactivated() {
        return this.entityData.get(DATA_DEACTIVATED);
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.entityData.set(DATA_DEACTIVATED, deactivated);
    }

    public boolean isChargingHorns() {
        return this.entityData.get(DATA_CHARGING_HORNS);
    }

    public void setChargingHorns(boolean charging) {
        this.entityData.set(DATA_CHARGING_HORNS, charging);
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            super.tick();
            return;
        }

        super.tick();

        if (this.level().isClientSide() && this.isChargingHorns()) {
            Vec3 head = this.getEyePosition();
            this.level().addParticle(ParticleTypes.FLAME, head.x + (this.random.nextDouble() - 0.5) * 0.4, head.y + 0.3, head.z + (this.random.nextDouble() - 0.5) * 0.4, 0, 0.05, 0);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (this.isDeactivated()) {
            return;
        }

        Level level = this.level();
        if (level instanceof ServerLevel serverLevel) {
            Vec3 start = this.getEyePosition().add(this.getLookAngle().scale(0.5));
            Vec3 targetPos = target.getEyePosition();

            this.playSound(SoundEvents.BLAZE_SHOOT, 1.0f, 1.2f);
            this.setChargingHorns(true);

            int beamSteps = (int) Math.min(24.0, targetPos.distanceTo(start) * 2.0);
            for (int i = 0; i < beamSteps; i++) {
                double frac = (double) i / (double) beamSteps;
                Vec3 p = start.lerp(targetPos, frac);
                serverLevel.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 2, 0.1, 0.1, 0.1, 0.02);
                serverLevel.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.01);
            }

            target.hurtServer(serverLevel, serverLevel.damageSources().mobAttack(this), 6.0f);
            target.igniteForSeconds(4);
            serverLevel.sendParticles(ParticleTypes.LAVA, target.getX(), target.getY() + 1.0, target.getZ(), 6, 0.3, 0.3, 0.3, 0.1);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.IS_FIRE) && this.random.nextFloat() < 0.2f) {
            level.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 1.2, this.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(Items.BLAZE_POWDER, 2 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(Items.GUNPOWDER, 1 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1));
        if (this.random.nextFloat() < 0.40f) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", this.isDeactivated());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
    }
}
