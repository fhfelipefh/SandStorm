package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.CrushingSpikeGateBlock;
import com.fhfelipefh.sandstorm.content.block.entity.CrushingSpikeGateBlockEntity;
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
import net.minecraft.world.phys.Vec3;

public class CrushingSpikeGateRenderer implements BlockEntityRenderer<CrushingSpikeGateBlockEntity, CrushingSpikeGateRenderState> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/block/crushing_spike_gate.png");
    private final CrushingSpikeGateModel model;

    public CrushingSpikeGateRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new CrushingSpikeGateModel(CrushingSpikeGateModel.createSpikesLayer().bakeRoot());
    }

    @Override
    public CrushingSpikeGateRenderState createRenderState() {
        return new CrushingSpikeGateRenderState();
    }

    @Override
    public void extractRenderState(CrushingSpikeGateBlockEntity entity, CrushingSpikeGateRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        state.progress = entity.getProgress(partialTick);
        if (entity.getBlockState().hasProperty(CrushingSpikeGateBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(CrushingSpikeGateBlock.FACING);
        }
    }

    @Override
    public void submit(CrushingSpikeGateRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        float translateY = (1.0f - state.progress) * (12.0f / 16.0f); // Move up by 12 pixels when open (progress=0), 0 when closed (progress=1)
        
        poseStack.translate(0.5, translateY, 0.5);
        poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
        poseStack.translate(-0.5, 0, -0.5);
        collector.submitModelPart(this.model.getRoot(), poseStack, RenderTypes.entityCutout(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }
}
