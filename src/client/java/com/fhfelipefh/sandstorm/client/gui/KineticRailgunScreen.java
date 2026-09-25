package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.KineticRailgunMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;


public class KineticRailgunScreen extends AbstractContainerScreen<KineticRailgunMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public KineticRailgunScreen(KineticRailgunMenu menu, Inventory playerInventory, Component title) {
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
        extractor.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF00E5FF, false);
        extractor.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF78909C, false);
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA070B14);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        int gx = x + 8;
        int gy = y + 16;
        int gw = 160;
        int gh = 6;
        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int energyW = this.menu.getEnergyScaled(gw);
        if (energyW > 0) {
            extractor.fill(gx, gy, gx + energyW, gy + gh, 0xFF00E5FF);
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

        int cd = this.menu.getCooldown();
        String statusText;
        int statusColor;
        if (cd > 0) {
            statusText = String.format("RECARGA: %.1fs", (float) cd / 20.0f);
            statusColor = 0xFFF59E0B;
        } else if (this.menu.getStoredEnergy() < 5000) {
            statusText = "SEM ENERGIA";
            statusColor = 0xFFEF4444;
        } else {
            statusText = "STATUS: PRONTO";
            statusColor = 0xFF10B981;
        }
        extractor.text(this.font, Component.literal(statusText), x + 8, y + 26, statusColor, false);

        String shotsText = String.format("DISPAROS: %d", this.menu.getTotalShotsFired());
        extractor.text(this.font, Component.literal(shotsText), x + 8, y + 38, 0xFF94A3B8, false);

        extractor.text(this.font, Component.literal("MUNIÇÃO 3x3"), x + 118, y + 26, 0xFF64748B, false);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 16 && mouseY <= y + 22) {
            String energyTip = String.format("Energia: %s / %s J", NumberFormat.compact(this.menu.getStoredEnergy()), NumberFormat.compact(this.menu.getMaxEnergy()));
            extractor.setTooltipForNextFrame(this.font, Component.literal(energyTip), mouseX, mouseY);
        }
    }
}
