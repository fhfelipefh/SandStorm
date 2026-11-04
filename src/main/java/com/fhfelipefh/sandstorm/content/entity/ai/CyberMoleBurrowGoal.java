package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.CyberMoleEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class CyberMoleBurrowGoal extends Goal {
    private final CyberMoleEntity mole;

    public CyberMoleBurrowGoal(CyberMoleEntity mole) {
        this.mole = mole;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.mole.isAlive() || this.mole.isDeactivated() || this.mole.isOrderedToSit()) {
            return false;
        }
        if (this.mole.isSurfacedCombat()) {
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
        this.mole.setBurrowed(true);
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
        LivingEntity target = this.mole.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        this.mole.setBurrowed(true);
        this.mole.getLookControl().setLookAt(target, 40.0f, 40.0f);
        this.mole.getNavigation().moveTo(target, 1.35);

        if (this.mole.level() instanceof ServerLevel serverLevel) {
            if (this.mole.tickCount % 3 == 0) {
                double x = this.mole.getX() + (this.mole.getRandom().nextDouble() - 0.5) * 1.5;
                double y = this.mole.getY() + 0.2;
                double z = this.mole.getZ() + (this.mole.getRandom().nextDouble() - 0.5) * 1.5;
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                        x, y, z, 8, 0.3, 0.2, 0.3, 0.05
                );
                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        x, y, z, 2, 0.2, 0.1, 0.2, 0.02
                );
            }
        }

        double distanceSq = this.mole.distanceToSqr(target);
        if (distanceSq <= 6.25) {
            this.mole.triggerEruption(target);
        }
    }
}

