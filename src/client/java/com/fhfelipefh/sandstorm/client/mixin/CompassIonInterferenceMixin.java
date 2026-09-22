package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassAngleState.class)
public class CompassIonInterferenceMixin {

    @Inject(method = "calculate", at = @At("RETURN"), cancellable = true)
    private void applyIonInterference(ItemStack stack, ClientLevel level, int seed, ItemOwner owner, CallbackInfoReturnable<Float> cir) {
        if (SandstormWeatherHandler.getWeather().isActive() && SandstormWeatherHandler.getWeather().getIntensity() >= 0.70) {
            long now = System.currentTimeMillis();
            float time = (float) (now % 10000L) / 1000.0f;
            float jitter = (float) Math.sin(now * 0.02) * 0.25f;
            float erraticAngle = (time * 3.7f + jitter) % 1.0f;
            if (erraticAngle < 0.0f) {
                erraticAngle += 1.0f;
            }
            cir.setReturnValue(erraticAngle);
        }
    }
}
