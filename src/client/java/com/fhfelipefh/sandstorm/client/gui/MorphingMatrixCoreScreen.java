package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.MorphingMatrixCoreMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class MorphingMatrixCoreScreen extends AbstractContainerScreen<MorphingMatrixCoreMenu> {

    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 168;

    public MorphingMatrixCoreScreen(MorphingMatrixCoreMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 10;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 75;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        if (mx >= x + 10 && mx <= x + 85 && my >= y + 36 && my <= y + 50) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MorphingMatrixCoreMenu.BUTTON_SCAN);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        if (mx >= x + 91 && mx <= x + 166 && my >= y + 36 && my <= y + 50) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MorphingMatrixCoreMenu.BUTTON_HOLOGRAM);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        if (mx >= x + 10 && mx <= x + 85 && my >= y + 52 && my <= y + 66) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MorphingMatrixCoreMenu.BUTTON_LIQUEFY);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        if (mx >= x + 91 && mx <= x + 166 && my >= y + 52 && my <= y + 66) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MorphingMatrixCoreMenu.BUTTON_SOLIDIFY);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderControlsAndStatus(extractor, mouseX, mouseY);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, 156, 0xFF00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 68, 0xFF78909C);
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

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + 1, x + CHASSIS_WIDTH - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + CHASSIS_HEIGHT - 4, x + 4, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + CHASSIS_HEIGHT - 4, x + CHASSIS_WIDTH - 1, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);

        int slotX = x + 79;
        int slotY = y + 61;
        extractor.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF0A121E);
        extractor.fill(slotX, slotY, slotX + 18, slotY + 1, 0xFF00E5FF);
        extractor.fill(slotX, slotY + 17, slotX + 18, slotY + 18, 0xFF00E5FF);
        extractor.fill(slotX, slotY + 1, slotX + 1, slotY + 18, 0xFF00E5FF);
        extractor.fill(slotX + 17, slotY + 1, slotX + 18, slotY + 18, 0xFF00E5FF);

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
        }
    }

    private void renderControlsAndStatus(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int statusBoxY = y + 15;
        extractor.fill(x + 10, statusBoxY, x + 166, statusBoxY + 10, 0xFF0A121E);
        String statusText = switch (this.menu.getState()) {
            case 1 -> "§e● ESCANEANDO...";
            case 2 -> "§6● LIQUEFAZENDO...";
            case 3 -> "§e● METAL LÍQUIDO";
            case 4 -> "§b● RECONSTRUINDO...";
            default -> "§a● SÓLIDO ATIVO";
        };
        extractor.text(this.font, Component.literal(statusText), x + 12, statusBoxY + 1, 0xFFFFFFFF, false);

        String savedInfo = String.format("§7Matriz: §f%d", this.menu.getSavedCount());
        int savedW = this.font.width(savedInfo);
        extractor.text(this.font, Component.literal(savedInfo), x + 164 - savedW, statusBoxY + 1, 0xFFFFFFFF, false);

        int barY = y + 27;
        extractor.fill(x + 10, barY, x + 166, barY + 5, 0xFF05080E);
        int reserveW = this.menu.getReserveScaled(156);
        if (reserveW > 0) {
            extractor.fill(x + 10, barY, x + 10 + reserveW, barY + 5, 0xFF00E5FF);
        }

        renderCyberButton(extractor, x + 10, y + 36, 75, 14, "⬡ ESCANEAR", mouseX, mouseY, 0xFF00E5FF);
        String holoText = this.menu.isHologramActive() ? "◈ HOLO: ATIVO" : "◈ HOLO: OFF";
        renderCyberButton(extractor, x + 91, y + 36, 75, 14, holoText, mouseX, mouseY, this.menu.isHologramActive() ? 0xFF00E676 : 0xFF78909C);

        renderCyberButton(extractor, x + 10, y + 52, 75, 14, "∿ LIQUEFAZER", mouseX, mouseY, 0xFFFF9100);
        renderCyberButton(extractor, x + 91, y + 52, 75, 14, "⬢ RECONSTRUIR", mouseX, mouseY, 0xFF00E5FF);

        String feedLabel = "§bALIMENTAR: [ ]";
        extractor.text(this.font, Component.literal(feedLabel), x + 100, y + 66, 0xFF78909C, false);
    }

    private void renderCyberButton(GuiGraphicsExtractor extractor, int bx, int by, int bw, int bh, String label, int mx, int my, int accentColor) {
        boolean hovered = mx >= bx && mx <= bx + bw && my >= by && my <= by + bh;
        int bg = hovered ? 0xFF142436 : 0xFF0A121E;
        int border = hovered ? 0xFF00E5FF : accentColor;
        int textCol = hovered ? 0xFFFFFFFF : 0xFFCFD8DC;

        extractor.fill(bx, by, bx + bw, by + bh, bg);
        extractor.fill(bx, by, bx + bw, by + 1, border);
        extractor.fill(bx, by + bh - 1, bx + bw, by + bh, border);
        extractor.fill(bx, by + 1, bx + 1, by + bh, border);
        extractor.fill(bx + bw - 1, by + 1, bx + bw, by + bh, border);

        int textW = this.font.width(label);
        extractor.text(this.font, Component.literal(label), bx + (bw - textW) / 2, by + 3, textCol, false);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 10 && mouseX <= x + 85 && mouseY >= y + 36 && mouseY <= y + 50) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§bEscanear Estrutura"),
                    Component.literal("§7Salva todos os blocos de liga"),
                    Component.literal("§7no raio de 32 blocos do Núcleo.")
            ), mouseX, mouseY);
        } else if (mouseX >= x + 91 && mouseX <= x + 166 && mouseY >= y + 36 && mouseY <= y + 50) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§bProjeção Holográfica"),
                    Component.literal("§7Ativa ou desativa a projeção 3D"),
                    Component.literal("§7da estrutura gravada na matriz.")
            ), mouseX, mouseY);
        } else if (mouseX >= x + 10 && mouseX <= x + 85 && mouseY >= y + 52 && mouseY <= y + 66) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§6Liquefazer Fortaleza"),
                    Component.literal("§7Dissolve gradualmente os blocos"),
                    Component.literal("§7em poças de metal líquido.")
            ), mouseX, mouseY);
        } else if (mouseX >= x + 91 && mouseX <= x + 166 && mouseY >= y + 52 && mouseY <= y + 66) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§aReconstruir Estrutura"),
                    Component.literal("§7Ergue e materializa os blocos"),
                    Component.literal("§7usando o saldo de reservas.")
            ), mouseX, mouseY);
        } else if (mouseX >= x + 10 && mouseX <= x + 166 && mouseY >= y + 27 && mouseY <= y + 32) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal(String.format("§bReservas de Liga: §f%d / %d", this.menu.getReserveBlocks(), this.menu.getMaxReserveBlocks()))
            ), mouseX, mouseY);
        } else if (mouseX >= x + 79 && mouseX <= x + 97 && mouseY >= y + 61 && mouseY <= y + 79) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§bAlimentador de Liga"),
                    Component.literal("§7Insira Blocos de Liga, Portas,"),
                    Component.literal("§7Janelas ou Gálio para abastecer.")
            ), mouseX, mouseY);
        }
    }
}
