package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SandwormSlitherChaseGoal extends Goal {
    private final SandwormEntity sandworm;

    public SandwormSlitherChaseGoal(SandwormEntity sandworm) {
        this.sandworm = sandworm;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.sandworm.getSandwormState() != SandwormState.SURFACED_ASSAULT) {
            return false;
        }
        LivingEntity target = this.sandworm.getTarget();
        if (target == null || !target.isAlive() || !this.sandworm.canAttack(target)) {
            return false;
        }
        double distSqr = this.sandworm.distanceToSqr(target);
        return distSqr > 196.0 && distSqr <= 4096.0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        this.sandworm.startSubmerging();
    }

    @Override
    public void stop() {
        this.sandworm.getNavigation().stop();
    }
}
