package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.entity.XpVacuumBlockEntity;
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

public class XpVacuumBlockEntityRenderer implements BlockEntityRenderer<XpVacuumBlockEntity, XpVacuumRenderState> {

    public XpVacuumBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public XpVacuumRenderState createRenderState() {
        return new XpVacuumRenderState();
    }

    @Override
    public void extractRenderState(XpVacuumBlockEntity entity, XpVacuumRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        state.fluidAmount = entity.getFluidAmount();
        state.maxCapacity = entity.getMaxCapacity();
        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(XpVacuumRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float rotAngle = state.animationTicks * 0.12f;

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            for (int arm = 0; arm < 4; arm++) {
                float armOffset = arm * ((float) Math.PI / 2.0f);
                for (int seg = 0; seg < 6; seg++) {
                    float r1 = 0.10f + seg * 0.05f;
                    float r2 = 0.10f + (seg + 1) * 0.05f;
                    float theta1 = rotAngle + armOffset + seg * 0.35f;
                    float theta2 = rotAngle + armOffset + (seg + 1) * 0.35f;

                    float x1 = 0.5f + Mth.cos(theta1) * r1;
                    float z1 = 0.5f + Mth.sin(theta1) * r1;
                    float y1 = 1.002f - (seg * 0.012f);

                    float x2 = 0.5f + Mth.cos(theta2) * r2;
                    float z2 = 0.5f + Mth.sin(theta2) * r2;
                    float y2 = 1.002f - ((seg + 1) * 0.012f);

                    consumer.addVertex(pose, x1, y1, z1).setColor(120, 245, 30, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.2f);
                    consumer.addVertex(pose, x2, y2, z2).setColor(220, 255, 90, 240).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.2f);
                }
            }
        });
    }
}

