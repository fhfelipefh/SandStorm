package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormState;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SeismicScorpionEntity extends PathfinderMob implements Enemy, DeactivatableAndroid {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(SeismicScorpionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_STING_TICKS = SynchedEntityData.defineId(SeismicScorpionEntity.class, EntityDataSerializers.INT);

    public SeismicScorpionEntity(EntityType<? extends SeismicScorpionEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 25;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_STING_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25, true));
        this.goalSelector.addGoal(2, new DesertExplorationWanderGoal(this, 0.85));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, SandwormEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ApexSandwormEntity.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean isDeactivated() {
        return this.entityData.get(DATA_DEACTIVATED);
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.entityData.set(DATA_DEACTIVATED, deactivated);
        if (deactivated) {
            this.getNavigation().stop();
            this.setTarget(null);
        }
    }

    public int getStingTicks() {
        return this.entityData.get(DATA_STING_TICKS);
    }

    public void triggerStingAnimation() {
        this.entityData.set(DATA_STING_TICKS, 16);
    }

    public float getStingAnimationProgress(float partialTick) {
        int ticks = this.getStingTicks();
        if (ticks <= 0) {
            return 0.0f;
        }
        return Math.clamp((16.0f - (ticks - partialTick)) / 16.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            super.tick();
            this.setDeltaMovement(0.0, Math.min(0.0, this.getDeltaMovement().y), 0.0);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            return;
        }

        super.tick();

        if (!this.level().isClientSide()) {
            int sting = this.getStingTicks();
            if (sting > 0) {
                this.entityData.set(DATA_STING_TICKS, sting - 1);
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt) {
            this.triggerStingAnimation();
            if (target instanceof SandwormEntity sandworm) {
                sandworm.setSandwormState(SandwormState.SURFACED_ASSAULT);
                sandworm.resetSurfaceTimer();
                sandworm.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 4));
                sandworm.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
                level.playSound(null, this.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 1.0f, 1.8f);
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + 1.0, target.getZ(), 25, 0.6, 0.6, 0.6, 0.15);
                level.sendParticles(ParticleTypes.SONIC_BOOM, target.getX(), target.getY() + 1.0, target.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            } else if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1));
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
            }
        }
        return hurt;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.SCORPION_CHITIN_PLATE, 1 + this.random.nextInt(3)));
        if (this.random.nextFloat() < 0.7f) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.SEISMIC_SCORPION_STINGER, 1));
        }
        this.spawnAtLocation(level, new ItemStack(SandStormItems.SCRAP_METAL, 1 + this.random.nextInt(2)));
        if (this.random.nextFloat() < 0.25f) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1));
        }
    }

    @Override
    public void checkDespawn() {
        if (this.isPersistenceRequired() || this.isNoAi()) {
            return;
        }
        super.checkDespawn();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isDeactivated() ? null : SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.isDeactivated() ? SoundEvents.NETHERITE_BLOCK_BREAK : SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isDeactivated() ? SoundEvents.ITEM_BREAK.value() : SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putInt("StingTicks", this.getStingTicks());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        int sting = input.getIntOr("StingTicks", 0);
        this.entityData.set(DATA_STING_TICKS, sting);
    }
}
