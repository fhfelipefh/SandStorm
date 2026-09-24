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
        float brightnessBoost = switch (mode) {
            case FlashlightState.MODE_LOW -> 0.55f;
            case FlashlightState.MODE_MEDIUM -> 1.0f;
            case FlashlightState.MODE_HIGH -> 1.6f;
            default -> 0.0f;
        };
        if (state.blockFactor < lightFactor) {
            state.blockFactor = lightFactor;
        }
        if (state.skyFactor < lightFactor * 0.5f) {
            state.skyFactor = lightFactor * 0.5f;
        }
        state.brightness = Math.max(state.brightness, brightnessBoost);
    }
}
