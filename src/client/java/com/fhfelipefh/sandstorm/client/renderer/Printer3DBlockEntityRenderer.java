package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.Printer3DBlock;
import com.fhfelipefh.sandstorm.content.block.entity.Printer3DBlockEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Printer3DBlockEntityRenderer implements BlockEntityRenderer<Printer3DBlockEntity, Printer3DRenderState> {
    private static final Identifier MECHANISM_TEXTURE = SandStormMod.id("textures/block/printer_3d_casing.png");
    private final ItemModelResolver itemModelResolver;
    private final Font font;
    private final Printer3DMechanismsModel model;

    public Printer3DBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.font = context.font();
        this.model = new Printer3DMechanismsModel(Printer3DMechanismsModel.createBodyLayer().bakeRoot());
    }

    @Override
    public Printer3DRenderState createRenderState() {
        return new Printer3DRenderState();
    }

    @Override
    public void extractRenderState(Printer3DBlockEntity entity, Printer3DRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(Printer3DBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(Printer3DBlock.FACING);
        }
        state.progress = entity.getProgress();
        state.maxProgress = entity.getMaxProgress();
        state.isProcessing = entity.isProcessing();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;

        ItemStack printItem = entity.getPrintingItem();
        if (!printItem.isEmpty() && level != null) {
            state.hasItem = true;
            this.itemModelResolver.updateForTopItem(state.itemRenderState, printItem, ItemDisplayContext.FIXED, level, null, 0);
        } else {
            state.hasItem = false;
            state.itemRenderState.clear();
        }
    }

    @Override
    public void submit(Printer3DRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float yRot = -state.facing.toYRot();
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.translate(-0.5, -0.5, -0.5);

        float progressRatio = state.maxProgress > 0 ? (float) state.progress / (float) state.maxProgress : 0.0f;
        float toolX;
        float toolY;
        float toolZ;

        if (state.isProcessing) {
            toolY = 4.0f + (progressRatio * 7.5f);
            toolX = 8.0f + Mth.sin(state.animationTicks * 0.35f) * 2.2f;
            toolZ = 8.0f + Mth.cos(state.animationTicks * 0.55f) * 2.2f;
        } else if (state.hasItem) {
            toolY = 12.0f;
            toolX = 4.5f;
            toolZ = 4.5f;
        } else {
            toolY = 12.5f;
            toolX = 4.0f;
            toolZ = 4.0f;
        }

        poseStack.pushPose();
        poseStack.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);

        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), (pose, consumer) -> {
            this.model.getZRods().render(poseStack, consumer, state.lightCoords, OverlayTexture.NO_OVERLAY);

            poseStack.pushPose();
            poseStack.translate(8.0f, toolY, toolZ);
            this.model.getGantryRail().render(poseStack, consumer, state.lightCoords, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(toolX, toolY, toolZ);
            this.model.getToolhead().render(poseStack, consumer, state.lightCoords, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        });

        if (state.isProcessing) {
            float bedSurfaceY = 2.8f;
            float itemHeight = 0.5f + (progressRatio * 3.5f);
            float targetLaserY = bedSurfaceY + itemHeight;
            float nozzleTipY = toolY + 3.5f;

            collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
                consumer.addVertex(pose, toolX, nozzleTipY, toolZ).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);
                consumer.addVertex(pose, toolX, targetLaserY, toolZ).setColor(0x80, 0xF3, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);

                consumer.addVertex(pose, toolX - 0.5f, targetLaserY, toolZ).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 1.0f, 0.0f, 0.0f);
                consumer.addVertex(pose, toolX + 0.5f, targetLaserY, toolZ).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 1.0f, 0.0f, 0.0f);

                consumer.addVertex(pose, toolX, targetLaserY, toolZ - 0.5f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 0.0f, 1.0f);
                consumer.addVertex(pose, toolX, targetLaserY, toolZ + 0.5f).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 0.0f, 1.0f);
            });
        }
        poseStack.popPose();

        if (state.hasItem) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.18, 0.5);
            poseStack.rotateDegrees(Axis.XP, 90.0f);
            if (state.isProcessing) {
                float itemScaleY = 0.06f + (progressRatio * 0.34f);
                poseStack.scale(0.40f, 0.40f, itemScaleY);
            } else {
                poseStack.scale(0.40f, 0.40f, 0.40f);
            }
            state.itemRenderState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.13, -0.05);
        poseStack.scale(0.007f, -0.007f, 0.007f);

        String displayText;
        int displayColor;
        if (state.isProcessing) {
            int pct = Math.min(100, (int) (progressRatio * 100.0f));
            displayText = "PRINTING " + pct + "%";
            displayColor = 0xFF00E5FF;
        } else if (state.hasItem) {
            displayText = "COMPLETE";
            displayColor = 0xFF00E676;
        } else {
            displayText = "STANDBY";
            displayColor = 0xFF78909C;
        }

        int textWidth = this.font.width(displayText);
        collector.submitText(poseStack, -textWidth / 2.0f, 0.0f, Component.literal(displayText).getVisualOrderText(), false, Font.DisplayMode.NORMAL, displayColor, 0, 0xF000F0, 0);
        poseStack.popPose();

        poseStack.popPose();
    }
}
