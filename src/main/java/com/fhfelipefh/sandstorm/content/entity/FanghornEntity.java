package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
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

public class FanghornEntity extends PathfinderMob implements Enemy, DeactivatableAndroid, RangedAttackMob, OverridableMachine {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(FanghornEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHARGING_HORNS = SynchedEntityData.defineId(FanghornEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_OVERRIDDEN = SynchedEntityData.defineId(FanghornEntity.class, EntityDataSerializers.BOOLEAN);

    private int hornChargeTicks = 0;
    private UUID overrideOwner;

    public FanghornEntity(EntityType<? extends FanghornEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.MOVEMENT_SPEED, 0.29)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_CHARGING_HORNS, false);
        builder.define(DATA_OVERRIDDEN, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 60, 16.0f));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, false));
        this.goalSelector.addGoal(3, new DesertExplorationWanderGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, LivingEntity.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new FanghornAttackPlayerGoal(this));
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

    public boolean isChargingHorns() {
        return this.entityData.get(DATA_CHARGING_HORNS);
    }

    public void setChargingHorns(boolean charging) {
        this.entityData.set(DATA_CHARGING_HORNS, charging);
    }

    public int getHornChargeTicks() {
        return this.hornChargeTicks;
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
        return new Vec3(0.0, dimensions.height() * 0.78, -0.15 * scale);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5f);
        this.yRotO = this.getYRot();
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.yBodyRot;

        if (player.isJumping() && this.onGround()) {
            Vec3 delta = this.getDeltaMovement();
            this.setDeltaMovement(delta.x, 0.60, delta.z);
        }

        if (this.getDeltaMovement().horizontalDistanceSqr() > 0.05) {
            damageCollidingEnemies();
        }
    }

    private void damageCollidingEnemies() {
        if (this.level().isClientSide()) {
            return;
        }
        AABB box = this.getBoundingBox().inflate(0.5, 0.0, 0.5);
        List<LivingEntity> enemies = this.level().getEntitiesOfClass(LivingEntity.class, box, e -> e != this && e != this.getFirstPassenger() && !e.isAlliedTo(this));
        for (LivingEntity target : enemies) {
            target.hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 8.0f);
            target.push(this.getDeltaMovement().x * 1.5, 0.2, this.getDeltaMovement().z * 1.5);
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa * 0.5f, 0.0, player.zza > 0 ? player.zza : player.zza * 0.35f);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.25f;
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            super.tick();
            return;
        }

        super.tick();

        if (this.isChargingHorns()) {
            this.hornChargeTicks++;
            if (this.level().isClientSide()) {
                Vec3 look = this.getLookAngle();
                Vec3 headPos = this.position().add(look.x * 1.2, 1.4, look.z * 1.2);
                this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, headPos.x, headPos.y, headPos.z, 0.0, 0.05, 0.0);
            }
        } else {
            this.hornChargeTicks = Math.max(0, this.hornChargeTicks - 1);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.setChargingHorns(true);
        Vec3 dir = target.position().subtract(this.position()).normalize();
        this.setDeltaMovement(dir.x * 1.2, 0.2, dir.z * 1.2);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY() + 1.2, this.getZ(), 16, 0.3, 0.3, 0.3, 0.1);
        }

        this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.8f, 2.0f);
        target.hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 7.0f);
        this.setChargingHorns(false);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
            level.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.0, this.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
            return super.hurtServer(level, damageSource, amount * 0.5f);
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2));
        this.spawnAtLocation(level, new ItemStack(Items.COPPER_INGOT, 2));
        this.spawnAtLocation(level, new ItemStack(Items.IRON_INGOT, 2 + this.random.nextInt(3)));
        if (this.random.nextFloat() < 0.4f) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putBoolean("ChargingHorns", this.isChargingHorns());
        output.putBoolean("Overridden", this.isOverridden());
        if (this.overrideOwner != null) {
            output.putString("OverrideOwner", this.overrideOwner.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setChargingHorns(input.getBooleanOr("ChargingHorns", false));
        this.setOverridden(input.getBooleanOr("Overridden", false));
        String ownerStr = input.getStringOr("OverrideOwner", "");
        if (!ownerStr.isEmpty()) {
            this.overrideOwner = UUID.fromString(ownerStr);
        }
    }

    private static class FanghornAttackPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final FanghornEntity machine;

        public FanghornAttackPlayerGoal(FanghornEntity machine) {
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
}
