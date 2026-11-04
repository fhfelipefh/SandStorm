package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

public class ScavengerVultureEntity extends PathfinderMob implements Enemy, DeactivatableAndroid {
    public static final int STATE_CIRCLING = 0;
    public static final int STATE_DIVING = 1;
    public static final int STATE_SCRAPPING = 2;
    public static final int STATE_FLEEING = 3;
    public static final int STATE_ATTACKING = 4;

    private static final EntityDataAccessor<Integer> DATA_VULTURE_STATE = SynchedEntityData.defineId(ScavengerVultureEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DEACTIVATED = SynchedEntityData.defineId(ScavengerVultureEntity.class, EntityDataSerializers.BOOLEAN);

    private BlockPos targetCarcassPos;
    private UUID targetCarcassUuid;
    private int circlingTicks = 0;
    private int scrappingTicks = 0;
    private int fleeingTicks = 0;
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
                .add(Attributes.ATTACK_DAMAGE, 2.5)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VULTURE_STATE, STATE_CIRCLING);
        builder.define(DATA_DEACTIVATED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new VultureFleeGoal(this));
        this.goalSelector.addGoal(2, new VultureScrapGoal(this));
        this.goalSelector.addGoal(3, new VultureDiveGoal(this));
        this.goalSelector.addGoal(4, new VultureCircleGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public int getVultureState() {
        return this.entityData.get(DATA_VULTURE_STATE);
    }

    public void setVultureState(int state) {
        this.entityData.set(DATA_VULTURE_STATE, state);
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

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        boolean hurt = super.hurtServer(serverLevel, damageSource, amount);
        if (hurt && this.isAlive() && !this.isDeactivated()) {
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
            if (this.vulture.getVultureState() == STATE_SCRAPPING) {
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
            return !this.vulture.isDeactivated() && this.vulture.getVultureState() == STATE_CIRCLING;
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
            return !this.vulture.isDeactivated() && this.vulture.getVultureState() == STATE_DIVING;
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
            return !this.vulture.isDeactivated() && this.vulture.getVultureState() == STATE_SCRAPPING;
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
            return !this.vulture.isDeactivated() && this.vulture.getVultureState() == STATE_FLEEING;
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
}
