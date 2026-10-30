package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.multiblock.MultiblockBlockRequirement;
import com.fhfelipefh.sandstorm.content.multiblock.MultiblockStructure;
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
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MultiblockHologramRenderer {
    private static MultiblockStructure activeStructure;
    private static BlockPos anchorPos;
    private static Direction facing = Direction.SOUTH;

    public static void setActiveStructure(MultiblockStructure structure, BlockPos pos, Direction dir) {
        activeStructure = structure;
        anchorPos = pos;
        facing = (dir != null && dir.getAxis().isHorizontal()) ? dir : Direction.SOUTH;
    }

    public static void clearActiveStructure() {
        activeStructure = null;
        anchorPos = null;
        facing = Direction.SOUTH;
    }

    public static boolean isStructureActive() {
        return activeStructure != null && anchorPos != null;
    }

    public static MultiblockStructure getActiveStructure() {
        return activeStructure;
    }

    public static BlockPos getAnchorPos() {
        return anchorPos;
    }

    public static Direction getFacing() {
        return facing;
    }

    public static void render(LevelRenderContext context) {
        if (activeStructure == null || anchorPos == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
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

        BlockPos hoveredPos = null;
        if (mc.hitResult instanceof BlockHitResult bhr && bhr.getType() == HitResult.Type.BLOCK) {
            hoveredPos = bhr.getBlockPos();
        }

        int completedBlocks = 0;
        int totalBlocks = activeStructure.getTotalBlocks();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (MultiblockBlockRequirement req : activeStructure.getRequirements()) {
            BlockPos rotated = MultiblockStructure.rotateOffset(req.offset(), facing);
            BlockPos targetPos = anchorPos.offset(rotated);

            minX = Math.min(minX, targetPos.getX());
            minY = Math.min(minY, targetPos.getY());
            minZ = Math.min(minZ, targetPos.getZ());
            maxX = Math.max(maxX, targetPos.getX() + 1);
            maxY = Math.max(maxY, targetPos.getY() + 1);
            maxZ = Math.max(maxZ, targetPos.getZ() + 1);

            BlockState state = mc.level.getBlockState(targetPos);
            boolean isCorrect = state.is(req.expectedBlock());
            if (isCorrect) {
                completedBlocks++;
            }

            renderBlockHologram(poseStack, collector, camPos, targetPos, req, isCorrect, ticks, targetPos.equals(hoveredPos));
        }

        if (minX != Integer.MAX_VALUE) {
            renderStructureHeader(mc.font, poseStack, collector, camState, camPos, minX, minY, minZ, maxX, maxY, maxZ, completedBlocks, totalBlocks);
        }
    }

    private static void renderBlockHologram(PoseStack poseStack, SubmitNodeCollector collector,
                                            Vec3 camPos, BlockPos worldPos, MultiblockBlockRequirement req,
                                            boolean isCorrect, float ticks, boolean isHovered) {
        float bMinX = (float) (worldPos.getX() - camPos.x) - 0.002f;
        float bMinY = (float) (worldPos.getY() - camPos.y) - 0.002f;
        float bMinZ = (float) (worldPos.getZ() - camPos.z) - 0.002f;
        float bMaxX = (float) (worldPos.getX() + 1 - camPos.x) + 0.002f;
        float bMaxY = (float) (worldPos.getY() + 1 - camPos.y) + 0.002f;
        float bMaxZ = (float) (worldPos.getZ() + 1 - camPos.z) + 0.002f;

        int r;
        int g;
        int b;
        int alpha;

        if (isCorrect) {
            r = 0;
            g = 230;
            b = 118;
            alpha = isHovered ? 240 : 140;
        } else {
            int color = req.colorRgb();
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = color & 0xFF;

            float pulse = (Mth.sin(ticks * 0.18f + (worldPos.getX() + worldPos.getY() + worldPos.getZ())) + 1.0f) * 0.5f;
            alpha = isHovered ? 255 : (int) (110 + pulse * 90);
        }

        int finalR = r;
        int finalG = g;
        int finalB = b;
        int finalAlpha = alpha;

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            drawBoxWireframe(consumer, pose, bMinX, bMinY, bMinZ, bMaxX, bMaxY, bMaxZ, finalR, finalG, finalB, finalAlpha);

            float arm = 0.25f;
            int bracketAlpha = Math.min(255, finalAlpha + 50);
            drawCornerBracket(consumer, pose, bMinX, bMinY, bMinZ, arm, arm, arm, finalR, finalG, finalB, bracketAlpha);
            drawCornerBracket(consumer, pose, bMaxX, bMinY, bMinZ, -arm, arm, arm, finalR, finalG, finalB, bracketAlpha);
            drawCornerBracket(consumer, pose, bMinX, bMinY, bMaxZ, arm, arm, -arm, finalR, finalG, finalB, bracketAlpha);
            drawCornerBracket(consumer, pose, bMaxX, bMinY, bMaxZ, -arm, arm, -arm, finalR, finalG, finalB, bracketAlpha);
            drawCornerBracket(consumer, pose, bMinX, bMaxY, bMinZ, arm, -arm, arm, finalR, finalG, finalB, bracketAlpha);
            drawCornerBracket(consumer, pose, bMaxX, bMaxY, bMinZ, -arm, -arm, arm, finalR, finalG, finalB, bracketAlpha);
            drawCornerBracket(consumer, pose, bMinX, bMaxY, bMaxZ, arm, -arm, -arm, finalR, finalG, finalB, bracketAlpha);
            drawCornerBracket(consumer, pose, bMaxX, bMaxY, bMaxZ, -arm, -arm, -arm, finalR, finalG, finalB, bracketAlpha);

            if (!isCorrect) {
                consumer.addVertex(pose, bMinX, bMinY, bMinZ).setColor(finalR, finalG, finalB, 60).setNormal(pose, 1.0f, 1.0f, 1.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, bMaxX, bMaxY, bMaxZ).setColor(finalR, finalG, finalB, 60).setNormal(pose, 1.0f, 1.0f, 1.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, bMaxX, bMinY, bMinZ).setColor(finalR, finalG, finalB, 60).setNormal(pose, -1.0f, 1.0f, 1.0f).setLineWidth(1.0f);
                consumer.addVertex(pose, bMinX, bMaxY, bMaxZ).setColor(finalR, finalG, finalB, 60).setNormal(pose, -1.0f, 1.0f, 1.0f).setLineWidth(1.0f);
            }
        });
    }

    private static void renderStructureHeader(Font font, PoseStack poseStack, SubmitNodeCollector collector,
                                              CameraRenderState camState, Vec3 camPos,
                                              int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
                                              int completedBlocks, int totalBlocks) {
        float centerX = (float) ((minX + maxX) * 0.5f - camPos.x);
        float topY = (float) (maxY - camPos.y) + 0.6f;
        float centerZ = (float) ((minZ + maxZ) * 0.5f - camPos.z);

        float distSq = centerX * centerX + topY * topY + centerZ * centerZ;
        if (distSq > 2500.0f) {
            return;
        }

        int percent = (totalBlocks > 0) ? (completedBlocks * 100 / totalBlocks) : 0;
        String line1 = "✦ HOLOGRAMA MULTIBLOCO | SANDSTORM ✦";
        String line2 = Component.translatable(activeStructure.getTitleKey()).getString();
        String line3 = (completedBlocks == totalBlocks)
                ? "§aESTRUTURA COMPLETA (100%)"
                : "§eMontagem: " + completedBlocks + " / " + totalBlocks + " (" + percent + "%)";

        poseStack.pushPose();
        poseStack.translate(centerX, topY, centerZ);
        poseStack.rotateDegrees(Axis.YP, -camState.yRot);
        poseStack.rotateDegrees(Axis.XP, camState.xRot);
        float scale = 0.02f;
        poseStack.scale(scale, -scale, scale);

        int w1 = font.width(line1);
        int w2 = font.width(line2);
        int w3 = font.width(line3);

        collector.submitText(poseStack, -w1 / 2.0f, -14.0f, Component.literal(line1).getVisualOrderText(), false, Font.DisplayMode.NORMAL, 0xFF00E5FF, 0x900A0E17, 0xF000F0, 0);
        collector.submitText(poseStack, -w2 / 2.0f, -2.0f, Component.literal(line2).getVisualOrderText(), false, Font.DisplayMode.NORMAL, 0xFFFFFFFF, 0x900A0E17, 0xF000F0, 0);
        collector.submitText(poseStack, -w3 / 2.0f, 10.0f, Component.literal(line3).getVisualOrderText(), false, Font.DisplayMode.NORMAL, 0xFFFFD54F, 0x900A0E17, 0xF000F0, 0);

        poseStack.popPose();
    }

    private static void drawBoxWireframe(VertexConsumer consumer, PoseStack.Pose pose,
                                         float minX, float minY, float minZ,
                                         float maxX, float maxY, float maxZ,
                                         int r, int g, int b, int a) {
        addLine(consumer, pose, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        addLine(consumer, pose, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        addLine(consumer, pose, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        addLine(consumer, pose, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        addLine(consumer, pose, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        addLine(consumer, pose, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        addLine(consumer, pose, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        addLine(consumer, pose, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        addLine(consumer, pose, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        addLine(consumer, pose, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        addLine(consumer, pose, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        addLine(consumer, pose, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
    }

    private static void drawCornerBracket(VertexConsumer consumer, PoseStack.Pose pose,
                                          float x, float y, float z,
                                          float dx, float dy, float dz,
                                          int r, int g, int b, int a) {
        addLine(consumer, pose, x, y, z, x + dx, y, z, r, g, b, a);
        addLine(consumer, pose, x, y, z, x, y + dy, z, r, g, b, a);
        addLine(consumer, pose, x, y, z, x, y, z + dz, r, g, b, a);
    }

    private static void addLine(VertexConsumer consumer, PoseStack.Pose pose,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                int r, int g, int b, int a) {
        consumer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
        consumer.addVertex(pose, x2, y2, z2).setColor(r, g, b, a).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(2.0f);
    }
}

