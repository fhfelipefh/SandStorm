package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.entity.SynapticCondenserBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SynapticCondenserBlockEntityRenderer implements BlockEntityRenderer<SynapticCondenserBlockEntity, SynapticCondenserRenderState> {
    private final Font font;

    public SynapticCondenserBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public SynapticCondenserRenderState createRenderState() {
        return new SynapticCondenserRenderState();
    }

    @Override
    public void extractRenderState(SynapticCondenserBlockEntity entity, SynapticCondenserRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        state.fluidAmount = entity.getFluidAmount();
        state.maxCapacity = entity.getMaxCapacity();
        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(SynapticCondenserRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.fluidAmount > 0) {
            float fillRatio = (float) state.fluidAmount / (float) state.maxCapacity;
            float pulse = 0.85f + 0.15f * Mth.sin(state.animationTicks * 0.1f);
            int alpha = (int) (fillRatio * 200.0f + 55.0f);
            int r = (int) (120 * pulse);
            int g = (int) (245 * pulse);
            int b = (int) (30 * pulse);

            collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
                int segments = 16;
                float radius = 0.20f + fillRatio * 0.18f;
                for (int i = 0; i < segments; i++) {
                    float a1 = (i / (float) segments) * ((float) Math.PI * 2.0f);
                    float a2 = ((i + 1) / (float) segments) * ((float) Math.PI * 2.0f);

                    float x1 = 0.5f + Mth.cos(a1) * radius;
                    float z1 = 0.5f + Mth.sin(a1) * radius;
                    float x2 = 0.5f + Mth.cos(a2) * radius;
                    float z2 = 0.5f + Mth.sin(a2) * radius;

                    consumer.addVertex(pose, x1, 1.002f, z1).setColor(r, g, b, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                    consumer.addVertex(pose, x2, 1.002f, z2).setColor(r, g, b, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                }
            });

            poseStack.pushPose();
            poseStack.translate(0.5, 1.25, 0.5);
            poseStack.rotateDegrees(Axis.YP, -camera.yRot);
            poseStack.rotateDegrees(Axis.XP, camera.xRot);
            poseStack.scale(-0.015f, -0.015f, 0.015f);

            String text = state.fluidAmount + " / " + state.maxCapacity + " mB";
            int width = this.font.width(text);
            collector.submitText(poseStack, -width / 2.0f, 0.0f, Component.literal(text).getVisualOrderText(), false, Font.DisplayMode.NORMAL, 0xFF7CFC00, 0, 0xF000F0, 0);
            poseStack.popPose();
        }
    }
}

