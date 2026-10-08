package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MegazordPickabilityMixin {
    @Inject(method = "isPickable", at = @At("HEAD"), cancellable = true)
    private void ignoreLocalPilotVehicle(CallbackInfoReturnable<Boolean> cir) {
        Minecraft client = Minecraft.getInstance();
        Object self = this;
        if (self instanceof MegazordEntity megazord && client.player != null && client.player.getVehicle() == megazord) {
            cir.setReturnValue(false);
        }
    }
}
