package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.AutonomousSonicTurretBlock;
import com.fhfelipefh.sandstorm.content.block.entity.AutonomousSonicTurretBlockEntity;
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

public class AutonomousSonicTurretRenderer implements BlockEntityRenderer<AutonomousSonicTurretBlockEntity, AutonomousSonicTurretRenderState> {
    private static final Identifier MECHANISM_TEXTURE = SandStormMod.id("textures/entity/autonomous_sonic_turret/autonomous_sonic_turret_mechanisms.png");
    private final AutonomousSonicTurretModel model;

    public AutonomousSonicTurretRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new AutonomousSonicTurretModel(AutonomousSonicTurretModel.createBodyLayer().bakeRoot());
    }

    @Override
    public AutonomousSonicTurretRenderState createRenderState() {
        return new AutonomousSonicTurretRenderState();
    }

    @Override
    public void extractRenderState(AutonomousSonicTurretBlockEntity entity, AutonomousSonicTurretRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(AutonomousSonicTurretBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(AutonomousSonicTurretBlock.FACING);
        }

        float prevYaw = entity.getPrevYaw();
        float currentYaw = entity.getCurrentYaw();
        state.yaw = prevYaw + Mth.wrapDegrees(currentYaw - prevYaw) * partialTick;

        float prevPitch = entity.getPrevPitch();
        float currentPitch = entity.getCurrentPitch();
        state.pitch = Mth.lerp(partialTick, prevPitch, currentPitch);

        state.hasTarget = entity.hasTarget();
        state.shootFlashTicks = entity.getShootFlashTicks();
        state.energy = entity.getEnergy();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(AutonomousSonicTurretRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.375, 0.5);
        poseStack.rotateDegrees(Axis.YP, -state.yaw + 180.0f);
        poseStack.rotateDegrees(Axis.XP, -state.pitch);

        poseStack.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);

        collector.submitModelPart(this.model.getHead(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        if (state.shootFlashTicks > 0) {
            float flashScale = state.shootFlashTicks / 5.0f;
            renderMuzzleFlash(poseStack, collector, flashScale);
        }

        poseStack.popPose();
    }

    private void renderMuzzleFlash(PoseStack poseStack, SubmitNodeCollector collector, float flashScale) {
        int alpha = (int) (255 * flashScale);
        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            float dist = flashScale * 3.5f;

            consumer.addVertex(pose, -3.5f, 3.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, -1.5f, 3.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);

            consumer.addVertex(pose, -2.5f, 2.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, -2.5f, 4.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

            consumer.addVertex(pose, -2.5f, 3.5f, -10.0f).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, -2.5f, 3.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.0f);

            consumer.addVertex(pose, 1.5f, 3.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, 3.5f, 3.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);

            consumer.addVertex(pose, 2.5f, 2.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, 2.5f, 4.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

            consumer.addVertex(pose, 2.5f, 3.5f, -10.0f).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, 2.5f, 3.5f, -10.0f - dist).setColor(0x00, 0xE5, 0xFF, alpha).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(2.0f);
        });
    }
}
