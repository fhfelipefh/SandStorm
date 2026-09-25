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
        return mouseX >= x + 104 && mouseX <= x + 128 && mouseY >= y + 33 && mouseY <= y + 43;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderMolecularLaserChamber(extractor);
    }

    private void renderMolecularLaserChamber(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 104;
        int cy1 = y + 33;
        int cx2 = x + 128;
        int cy2 = y + 43;

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

        renderCircuitTrace(extractor, x + 42, y + 43, x + 62, y + 25);
        renderCircuitTrace(extractor, x + 42, y + 43, x + 62, y + 43);
        renderCircuitTrace(extractor, x + 42, y + 43, x + 62, y + 61);
        renderCircuitTrace(extractor, x + 78, y + 43, x + 84, y + 43);
    }

    private void renderCircuitTrace(GuiGraphicsExtractor extractor, int x1, int y1, int x2, int y2) {
        int color = this.menu.isProcessing() ? 0xFF00E5FF : 0xFF1C2836;
        extractor.fill(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2) + 1, Math.min(y1, y2) + 1, color);
        extractor.fill(x2, Math.min(y1, y2), x2 + 1, Math.max(y1, y2) + 1, color);
    }
}
