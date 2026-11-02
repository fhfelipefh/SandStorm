package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.client.SandStormClientWorldHelper;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public abstract class ToastManagerMixin {

    @Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
    private void sandstorm$suppressVanillaToasts(Toast toast, CallbackInfo ci) {
        if (SandStormClientWorldHelper.isSandStormWorld() && SandStormClientWorldHelper.isVanillaNotification(toast)) {
            ci.cancel();
        }
    }
}

