package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.client.FlashlightState;
import com.fhfelipefh.sandstorm.content.survival.FlashlightFocalModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

public final class FlashlightFocalRenderer {
    private FlashlightFocalRenderer() {}

    public static void render(GuiGraphicsExtractor extractor, Minecraft client, int screenWidth, int screenHeight) {
        if (!FlashlightState.isActive()) {
            return;
        }

        Player player = client.player;
        if (player == null) {
            return;
        }

        double hitDist = FlashlightFocalModel.DEFAULT_MAX_RANGE;
        HitResult hit = player.pick(FlashlightFocalModel.DEFAULT_MAX_RANGE, 1.0f, false);
        if (hit.getType() != HitResult.Type.MISS) {
            hitDist = hit.getLocation().distanceTo(player.getEyePosition());
        }

        float radius = FlashlightFocalModel.calculateFocalApertureRadius(hitDist, screenWidth, screenHeight);
        int cx = screenWidth / 2;
        int cy = screenHeight / 2;

        int rInt = Math.round(radius);
        int rOuter = Math.round(radius * 1.35f);

        int edgeAlpha = (int) (FlashlightFocalModel.calculateVignetteAlpha(rOuter, radius) * 255.0f);
        int outerColor = (edgeAlpha << 24) | 0x05080E;

        int leftEnd = Math.max(0, cx - rOuter);
        int rightStart = Math.min(screenWidth, cx + rOuter);
        int topEnd = Math.max(0, cy - rOuter);
        int bottomStart = Math.min(screenHeight, cy + rOuter);

        if (leftEnd > 0) {
            extractor.fill(0, 0, leftEnd, screenHeight, outerColor);
        }
        if (rightStart < screenWidth) {
            extractor.fill(rightStart, 0, screenWidth, screenHeight, outerColor);
        }
        if (topEnd > 0) {
            extractor.fill(leftEnd, 0, rightStart, topEnd, outerColor);
        }
        if (bottomStart < screenHeight) {
            extractor.fill(leftEnd, bottomStart, rightStart, screenHeight, outerColor);
        }

        int stepCount = 5;
        for (int i = 0; i < stepCount; i++) {
            float stepRatio = 0.75f + (float) i / (float) stepCount * 0.55f;
            float stepR = radius * stepRatio;
            float stepAlpha = FlashlightFocalModel.calculateVignetteAlpha(stepR, radius);
            int color = ((int) (stepAlpha * 255.0f) << 24) | 0x070B12;

            int stepInt = Math.round(stepR);
            int stepOuter = Math.round(stepR + (radius * 0.12f));

            int x1 = Math.max(0, cx - stepOuter);
            int x2 = Math.min(screenWidth, cx + stepOuter);
            int y1 = Math.max(0, cy - stepOuter);
            int y2 = Math.min(screenHeight, cy + stepOuter);

            int inX1 = Math.max(0, cx - stepInt);
            int inX2 = Math.min(screenWidth, cx + stepInt);
            int inY1 = Math.max(0, cy - stepInt);
            int inY2 = Math.min(screenHeight, cy + stepInt);

            extractor.fill(x1, y1, inX1, y2, color);
            extractor.fill(inX2, y1, x2, y2, color);
            extractor.fill(inX1, y1, inX2, inY1, color);
            extractor.fill(inX1, inY2, inX2, y2, color);
        }

        int ringColor = 0x3000E5FF;
        int rx1 = cx - rInt;
        int rx2 = cx + rInt;
        int ry1 = cy - rInt;
        int ry2 = cy + rInt;
        extractor.fill(rx1, ry1, rx2, ry1 + 1, ringColor);
        extractor.fill(rx1, ry2 - 1, rx2, ry2, ringColor);
        extractor.fill(rx1, ry1, rx1 + 1, ry2, ringColor);
        extractor.fill(rx2 - 1, ry1, rx2, ry2, ringColor);
    }
}
