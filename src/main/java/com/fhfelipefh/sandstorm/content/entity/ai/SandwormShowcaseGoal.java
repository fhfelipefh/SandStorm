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

        this.worm.setSandwormState(SandwormState.SURFACED_ASSAULT);
        this.worm.setRearingProgress(1.0f);
        this.worm.setDeltaMovement(0, 0, 0);

        float sway = Mth.sin(this.showcaseTick * 0.04f) * 25.0f;
        this.worm.setYRot(this.initialYaw + sway);

        if (this.showcaseTick % 120 == 60) {
            double yawRad = Math.toRadians(this.worm.getYRot());
            Vec3 forward = new Vec3(-Math.sin(yawRad) * 12.0, 0, Math.cos(yawRad) * 12.0);
            BlockPos strikePos = this.worm.blockPosition().offset((int) forward.x, 0, (int) forward.z);
            this.worm.triggerBiteAnimation(strikePos);
        }

        if (this.showcaseTick % 120 == 80 && this.worm.level() instanceof ServerLevel serverLevel) {
            this.worm.executeBiteImpact(serverLevel);
        }

        if (this.worm.level() instanceof ServerLevel serverLevel && this.showcaseTick % 10 == 0) {
            double x = this.worm.getX() + (this.worm.getRandom().nextDouble() - 0.5) * 3.5;
            double y = this.worm.getY() + 0.3;
            double z = this.worm.getZ() + (this.worm.getRandom().nextDouble() - 0.5) * 3.5;
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), x, y, z, 8, 0.8, 0.4, 0.8, 0.1);
            serverLevel.sendParticles(ParticleTypes.POOF, x, y, z, 2, 0.5, 0.2, 0.5, 0.02);
        }

        this.showcaseTick++;
        if (this.showcaseTick >= 12000) {
            this.showcaseTick = 0;
        }
    }
}
