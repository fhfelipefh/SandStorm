package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.CyborgIncubatorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CyborgIncubatorScreen extends BaseMachineScreen<CyborgIncubatorMenu> {
    public CyborgIncubatorScreen(CyborgIncubatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 26;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:cyborg_incubator_vat";
    }

    @Override
    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 96 && mouseX <= x + 142 && mouseY >= y + 20 && mouseY <= y + 68;
    }

    @Override
    protected void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + imageWidth, y + imageHeight, 0xFA0A0E17);
        extractor.fill(x, y, x + imageWidth, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, 0xFF00E5FF);
        extractor.fill(x, y, x + 1, y + imageHeight, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 1, y, x + imageWidth, y + imageHeight, 0xFF00E5FF);

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 4, y + 1, x + imageWidth - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + imageHeight - 4, x + 4, y + imageHeight - 1, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 4, y + imageHeight - 4, x + imageWidth - 1, y + imageHeight - 1, 0xFF00E5FF);

        extractor.fill(x + 4, y + 4, x + imageWidth - 4, y + 16, 0xDD101824);
        extractor.fill(x + 4, y + 16, x + imageWidth - 4, y + 17, 0x8800E5FF);

        boolean wpt = this.menu.isWptConnected();
        int wptColor = wpt ? 0xFF00E5FF : 0xFF455A64;
        extractor.fill(x + 10, y + 6, x + 20, y + 14, 0xFF05080E);
        extractor.fill(x + 14, y + 7, x + 16, y + 13, wptColor);
        extractor.fill(x + 11, y + 8, x + 13, y + 10, wptColor);
        extractor.fill(x + 17, y + 8, x + 19, y + 10, wptColor);

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            boolean isMachineSlot = slot.index < 6;
            boolean isOutputSlot = slot.index == 5;
            int borderColor = isOutputSlot ? 0xFF00FFCC : (isMachineSlot ? 0xFF00E5FF : 0xFF1E293B);
            int bgColor = isOutputSlot ? 0xDD0D1B2B : (isMachineSlot ? 0xDD0D131F : 0xAA080C14);

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }

        renderFluidTank(extractor);
        renderGestationChamber(extractor);
        renderSynthesisArrow(extractor);
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        super.renderCustomTooltips(extractor, mouseX, mouseY);
        renderTelemetryTooltips(extractor, mouseX, mouseY);
    }

    private void renderFluidTank(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int fx1 = x + 10;
        int fy1 = y + 20;
        int fx2 = x + 22;
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

    private void renderSynthesisArrow(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int ax = x + 144;
        int ay = y + 43;
        int arrowColor = this.menu.isProcessing() ? 0xFF00FF88 : 0xFF00B4D8;

        extractor.fill(ax, ay, ax + 4, ay + 1, arrowColor);
        extractor.fill(ax + 2, ay - 1, ax + 4, ay, arrowColor);
        extractor.fill(ax + 2, ay + 1, ax + 4, ay + 2, arrowColor);
    }

    private void renderGestationChamber(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 96;
        int cy1 = y + 20;
        int cx2 = x + 142;
        int cy2 = y + 68;

        extractor.fill(cx1 - 1, cy1 - 1, cx2 + 1, cy2 + 1, 0xFF00B4D8);
        extractor.fill(cx1, cy1, cx2, cy2, 0xF003111A);

        for (int gy = cy1 + 8; gy < cy2 - 12; gy += 8) {
            extractor.fill(cx1, gy, cx2, gy + 1, 0x2200E5FF);
        }

        int stage = this.menu.getCurrentStage();
        int centerX = (cx1 + cx2) / 2;

        if (stage >= 1) {
            extractor.fill(centerX - 3, cy1 + 14, centerX + 4, cy1 + 20, 0xFFCFD8DC);
            extractor.fill(centerX - 1, cy1 + 20, centerX + 2, cy1 + 34, 0xFF90A4AE);
            extractor.fill(centerX - 5, cy1 + 23, centerX + 6, cy1 + 25, 0xFFB0BEC5);
            extractor.fill(centerX - 4, cy1 + 27, centerX + 5, cy1 + 29, 0xFFB0BEC5);
        }

        if (stage >= 2) {
            extractor.fill(centerX - 6, cy1 + 23, centerX - 4, cy1 + 32, 0xFFFF4081);
            extractor.fill(centerX + 5, cy1 + 23, centerX + 7, cy1 + 32, 0xFFFF4081);
            extractor.fill(centerX - 2, cy1 + 24, centerX + 3, cy1 + 30, 0x88FF4081);
        }

        if (stage >= 3) {
            int brainPulse = (int) ((System.currentTimeMillis() / 250) % 2);
            int brainColor = brainPulse == 0 ? 0xFFE040FB : 0xFFD500F9;
            extractor.fill(centerX - 2, cy1 + 15, centerX + 3, cy1 + 19, brainColor);
        }

        if (stage >= 4) {
            int time = (int) (System.currentTimeMillis() / 80);
            int bubbleOffset = time % 24;
            extractor.fill(centerX - 8, cy2 - 14 - bubbleOffset, centerX - 6, cy2 - 12 - bubbleOffset, 0xFF80D8FF);
            extractor.fill(centerX + 7, cy2 - 14 - ((bubbleOffset + 12) % 24), centerX + 9, cy2 - 12 - ((bubbleOffset + 12) % 24), 0xFF80D8FF);
        }

        long scanTime = (System.currentTimeMillis() / 35) % (cy2 - cy1);
        int scanY = cy1 + (int) scanTime;
        extractor.fill(cx1, scanY, cx2, scanY + 1, 0x5500E5FF);

        String stageText = stage + "/4";
        int stageWidth = this.font.width(stageText);
        extractor.text(this.font, Component.literal(stageText), cx1 + (46 - stageWidth) / 2, cy1 + 2, 0xFF00E5FF, false);

        int sync = this.menu.getTissueCompatibility();
        String syncText = sync + "%";
        int syncWidth = this.font.width(syncText);
        int syncColor = sync >= 95 ? 0xFF00FF88 : (sync > 0 ? 0xFFFFB300 : 0xFF78909C);
        extractor.text(this.font, Component.literal(syncText), cx1 + (46 - syncWidth) / 2, cy2 - 12, syncColor, false);

        int px1 = cx1 + 2;
        int py1 = cy2 - 4;
        int px2 = cx2 - 2;
        int py2 = cy2 - 2;
        extractor.fill(px1, py1, px2, py2, 0xFF021B2B);

        int progressScaled = this.menu.getProgressScaled(px2 - px1);
        if (progressScaled > 0) {
            extractor.fill(px1, py1, px1 + progressScaled, py2, 0xFF00E5FF);
        }
    }

    private void renderTelemetryTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 10 && mouseX <= x + 22 && mouseY >= y + 20 && mouseY <= y + 68) {
            Component tooltip = Component.translatable("tooltip.sandstorm.fluid_tank_storage", this.menu.getFluidAmount(), this.menu.getMaxFluid());
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 96 && mouseX <= x + 142 && mouseY >= y + 20 && mouseY <= y + 68) {
            Component stageTitle = Component.translatable("tooltip.sandstorm.cyborg_incubator.stage_" + this.menu.getCurrentStage());
            extractor.setTooltipForNextFrame(this.font, stageTitle, mouseX, mouseY);
        }
    }
}
