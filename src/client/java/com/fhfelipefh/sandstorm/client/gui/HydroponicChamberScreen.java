package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.HydroponicChamberMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HydroponicChamberScreen extends BaseMachineScreen<HydroponicChamberMenu> {
    public HydroponicChamberScreen(HydroponicChamberMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderChamberPreview(extractor);
    }

    private void renderChamberPreview(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 76;
        int cy1 = y + 24;
        int cx2 = x + 106;
        int cy2 = y + 64;

        extractor.fill(cx1 - 1, cy1 - 1, cx2 + 1, cy2 + 1, 0xFFBA68C8);
        extractor.fill(cx1, cy1, cx2, cy2, 0xF0120B1C);

        for (int gy = cy1 + 6; gy < cy2 - 4; gy += 6) {
            extractor.fill(cx1 + 2, gy, cx2 - 2, gy + 1, 0x33BA68C8);
        }

        boolean processing = this.menu.isProcessing();
        int maxProg = this.menu.getMaxProgress();
        int prog = this.menu.getProgress();
        float ratio = maxProg > 0 ? (float) prog / (float) maxProg : 0.0f;

        if (processing) {
            int fluidH = 10;
            extractor.fill(cx1 + 2, cy2 - fluidH, cx2 - 2, cy2 - 1, 0x880288D1);

            int plantH = Math.max(3, (int) (ratio * 24.0f));
            int plantTop = cy2 - 4 - plantH;
            extractor.fill(cx1 + 14, plantTop, cx2 - 14, cy2 - 4, 0xFF4CAF50);
            extractor.fill(cx1 + 10, plantTop + 4, cx2 - 10, plantTop + 6, 0xFF81C784);

            long ticks = System.currentTimeMillis() / 80;
            int uvAlpha = (int) (180 + Math.sin(ticks * 0.15) * 50);
            int uvColor = (uvAlpha << 24) | 0xCE93D8;
            extractor.fill(cx1 + 2, cy1 + 2, cx2 - 2, cy1 + 4, uvColor);
        } else if (this.menu.slots.get(3).hasItem()) {
            int plantTop = cy2 - 28;
            extractor.fill(cx1 + 14, plantTop, cx2 - 14, cy2 - 4, 0xFF66BB6A);
            extractor.fill(cx1 + 9, plantTop + 6, cx2 - 9, plantTop + 9, 0xFFA5D6A7);
        }
    }
}
