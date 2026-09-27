package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.DesalinationFilterBlock;
import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DesalinationFilterBlockEntityRenderer implements BlockEntityRenderer<DesalinationFilterBlockEntity, DesalinationFilterRenderState> {
    private static final Identifier MECHANISM_TEXTURE = SandStormMod.id("textures/entity/desalination_filter/desalination_filter_mechanisms.png");
    private final Font font;
    private final DesalinationFilterMechanismsModel model;

    public DesalinationFilterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
        this.model = new DesalinationFilterMechanismsModel(DesalinationFilterMechanismsModel.createBodyLayer().bakeRoot());
    }

    @Override
    public DesalinationFilterRenderState createRenderState() {
        return new DesalinationFilterRenderState();
    }

    @Override
    public void extractRenderState(DesalinationFilterBlockEntity entity, DesalinationFilterRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(DesalinationFilterBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(DesalinationFilterBlock.FACING);
        }
        state.progress = entity.getProgress();
        state.maxProgress = entity.getMaxProgress();
        state.isProcessing = entity.isProcessing();
        state.waterInput = entity.getWaterInput();
        state.waterOutput = entity.getWaterOutput();
        state.maxWater = entity.getMaxWater();
        state.hasCartridge = entity.hasFilterCartridge();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(DesalinationFilterRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float yRot = -state.facing.toYRot();
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.translate(-0.5, -0.5, -0.5);

        float progressRatio = state.maxProgress > 0 ? (float) state.progress / (float) state.maxProgress : 0.0f;

        poseStack.pushPose();
        poseStack.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);
        poseStack.translate(8.0f, 0.0f, 8.0f);

        collector.submitModelPart(this.model.getSpiralCoil(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(this.model.getLeftTank(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(this.model.getRightTank(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(this.model.getFunnel(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(this.model.getPipes(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            if (state.waterInput > 0) {
                float inRatio = Math.min(1.0f, (float) state.waterInput / (float) state.maxWater);
                float inWaterY = 2.1f + (inRatio * 4.6f);

                consumer.addVertex(pose, -5.4f, inWaterY, -2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, -1.6f, inWaterY, -2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, -1.6f, inWaterY, -2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, -1.6f, inWaterY, 2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, -1.6f, inWaterY, 2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, -5.4f, inWaterY, 2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, -5.4f, inWaterY, 2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, -5.4f, inWaterY, -2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, -5.4f, inWaterY, 0.0f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, -1.6f, inWaterY, 0.0f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, -3.5f, inWaterY, -2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, -3.5f, inWaterY, 2.9f).setColor(0x29, 0x80, 0xB9, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
            }

            if (state.waterOutput > 0) {
                float outRatio = Math.min(1.0f, (float) state.waterOutput / (float) state.maxWater);
                float outWaterY = 2.1f + (outRatio * 3.6f);

                consumer.addVertex(pose, 1.6f, outWaterY, -2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, 5.4f, outWaterY, -2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, 5.4f, outWaterY, -2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, 5.4f, outWaterY, 2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, 5.4f, outWaterY, 2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, 1.6f, outWaterY, 2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, 1.6f, outWaterY, 2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, 1.6f, outWaterY, -2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, 1.6f, outWaterY, 0.0f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, 5.4f, outWaterY, 0.0f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, 3.5f, outWaterY, -2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, 3.5f, outWaterY, 2.9f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
            }

            if (state.isProcessing) {
                float drop1Y = 10.0f - ((state.animationTicks * 0.3f) % 6.0f);
                float angle = state.animationTicks * 0.4f;
                float drop1X = Mth.cos(angle) * 1.6f;
                float drop1Z = Mth.sin(angle) * 1.6f;
                consumer.addVertex(pose, drop1X, drop1Y, drop1Z).setColor(0x80, 0xF3, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, drop1X, drop1Y - 0.5f, drop1Z).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                float drop2Y = 6.5f - ((state.animationTicks * 0.25f + 2.0f) % 3.5f);
                float drop2X = 3.5f;
                float drop2Z = 0.0f;
                consumer.addVertex(pose, drop2X, drop2Y, drop2Z).setColor(0x80, 0xF3, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, drop2X, drop2Y - 0.4f, drop2Z).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

                consumer.addVertex(pose, 0.0f, 3.5f, 0.0f).setColor(0x00, 0xE5, 0xFF, 0x80).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, 0.0f, 11.5f, 0.0f).setColor(0x80, 0xF3, 0xFF, 0x80).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
            }
        });

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5, 0.13, -0.05);
        poseStack.scale(0.007f, -0.007f, 0.007f);

        String displayText;
        int displayColor;
        if (state.isProcessing) {
            int pct = Math.min(100, (int) (progressRatio * 100.0f));
            displayText = "DESALINATING " + pct + "%";
            displayColor = 0xFF00E5FF;
        } else if (state.waterOutput >= state.maxWater) {
            displayText = "OUTPUT FULL";
            displayColor = 0xFFFFB74D;
        } else if (state.waterInput > 0) {
            displayText = "STANDBY (" + (state.waterInput / 1000) + "B)";
            displayColor = 0xFF00E676;
        } else {
            displayText = "NO WATER";
            displayColor = 0xFF78909C;
        }

        int textWidth = this.font.width(displayText);
        collector.submitText(poseStack, -textWidth / 2.0f, 0.0f, Component.literal(displayText).getVisualOrderText(), false, Font.DisplayMode.NORMAL, displayColor, 0, 0xF000F0, 0);
        poseStack.popPose();

        poseStack.popPose();
    }
}
