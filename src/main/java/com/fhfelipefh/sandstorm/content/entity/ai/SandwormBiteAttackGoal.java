package com.fhfelipefh.sandstorm.content.entity.ai;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
        return target != null && target.isAlive() && this.sandworm.canAttack(target);
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

        return this.sandworm.getSurfaceTicks() > 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.sandworm.getTarget();
        if (target == null) {
            return;
        }

        this.sandworm.getLookControl().setLookAt(target, 30.0f, 30.0f);
        this.sandworm.getNavigation().moveTo(target, 1.25);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        double reachSqr = (this.sandworm.getBbWidth() * 1.5 + target.getBbWidth()) * (this.sandworm.getBbWidth() * 1.5 + target.getBbWidth());
        double distSqr = this.sandworm.distanceToSqr(target);

        if (distSqr <= reachSqr && this.attackCooldown <= 0) {
            this.performBiteAttack(target);
            this.attackCooldown = 20;
        }

        this.sandworm.decrementSurfaceTicks();
        if (this.sandworm.getSurfaceTicks() <= 0) {
            this.sandworm.startSubmerging();
        }
    }

    private void performBiteAttack(LivingEntity target) {
        if (this.sandworm.level() instanceof ServerLevel serverLevel) {
            float damage = (float) this.sandworm.getAttributeValue(Attributes.ATTACK_DAMAGE);
            target.hurtServer(serverLevel, this.sandworm.damageSources().mobAttack(this.sandworm), damage);
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
            serverLevel.playSound(null, this.sandworm.blockPosition(), SandStormSoundEvents.SANDWORM_ATTACK, SoundSource.HOSTILE, 1.2f, 1.0f);
        }
    }

    @Override
    public void stop() {
        if (this.sandworm.getSandwormState() == SandwormState.SURFACED_ASSAULT && this.sandworm.getSurfaceTicks() <= 0) {
            this.sandworm.startSubmerging();
        }
    }
}
