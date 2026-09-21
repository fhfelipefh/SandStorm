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
        } else if (this.showcaseTick < 50) {
            float progress = this.showcaseTick / 50.0f;
            this.worm.setRearingProgress(progress);
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 50) {
            this.worm.setSandwormState(SandwormState.SURFACED_ASSAULT);
            this.worm.setRearingProgress(1.0f);
        } else if (this.showcaseTick < 170) {
            this.worm.setSandwormState(SandwormState.SURFACED_ASSAULT);
            this.worm.setRearingProgress(1.0f);
            float sway = Mth.sin((this.showcaseTick - 50) * 0.04f) * 22.0f;
            this.worm.setYRot(this.initialYaw + sway);
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 170) {
            double yawRad = Math.toRadians(this.worm.getYRot());
            Vec3 forward = new Vec3(-Math.sin(yawRad) * 12.0, 0, Math.cos(yawRad) * 12.0);
            BlockPos strikePos = this.worm.blockPosition().offset((int) forward.x, 0, (int) forward.z);
            this.worm.triggerBiteAnimation(strikePos);
        } else if (this.showcaseTick < 230) {
            if (this.showcaseTick == 190 && this.worm.level() instanceof ServerLevel serverLevel) {
                this.worm.executeBiteImpact(serverLevel);
            }
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 230) {
            this.worm.startSubmerging();
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick < 270) {
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 270) {
            this.worm.setSandwormState(SandwormState.BURROWED);
        } else if (this.showcaseTick < 400) {
            this.worm.setSandwormState(SandwormState.BURROWED);
            float travelPhase = (this.showcaseTick - 270) / 130.0f;
            double offsetX = Math.sin(travelPhase * Math.PI) * 12.0;
            double offsetZ = (1.0 - Math.cos(travelPhase * Math.PI)) * 6.0;
            double currentX = this.originPos.getX() + 0.5 + offsetX;
            double currentZ = this.originPos.getZ() + 0.5 + offsetZ;
            this.worm.setPos(currentX, this.originPos.getY(), currentZ);

            if (this.worm.level() instanceof ServerLevel serverLevel && this.showcaseTick % 3 == 0) {
                double x = this.worm.getX() + (this.worm.getRandom().nextDouble() - 0.5) * 3.5;
                double y = this.worm.getY() + 0.3;
                double z = this.worm.getZ() + (this.worm.getRandom().nextDouble() - 0.5) * 3.5;
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), x, y, z, 14, 0.8, 0.4, 0.8, 0.12);
                serverLevel.sendParticles(ParticleTypes.POOF, x, y, z, 3, 0.6, 0.2, 0.6, 0.02);
            }
        } else if (this.showcaseTick == 400) {
            this.worm.setSandwormState(SandwormState.BREACHING);
            this.worm.setRearingProgress(0.0f);
            this.worm.triggerBreachShockwave();
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick < 450) {
            float progress = (this.showcaseTick - 400) / 50.0f;
            this.worm.setRearingProgress(progress);
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 450) {
            this.worm.setSandwormState(SandwormState.SURFACED_ASSAULT);
            this.worm.triggerBiteAnimation();
        } else if (this.showcaseTick < 490) {
            if (this.showcaseTick == 470 && this.worm.level() instanceof ServerLevel serverLevel) {
                this.worm.executeBiteImpact(serverLevel);
            }
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 490) {
            this.worm.startSubmerging();
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick < 530) {
            this.worm.setDeltaMovement(0, 0, 0);
        } else if (this.showcaseTick == 530) {
            this.worm.setSandwormState(SandwormState.BURROWED);
            this.worm.setPos(this.originPos.getX() + 0.5, this.originPos.getY(), this.originPos.getZ() + 0.5);
            this.worm.setYRot(this.initialYaw);
        } else if (this.showcaseTick < 570) {
            this.worm.setSandwormState(SandwormState.BURROWED);
            this.worm.setDeltaMovement(0, 0, 0);
            if (this.worm.level() instanceof ServerLevel serverLevel && this.showcaseTick % 4 == 0) {
                double x = this.worm.getX() + (this.worm.getRandom().nextDouble() - 0.5) * 3.5;
                double y = this.worm.getY() + 0.3;
                double z = this.worm.getZ() + (this.worm.getRandom().nextDouble() - 0.5) * 3.5;
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), x, y, z, 10, 0.8, 0.4, 0.8, 0.1);
                serverLevel.sendParticles(ParticleTypes.POOF, x, y, z, 2, 0.5, 0.2, 0.5, 0.02);
            }
        }

        this.showcaseTick++;
        if (this.showcaseTick >= 570) {
            this.showcaseTick = 0;
        }
    }
}
