package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.SandstoneCrateBlock;
import com.fhfelipefh.sandstorm.content.block.entity.SandstoneCrateBlockEntity;
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

public class SandstoneCrateBlockEntityRenderer implements BlockEntityRenderer<SandstoneCrateBlockEntity, SandstoneCrateRenderState> {

    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/chest/sandstone_crate.png");
    private final SandstoneCrateModel model;

    public SandstoneCrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SandstoneCrateModel(SandstoneCrateModel.createBodyLayer().bakeRoot());
    }

    @Override
    public SandstoneCrateRenderState createRenderState() {
        return new SandstoneCrateRenderState();
    }

    @Override
    public void extractRenderState(SandstoneCrateBlockEntity entity, SandstoneCrateRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(SandstoneCrateBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(SandstoneCrateBlock.FACING);
        }
        state.openness = entity.getOpenNess(partialTick);
    }

    @Override
    public void submit(SandstoneCrateRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
        poseStack.translate(-0.5, 0.0, -0.5);

        float f = state.openness;
        f = 1.0f - f;
        f = 1.0f - f * f * f;
        this.model.setupAnim(f);

        collector.submitModelPart(this.model.getRoot(), poseStack, RenderTypes.entityCutout(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();
    }
}
