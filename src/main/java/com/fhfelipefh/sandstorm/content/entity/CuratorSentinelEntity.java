package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class CuratorSentinelEntity extends PathfinderMob {

    private static final EntityDataAccessor<Boolean> DATA_ALARM = SynchedEntityData.defineId(CuratorSentinelEntity.class, EntityDataSerializers.BOOLEAN);

    private int alarmTicks = 0;

    public CuratorSentinelEntity(EntityType<? extends CuratorSentinelEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 32.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ALARM, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false));
        this.goalSelector.addGoal(3, new InspectArtworkGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DerelictAutomatonEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, CrawlerDroneEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, DefectiveSentinelEntity.class, true));
    }

    public boolean isAlarmActive() {
        return this.entityData.get(DATA_ALARM);
    }

    public void setAlarmActive(boolean active) {
        this.entityData.set(DATA_ALARM, active);
    }

    public void triggerVandalismAlert(Player vandal) {
        this.setTarget(vandal);
        this.alarmTicks = 400;
        this.setAlarmActive(true);
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.IRON_GOLEM_HURT, SoundSource.HOSTILE, 1.5f, 1.5f);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getY() + 1.2, this.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
            }
        }
    }

    public static void notifyVandalism(Level level, BlockPos pos, Player player) {
        if (level.isClientSide() || player == null) {
            return;
        }
        AABB searchBox = new AABB(pos).inflate(24.0);
        List<CuratorSentinelEntity> curators = level.getEntitiesOfClass(CuratorSentinelEntity.class, searchBox);
        for (CuratorSentinelEntity curator : curators) {
            curator.triggerVandalismAlert(player);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            if (this.alarmTicks > 0) {
                this.alarmTicks--;
                if (this.alarmTicks == 0 && this.getTarget() == null) {
                    this.setAlarmActive(false);
                }
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        boolean hurt = super.hurtServer(level, damageSource, amount);
        if (hurt && damageSource.getEntity() instanceof LivingEntity attacker) {
            this.setTarget(attacker);
            this.alarmTicks = 300;
            this.setAlarmActive(true);
        }
        return hurt;
    }

    static class InspectArtworkGoal extends Goal {
        private final CuratorSentinelEntity mob;
        private BlockPos targetArtwork = null;
        private int inspectTicks = 0;

        InspectArtworkGoal(CuratorSentinelEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.mob.isAlarmActive() || this.mob.getTarget() != null) {
                return false;
            }
            if (this.mob.getRandom().nextInt(60) != 0) {
                return false;
            }
            BlockPos current = this.mob.blockPosition();
            for (BlockPos check : BlockPos.betweenClosed(current.offset(-8, -2, -8), current.offset(8, 2, 8))) {
                Block block = this.mob.level().getBlockState(check).getBlock();
                if (block == SandStormBlocks.SANDSTONE_STATUE || block == SandStormBlocks.CLAY_URN || block == SandStormBlocks.TERRACOTTA_AMPHORA) {
                    this.targetArtwork = check.immutable();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetArtwork != null && this.inspectTicks < 100 && !this.mob.isAlarmActive() && this.mob.getTarget() == null;
        }

        @Override
        public void start() {
            this.inspectTicks = 0;
            if (this.targetArtwork != null) {
                this.mob.getNavigation().moveTo(this.targetArtwork.getX() + 0.5, this.targetArtwork.getY(), this.targetArtwork.getZ() + 0.5, 0.7);
            }
        }

        @Override
        public void tick() {
            this.inspectTicks++;
            if (this.targetArtwork != null) {
                this.mob.getLookControl().setLookAt(this.targetArtwork.getX() + 0.5, this.targetArtwork.getY() + 0.5, this.targetArtwork.getZ() + 0.5);
            }
        }

        @Override
        public void stop() {
            this.targetArtwork = null;
            this.inspectTicks = 0;
        }
    }
}
