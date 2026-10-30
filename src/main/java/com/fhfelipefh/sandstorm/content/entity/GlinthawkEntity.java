package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GlinthawkEntity extends PathfinderMob implements Enemy, DeactivatableAndroid {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(GlinthawkEntity.class, EntityDataSerializers.BOOLEAN);
    private int attackCooldown = 0;

    public GlinthawkEntity(EntityType<? extends GlinthawkEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new GlinthawkMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 28.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FLYING_SPEED, 0.45)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GlinthawkCircleGoal(this));
        this.goalSelector.addGoal(2, new GlinthawkSwoopAttackGoal(this));

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
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isNoGravity() {
        return !this.isDeactivated();
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            this.setNoGravity(false);
            super.tick();
            return;
        }

        super.tick();

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && this.attackCooldown == 0 && this.level() instanceof ServerLevel serverLevel) {
            double distSq = this.distanceToSqr(target);
            if (distSq <= 256.0) {
                performCryoSpit(serverLevel, target);
                this.attackCooldown = 50;
            }
        }
    }

    private void performCryoSpit(ServerLevel serverLevel, LivingEntity target) {
        Vec3 start = this.getEyePosition();
        Vec3 targetPos = target.getEyePosition();
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.PHANTOM_BITE, this.getSoundSource(), 1.0f, 1.4f);

        int steps = 16;
        for (int i = 0; i <= steps; i++) {
            double frac = (double) i / (double) steps;
            Vec3 p = start.lerp(targetPos, frac);
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, p.x, p.y, p.z, 2, 0.1, 0.1, 0.1, 0.02);
            serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.01);
        }

        target.hurtServer(serverLevel, serverLevel.damageSources().mobAttack(this), 5.0f);
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1));
        target.setTicksFrozen(120);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1 + this.random.nextInt(2)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1));
        this.spawnAtLocation(level, new ItemStack(Items.COPPER_INGOT, 2 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(Items.PRISMARINE_CRYSTALS, 1 + this.random.nextInt(2)));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
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

    private static class GlinthawkMoveControl extends MoveControl {
        private final GlinthawkEntity glinthawk;

        public GlinthawkMoveControl(GlinthawkEntity glinthawk) {
            super(glinthawk);
            this.glinthawk = glinthawk;
        }

        @Override
        public void tick() {
            if (this.glinthawk.isDeactivated()) {
                return;
            }
            if (this.operation == Operation.MOVE_TO) {
                Vec3 delta = new Vec3(this.wantedX - this.glinthawk.getX(), this.wantedY - this.glinthawk.getY(), this.wantedZ - this.glinthawk.getZ());
                double dist = delta.length();
                if (dist < this.glinthawk.getBoundingBox().getSize()) {
                    this.operation = Operation.WAIT;
                    this.glinthawk.setDeltaMovement(this.glinthawk.getDeltaMovement().scale(0.5));
                } else {
                    this.glinthawk.setDeltaMovement(this.glinthawk.getDeltaMovement().add(delta.scale(this.speedModifier * 0.05 / dist)));
                    Vec3 move = this.glinthawk.getDeltaMovement();
                    this.glinthawk.setYRot(-((float) Mth.atan2(move.x, move.z)) * (180.0f / (float) Math.PI));
                    this.glinthawk.yBodyRot = this.glinthawk.getYRot();
                }
            }
        }
    }

    private static class GlinthawkCircleGoal extends Goal {
        private final GlinthawkEntity glinthawk;
        private float angle = 0.0f;

        public GlinthawkCircleGoal(GlinthawkEntity glinthawk) {
            this.glinthawk = glinthawk;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.glinthawk.isDeactivated() && this.glinthawk.getTarget() == null;
        }

        @Override
        public void tick() {
            this.angle += 0.05f;
            double radius = 12.0;
            double cx = this.glinthawk.getX() + Math.cos(this.angle) * radius;
            double cz = this.glinthawk.getZ() + Math.sin(this.angle) * radius;
            double cy = Math.max(165.0, this.glinthawk.getY() + (this.glinthawk.getRandom().nextDouble() - 0.5) * 2.0);
            this.glinthawk.getMoveControl().setWantedPosition(cx, cy, cz, 0.8);
        }
    }

    private static class GlinthawkSwoopAttackGoal extends Goal {
        private final GlinthawkEntity glinthawk;

        public GlinthawkSwoopAttackGoal(GlinthawkEntity glinthawk) {
            this.glinthawk = glinthawk;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.glinthawk.isDeactivated() && this.glinthawk.getTarget() != null && this.glinthawk.getTarget().isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = this.glinthawk.getTarget();
            if (target != null) {
                double targetY = target.getEyeY() + 4.0;
                this.glinthawk.getMoveControl().setWantedPosition(target.getX(), targetY, target.getZ(), 1.2);
            }
        }
    }
}
