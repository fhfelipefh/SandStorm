package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.QuantumDiskDriveMenu;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskStorage;
import com.fhfelipefh.sandstorm.content.storage.QuantumStorageCartridgeItem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public class QuantumDiskDriveScreen extends AbstractContainerScreen<QuantumDiskDriveMenu> {

    public QuantumDiskDriveScreen(QuantumDiskDriveMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = 72;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF0E141B);
        extractor.fill(x + 1, y + 1, x + this.imageWidth - 1, y + 14, 0xFF15222E);
        extractor.fill(x + 1, y + 14, x + this.imageWidth - 1, y + 15, 0xFF00E5FF);

        for (int i = 0; i < 8; i++) {
            int col = i % 4;
            int row = i / 4;
            int sx = x + 52 + col * 18;
            int sy = y + 19 + row * 18;
            extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF18232C);
            extractor.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF0B1015);

            ItemStack cartridge = this.menu.getContainer().getItem(i);
            if (!cartridge.isEmpty() && cartridge.getItem() instanceof QuantumStorageCartridgeItem cItem) {
                long stored = QuantumDiskStorage.getTotalItemCount(cartridge);
                int cap = cItem.getTier().getCapacity();
                int pct = cap > 0 ? (int) ((stored * 100) / cap) : 0;
                int barColor = pct >= 100 ? 0xFFFF1744 : (pct >= 80 ? 0xFFFFB300 : 0xFF00E5FF);
                int barH = Math.min(14, (pct * 14) / 100);
                extractor.fill(sx + 15, sy + 16 - barH, sx + 17, sy + 16, barColor);
            }
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                int px = x + 7 + c * 18;
                int py = y + 83 + r * 18;
                extractor.fill(px, py, px + 18, py + 18, 0xFF18232C);
                extractor.fill(px + 1, py + 1, px + 17, py + 17, 0xFF0B1015);
            }
        }

        for (int c = 0; c < 9; c++) {
            int hx = x + 7 + c * 18;
            int hy = y + 141;
            extractor.fill(hx, hy, hx + 18, hy + 18, 0xFF18232C);
            extractor.fill(hx + 1, hy + 1, hx + 17, hy + 17, 0xFF0B1015);
        }

        long totalStored = 0;
        long totalCap = 0;
        for (int i = 0; i < 8; i++) {
            ItemStack stack = this.menu.getContainer().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof QuantumStorageCartridgeItem cItem) {
                totalStored += QuantumDiskStorage.getTotalItemCount(stack);
                totalCap += cItem.getTier().getCapacity();
            }
        }

        String summary = String.format(Locale.ROOT, "Total: %,d / %,d", totalStored, totalCap);
        Component summaryComp = Component.literal(summary);
        int summaryW = this.font.width(summaryComp);
        extractor.text(this.font, summaryComp, x + (this.imageWidth - summaryW) / 2, y + 60, 0x00E5FF, false);

        super.extractRenderState(extractor, mouseX, mouseY, delta);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, 8, 4, 0x00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x90A4AE);
    }

    private void drawAdaptiveText(GuiGraphicsExtractor extractor, Component text, int x, int y, int color) {
        extractor.text(this.font, text, x, y, color, false);
    }
}
