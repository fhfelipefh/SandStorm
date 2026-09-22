package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.client.mirage.DesertMirageHandler;
import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class SandstormFogMixin {

    private static final float DUST_R = 0.74f;
    private static final float DUST_G = 0.57f;
    private static final float DUST_B = 0.41f;

    @Inject(method = "setupFog", at = @At("RETURN"))
    private void applySandstormFog(Camera camera, int i, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<FogData> cir) {
        FogData data = cir.getReturnValue();
        if (data == null || clientLevel == null) {
            return;
        }

        SandstormWeatherComponent weather = SandstormWeatherHandler.getWeather();
        if (!weather.isActive()) {
            if (DesertMirageHandler.isMirageActive(Minecraft.getInstance())) {
                float gameTime = (float) clientLevel.getGameTime() + deltaTracker.getGameTimeDeltaPartialTick(true);
                float heatwave = (float) Math.sin(gameTime * 0.12f) * 3.5f;

                Vector4f color = data.color;
                if (color != null) {
                    color.x = Math.min(1.0f, color.x + 0.12f);
                    color.y = Math.min(1.0f, color.y + 0.06f);
                }

                data.renderDistanceEnd = Math.max(32.0f, data.renderDistanceEnd - 22.0f + heatwave);
                data.renderDistanceStart = Math.max(6.0f, data.renderDistanceStart - 8.0f + heatwave * 0.4f);
                data.environmentalEnd = data.renderDistanceEnd;
                data.environmentalStart = data.renderDistanceStart;
            }
            return;
        }

        float intensity = (float) weather.getIntensity();
        if (intensity <= 0.02f) {
            return;
        }

        boolean outdoors = clientLevel.canSeeSky(camera.blockPosition()) || clientLevel.canSeeSky(camera.blockPosition().above());
        float factor = outdoors ? intensity : (intensity * 0.35f);

        Vector4f color = data.color;
        if (color != null) {
            color.x = color.x + (DUST_R - color.x) * factor;
            color.y = color.y + (DUST_G - color.y) * factor;
            color.z = color.z + (DUST_B - color.z) * factor;
        }

        float targetEnd = 24.0f + (1.0f - factor) * 44.0f;
        float targetStart = 4.0f + (1.0f - factor) * 8.0f;

        data.renderDistanceEnd = Math.min(data.renderDistanceEnd, targetEnd);
        data.environmentalEnd = Math.min(data.environmentalEnd, targetEnd);
        data.renderDistanceStart = Math.min(data.renderDistanceStart, targetStart);
        data.environmentalStart = Math.min(data.environmentalStart, targetStart);
        data.skyEnd = Math.min(data.skyEnd, targetEnd * 1.15f);
        data.cloudEnd = Math.min(data.cloudEnd, targetEnd * 1.15f);
    }
}
