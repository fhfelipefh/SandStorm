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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
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
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, SandwormState.BURROWED.ordinal());
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

    public void startSubmerging() {
        this.setSandwormState(SandwormState.SUBMERGING);
        this.submergingTicks = 25;
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), SandStormSoundEvents.SANDWORM_RUMBLE, SoundSource.HOSTILE, 1.0f, 0.8f);
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                    this.getX(), this.getY() + 0.2, this.getZ(), 20, 0.6, 0.4, 0.6, 0.1
            );
        }
    }

    public void triggerBreachShockwave() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.playSound(null, this.blockPosition(), SandStormSoundEvents.SANDWORM_EMERGE, SoundSource.HOSTILE, 1.4f, 0.9f);
        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                this.getX(), this.getY() + 0.5, this.getZ(), 45, 1.2, 0.8, 1.2, 0.2
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

        if (this.level().isClientSide()) {
            return;
        }

        LivingEntity currentTarget = this.getTarget();
        if (currentTarget != null && !this.canAttack(currentTarget)) {
            this.setTarget(null);
            if (this.getSandwormState().isSurfaced()) {
                this.startSubmerging();
            }
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

        this.targetSelector.addGoal(1, new SandwormSeismicTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (!super.canAttack(target)) {
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
