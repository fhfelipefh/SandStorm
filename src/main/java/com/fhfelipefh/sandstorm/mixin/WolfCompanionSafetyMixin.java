package com.fhfelipefh.sandstorm.mixin;

import com.fhfelipefh.sandstorm.content.entity.ai.CompanionTrapHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public abstract class WolfCompanionSafetyMixin {

    @Inject(method = "wantsToAttack", at = @At("HEAD"), cancellable = true)
    private void sandstorm$neverAttackTamedCompanions(LivingEntity target, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        if (target != null && CompanionTrapHelper.isTamedCompanion(target)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void sandstorm$neverTargetTamedCompanions(LivingEntity target, CallbackInfo ci) {
        if (target != null && CompanionTrapHelper.isTamedCompanion(target)) {
            ci.cancel();
        }
    }
}
