package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class ScoutDroneEntity extends PathfinderMob implements Enemy, RangedAttackMob, DeactivatableAndroid {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(ScoutDroneEntity.class, EntityDataSerializers.BOOLEAN);

    public ScoutDroneEntity(EntityType<? extends ScoutDroneEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0, 30, 45, 14.0f));
        this.goalSelector.addGoal(2, new DesertExplorationWanderGoal(this, 0.95));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
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
        this.setNoAi(deactivated);
        if (deactivated) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1.0);
            this.setHealth(1.0f);
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
            this.setTarget(null);
            this.getNavigation().stop();
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
    public void tick() {
        if (this.isDeactivated()) {
            super.tick();
            this.setDeltaMovement(0.0, Math.min(0.0, this.getDeltaMovement().y), 0.0);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            return;
        }
        super.tick();
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (this.isDeactivated()) {
            return;
        }
        Level currentLevel = this.level();
        if (currentLevel instanceof ServerLevel serverLevel) {
            Vec3 start = this.getEyePosition();
            Vec3 end = target.getEyePosition();
            Vec3 diff = end.subtract(start);
            int steps = (int) Math.max(3.0, diff.length() * 2.0);

            for (int i = 0; i <= steps; i++) {
                double t = i / (double) steps;
                Vec3 point = start.add(diff.scale(t));
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.02);
            }

            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SandStormSoundEvents.PLASMA_RIFLE_FIRE, SoundSource.HOSTILE, 1.0f, 1.8f);

            target.hurtServer(serverLevel, damageSources().mobAttack(this), 4.5f);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(serverLevel, damageSource, recentlyHit);
        if (this.isDeactivated()) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, 1));
            if (this.random.nextFloat() < 0.25f) {
                this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.LOOSE_WIRES, 1));
            }
            if (this.random.nextFloat() < 0.1f) {
                this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.CIRCUIT_BOARD, 1));
            }
            return;
        }
        int scrapCount = 1 + this.random.nextInt(2);
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, scrapCount));
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.LOOSE_WIRES, 1 + this.random.nextInt(2)));
        if (this.random.nextFloat() < 0.25f) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.CIRCUIT_BOARD, 1));
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.isDeactivated() ? SoundEvents.NETHERITE_BLOCK_BREAK : SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isDeactivated() ? SoundEvents.ITEM_BREAK.value() : SoundEvents.IRON_GOLEM_DEATH;
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
