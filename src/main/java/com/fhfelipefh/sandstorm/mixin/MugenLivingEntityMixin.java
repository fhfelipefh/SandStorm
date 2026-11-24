package com.fhfelipefh.sandstorm.mixin;

import com.fhfelipefh.sandstorm.content.survival.MugenInfinityHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MugenLivingEntityMixin {

    @Inject(method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;FZ)V", at = @At("HEAD"), cancellable = true)
    private void sandstorm$cancelKnockbackWithSource(double strength, double x, double z, DamageSource source, float scale, boolean force, CallbackInfo ci) {
        if (MugenInfinityHandler.shouldCancelKnockback((LivingEntity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("HEAD"), cancellable = true)
    private void sandstorm$cancelKnockbackFiveArgs(double strength, double x, double z, DamageSource source, float scale, CallbackInfo ci) {
        if (MugenInfinityHandler.shouldCancelKnockback((LivingEntity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void sandstorm$preventPushingWhenMugenActive(CallbackInfoReturnable<Boolean> cir) {
        if (MugenInfinityHandler.shouldCancelKnockback((LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
