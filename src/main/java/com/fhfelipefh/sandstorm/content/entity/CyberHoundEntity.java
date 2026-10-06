package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CyberHoundEntity extends TamableAnimal {
    private int alertCooldown = 0;

    public CyberHoundEntity(EntityType<? extends CyberHoundEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.15, 10.0f, 2.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, DerelictAutomatonEntity.class, false));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Level currentLevel = this.level();

        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
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
                        SoundEvent clickSound = this.isOrderedToSit() ? SoundEvents.PISTON_CONTRACT : SoundEvents.PISTON_EXTEND;
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                clickSound, this.getSoundSource(), 0.7f, 1.6f);
                        Component status = this.isOrderedToSit()
                                ? Component.translatable("message.sandstorm.cyber_hound_standby")
                                : Component.translatable("message.sandstorm.cyber_hound_active");
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(status, true);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.alertCooldown > 0) {
            this.alertCooldown--;
        }

        Level currentLevel = this.level();
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
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("AlertCooldown", this.alertCooldown);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.alertCooldown = input.getIntOr("AlertCooldown", 0);
    }
}
