package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.entity.KineticRailgunBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class KineticRailgunBlockEntityRenderer implements BlockEntityRenderer<KineticRailgunBlockEntity, KineticRailgunRenderState> {

    public KineticRailgunBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public KineticRailgunRenderState createRenderState() {
        return new KineticRailgunRenderState();
    }

    @Override
    public void extractRenderState(KineticRailgunBlockEntity entity, KineticRailgunRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        state.cooldown = entity.getCooldown();
        state.readyToFire = entity.getStoredEnergy() >= 5000;
        state.storedEnergy = entity.getStoredEnergy();
        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(KineticRailgunRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float recoil = state.cooldown > 45 ? ((state.cooldown - 45) / 15.0f) * 0.15f : 0.0f;
        float sweepYaw = (state.animationTicks * 0.8f) % ((float) Math.PI * 2.0f);
        float cos = Mth.cos(sweepYaw);
        float sin = Mth.sin(sweepYaw);

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            float baseY = 0.95f;
            float tipY = 1.65f - recoil;

            float r1X = 0.5f + cos * 0.08f;
            float r1Z = 0.5f + sin * 0.08f;
            float r2X = 0.5f - cos * 0.08f;
            float r2Z = 0.5f - sin * 0.08f;

            consumer.addVertex(pose, r1X, baseY, r1Z).setColor(70, 75, 80, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, r1X, tipY, r1Z).setColor(0, 229, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

            consumer.addVertex(pose, r2X, baseY, r2Z).setColor(70, 75, 80, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, r2X, tipY, r2Z).setColor(0, 229, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

            for (int ring = 1; ring <= 4; ring++) {
                float ringY = baseY + (ring * 0.14f) - (recoil * 0.5f);
                float ringPulse = 0.7f + 0.3f * Mth.sin(state.animationTicks * 0.25f + ring);
                int glow = (int) (255 * ringPulse);

                consumer.addVertex(pose, r1X, ringY, r1Z).setColor(0, glow, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                consumer.addVertex(pose, r2X, ringY, r2Z).setColor(0, glow, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
            }

            if (recoil > 0.02f) {
                for (int s = 0; s < 4; s++) {
                    float a = s * ((float) Math.PI / 2.0f);
                    float sx = 0.5f + Mth.cos(a) * 0.18f;
                    float sz = 0.5f + Mth.sin(a) * 0.18f;
                    consumer.addVertex(pose, 0.5f, tipY, 0.5f).setColor(255, 255, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
                    consumer.addVertex(pose, sx, tipY + 0.12f, sz).setColor(0, 229, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
                }
            }
        });
    }
}
