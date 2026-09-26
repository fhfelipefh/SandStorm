package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.SolidStateAccumulatorMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class SolidStateAccumulatorScreen extends AbstractContainerScreen<SolidStateAccumulatorMenu> {
    public SolidStateAccumulatorScreen(SolidStateAccumulatorMenu menu, Inventory playerInventory, Component title) {
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
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, this.imageWidth - 16, 0xFF00E5FF);
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
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();

        int buttonX = this.leftPos + 80;
        int buttonY = this.topPos + 35;
        int buttonW = 30;
        int buttonH = 16;

        if (mx >= buttonX && mx <= buttonX + buttonW && my >= buttonY && my <= buttonY + buttonH) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                return true;
            }
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
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

        int barX = x + 14;
        int barY = y + 20;
        int barW = 148;
        int barH = 10;
        extractor.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF1E293B);
        extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF05080E);

        float pct = this.menu.getChargePercentage();
        int filledW = Math.round(pct * barW);
        if (filledW > 0) {
            int barColor = this.menu.isCharging() ? 0xFF00E676 : (this.menu.isDischarging() ? 0xFFFF9100 : 0xFF00E5FF);
            extractor.fill(barX, barY, barX + filledW, barY + barH, barColor);
        }

        renderSlotFrame(extractor, x + 56, y + 35);
        renderSlotFrame(extractor, x + 116, y + 35);

        int btnX = x + 78;
        int btnY = y + 35;
        extractor.fill(btnX, btnY, btnX + 34, btnY + 16, 0xFF1E293B);
        extractor.fill(btnX + 1, btnY + 1, btnX + 33, btnY + 15, 0xFF0F172A);

        String modeText = switch (this.menu.getMode()) {
            case 0 -> "AUTO";
            case 1 -> "CHG";
            case 2 -> "DIS";
            default -> "MODE";
        };
        int textW = this.font.width(modeText);
        extractor.text(this.font, Component.literal(modeText), btnX + (34 - textW) / 2, btnY + 4, 0xFF00E5FF, false);

        String statusText;
        int statusColor;
        if (this.menu.isCharging()) {
            statusText = "STATUS: RECARREGANDO";
            statusColor = 0xFF00E676;
        } else if (this.menu.isDischarging()) {
            statusText = "STATUS: DESCARGANDO";
            statusColor = 0xFFFF9100;
        } else {
            statusText = "STATUS: BUFFER ESTAVEL";
            statusColor = 0xFF78909C;
        }
        extractor.text(this.font, Component.literal(statusText), x + 14, y + 56, statusColor, false);
    }

    private void renderSlotFrame(GuiGraphicsExtractor extractor, int sx, int sy) {
        extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF334155);
        extractor.fill(sx, sy, sx + 16, sy + 16, 0xFF05080E);
    }

    private void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 14 && mouseX <= x + 162 && mouseY >= y + 20 && mouseY <= y + 30) {
            int stored = this.menu.getStoredEnergy();
            int max = this.menu.getMaxEnergy();
            Component tooltip = Component.literal("§bEnergia: §f" + NumberFormat.compact(stored) + " / " + NumberFormat.compact(max) + " J (" + Math.round(this.menu.getChargePercentage() * 100f) + "%)");
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }

        if (mouseX >= x + 78 && mouseX <= x + 112 && mouseY >= y + 35 && mouseY <= y + 51) {
            List<Component> modeDesc = switch (this.menu.getMode()) {
                case 0 -> List.of(
                        Component.literal("§aModo Automático"),
                        Component.literal("§7Carrega com sol ou calor e"),
                        Component.literal("§7descarrega na tempestade.")
                );
                case 1 -> List.of(
                        Component.literal("§bApenas Carga"),
                        Component.literal("§7Apenas absorve energia da malha.")
                );
                case 2 -> List.of(
                        Component.literal("§6Apenas Descarga"),
                        Component.literal("§7Fornece energia ininterrupta"),
                        Component.literal("§7para a rede WPT.")
                );
                default -> List.of();
            };
            if (!modeDesc.isEmpty()) {
                extractor.setComponentTooltipForNextFrame(this.font, modeDesc, mouseX, mouseY);
            }
        }
    }
}
