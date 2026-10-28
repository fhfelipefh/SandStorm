package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.AtmosphericCarbonExtractorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AtmosphericCarbonExtractorScreen extends BaseMachineScreen<AtmosphericCarbonExtractorMenu> {
    public AtmosphericCarbonExtractorScreen(AtmosphericCarbonExtractorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:atmospheric_carbon_extractor";
    }

    @Override
    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 44 && mouseX <= x + 72 && mouseY >= y + 30 && mouseY <= y + 46;
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderElectrolyzerPreview(extractor);
    }

    private void renderElectrolyzerPreview(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 40;
        int cy1 = y + 26;
        int cx2 = x + 72;
        int cy2 = y + 54;

        extractor.fill(cx1 - 1, cy1 - 1, cx2 + 1, cy2 + 1, 0xFF37474F);
        extractor.fill(cx1, cy1, cx2, cy2, 0xF01A1C23);

        boolean processing = this.menu.isProcessing();
        int maxProg = this.menu.getMaxProgress();
        int prog = this.menu.getProgress();
        float ratio = maxProg > 0 ? (float) prog / (float) maxProg : 0.0f;

        if (processing) {
            long ticks = System.currentTimeMillis() / 40;
            int fanOffset = (int) (ticks % 8);
            extractor.fill(cx1 + 4 + fanOffset, cy1 + 2, cx1 + 6 + fanOffset, cy1 + 6, 0xFF00E5FF);

            extractor.fill(cx1 + 3, cy2 - 10, cx2 - 3, cy2 - 2, 0xFFBF360C);
            extractor.fill(cx1 + 4, cy2 - 9, cx2 - 4, cy2 - 3, 0xFFFF7043);

            int depositWidth = (int) (ratio * 20.0f);
            if (depositWidth > 0) {
                extractor.fill(cx1 + 6, cy2 - 7, cx1 + 6 + depositWidth, cy2 - 4, 0xFF212121);
            }

            int bubbleX = (int) (cx1 + 8 + (Math.sin(ticks * 0.2) + 1.0) * 6.0);
            extractor.fill(bubbleX, cy1 + 8, bubbleX + 2, cy1 + 10, 0xAAFFFFFF);
        } else if (hasOutputReady()) {
            extractor.fill(cx1 + 6, cy1 + 8, cx2 - 6, cy2 - 8, 0xFF00E676);
            extractor.fill(cx1 + 8, cy1 + 10, cx2 - 8, cy2 - 10, 0xFFB9F6CA);
        }
    }
}

