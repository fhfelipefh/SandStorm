package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.SandwormBiteAttackGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormBreachGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormBurrowGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormSeismicTargetGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormShowcaseGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormSlitherChaseGoal;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
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
    private static final EntityDataAccessor<Integer> DATA_SUBMERGE_TICKS = SynchedEntityData.defineId(SandwormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BITE_TICKS = SynchedEntityData.defineId(SandwormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_REARING = SynchedEntityData.defineId(SandwormEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<BlockPos> DATA_STRIKE_POS = SynchedEntityData.defineId(SandwormEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> DATA_SHOWCASE_MODE = SynchedEntityData.defineId(SandwormEntity.class, EntityDataSerializers.BOOLEAN);

    private int surfaceTicks = 140;
    private int submergingTicks = 0;
    private float groundSink = 0.4f;
    private float groundSlopePitch = 0.0f;
    private float groundSlopeRoll = 0.0f;

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
        builder.define(DATA_SUBMERGE_TICKS, 0);
        builder.define(DATA_BITE_TICKS, 0);
        builder.define(DATA_REARING, 1.0f);
        builder.define(DATA_STRIKE_POS, BlockPos.ZERO);
        builder.define(DATA_SHOWCASE_MODE, false);
    }

    public SandwormState getSandwormState() {
        return SandwormState.fromOrdinal(this.entityData.get(DATA_STATE));
    }

    public void setSandwormState(SandwormState state) {
        this.entityData.set(DATA_STATE, state.ordinal());
    }

    public float getRearingProgress() {
        return this.entityData.get(DATA_REARING);
    }

    public void setRearingProgress(float progress) {
        this.entityData.set(DATA_REARING, Mth.clamp(progress, 0.0f, 1.0f));
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

    public void setSubmergeTicks(int ticks) {
        this.entityData.set(DATA_SUBMERGE_TICKS, ticks);
    }

    public BlockPos getStrikePos() {
        return this.entityData.get(DATA_STRIKE_POS);
    }

    public int getBiteTicks() {
        return this.entityData.get(DATA_BITE_TICKS);
    }

    public void triggerBiteAnimation(BlockPos strikePos) {
        this.entityData.set(DATA_STRIKE_POS, strikePos);
        this.entityData.set(DATA_BITE_TICKS, 28);
    }

    public void triggerBiteAnimation() {
        this.triggerBiteAnimation(this.blockPosition());
    }

    public float getBreachAnimationProgress(float partialTick) {
        int ticks = this.entityData.get(DATA_BREACH_TICKS);
        if (ticks <= 0) {
            return 0.0f;
        }
        return Mth.clamp((35.0f - (ticks - partialTick)) / 35.0f, 0.0f, 1.0f);
    }

    public float getSubmergeAnimationProgress(float partialTick) {
        int ticks = this.entityData.get(DATA_SUBMERGE_TICKS);
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
        return Mth.clamp((28.0f - (ticks - partialTick)) / 28.0f, 0.0f, 1.0f);
    }

    public void startSubmerging() {
        this.setSandwormState(SandwormState.SUBMERGING);
        this.submergingTicks = 35;
        this.setSubmergeTicks(35);
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
                this.getX(), this.getY() + 0.5, this.getZ(), 140, 3.5, 2.0, 3.5, 0.45
        );
        serverLevel.sendParticles(
                ParticleTypes.GUST_EMITTER_LARGE,
                this.getX(), this.getY() + 0.5, this.getZ(), 2, 0.0, 0.0, 0.0, 0.0
        );
        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                this.getX(), this.getY() + 1.0, this.getZ(), 4, 1.5, 0.5, 1.5, 0.0
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

    public void executeBiteImpact(ServerLevel serverLevel) {
        BlockPos strikePos = this.getStrikePos();
        Vec3 impactCenter;
        if (strikePos == null || strikePos.equals(BlockPos.ZERO)) {
            double yawRad = Math.toRadians(this.getYRot());
            impactCenter = new Vec3(this.getX() - Math.sin(yawRad) * 12.0, this.getY(), this.getZ() + Math.cos(yawRad) * 12.0);
        } else {
            impactCenter = Vec3.atCenterOf(strikePos);
        }

        serverLevel.playSound(null, BlockPos.containing(impactCenter), SandStormSoundEvents.SANDWORM_ATTACK, SoundSource.HOSTILE, 2.2f, 0.8f);
        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                impactCenter.x, impactCenter.y + 0.5, impactCenter.z, 90, 2.2, 1.2, 2.2, 0.35
        );
        serverLevel.sendParticles(
                ParticleTypes.GUST_EMITTER_LARGE,
                impactCenter.x, impactCenter.y + 0.5, impactCenter.z, 2, 0.0, 0.0, 0.0, 0.0
        );
        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                impactCenter.x, impactCenter.y + 0.8, impactCenter.z, 3, 1.0, 0.5, 1.0, 0.0
        );

        AABB impactBounds = new AABB(
                impactCenter.x - 4.5, impactCenter.y - 2.5, impactCenter.z - 4.5,
                impactCenter.x + 4.5, impactCenter.y + 3.5, impactCenter.z + 4.5
        );

        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, impactBounds, e ->
                e != this && !(e instanceof SandwormEntity)
        );

        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 8.0f;

        for (LivingEntity entity : targets) {
            if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
                continue;
            }
            if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(entity.getBlockX(), entity.getBlockZ())) {
                continue;
            }

            entity.hurtServer(serverLevel, this.damageSources().mobAttack(this), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 2));

            Vec3 push = entity.position().subtract(impactCenter);
            if (push.horizontalDistanceSqr() < 0.001) {
                double yawRad = Math.toRadians(this.getYRot());
                push = new Vec3(-Math.sin(yawRad), 0.35, Math.cos(yawRad));
            } else {
                push = push.normalize();
            }
            entity.setDeltaMovement(push.x * 1.5, 0.45, push.z * 1.5);
        }
    }

    @Override
    public void tick() {
        super.tick();

        int breach = this.entityData.get(DATA_BREACH_TICKS);
        if (breach > 0) {
            this.entityData.set(DATA_BREACH_TICKS, breach - 1);
        }
        int submerge = this.entityData.get(DATA_SUBMERGE_TICKS);
        if (submerge > 0) {
            this.entityData.set(DATA_SUBMERGE_TICKS, submerge - 1);
        }
        int bite = this.entityData.get(DATA_BITE_TICKS);
        if (bite > 0) {
            if (bite == 8 && this.level() instanceof ServerLevel serverLevel) {
                this.executeBiteImpact(serverLevel);
            }
            this.entityData.set(DATA_BITE_TICKS, bite - 1);
        }

        updateGroundTerrain();

        if (this.level().isClientSide()) {
            spawnGroundAmbientParticles();
            return;
        }

        if (!this.isShowcaseMode()) {
            LivingEntity currentTarget = this.getTarget();
            if (currentTarget != null && !this.canAttack(currentTarget)) {
                this.setTarget(null);
                currentTarget = null;
            }

            if (currentTarget == null && this.getSandwormState() == SandwormState.BURROWED) {
                this.setSandwormState(SandwormState.SURFACED_ASSAULT);
            } else if (currentTarget != null && this.getSandwormState() == SandwormState.SURFACED_ASSAULT && this.distanceToSqr(currentTarget) > 196.0) {
                this.startSubmerging();
            }
        }

        this.setJumping(false);

        if (this.getSandwormState() == SandwormState.BREACHING) {
            this.setDeltaMovement(0.0, Math.min(0.0, this.getDeltaMovement().y), 0.0);
            this.getNavigation().stop();
        }

        if (this.getDeltaMovement().horizontalDistanceSqr() > 0.001 && this.level() instanceof ServerLevel serverLevel) {
            double yawRad = Math.toRadians(this.getYRot());
            double sideX = Math.cos(yawRad) * 2.5;
            double sideZ = Math.sin(yawRad) * 2.5;
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), this.getX() - sideX, this.getY() + 0.3, this.getZ() - sideZ, 4, 0.4, 0.2, 0.4, 0.1);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), this.getX() + sideX, this.getY() + 0.3, this.getZ() + sideZ, 4, 0.4, 0.2, 0.4, 0.1);
            serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.2, this.getZ(), 2, 0.8, 0.2, 0.8, 0.02);
        }

        if (this.getSandwormState() == SandwormState.SUBMERGING) {
            this.submergingTicks--;
            if (this.submergingTicks <= 0) {
                this.setSandwormState(SandwormState.BURROWED);
                this.setSubmergeTicks(0);
            }
        }

        AABB bodyBounds = this.getBoundingBox().inflate(1.5, 0.5, 1.5);
        List<LivingEntity> insideEntities = this.level().getEntitiesOfClass(LivingEntity.class, bodyBounds, e -> e != this && !(e instanceof SandwormEntity));
        for (LivingEntity entity : insideEntities) {
            if (entity instanceof Player player && player.isSpectator()) {
                continue;
            }
            double dx = entity.getX() - this.getX();
            double dz = entity.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            Vec3 pushDir;
            if (dist < 0.05) {
                float randomAngle = this.getRandom().nextFloat() * ((float) Math.PI * 2.0f);
                pushDir = new Vec3(Math.cos(randomAngle), 0.35, Math.sin(randomAngle)).normalize();
            } else {
                pushDir = new Vec3(dx / dist, 0.35, dz / dist).normalize();
            }
            entity.setDeltaMovement(pushDir.x * 1.6, 0.45, pushDir.z * 1.6);
            if (this.level() instanceof ServerLevel serverLevel) {
                if (!(entity instanceof Player player && player.isCreative())) {
                    entity.hurtServer(serverLevel, this.damageSources().mobAttack(this), 6.0f);
                }
            }
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SandwormShowcaseGoal(this));
        this.goalSelector.addGoal(1, new SandwormBreachGoal(this));
        this.goalSelector.addGoal(2, new SandwormBiteAttackGoal(this));
        this.goalSelector.addGoal(3, new SandwormSlitherChaseGoal(this));
        this.goalSelector.addGoal(4, new SandwormBurrowGoal(this));

        this.targetSelector.addGoal(1, new SandwormSeismicTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.isShowcaseMode()) {
            return false;
        }
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
        if (this.isShowcaseMode()) {
            return true;
        }

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

    private void updateGroundTerrain() {
        if (this.getSandwormState() == SandwormState.BURROWED) {
            this.groundSink = 0.0f;
            this.groundSlopePitch = 0.0f;
            this.groundSlopeRoll = 0.0f;
            return;
        }

        double entityY = this.getY();
        double minSurfaceY = entityY;
        double northSurfaceY = entityY;
        double southSurfaceY = entityY;
        double eastSurfaceY = entityY;
        double westSurfaceY = entityY;

        double radius = 4.2;
        int sampleCount = 8;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < sampleCount; i++) {
            double angle = (i / (double) sampleCount) * Math.PI * 2.0;
            double sx = this.getX() + Math.cos(angle) * radius;
            double sz = this.getZ() + Math.sin(angle) * radius;

            pos.set(sx, entityY + 2, sz);
            while (pos.getY() > entityY - 8 && this.level().getBlockState(pos).isAir()) {
                pos.move(0, -1, 0);
            }
            double surfaceY = pos.getY() + 1.0;
            if (surfaceY < minSurfaceY) {
                minSurfaceY = surfaceY;
            }

            if (i == 0) {
                eastSurfaceY = surfaceY;
            } else if (i == 2) {
                southSurfaceY = surfaceY;
            } else if (i == 4) {
                westSurfaceY = surfaceY;
            } else if (i == 6) {
                northSurfaceY = surfaceY;
            }
        }

        double drop = entityY - minSurfaceY;
        float targetSink = (float) Math.max(0.45, drop + 0.35);
        this.groundSink = Mth.lerp(0.2f, this.groundSink, targetSink);

        double slopeZ = (southSurfaceY - northSurfaceY) / (radius * 2.0);
        double slopeX = (eastSurfaceY - westSurfaceY) / (radius * 2.0);
        this.groundSlopePitch = Mth.lerp(0.15f, this.groundSlopePitch, (float) slopeZ);
        this.groundSlopeRoll = Mth.lerp(0.15f, this.groundSlopeRoll, (float) slopeX);
    }

    private void spawnGroundAmbientParticles() {
        if (this.getSandwormState() == SandwormState.BURROWED) {
            return;
        }

        float baseRadius = 4.2f;
        int count = this.getSandwormState() == SandwormState.BREACHING ? 10 : 5;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < count; i++) {
            float angle = this.random.nextFloat() * ((float) Math.PI * 2.0f);
            double dist = baseRadius * (0.8 + this.random.nextFloat() * 0.4);
            double px = this.getX() + Math.cos(angle) * dist;
            double pz = this.getZ() + Math.sin(angle) * dist;

            pos.set(px, this.getY() + 2, pz);
            while (pos.getY() > this.getY() - 8 && this.level().getBlockState(pos).isAir()) {
                pos.move(0, -1, 0);
            }
            double py = pos.getY() + 1.0;

            double vx = (this.random.nextDouble() - 0.5) * 0.2;
            double vy = this.random.nextDouble() * 0.18 + 0.04;
            double vz = (this.random.nextDouble() - 0.5) * 0.2;

            this.level().addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                    px, py + 0.05, pz,
                    vx, vy, vz
            );

            if (this.random.nextInt(3) == 0) {
                this.level().addParticle(
                        ParticleTypes.DUST_PLUME,
                        px, py + 0.05, pz,
                        vx * 0.3, 0.04, vz * 0.3
                );
            }
        }
    }

    public float getGroundSink() {
        return this.groundSink;
    }

    public float getGroundSlopePitch() {
        return this.groundSlopePitch;
    }

    public float getGroundSlopeRoll() {
        return this.groundSlopeRoll;
    }

    public boolean isShowcaseMode() {
        return this.entityData.get(DATA_SHOWCASE_MODE);
    }

    public void setShowcaseMode(boolean showcase) {
        this.entityData.set(DATA_SHOWCASE_MODE, showcase);
        if (showcase) {
            this.setPermanentlyInvulnerable(true);
            this.setPersistenceRequired();
        }
    }
}
