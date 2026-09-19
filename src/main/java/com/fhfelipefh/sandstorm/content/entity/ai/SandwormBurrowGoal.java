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
        return this.sandworm.getSandwormState() == SandwormState.BURROWED;
    }

    @Override
    public boolean canContinueToUse() {
        return this.sandworm.getSandwormState() == SandwormState.BURROWED;
    }

    @Override
    public void tick() {
        LivingEntity target = this.sandworm.getTarget();
        if (target != null && target.isAlive() && this.sandworm.canAttack(target)) {
            this.sandworm.getLookControl().setLookAt(target, 30.0f, 30.0f);
            this.sandworm.getNavigation().moveTo(target, 1.35);
        }

        if (this.sandworm.level() instanceof ServerLevel serverLevel) {
            if (this.sandworm.tickCount % 5 == 0) {
                double x = this.sandworm.getX() + (this.sandworm.getRandom().nextDouble() - 0.5) * 2.0;
                double y = this.sandworm.getY() + 0.2;
                double z = this.sandworm.getZ() + (this.sandworm.getRandom().nextDouble() - 0.5) * 2.0;
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                        x, y, z, 6, 0.3, 0.2, 0.3, 0.05
                );
            }

            if (this.sandworm.tickCount % 80 == 0) {
                serverLevel.playSound(
                        null,
                        this.sandworm.blockPosition(),
                        SandStormSoundEvents.SANDWORM_RUMBLE,
                        SoundSource.HOSTILE,
                        1.0f,
                        0.75f
                );
            }
        }
    }
}
