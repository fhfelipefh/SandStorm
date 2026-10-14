package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.IrrigationPumpMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class IrrigationPumpScreen extends BaseMachineScreen<IrrigationPumpMenu> {
    public IrrigationPumpScreen(IrrigationPumpMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:irrigation_pump";
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    protected void renderEnergyMeter(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderFluidAndNutrientTanks(extractor);
        renderDashboardInfo(extractor);
    }

    private void renderFluidAndNutrientTanks(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int waterX = x + 12;
        int waterY = y + 20;
        int tankW = 10;
        int waterH = 48;

        extractor.fill(waterX - 1, waterY - 1, waterX + tankW + 1, waterY + waterH + 1, 0xFF1E293B);
        extractor.fill(waterX, waterY, waterX + tankW, waterY + waterH, 0xFF05080E);
        int waterScaled = this.menu.getWaterScaled(waterH - 2);
        if (waterScaled > 0) {
            extractor.fill(waterX + 1, waterY + waterH - 1 - waterScaled, waterX + tankW - 1, waterY + waterH - 1, 0xFF00E5FF);
        }

        int rightX = x + 154;
        int nutrY = y + 20;
        int nutrH = 22;
        extractor.fill(rightX - 1, nutrY - 1, rightX + tankW + 1, nutrY + nutrH + 1, 0xFF1E293B);
        extractor.fill(rightX, nutrY, rightX + tankW, nutrY + nutrH, 0xFF05080E);
        int nutrScaled = this.menu.getNutrientsScaled(nutrH - 2);
        if (nutrScaled > 0) {
            extractor.fill(rightX + 1, nutrY + nutrH - 1 - nutrScaled, rightX + tankW - 1, nutrY + nutrH - 1, 0xFF2ECC71);
        }

        int energyY = y + 46;
        int energyH = 22;
        extractor.fill(rightX - 1, energyY - 1, rightX + tankW + 1, energyY + energyH + 1, 0xFF1E293B);
        extractor.fill(rightX, energyY, rightX + tankW, energyY + energyH, 0xFF05080E);
        int energyScaled = this.menu.getEnergyScaled(energyH - 2);
        if (energyScaled > 0) {
            int energyColor = 0xFF00E5FF;
            if (this.menu.getEnergy() < this.menu.getMaxEnergy() / 4) {
                energyColor = 0xFFFF1744;
            } else if (this.menu.getEnergy() < this.menu.getMaxEnergy() / 2) {
                energyColor = 0xFFFF9100;
            }
            extractor.fill(rightX + 1, energyY + energyH - 1 - energyScaled, rightX + tankW - 1, energyY + energyH - 1, energyColor);
        }
    }

    private void renderDashboardInfo(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx = x + 48;
        int cy = y + 20;
        int cw = 80;
        int ch = 48;

        extractor.fill(cx - 1, cy - 1, cx + cw + 1, cy + ch + 1, 0xFF1E293B);
        extractor.fill(cx, cy, cx + cw, cy + ch, 0xEE080D16);
        extractor.fill(cx + 1, cy + 1, cx + cw - 1, cy + 2, 0x3300E5FF);

        boolean active = this.menu.isActive();
        Component statusText = Component.literal(active ? "● ATIVO" : "○ STANDBY");
        int statusColor = active ? 0xFF00E676 : 0xFF78909C;
        int sw = this.font.width(statusText);
        float sx = (cx + cw / 2.0f) - (sw / 2.0f);
        drawAdaptiveText(extractor, statusText, sx, cy + 5, cw - 8, statusColor);

        extractor.fill(cx + 8, cy + 16, cx + cw - 8, cy + 17, 0x3300E5FF);

        int count = this.menu.getConnectedSprinklers();
        int radius = this.menu.getPressureRadius();

        Component spText = Component.translatable("gui.sandstorm.irrigation_pump.sprinklers", count);
        int spW = this.font.width(spText);
        float spX = (cx + cw / 2.0f) - (spW / 2.0f);
        drawAdaptiveText(extractor, spText, spX, cy + 21, cw - 8, 0xFF80D8FF);

        Component reachText = Component.translatable("gui.sandstorm.irrigation_pump.reach", radius);
        int rW = this.font.width(reachText);
        float rX = (cx + cw / 2.0f) - (rW / 2.0f);
        drawAdaptiveText(extractor, reachText, rX, cy + 34, cw - 8, 0xFFA5D6A7);
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 11 && mouseX <= x + 23 && mouseY >= y + 19 && mouseY <= y + 69) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.irrigation_pump.water_tank"),
                    Component.translatable("gui.sandstorm.irrigation_pump.water", this.menu.getWaterAmount(), this.menu.getMaxWater())
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 153 && mouseX <= x + 165 && mouseY >= y + 19 && mouseY <= y + 43) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.irrigation_pump.nutrient_tank"),
                    Component.translatable("gui.sandstorm.irrigation_pump.nutrients", this.menu.getNutrients(), this.menu.getMaxNutrients())
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 153 && mouseX <= x + 165 && mouseY >= y + 45 && mouseY <= y + 69) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.irrigation_pump.energy", formatCompact(this.menu.getEnergy()), formatCompact(this.menu.getMaxEnergy()))
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 48 && mouseX <= x + 128 && mouseY >= y + 19 && mouseY <= y + 69) {
            List<Component> tip = List.of(
                    Component.literal("§bMonitor de Irrigação"),
                    Component.literal(this.menu.isActive() ? "§aStatus: Ativo" : "§7Status: Em Espera"),
                    Component.translatable("gui.sandstorm.irrigation_pump.sprinklers", this.menu.getConnectedSprinklers()),
                    Component.translatable("gui.sandstorm.irrigation_pump.reach", this.menu.getPressureRadius())
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else {
            super.renderCustomTooltips(extractor, mouseX, mouseY);
        }
    }
}
