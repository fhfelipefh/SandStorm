package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.ElectricArcFurnaceBlock;
import com.fhfelipefh.sandstorm.content.block.entity.ElectricArcFurnaceBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ElectricArcFurnaceBlockEntityRenderer implements BlockEntityRenderer<ElectricArcFurnaceBlockEntity, ElectricArcFurnaceRenderState> {
    private final ItemModelResolver itemModelResolver;

    public ElectricArcFurnaceBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ElectricArcFurnaceRenderState createRenderState() {
        return new ElectricArcFurnaceRenderState();
    }

    @Override
    public void extractRenderState(ElectricArcFurnaceBlockEntity entity, ElectricArcFurnaceRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(ElectricArcFurnaceBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(ElectricArcFurnaceBlock.FACING);
        }
        state.isProcessing = entity.isProcessing();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;

        ItemStack inputStack = entity.getItem(0);
        if (!inputStack.isEmpty() && level != null && state.isProcessing) {
            state.hasItem = true;
            this.itemModelResolver.updateForTopItem(state.itemRenderState, inputStack, ItemDisplayContext.FIXED, level, null, 0);
        } else {
            state.hasItem = false;
        }
    }

    @Override
    public void submit(ElectricArcFurnaceRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (!state.isProcessing) {
            return;
        }

        if (state.hasItem) {
            poseStack.pushPose();
            float bob = Mth.sin(state.animationTicks * 0.15f) * 0.02f;
            poseStack.translate(0.5, 0.45 + bob, 0.5);
            poseStack.scale(0.35f, 0.35f, 0.35f);
            state.itemRenderState.submit(poseStack, collector, 15728880, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        float time = state.animationTicks;
        float jitterX = Mth.sin(time * 1.5f) * 0.04f;
        float jitterY = Mth.cos(time * 2.1f) * 0.04f;

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            consumer.addVertex(pose, 0.30f, 0.45f, 0.5f).setColor(255, 140, 0, 255).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.5f);
            consumer.addVertex(pose, 0.5f + jitterX, 0.45f + jitterY, 0.5f).setColor(0, 229, 255, 255).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.5f);

            consumer.addVertex(pose, 0.5f + jitterX, 0.45f + jitterY, 0.5f).setColor(0, 229, 255, 255).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.5f);
            consumer.addVertex(pose, 0.70f, 0.45f, 0.5f).setColor(255, 140, 0, 255).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.5f);
        });
    }
}
