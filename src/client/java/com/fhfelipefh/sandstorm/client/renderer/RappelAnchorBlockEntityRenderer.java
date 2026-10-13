package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.RappelAnchorBlock;
import com.fhfelipefh.sandstorm.content.block.entity.RappelAnchorBlockEntity;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RappelAnchorBlockEntityRenderer implements BlockEntityRenderer<RappelAnchorBlockEntity, RappelAnchorRenderState> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/rappel_anchor/rappel_anchor_mechanism.png");
    private final RappelAnchorModel model;

    public RappelAnchorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new RappelAnchorModel(RappelAnchorModel.createBodyLayer().bakeRoot());
    }

    @Override
    public RappelAnchorRenderState createRenderState() {
        return new RappelAnchorRenderState();
    }

    @Override
    public void extractRenderState(RappelAnchorBlockEntity entity, RappelAnchorRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(RappelAnchorBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(RappelAnchorBlock.FACING);
        }
        state.state = entity.getState();
        state.deployedDepth = entity.getDeployedDepth();
        state.spoolAngle = entity.getSpoolAngle();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(RappelAnchorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        float yRot = -state.facing.toYRot();
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);

        collector.submitModelPart(this.model.getBase(), poseStack, RenderTypes.entityCutout(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        poseStack.pushPose();
        poseStack.translate(0.0f, 4.5f, 0.0f);
        poseStack.rotateDegrees(Axis.XP, state.spoolAngle);
        poseStack.translate(0.0f, -4.5f, 0.0f);
        collector.submitModelPart(this.model.getSpool(), poseStack, RenderTypes.entityCutout(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();

        poseStack.popPose();
    }
}
