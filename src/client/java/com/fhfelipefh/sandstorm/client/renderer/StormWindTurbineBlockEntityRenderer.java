package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.StormWindTurbineBlock;
import com.fhfelipefh.sandstorm.content.block.entity.StormWindTurbineBlockEntity;
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

public class StormWindTurbineBlockEntityRenderer implements BlockEntityRenderer<StormWindTurbineBlockEntity, StormWindTurbineRenderState> {
    private static final Identifier ROTOR_TEXTURE = SandStormMod.id("textures/entity/storm_wind_turbine/storm_wind_turbine_rotor.png");
    private final StormWindTurbineModel model;

    public StormWindTurbineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new StormWindTurbineModel(StormWindTurbineModel.createBodyLayer().bakeRoot());
    }

    @Override
    public StormWindTurbineRenderState createRenderState() {
        return new StormWindTurbineRenderState();
    }

    @Override
    public void extractRenderState(StormWindTurbineBlockEntity entity, StormWindTurbineRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(StormWindTurbineBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(StormWindTurbineBlock.FACING);
        }
        state.generationRate = entity.getCurrentGenerationRate();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(StormWindTurbineRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float yRot = -state.facing.toYRot();
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.translate(0.0, 0.0, -0.45);

        float spinSpeed = state.generationRate > 0 ? (float) Math.max(2.0, state.generationRate * 0.25) : 1.0f;
        float rollAngle = (state.animationTicks * spinSpeed * 4.0f) % 360.0f;
        poseStack.rotateDegrees(Axis.ZP, rollAngle);

        poseStack.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);
        collector.submitModelPart(this.model.getRoot(), poseStack, RenderTypes.entityCutout(ROTOR_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }
}
