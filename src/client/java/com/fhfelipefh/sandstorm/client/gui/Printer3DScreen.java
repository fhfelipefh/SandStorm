package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.Printer3DMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class Printer3DScreen extends BaseMachineScreen<Printer3DMenu> {
    public Printer3DScreen(Printer3DMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:printer_3d";
    }

    @Override
    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 66 && mouseX <= x + 110 && mouseY >= y + 18 && mouseY <= y + 58;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderPrinterChamberPreview(extractor);
    }

    private void renderPrinterChamberPreview(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 66;
        int cy1 = y + 18;
        int cx2 = x + 110;
        int cy2 = y + 58;

        extractor.fill(cx1 - 1, cy1 - 1, cx2 + 1, cy2 + 1, 0xFF00E5FF);
        extractor.fill(cx1, cy1, cx2, cy2, 0xF005080E);

        for (int gy = cy1 + 8; gy < cy2 - 4; gy += 8) {
            extractor.fill(cx1 + 2, gy, cx2 - 2, gy + 1, 0x2200E5FF);
        }
        for (int gx = cx1 + 8; gx < cx2 - 4; gx += 8) {
            extractor.fill(gx, cy1 + 2, gx + 1, cy2 - 4, 0x2200E5FF);
        }

        int bedY = cy2 - 4;
        extractor.fill(cx1 + 4, bedY, cx2 - 4, bedY + 3, 0xFF1C2836);
        extractor.fill(cx1 + 6, bedY, cx2 - 6, bedY + 1, 0xFF00E5FF);

        boolean processing = this.menu.isProcessing();
        int maxProg = this.menu.getMaxProgress();
        int prog = this.menu.getProgress();
        float ratio = maxProg > 0 ? (float) prog / (float) maxProg : 0.0f;

        if (processing) {
            int pieceH = Math.max(2, (int) (ratio * 16.0f));
            int pieceTop = bedY - pieceH;
            extractor.fill(cx1 + 14, pieceTop, cx2 - 14, bedY, 0xFF00B0FF);
            extractor.fill(cx1 + 16, pieceTop, cx2 - 16, pieceTop + 1, 0xFF80D8FF);

            long ticks = System.currentTimeMillis() / 40;
            int toolX = (int) (cx1 + 22 + Math.sin(ticks * 0.18) * 7.0);
            int toolY = pieceTop - 5;

            extractor.fill(cx1 + 2, toolY, cx2 - 2, toolY + 1, 0xFF455A64);
            extractor.fill(toolX - 3, toolY - 2, toolX + 3, toolY + 2, 0xFF263238);
            extractor.fill(toolX - 1, toolY + 2, toolX + 1, toolY + 4, 0xFFFFAB00);
            extractor.fill(toolX, toolY + 4, toolX + 1, pieceTop, 0xFF00E5FF);
            extractor.fill(toolX - 1, pieceTop, toolX + 2, pieceTop + 1, 0xFFFFFFFF);
        } else if (hasOutputReady()) {
            int pieceTop = bedY - 14;
            extractor.fill(cx1 + 14, pieceTop, cx2 - 14, bedY, 0xFF00E676);
            extractor.fill(cx1 + 16, pieceTop, cx2 - 16, pieceTop + 1, 0xFFB9F6CA);
        }
    }
}
