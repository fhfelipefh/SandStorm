package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.client.renderer.FirstPersonSuitArmRenderer;
import com.fhfelipefh.sandstorm.client.renderer.MegazordFirstPersonArmRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class FirstPersonSuitArmMixin {

    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    private void renderSuitRightHand(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, Identifier texture, boolean hasSleeve, CallbackInfo ci) {
        if (MegazordFirstPersonArmRenderer.renderRightArm(poseStack, collector, packedLight)
                || FirstPersonSuitArmRenderer.renderRightArm(poseStack, collector, packedLight)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    private void renderSuitLeftHand(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, Identifier texture, boolean hasSleeve, CallbackInfo ci) {
        if (MegazordFirstPersonArmRenderer.renderLeftArm(poseStack, collector, packedLight)
                || FirstPersonSuitArmRenderer.renderLeftArm(poseStack, collector, packedLight)) {
            ci.cancel();
        }
    }
}
