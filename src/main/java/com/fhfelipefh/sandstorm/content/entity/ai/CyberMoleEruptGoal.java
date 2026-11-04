package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.CyberMoleEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CyberMoleEruptGoal extends Goal {
    private final CyberMoleEntity mole;
    private int attackCooldown = 0;

    public CyberMoleEruptGoal(CyberMoleEntity mole) {
        this.mole = mole;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.mole.isAlive() || this.mole.isDeactivated() || this.mole.isOrderedToSit()) {
            return false;
        }
        if (!this.mole.isSurfacedCombat()) {
            return false;
        }
        LivingEntity target = this.mole.getTarget();
        return target != null && target.isAlive() && this.mole.canAttack(target);
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.mole.setBurrowed(false);
        this.attackCooldown = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mole.getTarget();
        if (target == null || !target.isAlive()) {
            this.mole.finishSurfacedCombat();
            return;
        }

        this.mole.setBurrowed(false);
        this.mole.getLookControl().setLookAt(target, 30.0f, 30.0f);
        this.mole.getNavigation().moveTo(target, 1.15);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        double distanceSq = this.mole.distanceToSqr(target);
        if (distanceSq <= 4.0 && this.attackCooldown <= 0) {
            if (this.mole.level() instanceof ServerLevel serverLevel) {
                this.mole.doHurtTarget(serverLevel, target);
            }
            this.attackCooldown = 15;
        }

        this.mole.tickSurfacedTimer();
    }
}

