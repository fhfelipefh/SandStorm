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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class WaterwingEntity extends TamableAnimal implements DeactivatableAndroid, OverridableMachine {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(WaterwingEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DIVING = SynchedEntityData.defineId(WaterwingEntity.class, EntityDataSerializers.BOOLEAN);
    private UUID overrideOwner;

    public WaterwingEntity(EntityType<? extends WaterwingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 55.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.FOLLOW_RANGE, 36.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_DIVING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.25, 10.0f, 2.0f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
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
    public void tick() {
        if (this.isDeactivated()) {
            this.setDeltaMovement(0, Math.min(0, this.getDeltaMovement().y), 0);
            super.tick();
            return;
        }
        super.tick();
    }

    @Override
    public boolean isOverridden() {
        return this.isTame();
    }

    @Override
    public void setOverridden(boolean overridden) {
        this.setTame(overridden, false);
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
        return this.isTame() && !this.isDeactivated();
    }

    public boolean isDiving() {
        return this.entityData.get(DATA_DIVING);
    }

    public void setDiving(boolean diving) {
        this.entityData.set(DATA_DIVING, diving);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!this.isTame()) {
            if (itemStack.is(SandStormItems.CIRCUIT_BOARD) || itemStack.is(SandStormItems.ELECTRIC_COMPONENT)) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                this.tame(player);
                this.setOrderedToSit(false);
                this.level().playSound(null, this.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, this.getSoundSource(), 1.0f, 1.2f);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getEyeY(), this.getZ(), 8, 0.4, 0.4, 0.4, 0.05);
                }
                return InteractionResult.SUCCESS;
            }
        } else if (this.isOwnedBy(player)) {
            if (player.isSecondaryUseActive()) {
                this.setOrderedToSit(!this.isOrderedToSit());
                return InteractionResult.SUCCESS;
            }
            if (itemStack.isEmpty()) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return new Vec3(0.0, dimensions.height() * 0.65, -0.15 * scale);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float f = player.xxa * 0.5f;
        float f1 = player.zza;
        if (f1 <= 0.0f) {
            f1 *= 0.25f;
        }
        return new Vec3(f, 0.0, f1);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5f);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        this.xRotO = this.getXRot();

        if (this.isInWater()) {
            this.setDiving(true);
            player.setAirSupply(player.getMaxAirSupply());
            Vec3 motion = this.getDeltaMovement();
            if (player.isJumping()) {
                this.setDeltaMovement(motion.x, 0.35, motion.z);
            } else if (player.isDescending()) {
                this.setDeltaMovement(motion.x, -0.35, motion.z);
            }
        } else {
            this.setDiving(false);
            if (player.isJumping()) {
                this.setDeltaMovement(this.getDeltaMovement().x, 0.35, this.getDeltaMovement().z);
            }
        }
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        if (this.isInWater()) {
            return 0.55f;
        }
        return 0.35f;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1 + this.random.nextInt(2)));
        this.spawnAtLocation(level, new ItemStack(Items.PRISMARINE_SHARD, 3 + this.random.nextInt(4)));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", this.isDeactivated());
        if (this.overrideOwner != null) {
            output.putString("OverrideOwner", this.overrideOwner.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        String ownerStr = input.getStringOr("OverrideOwner", "");
        if (!ownerStr.isEmpty()) {
            this.overrideOwner = UUID.fromString(ownerStr);
        }
    }
}
