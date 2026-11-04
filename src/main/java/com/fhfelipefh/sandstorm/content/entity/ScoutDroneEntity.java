package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class ScoutDroneEntity extends PathfinderMob implements Enemy, RangedAttackMob, DeactivatableAndroid {
    public static final int MAX_SHOTS = 5;
    public static final int RECHARGE_DURATION_TICKS = 100;

    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(ScoutDroneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_RECHARGING = SynchedEntityData.defineId(ScoutDroneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SHOT_COUNT = SynchedEntityData.defineId(ScoutDroneEntity.class, EntityDataSerializers.INT);

    private int rechargeTicks = 0;

    public ScoutDroneEntity(EntityType<? extends ScoutDroneEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_RECHARGING, false);
        builder.define(DATA_SHOT_COUNT, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AndroidRangedAttackGoal(this, 1.0, 50, 75, 10.0f));
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
            this.setRecharging(false);
            this.setShotCount(0);
            this.rechargeTicks = 0;
            this.setTarget(null);
            this.getNavigation().stop();
        }
    }

    public boolean isRecharging() {
        return this.entityData.get(DATA_RECHARGING);
    }

    public void setRecharging(boolean recharging) {
        this.entityData.set(DATA_RECHARGING, recharging);
    }

    public int getShotCount() {
        return this.entityData.get(DATA_SHOT_COUNT);
    }

    public void setShotCount(int shotCount) {
        this.entityData.set(DATA_SHOT_COUNT, shotCount);
    }

    public int getRechargeTicks() {
        return this.rechargeTicks;
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
        if (this.isRecharging()) {
            super.tick();
            this.getNavigation().stop();
            this.setDeltaMovement(0.0, Math.min(0.0, this.getDeltaMovement().y), 0.0);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            this.rechargeTicks++;
            if (this.level() instanceof ServerLevel serverLevel) {
                BlockParticleOption sandDust = new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState());
                serverLevel.sendParticles(sandDust, this.getX() + (this.random.nextDouble() - 0.5) * 0.8, this.getY(), this.getZ() + (this.random.nextDouble() - 0.5) * 0.8, 2, 0.0, 0.15, 0.0, 0.02);
                if (this.rechargeTicks % 20 == 0) {
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.SAND_STEP, SoundSource.HOSTILE, 0.8f, 0.6f);
                }
            }
            if (this.rechargeTicks >= RECHARGE_DURATION_TICKS) {
                this.setRecharging(false);
                this.setShotCount(0);
                this.rechargeTicks = 0;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.DISPENSER_FAIL, SoundSource.HOSTILE, 1.0f, 1.6f);
                }
            }
            return;
        }
        super.tick();
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (this.isDeactivated() || this.isRecharging()) {
            return;
        }
        Level currentLevel = this.level();
        if (currentLevel instanceof ServerLevel serverLevel) {
            Vec3 start = this.getEyePosition().add(this.getLookAngle().scale(0.3));
            Vec3 end = target.getEyePosition();
            Vec3 diff = end.subtract(start);
            int steps = (int) Math.max(4.0, diff.length() * 2.5);

            BlockParticleOption sandDust = new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState());
            for (int i = 0; i <= steps; i++) {
                double t = i / (double) steps;
                Vec3 point = start.add(diff.scale(t));
                serverLevel.sendParticles(sandDust, point.x, point.y, point.z, 2, 0.04, 0.04, 0.04, 0.01);
                serverLevel.sendParticles(ParticleTypes.CRIT, point.x, point.y, point.z, 1, 0.01, 0.01, 0.01, 0.01);
            }

            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.SAND_BREAK, SoundSource.HOSTILE, 1.2f, 1.5f);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.HOSTILE, 0.6f, 1.8f);

            target.hurtServer(serverLevel, damageSources().mobAttack(this), 1.5f);

            int currentShots = this.getShotCount() + 1;
            this.setShotCount(currentShots);
            if (currentShots >= MAX_SHOTS) {
                this.setRecharging(true);
                this.rechargeTicks = 0;
                this.getNavigation().stop();
                serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.HOSTILE, 0.8f, 1.4f);
            }
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
            if (this.random.nextFloat() < 0.50f) {
                this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SALVAGED_POLYMER, 1));
            }
            return;
        }
        int scrapCount = 1 + this.random.nextInt(2);
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, scrapCount));
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.LOOSE_WIRES, 1 + this.random.nextInt(2)));
        if (this.random.nextFloat() < 0.75f) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SALVAGED_POLYMER, 1 + this.random.nextInt(2)));
        }
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
        output.putBoolean("Recharging", this.isRecharging());
        output.putInt("ShotCount", this.getShotCount());
        output.putInt("RechargeTicks", this.rechargeTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setRecharging(input.getBooleanOr("Recharging", false));
        this.setShotCount(input.getIntOr("ShotCount", 0));
        this.rechargeTicks = input.getIntOr("RechargeTicks", 0);
    }

    private static class AndroidRangedAttackGoal extends RangedAttackGoal {
        private final ScoutDroneEntity android;

        public AndroidRangedAttackGoal(ScoutDroneEntity mob, double speedModifier, int minAttackInterval, int maxAttackInterval, float maxAttackDistance) {
            super(mob, speedModifier, minAttackInterval, maxAttackInterval, maxAttackDistance);
            this.android = mob;
        }

        @Override
        public boolean canUse() {
            return !this.android.isRecharging() && !this.android.isDeactivated() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.android.isRecharging() && !this.android.isDeactivated() && super.canContinueToUse();
        }
    }
}
