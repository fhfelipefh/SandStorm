package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.QuantumControllerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Locale;

public class QuantumControllerScreen extends AbstractContainerScreen<QuantumControllerMenu> {

    public QuantumControllerScreen(QuantumControllerMenu menu, Inventory playerInventory, Component title) {
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

        int barX = x + 20;
        int barY = y + 26;
        int barW = 16;
        int barH = 46;

        extractor.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF1E2D3D);
        extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF0A0F14);

        int energy = this.menu.getStoredEnergy();
        int maxEnergy = Math.max(1, this.menu.getMaxEnergy());
        int fillH = (int) (((long) energy * barH) / maxEnergy);
        if (fillH > 0) {
            extractor.fill(barX, barY + barH - fillH, barX + barW, barY + barH, 0xFF00E5FF);
        }

        int infoX = x + 44;
        int infoY = y + 28;
        int nodes = this.menu.getConnectedNodes();
        int drain = this.menu.getConsumptionPerTick();

        extractor.text(this.font, Component.literal(energy > 0 ? "Quantum Matrix: ONLINE" : "Quantum Matrix: OFFLINE"), infoX, infoY, energy > 0 ? 0xFF00E5FF : 0xFFFF1744, false);
        extractor.text(this.font, Component.literal("Nodes: " + nodes), infoX, infoY + 12, 0xFF90A4AE, false);
        extractor.text(this.font, Component.literal("Power: " + (energy > 0 ? "STABLE" : "OFFLINE")), infoX, infoY + 24, energy > 0 ? 0xFF00E676 : 0xFFFF1744, false);
        extractor.text(this.font, Component.literal("Drain: " + drain + " FE/t"), infoX, infoY + 36, 0xFF78909C, false);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                int sx = x + 7 + c * 18;
                int sy = y + 83 + r * 18;
                extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF18232C);
                extractor.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF0B1015);
            }
        }

        for (int c = 0; c < 9; c++) {
            int sx = x + 7 + c * 18;
            int sy = y + 141;
            extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF18232C);
            extractor.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF0B1015);
        }

        super.extractRenderState(extractor, mouseX, mouseY, delta);

        if (mouseX >= barX && mouseX <= barX + barW && mouseY >= barY && mouseY <= barY + barH) {
            List<Component> tooltip = List.of(
                    Component.literal(String.format(Locale.ROOT, "Energy: %,d / %,d FE", energy, maxEnergy)),
                    Component.literal(String.format(Locale.ROOT, "Drain: %d FE/t", drain))
            );
            extractor.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, 8, 4, 0xFF00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, 8, this.inventoryLabelY, 0xFF90A4AE);
    }

    private void drawAdaptiveText(GuiGraphicsExtractor extractor, Component text, int x, int y, int color) {
        extractor.text(this.font, text, x, y, color, false);
    }
}
