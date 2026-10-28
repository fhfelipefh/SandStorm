package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.entity.SynapticRepairerBlockEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SynapticRepairerBlockEntityRenderer implements BlockEntityRenderer<SynapticRepairerBlockEntity, SynapticRepairerRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final Font font;

    public SynapticRepairerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.font = context.font();
    }

    @Override
    public SynapticRepairerRenderState createRenderState() {
        return new SynapticRepairerRenderState();
    }

    @Override
    public void extractRenderState(SynapticRepairerBlockEntity entity, SynapticRepairerRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        ItemStack stored = entity.getStoredItem();
        Level level = entity.getLevel();
        state.hasItem = !stored.isEmpty();
        if (state.hasItem) {
            this.itemModelResolver.updateForTopItem(state.itemRenderState, stored, ItemDisplayContext.GROUND, level, null, 0);
            state.damageValue = stored.getDamageValue();
            state.maxDamage = stored.getMaxDamage();
            state.isRepairing = stored.isDamaged() && entity.getFluidAmount() >= 2;
        } else {
            state.isRepairing = false;
            state.damageValue = 0;
            state.maxDamage = 0;
        }
        state.fluidAmount = entity.getFluidAmount();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(SynapticRepairerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.hasItem) {
            return;
        }

        float bobY = 0.82f + Mth.sin(state.animationTicks * 0.12f) * 0.04f;

        poseStack.pushPose();
        poseStack.translate(0.5, bobY, 0.5);
        poseStack.rotateDegrees(Axis.YP, state.animationTicks * 2.5f);
        poseStack.scale(0.55f, 0.55f, 0.55f);
        state.itemRenderState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        if (state.isRepairing) {
            collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
                consumer.addVertex(pose, 0.25f, 0.95f, 0.25f).setColor(124, 252, 0, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                consumer.addVertex(pose, 0.5f, bobY, 0.5f).setColor(200, 255, 60, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

                consumer.addVertex(pose, 0.75f, 0.95f, 0.25f).setColor(124, 252, 0, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                consumer.addVertex(pose, 0.5f, bobY, 0.5f).setColor(200, 255, 60, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

                consumer.addVertex(pose, 0.25f, 0.95f, 0.75f).setColor(124, 252, 0, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                consumer.addVertex(pose, 0.5f, bobY, 0.5f).setColor(200, 255, 60, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

                consumer.addVertex(pose, 0.75f, 0.95f, 0.75f).setColor(124, 252, 0, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
                consumer.addVertex(pose, 0.5f, bobY, 0.5f).setColor(200, 255, 60, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
            });

            poseStack.pushPose();
            poseStack.translate(0.5, 1.25, 0.5);
            poseStack.rotateDegrees(Axis.YP, -camera.yRot);
            poseStack.rotateDegrees(Axis.XP, camera.xRot);
            poseStack.scale(-0.015f, -0.015f, 0.015f);

            int healthPct = state.maxDamage > 0 ? (int) (((state.maxDamage - state.damageValue) / (float) state.maxDamage) * 100.0f) : 100;
            String text = "REPAIR " + healthPct + "%";
            int width = this.font.width(text);
            collector.submitText(poseStack, -width / 2.0f, 0.0f, Component.literal(text).getVisualOrderText(), false, Font.DisplayMode.NORMAL, 0xFF7CFC00, 0, 0xF000F0, 0);
            poseStack.popPose();
        }
    }
}

