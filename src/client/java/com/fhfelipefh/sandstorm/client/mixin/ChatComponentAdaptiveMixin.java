package com.fhfelipefh.sandstorm.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public class ChatComponentAdaptiveMixin {

    @Inject(method = "getWidth(D)I", at = @At("RETURN"), cancellable = true)
    private static void sandstorm$clampChatWidthToScreen(double chatWidthOption, CallbackInfoReturnable<Integer> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getWindow() != null) {
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            if (screenWidth > 0) {
                int maxAllowed = screenWidth - 8;
                int original = cir.getReturnValue();
                if (original > maxAllowed) {
                    cir.setReturnValue(Math.max(40, maxAllowed));
                }
            }
        }
    }
}
