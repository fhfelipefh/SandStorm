package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.SandSuctionQuarryMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class SandSuctionQuarryScreen extends AbstractContainerScreen<SandSuctionQuarryMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public SandSuctionQuarryScreen(SandSuctionQuarryMenu menu, Inventory playerInventory, Component title) {
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
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        if (mx >= x + 28 && mx <= x + 42 && my >= y + 18 && my <= y + 31) {
            triggerButton(SandSuctionQuarryMenu.BUTTON_DEC_8);
            return true;
        }
        if (mx >= x + 44 && mx <= x + 56 && my >= y + 18 && my <= y + 31) {
            triggerButton(SandSuctionQuarryMenu.BUTTON_DEC_1);
            return true;
        }
        if (mx >= x + 76 && mx <= x + 88 && my >= y + 18 && my <= y + 31) {
            triggerButton(SandSuctionQuarryMenu.BUTTON_INC_1);
            return true;
        }
        if (mx >= x + 90 && mx <= x + 104 && my >= y + 18 && my <= y + 31) {
            triggerButton(SandSuctionQuarryMenu.BUTTON_INC_8);
            return true;
        }
        if (mx >= x + 28 && mx <= x + 104 && my >= y + 34 && my <= y + 48) {
            triggerButton(SandSuctionQuarryMenu.BUTTON_TOGGLE_PAUSE);
            return true;
        }
        if (mx >= x + 28 && mx <= x + 104 && my >= y + 51 && my <= y + 65) {
            triggerButton(SandSuctionQuarryMenu.BUTTON_RESET_SCAN);
            return true;
        }

        return super.mouseClicked(event, isDouble);
    }

    private void triggerButton(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderControlsAndStatus(extractor);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, 156, 0xFF00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CHASSIS_WIDTH - 16, 0xFF78909C);
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

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA070B14);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        int ex = x + 10;
        int ey = y + 18;
        int ew = 12;
        int eh = 48;
        extractor.fill(ex - 1, ey - 1, ex + ew + 1, ey + eh + 1, 0xFF1E293B);
        extractor.fill(ex, ey, ex + ew, ey + eh, 0xFF05080E);
        int energyH = this.menu.getEnergyScaled(eh);
        if (energyH > 0) {
            extractor.fill(ex, ey + eh - energyH, ex + ew, ey + eh, 0xFF00E5FF);
        }

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            boolean isBuffer = slot.index < SandSuctionQuarryMenu.CONTAINER_SIZE;
            int borderColor = isBuffer ? 0xFF00E5FF : 0xFF1E293B;
            int bgColor = isBuffer ? 0xDD0D131F : 0xAA080C14;

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }
    }

    private void renderControlsAndStatus(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        renderButton(extractor, x + 28, y + 18, 14, 13, "-8", 0xFF00E5FF);
        renderButton(extractor, x + 44, y + 18, 12, 13, "-1", 0xFF00E5FF);
        String radiusText = "R:" + this.menu.getRadius();
        extractor.text(this.font, Component.literal(radiusText), x + 58, y + 21, 0xFFFFFFFF, false);
        renderButton(extractor, x + 76, y + 18, 12, 13, "+1", 0xFF00E5FF);
        renderButton(extractor, x + 90, y + 18, 14, 13, "+8", 0xFF00E5FF);

        boolean paused = this.menu.isPaused();
        String pauseLabel = paused ? "LIGAR" : "PAUSAR";
        int pauseColor = paused ? 0xFF69F0AE : 0xFFFF5252;
        renderButton(extractor, x + 28, y + 34, 76, 14, pauseLabel, pauseColor);

        renderButton(extractor, x + 28, y + 51, 76, 14, "REINICIAR", 0xFF78909C);

        int status = this.menu.getStatus();
        String statusLabel = switch (status) {
            case 0 -> "PAUSADO";
            case 1 -> "SUGANDO (Y:" + this.menu.getScanY() + ")";
            case 2 -> "BUFFER CHEIO";
            case 3 -> "CONCLUIDO";
            default -> "OCIOSO";
        };
        int statusColor = switch (status) {
            case 0 -> 0xFFFFD54F;
            case 1 -> 0xFF00E5FF;
            case 2 -> 0xFFFF5252;
            case 3 -> 0xFF69F0AE;
            default -> 0xFF78909C;
        };
        extractor.text(this.font, Component.literal(statusLabel), x + 28, y + 68, statusColor, false);
    }

    private void renderButton(GuiGraphicsExtractor extractor, int bx, int by, int bw, int bh, String label, int textColor) {
        extractor.fill(bx, by, bx + bw, by + bh, 0xDD0D131F);
        extractor.fill(bx, by, bx + bw, by + 1, textColor);
        extractor.fill(bx, by + bh - 1, bx + bw, by + bh, textColor);
        extractor.fill(bx, by, bx + 1, by + bh, textColor);
        extractor.fill(bx + bw - 1, by, bx + bw, by + bh, textColor);

        Component text = Component.literal(label);
        int tw = this.font.width(text);
        int tx = bx + (bw - tw) / 2;
        int ty = by + (bh - 8) / 2;
        extractor.text(this.font, text, tx, ty, textColor, false);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 10 && mouseX <= x + 22 && mouseY >= y + 18 && mouseY <= y + 66) {
            List<Component> energyTooltip = List.of(
                    Component.literal("Energia: " + NumberFormat.compact(this.menu.getEnergy()) + " / " + NumberFormat.compact(this.menu.getMaxEnergy()) + " FE"),
                    Component.literal("Consumo: 1.000 FE por bloco")
            );
            extractor.setComponentTooltipForNextFrame(this.font, energyTooltip, mouseX, mouseY);
        } else if (mouseX >= x + 28 && mouseX <= x + 104 && mouseY >= y + 18 && mouseY <= y + 31) {
            int r = this.menu.getRadius();
            int area = r * 2 + 1;
            List<Component> radiusTooltip = List.of(
                    Component.literal("Raio de Escavacao: " + r + " blocos"),
                    Component.literal("Area Horizontal: " + area + "x" + area + " blocos")
            );
            extractor.setComponentTooltipForNextFrame(this.font, radiusTooltip, mouseX, mouseY);
        } else if (mouseX >= x + 28 && mouseX <= x + 104 && mouseY >= y + 34 && mouseY <= y + 48) {
            List<Component> pauseTooltip = List.of(
                    Component.literal("Ligar ou pausar operacao de succao")
            );
            extractor.setComponentTooltipForNextFrame(this.font, pauseTooltip, mouseX, mouseY);
        } else if (mouseX >= x + 28 && mouseX <= x + 104 && mouseY >= y + 51 && mouseY <= y + 65) {
            List<Component> resetTooltip = List.of(
                    Component.literal("Reiniciar varredura do topo (Y=319)")
            );
            extractor.setComponentTooltipForNextFrame(this.font, resetTooltip, mouseX, mouseY);
        }
    }
}

