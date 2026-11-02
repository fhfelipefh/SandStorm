package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.client.SandStormClientWorldHelper;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialSteps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tutorial.class)
public abstract class TutorialMixin {

    @Shadow
    public abstract void stop();

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void sandstorm$disableTutorialStart(CallbackInfo ci) {
        if (SandStormClientWorldHelper.isSandStormWorld()) {
            this.stop();
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void sandstorm$disableTutorialTick(CallbackInfo ci) {
        if (SandStormClientWorldHelper.isSandStormWorld()) {
            this.stop();
            ci.cancel();
        }
    }

    @Inject(method = "setStep", at = @At("HEAD"), cancellable = true)
    private void sandstorm$disableTutorialSetStep(TutorialSteps step, CallbackInfo ci) {
        if (SandStormClientWorldHelper.isSandStormWorld() && step != TutorialSteps.NONE) {
            this.stop();
            ci.cancel();
        }
    }
}

