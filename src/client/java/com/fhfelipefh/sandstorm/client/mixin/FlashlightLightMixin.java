package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.client.FlashlightState;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LightmapRenderStateExtractor.class, priority = 1050)
public class FlashlightLightMixin {

    private static final float FLASHLIGHT_BLOCK_FACTOR = 0.45f;

    @Inject(method = "extract", at = @At("RETURN"))
    private void applyFlashlightLight(LightmapRenderState state, float partialTicks, CallbackInfo ci) {
        if (!FlashlightState.isActive()) {
            return;
        }
        if (state.blockFactor < FLASHLIGHT_BLOCK_FACTOR) {
            state.blockFactor = FLASHLIGHT_BLOCK_FACTOR;
        }
    }
}
