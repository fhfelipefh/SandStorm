package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SandwormShowcaseGoal extends Goal {
    private final SandwormEntity worm;
    private int showcaseTick;
    private BlockPos originPos;
    private float initialYaw;

    public SandwormShowcaseGoal(SandwormEntity worm) {
        this.worm = worm;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.worm.isShowcaseMode();
    }

    @Override
    public boolean canContinueToUse() {
        return this.worm.isShowcaseMode();
    }

    @Override
    public void start() {
        this.showcaseTick = 0;
        this.originPos = this.worm.blockPosition();
        this.initialYaw = this.worm.getYRot();
    }

    @Override
    public void tick() {
        if (this.originPos == null) {
            this.originPos = this.worm.blockPosition();
            this.initialYaw = this.worm.getYRot();
        }

        if (this.showcaseTick == 0) {
            this.worm.setSandwormState(SandwormState.BREACHING);
            this.worm.setRearingProgress(0.0f);
            this.worm.triggerBreachShockwave();
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick < 60) {
            float progress = this.showcaseTick / 60.0f;
            this.worm.setRearingProgress(progress);
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 60) {
            this.worm.setSandwormState(SandwormState.SURFACED_ASSAULT);
            this.worm.setRearingProgress(1.0f);
        } else if (this.showcaseTick < 160) {
            this.worm.setSandwormState(SandwormState.SURFACED_ASSAULT);
            this.worm.setRearingProgress(1.0f);
            float sway = Mth.sin((this.showcaseTick - 60) * 0.05f) * 20.0f;
            this.worm.setYRot(this.initialYaw + sway);
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 160) {
            double yawRad = Math.toRadians(this.worm.getYRot());
            Vec3 forward = new Vec3(-Math.sin(yawRad) * 12.0, 0, Math.cos(yawRad) * 12.0);
            BlockPos strikePos = this.worm.blockPosition().offset((int) forward.x, 0, (int) forward.z);
            this.worm.triggerBiteAnimation(strikePos);
        } else if (this.showcaseTick < 220) {
            if (this.showcaseTick == 180 && this.worm.level() instanceof ServerLevel serverLevel) {
                this.worm.executeBiteImpact(serverLevel);
            }
        } else if (this.showcaseTick < 340) {
            this.worm.setRearingProgress(0.2f);
            float angle = (this.showcaseTick - 220) * 0.05f;
            double radius = 7.0;
            double targetX = this.originPos.getX() + Math.cos(angle) * radius;
            double targetZ = this.originPos.getZ() + Math.sin(angle) * radius;
            double dx = targetX - this.worm.getX();
            double dz = targetZ - this.worm.getZ();
            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
            this.worm.setYRot(targetYaw);
            this.worm.setDeltaMovement(dx * 0.12, this.worm.getDeltaMovement().y, dz * 0.12);
        } else if (this.showcaseTick == 340) {
            this.worm.triggerBiteAnimation();
        } else if (this.showcaseTick < 400) {
            if (this.showcaseTick == 360 && this.worm.level() instanceof ServerLevel serverLevel) {
                this.worm.executeBiteImpact(serverLevel);
            }
            float rearUp = Mth.clamp((this.showcaseTick - 360) / 40.0f, 0.0f, 1.0f);
            this.worm.setRearingProgress(0.2f + rearUp * 0.6f);
        } else if (this.showcaseTick == 400) {
            this.worm.startSubmerging();
        } else if (this.showcaseTick < 460) {
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 460) {
            this.worm.setSandwormState(SandwormState.BURROWED);
            this.worm.setPos(this.originPos.getX() + 0.5, this.originPos.getY(), this.originPos.getZ() + 0.5);
            this.worm.setYRot(this.initialYaw);
        } else if (this.showcaseTick < 540) {
            this.worm.setSandwormState(SandwormState.BURROWED);
            this.worm.setDeltaMovement(0, 0, 0);
            if (this.worm.level() instanceof ServerLevel serverLevel && this.showcaseTick % 4 == 0) {
                double x = this.worm.getX() + (this.worm.getRandom().nextDouble() - 0.5) * 4.0;
                double y = this.worm.getY() + 0.3;
                double z = this.worm.getZ() + (this.worm.getRandom().nextDouble() - 0.5) * 4.0;
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), x, y, z, 10, 0.8, 0.4, 0.8, 0.1);
                serverLevel.sendParticles(ParticleTypes.POOF, x, y, z, 2, 0.5, 0.2, 0.5, 0.02);
            }
        }

        this.showcaseTick++;
        if (this.showcaseTick >= 540) {
            this.showcaseTick = 0;
        }
    }
}
