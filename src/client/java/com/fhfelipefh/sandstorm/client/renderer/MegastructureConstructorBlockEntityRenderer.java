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
        state.materialReadiness = entity.getMaterialReadinessPercent();
        if (state.blueprint != null) {
            state.minPos = state.blueprint.getMinPos();
            state.maxPos = state.blueprint.getMaxPos();
        }
    }

    @Override
    public void submit(MegastructureConstructorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.blueprint == null) {
            return;
        }

        renderHolographicProjection(state, poseStack, collector);
        renderHolographicDisplay(state, poseStack, collector);
    }

    private void renderHolographicProjection(MegastructureConstructorRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        List<MegastructureBlueprint.BlockPlacement> placements = state.blueprint.getPlacements();
        if (placements.isEmpty()) {
            return;
        }

        float pulse = (Mth.sin(state.animationTicks * 0.15f) + 1.0f) * 0.5f;
        int activeRed = (int) (255 * (0.8f + pulse * 0.2f));
        int activeGreen = (int) (145 * (0.8f + pulse * 0.2f));

        BlockPos min = state.minPos != null ? state.minPos : BlockPos.ZERO;
        BlockPos max = state.maxPos != null ? state.maxPos : BlockPos.ZERO;
        float bMinX = min.getX() - 0.2f;
        float bMinY = min.getY();
        float bMinZ = min.getZ() - 0.2f;
        float bMaxX = max.getX() + 1.2f;
        float bMaxY = max.getY() + 1.2f;
        float bMaxZ = max.getZ() + 1.2f;

        float scanProgress = (Mth.sin(state.animationTicks * 0.08f) + 1.0f) * 0.5f;
        float scanY = bMinY + scanProgress * Math.max(1.0f, (bMaxY - bMinY));

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            renderEmitterRays(consumer, pose, state.animationTicks, bMinX, bMinY, bMinZ, bMaxX, bMaxZ, bMaxY);
            renderBoundingCage(consumer, pose, bMinX, bMinY, bMinZ, bMaxX, bMaxY, bMaxZ, scanY);

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
                    g = 230;
                    b = 118;
                    a = 50;
                } else if (i == state.constructedBlocks && state.isBuilding) {
                    r = activeRed;
                    g = activeGreen;
                    b = 0;
                    a = 255;
                } else {
                    float distToScan = Math.abs(rel.getY() - scanY);
                    if (distToScan < 1.2f) {
                        float intensity = 1.0f - (distToScan / 1.2f);
                        r = (int) (120 * intensity);
                        g = 255;
                        b = 255;
                        a = (int) (160 + 95 * intensity);
                    } else {
                        r = 0;
                        g = 229;
                        b = 255;
                        a = (int) (80 + pulse * 40);
                    }
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

    private void renderEmitterRays(VertexConsumer consumer, PoseStack.Pose pose, float ticks,
                                   float bMinX, float bMinY, float bMinZ, float bMaxX, float bMaxZ, float bMaxY) {
        float ox = 0.5f;
        float oy = 1.02f;
        float oz = 0.5f;

        consumer.addVertex(pose, ox, oy, oz).setColor(0, 229, 255, 140).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, bMinX, bMinY, bMinZ).setColor(0, 229, 255, 60).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, ox, oy, oz).setColor(0, 229, 255, 140).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, bMaxX, bMinY, bMinZ).setColor(0, 229, 255, 60).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, ox, oy, oz).setColor(0, 229, 255, 140).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, bMaxX, bMinY, bMaxZ).setColor(0, 229, 255, 60).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, ox, oy, oz).setColor(0, 229, 255, 140).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, bMinX, bMinY, bMaxZ).setColor(0, 229, 255, 60).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, ox, oy, oz).setColor(0, 229, 255, 200).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, ox, bMaxY, oz).setColor(0, 229, 255, 80).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

        float rot = ticks * 0.05f;
        float r = 0.35f;
        int segments = 8;
        for (int s = 0; s < segments; s++) {
            float a1 = rot + (float) (s * 2.0 * Math.PI / segments);
            float a2 = rot + (float) ((s + 1) * 2.0 * Math.PI / segments);
            float x1 = ox + Mth.cos(a1) * r;
            float z1 = oz + Mth.sin(a1) * r;
            float x2 = ox + Mth.cos(a2) * r;
            float z2 = oz + Mth.sin(a2) * r;
            consumer.addVertex(pose, x1, oy + 0.02f, z1).setColor(0, 229, 255, 180).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
            consumer.addVertex(pose, x2, oy + 0.02f, z2).setColor(0, 229, 255, 180).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        }
    }

    private void renderBoundingCage(VertexConsumer consumer, PoseStack.Pose pose,
                                    float x1, float y1, float z1, float x2, float y2, float z2, float scanY) {
        int r = 0;
        int g = 229;
        int b = 255;
        int a = 140;

        float arm = 1.8f;
        drawCornerBracket(consumer, pose, x1, y1, z1, arm, arm, arm, r, g, b, a);
        drawCornerBracket(consumer, pose, x2, y1, z1, -arm, arm, arm, r, g, b, a);
        drawCornerBracket(consumer, pose, x1, y1, z2, arm, arm, -arm, r, g, b, a);
        drawCornerBracket(consumer, pose, x2, y1, z2, -arm, arm, -arm, r, g, b, a);
        drawCornerBracket(consumer, pose, x1, y2, z1, arm, -arm, arm, r, g, b, a);
        drawCornerBracket(consumer, pose, x2, y2, z1, -arm, -arm, arm, r, g, b, a);
        drawCornerBracket(consumer, pose, x1, y2, z2, arm, -arm, -arm, r, g, b, a);
        drawCornerBracket(consumer, pose, x2, y2, z2, -arm, -arm, -arm, r, g, b, a);

        consumer.addVertex(pose, x1, scanY, z1).setColor(180, 255, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x2, scanY, z1).setColor(180, 255, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

        consumer.addVertex(pose, x2, scanY, z1).setColor(180, 255, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x2, scanY, z2).setColor(180, 255, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

        consumer.addVertex(pose, x2, scanY, z2).setColor(180, 255, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x1, scanY, z2).setColor(180, 255, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

        consumer.addVertex(pose, x1, scanY, z2).setColor(180, 255, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x1, scanY, z1).setColor(180, 255, 255, 220).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
    }

    private void drawCornerBracket(VertexConsumer consumer, PoseStack.Pose pose,
                                   float x, float y, float z, float dx, float dy, float dz,
                                   int r, int g, int b, int a) {
        consumer.addVertex(pose, x, y, z).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x + dx, y, z).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);

        consumer.addVertex(pose, x, y, z).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x, y + dy, z).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

        consumer.addVertex(pose, x, y, z).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x, y, z + dz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(2.0f);
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
        int pct = state.totalBlocks > 0 ? (state.constructedBlocks * 100 / state.totalBlocks) : 0;
        String line1 = "✦ " + state.blueprint.getDisplayName().toUpperCase() + " ✦";
        String line2 = state.blueprint.getSizeX() + "x" + state.blueprint.getSizeY() + "x" + state.blueprint.getSizeZ() + " • " + state.totalBlocks + " BLOCKS";
        String line3 = state.isDone ? "[COMPLETE • SHIELD ACTIVE]" : (state.isBuilding ? "[PRINTING " + pct + "%]" : "[STANDBY • MATS " + state.materialReadiness + "%]");
        int color1 = 0xFF00E5FF;
        int color2 = 0xFF80D8FF;
        int color3 = state.isDone ? 0xFF00E676 : (state.isBuilding ? 0xFFFFD600 : (state.materialReadiness == 100 ? 0xFF00E676 : 0xFF00E5FF));

        int w1 = this.font.width(line1);
        int w2 = this.font.width(line2);
        int w3 = this.font.width(line3);

        renderDisplayFace(poseStack, collector, state.facing.toYRot(), line1, line2, line3, color1, color2, color3, w1, w2, w3);
        renderDisplayFace(poseStack, collector, state.facing.toYRot() + 180.0f, line1, line2, line3, color1, color2, color3, w1, w2, w3);
    }

    private void renderDisplayFace(PoseStack poseStack, SubmitNodeCollector collector, float yRot,
                                   String line1, String line2, String line3,
                                   int c1, int c2, int c3, int w1, int w2, int w3) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.35, 0.5);
        poseStack.rotateDegrees(Axis.YP, -yRot);
        poseStack.scale(0.012f, -0.012f, 0.012f);

        collector.submitText(poseStack, -w1 / 2.0f, -14.0f, Component.literal(line1).getVisualOrderText(), false, Font.DisplayMode.NORMAL, c1, 0x90000000, 0xF000F0, 0);
        collector.submitText(poseStack, -w2 / 2.0f, -2.0f, Component.literal(line2).getVisualOrderText(), false, Font.DisplayMode.NORMAL, c2, 0x90000000, 0xF000F0, 0);
        collector.submitText(poseStack, -w3 / 2.0f, 10.0f, Component.literal(line3).getVisualOrderText(), false, Font.DisplayMode.NORMAL, c3, 0x90000000, 0xF000F0, 0);

        poseStack.popPose();
    }
}
