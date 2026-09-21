package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.MachineMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public abstract class BaseMachineScreen<T extends AbstractContainerMenu & MachineMenu> extends AbstractContainerScreen<T> {

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    protected static String formatCompact(long value) {
        return NumberFormat.compact(value);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 28;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderCustomTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int maxTitleWidth = this.imageWidth - this.titleLabelX - 6;
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, maxTitleWidth, 0xFF00E5FF);
        int maxInvWidth = this.imageWidth - this.inventoryLabelX - 6;
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, maxInvWidth, 0xFF78909C);
    }

    private void drawAdaptiveText(GuiGraphicsExtractor extractor, Component text, float x, float y, float maxPixelWidth, int color) {
        int textWidth = this.font.width(text);
        if (textWidth <= maxPixelWidth || maxPixelWidth <= 0) {
            extractor.text(this.font, text, (int) x, (int) y, color, false);
        } else {
            float scale = maxPixelWidth / (float) textWidth;
            float offsetY = (9f - 9f * scale) / 2f;
            extractor.pose().pushMatrix();
            extractor.pose().translate(x, y + offsetY);
            extractor.pose().scale(scale, scale);
            extractor.text(this.font, text, 0, 0, color, false);
            extractor.pose().popMatrix();
        }
    }

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
            boolean isMachineSlot = slot.index < 4;
            int borderColor = isMachineSlot ? 0xFF00E5FF : 0xFF1E293B;
            int bgColor = isMachineSlot ? 0xDD0D131F : 0xAA080C14;

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }

        renderEnergyMeter(extractor, x + 8, y + 19, 16, 26);
        renderProgressBar(extractor, x + 70, y + 39, 36, 12);
    }

    protected void renderEnergyMeter(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF05080E);

        int scaled = this.menu.getEnergyScaled(height);
        if (scaled > 0) {
            int energyColor = 0xFF00E5FF;
            if (this.menu.getEnergy() < this.menu.getMaxEnergy() / 4) {
                energyColor = 0xFFFF1744;
            } else if (this.menu.getEnergy() < this.menu.getMaxEnergy() / 2) {
                energyColor = 0xFFFF9100;
            }
            extractor.fill(x + 1, y + height - scaled, x + width - 1, y + height, energyColor);
        }
    }

    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF080C14);

        int progressWidth = this.menu.getProgressScaled(width - 2);
        if (progressWidth > 0) {
            extractor.fill(x + 1, y + 1, x + 1 + progressWidth, y + height - 1, 0xFF00E5FF);
            extractor.fill(x + progressWidth - 1, y + 1, x + 1 + progressWidth, y + height - 1, 0xFFFFFFFF);
        }

        int arrowY = y + height / 2;
        extractor.fill(x + width - 4, arrowY - 2, x + width - 2, arrowY + 3, 0xFF00E5FF);
    }

    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 7 && mouseX <= x + 25 && mouseY >= y + 18 && mouseY <= y + 46) {
            String energy = formatCompact(this.menu.getEnergy());
            String maxEnergy = formatCompact(this.menu.getMaxEnergy());
            String wptStatus = this.menu.isWptConnected()
                    ? "§a⚡ WPT Ativa"
                    : "§c⚡ Sem WPT";
            Component tooltip = Component.literal("§b" + energy + " / " + maxEnergy + " J " + wptStatus);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 8 && mouseX <= x + 22 && mouseY >= y + 4 && mouseY <= y + 16) {
            String wptStatus = this.menu.isWptConnected()
                    ? "§aWPT Online"
                    : "§7WPT Offline";
            Component tooltip = Component.literal(wptStatus);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 69 && mouseX <= x + 107 && mouseY >= y + 38 && mouseY <= y + 52) {
            int maxProg = this.menu.getMaxProgress();
            int pct = maxProg > 0 ? (this.menu.getProgress() * 100 / maxProg) : 0;
            String status = this.menu.isProcessing() ? " §a[PROCESSANDO]" : " §7[EM ESPERA]";
            Component tooltip = Component.literal("§bProgresso: §f" + pct + "%" + status);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }
}
