package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.PlasmaShieldMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;


public class PlasmaShieldScreen extends AbstractContainerScreen<PlasmaShieldMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public PlasmaShieldScreen(PlasmaShieldMenu menu, Inventory playerInventory, Component title) {
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
        int x = this.leftPos;
        int y = this.topPos;

        int b0x = x + 16;
        int b0y = y + 44;
        if (mx >= b0x && mx <= b0x + 60 && my >= b0y && my <= b0y + 14) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        int b1x = x + 82;
        int b1y = y + 44;
        if (mx >= b1x && mx <= b1x + 60 && my >= b1y && my <= b1y + 14) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
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
        renderControlsAndStatus(extractor, mouseX, mouseY);
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
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
        }
    }

    private void renderControlsAndStatus(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        boolean active = this.menu.isShieldActive();
        Component statusComp = active ? Component.translatable("gui.sandstorm.plasma_shield.status_online") : Component.translatable("gui.sandstorm.plasma_shield.status_offline");
        int statusColor = active ? 0xFF00E5FF : 0xFF64748B;
        drawAdaptiveText(extractor, statusComp, x + 16, y + 26, 60, statusColor);

        int radius = this.menu.getShieldRadius();
        Component radComp = Component.translatable("gui.sandstorm.plasma_shield.radius", radius);
        drawAdaptiveText(extractor, radComp, x + 16, y + 35, 60, 0xFF94A3B8);

        Component threatComp = Component.translatable("gui.sandstorm.plasma_shield.threats", this.menu.getThreatCount());
        drawAdaptiveText(extractor, threatComp, x + 82, y + 35, 60, 0xFFFFB300);

        int b0x = x + 16;
        int b0y = y + 44;
        Component b0Comp = active ? Component.translatable("gui.sandstorm.plasma_shield.deactivate") : Component.translatable("gui.sandstorm.plasma_shield.activate");
        int tw0 = this.font.width(b0Comp);
        extractor.text(this.font, b0Comp, b0x + (60 - tw0) / 2, b0y + 3, 0xFFFFFFFF, false);

        int b1x = x + 82;
        int b1y = y + 44;
        boolean b1Hover = mouseX >= b1x && mouseX <= b1x + 60 && mouseY >= b1y && mouseY <= b1y + 14;
        extractor.fill(b1x, b1y, b1x + 60, b1y + 14, b1Hover ? 0xFF0284C7 : 0xFF0F172A);
        Component b1Comp = Component.translatable("gui.sandstorm.plasma_shield.radius_toggle");
        int tw1 = this.font.width(b1Comp);
        extractor.text(this.font, b1Comp, b1x + (60 - tw1) / 2, b1y + 3, 0xFFFFFFFF, false);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 16 && mouseY <= y + 22) {
            Component energyTip = Component.translatable("gui.sandstorm.plasma_shield.energy", NumberFormat.compact(this.menu.getStoredEnergy()), NumberFormat.compact(this.menu.getMaxEnergy()));
            extractor.setTooltipForNextFrame(this.font, energyTip, mouseX, mouseY);
        }
    }
}
