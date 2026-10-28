package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.DeepCoreDrillBlock;
import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreDrillBlockEntity;
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

public class DeepCoreDrillBlockEntityRenderer implements BlockEntityRenderer<DeepCoreDrillBlockEntity, DeepCoreDrillRenderState> {

    public DeepCoreDrillBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public DeepCoreDrillRenderState createRenderState() {
        return new DeepCoreDrillRenderState();
    }

    @Override
    public void extractRenderState(DeepCoreDrillBlockEntity entity, DeepCoreDrillRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(DeepCoreDrillBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(DeepCoreDrillBlock.FACING);
        }
        state.isDrilling = entity.isProcessing();
        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(DeepCoreDrillRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.isDrilling) {
            return;
        }

        float drillRot = state.animationTicks * 0.45f;
        float pistonY = Mth.sin(state.animationTicks * 0.35f) * 0.04f;

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            for (int i = 0; i < 4; i++) {
                float angle = drillRot + (i * ((float) Math.PI / 2.0f));
                float rTop = 0.22f;
                float rBottom = 0.08f;

                float x1 = 0.5f + Mth.cos(angle) * rTop;
                float z1 = 0.5f + Mth.sin(angle) * rTop;
                float y1 = 0.85f + pistonY;

                float x2 = 0.5f + Mth.cos(angle + 0.8f) * rBottom;
                float z2 = 0.5f + Mth.sin(angle + 0.8f) * rBottom;
                float y2 = 0.15f + pistonY;

                consumer.addVertex(pose, x1, y1, z1).setColor(240, 180, 50, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.8f);
                consumer.addVertex(pose, x2, y2, z2).setColor(255, 100, 20, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.8f);

                consumer.addVertex(pose, x2, y2, z2).setColor(255, 200, 50, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.8f);
                consumer.addVertex(pose, 0.5f, -0.05f + pistonY, 0.5f).setColor(255, 255, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.8f);
            }
        });
    }
}

