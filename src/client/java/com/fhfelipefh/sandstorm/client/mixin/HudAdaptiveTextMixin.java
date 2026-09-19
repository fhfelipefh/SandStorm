package com.fhfelipefh.sandstorm.client.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudAdaptiveTextMixin {

    @Shadow
    private Component overlayMessageString;

    @Shadow
    private Component title;

    @Shadow
    private Component subtitle;

    @Shadow
    public abstract Font getFont();

    @Inject(method = "extractOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"))
    private void sandstorm$adaptOverlayMessageScale(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.overlayMessageString != null) {
            int textWidth = this.getFont().width(this.overlayMessageString);
            float maxAllowed = extractor.guiWidth() - 24f;
            if (textWidth > maxAllowed && textWidth > 0) {
                float scale = maxAllowed / (float) textWidth;
                extractor.pose().scale(scale, scale);
            }
        }
    }

    @Inject(method = "extractTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V", ordinal = 0))
    private void sandstorm$adaptTitleScale(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.title != null) {
            int textWidth = this.getFont().width(this.title);
            float currentWidth = textWidth * 4.0f;
            float maxAllowed = extractor.guiWidth() - 24f;
            if (currentWidth > maxAllowed && currentWidth > 0) {
                float scale = maxAllowed / currentWidth;
                extractor.pose().scale(scale, scale);
            }
        }
    }

    @Inject(method = "extractTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V", ordinal = 1))
    private void sandstorm$adaptSubtitleScale(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.subtitle != null) {
            int textWidth = this.getFont().width(this.subtitle);
            float currentWidth = textWidth * 2.0f;
            float maxAllowed = extractor.guiWidth() - 24f;
            if (currentWidth > maxAllowed && currentWidth > 0) {
                float scale = maxAllowed / currentWidth;
                extractor.pose().scale(scale, scale);
            }
        }
    }
}
