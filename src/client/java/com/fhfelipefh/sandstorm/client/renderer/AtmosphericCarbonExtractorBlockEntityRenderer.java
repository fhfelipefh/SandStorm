package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.AtmosphericCarbonExtractorBlock;
import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericCarbonExtractorBlockEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AtmosphericCarbonExtractorBlockEntityRenderer implements BlockEntityRenderer<AtmosphericCarbonExtractorBlockEntity, AtmosphericCarbonExtractorRenderState> {
    private static final Identifier FAN_TEXTURE = SandStormMod.id("textures/entity/atmospheric_carbon_extractor/extractor_fan.png");
    private final AtmosphericCarbonExtractorFanModel model;

    public AtmosphericCarbonExtractorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new AtmosphericCarbonExtractorFanModel(AtmosphericCarbonExtractorFanModel.createBodyLayer().bakeRoot());
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
        state.isDiamondMode = entity.isDiamondMode();
        state.combustionBoost = entity.getCombustionBoost();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(AtmosphericCarbonExtractorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.0 - (1.2 / 16.0), 0.5);

        float fanSpeed = state.isProcessing ? (12.0f + state.combustionBoost * 8.0f) : 1.2f;
        float fanAngle = (state.animationTicks * fanSpeed) % 360.0f;
        poseStack.rotateDegrees(Axis.YP, fanAngle);

        poseStack.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);
        collector.submitModelPart(this.model.getRoot(), poseStack, RenderTypes.entityCutoutCull(FAN_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();

        if (state.combustionBoost > 0 || state.isDiamondMode) {
            collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
                if (state.combustionBoost > 0) {
                    float heatPulse = (Mth.sin(state.animationTicks * 0.4f) + 1.0f) * 0.5f;
                    int heatAlpha = 140 + (int) (heatPulse * 90.0f);
                    consumer.addVertex(pose, 0.2f, 0.1f, 0.5f).setColor(255, 87, 34, heatAlpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                    consumer.addVertex(pose, 0.8f, 0.1f, 0.5f).setColor(255, 171, 0, heatAlpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                }

                if (state.isDiamondMode) {
                    float pulse = (Mth.sin(state.animationTicks * 0.3f) + 1.0f) * 0.5f;
                    int alpha = 180 + (int) (pulse * 75.0f);
                    consumer.addVertex(pose, 0.5f, 0.7f, 0.5f).setColor(0, 229, 255, alpha).setNormal(pose, 0.0f, -1.0f, 0.0f).setLineWidth(2.5f);
                    consumer.addVertex(pose, 0.5f, 0.35f, 0.5f).setColor(224, 247, 250, alpha).setNormal(pose, 0.0f, -1.0f, 0.0f).setLineWidth(2.5f);

                    consumer.addVertex(pose, 0.42f, 0.35f, 0.42f).setColor(0, 229, 255, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                    consumer.addVertex(pose, 0.58f, 0.35f, 0.58f).setColor(0, 229, 255, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                }
            });
        }
    }
}
