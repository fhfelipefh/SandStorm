package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class MegazordPilotPoseMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void useStandingPilotPose(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        if (avatar instanceof Player player && player.getVehicle() instanceof MegazordEntity) {
            boolean isLocalFirstPersonPilot = player == Minecraft.getInstance().player
                    && Minecraft.getInstance().options.getCameraType().isFirstPerson();
            state.isPassenger = false;
            state.isInvisible = !isLocalFirstPersonPilot;
            state.isInvisibleToPlayer = !isLocalFirstPersonPilot;
        }
    }
}
