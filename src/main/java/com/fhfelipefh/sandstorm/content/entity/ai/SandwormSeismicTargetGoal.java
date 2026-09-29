package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.component.SeismicTrackerComponent;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.entity.ScrapSentinelEntity;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SandwormSeismicTargetGoal extends TargetGoal {
    private static final double MAX_SEISMIC_RANGE = 64.0;
    private static final double WALKING_RANGE = 36.0;
    private static final double SNEAK_RANGE = 8.0;
    private static final double MAX_CONTINUE_RANGE_SQR = (MAX_SEISMIC_RANGE + 16.0) * (MAX_SEISMIC_RANGE + 16.0);
    private static final double CLUSTER_RADIUS = 16.0;
    private static final double CLUSTER_RADIUS_SQR = CLUSTER_RADIUS * CLUSTER_RADIUS;

    private final TargetingConditions targetingConditions;
    private LivingEntity potentialTarget;
    private int reevaluateTicks = 0;

    public SandwormSeismicTargetGoal(Mob mob) {
        super(mob, false);
        this.targetingConditions = TargetingConditions.forCombat().range(MAX_SEISMIC_RANGE).ignoreLineOfSight();
    }

    @Override
    public boolean canUse() {
        if (this.mob.level().isClientSide()) {
            return false;
        }

        AABB searchBox = this.mob.getBoundingBox().inflate(MAX_SEISMIC_RANGE, 24.0, MAX_SEISMIC_RANGE);
        List<LivingEntity> nearbyPrey = this.mob.level().getEntitiesOfClass(LivingEntity.class, searchBox, this::isValidPrey);

        LivingEntity bestTarget = null;
        double bestScore = -Double.MAX_VALUE;

        for (LivingEntity prey : nearbyPrey) {
            double score = this.calculatePreyScore(prey, nearbyPrey);
            if (score > bestScore) {
                bestScore = score;
                bestTarget = prey;
            }
        }

        if (bestTarget != null) {
            this.potentialTarget = bestTarget;
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.potentialTarget);
        this.targetMob = this.potentialTarget;
        this.reevaluateTicks = 0;
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        this.reevaluateTicks++;
        if (this.reevaluateTicks >= 20) {
            this.reevaluateTicks = 0;
            this.reevaluateCrowdTarget();
        }
    }

    private void reevaluateCrowdTarget() {
        if (this.mob.level().isClientSide()) {
            return;
        }

        AABB searchBox = this.mob.getBoundingBox().inflate(MAX_SEISMIC_RANGE, 24.0, MAX_SEISMIC_RANGE);
        List<LivingEntity> nearbyPrey = this.mob.level().getEntitiesOfClass(LivingEntity.class, searchBox, this::isValidPrey);
        if (nearbyPrey.isEmpty()) {
            return;
        }

        LivingEntity currentTarget = this.mob.getTarget();
        double currentScore = currentTarget != null ? this.calculatePreyScore(currentTarget, nearbyPrey) : -Double.MAX_VALUE;

        LivingEntity bestCandidate = null;
        double bestScore = -Double.MAX_VALUE;

        for (LivingEntity prey : nearbyPrey) {
            double score = this.calculatePreyScore(prey, nearbyPrey);
            if (score > bestScore) {
                bestScore = score;
                bestCandidate = prey;
            }
        }

        if (bestCandidate != null && bestCandidate != currentTarget && bestScore > currentScore + 800.0) {
            this.mob.setTarget(bestCandidate);
            this.targetMob = bestCandidate;
        }
    }

    public double calculatePreyScore(LivingEntity candidate, List<LivingEntity> allPrey) {
        double crowdNoise = 0.0;
        int crowdCount = 0;

        for (LivingEntity other : allPrey) {
            if (candidate.distanceToSqr(other) <= CLUSTER_RADIUS_SQR) {
                crowdNoise += calculateEntityNoise(other);
                crowdCount++;
            }
        }

        double distSqr = this.mob.distanceToSqr(candidate);
        return (crowdNoise * 1500.0) + (crowdCount * 500.0) - distSqr;
    }

    public static double calculateEntityNoise(LivingEntity entity) {
        double noise = 1.0;
        if (entity.isSprinting()) {
            noise += 2.0;
        }
        if (entity.isPassenger()) {
            noise += 2.0;
        }
        if (entity instanceof Player player) {
            int chunkX = player.getBlockX() >> 4;
            int chunkZ = player.getBlockZ() >> 4;
            SeismicTrackerComponent tracker = SeismicSurvivalHandler.getTracker();
            double chunkVibration = tracker.getVibration(chunkX, chunkZ);
            if (chunkVibration >= 20.0) {
                noise += 2.5;
            }
            if (player.isShiftKeyDown()) {
                noise *= 0.2;
            } else if (player.getDeltaMovement().horizontalDistanceSqr() > 0.001) {
                noise += 1.0;
            }
        } else {
            if (entity.getDeltaMovement().horizontalDistanceSqr() > 0.002) {
                noise += 1.0;
            }
        }
        if (entity.hurtTime > 0) {
            noise += 1.5;
        }
        return Math.max(0.1, noise);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity currentTarget = this.mob.getTarget();
        if (currentTarget == null || !currentTarget.isAlive()) {
            return false;
        }
        if (currentTarget instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(currentTarget.getBlockX(), currentTarget.getBlockZ())) {
            return false;
        }
        if (KineticShieldTracker.isInsideShield(currentTarget.level().dimension(), currentTarget.blockPosition())) {
            return false;
        }
        return this.mob.distanceToSqr(currentTarget) <= MAX_CONTINUE_RANGE_SQR;
    }

    public boolean isValidPrey(LivingEntity entity) {
        if (entity == null || !entity.isAlive() || entity.isSpectator() || entity == this.mob) {
            return false;
        }
        if (entity.getType() == this.mob.getType()) {
            return false;
        }
        if (entity instanceof ScrapSentinelEntity) {
            return false;
        }
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(entity.getBlockX(), entity.getBlockZ())) {
            return false;
        }
        if (KineticShieldTracker.isInsideShield(entity.level().dimension(), entity.blockPosition())) {
            return false;
        }
        if (SeismicSurvivalHandler.isSeismicSafeBlock(entity.getBlockStateOn()) || SeismicSurvivalHandler.isSeismicSafeBlock(entity.level().getBlockState(entity.blockPosition()))) {
            return false;
        }

        boolean onGround = entity.onGround();
        boolean isPassenger = entity.isPassenger();
        if (!onGround && !isPassenger) {
            return false;
        }

        double distSqr = this.mob.distanceToSqr(entity);
        double maxAllowedRange = calculateMaxDetectionRange(entity);

        return distSqr <= maxAllowedRange * maxAllowedRange;
    }

    public static double calculateMaxDetectionRange(LivingEntity entity) {
        if (entity.isPassenger() || entity.isSprinting()) {
            return MAX_SEISMIC_RANGE;
        }
        if (entity instanceof Player player) {
            int chunkX = player.getBlockX() >> 4;
            int chunkZ = player.getBlockZ() >> 4;
            double chunkVibration = SeismicSurvivalHandler.getTracker().getVibration(chunkX, chunkZ);
            if (chunkVibration >= 20.0) {
                return MAX_SEISMIC_RANGE;
            }
            if (player.isShiftKeyDown()) {
                return SNEAK_RANGE;
            }
            double deltaMovementSqr = player.getDeltaMovement().horizontalDistanceSqr();
            if (deltaMovementSqr > 0.001) {
                return WALKING_RANGE;
            }
            return SNEAK_RANGE;
        }

        double movementSqr = entity.getDeltaMovement().horizontalDistanceSqr();
        if (movementSqr > 0.002) {
            return WALKING_RANGE;
        }
        return SNEAK_RANGE;
    }
}
