package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class FrostclawEntity extends PathfinderMob implements Enemy, DeactivatableAndroid, OverridableMachine {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(FrostclawEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHEST_SAC_INTACT = SynchedEntityData.defineId(FrostclawEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SLAM_ATTACKING = SynchedEntityData.defineId(FrostclawEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_OVERRIDDEN = SynchedEntityData.defineId(FrostclawEntity.class, EntityDataSerializers.BOOLEAN);

    private int slamCooldown = 0;
    private UUID overrideOwner;

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
        builder.define(DATA_OVERRIDDEN, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25, false));
        this.goalSelector.addGoal(2, new DesertExplorationWanderGoal(this, 0.85));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new FrostclawAttackPlayerGoal(this));
        this.targetSelector.addGoal(3, new FrostclawDefendGoal(this));
    }

    @Override
    public boolean isDeactivated() {
        return this.entityData.get(DATA_DEACTIVATED);
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.entityData.set(DATA_DEACTIVATED, deactivated);
        this.setNoAi(deactivated);
        if (deactivated) {
            this.setTarget(null);
            this.getNavigation().stop();
            this.ejectPassengers();
        }
    }

    @Override
    public Component getName() {
        if (this.isDeactivated()) {
            return Component.translatable(this.getType().getDescriptionId() + ".deactivated");
        }
        return super.getName();
    }

    @Override
    public boolean isOverridden() {
        return this.entityData.get(DATA_OVERRIDDEN);
    }

    @Override
    public void setOverridden(boolean overridden) {
        this.entityData.set(DATA_OVERRIDDEN, overridden);
    }

    @Override
    public UUID getOverrideOwner() {
        return this.overrideOwner;
    }

    @Override
    public void setOverrideOwner(UUID owner) {
        this.overrideOwner = owner;
    }

    @Override
    public boolean canBeRiddenBy(Player player) {
        return false;
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

        if (this.slamCooldown > 0) {
            this.slamCooldown--;
        }

        if (this.isOverridden()) {
            tickCryoAura();
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && this.slamCooldown == 0 && this.distanceToSqr(target) < 16.0) {
            triggerGroundSlam(target);
        }
    }

    private void tickCryoAura() {
        if (this.tickCount % 20 == 0) {
            AABB auraBox = this.getBoundingBox().inflate(8.0);
            List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, auraBox, p -> p.isAlive() && (this.overrideOwner == null || p.getUUID().equals(this.overrideOwner)));
            for (Player player : nearbyPlayers) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false, true));
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 1.5, this.getZ(), 8, 2.0, 0.5, 2.0, 0.02);
            }
        }
    }

    private void triggerGroundSlam(LivingEntity target) {
        this.setSlamAttacking(true);
        this.slamCooldown = 120;

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 0.2, this.getZ(), 40, 2.0, 0.2, 2.0, 0.15);
            serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL, this.getX(), this.getY() + 0.2, this.getZ(), 20, 1.5, 0.3, 1.5, 0.1);
        }

        this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.4f, 0.7f);

        AABB damageArea = this.getBoundingBox().inflate(4.0, 1.0, 4.0);
        List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, damageArea, e -> e != this && (!this.isOverridden() || !e.isAlliedTo(this)));
        for (LivingEntity victim : victims) {
            victim.hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 14.0f);
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
            victim.push(0.0, 0.45, 0.0);
        }

        this.setSlamAttacking(false);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        Entity attacker = damageSource.getEntity();
        if (this.isChestSacIntact() && attacker instanceof LivingEntity livingAttacker) {
            if (this.distanceToSqr(livingAttacker) > 16.0 && this.random.nextFloat() < 0.25f) {
                this.setChestSacIntact(false);
                level.explode(this, this.getX(), this.getY() + 2.0, this.getZ(), 3.5f, Level.ExplosionInteraction.NONE);
                level.sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 2.0, this.getZ(), 60, 1.5, 1.5, 1.5, 0.25);
                AABB freezeArea = this.getBoundingBox().inflate(6.0);
                List<LivingEntity> frozen = level.getEntitiesOfClass(LivingEntity.class, freezeArea, e -> e != this);
                for (LivingEntity victim : frozen) {
                    victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 4));
                }
            }
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 4 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(Items.BLUE_ICE, 2 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.CRYO_CELL_COOLER, 1));
        if (this.random.nextFloat() < 0.5f) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 2));
        }
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
        output.putBoolean("Overridden", this.isOverridden());
        if (this.overrideOwner != null) {
            output.putString("OverrideOwner", this.overrideOwner.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setChestSacIntact(input.getBooleanOr("ChestSacIntact", true));
        this.setOverridden(input.getBooleanOr("Overridden", false));
        String ownerStr = input.getStringOr("OverrideOwner", "");
        if (!ownerStr.isEmpty()) {
            this.overrideOwner = UUID.fromString(ownerStr);
        }
    }

    private static class FrostclawAttackPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final FrostclawEntity machine;

        public FrostclawAttackPlayerGoal(FrostclawEntity machine) {
            super(machine, Player.class, true);
            this.machine = machine;
        }

        @Override
        public boolean canUse() {
            return !this.machine.isOverridden() && !this.machine.isDeactivated() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.machine.isOverridden() && !this.machine.isDeactivated() && super.canContinueToUse();
        }
    }

    private static class FrostclawDefendGoal extends NearestAttackableTargetGoal<Monster> {
        private final FrostclawEntity machine;

        public FrostclawDefendGoal(FrostclawEntity machine) {
            super(machine, Monster.class, true);
            this.machine = machine;
        }

        @Override
        public boolean canUse() {
            return this.machine.isOverridden() && !this.machine.isDeactivated() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.machine.isOverridden() && !this.machine.isDeactivated() && super.canContinueToUse();
        }
    }
}
