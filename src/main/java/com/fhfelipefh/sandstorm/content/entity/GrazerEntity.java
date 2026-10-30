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
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class GrazerEntity extends PathfinderMob implements DeactivatableAndroid {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(GrazerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CANISTERS_INTACT = SynchedEntityData.defineId(GrazerEntity.class, EntityDataSerializers.BOOLEAN);
    private int rotaryTicks = 0;

    public GrazerEntity(EntityType<? extends GrazerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_CANISTERS_INTACT, true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(3, new DesertExplorationWanderGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, LivingEntity.class, 6.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public boolean isDeactivated() {
        return this.entityData.get(DATA_DEACTIVATED);
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.entityData.set(DATA_DEACTIVATED, deactivated);
    }

    public boolean areCanistersIntact() {
        return this.entityData.get(DATA_CANISTERS_INTACT);
    }

    public void setCanistersIntact(boolean intact) {
        this.entityData.set(DATA_CANISTERS_INTACT, intact);
    }

    public int getRotaryTicks() {
        return this.rotaryTicks;
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            super.tick();
            return;
        }

        super.tick();

        if (this.level().isClientSide()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.001 || this.getTarget() != null) {
                this.rotaryTicks += 4;
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (this.areCanistersIntact() && (damageSource.is(DamageTypeTags.IS_FIRE) || damageSource.is(DamageTypeTags.IS_EXPLOSION) || this.random.nextFloat() < 0.15f)) {
            this.setCanistersIntact(false);
            level.explode(this, this.getX(), this.getY() + 1.0, this.getZ(), 2.2f, Level.ExplosionInteraction.NONE);
            level.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 1.2, this.getZ(), 12, 0.4, 0.4, 0.4, 0.1);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (this.areCanistersIntact()) {
            this.spawnAtLocation(level, new ItemStack(Items.BLAZE_POWDER, 2 + this.random.nextInt(3)));
        }
        this.spawnAtLocation(level, new ItemStack(Items.COPPER_INGOT, 1 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1));
        if (this.random.nextFloat() < 0.35f) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.IRON_GOLEM_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putBoolean("CanistersIntact", this.areCanistersIntact());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setCanistersIntact(input.getBooleanOr("CanistersIntact", true));
    }
}
