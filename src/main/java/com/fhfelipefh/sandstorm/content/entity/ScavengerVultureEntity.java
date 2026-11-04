package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class ScavengerVultureEntity extends PathfinderMob implements Enemy, DeactivatableAndroid, OverridableMachine {
    public static final int STATE_CIRCLING = 0;
    public static final int STATE_DIVING = 1;
    public static final int STATE_SCRAPPING = 2;
    public static final int STATE_FLEEING = 3;
    public static final int STATE_ATTACKING = 4;
    public static final int STATE_ESCORTING = 5;
    public static final int STATE_SCOUT_DIVE = 6;
    public static final int STATE_BEACON = 7;

    private static final EntityDataAccessor<Integer> DATA_VULTURE_STATE = SynchedEntityData.defineId(ScavengerVultureEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(ScavengerVultureEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_OVERRIDDEN = SynchedEntityData.defineId(ScavengerVultureEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(ScavengerVultureEntity.class, EntityDataSerializers.BOOLEAN);

    private static final long REPROGRAM_POWER_COST = 4000L;
    private BlockPos targetCarcassPos;
    private UUID targetCarcassUuid;
    private UUID lastResolvedCarcassUuid;
    private UUID overrideOwner;
    private int circlingTicks = 0;
    private int scrappingTicks = 0;
    private int fleeingTicks = 0;
    private int beaconTicks = 0;
    private int attackCooldown = 0;
    private double orbitAngle = 0.0;

    public ScavengerVultureEntity(EntityType<? extends ScavengerVultureEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new VultureMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FLYING_SPEED, 0.50)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.ATTACK_DAMAGE, 3.5)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VULTURE_STATE, STATE_CIRCLING);
        builder.define(DATA_DEACTIVATED, false);
        builder.define(DATA_OVERRIDDEN, false);
        builder.define(DATA_SITTING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new HackedVultureCombatGoal(this));
        this.goalSelector.addGoal(2, new VultureFleeGoal(this));
        this.goalSelector.addGoal(3, new HackedVultureBeaconGoal(this));
        this.goalSelector.addGoal(4, new HackedVultureScoutGoal(this));
        this.goalSelector.addGoal(5, new HackedVultureEscortGoal(this));
        this.goalSelector.addGoal(6, new VultureScrapGoal(this));
        this.goalSelector.addGoal(7, new VultureDiveGoal(this));
        this.goalSelector.addGoal(8, new VultureCircleGoal(this));

        this.targetSelector.addGoal(1, new HackedVultureProtectOwnerGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    public int getVultureState() {
        return this.entityData.get(DATA_VULTURE_STATE);
    }

    public void setVultureState(int state) {
        this.entityData.set(DATA_VULTURE_STATE, state);
    }

    @Override
    public boolean isOverridden() {
        return this.entityData.get(DATA_OVERRIDDEN);
    }

    @Override
    public void setOverridden(boolean overridden) {
        this.entityData.set(DATA_OVERRIDDEN, overridden);
        if (overridden) {
            this.setVultureState(STATE_ESCORTING);
        }
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

    public boolean isOrderedToSit() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setOrderedToSit(boolean sitting) {
        this.entityData.set(DATA_SITTING, sitting);
        if (sitting) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setVultureState(STATE_BEACON);
        } else {
            this.setVultureState(STATE_ESCORTING);
        }
    }

    public LivingEntity getOwner() {
        UUID uuid = this.getOverrideOwner();
        if (uuid != null && this.level() instanceof ServerLevel serverLevel) {
            return serverLevel.getPlayerByUUID(uuid);
        }
        return null;
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return entity != null && entity.getUUID().equals(this.getOverrideOwner());
    }

    public UUID getLastResolvedCarcassUuid() {
        return this.lastResolvedCarcassUuid;
    }

    public void setLastResolvedCarcassUuid(UUID uuid) {
        this.lastResolvedCarcassUuid = uuid;
    }

    @Override
    public Component getName() {
        if (this.isDeactivated()) {
            return Component.translatable(this.getType().getDescriptionId() + ".deactivated");
        }
        if (this.isOverridden()) {
            LivingEntity owner = this.getOwner();
            if (owner != null) {
                return Component.literal("§b" + owner.getName().getString() + " §7- Abutre");
            }
            return Component.literal("§bAbutre Rastreador");
        }
        return super.getName();
    }

    public boolean isCompanionAlly(Entity other) {
        if (!this.isOverridden() || other == null) {
            return false;
        }
        if (other == this.getOwner()) {
            return true;
        }
        if (other instanceof TamableAnimal tamable && tamable.getOwner() == this.getOwner()) {
            return true;
        }
        if (other instanceof OverridableMachine om && om.getOverrideOwner() != null && om.getOverrideOwner().equals(this.getOverrideOwner())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.isOverridden()) {
            if (this.isCompanionAlly(target)) {
                return false;
            }
        }
        return super.canAttack(target);
    }

    public void setTargetCarcass(LivingEntity carcass) {
        if (carcass != null) {
            this.targetCarcassUuid = carcass.getUUID();
            this.targetCarcassPos = carcass.blockPosition();
        }
    }

    public BlockPos getTargetCarcassPos() {
        return targetCarcassPos;
    }

    public void setTargetCarcassPos(BlockPos pos) {
        this.targetCarcassPos = pos;
    }

    public UUID getTargetCarcassUuid() {
        return targetCarcassUuid;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Level currentLevel = this.level();

        if (this.isDeactivated()) {
            return super.mobInteract(player, hand);
        }

        if (this.isOverridden()) {
            if (this.isOwnedBy(player)) {
                if (itemStack.is(SandStormItems.REPROGRAMMER_TOOL)) {
                    return InteractionResult.SUCCESS;
                }

                if (itemStack.is(SandStormItems.SCRAP_METAL) || itemStack.is(SandStormItems.TITANIUM_CHITIN_COMPOSITE)) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        float healAmount = itemStack.is(SandStormItems.TITANIUM_CHITIN_COMPOSITE) ? 12.0f : 6.0f;
                        this.heal(healAmount);
                        if (!player.isCreative()) {
                            itemStack.shrink(1);
                        }
                        if (currentLevel instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                    this.getX(), this.getY() + 0.5, this.getZ(),
                                    15, 0.25, 0.25, 0.25, 0.05);
                            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                    SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.9f, 1.3f);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.CONSUME;
                }

                if (!player.isSecondaryUseActive()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    if (currentLevel instanceof ServerLevel serverLevel) {
                        SoundEvent servoSound = this.isOrderedToSit()
                                ? SandStormSoundEvents.CYBER_COMPANION_SERVO_RETRACT
                                : SandStormSoundEvents.CYBER_COMPANION_SERVO_EXPAND;
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                servoSound, SoundSource.PLAYERS, 0.9f, 1.0f);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (itemStack.is(SandStormItems.REPROGRAMMER_TOOL)) {
                return InteractionResult.FAIL;
            }
        } else if (itemStack.is(SandStormItems.REPROGRAMMER_TOOL)) {
            if (!currentLevel.isClientSide() && currentLevel instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                if (!serverPlayer.isCreative()) {
                    SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(serverPlayer);
                    if (suit.getStoredEnergy() < REPROGRAM_POWER_COST) {
                        return InteractionResult.FAIL;
                    }
                    suit.consumeEnergy(REPROGRAM_POWER_COST);
                }

                this.setOverridden(true);
                this.setOverrideOwner(player.getUUID());
                this.setOrderedToSit(false);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.getX(), this.getY() + 0.6, this.getZ(),
                        35, 0.35, 0.4, 0.35, 0.1);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        this.getX(), this.getY() + 0.6, this.getZ(),
                        20, 0.3, 0.3, 0.3, 0.05);
                serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        boolean hurt = super.hurtServer(serverLevel, damageSource, amount);
        if (hurt && this.isAlive() && !this.isDeactivated()) {
            if (this.isOverridden()) {
                Entity attacker = damageSource.getEntity();
                if (attacker instanceof LivingEntity livingAttacker && this.canAttack(livingAttacker)) {
                    this.setTarget(livingAttacker);
                    this.setVultureState(STATE_ATTACKING);
                }
                return hurt;
            }
            this.setVultureState(STATE_FLEEING);
            this.fleeingTicks = 0;
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SandStormSoundEvents.SCAVENGER_VULTURE_FLEE, SoundSource.HOSTILE, 1.0f, 1.1f);
            Entity attacker = damageSource.getEntity();
            if (attacker != null) {
                Vec3 fleeDir = this.position().subtract(attacker.position()).normalize().add(0, 0.6, 0).normalize().scale(0.65);
                this.setDeltaMovement(fleeDir);
            } else {
                this.setDeltaMovement(new Vec3(0, 0.55, 0));
            }
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        boolean hit = super.doHurtTarget(serverLevel, target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
        }
        return hit;
    }

    @Override
    public void tick() {
        if (this.isDeactivated()) {
            this.setDeltaMovement(0.0, Math.min(0.0, this.getDeltaMovement().y), 0.0);
            super.tick();
            return;
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.isOverridden() && !this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            if (this.isOrderedToSit()) {
                this.setDeltaMovement(0.0, Math.min(0.0, this.getDeltaMovement().y), 0.0);
                super.tick();
                return;
            }

            if (this.targetCarcassUuid != null) {
                Entity entity = serverLevel.getEntity(this.targetCarcassUuid);
                boolean resolved = false;
                if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                    resolved = true;
                } else if (entity instanceof DeactivatableAndroid da && !da.isDeactivated()) {
                    resolved = true;
                }

                if (resolved) {
                    this.lastResolvedCarcassUuid = this.targetCarcassUuid;
                    this.targetCarcassUuid = null;
                    this.targetCarcassPos = null;
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + 0.8, this.getZ(),
                            18, 0.35, 0.35, 0.35, 0.08);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 1.4f);
                    this.setDeltaMovement(0.0, 0.50, 0.0);
                    this.setVultureState(STATE_ESCORTING);
                }
            }

            if (this.getVultureState() == STATE_BEACON) {
                this.beaconTicks++;
                if (this.beaconTicks % 15 == 0) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            this.getX(), this.getY() + 0.9, this.getZ(),
                            6, 0.2, 0.6, 0.2, 0.08);
                    serverLevel.sendParticles(ParticleTypes.GLOW,
                            this.getX(), this.getY() + 0.9, this.getZ(),
                            3, 0.1, 0.4, 0.1, 0.02);
                }
                if (this.beaconTicks % 60 == 0) {
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 1.2f, 1.8f);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.BEACON_AMBIENT, SoundSource.NEUTRAL, 0.6f, 1.9f);
                }
            }
        }

        super.tick();
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(serverLevel, damageSource, recentlyHit);
        int feathers = 1 + this.random.nextInt(3);
        for (int i = 0; i < feathers; i++) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.CARBON_FIBER_SERVO_FEATHER));
        }
        if (this.random.nextFloat() < 0.60f) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.AVIONICS_OPTICAL_SENSOR));
        }
        if (this.random.nextFloat() < 0.40f) {
            this.spawnAtLocation(serverLevel, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE));
        }
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
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SandStormSoundEvents.SCAVENGER_VULTURE_FLEE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SandStormSoundEvents.SCAVENGER_VULTURE_FLEE;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("VultureState", this.getVultureState());
        output.putBoolean("Deactivated", this.isDeactivated());
        output.putBoolean("Overridden", this.isOverridden());
        output.putBoolean("Sitting", this.isOrderedToSit());
        if (this.overrideOwner != null) {
            output.putString("OverrideOwner", this.overrideOwner.toString());
        }
        if (this.lastResolvedCarcassUuid != null) {
            output.putString("LastResolvedCarcass", this.lastResolvedCarcassUuid.toString());
        }
        output.putInt("CirclingTicks", this.circlingTicks);
        output.putInt("ScrappingTicks", this.scrappingTicks);
        output.putInt("FleeingTicks", this.fleeingTicks);
        if (this.targetCarcassPos != null) {
            output.putInt("TargetX", this.targetCarcassPos.getX());
            output.putInt("TargetY", this.targetCarcassPos.getY());
            output.putInt("TargetZ", this.targetCarcassPos.getZ());
        }
        if (this.targetCarcassUuid != null) {
            output.putString("TargetCarcassUuid", this.targetCarcassUuid.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setVultureState(input.getIntOr("VultureState", STATE_CIRCLING));
        this.setDeactivated(input.getBooleanOr("Deactivated", false));
        this.setOverridden(input.getBooleanOr("Overridden", false));
        this.setOrderedToSit(input.getBooleanOr("Sitting", false));
        String ownerStr = input.getStringOr("OverrideOwner", "");
        if (!ownerStr.isEmpty()) {
            this.overrideOwner = UUID.fromString(ownerStr);
        }
        String lastResolvedStr = input.getStringOr("LastResolvedCarcass", "");
        if (!lastResolvedStr.isEmpty()) {
            this.lastResolvedCarcassUuid = UUID.fromString(lastResolvedStr);
        }
        this.circlingTicks = input.getIntOr("CirclingTicks", 0);
        this.scrappingTicks = input.getIntOr("ScrappingTicks", 0);
        this.fleeingTicks = input.getIntOr("FleeingTicks", 0);
        int targetX = input.getIntOr("TargetX", Integer.MIN_VALUE);
        int targetY = input.getIntOr("TargetY", Integer.MIN_VALUE);
        int targetZ = input.getIntOr("TargetZ", Integer.MIN_VALUE);
        if (targetX != Integer.MIN_VALUE && targetY != Integer.MIN_VALUE && targetZ != Integer.MIN_VALUE) {
            this.targetCarcassPos = new BlockPos(targetX, targetY, targetZ);
        }
        String uuidStr = input.getStringOr("TargetCarcassUuid", "");
        if (!uuidStr.isEmpty()) {
            this.targetCarcassUuid = UUID.fromString(uuidStr);
        }
    }

    public static void spawnForCarcass(ServerLevel level, LivingEntity carcass) {
        if (carcass == null || !carcass.isAlive()) {
            return;
        }
        BlockPos carcassPos = carcass.blockPosition();
        BlockPos spawnPos = carcassPos.offset(level.getRandom().nextInt(16) - 8, 38, level.getRandom().nextInt(16) - 8);
        ScavengerVultureEntity vulture = SandStormEntities.SCAVENGER_VULTURE.create(level, EntitySpawnReason.EVENT);
        if (vulture != null) {
            vulture.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            vulture.setYRot(level.getRandom().nextFloat() * 360.0f);
            vulture.setTargetCarcass(carcass);
            vulture.setVultureState(STATE_CIRCLING);
            level.addFreshEntity(vulture);
            level.playSound(null, spawnPos, SandStormSoundEvents.SCAVENGER_VULTURE_SCREECH, SoundSource.HOSTILE, 1.2f, 1.0f);
        }
    }

    static class VultureMoveControl extends MoveControl {
        private final ScavengerVultureEntity vulture;

        public VultureMoveControl(ScavengerVultureEntity vulture) {
            super(vulture);
            this.vulture = vulture;
        }

        @Override
        public void tick() {
            if (this.vulture.isDeactivated()) {
                return;
            }
            if (this.vulture.getVultureState() == STATE_SCRAPPING || this.vulture.getVultureState() == STATE_BEACON) {
                super.tick();
                return;
            }
            if (this.operation == Operation.MOVE_TO) {
                Vec3 dir = new Vec3(this.wantedX - this.vulture.getX(), this.wantedY - this.vulture.getY(), this.wantedZ - this.vulture.getZ());
                double dist = dir.length();
                if (dist < 0.6) {
                    this.operation = Operation.WAIT;
                    this.vulture.setDeltaMovement(this.vulture.getDeltaMovement().scale(0.6));
                } else {
                    double speed = this.speedModifier * 0.045;
                    this.vulture.setDeltaMovement(this.vulture.getDeltaMovement().add(dir.scale(speed / dist)));
                    Vec3 motion = this.vulture.getDeltaMovement();
                    this.vulture.setYRot(-((float) Mth.atan2(motion.x, motion.z)) * (180.0f / (float) Math.PI));
                    this.vulture.yBodyRot = this.vulture.getYRot();
                }
            }
        }
    }

    static class VultureCircleGoal extends Goal {
        private final ScavengerVultureEntity vulture;

        public VultureCircleGoal(ScavengerVultureEntity vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.vulture.isOverridden() && !this.vulture.isDeactivated() && this.vulture.getVultureState() == STATE_CIRCLING;
        }

        @Override
        public void tick() {
            this.vulture.circlingTicks++;
            BlockPos center = this.vulture.targetCarcassPos != null ? this.vulture.targetCarcassPos : this.vulture.blockPosition();
            this.vulture.orbitAngle += 0.035;
            double radius = 28.0;
            double targetX = center.getX() + Math.cos(this.vulture.orbitAngle) * radius;
            double targetZ = center.getZ() + Math.sin(this.vulture.orbitAngle) * radius;
            double targetY = center.getY() + 36.0;

            this.vulture.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 1.0);

            if (this.vulture.circlingTicks % 160 == 0 && this.vulture.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, this.vulture.getX(), this.vulture.getY(), this.vulture.getZ(),
                        SandStormSoundEvents.SCAVENGER_VULTURE_SCREECH, SoundSource.HOSTILE, 1.2f, 1.0f);
            }

            if (this.vulture.circlingTicks >= 1800) {
                this.vulture.setVultureState(STATE_DIVING);
            }
        }
    }

    static class VultureDiveGoal extends Goal {
        private final ScavengerVultureEntity vulture;

        public VultureDiveGoal(ScavengerVultureEntity vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.vulture.isOverridden() && !this.vulture.isDeactivated() && this.vulture.getVultureState() == STATE_DIVING;
        }

        @Override
        public void tick() {
            BlockPos center = this.vulture.targetCarcassPos != null ? this.vulture.targetCarcassPos : this.vulture.blockPosition();
            double targetX = center.getX() + 0.5;
            double targetY = center.getY() + 0.2;
            double targetZ = center.getZ() + 0.5;

            this.vulture.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 1.6);

            Vec3 diff = new Vec3(targetX - this.vulture.getX(), targetY - this.vulture.getY(), targetZ - this.vulture.getZ());
            if (diff.length() < 2.5) {
                this.vulture.setVultureState(STATE_SCRAPPING);
                this.vulture.scrappingTicks = 0;
            }
        }
    }

    static class VultureScrapGoal extends Goal {
        private final ScavengerVultureEntity vulture;

        public VultureScrapGoal(ScavengerVultureEntity vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !this.vulture.isOverridden() && !this.vulture.isDeactivated() && this.vulture.getVultureState() == STATE_SCRAPPING;
        }

        @Override
        public void tick() {
            this.vulture.scrappingTicks++;
            BlockPos center = this.vulture.targetCarcassPos != null ? this.vulture.targetCarcassPos : this.vulture.blockPosition();
            this.vulture.getLookControl().setLookAt(center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5);

            if (this.vulture.level() instanceof ServerLevel serverLevel) {
                if (this.vulture.scrappingTicks % 5 == 0) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            center.getX() + 0.5, center.getY() + 0.4, center.getZ() + 0.5,
                            6, 0.3, 0.2, 0.3, 0.05);
                }
                if (this.vulture.scrappingTicks % 30 == 0) {
                    serverLevel.playSound(null, this.vulture.getX(), this.vulture.getY(), this.vulture.getZ(),
                            SandStormSoundEvents.SCAVENGER_VULTURE_TEAR, SoundSource.HOSTILE, 0.9f, 1.1f);
                }

                if (this.vulture.scrappingTicks >= 900) {
                    if (this.vulture.targetCarcassUuid != null) {
                        Entity carcass = serverLevel.getEntity(this.vulture.targetCarcassUuid);
                        if (carcass != null) {
                            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                                    carcass.getX(), carcass.getY() + 0.5, carcass.getZ(),
                                    15, 0.4, 0.4, 0.4, 0.05);
                            carcass.discard();
                        }
                    }
                    this.vulture.setVultureState(STATE_FLEEING);
                    this.vulture.fleeingTicks = 0;
                }
            }
        }
    }

    static class VultureFleeGoal extends Goal {
        private final ScavengerVultureEntity vulture;

        public VultureFleeGoal(ScavengerVultureEntity vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.vulture.isOverridden() && !this.vulture.isDeactivated() && this.vulture.getVultureState() == STATE_FLEEING;
        }

        @Override
        public void tick() {
            this.vulture.fleeingTicks++;
            Vec3 current = this.vulture.getDeltaMovement();
            this.vulture.setDeltaMovement(current.x * 1.02, Math.min(current.y + 0.035, 0.65), current.z * 1.02);

            if (this.vulture.fleeingTicks > 160 || this.vulture.getY() > 300.0) {
                this.vulture.discard();
            }
        }
    }

    static class HackedVultureProtectOwnerGoal extends TargetGoal {
        private final ScavengerVultureEntity vulture;
        private LivingEntity attacker;
        private int timestamp;

        public HackedVultureProtectOwnerGoal(ScavengerVultureEntity vulture) {
            super(vulture, false);
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!this.vulture.isOverridden() || this.vulture.isDeactivated() || this.vulture.isOrderedToSit()) {
                return false;
            }
            LivingEntity owner = this.vulture.getOwner();
            if (owner == null) {
                return false;
            }
            this.attacker = owner.getLastHurtByMob();
            int lastHurtTimestamp = owner.getLastHurtByMobTimestamp();
            if (lastHurtTimestamp != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT)) {
                return true;
            }
            this.attacker = owner.getLastHurtMob();
            int lastAttackedTimestamp = owner.getLastHurtMobTimestamp();
            return lastAttackedTimestamp != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            this.mob.setTarget(this.attacker);
            LivingEntity owner = this.vulture.getOwner();
            if (owner != null) {
                this.timestamp = (this.attacker == owner.getLastHurtByMob())
                        ? owner.getLastHurtByMobTimestamp()
                        : owner.getLastHurtMobTimestamp();
            }
            this.vulture.setVultureState(STATE_ATTACKING);
            super.start();
        }
    }

    static class HackedVultureCombatGoal extends Goal {
        private final ScavengerVultureEntity vulture;

        public HackedVultureCombatGoal(ScavengerVultureEntity vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.vulture.getTarget();
            return this.vulture.isOverridden() && !this.vulture.isDeactivated() && !this.vulture.isOrderedToSit()
                    && target != null && target.isAlive() && !target.isRemoved() && this.vulture.canAttack(target);
        }

        @Override
        public void tick() {
            LivingEntity target = this.vulture.getTarget();
            if (target == null || !target.isAlive() || target.isRemoved()) {
                this.vulture.setTarget(null);
                this.vulture.setVultureState(STATE_ESCORTING);
                return;
            }

            this.vulture.setVultureState(STATE_ATTACKING);
            this.vulture.getLookControl().setLookAt(target, 30.0f, 30.0f);
            this.vulture.getMoveControl().setWantedPosition(target.getX(), target.getEyeY(), target.getZ(), 1.8);

            double distSq = this.vulture.distanceToSqr(target);
            if (distSq <= 6.0 && this.vulture.attackCooldown <= 0 && this.vulture.level() instanceof ServerLevel serverLevel) {
                this.vulture.doHurtTarget(serverLevel, target);
                this.vulture.attackCooldown = 18;
                this.vulture.setDeltaMovement(0.0, 0.45, 0.0);
                serverLevel.playSound(null, this.vulture.getX(), this.vulture.getY(), this.vulture.getZ(),
                        SandStormSoundEvents.SCAVENGER_VULTURE_SCREECH, SoundSource.HOSTILE, 1.2f, 1.2f);
            }
        }
    }

    static class HackedVultureBeaconGoal extends Goal {
        private final ScavengerVultureEntity vulture;

        public HackedVultureBeaconGoal(ScavengerVultureEntity vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.vulture.isOverridden() && !this.vulture.isDeactivated()
                    && (this.vulture.isOrderedToSit() || this.vulture.getVultureState() == STATE_BEACON);
        }

        @Override
        public void tick() {
            this.vulture.setDeltaMovement(0.0, Math.min(0.0, this.vulture.getDeltaMovement().y), 0.0);
            if (this.vulture.targetCarcassPos != null) {
                BlockPos p = this.vulture.targetCarcassPos;
                this.vulture.getLookControl().setLookAt(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
            }
        }
    }

    static class HackedVultureScoutGoal extends Goal {
        private final ScavengerVultureEntity vulture;

        public HackedVultureScoutGoal(ScavengerVultureEntity vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.vulture.isOverridden() && !this.vulture.isDeactivated() && !this.vulture.isOrderedToSit()
                    && this.vulture.getTarget() == null
                    && (this.vulture.getVultureState() == STATE_SCOUT_DIVE || this.vulture.targetCarcassPos != null);
        }

        @Override
        public void tick() {
            BlockPos target = this.vulture.targetCarcassPos;
            if (target == null) {
                this.vulture.setVultureState(STATE_ESCORTING);
                return;
            }

            double tx = target.getX() + 0.5;
            double ty = target.getY() + 0.2;
            double tz = target.getZ() + 0.5;

            this.vulture.getMoveControl().setWantedPosition(tx, ty, tz, 1.6);
            this.vulture.getLookControl().setLookAt(tx, ty + 0.5, tz);

            if (this.vulture.level() instanceof ServerLevel serverLevel && this.vulture.tickCount % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.vulture.getX(), this.vulture.getY() + 0.1, this.vulture.getZ(),
                        2, 0.04, 0.04, 0.04, 0.02);
            }

            Vec3 diff = new Vec3(tx - this.vulture.getX(), ty - this.vulture.getY(), tz - this.vulture.getZ());
            if (diff.length() < 2.5) {
                this.vulture.setVultureState(STATE_BEACON);
                this.vulture.beaconTicks = 0;
                this.vulture.setDeltaMovement(Vec3.ZERO);
            }
        }
    }

    static class HackedVultureEscortGoal extends Goal {
        private final ScavengerVultureEntity vulture;

        public HackedVultureEscortGoal(ScavengerVultureEntity vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.vulture.isOverridden() && !this.vulture.isDeactivated() && !this.vulture.isOrderedToSit()
                    && this.vulture.getTarget() == null && this.vulture.targetCarcassPos == null;
        }

        @Override
        public void tick() {
            LivingEntity owner = this.vulture.getOwner();
            if (owner == null) {
                return;
            }

            this.vulture.setVultureState(STATE_ESCORTING);

            if (this.vulture.tickCount % 20 == 0 && this.vulture.level() instanceof ServerLevel serverLevel) {
                AABB searchBox = this.vulture.getBoundingBox().inflate(64.0, 36.0, 64.0);
                List<LivingEntity> carcasses = serverLevel.getEntitiesOfClass(LivingEntity.class, searchBox, e ->
                        e != this.vulture && e.isAlive() && !e.isRemoved()
                                && e instanceof DeactivatableAndroid da && da.isDeactivated()
                                && !e.getUUID().equals(this.vulture.getLastResolvedCarcassUuid())
                );
                if (!carcasses.isEmpty()) {
                    carcasses.sort(Comparator.comparingDouble(e -> e.distanceToSqr(this.vulture)));
                    LivingEntity nearest = carcasses.get(0);
                    this.vulture.setTargetCarcass(nearest);
                    this.vulture.setVultureState(STATE_SCOUT_DIVE);
                    serverLevel.playSound(null, this.vulture.getX(), this.vulture.getY(), this.vulture.getZ(),
                            SandStormSoundEvents.SCAVENGER_VULTURE_SCREECH, SoundSource.NEUTRAL, 1.1f, 1.2f);
                    return;
                }
            }

            double distSq = this.vulture.distanceToSqr(owner);
            if (distSq > 2304.0) {
                this.vulture.setPos(owner.getX(), owner.getY() + 10.0, owner.getZ());
                this.vulture.setDeltaMovement(Vec3.ZERO);
                return;
            }

            if (distSq > 144.0) {
                this.vulture.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + 10.0, owner.getZ(), 1.4);
            } else {
                this.vulture.orbitAngle += 0.04;
                double ox = owner.getX() + Math.cos(this.vulture.orbitAngle) * 9.0;
                double oz = owner.getZ() + Math.sin(this.vulture.orbitAngle) * 9.0;
                double oy = owner.getY() + 10.0;
                this.vulture.getMoveControl().setWantedPosition(ox, oy, oz, 1.0);
            }
        }
    }
}
