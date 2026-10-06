package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.resources.Identifier;

import java.util.EnumSet;
import java.util.List;

public class CyberneticGolemEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> DATA_OVERDRIVE = SynchedEntityData.defineId(CyberneticGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_HEAT = SynchedEntityData.defineId(CyberneticGolemEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DATA_TIER = SynchedEntityData.defineId(CyberneticGolemEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_ANIM = SynchedEntityData.defineId(CyberneticGolemEntity.class, EntityDataSerializers.INT);

    private static final Identifier OVERDRIVE_SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath("sandstorm", "cybernetic_golem_overdrive");

    public CyberneticGolemEntity(EntityType<? extends CyberneticGolemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OVERDRIVE, false);
        builder.define(DATA_HEAT, 0.0f);
        builder.define(DATA_TIER, "iron");
        builder.define(DATA_ATTACK_ANIM, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(3, new GuardPlayerGoal(this, 1.0, 10.0f, 3.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new DefendPlayerTargetGoal(this));
        this.targetSelector.addGoal(3, new HostileTargetGoal(this));
    }

    public boolean isOverdrive() {
        return this.entityData.get(DATA_OVERDRIVE);
    }

    public void setOverdrive(boolean overdrive) {
        this.entityData.set(DATA_OVERDRIVE, overdrive);
        applyOverdriveSpeedModifier(overdrive);
    }

    public float getHeat() {
        return this.entityData.get(DATA_HEAT);
    }

    public void setHeat(float heat) {
        this.entityData.set(DATA_HEAT, Math.max(0.0f, Math.min(1.0f, heat)));
    }

    public GolemMetalTier getMetalTier() {
        return GolemMetalTier.byId(this.entityData.get(DATA_TIER));
    }

    public void setMetalTier(GolemMetalTier tier) {
        if (tier == null) {
            tier = GolemMetalTier.IRON;
        }
        this.entityData.set(DATA_TIER, tier.getId());
        applyTierAttributes(tier);
    }

    public int getAttackAnimTicks() {
        return this.entityData.get(DATA_ATTACK_ANIM);
    }

    public void setAttackAnimTicks(int ticks) {
        this.entityData.set(DATA_ATTACK_ANIM, ticks);
    }

    public void applyTierAttributes(GolemMetalTier tier) {
        AttributeInstance hp = this.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(tier.getMaxHealth());
            this.setHealth((float) tier.getMaxHealth());
        }
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.setBaseValue(tier.getArmor());
        }
        AttributeInstance toughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughness != null) {
            toughness.setBaseValue(tier.getArmorToughness());
        }
        AttributeInstance dmg = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) {
            dmg.setBaseValue(tier.getAttackDamage());
        }
        AttributeInstance kb = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kb != null) {
            kb.setBaseValue(tier.getKnockbackResistance());
        }
    }

    private void applyOverdriveSpeedModifier(boolean active) {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        speed.removeModifier(OVERDRIVE_SPEED_MODIFIER_ID);
        if (active) {
            GolemMetalTier tier = getMetalTier();
            double boostAmount = tier.getBoostSpeed() - 0.25;
            speed.addTransientModifier(new AttributeModifier(OVERDRIVE_SPEED_MODIFIER_ID, boostAmount, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void tick() {
        super.tick();

        int anim = getAttackAnimTicks();
        if (anim > 0) {
            setAttackAnimTicks(anim - 1);
        }

        Level lvl = this.level();
        if (lvl.isClientSide()) {
            if (isOverdrive()) {
                lvl.addParticle(ParticleTypes.ELECTRIC_SPARK,
                        this.getRandomX(0.5), this.getY() + 0.6 + this.random.nextDouble() * 1.2, this.getRandomZ(0.5),
                        (this.random.nextDouble() - 0.5) * 0.2, 0.1, (this.random.nextDouble() - 0.5) * 0.2);
            } else if (getHeat() > 0.05f) {
                if (this.tickCount % 4 == 0) {
                    lvl.addParticle(ParticleTypes.SMOKE,
                            this.getRandomX(0.4), this.getY() + 0.8 + this.random.nextDouble() * 0.8, this.getRandomZ(0.4),
                            0.0, 0.04, 0.0);
                }
            }
            return;
        }

        ServerLevel serverLevel = (ServerLevel) lvl;
        LivingEntity target = this.getTarget();

        if (target != null && target.isAlive()) {
            if (!isOverdrive() && getHeat() <= 0.01f) {
                double distanceSq = this.distanceToSqr(target);
                if (distanceSq > 12.0) {
                    setOverdrive(true);
                    setHeat(1.0f);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.NEUTRAL, 0.8f, 1.6f);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 0.9f, 1.2f);
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            this.getX(), this.getY() + 1.2, this.getZ(), 24, 0.4, 0.6, 0.4, 0.12);
                }
            }

            if (isOverdrive()) {
                if (this.tickCount % 2 == 0) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            this.getX(), this.getY() + 1.0, this.getZ(), 4, 0.3, 0.5, 0.3, 0.08);
                }
            }
        } else {
            if (isOverdrive()) {
                setOverdrive(false);
            }

            float currentHeat = getHeat();
            if (currentHeat > 0.0f) {
                int cooldown = getMetalTier().getCooldownTicks();
                float nextHeat = currentHeat - (1.0f / (float) cooldown);
                if (nextHeat < 0.0f) {
                    nextHeat = 0.0f;
                }
                setHeat(nextHeat);

                if (this.tickCount % 4 == 0) {
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            this.getX(), this.getY() + 1.1, this.getZ(), 2, 0.25, 0.35, 0.25, 0.01);
                }

                if (this.tickCount % 25 == 0) {
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.35f, 0.65f);
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        setAttackAnimTicks(10);
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (isOverdrive()) {
            damage *= 1.4f;
        }

        DamageSource source = this.damageSources().mobAttack(this);
        boolean success = target.hurtServer(level, source, damage);

        if (success) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.42, 0.0));
            level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.IRON_GOLEM_ATTACK, SoundSource.NEUTRAL, 1.0f, 1.0f);

            if (isOverdrive()) {
                level.sendParticles(ParticleTypes.CRIT,
                        target.getX(), target.getY() + 1.0, target.getZ(), 12, 0.3, 0.3, 0.3, 0.1);
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        target.getX(), target.getY() + 0.8, target.getZ(), 10, 0.4, 0.4, 0.4, 0.08);
            }
        }

        return success;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        float currentHealth = this.getHealth();
        float maxHealth = this.getMaxHealth();

        if (currentHealth < maxHealth) {
            float healAmount = 0.0f;
            if (held.is(Items.IRON_INGOT)) {
                healAmount = 25.0f;
            } else if (held.is(Items.COPPER_INGOT)) {
                healAmount = 20.0f;
            } else if (held.is(Items.GOLD_INGOT)) {
                healAmount = 30.0f;
            } else if (held.is(Items.NETHERITE_INGOT)) {
                healAmount = 100.0f;
            }

            if (healAmount > 0.0f) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                this.heal(healAmount);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0f, 1.0f + (this.random.nextFloat() - 0.5f) * 0.2f);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player || target instanceof CyberneticGolemEntity) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_DAMAGE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0f, 1.0f);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Overdrive", this.isOverdrive());
        output.putFloat("Heat", this.getHeat());
        output.putString("MetalTier", this.entityData.get(DATA_TIER));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setOverdrive(input.getBooleanOr("Overdrive", false));
        this.setHeat(input.getFloatOr("Heat", 0.0f));
        String tierId = input.getStringOr("MetalTier", "iron");
        this.setMetalTier(GolemMetalTier.byId(tierId));
    }

    private static class HostileTargetGoal extends NearestAttackableTargetGoal<Monster> {
        public HostileTargetGoal(CyberneticGolemEntity golem) {
            super(golem, Monster.class, false);
        }
    }

    private static class GuardPlayerGoal extends Goal {
        private final CyberneticGolemEntity golem;
        private Player targetPlayer;
        private final double speedModifier;
        private final float maxDist;
        private final float minDist;

        public GuardPlayerGoal(CyberneticGolemEntity golem, double speedModifier, float maxDist, float minDist) {
            this.golem = golem;
            this.speedModifier = speedModifier;
            this.maxDist = maxDist;
            this.minDist = minDist;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.golem.getTarget() != null) {
                return false;
            }
            List<Player> players = this.golem.level().getEntitiesOfClass(Player.class, this.golem.getBoundingBox().inflate(this.maxDist));
            for (Player player : players) {
                if (!player.isSpectator() && !player.isCreative()) {
                    this.targetPlayer = player;
                    return this.golem.distanceToSqr(player) > (this.minDist * this.minDist);
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetPlayer != null && this.targetPlayer.isAlive()
                    && this.golem.getTarget() == null
                    && this.golem.distanceToSqr(this.targetPlayer) > (this.minDist * this.minDist)
                    && this.golem.distanceToSqr(this.targetPlayer) < (this.maxDist * this.maxDist * 2.0);
        }

        @Override
        public void tick() {
            if (this.targetPlayer != null) {
                this.golem.getLookControl().setLookAt(this.targetPlayer, 10.0f, (float) this.golem.getMaxHeadXRot());
                this.golem.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
            }
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            this.golem.getNavigation().stop();
        }
    }

    private static class DefendPlayerTargetGoal extends Goal {
        private final CyberneticGolemEntity golem;
        private LivingEntity threatTarget;

        public DefendPlayerTargetGoal(CyberneticGolemEntity golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            List<Player> players = this.golem.level().getEntitiesOfClass(Player.class, this.golem.getBoundingBox().inflate(24.0));
            for (Player player : players) {
                if (player.isSpectator()) {
                    continue;
                }
                LivingEntity attacker = player.getLastHurtByMob();
                if (attacker != null && attacker.isAlive() && !(attacker instanceof Player) && !(attacker instanceof CyberneticGolemEntity)) {
                    this.threatTarget = attacker;
                    return true;
                }
                LivingEntity target = player.getLastHurtMob();
                if (target != null && target.isAlive() && !(target instanceof Player) && !(target instanceof CyberneticGolemEntity)) {
                    this.threatTarget = target;
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            this.golem.setTarget(this.threatTarget);
            super.start();
        }
    }
}
