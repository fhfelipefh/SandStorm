package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.VaultDoorControllerBlock;
import com.fhfelipefh.sandstorm.content.block.entity.VaultDoorControllerBlockEntity;
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
import net.minecraft.world.phys.Vec3;

public class VaultDoorControllerBlockEntityRenderer implements BlockEntityRenderer<VaultDoorControllerBlockEntity, VaultDoorRenderState> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/block/vault_door.png");
    private final VaultDoorModel model;

    public VaultDoorControllerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new VaultDoorModel(VaultDoorModel.createBodyLayer().bakeRoot());
    }

    @Override
    public VaultDoorRenderState createRenderState() {
        return new VaultDoorRenderState();
    }

    @Override
    public void extractRenderState(VaultDoorControllerBlockEntity entity, VaultDoorRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(VaultDoorControllerBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(VaultDoorControllerBlock.FACING);
        }
        state.open = entity.getBlockState().getValue(VaultDoorControllerBlock.OPEN);
        state.progress = entity.getProgress(partialTick);
    }

    @Override
    public void submit(VaultDoorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.rotateDegrees(Axis.YP, 180.0f - state.facing.toYRot());

        float p = state.progress;
        if (p < 0.333f) {
            float phase1 = Mth.clamp(p / 0.333f, 0.0f, 1.0f);
            this.model.getWheel().zRot = (float) Math.toRadians(phase1 * 360.0f);
            float retractProgress = Mth.clamp((phase1 - 0.5f) / 0.5f, 0.0f, 1.0f);
            float barScale = 1.0f - (retractProgress * 0.2f);
            this.model.getLockingBars().xScale = barScale;
            this.model.getLockingBars().yScale = barScale;
            this.model.getDoorBody().yRot = 0.0f;
        } else {
            float phase2 = Mth.clamp((p - 0.333f) / 0.667f, 0.0f, 1.0f);
            this.model.getWheel().zRot = (float) Math.toRadians(360.0f);
            this.model.getLockingBars().xScale = 0.8f;
            this.model.getLockingBars().yScale = 0.8f;
            float eased = phase2 < 0.5f ? 4.0f * phase2 * phase2 * phase2 : 1.0f - (float) Math.pow(-2.0f * phase2 + 2.0f, 3) / 2.0f;
            this.model.getDoorBody().yRot = -(float) Math.toRadians(eased * 95.0f);
        }

        int light = state.lightCoords != 0 ? state.lightCoords : 15728880;
        collector.submitModelPart(this.model.getRoot(), poseStack, RenderTypes.entityCutout(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }
}
