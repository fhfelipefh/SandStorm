package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.DewCondenserMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class DewCondenserScreen extends AbstractContainerScreen<DewCondenserMenu> {
    public DewCondenserScreen(DewCondenserMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, this.imageWidth - 24, 0xFF00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, this.imageWidth - 16, 0xFF78909C);
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

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        renderSlotFrames(extractor);
        renderWaterReservoir(extractor);
        renderCondensationModule(extractor);
        renderOutputBadge(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderCustomTooltips(extractor, mouseX, mouseY);
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xF00A0E17);
        extractor.fill(x, y, x + this.imageWidth, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + this.imageHeight - 1, x + this.imageWidth, y + this.imageHeight, 0xFF00E5FF);
        extractor.fill(x, y, x + 1, y + this.imageHeight, 0xFF00E5FF);
        extractor.fill(x + this.imageWidth - 1, y, x + this.imageWidth, y + this.imageHeight, 0xFF00E5FF);
    }

    private void renderSlotFrames(GuiGraphicsExtractor extractor) {
        for (Slot slot : this.menu.slots) {
            int sx = this.leftPos + slot.x;
            int sy = this.topPos + slot.y;
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xFF05080E);
        }
    }

    private void renderWaterReservoir(GuiGraphicsExtractor extractor) {
        int tankX = this.leftPos + 18;
        int tankY = this.topPos + 20;
        int tankW = 16;
        int tankH = 48;

        extractor.fill(tankX - 1, tankY - 1, tankX + tankW + 1, tankY + tankH + 1, 0xFF1E293B);
        extractor.fill(tankX, tankY, tankX + tankW, tankY + tankH, 0xFF05080E);

        float pct = this.menu.getWaterPercentage();
        int filledH = Math.round(pct * tankH);
        if (filledH > 0) {
            extractor.fill(tankX, tankY + tankH - filledH, tankX + tankW, tankY + tankH, 0xFF00B0FF);
        }
    }

    private void renderCondensationModule(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int status = this.menu.getStatus();
        int badgeColor = switch (status) {
            case 0 -> 0xFFFF5252;
            case 1 -> 0xFFFFD740;
            case 2 -> 0xFF00E5FF;
            case 3 -> 0xFF69F0AE;
            default -> 0xFF78909C;
        };

        extractor.fill(x + 88, y + 26, x + 92, y + 30, badgeColor);

        int barX = x + 76;
        int barY = y + 42;
        int barW = 28;
        int barH = 5;

        extractor.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF1E293B);
        extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF05080E);

        int filledW = Math.round(this.menu.getProgressPercentage() * barW);
        if (filledW > 0) {
            extractor.fill(barX, barY, barX + filledW, barY + barH, 0xFF00E5FF);
        }
    }

    private void renderOutputBadge(GuiGraphicsExtractor extractor) {
        int bx = this.leftPos + 54;
        int by = this.topPos + 60;
        int bw = 68;
        int bh = 10;

        extractor.fill(bx, by, bx + bw, by + bh, 0xFF0F172A);
        extractor.fill(bx, by, bx + bw, by + 1, 0xFF00E5FF);
        extractor.fill(bx, by + bh - 1, bx + bw, by + bh, 0xFF00E5FF);
        extractor.fill(bx, by, bx + 1, by + bh, 0xFF00E5FF);
        extractor.fill(bx + bw - 1, by, bx + bw, by + bh, 0xFF00E5FF);

        drawAdaptiveText(extractor, Component.translatable("gui.sandstorm.dew_condenser.output_down"), bx + 2, by + 1, bw - 4, 0xFF00E5FF);
    }

    private void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 16 && mouseX <= x + 34 && mouseY >= y + 20 && mouseY <= y + 68) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.sandstorm.dew_condenser.reservoir"));
            tooltip.add(Component.literal(this.menu.getWater() + " / " + this.menu.getMaxWater() + " mB"));
            tooltip.add(Component.translatable("gui.sandstorm.dew_condenser.bottles", this.menu.getWater() / 250));
            extractor.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 76 && mouseX <= x + 104 && mouseY >= y + 26 && mouseY <= y + 50) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(getStatusComponent(this.menu.getStatus()));
            tooltip.add(Component.literal(Math.round(this.menu.getProgressPercentage() * 100) + "%"));
            extractor.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 50 && mouseX <= x + 126 && mouseY >= y + 58 && mouseY <= y + 72) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.sandstorm.dew_condenser.output_down"));
            tooltip.add(Component.translatable("gui.sandstorm.dew_condenser.output_down_desc"));
            extractor.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }

    private Component getStatusComponent(int status) {
        return switch (status) {
            case 0 -> Component.translatable("gui.sandstorm.dew_condenser.no_sky");
            case 1 -> Component.translatable("gui.sandstorm.dew_condenser.waiting_night");
            case 2 -> Component.translatable("gui.sandstorm.dew_condenser.condensing");
            case 3 -> Component.translatable("gui.sandstorm.dew_condenser.full");
            default -> Component.empty();
        };
    }
}
