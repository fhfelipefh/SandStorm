package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class AquiferBeetleEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> DATA_HIBERNATING =
            SynchedEntityData.defineId(AquiferBeetleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STEAM_ATTACKING =
            SynchedEntityData.defineId(AquiferBeetleEntity.class, EntityDataSerializers.BOOLEAN);

    private int angerTime = 0;
    private int steamCooldown = 0;
    private BlockPos investigatedPumpPos = null;

    public AquiferBeetleEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HIBERNATING, true);
        builder.define(DATA_STEAM_ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeetleFloatGoal(this));
        this.goalSelector.addGoal(1, new ScaldingSteamJetGoal(this));
        this.goalSelector.addGoal(2, new InvestigateDesalinationPumpGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public boolean isHibernating() {
        return this.entityData.get(DATA_HIBERNATING);
    }

    public void setHibernating(boolean hibernating) {
        this.entityData.set(DATA_HIBERNATING, hibernating);
    }

    public boolean isSteamAttacking() {
        return this.entityData.get(DATA_STEAM_ATTACKING);
    }

    public void setSteamAttacking(boolean attacking) {
        this.entityData.set(DATA_STEAM_ATTACKING, attacking);
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    public void awaken() {
        if (!isHibernating()) {
            return;
        }
        setHibernating(false);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY() + 0.4, getZ(), 20, 0.4, 0.2, 0.4, 0.05);
            serverLevel.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 0.3, getZ(), 25, 0.5, 0.2, 0.5, 0.1);
            serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.SHULKER_OPEN, SoundSource.HOSTILE, 1.0f, 0.85f);
            serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.HOSTILE, 0.8f, 1.4f);
        }
    }

    public void hearPumpNoise(BlockPos pumpPos) {
        this.investigatedPumpPos = pumpPos.immutable();
        if (isHibernating()) {
            awaken();
        }
    }

    public static void alertNearbyBeetles(Level level, BlockPos pos, double radius) {
        if (level.isClientSide()) {
            return;
        }
        AABB searchBox = new AABB(pos).inflate(radius);
        List<AquiferBeetleEntity> beetles = level.getEntitiesOfClass(AquiferBeetleEntity.class, searchBox);
        for (AquiferBeetleEntity beetle : beetles) {
            beetle.hearPumpNoise(pos);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (isHibernating()) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y > 0.0 ? 0.0 : this.getDeltaMovement().y, 0.0);
            return;
        }

        if (this.angerTime > 0) {
            this.angerTime--;
            if (this.angerTime <= 0) {
                this.setTarget(null);
            }
        }

        if (this.steamCooldown > 0) {
            this.steamCooldown--;
        }
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!this.level().isClientSide() && player.isAlive()) {
            boolean brusqueCollision = player.isSprinting() || player.getDeltaMovement().horizontalDistanceSqr() > 0.04;
            if (brusqueCollision) {
                if (isHibernating()) {
                    awaken();
                }
                if (player instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
                    this.angerTime = 400;
                    this.setTarget(serverPlayer);
                }
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (isHibernating()) {
            awaken();
        }
        boolean hurt = super.hurtServer(level, source, amount);
        if (source.getEntity() instanceof LivingEntity attacker) {
            this.angerTime = 400;
            this.setTarget(attacker);
        }
        return hurt;
    }

    @Override
    public boolean isPushable() {
        return !isHibernating() && super.isPushable();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isHibernating();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.BLINDNESS || effect.getEffect() == MobEffects.DARKNESS) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isHibernating() ? null : SoundEvents.TURTLE_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SHULKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TURTLE_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Hibernating", isHibernating());
        output.putInt("AngerTime", this.angerTime);
        output.putInt("SteamCooldown", this.steamCooldown);
        if (this.investigatedPumpPos != null) {
            output.putInt("PumpX", this.investigatedPumpPos.getX());
            output.putInt("PumpY", this.investigatedPumpPos.getY());
            output.putInt("PumpZ", this.investigatedPumpPos.getZ());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setHibernating(input.getBooleanOr("Hibernating", true));
        this.angerTime = input.getIntOr("AngerTime", 0);
        this.steamCooldown = input.getIntOr("SteamCooldown", 0);
        if (input.getInt("PumpX").isPresent() && input.getInt("PumpY").isPresent() && input.getInt("PumpZ").isPresent()) {
            this.investigatedPumpPos = new BlockPos(
                    input.getIntOr("PumpX", 0),
                    input.getIntOr("PumpY", 0),
                    input.getIntOr("PumpZ", 0)
            );
        }
    }

    private static class BeetleFloatGoal extends FloatGoal {
        private final AquiferBeetleEntity beetle;

        public BeetleFloatGoal(AquiferBeetleEntity beetle) {
            super(beetle);
            this.beetle = beetle;
        }

        @Override
        public boolean canUse() {
            return !this.beetle.isHibernating() && super.canUse();
        }
    }

    private static class ScaldingSteamJetGoal extends Goal {
        private final AquiferBeetleEntity beetle;
        private int attackTicks;

        public ScaldingSteamJetGoal(AquiferBeetleEntity beetle) {
            this.beetle = beetle;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.beetle.getTarget();
            return target != null
                    && target.isAlive()
                    && !this.beetle.isHibernating()
                    && this.beetle.steamCooldown <= 0
                    && this.beetle.distanceToSqr(target) <= 64.0;
        }

        @Override
        public void start() {
            this.beetle.getNavigation().stop();
            this.beetle.setSteamAttacking(true);
            this.attackTicks = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.beetle.getTarget();
            if (target == null) {
                return;
            }
            this.beetle.getLookControl().setLookAt(target, 30.0f, 30.0f);
            this.attackTicks++;

            if (!(this.beetle.level() instanceof ServerLevel serverLevel)) {
                return;
            }

            Vec3 beetlePos = this.beetle.position().add(0, 0.4, 0);
            Vec3 targetPos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
            Vec3 dir = targetPos.subtract(beetlePos).normalize();
            Vec3 nozzlePos = beetlePos.add(dir.scale(0.7));

            if (this.attackTicks <= 10) {
                serverLevel.sendParticles(ParticleTypes.WHITE_SMOKE, nozzlePos.x, nozzlePos.y, nozzlePos.z, 3, 0.1, 0.1, 0.1, 0.02);
                if (this.attackTicks == 1) {
                    serverLevel.playSound(null, nozzlePos.x, nozzlePos.y, nozzlePos.z, SoundEvents.LAVA_EXTINGUISH, SoundSource.HOSTILE, 0.6f, 1.8f);
                }
            } else if (this.attackTicks <= 35) {
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, nozzlePos.x, nozzlePos.y, nozzlePos.z, 5, dir.x * 0.3, dir.y * 0.3, dir.z * 0.3, 0.08);
                serverLevel.sendParticles(ParticleTypes.SPLASH, nozzlePos.x, nozzlePos.y, nozzlePos.z, 8, dir.x * 0.2, dir.y * 0.2, dir.z * 0.2, 0.12);
                serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, nozzlePos.x, nozzlePos.y, nozzlePos.z, 4, dir.x * 0.1, dir.y * 0.1, dir.z * 0.1, 0.05);

                if (this.attackTicks % 5 == 0) {
                    serverLevel.playSound(null, nozzlePos.x, nozzlePos.y, nozzlePos.z, SoundEvents.LAVA_EXTINGUISH, SoundSource.HOSTILE, 0.9f, 0.8f);
                    serverLevel.playSound(null, nozzlePos.x, nozzlePos.y, nozzlePos.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 0.7f, 1.2f);
                }

                if (this.attackTicks % 5 == 0 && this.beetle.distanceToSqr(target) <= 64.0) {
                    if (target instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator()) {
                        SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
                        suit.getThermal().setCurrentTemperature(50.0);
                        suit.consumeEnergy(5000L);
                        player.sendSystemMessage(Component.translatable("message.sandstorm.beetle_scalding_steam"), true);
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
                    } else if (!(target instanceof Player)) {
                        target.hurtServer(serverLevel, this.beetle.damageSources().dryOut(), 1.0f);
                        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
                    }
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.beetle.getTarget();
            return target != null && target.isAlive() && this.attackTicks <= 40;
        }

        @Override
        public void stop() {
            this.beetle.setSteamAttacking(false);
            this.beetle.steamCooldown = 90;
        }
    }

    private static class InvestigateDesalinationPumpGoal extends Goal {
        private final AquiferBeetleEntity beetle;
        private int investigateTicks;

        public InvestigateDesalinationPumpGoal(AquiferBeetleEntity beetle) {
            this.beetle = beetle;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !this.beetle.isHibernating()
                    && this.beetle.getTarget() == null
                    && this.beetle.investigatedPumpPos != null;
        }

        @Override
        public void start() {
            this.investigateTicks = 0;
            if (this.beetle.investigatedPumpPos != null) {
                this.beetle.getNavigation().moveTo(
                        this.beetle.investigatedPumpPos.getX() + 0.5,
                        this.beetle.investigatedPumpPos.getY(),
                        this.beetle.investigatedPumpPos.getZ() + 0.5,
                        1.0
                );
            }
        }

        @Override
        public void tick() {
            this.investigateTicks++;
            if (this.beetle.investigatedPumpPos == null) {
                return;
            }
            double distSqr = this.beetle.distanceToSqr(Vec3.atCenterOf(this.beetle.investigatedPumpPos));
            this.beetle.getLookControl().setLookAt(
                    this.beetle.investigatedPumpPos.getX() + 0.5,
                    this.beetle.investigatedPumpPos.getY() + 0.5,
                    this.beetle.investigatedPumpPos.getZ() + 0.5
            );
            if (distSqr <= 16.0) {
                if (this.investigateTicks % 40 == 0) {
                    double angle = this.beetle.getRandom().nextDouble() * Math.PI * 2.0;
                    double radius = 2.5 + this.beetle.getRandom().nextDouble() * 2.0;
                    double targetX = this.beetle.investigatedPumpPos.getX() + 0.5 + Math.cos(angle) * radius;
                    double targetZ = this.beetle.investigatedPumpPos.getZ() + 0.5 + Math.sin(angle) * radius;
                    this.beetle.getNavigation().moveTo(targetX, this.beetle.investigatedPumpPos.getY(), targetZ, 0.8);
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.beetle.isHibernating()
                    && this.beetle.getTarget() == null
                    && this.beetle.investigatedPumpPos != null
                    && this.investigateTicks < 260;
        }

        @Override
        public void stop() {
            this.beetle.investigatedPumpPos = null;
        }
    }
}
