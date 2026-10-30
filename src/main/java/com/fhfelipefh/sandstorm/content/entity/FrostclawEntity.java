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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FrostclawEntity extends PathfinderMob implements Enemy, DeactivatableAndroid {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(FrostclawEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHEST_SAC_INTACT = SynchedEntityData.defineId(FrostclawEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SLAM_ATTACKING = SynchedEntityData.defineId(FrostclawEntity.class, EntityDataSerializers.BOOLEAN);
    private int slamCooldown = 0;

    public FrostclawEntity(EntityType<? extends FrostclawEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
                .add(Attributes.FOLLOW_RANGE, 36.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_CHEST_SAC_INTACT, true);
        builder.define(DATA_SLAM_ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25, false));
        this.goalSelector.addGoal(2, new DesertExplorationWanderGoal(this, 0.85));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

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

    public boolean isChestSacIntact() {
        return this.entityData.get(DATA_CHEST_SAC_INTACT);
    }

    public void setChestSacIntact(boolean intact) {
        this.entityData.set(DATA_CHEST_SAC_INTACT, intact);
    }

    public boolean isSlamAttacking() {
        return this.entityData.get(DATA_SLAM_ATTACKING);
    }

    public void setSlamAttacking(boolean slam) {
        this.entityData.set(DATA_SLAM_ATTACKING, slam);
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            super.tick();
            return;
        }

        super.tick();

        Level currentLevel = this.level();
        if (!currentLevel.isClientSide() && currentLevel instanceof ServerLevel serverLevel) {
            if (this.slamCooldown > 0) {
                this.slamCooldown--;
            }

            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && this.slamCooldown == 0 && this.distanceToSqr(target) <= 36.0) {
                performGroundSlam(serverLevel);
                this.slamCooldown = 100;
            }
        }
    }

    private void performGroundSlam(ServerLevel serverLevel) {
        this.setSlamAttacking(true);
        this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.4f, 0.7f);
        this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.6f, 0.8f);

        double cx = this.getX();
        double cy = this.getY();
        double cz = this.getZ();

        for (int i = 0; i < 32; i++) {
            double angle = (2.0 * Math.PI * i) / 32.0;
            double rx = Math.cos(angle) * 4.5;
            double rz = Math.sin(angle) * 4.5;
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, cx + rx, cy + 0.2, cz + rz, 4, 0.2, 0.3, 0.2, 0.08);
            serverLevel.sendParticles(ParticleTypes.CLOUD, cx + rx * 0.5, cy + 0.3, cz + rz * 0.5, 2, 0.1, 0.2, 0.1, 0.03);
        }

        AABB shockBox = this.getBoundingBox().inflate(6.0, 2.0, 6.0);
        List<LivingEntity> enemies = serverLevel.getEntitiesOfClass(LivingEntity.class, shockBox, e -> e != this && e.isAlive());
        for (LivingEntity enemy : enemies) {
            enemy.hurtServer(serverLevel, serverLevel.damageSources().mobAttack(this), 14.0f);
            enemy.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 120, 2));
            enemy.setTicksFrozen(200);
            enemy.push((enemy.getX() - cx) * 0.3, 0.35, (enemy.getZ() - cz) * 0.3);
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1));
            living.setTicksFrozen(140);
        }
        return hurt;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (this.isChestSacIntact() && (this.getHealth() - amount < 60.0f || this.random.nextFloat() < 0.12f)) {
            this.setChestSacIntact(false);
            level.playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK, this.getSoundSource(), 1.5f, 0.6f);
            level.sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 1.5, this.getZ(), 40, 1.0, 1.0, 1.0, 0.15);
            level.sendParticles(ParticleTypes.ITEM_SNOWBALL, this.getX(), this.getY() + 1.5, this.getZ(), 20, 0.5, 0.5, 0.5, 0.1);

            AABB blastBox = this.getBoundingBox().inflate(8.0, 3.0, 8.0);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, blastBox, LivingEntity::isAlive);
            for (LivingEntity e : entities) {
                e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 160, 3));
                e.setTicksFrozen(300);
            }
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 3 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.RAW_SILICON, 4 + this.random.nextInt(4)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 2 + this.random.nextInt(2)));
        this.spawnAtLocation(level, new ItemStack(Items.PACKED_ICE, 4 + this.random.nextInt(4)));
        this.spawnAtLocation(level, new ItemStack(Items.PRISMARINE_CRYSTALS, 2 + this.random.nextInt(3)));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putBoolean("ChestSacIntact", this.isChestSacIntact());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setChestSacIntact(input.getBooleanOr("ChestSacIntact", true));
    }
}
