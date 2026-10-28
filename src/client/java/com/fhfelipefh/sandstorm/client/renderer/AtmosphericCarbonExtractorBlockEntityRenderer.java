package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.AtmosphericCarbonExtractorBlock;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericCarbonExtractorBlockEntity;
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

public class AtmosphericCarbonExtractorBlockEntityRenderer implements BlockEntityRenderer<AtmosphericCarbonExtractorBlockEntity, AtmosphericCarbonExtractorRenderState> {
    public AtmosphericCarbonExtractorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public AtmosphericCarbonExtractorRenderState createRenderState() {
        return new AtmosphericCarbonExtractorRenderState();
    }

    @Override
    public void extractRenderState(AtmosphericCarbonExtractorBlockEntity entity, AtmosphericCarbonExtractorRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(AtmosphericCarbonExtractorBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(AtmosphericCarbonExtractorBlock.FACING);
        }
        state.isProcessing = entity.isProcessing();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(AtmosphericCarbonExtractorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (!state.isProcessing) {
            return;
        }

        float fanAngle = state.animationTicks * 0.25f;
        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            for (int blade = 0; blade < 4; blade++) {
                float angle = fanAngle + blade * ((float) Math.PI / 2.0f);
                float x1 = 0.5f;
                float z1 = 0.5f;
                float x2 = 0.5f + Mth.cos(angle) * 0.32f;
                float z2 = 0.5f + Mth.sin(angle) * 0.32f;
                consumer.addVertex(pose, x1, 1.003f, z1).setColor(0, 229, 255, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
                consumer.addVertex(pose, x2, 1.003f, z2).setColor(128, 222, 234, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
            }

            float pulse = (Mth.sin(state.animationTicks * 0.2f) + 1.0f) * 0.5f;
            int glowAlpha = 150 + (int) (pulse * 105.0f);
            consumer.addVertex(pose, 0.35f, 0.45f, 0.5f).setColor(255, 87, 34, glowAlpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
            consumer.addVertex(pose, 0.65f, 0.45f, 0.5f).setColor(255, 112, 67, glowAlpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        });
    }
}
