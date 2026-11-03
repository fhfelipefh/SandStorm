package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.network.SyncAnomalyRadarPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class AnomalyRadarScreen extends Screen {
    private final SyncAnomalyRadarPayload payload;
    private String activeFilter = "ALL";
    private static final int RADAR_RADIUS = 84;
    private static final double MAX_SCAN_DISTANCE = 96.0;

    public AnomalyRadarScreen(SyncAnomalyRadarPayload payload) {
        super(Component.translatable("gui.sandstorm.radar.title"));
        this.payload = payload;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        extractor.fill(0, 0, this.width, this.height, 0xC00A0E17);

        int panelWidth = 320;
        int panelHeight = 220;
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = (this.height - panelHeight) / 2;

        extractor.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xF00F1722);
        extractor.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 1, 0xFF00E5FF);
        extractor.fill(panelLeft, panelTop + panelHeight - 1, panelLeft + panelWidth, panelTop + panelHeight, 0xFF00E5FF);
        extractor.fill(panelLeft, panelTop, panelLeft + 1, panelTop + panelHeight, 0xFF00E5FF);
        extractor.fill(panelLeft + panelWidth - 1, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xFF00E5FF);

        extractor.text(this.font, Component.translatable("gui.sandstorm.radar.title"), panelLeft + 12, panelTop + 10, 0xFF00E5FF);
        extractor.fill(panelLeft + 8, panelTop + 24, panelLeft + 98, panelTop + 25, 0x4400E5FF);
        extractor.fill(panelLeft + 98, panelTop + 8, panelLeft + 99, panelTop + panelHeight - 8, 0x3300E5FF);

        renderFilterButtons(extractor, panelLeft + 8, panelTop + 30);
        renderTelemetryInfo(extractor, panelLeft + 8, panelTop + 140);
        renderCloseButton(extractor, panelLeft + 8, panelTop + panelHeight - 22);

        int radarCenterX = panelLeft + 99 + (panelWidth - 99) / 2;
        int radarCenterY = panelTop + panelHeight / 2;

        renderRadarGrid(extractor, radarCenterX, radarCenterY);
        renderPingsAndTooltips(extractor, radarCenterX, radarCenterY, mouseX, mouseY);
        renderPlayerMarker(extractor, radarCenterX, radarCenterY);

        if (payload.ionInterference()) {
            renderIonNoise(extractor, radarCenterX, radarCenterY);
        }
    }

    private void renderFilterButtons(GuiGraphicsExtractor extractor, int x, int y) {
        String[] filters = {"ALL", "buried_ruins", "outpost_terminal", "fuel_silo", "seismic_vault"};
        String[] labels = {"gui.sandstorm.radar.btn_all", "gui.sandstorm.radar.btn_ruins", "gui.sandstorm.radar.btn_data", "gui.sandstorm.radar.btn_fuel", "gui.sandstorm.radar.btn_vault"};
        int[] colors = {0xFFFFFFFF, 0xFFFFB300, 0xFF00E5FF, 0xFFFF6D00, 0xFFE040FB};

        for (int i = 0; i < filters.length; i++) {
            int by = y + i * 20;
            boolean selected = activeFilter.equals(filters[i]);
            int bg = selected ? 0xFF005B66 : 0x44141E28;
            int border = selected ? 0xFF00E5FF : 0x44263238;

            extractor.fill(x, by, x + 84, by + 16, bg);
            extractor.fill(x, by, x + 84, by + 1, border);
            extractor.fill(x, by + 15, x + 84, by + 16, border);
            extractor.fill(x, by, x + 1, by + 16, border);
            extractor.fill(x + 83, by, x + 84, by + 16, border);

            extractor.fill(x + 5, by + 5, x + 11, by + 11, colors[i]);
            extractor.text(this.font, Component.translatable(labels[i]), x + 16, by + 4, selected ? 0xFFFFFFFF : 0xFFB0BEC5);
        }
    }

    private void renderTelemetryInfo(GuiGraphicsExtractor extractor, int x, int y) {
        int count = getFilteredPingCount();
        extractor.text(this.font, Component.literal("§7Det: §f" + count), x + 4, y, 0xFFB0BEC5);
        extractor.text(this.font, Component.translatable("gui.sandstorm.radar.range"), x + 4, y + 12, 0xFF81C784);
        extractor.text(this.font, Component.literal("§7Y: §e" + payload.playerY()), x + 4, y + 24, 0xFFFFD54F);
    }

    private void renderCloseButton(GuiGraphicsExtractor extractor, int x, int y) {
        extractor.fill(x, y, x + 84, y + 14, 0x4437474F);
        extractor.fill(x, y, x + 84, y + 1, 0xFF78909C);
        extractor.fill(x, y + 13, x + 84, y + 14, 0xFF78909C);
        extractor.fill(x, y, x + 1, y + 14, 0xFF78909C);
        extractor.fill(x + 83, y, x + 84, y + 14, 0xFF78909C);
        extractor.text(this.font, Component.translatable("gui.sandstorm.radar.close"), x + 24, y + 3, 0xFFECEFF1);
    }

    private void renderRadarGrid(GuiGraphicsExtractor extractor, int cx, int cy) {
        int[] rings = {21, 42, 63, RADAR_RADIUS};
        for (int r : rings) {
            for (int deg = 0; deg < 360; deg += 10) {
                double rad = Math.toRadians(deg);
                int rx = cx + (int) (Math.cos(rad) * r);
                int ry = cy + (int) (Math.sin(rad) * r);
                extractor.fill(rx, ry, rx + 1, ry + 1, 0x4400E5FF);
            }
        }

        extractor.fill(cx - RADAR_RADIUS, cy, cx + RADAR_RADIUS, cy + 1, 0x3300E5FF);
        extractor.fill(cx, cy - RADAR_RADIUS, cx + 1, cy + RADAR_RADIUS, 0x3300E5FF);

        extractor.text(this.font, Component.literal("N"), cx - 3, cy - RADAR_RADIUS - 10, 0xFF00E5FF);
        extractor.text(this.font, Component.literal("S"), cx - 3, cy + RADAR_RADIUS + 3, 0xFF00E5FF);
        extractor.text(this.font, Component.literal("W"), cx - RADAR_RADIUS - 10, cy - 4, 0xFF00E5FF);
        extractor.text(this.font, Component.literal("E"), cx + RADAR_RADIUS + 4, cy - 4, 0xFF00E5FF);

        long time = System.currentTimeMillis();
        double sweep = (time % 3000) / 3000.0 * 2.0 * Math.PI;
        for (int d = 0; d < RADAR_RADIUS; d += 2) {
            int sx = cx + (int) (Math.cos(sweep) * d);
            int sy = cy + (int) (Math.sin(sweep) * d);
            extractor.fill(sx, sy, sx + 1, sy + 1, 0x6600E5FF);
        }
    }

    private void renderPlayerMarker(GuiGraphicsExtractor extractor, int cx, int cy) {
        extractor.fill(cx - 2, cy - 2, cx + 3, cy + 3, 0xFF00E676);
        double rad = Math.toRadians(payload.playerYaw() + 90.0);
        int tipX = cx + (int) (Math.cos(rad) * 7.0);
        int tipY = cy + (int) (Math.sin(rad) * 7.0);
        extractor.fill(tipX - 1, tipY - 1, tipX + 2, tipY + 2, 0xFF76FF03);
    }

    private void renderPingsAndTooltips(GuiGraphicsExtractor extractor, int cx, int cy, int mouseX, int mouseY) {
        long time = System.currentTimeMillis();
        SyncAnomalyRadarPayload.AnomalyPing hoveredPing = null;
        int hoveredDist = 0;

        for (SyncAnomalyRadarPayload.AnomalyPing ping : payload.pings()) {
            if (!activeFilter.equals("ALL") && !activeFilter.equals(ping.type())) {
                continue;
            }

            int dx = ping.x() - payload.playerX();
            int dz = ping.z() - payload.playerZ();
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist <= MAX_SCAN_DISTANCE) {
                int px = cx + (int) ((dx / MAX_SCAN_DISTANCE) * RADAR_RADIUS);
                int py = cy + (int) ((dz / MAX_SCAN_DISTANCE) * RADAR_RADIUS);

                if (payload.ionInterference()) {
                    px += (int) (Math.sin((time / 100.0) + ping.x()) * 3);
                    py += (int) (Math.cos((time / 100.0) + ping.z()) * 3);
                }

                int color = getPingColor(ping.type());
                extractor.fill(px - 2, py - 2, px + 3, py + 3, color);
                extractor.fill(px - 1, py - 1, px + 2, py + 2, 0xFFFFFFFF);

                if (Math.abs(mouseX - px) <= 4 && Math.abs(mouseY - py) <= 4) {
                    hoveredPing = ping;
                    hoveredDist = (int) dist;
                }
            }
        }

        if (hoveredPing != null) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable(getPingTranslatable(hoveredPing.type())));
            tooltip.add(Component.literal("§7Dist: §f" + hoveredDist + "m  §7Elev: §f" + hoveredPing.y()));
            tooltip.add(Component.literal("§7Pos: §e[" + hoveredPing.x() + ", " + hoveredPing.y() + ", " + hoveredPing.z() + "]"));
            extractor.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }

    private void renderIonNoise(GuiGraphicsExtractor extractor, int cx, int cy) {
        extractor.text(this.font, Component.translatable("gui.sandstorm.radar.ion_interference"), cx - 72, cy - RADAR_RADIUS + 4, 0xFFFF5252);
        long time = System.currentTimeMillis();
        for (int i = 0; i < 8; i++) {
            int rx = cx - RADAR_RADIUS + (int) ((time * (i + 1) * 37) % (RADAR_RADIUS * 2));
            int ry = cy - RADAR_RADIUS + (int) ((time * (i + 1) * 67) % (RADAR_RADIUS * 2));
            extractor.fill(rx, ry, rx + 6, ry + 1, 0x88FF1744);
        }
    }

    private int getFilteredPingCount() {
        int count = 0;
        for (SyncAnomalyRadarPayload.AnomalyPing ping : payload.pings()) {
            if (activeFilter.equals("ALL") || activeFilter.equals(ping.type())) {
                count++;
            }
        }
        return count;
    }

    private int getPingColor(String type) {
        return switch (type) {
            case "buried_ruins" -> 0xFFFFB300;
            case "outpost_terminal" -> 0xFF00E5FF;
            case "fuel_silo" -> 0xFFFF6D00;
            case "seismic_vault" -> 0xFFE040FB;
            default -> 0xFFFFFFFF;
        };
    }

    private String getPingTranslatable(String type) {
        return switch (type) {
            case "buried_ruins" -> "gui.sandstorm.radar.ping_buried_ruins";
            case "outpost_terminal" -> "gui.sandstorm.radar.ping_outpost_terminal";
            case "fuel_silo" -> "gui.sandstorm.radar.ping_fuel_silo";
            case "seismic_vault" -> "gui.sandstorm.radar.ping_seismic_vault";
            default -> "gui.sandstorm.radar.ping_unknown";
        };
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        int panelWidth = 320;
        int panelHeight = 220;
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = (this.height - panelHeight) / 2;

        int btnX = panelLeft + 8;
        int btnY = panelTop + 30;

        String[] filters = {"ALL", "buried_ruins", "outpost_terminal", "fuel_silo", "seismic_vault"};
        for (int i = 0; i < filters.length; i++) {
            int by = btnY + i * 20;
            if (event.x() >= btnX && event.x() <= btnX + 84 && event.y() >= by && event.y() <= by + 16) {
                activeFilter = filters[i];
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
                return true;
            }
        }

        int closeY = panelTop + panelHeight - 22;
        if (event.x() >= btnX && event.x() <= btnX + 84 && event.y() >= closeY && event.y() <= closeY + 14) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 0.9f));
            onClose();
            return true;
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 256 || event.key() == 69) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }
}

