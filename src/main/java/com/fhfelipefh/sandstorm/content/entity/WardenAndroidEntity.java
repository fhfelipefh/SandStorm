package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
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
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class WardenAndroidEntity extends PathfinderMob implements Enemy, DeactivatableAndroid {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(WardenAndroidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_ANIM = SynchedEntityData.defineId(WardenAndroidEntity.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            UUID.randomUUID(),
            Component.translatable("entity.sandstorm.warden_android"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.NOTCHED_12
    ).setDarkenScreen(true).setCreateWorldFog(true);

    private int sonicCooldown = 80;
    private int stompCooldown = 120;
    private int empCooldown = 160;

    public WardenAndroidEntity(EntityType<? extends WardenAndroidEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 150;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 24.0)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.ARMOR_TOUGHNESS, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_ATTACK_ANIM, 0);
    }

    @Override
    public boolean isDeactivated() {
        return this.entityData.get(DATA_DEACTIVATED);
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.entityData.set(DATA_DEACTIVATED, deactivated);
    }

    public int getAttackAnim() {
        return this.entityData.get(DATA_ATTACK_ANIM);
    }

    public void setAttackAnim(int anim) {
        this.entityData.set(DATA_ATTACK_ANIM, anim);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        if (isDeactivated()) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
            this.bossEvent.setProgress(0.0f);
            return;
        }

        float healthPct = this.getHealth() / this.getMaxHealth();
        this.bossEvent.setProgress(healthPct);

        boolean berserk = healthPct <= 0.35f;
        if (berserk) {
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);
            if (this.level() instanceof ServerLevel serverLevel && this.random.nextFloat() < 0.3f) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 1.8, this.getZ(), 4, 0.4, 0.4, 0.4, 0.02);
            }
        } else if (healthPct <= 0.70f) {
            this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        } else {
            this.bossEvent.setColor(BossEvent.BossBarColor.BLUE);
        }

        if (this.sonicCooldown > 0) {
            this.sonicCooldown -= berserk ? 2 : 1;
        }
        if (this.stompCooldown > 0) {
            this.stompCooldown -= berserk ? 2 : 1;
        }
        if (this.empCooldown > 0) {
            this.empCooldown--;
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && this.level() instanceof ServerLevel serverLevel) {
            double distSq = this.distanceToSqr(target);

            if (distSq <= 64.0 && this.stompCooldown <= 0) {
                executeSeismicStomp(serverLevel);
            } else if (distSq >= 36.0 && distSq <= 1024.0 && this.sonicCooldown <= 0) {
                executeSonicBeam(serverLevel, target);
            }

            if (this.empCooldown <= 0) {
                executeEmpPulse(serverLevel);
            }
        }
    }

    private void executeSonicBeam(ServerLevel serverLevel, LivingEntity target) {
        this.sonicCooldown = 90;
        setAttackAnim(20);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0f, 0.85f);

        Vec3 eyePos = this.getEyePosition();
        Vec3 targetEyePos = target.getEyePosition();
        Vec3 dir = targetEyePos.subtract(eyePos).normalize();
        double distance = Math.min(32.0, eyePos.distanceTo(targetEyePos));

        for (int i = 1; i <= (int) distance; i++) {
            Vec3 point = eyePos.add(dir.scale(i));
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        target.hurtServer(serverLevel, this.damageSources().mobAttack(this), 20.0f);
        target.push(dir.x * 1.5, 0.5, dir.z * 1.5);
    }

    private void executeSeismicStomp(ServerLevel serverLevel) {
        this.stompCooldown = 140;
        setAttackAnim(15);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.5f, 0.9f);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.HOSTILE, 2.5f, 0.7f);

        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.2, this.getZ(), 30, 2.0, 0.2, 2.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 0.5, this.getZ(), 25, 2.5, 0.5, 2.5, 0.2);

        AABB stompArea = this.getBoundingBox().inflate(8.0, 3.0, 8.0);
        List<LivingEntity> victims = serverLevel.getEntitiesOfClass(LivingEntity.class, stompArea, e -> e != this && e.isAlive());
        for (LivingEntity victim : victims) {
            victim.hurtServer(serverLevel, this.damageSources().mobAttack(this), 16.0f);
            victim.push(0.0, 0.7, 0.0);
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 2));
            victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
        }
    }

    private void executeEmpPulse(ServerLevel serverLevel) {
        this.empCooldown = 200;
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 2.0f, 1.2f);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY() + 1.5, this.getZ(), 40, 3.0, 1.5, 3.0, 0.2);

        AABB empArea = this.getBoundingBox().inflate(12.0);
        List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(Player.class, empArea, LivingEntity::isAlive);
        for (Player player : nearbyPlayers) {
            SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player.getUUID());
            suit.consumeEnergy(10000L);
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0));
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (isDeactivated()) {
            return false;
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int starCount = 1 + this.random.nextInt(3);
        this.spawnAtLocation(level, new ItemStack(Items.NETHER_STAR, starCount));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.WARDEN_SINGULARITY_CORE, 1));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 6 + this.random.nextInt(7)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.NANO_ACTUATOR, 3 + this.random.nextInt(4)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.CIRCUIT_BOARD, 3 + this.random.nextInt(4)));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", isDeactivated());
        output.putInt("SonicCooldown", this.sonicCooldown);
        output.putInt("StompCooldown", this.stompCooldown);
        output.putInt("EmpCooldown", this.empCooldown);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setDeactivated(input.getBooleanOr("Deactivated", false));
        this.sonicCooldown = input.getIntOr("SonicCooldown", 80);
        this.stompCooldown = input.getIntOr("StompCooldown", 120);
        this.empCooldown = input.getIntOr("EmpCooldown", 160);
    }
}

