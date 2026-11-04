package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.entity.ai.CompanionGroundPathNavigation;
import com.fhfelipefh.sandstorm.content.entity.ai.CompanionTrapHelper;
import com.fhfelipefh.sandstorm.content.entity.ai.DesertExplorationWanderGoal;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CyberHoundEntity extends TamableAnimal implements DeactivatableAndroid {
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(CyberHoundEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_REPAIR_PROGRESS = SynchedEntityData.defineId(CyberHoundEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_REPAIR_REQUIRED = SynchedEntityData.defineId(CyberHoundEntity.class, EntityDataSerializers.INT);

    private int alertCooldown = 0;
    private int spikeJumpCooldown = 0;

    public CyberHoundEntity(EntityType<? extends CyberHoundEntity> entityType, Level level) {
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
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_REPAIR_PROGRESS, 0);
        builder.define(DATA_REPAIR_REQUIRED, 2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.15, 10.0f, 2.0f));
        this.goalSelector.addGoal(4, new DesertExplorationWanderGoal(this, 1.05));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, DerelictAutomatonEntity.class, false));
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
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1.0);
            this.setHealth(1.0f);
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
            this.setTarget(null);
            this.getNavigation().stop();
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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Level currentLevel = this.level();

        if (this.isDeactivated()) {
            if (itemStack.is(SandStormItems.SCRAP_METAL)) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                int req = Math.max(1, this.entityData.get(DATA_REPAIR_REQUIRED));
                int prog = this.entityData.get(DATA_REPAIR_PROGRESS) + 1;
                this.entityData.set(DATA_REPAIR_PROGRESS, prog);

                if (currentLevel instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            this.getX(), this.getY() + 0.4, this.getZ(),
                            20, 0.25, 0.25, 0.25, 0.08);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.IRON_GOLEM_REPAIR, this.getSoundSource(), 1.0f, 1.2f);
                }

                if (prog >= req) {
                    this.setDeactivated(false);
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0);
                    this.setHealth(30.0f);
                    this.tame(player);
                    this.setOrderedToSit(false);
                    QuestRewardHandler.triggerCyberHoundTamed(player);
                    if (currentLevel instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                this.getX(), this.getY() + 0.6, this.getZ(),
                                25, 0.4, 0.4, 0.4, 0.1);
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.BEACON_ACTIVATE, this.getSoundSource(), 1.0f, 1.4f);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
                QuestRewardHandler.triggerCyberHoundTamed(player);
                if (itemStack.is(SandStormItems.SCRAP_METAL)) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(6.0f);
                        if (!player.isCreative()) {
                            itemStack.shrink(1);
                        }
                        if (currentLevel instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                    this.getX(), this.getY() + 0.5, this.getZ(),
                                    15, 0.2, 0.3, 0.2, 0.05);
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
                    this.setTarget(null);
                    if (currentLevel instanceof ServerLevel serverLevel) {
                        Component status = this.isOrderedToSit()
                                ? Component.translatable("message.sandstorm.cyber_hound_standby")
                                : Component.translatable("message.sandstorm.cyber_hound_active");
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(status, true);
                        }
                        SoundEvent servoSound = this.isOrderedToSit()
                                ? SandStormSoundEvents.CYBER_COMPANION_SERVO_RETRACT
                                : SandStormSoundEvents.CYBER_COMPANION_SERVO_EXPAND;
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                servoSound, this.getSoundSource(), 0.9f, 1.0f);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (itemStack.is(SandStormItems.REPROGRAMMER_TOOL)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.sandstorm.cyber_hound_already_tamed"), true);
                }
                return InteractionResult.FAIL;
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            super.tick();
            this.setDeltaMovement(0.0, Math.min(0.0, this.getDeltaMovement().y), 0.0);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            return;
        }
        super.tick();

        if (this.alertCooldown > 0) {
            this.alertCooldown--;
        }

        Level currentLevel = this.level();
        if (!currentLevel.isClientSide() && this.isTame() && this.tickCount % 40 == 0) {
            LivingEntity owner = this.getOwner();
            if (owner instanceof ServerPlayer serverPlayer && !serverPlayer.entityTags().contains("sandstorm.tame_cyber_hound")) {
                QuestRewardHandler.triggerCyberHoundTamed(serverPlayer);
            }
        }
        if (!currentLevel.isClientSide() && this.isTame() && this.alertCooldown <= 0) {
            LivingEntity owner = this.getOwner();
            if (owner != null && this.distanceToSqr(owner) < 400.0) {
                AABB searchBox = this.getBoundingBox().inflate(16.0, 8.0, 16.0);
                boolean threatNear = !currentLevel.getEntitiesOfClass(DerelictAutomatonEntity.class, searchBox).isEmpty();
                if (threatNear && currentLevel instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.NOTE_BLOCK_BIT.value(), this.getSoundSource(), 1.0f, 1.8f);
                    this.alertCooldown = 120;
                }
            }
        }

        if (!currentLevel.isClientSide() && this.isTame() && !this.isOrderedToSit() && !this.isDeactivated() && this.onGround()) {
            checkSpikeJump(currentLevel);
        }
    }

    private void checkSpikeJump(Level level) {
        if (this.spikeJumpCooldown > 0) {
            this.spikeJumpCooldown--;
            return;
        }
        Vec3 movement = this.getDeltaMovement();
        double speedSqr = movement.x * movement.x + movement.z * movement.z;
        if (speedSqr < 0.0004 && !this.getNavigation().isInProgress()) {
            return;
        }
        double lookX = Math.abs(movement.x) > 0.02 ? movement.x : -Math.sin(Math.toRadians(this.getYRot()));
        double lookZ = Math.abs(movement.z) > 0.02 ? movement.z : Math.cos(Math.toRadians(this.getYRot()));
        Vec3 forward = new Vec3(lookX, 0, lookZ).normalize();

        BlockPos posFront = BlockPos.containing(this.getX() + forward.x * 1.2, this.getY(), this.getZ() + forward.z * 1.2);
        BlockPos posFrontBelow = posFront.below();
        BlockPos posFeet = this.blockPosition();
        BlockPos posFeetBelow = posFeet.below();

        boolean hazardAhead = CompanionTrapHelper.isSpikeTrapBlock(level.getBlockState(posFront))
                || CompanionTrapHelper.isSpikeTrapBlock(level.getBlockState(posFrontBelow))
                || CompanionTrapHelper.isSpikeTrapBlock(level.getBlockState(posFeet))
                || CompanionTrapHelper.isSpikeTrapBlock(level.getBlockState(posFeetBelow));

        if (hazardAhead) {
            double boost = 1.35;
            this.setDeltaMovement(movement.x * boost, 0.48, movement.z * boost);
            this.spikeJumpCooldown = 15;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.WOLF_STEP, this.getSoundSource(), 0.9f, 1.4f);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.getX(), this.getY() + 0.2, this.getZ(),
                        8, 0.2, 0.1, 0.2, 0.04);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (this.isTame() && (source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH) || source.is(DamageTypes.STALAGMITE))) {
            return false;
        }
        return super.hurtServer(serverLevel, source, amount);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(serverLevel, damageSource, recentlyHit);
        if (this.isDeactivated()) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, 1));
            return;
        }
        int scrapCount = 1 + this.random.nextInt(2);
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.SCRAP_METAL, scrapCount));
        this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.LOOSE_WIRES, 1));
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.isDeactivated() ? SoundEvents.NETHERITE_BLOCK_BREAK : SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isDeactivated() ? SoundEvents.ITEM_BREAK.value() : SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("AlertCooldown", this.alertCooldown);
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putInt("RepairProgress", this.entityData.get(DATA_REPAIR_PROGRESS));
        output.putInt("RepairRequired", this.entityData.get(DATA_REPAIR_REQUIRED));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.alertCooldown = input.getIntOr("AlertCooldown", 0);
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.entityData.set(DATA_REPAIR_PROGRESS, input.getIntOr("RepairProgress", 0));
        this.entityData.set(DATA_REPAIR_REQUIRED, input.getIntOr("RepairRequired", 2));
    }
}
