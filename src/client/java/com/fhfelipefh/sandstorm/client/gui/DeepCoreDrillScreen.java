package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.DeepCoreDrillMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class DeepCoreDrillScreen extends BaseMachineScreen<DeepCoreDrillMenu> {

    public DeepCoreDrillScreen(DeepCoreDrillMenu menu, Inventory playerInventory, Component title) {
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
            boolean isMachineSlot = slot.index < 8;
            int borderColor = isMachineSlot ? 0xFF00E5FF : 0xFF1E293B;
            int bgColor = isMachineSlot ? 0xDD0D131F : 0xAA080C14;

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }

        renderEnergyMeter(extractor, x + 14, y + 20, 14, 52);
        renderFluidTank(extractor, x + 72, y + 20, 14, 52);
        renderDrillProgress(extractor, x + 92, y + 40, 10, 20);
    }

    private void renderFluidTank(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF05080E);

        int fluidScaled = this.menu.getFluidScaled(height);
        if (fluidScaled > 0) {
            extractor.fill(x + 1, y + height - fluidScaled, x + width - 1, y + height, 0xFFD97706);
        }
    }

    private void renderDrillProgress(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF080C14);

        int progressScaled = this.menu.getProgressScaled(height);
        if (progressScaled > 0) {
            extractor.fill(x + 1, y + height - progressScaled, x + width - 1, y + height, 0xFF00E5FF);
        }
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 13 && mouseX <= x + 29 && mouseY >= y + 19 && mouseY <= y + 73) {
            String energy = formatCompact(this.menu.getEnergy());
            String maxEnergy = formatCompact(this.menu.getMaxEnergy());
            Component tooltip = Component.literal("§b" + energy + " / " + maxEnergy + " J");
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 71 && mouseX <= x + 87 && mouseY >= y + 19 && mouseY <= y + 73) {
            Component tooltip = Component.literal("§6Fluido Fóssil: §f" + this.menu.getFluidAmount() + " / " + this.menu.getMaxFluid() + " mB");
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 91 && mouseX <= x + 103 && mouseY >= y + 39 && mouseY <= y + 61) {
            String status = this.menu.isDrilling() ? "§a[PERFURANDO O MANTO]" : "§7[PARADO]";
            int maxProg = this.menu.getMaxProgress();
            int pct = maxProg > 0 ? (this.menu.getProgress() * 100 / maxProg) : 0;
            Component tooltip = Component.literal("§bProgresso: §f" + pct + "% " + status);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 8 && mouseX <= x + 22 && mouseY >= y + 4 && mouseY <= y + 16) {
            String wptStatus = this.menu.isWptConnected() ? "§aWPT Online" : "§7WPT Offline";
            Component tooltip = Component.literal(wptStatus);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }
}
