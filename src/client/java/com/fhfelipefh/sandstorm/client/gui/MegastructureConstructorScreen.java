package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.MegastructureConstructorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class MegastructureConstructorScreen extends BaseMachineScreen<MegastructureConstructorMenu> {

    public MegastructureConstructorScreen(MegastructureConstructorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + imageWidth, y + imageHeight, 0xFA0A0E17);
        extractor.fill(x, y, x + imageWidth, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + imageHeight, 0xFF00E5FF);
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
            boolean isMachineSlot = slot.index < 18;
            int borderColor = isMachineSlot ? 0xFF00E5FF : 0xFF1E293B;
            int bgColor = isMachineSlot ? 0xDD0D131F : 0xAA080C14;

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }

        renderEnergyMeter(extractor, x + 6, y + 20, 6, 52);

        int cx = x + 54;
        int cy = y + 20;
        int cw = 22;
        int ch = 52;
        extractor.fill(cx - 1, cy - 1, cx + cw + 1, cy + ch + 1, 0xFF1E293B);
        extractor.fill(cx, cy, cx + cw, cy + ch, 0xFF080C14);

        int pct = this.menu.getCompletionPercentage();
        int progressHeight = (pct * (ch - 2)) / 100;
        if (progressHeight > 0) {
            int progColor = this.menu.isDone() ? 0xFF00E676 : 0xFF00E5FF;
            extractor.fill(cx + 1, cy + ch - 1 - progressHeight, cx + cw - 1, cy + ch - 1, progColor);
        }

        if (this.menu.getLaserActive()) {
            extractor.fill(cx + 4, cy + 4, cx + cw - 4, cy + 8, 0xFFFF1744);
        } else if (this.menu.isPausedStorm()) {
            extractor.fill(cx + 4, cy + 4, cx + cw - 4, cy + 8, 0xFFFF9100);
        } else if (this.menu.isBuilding()) {
            extractor.fill(cx + 4, cy + 4, cx + cw - 4, cy + 8, 0xFF00E5FF);
        }
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 5 && mouseX <= x + 13 && mouseY >= y + 19 && mouseY <= y + 73) {
            String energy = formatCompact(this.menu.getEnergy());
            String maxEnergy = formatCompact(this.menu.getMaxEnergy());
            Component tooltip = Component.literal("§b" + energy + " / " + maxEnergy + " J");
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 53 && mouseX <= x + 77 && mouseY >= y + 19 && mouseY <= y + 73) {
            int pct = this.menu.getCompletionPercentage();
            int built = this.menu.getConstructedBlocks();
            int total = this.menu.getTotalBlocks();
            String bp = this.menu.getBlueprintName();
            String status = this.menu.isDone() ? "§a[CONCLUÍDO - CAMPO ATIVO]" : (this.menu.isBuilding() ? "§e[EM CONSTRUÇÃO]" : (this.menu.isPausedStorm() ? "§c[PAUSADO - TEMPESTADE]" : "§7[STANDBY]"));
            Component tooltip = Component.literal("§6" + bp + "\n§bProgresso: §f" + built + "/" + total + " (" + pct + "%)\n" + status);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 8 && mouseX <= x + 22 && mouseY >= y + 4 && mouseY <= y + 16) {
            String wptStatus = this.menu.isWptConnected() ? "§aWPT Online" : "§7WPT Offline";
            Component tooltip = Component.literal(wptStatus);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }
}
