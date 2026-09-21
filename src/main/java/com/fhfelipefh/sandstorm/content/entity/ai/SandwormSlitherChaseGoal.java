package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
        return distSqr > 256.0 && distSqr <= 4096.0;
    }

    @Override
    public boolean canContinueToUse() {
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
    public void start() {
        LivingEntity target = this.sandworm.getTarget();
        if (target != null) {
            this.sandworm.getNavigation().moveTo(target, 1.10);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.sandworm.getTarget();
        if (target == null) {
            return;
        }

        this.sandworm.getNavigation().moveTo(target, 1.10);
        double dx = target.getX() - this.sandworm.getX();
        double dz = target.getZ() - this.sandworm.getZ();
        float targetYaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        float currentYaw = this.sandworm.getYRot();
        float newYaw = Mth.rotateIfNecessary(currentYaw, targetYaw, 3.5f);
        this.sandworm.setYRot(newYaw);
        this.sandworm.setYHeadRot(newYaw);
        this.sandworm.setYBodyRot(newYaw);
        this.sandworm.getLookControl().setLookAt(target, 15.0f, 15.0f);
        this.sandworm.setRearingProgress(Math.max(0.0f, this.sandworm.getRearingProgress() - 0.05f));

        if (this.sandworm.tickCount % 40 == 0 && !this.sandworm.level().isClientSide()) {
            this.sandworm.level().playSound(
                    null,
                    this.sandworm.blockPosition(),
                    SandStormSoundEvents.SANDWORM_RUMBLE,
                    SoundSource.HOSTILE,
                    1.6f,
                    0.8f
            );
        }
    }

    @Override
    public void stop() {
        this.sandworm.getNavigation().stop();
        this.sandworm.setRearingProgress(1.0f);
    }
}
