package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.item.CyberneticCommandUplinkItem;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClientUplinkRenderer {
    private static BlockPos clientCornerA;
    private static BlockPos clientCornerB;
    private static CyberneticCommandUplinkItem.UplinkMode clientMode = CyberneticCommandUplinkItem.UplinkMode.MINING;
    private static BlockPos clientMoveTarget;
    private static long moveTargetTimestamp;

    public static void setClientZone(BlockPos cornerA, BlockPos cornerB, int modeOrdinal, BlockPos moveTarget) {
        clientCornerA = cornerA;
        clientCornerB = cornerB;
        clientMode = CyberneticCommandUplinkItem.UplinkMode.fromOrdinal(modeOrdinal);
        if (moveTarget != null) {
            clientMoveTarget = moveTarget;
            moveTargetTimestamp = System.currentTimeMillis();
        }
    }

    public static BlockPos getClientCornerA() {
        return clientCornerA;
    }

    public static BlockPos getClientCornerB() {
        return clientCornerB;
    }

    public static CyberneticCommandUplinkItem.UplinkMode getClientMode() {
        return clientMode;
    }

    public static BlockPos getClientMoveTarget() {
        return clientMoveTarget;
    }

    public static void reset() {
        clientCornerA = null;
        clientCornerB = null;
        clientMode = CyberneticCommandUplinkItem.UplinkMode.MINING;
        clientMoveTarget = null;
        moveTargetTimestamp = 0;
    }

    public static void render(LevelRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }

        boolean isHoldingUplink = player.getMainHandItem().is(SandStormItems.CYBERNETIC_COMMAND_UPLINK)
                || player.getOffhandItem().is(SandStormItems.CYBERNETIC_COMMAND_UPLINK);
        if (!isHoldingUplink) {
            return;
        }

        CameraRenderState camState = context.levelState().cameraRenderState;
        if (camState == null || camState.pos == null) {
            return;
        }
        Vec3 camPos = camState.pos;

        PoseStack poseStack = context.poseStack();
        SubmitNodeCollector collector = context.submitNodeCollector();
        float ticks = context.levelState().gameTime + context.levelState().worldPartialTicks;

        BlockPos prospectiveB = null;
        if (clientCornerA != null && clientCornerB == null) {
            if (mc.hitResult instanceof BlockHitResult bhr && bhr.getType() == HitResult.Type.BLOCK) {
                prospectiveB = bhr.getBlockPos();
            }
        }

        BlockPos posA = clientCornerA;
        BlockPos posB = clientCornerB != null ? clientCornerB : prospectiveB;
        boolean isPreview = clientCornerB == null && prospectiveB != null;

        int color = clientMode.getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        if (posA != null && posB != null) {
            renderHolographicBox(poseStack, collector, camPos, ticks, posA, posB, isPreview, r, g, b);
            renderFloatingDisplay(mc.font, poseStack, collector, camState, camPos, posA, posB, isPreview, color);
        } else if (clientCornerA != null) {
            renderCornerABeacon(poseStack, collector, camPos, ticks, clientCornerA, r, g, b);
        }

        if (clientMode == CyberneticCommandUplinkItem.UplinkMode.MOVE_ORDER) {
            renderMoveOrderTactical(poseStack, collector, camPos, ticks, mc);
        }
    }

    private static void renderHolographicBox(PoseStack poseStack, SubmitNodeCollector collector,
                                             Vec3 camPos, float ticks, BlockPos posA, BlockPos posB, boolean isPreview,
                                             int r, int g, int b) {
        int minX = Math.min(posA.getX(), posB.getX());
        int minY = Math.min(posA.getY(), posB.getY());
        int minZ = Math.min(posA.getZ(), posB.getZ());
        int maxX = Math.max(posA.getX(), posB.getX()) + 1;
        int maxY = Math.max(posA.getY(), posB.getY()) + 1;
        int maxZ = Math.max(posA.getZ(), posB.getZ()) + 1;

        float bMinX = (float) (minX - camPos.x) - 0.005f;
        float bMinY = (float) (minY - camPos.y) - 0.005f;
        float bMinZ = (float) (minZ - camPos.z) - 0.005f;
        float bMaxX = (float) (maxX - camPos.x) + 0.005f;
        float bMaxY = (float) (maxY - camPos.y) + 0.005f;
        float bMaxZ = (float) (maxZ - camPos.z) + 0.005f;

        float pulse = (Mth.sin(ticks * 0.15f) + 1.0f) * 0.5f;
        int lineAlpha = isPreview ? (int) (80 + pulse * 60) : (int) (140 + pulse * 60);
        int bracketAlpha = isPreview ? (int) (150 + pulse * 70) : 240;

        float scanProgress = (Mth.sin(ticks * 0.08f) + 1.0f) * 0.5f;
        float scanY = bMinY + scanProgress * Math.max(0.1f, (bMaxY - bMinY));

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            drawBoxLines(consumer, pose, bMinX, bMinY, bMinZ, bMaxX, bMaxY, bMaxZ, r, g, b, lineAlpha);

            float arm = Math.min(1.5f, Math.min(bMaxX - bMinX, Math.min(bMaxY - bMinY, bMaxZ - bMinZ)) * 0.35f);
            drawCornerBracket(consumer, pose, bMinX, bMinY, bMinZ, arm, arm, arm, r, g, b, bracketAlpha);
            drawCornerBracket(consumer, pose, bMaxX, bMinY, bMinZ, -arm, arm, arm, r, g, b, bracketAlpha);
            drawCornerBracket(consumer, pose, bMinX, bMinY, bMaxZ, arm, arm, -arm, r, g, b, bracketAlpha);
            drawCornerBracket(consumer, pose, bMaxX, bMinY, bMaxZ, -arm, arm, -arm, r, g, b, bracketAlpha);
            drawCornerBracket(consumer, pose, bMinX, bMaxY, bMinZ, arm, -arm, arm, r, g, b, bracketAlpha);
            drawCornerBracket(consumer, pose, bMaxX, bMaxY, bMinZ, -arm, -arm, arm, r, g, b, bracketAlpha);
            drawCornerBracket(consumer, pose, bMinX, bMaxY, bMaxZ, arm, -arm, -arm, r, g, b, bracketAlpha);
            drawCornerBracket(consumer, pose, bMaxX, bMaxY, bMaxZ, -arm, -arm, -arm, r, g, b, bracketAlpha);

            int scanR = Math.min(255, r + 60);
            int scanG = Math.min(255, g + 60);
            int scanB = Math.min(255, b + 60);
            int scanA = (int) (180 + pulse * 60);
            consumer.addVertex(pose, bMinX, scanY, bMinZ).setColor(scanR, scanG, scanB, scanA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
            consumer.addVertex(pose, bMaxX, scanY, bMinZ).setColor(scanR, scanG, scanB, scanA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);

            consumer.addVertex(pose, bMaxX, scanY, bMinZ).setColor(scanR, scanG, scanB, scanA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
            consumer.addVertex(pose, bMaxX, scanY, bMaxZ).setColor(scanR, scanG, scanB, scanA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);

            consumer.addVertex(pose, bMaxX, scanY, bMaxZ).setColor(scanR, scanG, scanB, scanA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
            consumer.addVertex(pose, bMinX, scanY, bMaxZ).setColor(scanR, scanG, scanB, scanA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);

            consumer.addVertex(pose, bMinX, scanY, bMaxZ).setColor(scanR, scanG, scanB, scanA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);
            consumer.addVertex(pose, bMinX, scanY, bMinZ).setColor(scanR, scanG, scanB, scanA).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.5f);

            if (isPreview) {
                consumer.addVertex(pose, bMinX, bMinY, bMinZ).setColor(r, g, b, 80).setNormal(pose, 1.0f, 1.0f, 1.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, bMaxX, bMaxY, bMaxZ).setColor(r, g, b, 80).setNormal(pose, 1.0f, 1.0f, 1.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, bMaxX, bMinY, bMinZ).setColor(r, g, b, 80).setNormal(pose, -1.0f, 1.0f, 1.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, bMinX, bMaxY, bMaxZ).setColor(r, g, b, 80).setNormal(pose, -1.0f, 1.0f, 1.0f).setLineWidth(1.0f);
            }
        });
    }

    private static void renderCornerABeacon(PoseStack poseStack, SubmitNodeCollector collector,
                                            Vec3 camPos, float ticks, BlockPos cornerA,
                                            int r, int g, int b) {
        float bMinX = (float) (cornerA.getX() - camPos.x) - 0.005f;
        float bMinY = (float) (cornerA.getY() - camPos.y) - 0.005f;
        float bMinZ = (float) (cornerA.getZ() - camPos.z) - 0.005f;
        float bMaxX = (float) (cornerA.getX() + 1 - camPos.x) + 0.005f;
        float bMaxY = (float) (cornerA.getY() + 1 - camPos.y) + 0.005f;
        float bMaxZ = (float) (cornerA.getZ() + 1 - camPos.z) + 0.005f;

        float pulse = (Mth.sin(ticks * 0.2f) + 1.0f) * 0.5f;
        int a = (int) (160 + pulse * 80);

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            drawBoxLines(consumer, pose, bMinX, bMinY, bMinZ, bMaxX, bMaxY, bMaxZ, r, g, b, a);
            float arm = 0.35f;
            drawCornerBracket(consumer, pose, bMinX, bMinY, bMinZ, arm, arm, arm, r, g, b, 250);
            drawCornerBracket(consumer, pose, bMaxX, bMinY, bMinZ, -arm, arm, arm, r, g, b, 250);
            drawCornerBracket(consumer, pose, bMinX, bMinY, bMaxZ, arm, arm, -arm, r, g, b, 250);
            drawCornerBracket(consumer, pose, bMaxX, bMinY, bMaxZ, -arm, arm, -arm, r, g, b, 250);
            drawCornerBracket(consumer, pose, bMinX, bMaxY, bMinZ, arm, -arm, arm, r, g, b, 250);
            drawCornerBracket(consumer, pose, bMaxX, bMaxY, bMinZ, -arm, -arm, arm, r, g, b, 250);
            drawCornerBracket(consumer, pose, bMinX, bMaxY, bMaxZ, arm, -arm, -arm, r, g, b, 250);
            drawCornerBracket(consumer, pose, bMaxX, bMaxY, bMaxZ, -arm, -arm, -arm, r, g, b, 250);

            float cx = (bMinX + bMaxX) * 0.5f;
            float cy = bMaxY;
            float cz = (bMinZ + bMaxZ) * 0.5f;
            consumer.addVertex(pose, cx, cy, cz).setColor(r, g, b, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(3.0f);
            consumer.addVertex(pose, cx, cy + 4.0f, cz).setColor(r, g, b, 20).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        });
    }

    private static void renderFloatingDisplay(Font font, PoseStack poseStack, SubmitNodeCollector collector,
                                              CameraRenderState camState, Vec3 camPos,
                                              BlockPos posA, BlockPos posB, boolean isPreview, int modeColor) {
        int minX = Math.min(posA.getX(), posB.getX());
        int minY = Math.min(posA.getY(), posB.getY());
        int minZ = Math.min(posA.getZ(), posB.getZ());
        int maxX = Math.max(posA.getX(), posB.getX()) + 1;
        int maxY = Math.max(posA.getY(), posB.getY()) + 1;
        int maxZ = Math.max(posA.getZ(), posB.getZ()) + 1;

        float centerX = (float) ((minX + maxX) * 0.5f - camPos.x);
        float topY = (float) (maxY - camPos.y) + 0.5f;
        float centerZ = (float) ((minZ + maxZ) * 0.5f - camPos.z);

        float distSq = centerX * centerX + topY * topY + centerZ * centerZ;
        if (distSq > 3600.0f) {
            return;
        }

        int sizeX = maxX - minX;
        int sizeY = maxY - minY;
        int sizeZ = maxZ - minZ;
        int totalBlocks = sizeX * sizeY * sizeZ;

        String line1 = isPreview ? "✦ [PREVIEW] " + clientMode.name() + " ✦" : "✦ " + clientMode.name() + " ZONE ✦";
        String line2 = sizeX + " x " + sizeY + " x " + sizeZ + " • " + totalBlocks + " BLOCKS";

        poseStack.pushPose();
        poseStack.translate(centerX, topY, centerZ);
        poseStack.rotateDegrees(Axis.YP, -camState.yRot);
        poseStack.rotateDegrees(Axis.XP, camState.xRot);
        float scale = 0.02f;
        poseStack.scale(scale, -scale, scale);

        int w1 = font.width(line1);
        int w2 = font.width(line2);
        collector.submitText(poseStack, -w1 / 2.0f, -12.0f, Component.literal(line1).getVisualOrderText(), false, Font.DisplayMode.NORMAL, modeColor | 0xFF000000, 0x80000000, 0xF000F0, 0);
        collector.submitText(poseStack, -w2 / 2.0f, 0.0f, Component.literal(line2).getVisualOrderText(), false, Font.DisplayMode.NORMAL, 0xFFFFFFFF, 0x80000000, 0xF000F0, 0);

        poseStack.popPose();
    }

    private static void renderMoveOrderTactical(PoseStack poseStack, SubmitNodeCollector collector,
                                                Vec3 camPos, float ticks, Minecraft mc) {
        if (mc.hitResult instanceof BlockHitResult bhr && bhr.getType() == HitResult.Type.BLOCK) {
            BlockPos targetPos = bhr.getBlockPos();
            float ox = (float) (targetPos.getX() + 0.5f - camPos.x);
            float oy = (float) (targetPos.getY() + 1.02f - camPos.y);
            float oz = (float) (targetPos.getZ() + 0.5f - camPos.z);

            collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
                int segments = 16;
                float r1 = 0.45f;
                float rot = ticks * 0.05f;
                for (int s = 0; s < segments; s++) {
                    float a1 = rot + (float) (s * 2.0 * Math.PI / segments);
                    float a2 = rot + (float) ((s + 1) * 2.0 * Math.PI / segments);
                    float x1 = ox + Mth.cos(a1) * r1;
                    float z1 = oz + Mth.sin(a1) * r1;
                    float x2 = ox + Mth.cos(a2) * r1;
                    float z2 = oz + Mth.sin(a2) * r1;
                    consumer.addVertex(pose, x1, oy, z1).setColor(255, 255, 255, 200).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
                    consumer.addVertex(pose, x2, oy, z2).setColor(255, 255, 255, 200).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
                }

                float r2 = 0.8f;
                consumer.addVertex(pose, ox - r2, oy, oz).setColor(255, 255, 255, 240).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.5f);
                consumer.addVertex(pose, ox + r2, oy, oz).setColor(255, 255, 255, 240).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.5f);
                consumer.addVertex(pose, ox, oy, oz - r2).setColor(255, 255, 255, 240).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(2.5f);
                consumer.addVertex(pose, ox, oy, oz + r2).setColor(255, 255, 255, 240).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(2.5f);
            });
        }

        if (clientMoveTarget != null && System.currentTimeMillis() - moveTargetTimestamp < 5000L) {
            float ox = (float) (clientMoveTarget.getX() + 0.5f - camPos.x);
            float oy = (float) (clientMoveTarget.getY() + 1.01f - camPos.y);
            float oz = (float) (clientMoveTarget.getZ() + 0.5f - camPos.z);
            float progress = (System.currentTimeMillis() - moveTargetTimestamp) / 5000.0f;
            int alpha = (int) (255 * (1.0f - progress));

            collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
                consumer.addVertex(pose, ox, oy, oz).setColor(255, 255, 255, alpha).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(3.0f);
                consumer.addVertex(pose, ox, oy + 5.0f, oz).setColor(255, 255, 255, 10).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
            });
        }
    }

    private static void drawCornerBracket(VertexConsumer consumer, PoseStack.Pose pose,
                                          float x, float y, float z, float dx, float dy, float dz,
                                          int r, int g, int b, int a) {
        consumer.addVertex(pose, x, y, z).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x + dx, y, z).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);

        consumer.addVertex(pose, x, y, z).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x, y + dy, z).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);

        consumer.addVertex(pose, x, y, z).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x, y, z + dz).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(2.0f);
    }

    private static void drawBoxLines(VertexConsumer consumer, PoseStack.Pose pose,
                                     float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                     int r, int g, int b, int a) {
        consumer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 0.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 0.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 0.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, -1.0f, 0.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 0.0f, -1.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);

        consumer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
        consumer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.5f);
    }
}
