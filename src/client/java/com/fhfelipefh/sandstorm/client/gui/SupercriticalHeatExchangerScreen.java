package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.SupercriticalHeatExchangerMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class SupercriticalHeatExchangerScreen extends AbstractContainerScreen<SupercriticalHeatExchangerMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public SupercriticalHeatExchangerScreen(SupercriticalHeatExchangerMenu menu, Inventory playerInventory, Component title) {
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
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 16, 0xFFEF4444);
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

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA140A0A);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFFEF4444);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFFEF4444);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFFEF4444);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFFEF4444);

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

        int fx = x + 40;
        int fy = y + 32;
        int fw = 14;
        int fh = 36;
        extractor.fill(fx - 1, fy - 1, fx + fw + 1, fy + fh + 1, 0xFF1E293B);
        extractor.fill(fx, fy, fx + fw, fy + fh, 0xFF0B1220);
        int fluidH = this.menu.getWaterScaled(fh);
        if (fluidH > 0) {
            extractor.fill(fx, fy + fh - fluidH, fx + fw, fy + fh, 0xFF2563EB);
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

        Component statusComp;
        int statusColor;
        if (this.menu.isOperating()) {
            statusComp = Component.translatable("gui.sandstorm.heat_exchanger.status_operating");
            statusColor = 0xFF10B981;
        } else if (this.menu.getWaterAmount() <= 0) {
            statusComp = Component.translatable("gui.sandstorm.heat_exchanger.status_no_water");
            statusColor = 0xFFEF4444;
        } else {
            statusComp = Component.translatable("gui.sandstorm.heat_exchanger.status_full");
            statusColor = 0xFF00E5FF;
        }
        drawAdaptiveText(extractor, statusComp, x + 8, y + 21, 100, statusColor);

        Component genComp = Component.translatable("gui.sandstorm.heat_exchanger.gen_rate", NumberFormat.compact(this.menu.getCurrentGenRate()));
        drawAdaptiveText(extractor, genComp, x + 116, y + 36, 50, 0xFFF59E0B);

        Component presComp = Component.translatable("gui.sandstorm.heat_exchanger.steam_pressure", this.menu.getSteamPressure());
        drawAdaptiveText(extractor, presComp, x + 116, y + 48, 50, 0xFFEF4444);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 14 && mouseY <= y + 20) {
            Component energyTip = Component.translatable("gui.sandstorm.heat_exchanger.energy", NumberFormat.compact(this.menu.getStoredEnergy()), NumberFormat.compact(this.menu.getMaxEnergy()));
            extractor.setTooltipForNextFrame(this.font, energyTip, mouseX, mouseY);
        }

        if (mouseX >= x + 40 && mouseX <= x + 54 && mouseY >= y + 32 && mouseY <= y + 68) {
            Component fluidTip = Component.translatable("gui.sandstorm.heat_exchanger.water", this.menu.getWaterAmount());
            extractor.setTooltipForNextFrame(this.font, fluidTip, mouseX, mouseY);
        }
    }
}
