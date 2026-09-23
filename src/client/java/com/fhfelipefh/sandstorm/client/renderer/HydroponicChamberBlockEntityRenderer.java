package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.HydroponicChamberBlock;
import com.fhfelipefh.sandstorm.content.block.entity.HydroponicChamberBlockEntity;
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

public class HydroponicChamberBlockEntityRenderer implements BlockEntityRenderer<HydroponicChamberBlockEntity, HydroponicChamberRenderState> {
    private static final Identifier MECHANISM_TEXTURE = SandStormMod.id("textures/entity/hydroponic_chamber/hydroponic_chamber_mechanisms.png");
    private final Font font;
    private final HydroponicChamberModel model;

    public HydroponicChamberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
        this.model = new HydroponicChamberModel(HydroponicChamberModel.createBodyLayer().bakeRoot());
    }

    @Override
    public HydroponicChamberRenderState createRenderState() {
        return new HydroponicChamberRenderState();
    }

    @Override
    public void extractRenderState(HydroponicChamberBlockEntity entity, HydroponicChamberRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(HydroponicChamberBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(HydroponicChamberBlock.FACING);
        }
        state.progress = entity.getProgress();
        state.maxProgress = entity.getMaxProgress();
        state.isProcessing = entity.isProcessing();
        state.insideDome = entity.isInsideDome();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(HydroponicChamberRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float yRot = -state.facing.toYRot();
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.translate(-0.5, -0.5, -0.5);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);

        collector.submitModelPart(this.model.getNutrientTray(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(this.model.getUvLamp(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        if (state.isProcessing) {
            float progressRatio = state.maxProgress > 0 ? (float) state.progress / (float) state.maxProgress : 0.0f;
            float growScale = 0.4f + (progressRatio * 0.7f);
            float sway = Mth.sin(state.animationTicks * 0.1f) * 2.0f;

            poseStack.pushPose();
            poseStack.rotateDegrees(Axis.YP, sway);
            poseStack.scale(growScale, growScale, growScale);
            collector.submitModelPart(this.model.getSeedling(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
            poseStack.popPose();
        }

        poseStack.popPose();

        if (state.insideDome) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.15, 0.5);
            poseStack.rotateDegrees(Axis.YP, yRot + 180.0f);
            poseStack.scale(0.012f, -0.012f, 0.012f);

            Component text = Component.literal("DOME 3X SPEED");
            int textW = this.font.width(text);
            collector.submitText(poseStack, -textW / 2.0f, 0.0f, text.getVisualOrderText(), false, Font.DisplayMode.NORMAL, 0xFF69F0AE, 0, 0, state.lightCoords);
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
