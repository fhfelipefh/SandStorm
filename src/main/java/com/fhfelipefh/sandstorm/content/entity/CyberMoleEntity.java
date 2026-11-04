package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.CompanionGroundPathNavigation;
import com.fhfelipefh.sandstorm.content.entity.ai.CyberMoleBurrowGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.CyberMoleEruptGoal;
import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class CyberMoleEntity extends TamableAnimal implements DeactivatableAndroid, OverridableMachine {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(CyberMoleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_BURROWED = SynchedEntityData.defineId(CyberMoleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SURFACED_COMBAT = SynchedEntityData.defineId(CyberMoleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_OVERRIDDEN = SynchedEntityData.defineId(CyberMoleEntity.class, EntityDataSerializers.BOOLEAN);

    private int surfacedTicks = 0;
    private int deactivationTicks = 0;
    private float drillRotation = 0.0f;
    private UUID overrideOwner = null;

    public CyberMoleEntity(EntityType<? extends CyberMoleEntity> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.DAMAGING, 32.0f);
        this.setPathfindingMalus(PathType.DAMAGING_IN_NEIGHBOR, 16.0f);
        this.setPathfindingMalus(PathType.DAMAGE_CAUTIOUS, 16.0f);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new CompanionGroundPathNavigation(this, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 16.0)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_BURROWED, false);
        builder.define(DATA_SURFACED_COMBAT, false);
        builder.define(DATA_OVERRIDDEN, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new CyberMoleEruptGoal(this));
        this.goalSelector.addGoal(4, new CyberMoleBurrowGoal(this));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.2, 4.0f, 14.0f));
        this.goalSelector.addGoal(6, new DesertExplorationWanderGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean isDeactivated() {
        return this.entityData.get(DATA_DEACTIVATED);
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.entityData.set(DATA_DEACTIVATED, deactivated);
        if (deactivated) {
            this.setBurrowed(false);
            this.setSurfacedCombat(false);
            this.deactivationTicks = 200;
            this.getNavigation().stop();
        }
    }

    public boolean isBurrowed() {
        return this.entityData.get(DATA_BURROWED);
    }

    public void setBurrowed(boolean burrowed) {
        this.entityData.set(DATA_BURROWED, burrowed);
    }

    public boolean isSurfacedCombat() {
        return this.entityData.get(DATA_SURFACED_COMBAT);
    }

    public void setSurfacedCombat(boolean surfaced) {
        this.entityData.set(DATA_SURFACED_COMBAT, surfaced);
    }

    public float getDrillRotation() {
        return this.drillRotation;
    }

    public void triggerEruption(LivingEntity target) {
        this.setSurfacedCombat(true);
        this.setBurrowed(false);
        this.surfacedTicks = 35;

        Level currentLevel = this.level();
        if (currentLevel instanceof ServerLevel serverLevel) {
            target.hurtServer(serverLevel, this.damageSources().mobAttack(this), 18.0f);
            Vec3 currentMotion = target.getDeltaMovement();
            target.setDeltaMovement(currentMotion.x, Math.max(currentMotion.y, 0.65), currentMotion.z);

            BlockPos pos = target.blockPosition();
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SANDSTONE.defaultBlockState()),
                    target.getX(), target.getY() + 0.3, target.getZ(),
                    25, 0.5, 0.4, 0.5, 0.15
            );
            serverLevel.sendParticles(
                    ParticleTypes.CRIT,
                    target.getX(), target.getY() + 0.6, target.getZ(),
                    15, 0.3, 0.3, 0.3, 0.1
            );
            serverLevel.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.8f, 1.6f);
            serverLevel.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.HOSTILE, 1.2f, 0.7f);
        }
    }

    public void tickSurfacedTimer() {
        if (this.surfacedTicks > 0) {
            this.surfacedTicks--;
            if (this.surfacedTicks <= 0) {
                this.finishSurfacedCombat();
            }
        }
    }

    public void finishSurfacedCombat() {
        this.setSurfacedCombat(false);
        if (this.getTarget() != null && this.getTarget().isAlive()) {
            this.setBurrowed(true);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isBurrowed()) {
            boolean isBypass = source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                    || source.is(DamageTypeTags.IS_EXPLOSION);
            if (!isBypass) {
                level.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                        this.getX(), this.getY() + 0.2, this.getZ(),
                        8, 0.3, 0.2, 0.3, 0.05
                );
                level.playSound(null, this.blockPosition(), SoundEvents.SAND_HIT, SoundSource.NEUTRAL, 0.6f, 1.2f);
                return false;
            }
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isAlive() && !this.isDeactivated()) {
            this.drillRotation += 0.35f;
            if (this.drillRotation > 6.2831855f) {
                this.drillRotation -= 6.2831855f;
            }
        }

        if (!this.level().isClientSide()) {
            if (this.isDeactivated()) {
                if (this.deactivationTicks > 0) {
                    this.deactivationTicks--;
                    if (this.deactivationTicks <= 0) {
                        this.setDeactivated(false);
                    }
                }
            }

            if (this.isBurrowed() && this.level() instanceof ServerLevel serverLevel) {
                if (this.tickCount % 5 == 0 && this.getDeltaMovement().horizontalDistanceSqr() > 0.001) {
                    serverLevel.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                            this.getX(), this.getY() + 0.1, this.getZ(),
                            5, 0.25, 0.1, 0.25, 0.03
                    );
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Level currentLevel = this.level();

        if (this.isTame() && this.isOwnedBy(player)) {
            if (itemStack.is(SandStormItems.SCRAP_METAL)) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(12.0f);
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                    if (currentLevel instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                this.getX(), this.getY() + 0.5, this.getZ(),
                                8, 0.2, 0.2, 0.2, 0.05);
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.ANVIL_USE, this.getSoundSource(), 0.8f, 1.4f);
                    }
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }

            if (!player.isSecondaryUseActive() && !itemStack.is(SandStormItems.REPROGRAMMER_TOOL)) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                if (this.isOrderedToSit()) {
                    this.setBurrowed(false);
                    this.setSurfacedCombat(false);
                }
                SoundEvent sound = this.isOrderedToSit()
                        ? SandStormSoundEvents.CYBER_COMPANION_SERVO_RETRACT
                        : SandStormSoundEvents.CYBER_COMPANION_SERVO_EXPAND;
                this.playSound(sound, 0.8f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
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
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putBoolean("Burrowed", this.isBurrowed());
        output.putBoolean("SurfacedCombat", this.isSurfacedCombat());
        output.putBoolean("Overridden", this.isOverridden());
        if (this.overrideOwner != null) {
            output.putString("OverrideOwner", this.overrideOwner.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setBurrowed(input.getBooleanOr("Burrowed", false));
        this.setSurfacedCombat(input.getBooleanOr("SurfacedCombat", false));
        this.setOverridden(input.getBooleanOr("Overridden", false));
        String ownerStr = input.getStringOr("OverrideOwner", "");
        if (!ownerStr.isEmpty()) {
            try {
                this.overrideOwner = UUID.fromString(ownerStr);
            } catch (IllegalArgumentException ignored) {
                this.overrideOwner = null;
            }
        }
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof CyberMoleEntity otherMole && otherMole.isTame() && otherMole.getOwner() == owner) {
            return false;
        }
        return super.wantsToAttack(target, owner);
    }
}

