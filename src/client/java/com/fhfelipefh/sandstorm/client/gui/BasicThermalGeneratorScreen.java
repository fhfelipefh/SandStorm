package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.block.entity.BasicThermalGeneratorBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.BasicThermalGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class BasicThermalGeneratorScreen extends BaseMachineScreen<BasicThermalGeneratorMenu> {

    public BasicThermalGeneratorScreen(BasicThermalGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderChassis(GuiGraphicsExtractor extractor) {
        super.renderChassis(extractor);
        int x = this.leftPos;
        int y = this.topPos;
        renderFlameIndicator(extractor, x + 81, y + 25, 14, 14);
        renderTelemetryCard(extractor, x + 108, y + 23, 54, 44);
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    protected void renderEnergyMeter(GuiGraphicsExtractor extractor, int unusedX, int unusedY, int unusedW, int unusedH) {
        int x = this.leftPos + 16;
        int y = this.topPos + 23;
        int width = 14;
        int height = 44;

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
            extractor.fill(x + 1, y + height - scaled, x + width - 1, y + height - scaled + 1, 0xFFFFFFFF);
        }
    }

    private void renderFlameIndicator(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF05080E);
        if (this.menu.isProcessing()) {
            int progress = this.menu.getProgressScaled(height);
            int flameH = Math.max(2, Math.min(height, progress > 0 ? progress : height));
            int flameTop = y + height - flameH;
            extractor.fill(x + 1, flameTop, x + width - 1, y + height, 0xFFFF6D00);
            extractor.fill(x + 3, y + height - (flameH * 2 / 3), x + width - 3, y + height, 0xFFFFD600);
            extractor.fill(x + 5, y + height - (flameH / 3), x + width - 5, y + height, 0xFFFFFFFF);
        } else {
            extractor.fill(x + 4, y + 5, x + width - 4, y + height - 2, 0xFF1E293B);
            extractor.fill(x + 6, y + 3, x + width - 6, y + 5, 0xFF1E293B);
        }
    }

    private void renderTelemetryCard(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xDD0D131F);

        extractor.fill(x, y, x + width, y + 10, 0xEE162234);
        extractor.fill(x, y + 10, x + width, y + 11, 0x8800E5FF);

        drawAdaptiveText(extractor, Component.literal("GERADOR"), x + 4, y + 2, width - 8, 0xFF00E5FF);

        boolean active = this.menu.isProcessing();
        Component statusText = active ? Component.literal("§a● ATIVO") : Component.literal("§7○ ESPERA");
        drawAdaptiveText(extractor, statusText, x + 4, y + 14, width - 8, 0xFFFFFFFF);

        Component rateText = active ? Component.literal("§b+16 J/t") : Component.literal("§80 J/t");
        drawAdaptiveText(extractor, rateText, x + 4, y + 24, width - 8, 0xFFB0BEC5);

        Component wptText = Component.literal("§3⚡ 14m");
        drawAdaptiveText(extractor, wptText, x + 4, y + 34, width - 8, 0xFF4DD0E1);
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 15 && mouseX <= x + 31 && mouseY >= y + 22 && mouseY <= y + 68) {
            List<Component> tip = List.of(
                    Component.literal("§bBuffer: §f" + formatCompact(this.menu.getEnergy()) + " / " + formatCompact(this.menu.getMaxEnergy()) + " J"),
                    Component.literal("§7Taxa Máx: §f" + BasicThermalGeneratorBlockEntity.MAX_TRANSFER + " J/t"),
                    Component.literal(this.menu.isProcessing() ? "§a⚡ Transmissão WPT Ativa" : "§7Sem transmissão ativa")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 80 && mouseX <= x + 96 && mouseY >= y + 24 && mouseY <= y + 40) {
            List<Component> tip;
            if (this.menu.isProcessing()) {
                int seconds = Math.max(1, this.menu.getProgress() / 20);
                tip = List.of(
                        Component.literal("§6Combustão Ativa"),
                        Component.literal("§7Tempo restante: §f" + seconds + "s"),
                        Component.literal("§bGerando: §a+16 J/t")
                );
            } else {
                tip = List.of(
                        Component.literal("§7Gerador em Espera"),
                        Component.literal("§8Insira carvão ou madeira abaixo")
                );
            }
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 107 && mouseX <= x + 163 && mouseY >= y + 22 && mouseY <= y + 68) {
            List<Component> tip = List.of(
                    Component.literal("§bGerador Térmico Básico"),
                    Component.literal("§7Converte combustíveis sólidos"),
                    Component.literal("§7Geração: §a16 J/t"),
                    Component.literal("§3Alcance WPT: §fRaio de 14 blocos")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else {
            super.renderCustomTooltips(extractor, mouseX, mouseY);
        }
    }
}
