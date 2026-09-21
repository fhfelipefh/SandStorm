package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SandwormBreachGoal extends Goal {
    private final SandwormEntity sandworm;
    private int breachTicks;

    public SandwormBreachGoal(SandwormEntity sandworm) {
        this.sandworm = sandworm;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.sandworm.getSandwormState() != SandwormState.BURROWED) {
            return false;
        }

        LivingEntity target = this.sandworm.getTarget();
        if (target == null || !target.isAlive() || !this.sandworm.canAttack(target)) {
            return false;
        }

        double distSqr = this.sandworm.distanceToSqr(target);
        return distSqr <= 196.0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.breachTicks > 0 && this.sandworm.getSandwormState() == SandwormState.BREACHING;
    }

    @Override
    public void start() {
        this.breachTicks = 35;
        this.sandworm.setSandwormState(SandwormState.BREACHING);
        this.sandworm.getNavigation().stop();
        this.sandworm.setDeltaMovement(0.0, 0.0, 0.0);
        LivingEntity target = this.sandworm.getTarget();
        if (target != null) {
            double dx = target.getX() - this.sandworm.getX();
            double dz = target.getZ() - this.sandworm.getZ();
            float targetYaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
            this.sandworm.setYRot(targetYaw);
            this.sandworm.setYHeadRot(targetYaw);
            this.sandworm.setYBodyRot(targetYaw);
        }
        this.sandworm.triggerBreachShockwave();
    }

    @Override
    public void tick() {
        this.breachTicks--;
        this.sandworm.getNavigation().stop();
        this.sandworm.setDeltaMovement(0.0, Math.min(0.0, this.sandworm.getDeltaMovement().y), 0.0);
        LivingEntity target = this.sandworm.getTarget();
        if (target != null) {
            this.sandworm.getLookControl().setLookAt(target, 40.0f, 40.0f);
            double dx = target.getX() - this.sandworm.getX();
            double dz = target.getZ() - this.sandworm.getZ();
            float targetYaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
            this.sandworm.setYRot(targetYaw);
            this.sandworm.setYHeadRot(targetYaw);
            this.sandworm.setYBodyRot(targetYaw);
        }

        if (this.breachTicks <= 0) {
            this.sandworm.setSandwormState(SandwormState.SURFACED_ASSAULT);
            this.sandworm.resetSurfaceTimer();
        }
    }

    @Override
    public void stop() {
        if (this.sandworm.getSandwormState() == SandwormState.BREACHING) {
            this.sandworm.setSandwormState(SandwormState.SURFACED_ASSAULT);
            this.sandworm.resetSurfaceTimer();
        }
    }
}
