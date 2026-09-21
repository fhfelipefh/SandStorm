package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

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

        double dx = target.getX() - this.sandworm.getX();
        double dz = target.getZ() - this.sandworm.getZ();
        float targetYaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        this.sandworm.setYRot(targetYaw);
        this.sandworm.setYHeadRot(targetYaw);
        this.sandworm.setYBodyRot(targetYaw);
        this.sandworm.getLookControl().setLookAt(target, 40.0f, 40.0f);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        double reach = 22.0;
        double reachSqr = reach * reach;
        double distSqr = this.sandworm.distanceToSqr(target);

        if (distSqr <= reachSqr && this.attackCooldown <= 0) {
            this.performBiteAttack(target);
            this.attackCooldown = 25;
        }

        this.sandworm.decrementSurfaceTicks();
        if (this.sandworm.getSurfaceTicks() <= 0) {
            this.sandworm.startSubmerging();
        }
    }

    private void performBiteAttack(LivingEntity target) {
        if (this.sandworm.level() instanceof ServerLevel serverLevel) {
            this.sandworm.triggerBiteAnimation();
            float damage = (float) this.sandworm.getAttributeValue(Attributes.ATTACK_DAMAGE) + 6.0f;
            target.hurtServer(serverLevel, this.sandworm.damageSources().mobAttack(this.sandworm), damage);
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 2));

            Vec3 knockback = target.position().subtract(this.sandworm.position()).normalize().scale(1.5);
            target.setDeltaMovement(knockback.x, 0.4, knockback.z);

            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                    target.getX(), target.getY() + 0.5, target.getZ(), 30, 0.8, 0.6, 0.8, 0.15
            );
            serverLevel.playSound(null, this.sandworm.blockPosition(), SandStormSoundEvents.SANDWORM_ATTACK, SoundSource.HOSTILE, 1.8f, 0.9f);
        }
    }

    @Override
    public void stop() {
        if (this.sandworm.getSandwormState() == SandwormState.SURFACED_ASSAULT && this.sandworm.getSurfaceTicks() <= 0) {
            this.sandworm.startSubmerging();
        }
    }
}
