package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.survival.FlashlightStateServer;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class ScrapSentinelEntity extends PathfinderMob implements Enemy {
    private static final EntityDataAccessor<Boolean> DATA_DORMANT = SynchedEntityData.defineId(ScrapSentinelEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_ANIM = SynchedEntityData.defineId(ScrapSentinelEntity.class, EntityDataSerializers.INT);

    private int empCooldown = 0;

    public ScrapSentinelEntity(EntityType<? extends ScrapSentinelEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 28.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DORMANT, true);
        builder.define(DATA_ATTACK_ANIM, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SentinelFloatGoal(this));
        this.goalSelector.addGoal(1, new SentinelEmpShockwaveGoal(this));
        this.goalSelector.addGoal(2, new SentinelMeleeAttackGoal(this, 1.25, false));
        this.goalSelector.addGoal(3, new SentinelStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new SentinelLookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new SentinelRandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new SentinelHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SentinelNearestAttackableTargetGoal(this));
    }

    public boolean isDormant() {
        return this.entityData.get(DATA_DORMANT);
    }

    public void setDormant(boolean dormant) {
        this.entityData.set(DATA_DORMANT, dormant);
    }

    public int getAttackAnim() {
        return this.entityData.get(DATA_ATTACK_ANIM);
    }

    public void setAttackAnim(int ticks) {
        this.entityData.set(DATA_ATTACK_ANIM, ticks);
    }

    public boolean canUseEmp() {
        return !isDormant() && this.empCooldown <= 0;
    }

    public void awaken(LivingEntity trigger) {
        if (!isDormant()) {
            if (trigger != null && getTarget() == null) {
                setTarget(trigger);
            }
            return;
        }
        setDormant(false);
        if (trigger != null) {
            setTarget(trigger);
        }
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 0.8, getZ(), 30, 0.5, 0.5, 0.5, 0.15);
            serverLevel.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 0.8, getZ(), 15, 0.3, 0.3, 0.3, 0.05);
            serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.0f, 0.5f);
            serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 0.9f, 1.6f);
        }
    }

    public static void alertNearbySentinels(Level level, BlockPos pos, double radius, LivingEntity trigger) {
        if (level.isClientSide()) {
            return;
        }
        AABB searchBox = new AABB(pos).inflate(radius);
        List<ScrapSentinelEntity> sentinels = level.getEntitiesOfClass(ScrapSentinelEntity.class, searchBox);
        for (ScrapSentinelEntity sentinel : sentinels) {
            if (sentinel.isDormant()) {
                sentinel.awaken(trigger);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (isDormant()) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y > 0.0 ? 0.0 : this.getDeltaMovement().y, 0.0);
            if (!this.level().isClientSide() && this.tickCount % 5 == 0) {
                checkFlashlightAwakening();
            }
            return;
        }

        if (this.empCooldown > 0) {
            this.empCooldown--;
        }

        int currentAnim = getAttackAnim();
        if (currentAnim > 0) {
            setAttackAnim(currentAnim - 1);
        }
    }

    private void checkFlashlightAwakening() {
        AABB scanBox = this.getBoundingBox().inflate(10.0);
        List<Player> nearby = this.level().getEntitiesOfClass(Player.class, scanBox);
        for (Player player : nearby) {
            if (!player.isCreative() && !player.isSpectator() && FlashlightStateServer.isActive(player.getUUID())) {
                awaken(player);
                break;
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (isDormant()) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                awaken(attacker);
            } else {
                awaken(null);
            }
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt) {
            triggerEmpDischarge(level, target);
        }
        return hurt;
    }

    public void triggerEmpDischarge(ServerLevel level, Entity target) {
        setAttackAnim(15);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + 1.0, target.getZ(), 25, 0.4, 0.4, 0.4, 0.15);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 0.7f, 2.0f);
        if (target instanceof ServerPlayer player) {
            SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
            long cur = suit.getStoredEnergy();
            suit.setStoredEnergy(Math.max(0L, cur - 8000L));
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
            player.sendSystemMessage(Component.translatable("message.sandstorm.sentinel_emp_drain"), true);
        }
    }

    public void performEmpShockwave(ServerLevel level) {
        this.empCooldown = 90;
        setAttackAnim(15);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 0.8, getZ(), 60, 1.8, 0.5, 1.8, 0.2);
        level.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 0.5, getZ(), 20, 1.2, 0.3, 1.2, 0.05);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.0f, 1.8f);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 0.9f, 2.0f);

        AABB pulseBox = getBoundingBox().inflate(6.0, 3.0, 6.0);
        List<Player> affectedPlayers = level.getEntitiesOfClass(Player.class, pulseBox);
        for (Player p : affectedPlayers) {
            if (p instanceof ServerPlayer sp && !sp.isCreative() && !sp.isSpectator()) {
                SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(sp);
                long cur = suit.getStoredEnergy();
                suit.setStoredEnergy(Math.max(0L, cur - 6000L));
                sp.hurtServer(level, damageSources().mobAttack(this), 2.0f);
                sp.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1));
                sp.sendSystemMessage(Component.translatable("message.sandstorm.sentinel_emp_drain"), true);
            }
        }
    }

    @Override
    public boolean isPushable() {
        return !isDormant() && super.isPushable();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isDormant();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isDormant() ? null : SoundEvents.IRON_GOLEM_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Dormant", isDormant());
        output.putInt("EmpCooldown", this.empCooldown);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setDormant(input.getBooleanOr("Dormant", true));
        this.empCooldown = input.getIntOr("EmpCooldown", 0);
    }

    private static class SentinelFloatGoal extends FloatGoal {
        private final ScrapSentinelEntity sentinel;

        public SentinelFloatGoal(ScrapSentinelEntity sentinel) {
            super(sentinel);
            this.sentinel = sentinel;
        }

        @Override
        public boolean canUse() {
            return !this.sentinel.isDormant() && super.canUse();
        }
    }

    private static class SentinelMeleeAttackGoal extends MeleeAttackGoal {
        private final ScrapSentinelEntity sentinel;

        public SentinelMeleeAttackGoal(ScrapSentinelEntity sentinel, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(sentinel, speedModifier, followingTargetEvenIfNotSeen);
            this.sentinel = sentinel;
        }

        @Override
        public boolean canUse() {
            return !this.sentinel.isDormant() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.sentinel.isDormant() && super.canContinueToUse();
        }
    }

    private static class SentinelStrollGoal extends WaterAvoidingRandomStrollGoal {
        private final ScrapSentinelEntity sentinel;

        public SentinelStrollGoal(ScrapSentinelEntity sentinel, double speedModifier) {
            super(sentinel, speedModifier);
            this.sentinel = sentinel;
        }

        @Override
        public boolean canUse() {
            return !this.sentinel.isDormant() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.sentinel.isDormant() && super.canContinueToUse();
        }
    }

    private static class SentinelLookAtPlayerGoal extends LookAtPlayerGoal {
        private final ScrapSentinelEntity sentinel;

        public SentinelLookAtPlayerGoal(ScrapSentinelEntity sentinel, Class<? extends LivingEntity> lookAtType, float lookDistance) {
            super(sentinel, lookAtType, lookDistance);
            this.sentinel = sentinel;
        }

        @Override
        public boolean canUse() {
            return !this.sentinel.isDormant() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.sentinel.isDormant() && super.canContinueToUse();
        }
    }

    private static class SentinelRandomLookAroundGoal extends RandomLookAroundGoal {
        private final ScrapSentinelEntity sentinel;

        public SentinelRandomLookAroundGoal(ScrapSentinelEntity sentinel) {
            super(sentinel);
            this.sentinel = sentinel;
        }

        @Override
        public boolean canUse() {
            return !this.sentinel.isDormant() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.sentinel.isDormant() && super.canContinueToUse();
        }
    }

    private static class SentinelHurtByTargetGoal extends HurtByTargetGoal {
        private final ScrapSentinelEntity sentinel;

        public SentinelHurtByTargetGoal(ScrapSentinelEntity sentinel) {
            super(sentinel);
            this.sentinel = sentinel;
        }

        @Override
        public boolean canUse() {
            return !this.sentinel.isDormant() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.sentinel.isDormant() && super.canContinueToUse();
        }
    }

    private static class SentinelNearestAttackableTargetGoal extends NearestAttackableTargetGoal<Player> {
        private final ScrapSentinelEntity sentinel;

        public SentinelNearestAttackableTargetGoal(ScrapSentinelEntity sentinel) {
            super(sentinel, Player.class, true);
            this.sentinel = sentinel;
        }

        @Override
        public boolean canUse() {
            return !this.sentinel.isDormant() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.sentinel.isDormant() && super.canContinueToUse();
        }
    }

    private static class SentinelEmpShockwaveGoal extends Goal {
        private final ScrapSentinelEntity sentinel;
        private int warmUpTicks = 0;

        public SentinelEmpShockwaveGoal(ScrapSentinelEntity sentinel) {
            this.sentinel = sentinel;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.sentinel.canUseEmp()) {
                return false;
            }
            LivingEntity target = this.sentinel.getTarget();
            return target != null && target.isAlive() && this.sentinel.distanceToSqr(target) <= 36.0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.warmUpTicks > 0;
        }

        @Override
        public void start() {
            this.warmUpTicks = 15;
            this.sentinel.setAttackAnim(15);
            this.sentinel.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = this.sentinel.getTarget();
            if (target != null) {
                this.sentinel.getLookControl().setLookAt(target, 30.0f, 30.0f);
            }
            this.warmUpTicks--;
            if (this.warmUpTicks == 0 && this.sentinel.level() instanceof ServerLevel serverLevel) {
                this.sentinel.performEmpShockwave(serverLevel);
            }
        }
    }
}
