package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.CyborgIncubatorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


public class CyborgIncubatorScreen extends BaseMachineScreen<CyborgIncubatorMenu> {
    public CyborgIncubatorScreen(CyborgIncubatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:cyborg_incubator_vat";
    }

    @Override
    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 68 && mouseX <= x + 134 && mouseY >= y + 70 && mouseY <= y + 76;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderGestationChamber(extractor);
        renderFluidTank(extractor);
        renderProgressBar(extractor);
        renderTelemetryTooltips(extractor, mouseX, mouseY);
    }

    private void renderGestationChamber(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 68;
        int cy1 = y + 16;
        int cx2 = x + 134;
        int cy2 = y + 68;

        extractor.fill(cx1 - 1, cy1 - 1, cx2 + 1, cy2 + 1, 0xFF00B4D8);
        extractor.fill(cx1, cy1, cx2, cy2, 0xF003111A);

        for (int gy = cy1 + 8; gy < cy2; gy += 10) {
            extractor.fill(cx1, gy, cx2, gy + 1, 0x2200E5FF);
        }

        int stage = this.menu.getCurrentStage();
        int centerX = (cx1 + cx2) / 2;

        if (stage >= 1) {
            extractor.fill(centerX - 3, cy1 + 8, centerX + 4, cy1 + 15, 0xFFCFD8DC);
            extractor.fill(centerX - 1, cy1 + 15, centerX + 2, cy1 + 35, 0xFF90A4AE);
            extractor.fill(centerX - 6, cy1 + 20, centerX + 7, cy1 + 22, 0xFFB0BEC5);
            extractor.fill(centerX - 5, cy1 + 24, centerX + 6, cy1 + 26, 0xFFB0BEC5);
            extractor.fill(centerX - 4, cy1 + 28, centerX + 5, cy1 + 30, 0xFFB0BEC5);
            extractor.fill(centerX - 8, cy1 + 18, centerX - 6, cy1 + 33, 0xFF78909C);
            extractor.fill(centerX + 7, cy1 + 18, centerX + 9, cy1 + 33, 0xFF78909C);
            extractor.fill(centerX - 4, cy1 + 35, centerX - 2, cy1 + 48, 0xFF78909C);
            extractor.fill(centerX + 3, cy1 + 35, centerX + 5, cy1 + 48, 0xFF78909C);
        }

        if (stage >= 2) {
            extractor.fill(centerX - 7, cy1 + 21, centerX - 5, cy1 + 32, 0xFFFF4081);
            extractor.fill(centerX + 6, cy1 + 21, centerX + 8, cy1 + 32, 0xFFFF4081);
            extractor.fill(centerX - 3, cy1 + 36, centerX - 1, cy1 + 47, 0xFFE91E63);
            extractor.fill(centerX + 2, cy1 + 36, centerX + 4, cy1 + 47, 0xFFE91E63);
            extractor.fill(centerX - 2, cy1 + 22, centerX + 3, cy1 + 29, 0x88FF4081);
        }

        if (stage >= 3) {
            int brainPulse = (int) ((System.currentTimeMillis() / 250) % 2);
            int brainColor = brainPulse == 0 ? 0xFFE040FB : 0xFFD500F9;
            extractor.fill(centerX - 2, cy1 + 9, centerX + 3, cy1 + 14, brainColor);
            extractor.fill(centerX - 1, cy1 + 7, centerX + 2, cy1 + 9, 0xFF00E5FF);
        }

        if (stage >= 4) {
            int time = (int) (System.currentTimeMillis() / 80);
            int bubbleOffset1 = (time) % 40;
            int bubbleOffset2 = (time + 20) % 40;
            extractor.fill(centerX - 10, cy2 - 4 - bubbleOffset1, centerX - 8, cy2 - 2 - bubbleOffset1, 0xFF80D8FF);
            extractor.fill(centerX + 9, cy2 - 4 - bubbleOffset2, centerX + 11, cy2 - 2 - bubbleOffset2, 0xFF80D8FF);
            extractor.fill(centerX - 1, cy1 + 16, centerX + 2, cy1 + 35, 0xAA00E5FF);
        }

        long scanTime = (System.currentTimeMillis() / 35) % (cy2 - cy1);
        int scanY = cy1 + (int) scanTime;
        extractor.fill(cx1, scanY, cx2, scanY + 1, 0x6600E5FF);

        String stageText = "STAGE " + stage + "/4";
        extractor.text(this.font, Component.literal(stageText), cx1 + 2, cy1 + 2, 0xFF00E5FF, false);

        int sync = this.menu.getTissueCompatibility();
        String syncText = sync + "%";
        int syncColor = sync >= 95 ? 0xFF00FF88 : (sync > 0 ? 0xFFFFB300 : 0xFF78909C);
        extractor.text(this.font, Component.literal(syncText), cx2 - this.font.width(syncText) - 2, cy1 + 2, syncColor, false);
    }

    private void renderProgressBar(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int px1 = x + 68;
        int py1 = y + 70;
        int px2 = x + 134;
        int py2 = y + 75;

        extractor.fill(px1 - 1, py1 - 1, px2 + 1, py2 + 1, 0xFF0077B6);
        extractor.fill(px1, py1, px2, py2, 0xFF021B2B);

        int progressScaled = this.menu.getProgressScaled(px2 - px1);
        if (progressScaled > 0) {
            extractor.fill(px1, py1, px1 + progressScaled, py2, 0xFF00E5FF);
            extractor.fill(px1, py1, px1 + progressScaled, py1 + 1, 0xFFB9F6CA);
        }
    }

    private void renderFluidTank(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int fx1 = x + 162;
        int fy1 = y + 16;
        int fx2 = x + 171;
        int fy2 = y + 68;

        extractor.fill(fx1 - 1, fy1 - 1, fx2 + 1, fy2 + 1, 0xFF0096C7);
        extractor.fill(fx1, fy1, fx2, fy2, 0xF0021526);

        int fluidScaled = this.menu.getFluidScaled(fy2 - fy1);
        if (fluidScaled > 0) {
            int fillTop = fy2 - fluidScaled;
            extractor.fill(fx1, fillTop, fx2, fy2, 0xFF00B4D8);
            extractor.fill(fx1, fillTop, fx2, fillTop + 1, 0xFF90E0EF);
        }
    }

    private void renderTelemetryTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 162 && mouseX <= x + 171 && mouseY >= y + 16 && mouseY <= y + 68) {
            Component tooltip = Component.translatable("tooltip.sandstorm.fluid_tank_storage", this.menu.getFluidAmount(), this.menu.getMaxFluid());
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 68 && mouseX <= x + 134 && mouseY >= y + 16 && mouseY <= y + 68) {
            Component stageTitle = Component.translatable("tooltip.sandstorm.cyborg_incubator.stage_" + this.menu.getCurrentStage());
            extractor.setTooltipForNextFrame(this.font, stageTitle, mouseX, mouseY);
        }
    }
}
