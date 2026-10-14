package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class DefectiveSentinelEntity extends PathfinderMob implements Enemy {

    public static final double DENSITY_RADIUS_WIDE = 16.0;
    public static final int MAX_SENTINELS_WIDE = 3;
    public static final double DENSITY_RADIUS_TIGHT = 8.0;
    public static final int MAX_SENTINELS_TIGHT = 2;

    private static final EntityDataAccessor<Boolean> DATA_CHARGING = SynchedEntityData.defineId(DefectiveSentinelEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TWITCH_TICKS = SynchedEntityData.defineId(DefectiveSentinelEntity.class, EntityDataSerializers.INT);

    private int dischargeCooldown = 0;

    public DefectiveSentinelEntity(EntityType<? extends DefectiveSentinelEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CHARGING, false);
        builder.define(DATA_TWITCH_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ElectricalDischargeGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(DATA_CHARGING, charging);
    }

    public int getTwitchTicks() {
        return this.entityData.get(DATA_TWITCH_TICKS);
    }

    public void setTwitchTicks(int ticks) {
        this.entityData.set(DATA_TWITCH_TICKS, ticks);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.dischargeCooldown > 0) {
            this.dischargeCooldown--;
        }

        int twitch = getTwitchTicks();
        if (twitch > 0) {
            setTwitchTicks(twitch - 1);
        } else if (!this.level().isClientSide() && this.random.nextInt(60) == 0) {
            setTwitchTicks(15);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.HOSTILE, 0.6f, 1.4f + this.random.nextFloat() * 0.4f);
        }

        if (!this.level().isClientSide() && (this.tickCount + this.getId()) % 60 == 0) {
            enforceDensityCap();
        }

        if (this.level().isClientSide()) {
            if (isCharging() || twitch > 0 || this.random.nextFloat() < 0.1f) {
                double sx = this.getX() + (this.random.nextDouble() - 0.5) * 0.8;
                double sy = this.getY() + 0.6 + this.random.nextDouble() * 0.8;
                double sz = this.getZ() + (this.random.nextDouble() - 0.5) * 0.8;
                this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, sx, sy, sz,
                        (this.random.nextDouble() - 0.5) * 0.1, 0.05, (this.random.nextDouble() - 0.5) * 0.1);
            }
        }
    }

    public static boolean canSpawnInArea(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        AABB tightBox = new AABB(pos).inflate(DENSITY_RADIUS_TIGHT);
        List<DefectiveSentinelEntity> tightList = level.getEntitiesOfClass(
                DefectiveSentinelEntity.class,
                tightBox,
                Entity::isAlive
        );
        if (tightList.size() >= MAX_SENTINELS_TIGHT) {
            return false;
        }

        AABB wideBox = new AABB(pos).inflate(DENSITY_RADIUS_WIDE);
        List<DefectiveSentinelEntity> wideList = level.getEntitiesOfClass(
                DefectiveSentinelEntity.class,
                wideBox,
                Entity::isAlive
        );
        return wideList.size() < MAX_SENTINELS_WIDE;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        if (levelReader instanceof Level level && !canSpawnInArea(level, this.blockPosition())) {
            return false;
        }
        return super.checkSpawnObstruction(levelReader);
    }

    private void enforceDensityCap() {
        AABB tightBox = this.getBoundingBox().inflate(DENSITY_RADIUS_TIGHT);
        List<DefectiveSentinelEntity> tightSentinels = this.level().getEntitiesOfClass(
                DefectiveSentinelEntity.class,
                tightBox,
                Entity::isAlive
        );
        if (tightSentinels.size() > MAX_SENTINELS_TIGHT) {
            tightSentinels.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
            int rank = tightSentinels.indexOf(this);
            if (rank >= MAX_SENTINELS_TIGHT) {
                this.discard();
                return;
            }
        }

        AABB wideBox = this.getBoundingBox().inflate(DENSITY_RADIUS_WIDE);
        List<DefectiveSentinelEntity> wideSentinels = this.level().getEntitiesOfClass(
                DefectiveSentinelEntity.class,
                wideBox,
                Entity::isAlive
        );
        if (wideSentinels.size() > MAX_SENTINELS_WIDE) {
            wideSentinels.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
            int rank = wideSentinels.indexOf(this);
            if (rank >= MAX_SENTINELS_WIDE) {
                this.discard();
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        boolean hurt = super.doHurtTarget(serverLevel, target);
        if (hurt && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
            this.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 0.5f, 2.0f);
        }
        return hurt;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.0f, 1.5f);
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.6f, Level.ExplosionInteraction.NONE);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(serverLevel, damageSource, recentlyHit);
        int wafers = 1 + this.random.nextInt(2);
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SILICON_WAFER, wafers));
        this.spawnAtLocation(serverLevel, new ItemStack(Items.REDSTONE, 2 + this.random.nextInt(4)));
        if (this.random.nextInt(3) == 0) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD, 1));
        }
    }

    private static class ElectricalDischargeGoal extends Goal {
        private final DefectiveSentinelEntity sentinel;
        private int chargeTicks;

        public ElectricalDischargeGoal(DefectiveSentinelEntity sentinel) {
            this.sentinel = sentinel;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.sentinel.getTarget();
            return target != null && target.isAlive()
                    && this.sentinel.dischargeCooldown <= 0
                    && this.sentinel.distanceToSqr(target) <= 36.0
                    && this.sentinel.hasLineOfSight(target);
        }

        @Override
        public void start() {
            this.chargeTicks = 20;
            this.sentinel.setCharging(true);
            this.sentinel.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.sentinel.setCharging(false);
            this.chargeTicks = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.sentinel.getTarget();
            if (target == null) {
                return;
            }
            this.sentinel.getLookControl().setLookAt(target, 30.0f, 30.0f);
            this.chargeTicks--;

            if (this.chargeTicks <= 0) {
                discharge(target);
            }
        }

        private void discharge(LivingEntity target) {
            this.sentinel.dischargeCooldown = 90;
            Level level = this.sentinel.level();
            level.playSound(null, this.sentinel.getX(), this.sentinel.getY(), this.sentinel.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 0.8f, 1.8f);

            AABB area = this.sentinel.getBoundingBox().inflate(5.0);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e != this.sentinel && e.isAlive() && !(e instanceof DefectiveSentinelEntity));

            for (LivingEntity nearby : targets) {
                if (nearby instanceof Player player && (player.isCreative() || player.isSpectator())) {
                    continue;
                }
                nearby.hurt(level.damageSources().magic(), 5.0f);
                nearby.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 80, 0));
                nearby.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
            }
            this.sentinel.setCharging(false);
        }
    }
}
