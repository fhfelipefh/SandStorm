package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.BasicThermalGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BasicThermalGeneratorScreen extends BaseMachineScreen<BasicThermalGeneratorMenu> {
    public BasicThermalGeneratorScreen(BasicThermalGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderChassis(GuiGraphicsExtractor extractor) {
        super.renderChassis(extractor);
        int x = this.leftPos;
        int y = this.topPos;
        renderFlameIndicator(extractor, x + 81, y + 32, 14, 14);
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    private void renderFlameIndicator(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF05080E);
        if (this.menu.isProcessing()) {
            int progress = this.menu.getProgressScaled(height);
            int flameH = Math.max(2, Math.min(height, progress > 0 ? progress : height));
            extractor.fill(x + 1, y + height - flameH, x + width - 1, y + height, 0xFFFF6D00);
            extractor.fill(x + 3, y + height - (flameH / 2), x + width - 3, y + height, 0xFFFFD600);
        }
    }
}

