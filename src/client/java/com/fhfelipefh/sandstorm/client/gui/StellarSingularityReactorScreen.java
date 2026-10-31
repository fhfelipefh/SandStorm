package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.StellarSingularityReactorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class StellarSingularityReactorScreen extends BaseMachineScreen<StellarSingularityReactorMenu> {
    public StellarSingularityReactorScreen(StellarSingularityReactorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    protected void renderChassis(GuiGraphicsExtractor extractor) {
        super.renderChassis(extractor);
        int x = this.leftPos;
        int y = this.topPos;

        renderContainmentField(extractor, x + 76, y + 41, 24, 24);
        renderBurnGauge(extractor, x + 60, y + 68, 56, 4);
    }

    private void renderContainmentField(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF0F172A);
        extractor.fill(x, y, x + width, y + height, 0xFF020617);
        if (this.menu.isProcessing()) {
            extractor.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0x3306B6D4);
        }
    }

    private void renderBurnGauge(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF05080E);
        int scaled = this.menu.getProgressScaled(width - 2);
        if (scaled > 0) {
            extractor.fill(x + 1, y + 1, x + 1 + scaled, y + height - 1, 0xFFE11D48);
        }
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        super.renderCustomTooltips(extractor, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 58 && mouseX <= x + 118 && mouseY >= y + 40 && mouseY <= y + 74) {
            int ticks = this.menu.getBurnTime();
            int totalSec = ticks / 20;
            int hours = totalSec / 3600;
            int minutes = (totalSec % 3600) / 60;
            int seconds = totalSec % 60;

            String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            Component line1 = Component.literal("Stellar Singularity Core");
            Component line2 = Component.literal("Combustao: " + timeFormatted);
            Component line3 = Component.literal("Saida: +25.000 J/t");
            Component line4 = Component.literal("Buffer: " + this.menu.getStoredEnergyLong() + " / 100.000.000 J");

            extractor.setComponentTooltipForNextFrame(this.font, List.of(line1, line2, line3, line4), mouseX, mouseY);
        }
    }
}

