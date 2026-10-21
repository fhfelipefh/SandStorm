package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.client.handler.HeavyMachineryCameraHandler;
import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
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

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    public abstract float yRot();

    @Shadow
    public abstract float xRot();

    @Inject(method = "update", at = @At("TAIL"))
    private void useMegazordOpticPosition(DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        Minecraft client = Minecraft.getInstance();
        if (!client.options.getCameraType().isFirstPerson() || client.player == null) {
            HeavyMachineryCameraHandler.reset();
            return;
        }

        if (client.player.getVehicle() instanceof MegazordEntity megazord
                && MegazordPilotProfile.isPilot(client.player, megazord)) {
            Vec3 opticPosition = MegazordPilotProfile.opticPosition(megazord, partialTick);
            Vec3 bobbing = HeavyMachineryCameraHandler.calculateMegazordBobbing(megazord, partialTick);
            setPosition(opticPosition.x + bobbing.x, opticPosition.y + bobbing.y, opticPosition.z + bobbing.z);

            float[] rotation = HeavyMachineryCameraHandler.applyRotationalLag(yRot(), xRot(), bobbing.y);
            setRotation(rotation[0], rotation[1]);
            return;
        }

        if (client.player.getVehicle() instanceof ExcavatorVehicleEntity excavator
                && excavator.getControllingPassenger() == client.player) {
            Vec3 eyePosition = client.player.getEyePosition(partialTick);
            Vec3 bobbing = HeavyMachineryCameraHandler.calculateExcavatorBobbing(excavator, partialTick);
            setPosition(eyePosition.x + bobbing.x, eyePosition.y + bobbing.y, eyePosition.z + bobbing.z);

            float[] rotation = HeavyMachineryCameraHandler.applyRotationalLag(yRot(), xRot(), bobbing.y);
            setRotation(rotation[0], rotation[1]);
            return;
        }

        HeavyMachineryCameraHandler.reset();
    }
}
