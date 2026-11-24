package com.fhfelipefh.sandstorm.mixin;

import com.fhfelipefh.sandstorm.content.survival.MugenInfinityHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MugenEntityMixin {

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void sandstorm$cancelEntityPush(Entity other, CallbackInfo ci) {
        if (MugenInfinityHandler.shouldCancelKnockback((Entity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "push(DDD)V", at = @At("HEAD"), cancellable = true)
    private void sandstorm$cancelCoordinatePush(double x, double y, double z, CallbackInfo ci) {
        if (MugenInfinityHandler.shouldCancelKnockback((Entity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "push(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    private void sandstorm$cancelVecPush(Vec3 vec, CallbackInfo ci) {
        if (MugenInfinityHandler.shouldCancelKnockback((Entity) (Object) this)) {
            ci.cancel();
        }
    }
}
