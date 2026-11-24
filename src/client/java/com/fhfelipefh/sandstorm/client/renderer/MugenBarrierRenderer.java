package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.survival.MugenInfinityHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class MugenBarrierRenderer {

    private MugenBarrierRenderer() {
    }

    public static void render(LevelRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        CameraRenderState camState = context.levelState().cameraRenderState;
        if (camState == null || camState.pos == null) {
            return;
        }

        Vec3 camPos = camState.pos;
        float ticks = context.levelState().gameTime + context.levelState().worldPartialTicks;
        PoseStack poseStack = context.poseStack();
        SubmitNodeCollector collector = context.submitNodeCollector();

        for (Player player : mc.level.players()) {
            if (!MugenInfinityHandler.isClientMugenActive(player.getId())) {
                continue;
            }
            boolean firstPersonSelf = player == mc.player && mc.options.getCameraType().isFirstPerson();
            float flash = MugenInfinityHandler.getClientImpactFlashStrength(player.getId());

            double px = Mth.lerp(context.levelState().worldPartialTicks, player.xo, player.getX());
            double py = Mth.lerp(context.levelState().worldPartialTicks, player.yo, player.getY()) + player.getBbHeight() * 0.55;
            double pz = Mth.lerp(context.levelState().worldPartialTicks, player.zo, player.getZ());

            poseStack.pushPose();
            poseStack.translate((float) (px - camPos.x), (float) (py - camPos.y), (float) (pz - camPos.z));

            if (firstPersonSelf) {
                renderAsymptoticRing(poseStack, collector, 1.35f, -0.55f, ticks * 3.0f, 12.0f, 0, 229, 255, (int) (75 + flash * 120), 2.0f);
                if (flash > 0.05f) {
                    renderAsymptoticRing(poseStack, collector, 1.25f + (1.0f - flash) * 0.45f, -0.35f, -ticks * 4.5f, -18.0f, 225, 245, 255, (int) (flash * 210), 2.8f);
                }
            } else {
                float pulse = 0.05f * Mth.sin(ticks * 0.14f);
                int r = (int) Mth.lerp(flash, 30.0f, 235.0f);
                int g = (int) Mth.lerp(flash, 225.0f, 245.0f);
                int b = 255;

                renderAsymptoticRing(poseStack, collector, 1.18f + pulse, 0.0f, ticks * 4.5f, 28.0f, r, g, b, 210, 2.4f);
                renderAsymptoticRing(poseStack, collector, 1.28f - pulse, 0.0f, -ticks * 3.8f, -42.0f, 150, 195, 255, 185, 2.1f);
                renderAsymptoticRing(poseStack, collector, 1.38f + pulse * 0.5f, 0.0f, ticks * 2.4f, 82.0f, 220, 250, 255, 150, 1.8f);
                renderAsymptoticRing(poseStack, collector, 1.05f, -0.68f, -ticks * 6.0f, 0.0f, 0, 229, 255, 170, 2.0f);

                if (flash > 0.02f) {
                    float shockRadius = 1.15f + (1.0f - flash) * 0.95f;
                    renderAsymptoticRing(poseStack, collector, shockRadius, 0.0f, ticks * 8.0f, -15.0f, 255, 255, 255, (int) (flash * 245), 3.4f);
                }
            }

            poseStack.popPose();
        }
    }

    private static void renderAsymptoticRing(PoseStack poseStack, SubmitNodeCollector collector, float radius, float yOffset, float yawDeg, float tiltDeg, int r, int g, int b, int a, float lineWidth) {
        poseStack.pushPose();
        poseStack.translate(0.0f, yOffset, 0.0f);
        poseStack.rotateDegrees(Axis.ZP, tiltDeg);
        poseStack.rotateDegrees(Axis.YP, yawDeg);
        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            int segments = 32;
            for (int i = 0; i < segments; i++) {
                float a1 = (float) (i * 2.0 * Math.PI / segments);
                float a2 = (float) ((i + 1) * 2.0 * Math.PI / segments);
                float x1 = Mth.cos(a1) * radius;
                float z1 = Mth.sin(a1) * radius;
                float x2 = Mth.cos(a2) * radius;
                float z2 = Mth.sin(a2) * radius;
                consumer.addVertex(pose, x1, 0.0f, z1).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(lineWidth);
                consumer.addVertex(pose, x2, 0.0f, z2).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(lineWidth);
            }
        });
        poseStack.popPose();
    }
}
