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

    @Inject(method = "extract", at = @At("RETURN"))
    private void applyFlashlightLight(LightmapRenderState state, float partialTicks, CallbackInfo ci) {
        if (!FlashlightState.isActive()) {
            return;
        }
        int mode = FlashlightState.getMode();
        float lightFactor = FlashlightState.getLightFactor();

        switch (mode) {
            case FlashlightState.MODE_LOW -> {
                state.blockFactor = Math.max(state.blockFactor, 0.60f);
                state.skyFactor = Math.max(state.skyFactor, 0.30f);
                state.brightness = Math.max(state.brightness, 0.70f);
                state.nightVisionEffectIntensity = Math.max(state.nightVisionEffectIntensity, 0.25f);
            }
            case FlashlightState.MODE_MEDIUM -> {
                state.blockFactor = Math.max(state.blockFactor, 0.85f);
                state.skyFactor = Math.max(state.skyFactor, 0.55f);
                state.brightness = Math.max(state.brightness, 1.0f);
                state.nightVisionEffectIntensity = Math.max(state.nightVisionEffectIntensity, 0.60f);
            }
            case FlashlightState.MODE_HIGH -> {
                state.blockFactor = 1.0f;
                state.skyFactor = 1.0f;
                state.brightness = 1.0f;
                state.nightVisionEffectIntensity = 1.0f;
                state.darknessEffectScale = 0.0f;
            }
            default -> {}
        }
    }
}
