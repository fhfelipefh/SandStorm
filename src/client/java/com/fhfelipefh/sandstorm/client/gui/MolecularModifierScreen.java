package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.MolecularModifierMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MolecularModifierScreen extends BaseMachineScreen<MolecularModifierMenu> {
    public MolecularModifierScreen(MolecularModifierMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:molecular_modifier";
    }

    @Override
    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 102 && mouseX <= x + 136 && mouseY >= y + 33 && mouseY <= y + 53;
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderMolecularLaserChamber(extractor);
    }

    private void renderMolecularLaserChamber(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 102;
        int cy1 = y + 33;
        int cx2 = x + 136;
        int cy2 = y + 53;

        extractor.fill(cx1 - 1, cy1 - 1, cx2 + 1, cy2 + 1, 0xFF00E5FF);
        extractor.fill(cx1, cy1, cx2, cy2, 0xF005080E);

        boolean processing = this.menu.isProcessing();
        int maxProg = this.menu.getMaxProgress();
        int prog = this.menu.getProgress();
        float ratio = maxProg > 0 ? (float) prog / (float) maxProg : 0.0f;

        int fillW = (int) ((cx2 - cx1) * ratio);
        if (fillW > 0) {
            extractor.fill(cx1, cy1, cx1 + fillW, cy2, 0xFF00B0FF);
            extractor.fill(cx1, cy1, cx1 + fillW, cy1 + 2, 0xFF80D8FF);
        }

        if (processing) {
            long ticks = System.currentTimeMillis() / 35;
            int beamX = (int) (cx1 + (Math.sin(ticks * 0.25) * 0.5 + 0.5) * (cx2 - cx1 - 2));
            extractor.fill(beamX, cy1 - 4, beamX + 1, cy2 + 4, 0xFF00E5FF);
            extractor.fill(beamX - 1, cy1 + 1, beamX + 2, cy2 - 1, 0x88FFFFFF);
        }

        int traceColor = processing ? 0xFF00E5FF : 0xFF1C2836;
        extractor.fill(x + 49, y + 42, x + 53, y + 44, traceColor);
        extractor.fill(x + 52, y + 24, x + 54, y + 62, traceColor);
        extractor.fill(x + 54, y + 24, x + 57, y + 26, traceColor);
        extractor.fill(x + 54, y + 42, x + 57, y + 44, traceColor);
        extractor.fill(x + 54, y + 60, x + 57, y + 62, traceColor);

        extractor.fill(x + 75, y + 42, x + 79, y + 44, traceColor);
        extractor.fill(x + 97, y + 42, x + 101, y + 44, traceColor);
        extractor.fill(x + 137, y + 42, x + 143, y + 44, traceColor);
        extractor.text(this.font, Component.literal("»"), x + 138, y + 38, traceColor, false);
    }
}
