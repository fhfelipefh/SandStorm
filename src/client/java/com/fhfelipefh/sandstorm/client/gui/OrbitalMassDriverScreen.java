package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.OrbitalMassDriverMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class OrbitalMassDriverScreen extends AbstractContainerScreen<OrbitalMassDriverMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public OrbitalMassDriverScreen(OrbitalMassDriverMenu menu, Inventory playerInventory, Component title) {
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
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int bx = this.leftPos + 58;
        int by = this.topPos + 55;
        int bw = 60;
        int bh = 14;

        if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderTelemetry(extractor, mouseX, mouseY);
        renderLaunchButton(extractor, mouseX, mouseY);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 16, 0xFF00E5FF);
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
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF00E5FF);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
        }
    }

    private void renderTelemetry(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int cd = this.menu.getLaunchCooldown();
        String statusText;
        int statusColor;
        if (cd > 0) {
            statusText = String.format("RESFRIANDO: %.1fs", (float) cd / 20.0f);
            statusColor = 0xFFF59E0B;
        } else if (this.menu.getStoredEnergy() < 250000) {
            statusText = "SEM ENERGIA (250kJ)";
            statusColor = 0xFFEF4444;
        } else if (!this.menu.canLaunch()) {
            statusText = "AGUARDANDO CARGA/CÉU";
            statusColor = 0xFF94A3B8;
        } else {
            statusText = "PRONTO PARA LANÇAMENTO";
            statusColor = 0xFF10B981;
        }
        extractor.text(this.font, Component.literal(statusText), x + 8, y + 24, statusColor, false);
    }

    private void renderLaunchButton(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int bx = this.leftPos + 58;
        int by = this.topPos + 55;
        int bw = 60;
        int bh = 14;

        boolean canLaunch = this.menu.canLaunch();
        boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;

        int border = canLaunch ? (hovered ? 0xFF00E5FF : 0xFF0288D1) : 0xFF334155;
        int bg = canLaunch ? (hovered ? 0xFF0E324D : 0xFF081C2B) : 0xFF0D131A;
        int textCol = canLaunch ? (hovered ? 0xFFFFFFFF : 0xFF00E5FF) : 0xFF64748B;

        extractor.fill(bx, by, bx + bw, by + bh, bg);
        extractor.fill(bx, by, bx + bw, by + 1, border);
        extractor.fill(bx, by + bh - 1, bx + bw, by + bh, border);
        extractor.fill(bx, by, bx + 1, by + bh, border);
        extractor.fill(bx + bw - 1, by, bx + bw, by + bh, border);

        String btnText = "LANÇAR";
        int tw = this.font.width(btnText);
        extractor.text(this.font, Component.literal(btnText), bx + (bw - tw) / 2, by + 3, textCol, false);
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
