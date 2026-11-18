package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.network.SyncAnomalyRadarPayload;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AnomalyWaypointRenderer {

    public record AnomalyWaypoint(int x, int y, int z, String type, long expireAtMs) {}

    private static final List<AnomalyWaypoint> ACTIVE_WAYPOINTS = new CopyOnWriteArrayList<>();

    public static void registerPings(List<SyncAnomalyRadarPayload.AnomalyPing> pings, long durationMs) {
        if (pings == null || pings.isEmpty()) {
            return;
        }
        long expireAt = System.currentTimeMillis() + durationMs;
        ACTIVE_WAYPOINTS.removeIf(w -> System.currentTimeMillis() >= w.expireAtMs());
        for (SyncAnomalyRadarPayload.AnomalyPing ping : pings) {
            boolean exists = false;
            for (AnomalyWaypoint w : ACTIVE_WAYPOINTS) {
                if (w.x() == ping.x() && w.z() == ping.z()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                ACTIVE_WAYPOINTS.add(new AnomalyWaypoint(ping.x(), ping.y(), ping.z(), ping.type(), expireAt));
            }
        }
    }

    public static void clear() {
        ACTIVE_WAYPOINTS.clear();
    }

    public static int getActiveCount() {
        return ACTIVE_WAYPOINTS.size();
    }

    public static void render(LevelRenderContext context) {
        if (ACTIVE_WAYPOINTS.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        ACTIVE_WAYPOINTS.removeIf(w -> now >= w.expireAtMs());
        if (ACTIVE_WAYPOINTS.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        CameraRenderState camState = context.levelState().cameraRenderState;
        if (camState == null || camState.pos == null) {
            return;
        }

        Vec3 camPos = camState.pos;
        PoseStack poseStack = context.poseStack();
        SubmitNodeCollector collector = context.submitNodeCollector();
        Font font = mc.font;

        for (AnomalyWaypoint w : ACTIVE_WAYPOINTS) {
            float wx = (float) (w.x() + 0.5 - camPos.x);
            float wy = (float) (w.y() - camPos.y);
            float wz = (float) (w.z() + 0.5 - camPos.z);

            double distSq = wx * wx + wy * wy + wz * wz;
            if (distSq > 160000.0) {
                continue;
            }

            int r = 255;
            int g = 40;
            int b = 40;
            int alpha = 230;

            collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
                consumer.addVertex(pose, wx, wy, wz)
                        .setColor(r, g, b, alpha)
                        .setNormal(pose, 0.0f, 1.0f, 0.0f)
                        .setLineWidth(3.5f);
                consumer.addVertex(pose, wx, wy + 40.0f, wz)
                        .setColor(r, g, b, 20)
                        .setNormal(pose, 0.0f, 1.0f, 0.0f)
                        .setLineWidth(1.5f);

                float arm = 0.6f;
                consumer.addVertex(pose, wx - arm, wy + 0.05f, wz)
                        .setColor(r, g, b, alpha)
                        .setNormal(pose, 1.0f, 0.0f, 0.0f)
                        .setLineWidth(2.0f);
                consumer.addVertex(pose, wx + arm, wy + 0.05f, wz)
                        .setColor(r, g, b, alpha)
                        .setNormal(pose, 1.0f, 0.0f, 0.0f)
                        .setLineWidth(2.0f);

                consumer.addVertex(pose, wx, wy + 0.05f, wz - arm)
                        .setColor(r, g, b, alpha)
                        .setNormal(pose, 0.0f, 0.0f, 1.0f)
                        .setLineWidth(2.0f);
                consumer.addVertex(pose, wx, wy + 0.05f, wz + arm)
                        .setColor(r, g, b, alpha)
                        .setNormal(pose, 0.0f, 0.0f, 1.0f)
                        .setLineWidth(2.0f);
            });

            double dist = Math.sqrt(distSq);
            int remainingSec = (int) Math.max(0, (w.expireAtMs() - now) / 1000);
            String label = getFriendlyTypeName(w.type());
            Component text1 = Component.literal("§c◆ " + label);
            Component text2 = Component.literal("§e" + (int) dist + "m §7(" + remainingSec + "s)");

            poseStack.pushPose();
            poseStack.translate(wx, wy + 2.2f, wz);
            poseStack.rotateDegrees(Axis.YP, -camState.yRot);
            poseStack.rotateDegrees(Axis.XP, camState.xRot);

            float scale = (float) Math.max(0.02f, dist * 0.0035f);
            poseStack.scale(scale, -scale, scale);

            int width1 = font.width(text1);
            int width2 = font.width(text2);

            collector.submitText(poseStack, -width1 / 2.0f, -12.0f, text1.getVisualOrderText(), false, Font.DisplayMode.SEE_THROUGH, 0xFFFF3333, 0x80000000, 0xF000F0, 0);
            collector.submitText(poseStack, -width2 / 2.0f, 0.0f, text2.getVisualOrderText(), false, Font.DisplayMode.SEE_THROUGH, 0xFFFFFFFF, 0x80000000, 0xF000F0, 0);

            poseStack.popPose();
        }
    }

    private static String getFriendlyTypeName(String type) {
        return switch (type) {
            case "buried_ruins" -> "Ruínas";
            case "outpost_terminal" -> "Terminal";
            case "fuel_silo" -> "Combustível";
            case "seismic_vault" -> "Bunker";
            default -> "Anomalia";
        };
    }
}
