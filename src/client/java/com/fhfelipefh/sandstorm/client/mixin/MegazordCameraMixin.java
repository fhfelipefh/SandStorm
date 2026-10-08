package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordPilotProfile;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MegazordCameraMixin {
    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Inject(method = "update", at = @At("TAIL"))
    private void useMegazordOpticPosition(DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        Minecraft client = Minecraft.getInstance();
        if (!client.options.getCameraType().isFirstPerson() || client.player == null
                || !(client.player.getVehicle() instanceof MegazordEntity megazord)
                || !MegazordPilotProfile.isPilot(client.player, megazord)) {
            return;
        }
        Vec3 opticPosition = MegazordPilotProfile.opticPosition(megazord, partialTick);
        setPosition(opticPosition.x, opticPosition.y, opticPosition.z);
    }
}
