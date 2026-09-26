package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.LithoPlasmaExtractorMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class LithoPlasmaExtractorScreen extends AbstractContainerScreen<LithoPlasmaExtractorMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public LithoPlasmaExtractorScreen(LithoPlasmaExtractorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderTelemetry(extractor);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 16, 0xFFA855F7);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CHASSIS_WIDTH - 16, 0xFF78909C);
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

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA0B0914);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFFA855F7);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFFA855F7);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFFA855F7);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFFA855F7);

        int gx = x + 8;
        int gy = y + 15;
        int gw = 160;
        int gh = 4;
        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int energyW = this.menu.getEnergyScaled(gw);
        if (energyW > 0) {
            extractor.fill(gx, gy, gx + energyW, gy + gh, 0xFF00E5FF);
        }

        int px = x + 68;
        int py = y + 38;
        int pw = 36;
        int ph = 6;
        extractor.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, 0xFF1E293B);
        extractor.fill(px, py, px + pw, py + ph, 0xFF0A0815);
        int progW = this.menu.getProgressScaled(pw);
        if (progW > 0) {
            extractor.fill(px, py, px + progW, py + ph, 0xFFA855F7);
        }

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
        }
    }

    private void renderTelemetry(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        String statusText;
        int statusColor;
        if (this.menu.isExtracting()) {
            statusText = "CENTRÍFUGA ATIVA";
            statusColor = 0xFF10B981;
        } else if (this.menu.getStoredEnergy() < 150) {
            statusText = "SEM ENERGIA";
            statusColor = 0xFFEF4444;
        } else {
            statusText = "AGUARDANDO SAIS";
            statusColor = 0xFF94A3B8;
        }
        drawAdaptiveText(extractor, Component.literal(statusText), x + 8, y + 21, 95, statusColor);

        String plasmaText = String.format("PLASMA: %d%%", this.menu.getPlasmaConcentration());
        drawAdaptiveText(extractor, Component.literal(plasmaText), x + 106, y + 21, 60, 0xFFA855F7);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 14 && mouseY <= y + 20) {
            String energyTip = String.format("Energia: %s / %s J", NumberFormat.compact(this.menu.getStoredEnergy()), NumberFormat.compact(this.menu.getMaxEnergy()));
            extractor.setTooltipForNextFrame(this.font, Component.literal(energyTip), mouseX, mouseY);
        } else if (mouseX >= x + 68 && mouseX <= x + 104 && mouseY >= y + 37 && mouseY <= y + 45) {
            extractor.setTooltipForNextFrame(this.font, Component.literal("Centrífuga de Separação de Plasma"), mouseX, mouseY);
        } else if (mouseX >= x + 23 && mouseX <= x + 41 && mouseY >= y + 31 && mouseY <= y + 49) {
            if (!this.menu.slots.get(0).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.literal("Sal de Lítio Bruto"), mouseX, mouseY);
            }
        } else if (mouseX >= x + 43 && mouseX <= x + 61 && mouseY >= y + 31 && mouseY <= y + 49) {
            if (!this.menu.slots.get(1).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.literal("Recipiente de Coleta"), mouseX, mouseY);
            }
        } else if (mouseX >= x + 33 && mouseX <= x + 51 && mouseY >= y + 51 && mouseY <= y + 69) {
            if (!this.menu.slots.get(2).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.literal("Célula de Energia / Bateria"), mouseX, mouseY);
            }
        } else if (mouseX >= x + 111 && mouseX <= x + 129 && mouseY >= y + 31 && mouseY <= y + 49) {
            if (!this.menu.slots.get(3).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.literal("Lítio Purificado"), mouseX, mouseY);
            }
        } else if (mouseX >= x + 131 && mouseX <= x + 149 && mouseY >= y + 31 && mouseY <= y + 49) {
            if (!this.menu.slots.get(4).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.literal("Liga de Lítio-Plasma"), mouseX, mouseY);
            }
        } else if (mouseX >= x + 121 && mouseX <= x + 139 && mouseY >= y + 51 && mouseY <= y + 69) {
            if (!this.menu.slots.get(5).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.literal("Subprodutos Minerais"), mouseX, mouseY);
            }
        }
    }
}
