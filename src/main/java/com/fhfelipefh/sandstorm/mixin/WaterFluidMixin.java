package com.fhfelipefh.sandstorm.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {
    @Inject(method = "canConvertToSource", at = @At("HEAD"), cancellable = true)
    private void disableInfiniteWater(ServerLevel level, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
