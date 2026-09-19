package com.fhfelipefh.sandstorm.content.entity.ai;

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

    private final TargetingConditions targetingConditions;
    private LivingEntity potentialTarget;

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
        List<Player> nearbyPlayers = this.mob.level().getEntitiesOfClass(Player.class, searchBox, this::isValidPrey);

        LivingEntity closestTarget = null;
        double closestDistSqr = Double.MAX_VALUE;

        for (Player player : nearbyPlayers) {
            double distSqr = this.mob.distanceToSqr(player);
            if (distSqr < closestDistSqr) {
                closestDistSqr = distSqr;
                closestTarget = player;
            }
        }

        if (closestTarget != null) {
            this.potentialTarget = closestTarget;
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.potentialTarget);
        this.targetMob = this.potentialTarget;
        super.start();
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
        if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(currentTarget.blockPosition().getX(), currentTarget.blockPosition().getZ())) {
            return false;
        }
        return this.mob.distanceToSqr(currentTarget) <= (MAX_SEISMIC_RANGE + 16.0) * (MAX_SEISMIC_RANGE + 16.0);
    }

    public boolean isValidPrey(LivingEntity entity) {
        if (entity == null || !entity.isAlive() || entity.isSpectator()) {
            return false;
        }
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        if (SeismicSurvivalHandler.getTracker().isInsideSafeZone(entity.blockPosition().getX(), entity.blockPosition().getZ())) {
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
        int chunkX = entity.blockPosition().getX() >> 4;
        int chunkZ = entity.blockPosition().getZ() >> 4;
        double chunkVibration = SeismicSurvivalHandler.getTracker().getVibration(chunkX, chunkZ);
        if (chunkVibration >= 20.0) {
            return MAX_SEISMIC_RANGE;
        }
        if (entity.isShiftKeyDown()) {
            return SNEAK_RANGE;
        }
        double deltaMovementSqr = entity.getDeltaMovement().horizontalDistanceSqr();
        if (deltaMovementSqr > 0.001) {
            return WALKING_RANGE;
        }
        return SNEAK_RANGE;
    }
}
