package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SandwormBiteAttackGoal extends Goal {
    private final SandwormEntity sandworm;
    private int attackCooldown = 0;

    public SandwormBiteAttackGoal(SandwormEntity sandworm) {
        this.sandworm = sandworm;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.sandworm.getSandwormState() != SandwormState.SURFACED_ASSAULT) {
            return false;
        }

        LivingEntity target = this.sandworm.getTarget();
        return target != null && target.isAlive() && this.sandworm.canAttack(target) && this.sandworm.distanceToSqr(target) <= 324.0;
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

        return this.sandworm.getSurfaceTicks() > 0 && this.sandworm.distanceToSqr(target) <= 400.0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.sandworm.getTarget();
        if (target == null) {
            return;
        }

        this.sandworm.getNavigation().stop();
        this.sandworm.setDeltaMovement(0.0, Math.min(0.0, this.sandworm.getDeltaMovement().y), 0.0);
        this.sandworm.setRearingProgress(Math.min(1.0f, this.sandworm.getRearingProgress() + 0.08f));

        BlockPos strikePos = this.sandworm.getStrikePos();
        if (this.sandworm.getBiteTicks() > 0 && strikePos != null && !strikePos.equals(BlockPos.ZERO)) {
            double sx = strikePos.getX() + 0.5 - this.sandworm.getX();
            double sz = strikePos.getZ() + 0.5 - this.sandworm.getZ();
            float strikeYaw = (float) (Math.atan2(sz, sx) * (180.0 / Math.PI)) - 90.0f;
            this.sandworm.setYRot(strikeYaw);
            this.sandworm.setYHeadRot(strikeYaw);
            this.sandworm.setYBodyRot(strikeYaw);
            return;
        }

        double dx = target.getX() - this.sandworm.getX();
        double dz = target.getZ() - this.sandworm.getZ();
        float targetYaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        float currentYaw = this.sandworm.getYRot();
        float newYaw = Mth.rotateIfNecessary(currentYaw, targetYaw, 3.5f);
        this.sandworm.setYRot(newYaw);
        this.sandworm.setYHeadRot(newYaw);
        this.sandworm.setYBodyRot(newYaw);
        this.sandworm.getLookControl().setLookAt(target, 15.0f, 15.0f);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        float yawDiff = Math.abs(Mth.wrapDegrees(targetYaw - currentYaw));
        double reach = 20.0;
        double reachSqr = reach * reach;
        double distSqr = this.sandworm.distanceToSqr(target);

        if (distSqr <= reachSqr && yawDiff <= 25.0f && this.attackCooldown <= 0) {
            this.performBiteAttack(target);
            this.attackCooldown = 40;
        }

        this.sandworm.decrementSurfaceTicks();
        if (this.sandworm.getSurfaceTicks() <= 0) {
            this.sandworm.startSubmerging();
        }
    }

    private void performBiteAttack(LivingEntity target) {
        BlockPos strikePos = target.blockPosition();
        this.sandworm.triggerBiteAnimation(strikePos);
        if (this.sandworm.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.sandworm.blockPosition(), SandStormSoundEvents.SANDWORM_RUMBLE, SoundSource.HOSTILE, 1.8f, 0.85f);
        }
    }

    @Override
    public void stop() {
        if (this.sandworm.getSandwormState() == SandwormState.SURFACED_ASSAULT && this.sandworm.getSurfaceTicks() <= 0) {
            this.sandworm.startSubmerging();
        }
    }
}
