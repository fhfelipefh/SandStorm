package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
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
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class DerelictAutomatonEntity extends PathfinderMob implements Enemy {
    private static final EntityDataAccessor<Boolean> DATA_OVERCHARGING = SynchedEntityData.defineId(DerelictAutomatonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_FUSE = SynchedEntityData.defineId(DerelictAutomatonEntity.class, EntityDataSerializers.INT);

    private static final int MAX_FUSE = 30;
    private int oldFuse = 0;

    public DerelictAutomatonEntity(EntityType<? extends DerelictAutomatonEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 22.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OVERCHARGING, false);
        builder.define(DATA_FUSE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AutomatonOverchargeGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, false));
        this.goalSelector.addGoal(3, new DesertExplorationWanderGoal(this, 0.95));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isOvercharging() {
        return this.entityData.get(DATA_OVERCHARGING);
    }

    public void setOvercharging(boolean overcharging) {
        this.entityData.set(DATA_OVERCHARGING, overcharging);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE);
    }

    public void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE, Math.clamp(fuse, 0, MAX_FUSE));
    }

    public float getOverchargeProgress(float partialTick) {
        float f = this.oldFuse + (this.getFuse() - this.oldFuse) * partialTick;
        return f / (float) MAX_FUSE;
    }

    @Override
    public void tick() {
        this.oldFuse = this.getFuse();
        super.tick();

        Level currentLevel = this.level();
        if (currentLevel.isClientSide()) {
            if (this.isOvercharging()) {
                currentLevel.addParticle(ParticleTypes.ELECTRIC_SPARK,
                        this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5),
                        (this.random.nextDouble() - 0.5) * 0.2, 0.1, (this.random.nextDouble() - 0.5) * 0.2);
                currentLevel.addParticle(ParticleTypes.SMOKE,
                        this.getRandomX(0.3), this.getY() + 0.8, this.getRandomZ(0.3),
                        0.0, 0.05, 0.0);
            }
            return;
        }

        if (this.isAlive() && currentLevel instanceof ServerLevel serverLevel) {
            int fuse = this.getFuse();
            if (this.isOvercharging()) {
                fuse++;
                this.setFuse(fuse);
                if (fuse % 5 == 0) {
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1.0f, 1.0f + (fuse / (float) MAX_FUSE));
                }
                if (fuse >= MAX_FUSE) {
                    this.detonate(serverLevel);
                }
            } else if (fuse > 0) {
                this.setFuse(fuse - 1);
            }
        }
    }

    public void detonate(ServerLevel serverLevel) {
        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.5, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY() + 0.5, this.getZ(), 40, 1.5, 1.0, 1.5, 0.2);
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0f, 0.9f);
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 1.2f, 1.8f);

        serverLevel.explode(this, this.getX(), this.getY(), this.getZ(), 2.6f, Level.ExplosionInteraction.MOB);

        AABB empBox = this.getBoundingBox().inflate(6.0, 4.0, 6.0);
        List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(Player.class, empBox);
        for (Player player : nearbyPlayers) {
            if (player instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
                SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(serverPlayer);
                long currentEnergy = suit.getStoredEnergy();
                suit.setStoredEnergy(Math.max(0L, currentEnergy - 6000L));
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1));
                serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.automaton_emp_drain"), true);
            }
        }

        int scrapCount = 1 + this.random.nextInt(3);
        serverLevel.addFreshEntity(new ItemEntity(serverLevel, this.getX(), this.getY() + 0.5, this.getZ(),
                new ItemStack(SandStormItems.SCRAP_METAL, scrapCount)));

        if (this.random.nextFloat() < 0.25f) {
            serverLevel.addFreshEntity(new ItemEntity(serverLevel, this.getX(), this.getY() + 0.5, this.getZ(),
                    new ItemStack(SandStormItems.CIRCUIT_BOARD, 1)));
        }

        this.discard();
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(serverLevel, damageSource, recentlyHit);
        int scrapCount = 1 + this.random.nextInt(2);
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, scrapCount));
        if (this.random.nextFloat() < 0.35f) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.CIRCUIT_BOARD, 1));
        }
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
        output.putBoolean("Overcharging", this.isOvercharging());
        output.putInt("Fuse", this.getFuse());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setOvercharging(input.getBooleanOr("Overcharging", false));
        this.setFuse(input.getIntOr("Fuse", 0));
    }

    private static class AutomatonOverchargeGoal extends Goal {
        private final DerelictAutomatonEntity automaton;
        private LivingEntity target;

        public AutomatonOverchargeGoal(DerelictAutomatonEntity automaton) {
            this.automaton = automaton;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity living = this.automaton.getTarget();
            return living != null && living.isAlive() && this.automaton.distanceToSqr(living) <= 12.25;
        }

        @Override
        public void start() {
            this.target = this.automaton.getTarget();
            this.automaton.getNavigation().stop();
            this.automaton.setOvercharging(true);
        }

        @Override
        public void stop() {
            this.target = null;
            this.automaton.setOvercharging(false);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.target == null || !this.target.isAlive()) {
                return false;
            }
            return this.automaton.distanceToSqr(this.target) <= 25.0;
        }

        @Override
        public void tick() {
            if (this.target != null) {
                this.automaton.getLookControl().setLookAt(this.target, 30.0f, 30.0f);
            }
            this.automaton.getNavigation().stop();
        }
    }
}
