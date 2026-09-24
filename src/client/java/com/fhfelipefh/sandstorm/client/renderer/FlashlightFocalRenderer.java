package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.client.FlashlightState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

public final class FlashlightFocalRenderer {
    private FlashlightFocalRenderer() {}

    public static void render(GuiGraphicsExtractor extractor, Minecraft client, int screenWidth, int screenHeight) {
        int mode = FlashlightState.getMode();
        if (mode <= FlashlightState.MODE_OFF) {
            return;
        }

        Player player = client.player;
        if (player == null) {
            return;
        }

        double maxRange = FlashlightFocalModel.getMaxRangeForMode(mode);
        double hitDist = maxRange;
        HitResult hit = player.pick(maxRange, 1.0f, false);
        if (hit != null && hit.getType() != HitResult.Type.MISS) {
            hitDist = hit.getLocation().distanceTo(player.getEyePosition());
        }

        float radius = FlashlightFocalModel.calculateFocalApertureRadius(hitDist, screenWidth, screenHeight, mode);
        int cx = screenWidth / 2;
        int cy = screenHeight / 2;
        int rInt = Math.round(radius);

        int ringColor = switch (mode) {
            case FlashlightState.MODE_LOW -> 0x3000E676;
            case FlashlightState.MODE_MEDIUM -> 0x50FFD600;
            case FlashlightState.MODE_HIGH -> 0x7000E5FF;
            default -> 0x3000E5FF;
        };

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
