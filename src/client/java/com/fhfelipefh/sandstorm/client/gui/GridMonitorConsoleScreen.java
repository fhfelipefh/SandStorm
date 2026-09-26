package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.GridMonitorConsoleMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GridMonitorConsoleScreen extends AbstractContainerScreen<GridMonitorConsoleMenu> {
    public GridMonitorConsoleScreen(GridMonitorConsoleMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, 96, 0xFF00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 160, 0xFF78909C);
    }

    private void drawAdaptiveText(GuiGraphicsExtractor extractor, Component text, float x, float y, float maxPixelWidth, int color) {
        int textWidth = this.font.width(text);
        if (textWidth <= maxPixelWidth || maxPixelWidth <= 0) {
            extractor.text(this.font, text, (int) x, (int) y, color, false);
        } else {
            float scale = maxPixelWidth / (float) textWidth;
            float offsetY = (9f - 9f * scale) / 2f;
            extractor.pose().pushMatrix();
            extractor.pose().translate(x, y + offsetY);
            extractor.pose().scale(scale, scale);
            extractor.text(this.font, text, 0, 0, color, false);
            extractor.pose().popMatrix();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderCustomTooltips(extractor, mouseX, mouseY);
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + 176, y + 166, 0xF008111A);
        extractor.fill(x, y, x + 176, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + 165, x + 176, y + 166, 0xFF00E5FF);
        extractor.fill(x, y, x + 1, y + 166, 0xFF00E5FF);
        extractor.fill(x + 175, y, x + 176, y + 166, 0xFF00E5FF);

        int status = this.menu.getGridStatus();
        String statusLabel = switch (status) {
            case 1 -> "SURPLUS";
            case 2 -> "DEFICIT";
            case 3 -> "CRITICAL";
            default -> "BALANCED";
        };
        int statusColor = switch (status) {
            case 1 -> 0xFF00E5FF;
            case 2 -> 0xFFFF9100;
            case 3 -> 0xFFFF1744;
            default -> 0xFF00E676;
        };

        int badgeW = this.font.width(statusLabel) + 8;
        int badgeX = x + 176 - badgeW - 8;
        int badgeY = y + 4;
        extractor.fill(badgeX, badgeY, badgeX + badgeW, badgeY + 11, 0xFF1E293B);
        extractor.fill(badgeX + 1, badgeY + 1, badgeX + badgeW - 1, badgeY + 10, 0xFF0B132B);
        extractor.text(this.font, Component.literal(statusLabel), badgeX + 4, badgeY + 2, statusColor, false);

        int panelX = x + 8;
        int panelY = y + 16;
        int panelW = 160;
        int panelH = 56;
        extractor.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0x80030712);
        extractor.fill(panelX, panelY, panelX + panelW, panelY + 1, 0xFF1E293B);
        extractor.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0xFF1E293B);

        int solarGen = this.menu.getSolarGenRate();
        int solarUnits = this.menu.getSolarCount();
        extractor.text(this.font, Component.literal("Solar: " + solarGen + " J/t (" + solarUnits + ")"), panelX + 4, panelY + 4, 0xFFFFD54F, false);

        int thermalGen = this.menu.getThermalGenRate();
        int thermalUnits = this.menu.getThermalCount();
        extractor.text(this.font, Component.literal("Thermal: " + thermalGen + " J/t (" + thermalUnits + ")"), panelX + 4, panelY + 14, 0xFFFF7043, false);

        int relays = this.menu.getRelayCount();
        int localWpt = this.menu.getLocalCoverageCharge();
        extractor.text(this.font, Component.literal("Torres WPT: " + relays + " | Local: " + localWpt + " J/t"), panelX + 4, panelY + 24, 0xFF4DD0E1, false);

        int stored = this.menu.getTotalStoredEnergy();
        int cap = this.menu.getTotalCapacity();
        int accumulators = this.menu.getAccumulatorCount();
        extractor.text(this.font, Component.literal("Baterias: " + NumberFormat.compact(stored) + " / " + NumberFormat.compact(cap) + " J (" + accumulators + ")"), panelX + 4, panelY + 34, 0xFF81C784, false);

        int barX = panelX + 4;
        int barY = panelY + 45;
        int barW = 152;
        int barH = 6;
        extractor.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF334155);
        extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF0F172A);

        float pct = cap > 0 ? Math.clamp((float) stored / (float) cap, 0.0f, 1.0f) : 0.0f;
        int filled = Math.round(pct * barW);
        if (filled > 0) {
            extractor.fill(barX, barY, barX + filled, barY + barH, statusColor);
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                renderSlotFrame(extractor, x + 7 + col * 18, y + 83 + row * 18);
            }
        }

        for (int col = 0; col < 9; col++) {
            renderSlotFrame(extractor, x + 7 + col * 18, y + 141);
        }
    }

    private void renderSlotFrame(GuiGraphicsExtractor extractor, int sx, int sy) {
        extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF1E293B);
        extractor.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF0B132B);
    }

    private void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 12 && mouseX <= x + 164 && mouseY >= y + 61 && mouseY <= y + 67) {
            int stored = this.menu.getTotalStoredEnergy();
            int cap = this.menu.getTotalCapacity();
            float pct = cap > 0 ? (float) stored / (float) cap * 100f : 0.0f;
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§aArmazenamento Global"),
                    Component.literal("§f" + NumberFormat.compact(stored) + " / " + NumberFormat.compact(cap) + " J (" + Math.round(pct) + "%)")
            ), mouseX, mouseY);
        }
    }
}
