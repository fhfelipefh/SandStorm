package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.GravitonSphereEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class GravitonSphereRenderer extends EntityRenderer<GravitonSphereEntity, GravitonSphereRenderState> {
    private static final Identifier SPHERE_TEXTURE = SandStormMod.id("textures/entity/graviton_sphere.png");

    public GravitonSphereRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
    }

    @Override
    public GravitonSphereRenderState createRenderState() {
        return new GravitonSphereRenderState();
    }

    @Override
    public void extractRenderState(GravitonSphereEntity entity, GravitonSphereRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.mode = entity.getSphereMode();
        state.collapsing = entity.isCollapsing();
        state.animationTicks = entity.tickCount + partialTick;
    }

    @Override
    public void submit(GravitonSphereRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        boolean isRed = state.mode == GravitonSphereEntity.MODE_RED_REVERSAL;
        boolean isPurple = state.mode == GravitonSphereEntity.MODE_VOID_PURPLE;
        float t = state.animationTicks;
        float pulse = 1.0f + 0.14f * Mth.sin(t * (isPurple ? 0.65f : 0.45f));
        float baseRadius = (isPurple ? 0.82f : (state.collapsing ? 0.68f : 0.48f)) * pulse;

        int coreR = isPurple ? 245 : (isRed ? 255 : 220);
        int coreG = isPurple ? 210 : (isRed ? 220 : 250);
        int coreB = 255;

        int outerR = isPurple ? 155 : (isRed ? 255 : 0);
        int outerG = isPurple ? 48 : (isRed ? 35 : 190);
        int outerB = isPurple ? 255 : (isRed ? 75 : 255);

        poseStack.pushPose();
        poseStack.translate(0.0f, isPurple ? 0.55f : 0.45f, 0.0f);

        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, t * (isPurple ? 14.0f : 9.0f));
        poseStack.rotateDegrees(Axis.XP, t * (isPurple ? 8.0f : 5.0f));
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(SPHERE_TEXTURE), (pose, consumer) -> {
            renderSphereMesh(consumer, pose, baseRadius * 0.55f, coreR, coreG, coreB, 250);
            renderSphereMesh(consumer, pose, baseRadius, outerR, outerG, outerB, isPurple ? 195 : 175);
        });
        poseStack.popPose();

        renderOrbitalRing(poseStack, collector, baseRadius * 1.45f, t * 14.0f, 28.0f, outerR, outerG, outerB, 230);
        renderOrbitalRing(poseStack, collector, baseRadius * 1.75f, -t * 11.0f, -52.0f, coreR, coreG, coreB, 195);
        renderOrbitalRing(poseStack, collector, baseRadius * 2.05f, t * 7.5f, 75.0f, outerR, outerG, outerB, 150);
        if (isPurple) {
            renderOrbitalRing(poseStack, collector, baseRadius * 1.62f, t * 19.0f, -18.0f, 0, 184, 255, 195);
            renderOrbitalRing(poseStack, collector, baseRadius * 1.62f, -t * 19.0f, 18.0f, 255, 30, 66, 195);
            renderOrbitalRing(poseStack, collector, baseRadius * 2.35f, t * 12.0f, 45.0f, 217, 70, 239, 175);
        }

        poseStack.popPose();
    }

    private void renderOrbitalRing(PoseStack poseStack, SubmitNodeCollector collector, float radius, float yawDeg, float tiltDeg, int r, int g, int b, int a) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.ZP, tiltDeg);
        poseStack.rotateDegrees(Axis.YP, yawDeg);
        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            int segments = 24;
            for (int i = 0; i < segments; i++) {
                float a1 = (float) (i * 2.0 * Math.PI / segments);
                float a2 = (float) ((i + 1) * 2.0 * Math.PI / segments);
                float x1 = Mth.cos(a1) * radius;
                float z1 = Mth.sin(a1) * radius;
                float x2 = Mth.cos(a2) * radius;
                float z2 = Mth.sin(a2) * radius;
                consumer.addVertex(pose, x1, 0.0f, z1).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.6f);
                consumer.addVertex(pose, x2, 0.0f, z2).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.6f);
            }
        });
        poseStack.popPose();
    }

    private void renderSphereMesh(VertexConsumer consumer, PoseStack.Pose pose, float radius, int r, int g, int b, int a) {
        int stacks = 8;
        int slices = 12;
        for (int i = 0; i < stacks; i++) {
            float phi1 = (float) (Math.PI * i / stacks);
            float phi2 = (float) (Math.PI * (i + 1) / stacks);
            for (int j = 0; j < slices; j++) {
                float theta1 = (float) (2.0 * Math.PI * j / slices);
                float theta2 = (float) (2.0 * Math.PI * (j + 1) / slices);

                float x11 = Mth.sin(phi1) * Mth.cos(theta1);
                float y11 = Mth.cos(phi1);
                float z11 = Mth.sin(phi1) * Mth.sin(theta1);

                float x21 = Mth.sin(phi2) * Mth.cos(theta1);
                float y21 = Mth.cos(phi2);
                float z21 = Mth.sin(phi2) * Mth.sin(theta1);

                float x22 = Mth.sin(phi2) * Mth.cos(theta2);
                float y22 = Mth.cos(phi2);
                float z22 = Mth.sin(phi2) * Mth.sin(theta2);

                float x12 = Mth.sin(phi1) * Mth.cos(theta2);
                float y12 = Mth.cos(phi1);
                float z12 = Mth.sin(phi1) * Mth.sin(theta2);

                float u1 = (float) j / slices;
                float u2 = (float) (j + 1) / slices;
                float v1 = (float) i / stacks;
                float v2 = (float) (i + 1) / stacks;

                addQuadVertex(consumer, pose, x11, y11, z11, radius, r, g, b, a, u1, v1);
                addQuadVertex(consumer, pose, x21, y21, z21, radius, r, g, b, a, u1, v2);
                addQuadVertex(consumer, pose, x22, y22, z22, radius, r, g, b, a, u2, v2);
                addQuadVertex(consumer, pose, x12, y12, z12, radius, r, g, b, a, u2, v1);
            }
        }
    }

    private void addQuadVertex(VertexConsumer consumer, PoseStack.Pose pose, float nx, float ny, float nz, float radius, int r, int g, int b, int a, float u, float v) {
        consumer.addVertex(pose, nx * radius, ny * radius, nz * radius)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(pose, nx, ny, nz);
    }
}
