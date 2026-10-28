package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.ElectricArcFurnaceMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ElectricArcFurnaceScreen extends BaseMachineScreen<ElectricArcFurnaceMenu> {
    public ElectricArcFurnaceScreen(ElectricArcFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:electric_arc_furnace";
    }

    @Override
    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 76 && mouseX <= x + 104 && mouseY >= y + 30 && mouseY <= y + 46;
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderInductionChamberPreview(extractor);
    }

    private void renderInductionChamberPreview(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 76;
        int cy1 = y + 28;
        int cx2 = x + 104;
        int cy2 = y + 50;

        extractor.fill(cx1 - 1, cy1 - 1, cx2 + 1, cy2 + 1, 0xFF455A64);
        extractor.fill(cx1, cy1, cx2, cy2, 0xF010151C);

        boolean processing = this.menu.isProcessing();
        int maxProg = this.menu.getMaxProgress();
        int prog = this.menu.getProgress();
        float ratio = maxProg > 0 ? (float) prog / (float) maxProg : 0.0f;

        if (processing) {
            long ticks = System.currentTimeMillis() / 50;
            int arcColor = (ticks % 2 == 0) ? 0xFF00E5FF : 0xFFFFD700;
            extractor.fill(cx1 + 2, cy1 + 2, cx1 + 5, cy2 - 2, 0xFFFF5722);
            extractor.fill(cx2 - 5, cy1 + 2, cx2 - 2, cy2 - 2, 0xFFFF5722);

            int arcY = (int) (cy1 + 6 + (Math.sin(ticks * 0.4) + 1.0) * 4.0);
            extractor.fill(cx1 + 5, arcY, cx2 - 5, arcY + 2, arcColor);
            extractor.fill(cx1 + 10, arcY - 2, cx2 - 10, arcY + 4, 0x5500E5FF);

            int barWidth = (int) (ratio * 24.0f);
            extractor.fill(cx1 + 2, cy2 - 3, cx1 + 2 + barWidth, cy2 - 1, 0xFFFF9100);
        } else if (hasOutputReady()) {
            extractor.fill(cx1 + 4, cy1 + 6, cx2 - 4, cy2 - 6, 0xFF00E676);
            extractor.fill(cx1 + 6, cy1 + 8, cx2 - 6, cy2 - 8, 0xFFB9F6CA);
        }
    }
}

