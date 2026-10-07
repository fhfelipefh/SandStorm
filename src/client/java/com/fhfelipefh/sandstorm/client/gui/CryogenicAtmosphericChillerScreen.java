package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.CryogenicAtmosphericChillerMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CryogenicAtmosphericChillerScreen extends AbstractContainerScreen<CryogenicAtmosphericChillerMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public CryogenicAtmosphericChillerScreen(CryogenicAtmosphericChillerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 10;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 74;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderReadings(extractor);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, 156, 0xFFBAE6FD);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CHASSIS_WIDTH - 16, 0xFF78909C);
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA07111B);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF38BDF8);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF38BDF8);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF38BDF8);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF38BDF8);

        int energyX = x + 10;
        int energyY = y + 17;
        int energyWidth = 156;
        int energyHeight = 5;
        extractor.fill(energyX - 1, energyY - 1, energyX + energyWidth + 1, energyY + energyHeight + 1, 0xFF1E293B);
        extractor.fill(energyX, energyY, energyX + energyWidth, energyY + energyHeight, 0xFF020617);
        int storedWidth = this.menu.getEnergyScaled(energyWidth);
        if (storedWidth > 0) {
            extractor.fill(energyX, energyY, energyX + storedWidth, energyY + energyHeight, 0xFF38BDF8);
        }

        for (Slot slot : this.menu.slots) {
            int slotX = x + slot.x;
            int slotY = y + slot.y;
            extractor.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF1E293B);
            extractor.fill(slotX, slotY, slotX + 16, slotY + 16, 0xAA080C14);
        }
    }

    private void renderReadings(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;
        Component energy = Component.translatable("gui.sandstorm.chiller.energy", NumberFormat.compact(this.menu.getEnergy()), NumberFormat.compact(this.menu.getMaxEnergy()));
        Component consumption = Component.translatable("gui.sandstorm.chiller.consumption", NumberFormat.compact(this.menu.getEnergyCostPerTick()));
        Component range = Component.translatable("gui.sandstorm.chiller.range", this.menu.getRadius());
        Component status = Component.translatable(this.menu.isActive() ? "gui.sandstorm.chiller.status_active" : "gui.sandstorm.chiller.status_standby");

        drawAdaptiveText(extractor, energy, x + 10, y + 29, 156, 0xFFE0F2FE);
        drawAdaptiveText(extractor, consumption, x + 10, y + 40, 156, 0xFFFDE68A);
        drawAdaptiveText(extractor, range, x + 10, y + 51, 92, 0xFFBAE6FD);
        drawAdaptiveText(extractor, status, x + 104, y + 51, 62, this.menu.isActive() ? 0xFF67E8F9 : 0xFF94A3B8);
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
}
