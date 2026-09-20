package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SandwormBreachGoal extends Goal {
    private final SandwormEntity sandworm;
    private int breachTicks;

    public SandwormBreachGoal(SandwormEntity sandworm) {
        this.sandworm = sandworm;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
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
        LivingEntity target = this.sandworm.getTarget();
        Vec3 forward = Vec3.ZERO;
        if (target != null) {
            forward = new Vec3(target.getX() - this.sandworm.getX(), 0, target.getZ() - this.sandworm.getZ()).normalize();
        }
        this.sandworm.setDeltaMovement(forward.x * 0.45, 1.15, forward.z * 0.45);
        this.sandworm.triggerBreachShockwave();
    }

    @Override
    public void tick() {
        this.breachTicks--;
        LivingEntity target = this.sandworm.getTarget();
        if (target != null) {
            this.sandworm.getLookControl().setLookAt(target, 40.0f, 40.0f);
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
