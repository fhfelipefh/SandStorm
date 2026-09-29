package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.NomadScavengerMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class NomadScavengerScreen extends AbstractContainerScreen<NomadScavengerMenu> {
    private static final int COLOR_BG_PANEL = 0xF0181412;
    private static final int COLOR_BORDER_OUTER = 0xFF5C4730;
    private static final int COLOR_BORDER_INNER = 0xFF8C6F4B;
    private static final int COLOR_AMBER = 0xFFFF9100;
    private static final int COLOR_CYAN = 0xFF00E5FF;
    private static final int COLOR_TEXT_MUTED = 0xFF9E8E7E;

    private static final List<Component> DIALOGUE_LINES = List.of(
            Component.literal("§6\"Água pura... por favor...\""),
            Component.literal("§6\"Dunas sussurram... circuitos secam...\""),
            Component.literal("§6\"Tome sucata. Deixe-me a umidade.\""),
            Component.literal("§6\"Não me agrida... apenas barganhe...\"")
    );

    public NomadScavengerScreen(NomadScavengerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 73;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, this.imageWidth - 16, COLOR_AMBER);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, this.imageWidth - 16, COLOR_TEXT_MUTED);
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
        renderPanel(extractor, mouseX, mouseY);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
    }

    private void renderPanel(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + this.imageWidth, y + this.imageHeight, COLOR_BG_PANEL);
        extractor.fill(x, y, x + this.imageWidth, y + 1, COLOR_BORDER_OUTER);
        extractor.fill(x, y + this.imageHeight - 1, x + this.imageWidth, y + this.imageHeight, COLOR_BORDER_OUTER);
        extractor.fill(x, y, x + 1, y + this.imageHeight, COLOR_BORDER_OUTER);
        extractor.fill(x + this.imageWidth - 1, y, x + this.imageWidth, y + this.imageHeight, COLOR_BORDER_OUTER);

        extractor.fill(x + 2, y + 2, x + this.imageWidth - 2, y + 3, COLOR_BORDER_INNER);
        extractor.fill(x + 2, y + 15, x + this.imageWidth - 2, y + 16, COLOR_BORDER_INNER);

        int dialogueIndex = (int) ((System.currentTimeMillis() / 7000L) % DIALOGUE_LINES.size());
        Component dialogue = DIALOGUE_LINES.get(dialogueIndex);
        drawAdaptiveText(extractor, dialogue, x + 8, y + 18, this.imageWidth - 16, 0xFFE0C8A0);

        int selectedTrade = this.menu.getSelectedTrade();
        for (int i = 0; i < 4; i++) {
            int tabY = y + 29 + i * 10;
            boolean isSelected = (i == selectedTrade);
            int tabBg = isSelected ? 0xFF3D2E1E : 0xFF241C15;
            int tabBorder = isSelected ? COLOR_CYAN : COLOR_BORDER_INNER;

            extractor.fill(x + 8, tabY, x + 56, tabY + 9, tabBg);
            extractor.fill(x + 8, tabY, x + 56, tabY + 1, tabBorder);
            extractor.fill(x + 8, tabY + 8, x + 56, tabY + 9, tabBorder);
            extractor.fill(x + 8, tabY, x + 9, tabY + 9, tabBorder);
            extractor.fill(x + 55, tabY, x + 56, tabY + 9, tabBorder);

            String label = getTradeLabel(i);
            int textColor = isSelected ? COLOR_CYAN : 0xFFB0A090;
            drawAdaptiveText(extractor, Component.literal(label), x + 11, tabY + 1, 42, textColor);
        }

        renderSlotFrame(extractor, x + 69, y + 35);
        renderSlotFrame(extractor, x + 129, y + 35);

        extractor.fill(x + 93, y + 43, x + 107, y + 45, COLOR_BORDER_INNER);
        extractor.fill(x + 105, y + 41, x + 107, y + 47, COLOR_BORDER_INNER);
        drawAdaptiveText(extractor, Component.literal("->"), x + 97, y + 39, 12, COLOR_AMBER);

        int cost = NomadScavengerMenu.getWaterCost(selectedTrade);
        String costText = "Custo: " + cost + "x Água";
        drawAdaptiveText(extractor, Component.literal(costText), x + 65, y + 21, 60, COLOR_CYAN);

        int btnX = x + 80;
        int btnY = y + 54;
        int btnW = 58;
        int btnH = 15;
        boolean btnHovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int btnBg = btnHovered ? 0xFF00838F : 0xFF004D40;
        int btnBorder = btnHovered ? COLOR_CYAN : 0xFF00B0FF;

        extractor.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
        extractor.fill(btnX, btnY, btnX + btnW, btnY + 1, btnBorder);
        extractor.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, btnBorder);
        extractor.fill(btnX, btnY, btnX + 1, btnY + btnH, btnBorder);
        extractor.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, btnBorder);

        drawAdaptiveText(extractor, Component.literal("Barganhar"), btnX + 6, btnY + 3, btnW - 12, 0xFFFFFFFF);

        renderInventoryGrid(extractor, x + 7, y + 83);
        renderHotbarGrid(extractor, x + 7, y + 141);
    }

    private String getTradeLabel(int index) {
        return switch (index) {
            case 0 -> "1x: Sucata";
            case 1 -> "2x: Circuito";
            case 2 -> "3x: Componente";
            case 3 -> "4x: Disco Tech";
            default -> "Troca";
        };
    }

    private void renderSlotFrame(GuiGraphicsExtractor extractor, int sx, int sy) {
        extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF0A0908);
        extractor.fill(sx, sy, sx + 18, sy + 1, 0xFF3D3228);
        extractor.fill(sx, sy + 17, sx + 18, sy + 18, 0xFF6D5A47);
        extractor.fill(sx, sy, sx + 1, sy + 18, 0xFF3D3228);
        extractor.fill(sx + 17, sy, sx + 18, sy + 18, 0xFF6D5A47);
    }

    private void renderInventoryGrid(GuiGraphicsExtractor extractor, int sx, int sy) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                renderSlotFrame(extractor, sx + c * 18, sy + r * 18);
            }
        }
    }

    private void renderHotbarGrid(GuiGraphicsExtractor extractor, int sx, int sy) {
        for (int c = 0; c < 9; c++) {
            renderSlotFrame(extractor, sx + c * 18, sy);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();

        int x = this.leftPos;
        int y = this.topPos;

        for (int i = 0; i < 4; i++) {
            int tabY = y + 29 + i * 10;
            if (mx >= x + 8 && mx <= x + 56 && my >= tabY && my <= tabY + 9) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.2f));
                    return true;
                }
            }
        }

        int btnX = x + 80;
        int btnY = y + 54;
        int btnW = 58;
        int btnH = 15;
        if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, NomadScavengerMenu.BUTTON_BARTER);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.VILLAGER_YES, 0.9f));
                return true;
            }
        }

        return super.mouseClicked(event, isDouble);
    }
}
