package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class GrazerEntity extends PathfinderMob implements DeactivatableAndroid, OverridableMachine {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(GrazerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CANISTERS_INTACT = SynchedEntityData.defineId(GrazerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_OVERRIDDEN = SynchedEntityData.defineId(GrazerEntity.class, EntityDataSerializers.BOOLEAN);

    private int rotaryTicks = 0;
    private UUID overrideOwner;

    public GrazerEntity(EntityType<? extends GrazerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_CANISTERS_INTACT, true);
        builder.define(DATA_OVERRIDDEN, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(3, new DesertExplorationWanderGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, LivingEntity.class, 6.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
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

    public boolean areCanistersIntact() {
        return this.entityData.get(DATA_CANISTERS_INTACT);
    }

    public void setCanistersIntact(boolean intact) {
        this.entityData.set(DATA_CANISTERS_INTACT, intact);
    }

    public int getRotaryTicks() {
        return this.rotaryTicks;
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
        return new Vec3(0.0, dimensions.height() * 0.75, -0.15 * scale);
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
            this.setDeltaMovement(delta.x, 0.55, delta.z);
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa * 0.5f, 0.0, player.zza > 0 ? player.zza : player.zza * 0.35f);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
        BlockState groundState = this.getBlockStateOn();
        if (groundState.is(BlockTags.SAND)) {
            return speed * 1.35f;
        }
        return speed * 1.15f;
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            super.tick();
            return;
        }

        super.tick();

        if (this.level().isClientSide()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.001 || this.getTarget() != null) {
                this.rotaryTicks += 4;
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (this.areCanistersIntact() && (damageSource.is(DamageTypeTags.IS_FIRE) || damageSource.is(DamageTypeTags.IS_EXPLOSION) || this.random.nextFloat() < 0.15f)) {
            this.setCanistersIntact(false);
            level.explode(this, this.getX(), this.getY() + 1.0, this.getZ(), 2.2f, Level.ExplosionInteraction.NONE);
            level.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 1.2, this.getZ(), 12, 0.4, 0.4, 0.4, 0.1);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (this.areCanistersIntact()) {
            this.spawnAtLocation(level, new ItemStack(Items.BLAZE_POWDER, 2 + this.random.nextInt(3)));
        }
        this.spawnAtLocation(level, new ItemStack(Items.COPPER_INGOT, 1 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1));
        if (this.random.nextFloat() < 0.35f) {
            this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.IRON_GOLEM_STEP;
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
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putBoolean("CanistersIntact", this.areCanistersIntact());
        output.putBoolean("Overridden", this.isOverridden());
        if (this.overrideOwner != null) {
            output.putString("OverrideOwner", this.overrideOwner.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setCanistersIntact(input.getBooleanOr("CanistersIntact", true));
        this.setOverridden(input.getBooleanOr("Overridden", false));
        String ownerStr = input.getStringOr("OverrideOwner", "");
        if (!ownerStr.isEmpty()) {
            this.overrideOwner = UUID.fromString(ownerStr);
        }
    }
}
