package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
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
import java.util.UUID;

public class GlinthawkEntity extends PathfinderMob implements Enemy, DeactivatableAndroid, OverridableMachine {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(GlinthawkEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_OVERRIDDEN = SynchedEntityData.defineId(GlinthawkEntity.class, EntityDataSerializers.BOOLEAN);

    private int attackCooldown = 0;
    private UUID overrideOwner;

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
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_OVERRIDDEN, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GlinthawkDiveAttackGoal(this));
        this.goalSelector.addGoal(2, new GlinthawkWanderGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new GlinthawkAttackPlayerGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void checkDespawn() {
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
            this.setTarget(null);
            this.getNavigation().stop();
            this.ejectPassengers();
        }
    }

    @Override
    public Component getName() {
        if (this.isDeactivated()) {
            return Component.translatable(this.getType().getDescriptionId() + ".deactivated");
        }
        return super.getName();
    }

    @Override
    public boolean isOverridden() {
        return this.entityData.get(DATA_OVERRIDDEN);
    }

    @Override
    public void setOverridden(boolean overridden) {
        this.entityData.set(DATA_OVERRIDDEN, overridden);
    }

    @Override
    public UUID getOverrideOwner() {
        return this.overrideOwner;
    }

    @Override
    public void setOverrideOwner(UUID owner) {
        this.overrideOwner = owner;
    }

    @Override
    public boolean canBeRiddenBy(Player player) {
        return this.isOverridden() && !this.isDeactivated();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.canBeRiddenBy(player) && hand == InteractionHand.MAIN_HAND && !player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return new Vec3(0.0, dimensions.height() * 0.55, -0.15 * scale);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5f);
        this.yRotO = this.getYRot();
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.yBodyRot;

        Vec3 look = player.getLookAngle();
        double forward = player.zza;
        double strafe = player.xxa;

        double vx = (look.x * forward + Math.cos(Math.toRadians(player.getYRot())) * strafe) * 0.45;
        double vz = (look.z * forward + Math.sin(Math.toRadians(player.getYRot())) * strafe) * 0.45;
        double vy = this.getDeltaMovement().y * 0.8;

        if (player.isJumping()) {
            vy = 0.35;
        } else if (forward > 0) {
            vy = look.y * 0.35;
        }

        this.setDeltaMovement(vx, vy, vz);
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            this.setDeltaMovement(0, -0.2, 0);
            super.tick();
            return;
        }

        super.tick();

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1 + this.random.nextInt(2)));
        this.spawnAtLocation(level, new ItemStack(Items.FEATHER, 3 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.ADVANCED_REFRACTION_LENS, 1));
        if (this.random.nextFloat() < 0.35f) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1));
        }
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
        output.putBoolean("Overridden", this.isOverridden());
        if (this.overrideOwner != null) {
            output.putString("OverrideOwner", this.overrideOwner.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setOverridden(input.getBooleanOr("Overridden", false));
        String ownerStr = input.getStringOr("OverrideOwner", "");
        if (!ownerStr.isEmpty()) {
            this.overrideOwner = UUID.fromString(ownerStr);
        }
    }

    private static class GlinthawkAttackPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final GlinthawkEntity machine;

        public GlinthawkAttackPlayerGoal(GlinthawkEntity machine) {
            super(machine, Player.class, true);
            this.machine = machine;
        }

        @Override
        public boolean canUse() {
            return !this.machine.isOverridden() && !this.machine.isDeactivated() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.machine.isOverridden() && !this.machine.isDeactivated() && super.canContinueToUse();
        }
    }

    static class GlinthawkMoveControl extends MoveControl {
        private final GlinthawkEntity hawk;

        public GlinthawkMoveControl(GlinthawkEntity hawk) {
            super(hawk);
            this.hawk = hawk;
        }

        @Override
        public void tick() {
            if (this.hawk.isVehicle()) {
                return;
            }
            if (this.operation == Operation.MOVE_TO) {
                Vec3 dir = new Vec3(this.wantedX - this.hawk.getX(), this.wantedY - this.hawk.getY(), this.wantedZ - this.hawk.getZ());
                double dist = dir.length();
                if (dist < this.hawk.getBoundingBox().getSize()) {
                    this.operation = Operation.WAIT;
                    this.hawk.setDeltaMovement(this.hawk.getDeltaMovement().scale(0.5));
                } else {
                    this.hawk.setDeltaMovement(this.hawk.getDeltaMovement().add(dir.scale(this.speedModifier * 0.05 / dist)));
                    Vec3 motion = this.hawk.getDeltaMovement();
                    this.hawk.setYRot(-((float) Mth.atan2(motion.x, motion.z)) * (180.0f / (float) Math.PI));
                    this.hawk.yBodyRot = this.hawk.getYRot();
                }
            }
        }
    }

    static class GlinthawkWanderGoal extends Goal {
        private final GlinthawkEntity hawk;

        public GlinthawkWanderGoal(GlinthawkEntity hawk) {
            this.hawk = hawk;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.hawk.getMoveControl().hasWanted() && this.hawk.getRandom().nextInt(30) == 0 && !this.hawk.isVehicle();
        }

        @Override
        public void start() {
            BlockPos targetPos = this.hawk.blockPosition().offset(
                    this.hawk.getRandom().nextInt(24) - 12,
                    this.hawk.getRandom().nextInt(10) - 3,
                    this.hawk.getRandom().nextInt(24) - 12
            );
            this.hawk.getMoveControl().setWantedPosition(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 0.8);
        }
    }

    static class GlinthawkDiveAttackGoal extends Goal {
        private final GlinthawkEntity hawk;

        public GlinthawkDiveAttackGoal(GlinthawkEntity hawk) {
            this.hawk = hawk;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.hawk.getTarget();
            return target != null && target.isAlive() && this.hawk.attackCooldown == 0 && !this.hawk.isVehicle();
        }

        @Override
        public void tick() {
            LivingEntity target = this.hawk.getTarget();
            if (target == null) {
                return;
            }
            this.hawk.getMoveControl().setWantedPosition(target.getX(), target.getY() + 1.2, target.getZ(), 1.4);
            if (this.hawk.distanceToSqr(target) < 4.0) {
                this.hawk.doHurtTarget((ServerLevel) this.hawk.level(), target);
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
                this.hawk.attackCooldown = 80;
                this.hawk.setDeltaMovement(this.hawk.getDeltaMovement().add(0, 0.45, 0));
                if (this.hawk.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CRIT, this.hawk.getX(), this.hawk.getY(), this.hawk.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
                }
            }
        }
    }
}
