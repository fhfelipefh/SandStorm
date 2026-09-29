package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.ThermalGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ThermalGeneratorScreen extends BaseMachineScreen<ThermalGeneratorMenu> {
    public ThermalGeneratorScreen(ThermalGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderChassis(GuiGraphicsExtractor extractor) {
        super.renderChassis(extractor);
        int x = this.leftPos;
        int y = this.topPos;
        renderLavaTank(extractor, x + 26, y + 19, 12, 26);
    }

    private void renderLavaTank(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF05080E);
        int scaled = this.menu.getLavaScaled(height);
        if (scaled > 0) {
            extractor.fill(x + 1, y + height - scaled, x + width - 1, y + height, 0xFFFF6D00);
        }
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        super.renderCustomTooltips(extractor, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;
        if (mouseX >= x + 25 && mouseX <= x + 39 && mouseY >= y + 18 && mouseY <= y + 46) {
            int lava = this.menu.getLavaAmount();
            int maxLava = this.menu.getMaxLava();
            Component tooltip = Component.translatable("gui.sandstorm.thermal_generator.lava", lava, maxLava);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }
}
