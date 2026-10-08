package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class MegazordItemTransformMixin {
    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void alignItemWithMegazordHand(
            PlayerRenderState playerRenderState,
            FirstPersonHandsAndItemsRenderState handsState,
            float partialTick,
            float equipProgress,
            InteractionHand hand,
            float swingProgress,
            ItemStack stack,
            float handHeight,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int packedLight,
            CallbackInfo ci
    ) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.options.getCameraType().isFirstPerson()
                && client.player.getVehicle() instanceof MegazordEntity) {
            float side = hand == InteractionHand.MAIN_HAND ? 1.0f : -1.0f;
            poseStack.translate(side * 0.08f, -0.06f, -0.16f);
            poseStack.scale(1.08f, 1.08f, 1.08f);
        }
    }
}
