package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.NaniteFabricatorBlock;
import com.fhfelipefh.sandstorm.content.block.entity.NaniteFabricatorBlockEntity;
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

public class NaniteFabricatorBlockEntityRenderer implements BlockEntityRenderer<NaniteFabricatorBlockEntity, NaniteFabricatorRenderState> {
    private static final Identifier MECHANISM_TEXTURE = SandStormMod.id("textures/entity/nanite_fabricator/nanite_fabricator_mechanisms.png");
    private final ItemModelResolver itemModelResolver;
    private final Font font;
    private final NaniteFabricatorMechanismsModel model;

    public NaniteFabricatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.font = context.font();
        this.model = new NaniteFabricatorMechanismsModel(NaniteFabricatorMechanismsModel.createBodyLayer().bakeRoot());
    }

    @Override
    public NaniteFabricatorRenderState createRenderState() {
        return new NaniteFabricatorRenderState();
    }

    @Override
    public void extractRenderState(NaniteFabricatorBlockEntity entity, NaniteFabricatorRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(NaniteFabricatorBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(NaniteFabricatorBlock.FACING);
        }
        state.progress = entity.getProgress();
        state.maxProgress = entity.getMaxProgress();
        state.isProcessing = entity.isProcessing();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;

        ItemStack fabricItem = entity.getFabricatingItem();
        if (!fabricItem.isEmpty() && level != null) {
            state.hasItem = true;
            this.itemModelResolver.updateForTopItem(state.itemRenderState, fabricItem, ItemDisplayContext.FIXED, level, null, 0);
        } else {
            state.hasItem = false;
            state.itemRenderState.clear();
        }
    }

    @Override
    public void submit(NaniteFabricatorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float yRot = -state.facing.toYRot();
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.translate(-0.5, -0.5, -0.5);

        float progressRatio = state.maxProgress > 0 ? (float) state.progress / (float) state.maxProgress : 0.0f;

        poseStack.pushPose();
        poseStack.scale(1.0f / 16.0f, 1.0f / 16.0f, 1.0f / 16.0f);

        poseStack.pushPose();
        poseStack.translate(8.0f, 14.0f, 8.0f);
        collector.submitModelPart(this.model.getMount(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        float armAngle = state.isProcessing ? Mth.sin(state.animationTicks * 0.25f) * 15.0f : 0.0f;
        float armExtend = state.isProcessing ? Mth.cos(state.animationTicks * 0.20f) * 1.5f : 0.0f;
        poseStack.translate(0.0f, armExtend, 0.0f);
        poseStack.rotateDegrees(Axis.XP, armAngle);
        collector.submitModelPart(this.model.getManipulatorArm(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();

        float bot1X;
        float bot1Y;
        float bot1Z;
        float bot2X;
        float bot2Y;
        float bot2Z;
        float bot3X;
        float bot3Y;
        float bot3Z;

        if (state.isProcessing) {
            float angle1 = state.animationTicks * 0.16f;
            bot1X = 8.0f + Mth.cos(angle1) * 3.2f;
            bot1Z = 8.0f + Mth.sin(angle1) * 3.2f;
            bot1Y = 5.2f + Mth.sin(state.animationTicks * 0.30f) * 0.8f;

            float angle2 = state.animationTicks * 0.20f + 2.1f;
            bot2X = 8.0f + Mth.cos(angle2) * 2.8f;
            bot2Z = 8.0f + Mth.sin(angle2) * 2.8f;
            bot2Y = 6.0f + Mth.cos(state.animationTicks * 0.25f) * 0.7f;

            float angle3 = state.animationTicks * 0.14f + 4.2f;
            bot3X = 8.0f + Mth.cos(angle3) * 3.5f;
            bot3Z = 8.0f + Mth.sin(angle3) * 3.5f;
            bot3Y = 4.8f + Mth.sin(state.animationTicks * 0.35f) * 0.6f;
        } else {
            bot1X = 5.5f;
            bot1Y = 3.6f;
            bot1Z = 5.5f;

            bot2X = 10.5f;
            bot2Y = 3.6f;
            bot2Z = 5.5f;

            bot3X = 8.0f;
            bot3Y = 3.6f;
            bot3Z = 10.5f;
        }

        renderBot(bot1X, bot1Y, bot1Z, poseStack, collector, state.lightCoords);
        renderBot(bot2X, bot2Y, bot2Z, poseStack, collector, state.lightCoords);
        renderBot(bot3X, bot3Y, bot3Z, poseStack, collector, state.lightCoords);

        if (state.isProcessing) {
            float focalX = 8.0f;
            float focalY = 3.8f + (progressRatio * 1.5f);
            float focalZ = 8.0f;

            collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
                consumer.addVertex(pose, bot1X, bot1Y, bot1Z).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);
                consumer.addVertex(pose, focalX, focalY, focalZ).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);

                consumer.addVertex(pose, bot2X, bot2Y, bot2Z).setColor(0xE0, 0x40, 0xFB, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);
                consumer.addVertex(pose, focalX, focalY, focalZ).setColor(0xE0, 0x40, 0xFB, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);

                consumer.addVertex(pose, bot3X, bot3Y, bot3Z).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);
                consumer.addVertex(pose, focalX, focalY, focalZ).setColor(0x80, 0xF3, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);

                float armTipY = 14.0f - 7.0f + armExtend;
                consumer.addVertex(pose, 8.0f, armTipY, 8.0f).setColor(0xE0, 0x40, 0xFB, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);
                consumer.addVertex(pose, focalX, focalY, focalZ).setColor(0x00, 0xE5, 0xFF, 0xFF).setNormal(pose, 0.0f, 1.0f, 0.0f);
            });
        }
        poseStack.popPose();

        if (state.hasItem) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.22, 0.5);
            if (state.isProcessing) {
                poseStack.rotateDegrees(Axis.YP, state.animationTicks * 2.0f);
                float itemScale = 0.08f + (progressRatio * 0.32f);
                poseStack.scale(itemScale, itemScale, itemScale);
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
            displayText = "SYNTHESIS " + pct + "%";
            displayColor = 0xFF00E5FF;
        } else if (state.hasItem) {
            displayText = "READY";
            displayColor = 0xFF00E676;
        } else {
            displayText = "ONLINE";
            displayColor = 0xFF78909C;
        }

        int textWidth = this.font.width(displayText);
        collector.submitText(poseStack, -textWidth / 2.0f, 0.0f, Component.literal(displayText).getVisualOrderText(), false, Font.DisplayMode.NORMAL, displayColor, 0, 0xF000F0, 0);
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderBot(float x, float y, float z, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        collector.submitModelPart(this.model.getNanobot(), poseStack, RenderTypes.entityCutout(MECHANISM_TEXTURE), lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();
    }
}
