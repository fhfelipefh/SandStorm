package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.DesalinationFilterBlock;
import com.fhfelipefh.sandstorm.content.block.entity.DesalinationFilterBlockEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DesalinationFilterBlockEntityRenderer implements BlockEntityRenderer<DesalinationFilterBlockEntity, DesalinationFilterRenderState> {
    private static final Identifier MECHANISM_TEXTURE = SandStormMod.id("textures/entity/desalination_filter/desalination_filter_mechanisms.png");

    private static final float IN_TANK_MIN_X = -5.5f;
    private static final float IN_TANK_MAX_X = -1.5f;
    private static final float IN_TANK_MIN_Z = -3.0f;
    private static final float IN_TANK_MAX_Z = 3.0f;
    private static final float IN_TANK_BASE_Y = 2.0f;
    private static final float IN_TANK_HEIGHT = 5.0f;

    private static final float OUT_TANK_MIN_X = 1.5f;
    private static final float OUT_TANK_MAX_X = 5.5f;
    private static final float OUT_TANK_MIN_Z = -3.0f;
    private static final float OUT_TANK_MAX_Z = 3.0f;
    private static final float OUT_TANK_BASE_Y = 2.0f;
    private static final float OUT_TANK_HEIGHT = 4.0f;

    private static final float DROP_SPAWN_Y = 11.0f;
    private static final float DROP_LAND_Y = 6.5f;
    private static final float DROP_FALL_SPEED = 0.12f;
    private static final int DROP_COUNT = 3;

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
            renderWaterSurfaces(pose, consumer, state);
            if (state.isProcessing && state.waterInput > 0) {
                renderFallingDrops(pose, consumer, state);
                renderSpiralGlow(pose, consumer, state);
            }
        });

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5, 0.13, -0.05);
        poseStack.scale(0.007f, -0.007f, 0.007f);

        String displayText = buildDisplayText(state, progressRatio);
        int displayColor = buildDisplayColor(state);

        if (state.isProcessing) {
            float pulse = 0.65f + 0.35f * Mth.sin(state.animationTicks * 0.18f);
            int r = Math.min(0xFF, (int) (((displayColor >> 16) & 0xFF) * pulse));
            int g = Math.min(0xFF, (int) (((displayColor >> 8) & 0xFF) * pulse));
            int b = Math.min(0xFF, (int) ((displayColor & 0xFF) * pulse));
            displayColor = 0xFF000000 | (r << 16) | (g << 8) | b;
        }

        int textWidth = this.font.width(displayText);
        collector.submitText(poseStack, -textWidth / 2.0f, 0.0f, Component.literal(displayText).getVisualOrderText(), false, Font.DisplayMode.NORMAL, displayColor, 0, 0xF000F0, 0);
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderWaterSurfaces(PoseStack.Pose pose, VertexConsumer consumer, DesalinationFilterRenderState state) {
        if (state.waterInput > 0) {
            float inRatio = Math.min(1.0f, (float) state.waterInput / (float) state.maxWater);
            float surfaceY = IN_TANK_BASE_Y + inRatio * IN_TANK_HEIGHT;
            float wave = state.isProcessing ? Mth.sin(state.animationTicks * 0.08f) * 0.18f : 0.0f;
            surfaceY += wave;
            drawWaterSurface(pose, consumer,
                    IN_TANK_MIN_X + 0.5f, IN_TANK_MAX_X - 0.5f,
                    IN_TANK_MIN_Z + 0.5f, IN_TANK_MAX_Z - 0.5f,
                    surfaceY, 0x0D, 0x73, 0xB8, 0xCC);
        }

        if (state.waterOutput > 0) {
            float outRatio = Math.min(1.0f, (float) state.waterOutput / (float) state.maxWater);
            float surfaceY = OUT_TANK_BASE_Y + outRatio * OUT_TANK_HEIGHT;
            float wave = state.isProcessing ? Mth.sin(state.animationTicks * 0.10f + 1.2f) * 0.12f : 0.0f;
            surfaceY += wave;
            drawWaterSurface(pose, consumer,
                    OUT_TANK_MIN_X + 0.5f, OUT_TANK_MAX_X - 0.5f,
                    OUT_TANK_MIN_Z + 0.5f, OUT_TANK_MAX_Z - 0.5f,
                    surfaceY, 0x00, 0xCC, 0xFF, 0xCC);
        }
    }

    private void drawWaterSurface(PoseStack.Pose pose, VertexConsumer consumer,
                                  float minX, float maxX, float minZ, float maxZ, float y,
                                  int r, int g, int b, int a) {
        consumer.addVertex(pose, minX, y, minZ).setColor(r, g, b, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
        consumer.addVertex(pose, maxX, y, minZ).setColor(r, g, b, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);

        consumer.addVertex(pose, maxX, y, minZ).setColor(r, g, b, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
        consumer.addVertex(pose, maxX, y, maxZ).setColor(r, g, b, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);

        consumer.addVertex(pose, maxX, y, maxZ).setColor(r, g, b, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
        consumer.addVertex(pose, minX, y, maxZ).setColor(r, g, b, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);

        consumer.addVertex(pose, minX, y, maxZ).setColor(r, g, b, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
        consumer.addVertex(pose, minX, y, minZ).setColor(r, g, b, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);

        float midZ = (minZ + maxZ) / 2f;
        float midX = (minX + maxX) / 2f;
        int rS = Math.min(0xFF, r + 0x40); int gS = Math.min(0xFF, g + 0x40); int bS = Math.min(0xFF, b + 0x40);
        consumer.addVertex(pose, minX, y + 0.06f, midZ).setColor(rS, gS, bS, (int)(a * 0.6f)).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
        consumer.addVertex(pose, maxX, y + 0.06f, midZ).setColor(rS, gS, bS, (int)(a * 0.6f)).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);

        consumer.addVertex(pose, midX, y + 0.06f, minZ).setColor(rS, gS, bS, (int)(a * 0.6f)).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
        consumer.addVertex(pose, midX, y + 0.06f, maxZ).setColor(rS, gS, bS, (int)(a * 0.6f)).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
    }

    private void renderFallingDrops(PoseStack.Pose pose, VertexConsumer consumer, DesalinationFilterRenderState state) {
        float fallRange = DROP_SPAWN_Y - DROP_LAND_Y;
        float totalCycleTicks = fallRange / DROP_FALL_SPEED;
        float dropSpacingTicks = totalCycleTicks / DROP_COUNT;

        for (int i = 0; i < DROP_COUNT; i++) {
            float offset = i * dropSpacingTicks;
            float dropPhase = ((state.animationTicks + offset) % totalCycleTicks) / totalCycleTicks;
            float dropY = DROP_SPAWN_Y - dropPhase * fallRange;

            float dropX = Mth.sin(state.animationTicks * 0.05f + i * 2.1f) * 0.5f;
            float dropZ = Mth.cos(state.animationTicks * 0.04f + i * 1.7f) * 0.5f;

            float alpha = dropPhase < 0.1f ? dropPhase / 0.1f : (dropPhase > 0.9f ? (1f - dropPhase) / 0.1f : 1.0f);
            int a = (int) (alpha * 0xEE);
            int aFade = (int) (alpha * 0x44);

            consumer.addVertex(pose, dropX, dropY, dropZ).setColor(0x80, 0xF3, 0xFF, a).setNormal(pose, 0f, -1f, 0f).setLineWidth(1f);
            consumer.addVertex(pose, dropX, dropY - 1.0f, dropZ).setColor(0x00, 0xCC, 0xFF, aFade).setNormal(pose, 0f, -1f, 0f).setLineWidth(1f);
        }
    }

    private void renderSpiralGlow(PoseStack.Pose pose, VertexConsumer consumer, DesalinationFilterRenderState state) {
        float[] glowYs = {4.5f, 6.5f, 8.5f, 10.5f};
        float glowRadius = 2.3f;
        int glowSegments = 8;

        for (float glowY : glowYs) {
            float ringPulse = 0.35f + 0.65f * Mth.abs(Mth.sin(state.animationTicks * 0.13f + glowY * 0.35f));
            int a = (int) (ringPulse * 0x55);
            int gV = Math.min(0xFF, (int) (0xCC * ringPulse));

            for (int seg = 0; seg < glowSegments; seg++) {
                float angle0 = (float) (seg * Math.PI * 2.0 / glowSegments);
                float angle1 = (float) ((seg + 1) * Math.PI * 2.0 / glowSegments);
                float x0 = Mth.cos(angle0) * glowRadius;
                float z0 = Mth.sin(angle0) * glowRadius;
                float x1 = Mth.cos(angle1) * glowRadius;
                float z1 = Mth.sin(angle1) * glowRadius;

                consumer.addVertex(pose, x0, glowY, z0).setColor(0x00, gV, 0xFF, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
                consumer.addVertex(pose, x1, glowY, z1).setColor(0x00, gV, 0xFF, a).setNormal(pose, 0f, 1f, 0f).setLineWidth(1f);
            }
        }
    }

    private String buildDisplayText(DesalinationFilterRenderState state, float progressRatio) {
        if (state.isProcessing) {
            int pct = Math.min(100, (int) (progressRatio * 100.0f));
            return "DESALING " + pct + "%";
        }
        if (!state.hasCartridge) {
            return "NO CARTRIDGE";
        }
        if (state.waterOutput >= state.maxWater) {
            return "OUTPUT FULL";
        }
        if (state.waterInput > 0) {
            return "READY (" + (state.waterInput / 1000) + "B)";
        }
        return "NO WATER";
    }

    private int buildDisplayColor(DesalinationFilterRenderState state) {
        if (state.isProcessing) {
            return 0x00E5FF;
        }
        if (!state.hasCartridge) {
            return 0xFF5252;
        }
        if (state.waterOutput >= state.maxWater) {
            return 0xFFB74D;
        }
        if (state.waterInput > 0) {
            return 0x00E676;
        }
        return 0x78909C;
    }
}
