package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class SandwormBurrowGoal extends Goal {
    private final SandwormEntity sandworm;

    public SandwormBurrowGoal(SandwormEntity sandworm) {
        this.sandworm = sandworm;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.sandworm.getSandwormState() != SandwormState.BURROWED) {
            return false;
        }
        LivingEntity target = this.sandworm.getTarget();
        return target != null && target.isAlive() && this.sandworm.canAttack(target);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.sandworm.getSandwormState() != SandwormState.BURROWED) {
            return false;
        }
        LivingEntity target = this.sandworm.getTarget();
        return target != null && target.isAlive() && this.sandworm.canAttack(target);
    }

    @Override
    public void tick() {
        LivingEntity target = this.sandworm.getTarget();
        if (target != null && target.isAlive() && this.sandworm.canAttack(target)) {
            this.sandworm.getLookControl().setLookAt(target, 40.0f, 40.0f);
            this.sandworm.getNavigation().moveTo(target, 1.4);
        }

        if (this.sandworm.level() instanceof ServerLevel serverLevel) {
            if (this.sandworm.tickCount % 3 == 0) {
                double x = this.sandworm.getX() + (this.sandworm.getRandom().nextDouble() - 0.5) * 4.0;
                double y = this.sandworm.getY() + 0.3;
                double z = this.sandworm.getZ() + (this.sandworm.getRandom().nextDouble() - 0.5) * 4.0;
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                        x, y, z, 12, 0.8, 0.4, 0.8, 0.1
                );
                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        x, y, z, 2, 0.5, 0.2, 0.5, 0.02
                );
            }

            if (this.sandworm.tickCount % 50 == 0) {
                serverLevel.playSound(
                        null,
                        this.sandworm.blockPosition(),
                        SandStormSoundEvents.SANDWORM_RUMBLE,
                        SoundSource.HOSTILE,
                        1.4f,
                        0.7f
                );
            }
        }
    }
}
