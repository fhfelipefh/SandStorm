package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.DeepCoreBoreholeMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class DeepCoreBoreholeScreen extends AbstractContainerScreen<DeepCoreBoreholeMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public DeepCoreBoreholeScreen(DeepCoreBoreholeMenu menu, Inventory playerInventory, Component title) {
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
        extractor.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFF8800, false);
        extractor.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF78909C, false);
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA0A0D14);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFFFF8800);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFFFF8800);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFFFF8800);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFFFF8800);

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

        int fx = x + 74;
        int fy = y + 56;
        int fw = 18;
        int fh = 18;
        extractor.fill(fx - 1, fy - 1, fx + fw + 1, fy + fh + 1, 0xFF1E293B);
        extractor.fill(fx, fy, fx + fw, fy + fh, 0xFF0B1220);
        int fluidH = this.menu.getFluidScaled(fh);
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

        String statusText;
        int statusColor;
        if (!this.menu.hasDrillBit()) {
            statusText = "REQUER BROCA";
            statusColor = 0xFFEF4444;
        } else if (this.menu.isDrilling()) {
            statusText = "PERFURANDO MANTO";
            statusColor = 0xFF10B981;
        } else {
            statusText = "SISTEMA PRONTO";
            statusColor = 0xFF00E5FF;
        }
        extractor.text(this.font, Component.literal(statusText), x + 8, y + 21, statusColor, false);

        String depthText = String.format("PROF: Y=%d", this.menu.getCurrentDepth());
        extractor.text(this.font, Component.literal(depthText), x + 8, y + 31, 0xFFF59E0B, false);

        String tempText = String.format("TEMP: %d K", this.menu.getTemperature());
        extractor.text(this.font, Component.literal(tempText), x + 8, y + 41, 0xFFEF4444, false);

        String presText = String.format("PRES: %d GPa", this.menu.getPressure());
        extractor.text(this.font, Component.literal(presText), x + 50, y + 41, 0xFF94A3B8, false);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 14 && mouseY <= y + 20) {
            String energyTip = String.format("Energia: %s / %s J", NumberFormat.compact(this.menu.getStoredEnergy()), NumberFormat.compact(this.menu.getMaxEnergy()));
            extractor.setTooltipForNextFrame(this.font, Component.literal(energyTip), mouseX, mouseY);
        }

        if (mouseX >= x + 74 && mouseX <= x + 92 && mouseY >= y + 56 && mouseY <= y + 74) {
            String fluidTip = String.format("Fluido Refrigerante: %d / 8000 mB", this.menu.getFluidAmount());
            extractor.setTooltipForNextFrame(this.font, Component.literal(fluidTip), mouseX, mouseY);
        }
    }
}
