package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.MegastructureConstructorBlock;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.megastructure.MegastructureBlueprint;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class MegastructureConstructorBlockEntityRenderer implements BlockEntityRenderer<MegastructureConstructorBlockEntity, MegastructureConstructorRenderState> {

    private final Font font;

    public MegastructureConstructorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public MegastructureConstructorRenderState createRenderState() {
        return new MegastructureConstructorRenderState();
    }

    @Override
    public void extractRenderState(MegastructureConstructorBlockEntity entity, MegastructureConstructorRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        if (entity.getBlockState().hasProperty(MegastructureConstructorBlock.FACING)) {
            state.facing = entity.getBlockState().getValue(MegastructureConstructorBlock.FACING);
        }
        state.blueprint = entity.getBlueprint();
        state.constructedBlocks = entity.getPlacementIndex();
        state.totalBlocks = entity.getTotalPlacements();
        state.isBuilding = entity.getBuildState() == MegastructureConstructorBlockEntity.STATE_BUILDING;
        state.isDone = entity.getBuildState() == MegastructureConstructorBlockEntity.STATE_COMPLETED;
        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
        state.targetRelPos = entity.getCurrentTargetRelPos();
    }

    @Override
    public void submit(MegastructureConstructorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.blueprint == null) {
            return;
        }

        renderHolographicWireframe(state, poseStack, collector);
        renderHolographicDisplay(state, poseStack, collector);
    }

    private void renderHolographicWireframe(MegastructureConstructorRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        List<MegastructureBlueprint.BlockPlacement> placements = state.blueprint.getPlacements();
        if (placements.isEmpty()) {
            return;
        }

        float pulse = (Mth.sin(state.animationTicks * 0.15f) + 1.0f) * 0.5f;
        int activeRed = (int) (255 * (0.8f + pulse * 0.2f));
        int activeGreen = (int) (145 * (0.8f + pulse * 0.2f));

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            for (int i = 0; i < placements.size(); i++) {
                MegastructureBlueprint.BlockPlacement placement = placements.get(i);
                BlockPos rel = placement.relativePos();
                float minX = rel.getX() + 0.05f;
                float minY = rel.getY() + 0.05f;
                float minZ = rel.getZ() + 0.05f;
                float maxX = rel.getX() + 0.95f;
                float maxY = rel.getY() + 0.95f;
                float maxZ = rel.getZ() + 0.95f;

                int r;
                int g;
                int b;
                int a;

                if (i < state.constructedBlocks) {
                    r = 0;
                    g = 180;
                    b = 255;
                    a = 40;
                } else if (i == state.constructedBlocks && state.isBuilding) {
                    r = activeRed;
                    g = activeGreen;
                    b = 0;
                    a = 255;
                } else {
                    r = 0;
                    g = 229;
                    b = 255;
                    a = 90;
                }

                drawBoxLines(consumer, pose, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);

                if (i == state.constructedBlocks && state.isBuilding) {
                    consumer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 1.0f, 1.0f).setLineWidth(2.0f);
                    consumer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 1.0f, 1.0f).setLineWidth(2.0f);
                    consumer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 1.0f, 1.0f).setLineWidth(2.0f);
                    consumer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 1.0f, 1.0f).setLineWidth(2.0f);
                }
            }

            if (state.isBuilding && state.targetRelPos != null) {
                float originX = 0.5f;
                float originY = 1.02f;
                float originZ = 0.5f;
                float targetX = state.targetRelPos.getX() + 0.5f;
                float targetY = state.targetRelPos.getY() + 0.5f;
                float targetZ = state.targetRelPos.getZ() + 0.5f;

                consumer.addVertex(pose, originX, originY, originZ).setColor(0, 229, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
                consumer.addVertex(pose, targetX, targetY, targetZ).setColor(255, 200, 50, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
            }
        });
    }

    private void drawBoxLines(VertexConsumer consumer, PoseStack.Pose pose,
                              float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                              int r, int g, int b, int a) {
        consumer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 0.0f, 0.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 0.0f, 0.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

        consumer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        consumer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
    }

    private void renderHolographicDisplay(MegastructureConstructorRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.25, 0.5);
        poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
        poseStack.scale(0.012f, -0.012f, 0.012f);

        int pct = state.totalBlocks > 0 ? (state.constructedBlocks * 100 / state.totalBlocks) : 0;
        String line1 = state.blueprint.getDisplayName();
        String line2 = state.isDone ? "COMPLETE [SHIELD ACTIVE]" : (state.isBuilding ? "PRINTING " + pct + "%" : "STANDBY " + pct + "%");
        int color1 = 0xFF00E5FF;
        int color2 = state.isDone ? 0xFF00E676 : (state.isBuilding ? 0xFFFFB300 : 0xFF90A4AE);

        int w1 = this.font.width(line1);
        int w2 = this.font.width(line2);

        collector.submitText(poseStack, -w1 / 2.0f, -6.0f, Component.literal(line1).getVisualOrderText(), false, Font.DisplayMode.NORMAL, color1, 0, 0xF000F0, 0);
        collector.submitText(poseStack, -w2 / 2.0f, 6.0f, Component.literal(line2).getVisualOrderText(), false, Font.DisplayMode.NORMAL, color2, 0, 0xF000F0, 0);

        poseStack.popPose();
    }
}
