package com.fhfelipefh.sandstorm.client.mixin;

import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Camera;

import net.minecraft.world.level.ClipContext;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MegazordBlockRaycastMixin {
    @Inject(method = "pick", at = @At("TAIL"))
    private void restorePilotBlockTarget(float partialTick, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null || !(player.getVehicle() instanceof MegazordEntity megazord)
                || !(client.hitResult instanceof EntityHitResult entityHit)
                || entityHit.getEntity() != megazord) {
            return;
        }

        Camera camera = client.gameRenderer.mainCamera();
        Vec3 origin = camera.position();
        Vector3fc forward = camera.forwardVector();
        Vec3 direction = new Vec3(forward.x(), forward.y(), forward.z());
        double reach = player.blockInteractionRange();
        Vec3 target = origin.add(direction.scale(reach));
        BlockHitResult blockHit = player.level().clip(new ClipContext(
                origin,
                target,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
        if (blockHit.getType() != HitResult.Type.MISS) {
            client.hitResult = blockHit;
        }
    }
}
