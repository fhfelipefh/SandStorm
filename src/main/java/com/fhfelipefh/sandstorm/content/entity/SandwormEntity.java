package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.SandwormBiteAttackGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormBreachGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormBurrowGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormSeismicTargetGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormState;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SandwormEntity extends PathfinderMob implements Enemy {
    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(SandwormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BREACH_TICKS = SynchedEntityData.defineId(SandwormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BITE_TICKS = SynchedEntityData.defineId(SandwormEntity.class, EntityDataSerializers.INT);

    private int surfaceTicks = 140;
    private int submergingTicks = 0;

    public SandwormEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 60;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 18.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.STEP_HEIGHT, 2.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, SandwormState.SURFACED_ASSAULT.ordinal());
        builder.define(DATA_BREACH_TICKS, 0);
        builder.define(DATA_BITE_TICKS, 0);
    }

    public SandwormState getSandwormState() {
        return SandwormState.fromOrdinal(this.entityData.get(DATA_STATE));
    }

    public void setSandwormState(SandwormState state) {
        this.entityData.set(DATA_STATE, state.ordinal());
    }

    public int getSurfaceTicks() {
        return this.surfaceTicks;
    }

    public void resetSurfaceTimer() {
        this.surfaceTicks = 140;
    }

    public void decrementSurfaceTicks() {
        if (this.surfaceTicks > 0) {
            this.surfaceTicks--;
        }
    }

    public void setBreachTicks(int ticks) {
        this.entityData.set(DATA_BREACH_TICKS, ticks);
    }

    public void triggerBiteAnimation() {
        this.entityData.set(DATA_BITE_TICKS, 16);
    }

    public float getBreachAnimationProgress(float partialTick) {
        int ticks = this.entityData.get(DATA_BREACH_TICKS);
        if (ticks <= 0) {
            return 0.0f;
        }
        return Mth.clamp((35.0f - (ticks - partialTick)) / 35.0f, 0.0f, 1.0f);
    }

    public float getBiteAnimationProgress(float partialTick) {
        int ticks = this.entityData.get(DATA_BITE_TICKS);
        if (ticks <= 0) {
            return 0.0f;
        }
        return Mth.clamp((16.0f - (ticks - partialTick)) / 16.0f, 0.0f, 1.0f);
    }

    public void startSubmerging() {
        this.setSandwormState(SandwormState.SUBMERGING);
        this.submergingTicks = 35;
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), SandStormSoundEvents.SANDWORM_RUMBLE, SoundSource.HOSTILE, 1.5f, 0.75f);
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                    this.getX(), this.getY() + 0.5, this.getZ(), 50, 1.5, 0.8, 1.5, 0.15
            );
            serverLevel.sendParticles(
                    ParticleTypes.GUST_EMITTER_SMALL,
                    this.getX(), this.getY() + 0.2, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0
            );
        }
    }

    public void triggerBreachShockwave() {
        this.setBreachTicks(35);
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.playSound(null, this.blockPosition(), SandStormSoundEvents.SANDWORM_EMERGE, SoundSource.HOSTILE, 2.0f, 0.85f);
        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                this.getX(), this.getY() + 1.0, this.getZ(), 80, 2.5, 1.5, 2.5, 0.35
        );
        serverLevel.sendParticles(
                ParticleTypes.GUST_EMITTER_LARGE,
                this.getX(), this.getY() + 0.5, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0
        );
        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                this.getX(), this.getY() + 1.0, this.getZ(), 3, 1.0, 0.5, 1.0, 0.0
        );

        AABB shockwaveBounds = this.getBoundingBox().inflate(6.0, 3.0, 6.0);
        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, shockwaveBounds, e ->
                e != this && !(e instanceof SandwormEntity)
        );

        for (LivingEntity entity : targets) {
            if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
                continue;
            }
            entity.hurtServer(serverLevel, this.damageSources().mobAttack(this), 12.0f);
            Vec3 push = entity.position().subtract(this.position()).normalize().scale(1.2);
            entity.setDeltaMovement(push.x, 0.55, push.z);
        }
    }

    @Override
    public void tick() {
        super.tick();

        int breach = this.entityData.get(DATA_BREACH_TICKS);
        if (breach > 0) {
            this.entityData.set(DATA_BREACH_TICKS, breach - 1);
        }
        int bite = this.entityData.get(DATA_BITE_TICKS);
        if (bite > 0) {
            this.entityData.set(DATA_BITE_TICKS, bite - 1);
        }

        if (this.level().isClientSide()) {
            return;
        }

        LivingEntity currentTarget = this.getTarget();
        if (currentTarget != null && !this.canAttack(currentTarget)) {
            this.setTarget(null);
            currentTarget = null;
        }

        if (currentTarget == null && this.getSandwormState() == SandwormState.BURROWED) {
            this.setSandwormState(SandwormState.SURFACED_ASSAULT);
        } else if (currentTarget != null && this.getSandwormState() == SandwormState.SURFACED_ASSAULT && this.distanceToSqr(currentTarget) > 256.0) {
            this.startSubmerging();
        }

        if (this.getSandwormState() == SandwormState.SUBMERGING) {
            this.submergingTicks--;
            if (this.submergingTicks <= 0) {
                this.setSandwormState(SandwormState.BURROWED);
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SandwormBreachGoal(this));
        this.goalSelector.addGoal(3, new SandwormBiteAttackGoal(this));
        this.goalSelector.addGoal(4, new SandwormBurrowGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SandwormSeismicTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, (entity, level) -> isValidLivingPrey(entity)));
    }

    public boolean isValidLivingPrey(LivingEntity entity) {
        if (entity == null || !entity.isAlive() || entity.isSpectator() || entity == this) {
            return false;
        }
        if (entity instanceof SandwormEntity) {
            return false;
        }
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        BlockPos pos = entity.blockPosition();
        return !SeismicSurvivalHandler.getTracker().isInsideSafeZone(pos.getX(), pos.getZ());
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (!super.canAttack(target)) {
            return false;
        }
        if (target instanceof SandwormEntity) {
            return false;
        }
        BlockPos pos = target.blockPosition();
        return !SeismicSurvivalHandler.getTracker().isInsideSafeZone(pos.getX(), pos.getZ());
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (source.is(DamageTypes.IN_WALL) ||
                source.is(DamageTypes.FALL) ||
                source.is(DamageTypes.DROWN)) {
            return true;
        }

        if (this.getSandwormState() == SandwormState.BURROWED && source.is(DamageTypeTags.IS_PROJECTILE)) {
            return true;
        }

        return super.isInvulnerableTo(level, source);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (damageSource.getEntity() instanceof ServerPlayer player) {
            player.addTag("sandstorm.kill_sandworm");
        } else if (this.getLastHurtByMob() instanceof ServerPlayer player) {
            player.addTag("sandstorm.kill_sandworm");
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.SANDWORM_CHITIN, 3 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.SANDWORM_TOOTH, 1 + this.random.nextInt(2)));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SandStormSoundEvents.SANDWORM_RUMBLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SandStormSoundEvents.SANDWORM_ATTACK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SandStormSoundEvents.SANDWORM_EMERGE;
    }
}
