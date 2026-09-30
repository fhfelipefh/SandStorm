package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.MegastructureConstructorMenu;
import com.fhfelipefh.sandstorm.content.megastructure.MegastructureBlueprint;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import java.util.List;

public class MegastructureConstructorScreen extends BaseMachineScreen<MegastructureConstructorMenu> {

    public MegastructureConstructorScreen(MegastructureConstructorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:megastructure_constructor";
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 26;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 74;
    }

    @Override
    protected void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + imageWidth, y + imageHeight, 0xFA0A0E17);
        extractor.fill(x, y, x + imageWidth, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + imageHeight, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 1, y, x + imageWidth, y + imageHeight, 0xFF00E5FF);

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 4, y + 1, x + imageWidth - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + imageHeight - 4, x + 4, y + imageHeight - 1, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 4, y + imageHeight - 4, x + imageWidth - 1, y + imageHeight - 1, 0xFF00E5FF);

        extractor.fill(x + 4, y + 4, x + imageWidth - 4, y + 16, 0xDD101824);
        extractor.fill(x + 4, y + 16, x + imageWidth - 4, y + 17, 0x8800E5FF);

        boolean wpt = this.menu.isWptConnected();
        int wptColor = wpt ? 0xFF00E5FF : 0xFF455A64;
        extractor.fill(x + 8, y + 6, x + 18, y + 14, 0xFF05080E);
        extractor.fill(x + 12, y + 7, x + 14, y + 13, wptColor);
        extractor.fill(x + 9, y + 8, x + 11, y + 10, wptColor);
        extractor.fill(x + 15, y + 8, x + 17, y + 10, wptColor);

        renderRecipeButton(extractor, this.lastMouseX, this.lastMouseY);
        renderBlueprintTabs(extractor, this.lastMouseX, this.lastMouseY);

        int barY = y + 20;
        int barH = 9;

        int energyX = x + 8;
        int energyW = 75;
        extractor.fill(energyX - 1, barY - 1, energyX + energyW + 1, barY + barH + 1, 0xFF1E293B);
        extractor.fill(energyX, barY, energyX + energyW, barY + barH, 0xFF05080E);
        int scaledEnergy = this.menu.getEnergyScaled(energyW);
        if (scaledEnergy > 0) {
            int energyColor = this.menu.getEnergy() < this.menu.getMaxEnergy() / 4 ? 0xFFFF1744 : 0xFF00E5FF;
            extractor.fill(energyX + 1, barY + 1, energyX + scaledEnergy, barY + barH - 1, energyColor);
        }
        drawAdaptiveText(extractor, Component.literal(formatCompact(this.menu.getEnergy()) + " J"), energyX + 3, barY + 1, energyW - 6, 0xFFFFFFFF);

        int ledX = x + 86;
        int ledColor = this.menu.getLaserActive() ? 0xFFFF1744 : (this.menu.isPausedStorm() ? 0xFFFF9100 : (this.menu.isBuilding() ? 0xFF00E5FF : 0xFF455A64));
        extractor.fill(ledX - 1, barY, ledX + 4, barY + barH, 0xFF1E293B);
        extractor.fill(ledX, barY + 1, ledX + 3, barY + barH - 1, ledColor);

        int progX = x + 93;
        int progW = 75;
        extractor.fill(progX - 1, barY - 1, progX + progW + 1, barY + barH + 1, 0xFF1E293B);
        extractor.fill(progX, barY, progX + progW, barY + barH, 0xFF080C14);
        int pct = this.menu.getCompletionPercentage();
        int scaledProg = (pct * (progW - 2)) / 100;
        if (scaledProg > 0) {
            int progColor = this.menu.isDone() ? 0xFF00E676 : 0xFF00E5FF;
            extractor.fill(progX + 1, barY + 1, progX + 1 + scaledProg, barY + barH - 1, progColor);
        }
        drawAdaptiveText(extractor, Component.literal(pct + "%"), progX + 3, barY + 1, progW - 6, 0xFFFFFFFF);

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            boolean isMachineSlot = slot.index < 18;
            int borderColor = isMachineSlot ? 0xFF00E5FF : 0xFF1E293B;
            int bgColor = isMachineSlot ? 0xDD0D131F : 0xAA080C14;

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }
    }

    private void renderRecipeButton(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int btnX = this.leftPos + this.imageWidth - 24;
        int btnY = this.topPos + 3;
        int btnW = 20;
        int btnH = 13;

        boolean hovered = mouseX >= btnX - 1 && mouseX <= btnX + btnW + 1 && mouseY >= btnY - 1 && mouseY <= btnY + btnH + 1;
        int btnBg = (this.recipeCatalogOpen || hovered) ? 0xEE162234 : 0xDD0D131F;
        int btnBorder = (this.recipeCatalogOpen || hovered) ? 0xFF00E5FF : 0xFF1E293B;

        extractor.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
        extractor.fill(btnX, btnY, btnX + btnW, btnY + 1, btnBorder);
        extractor.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, btnBorder);
        extractor.fill(btnX, btnY, btnX + 1, btnY + btnH, btnBorder);
        extractor.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, btnBorder);

        int iconColor = (this.recipeCatalogOpen || hovered) ? 0xFF80D8FF : 0xFF00E5FF;
        extractor.fill(btnX + 4, btnY + 2, btnX + 9, btnY + 11, iconColor);
        extractor.fill(btnX + 9, btnY + 2, btnX + 11, btnY + 11, 0xFF101824);
        extractor.fill(btnX + 11, btnY + 2, btnX + 16, btnY + 11, iconColor);
        extractor.fill(btnX + 5, btnY + 4, btnX + 8, btnY + 5, 0xFF05080E);
        extractor.fill(btnX + 5, btnY + 7, btnX + 8, btnY + 8, 0xFF05080E);
        extractor.fill(btnX + 12, btnY + 4, btnX + 15, btnY + 5, 0xFF05080E);
        extractor.fill(btnX + 12, btnY + 7, btnX + 15, btnY + 8, 0xFF05080E);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        for (int i = 0; i < MegastructureBlueprint.values().length; i++) {
            int tabX = x - 26;
            int tabY = y + 10 + i * 26;
            int tabW = 26;
            int tabH = 22;
            if (mx >= tabX && mx <= tabX + tabW && my >= tabY && my <= tabY + tabH) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }
        }

        return super.mouseClicked(event, isDouble);
    }

    private void renderBlueprintTabs(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        int activeIdx = this.menu.getBlueprintIndex();

        for (int i = 0; i < MegastructureBlueprint.values().length; i++) {
            int tabX = x - 26;
            int tabY = y + 10 + i * 26;
            int tabW = 26;
            int tabH = 22;

            boolean isActive = (i == activeIdx);
            boolean hovered = mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= tabY && mouseY <= tabY + tabH;

            int bgColor = isActive ? 0xEE101E30 : (hovered ? 0xDD121C2A : 0xDD0A0E17);
            int borderColor = isActive ? 0xFF00E5FF : (hovered ? 0xFF80D8FF : 0xFF1E293B);

            extractor.fill(tabX, tabY, tabX + tabW, tabY + tabH, bgColor);
            extractor.fill(tabX, tabY, tabX + tabW, tabY + 1, borderColor);
            extractor.fill(tabX, tabY + tabH - 1, tabX + tabW, tabY + tabH, borderColor);
            extractor.fill(tabX, tabY + 1, tabX + 1, tabY + tabH, borderColor);
            if (!isActive) {
                extractor.fill(tabX + tabW - 1, tabY, tabX + tabW, tabY + tabH, borderColor);
            }

            int iconColor = isActive ? 0xFF00E5FF : (hovered ? 0xFF80D8FF : 0xFF607D8B);
            renderTabGlyph(extractor, i, tabX + 5, tabY + 4, iconColor);
        }
    }

    private void renderTabGlyph(GuiGraphicsExtractor extractor, int index, int gx, int gy, int color) {
        switch (index) {
            case 0 -> {
                extractor.fill(gx + 3, gy + 1, gx + 11, gy + 3, color);
                extractor.fill(gx + 1, gy + 3, gx + 13, gy + 7, color);
                extractor.fill(gx, gy + 7, gx + 14, gy + 12, color);
                extractor.fill(gx + 3, gy + 4, gx + 11, gy + 11, 0xFF0A0E17);
                extractor.fill(gx + 6, gy + 1, gx + 8, gy + 12, color);
            }
            case 1 -> {
                extractor.fill(gx + 1, gy + 2, gx + 3, gy + 5, color);
                extractor.fill(gx + 6, gy + 2, gx + 8, gy + 5, color);
                extractor.fill(gx + 11, gy + 2, gx + 13, gy + 5, color);
                extractor.fill(gx, gy + 5, gx + 14, gy + 12, color);
                extractor.fill(gx + 5, gy + 8, gx + 9, gy + 12, 0xFF0A0E17);
            }
            case 2 -> {
                extractor.fill(gx + 6, gy, gx + 8, gy + 3, color);
                extractor.fill(gx + 5, gy + 3, gx + 9, gy + 9, color);
                extractor.fill(gx + 3, gy + 9, gx + 11, gy + 11, color);
                extractor.fill(gx + 2, gy + 11, gx + 4, gy + 13, color);
                extractor.fill(gx + 10, gy + 11, gx + 12, gy + 13, color);
                extractor.fill(gx + 6, gy + 11, gx + 8, gy + 14, 0xFFFFB300);
            }
            case 3 -> {
                extractor.fill(gx + 6, gy + 1, gx + 8, gy + 3, color);
                extractor.fill(gx + 4, gy + 3, gx + 10, gy + 6, color);
                extractor.fill(gx + 2, gy + 6, gx + 12, gy + 9, color);
                extractor.fill(gx, gy + 9, gx + 14, gy + 12, color);
                extractor.fill(gx + 6, gy + 1, gx + 8, gy + 3, 0xFFFFD600);
            }
        }
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        super.renderCustomTooltips(extractor, mouseX, mouseY);

        int x = this.leftPos;
        int y = this.topPos;

        for (int i = 0; i < MegastructureBlueprint.values().length; i++) {
            int tabX = x - 26;
            int tabY = y + 10 + i * 26;
            int tabW = 26;
            int tabH = 22;
            if (mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= tabY && mouseY <= tabY + tabH) {
                MegastructureBlueprint bp = MegastructureBlueprint.byIndex(i);
                boolean isCurrent = (i == this.menu.getBlueprintIndex());
                Component selectHint = isCurrent
                        ? Component.translatable("megastructure.sandstorm.tab_active").withStyle(ChatFormatting.AQUA)
                        : Component.translatable("megastructure.sandstorm.tab_click").withStyle(ChatFormatting.YELLOW);
                extractor.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.translatable("megastructure.sandstorm." + bp.getId()).withStyle(ChatFormatting.GOLD),
                        Component.literal("§bDimensões: §f" + bp.getSizeX() + "x" + bp.getSizeY() + "x" + bp.getSizeZ()),
                        Component.literal("§bTotal: §f" + bp.getPlacements().size() + " blocos"),
                        Component.translatable("megastructure.sandstorm." + bp.getId() + ".blocks").withStyle(ChatFormatting.GRAY),
                        selectHint
                ), mouseX, mouseY);
                return;
            }
        }

        if (mouseX >= x + 7 && mouseX <= x + 84 && mouseY >= y + 19 && mouseY <= y + 30) {
            String energy = formatCompact(this.menu.getEnergy());
            String maxEnergy = formatCompact(this.menu.getMaxEnergy());
            String wptStatus = this.menu.isWptConnected() ? "§a⚡ WPT Online" : "§7⚡ WPT Offline";
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§bEnergia: " + energy + " / " + maxEnergy + " J"),
                    Component.literal(wptStatus)
            ), mouseX, mouseY);
        } else if (mouseX >= x + 92 && mouseX <= x + 169 && mouseY >= y + 19 && mouseY <= y + 30) {
            int pct = this.menu.getCompletionPercentage();
            int built = this.menu.getConstructedBlocks();
            int total = this.menu.getTotalBlocks();
            MegastructureBlueprint bp = MegastructureBlueprint.byIndex(this.menu.getBlueprintIndex());
            Component status = switch (this.menu.isDone() ? 0 : (this.menu.isBuilding() ? 1 : (this.menu.isPausedStorm() ? 2 : 3))) {
                case 0 -> Component.translatable("megastructure.sandstorm.status.done").withStyle(ChatFormatting.GREEN);
                case 1 -> Component.translatable("megastructure.sandstorm.status.building").withStyle(ChatFormatting.YELLOW);
                case 2 -> Component.translatable("megastructure.sandstorm.status.paused").withStyle(ChatFormatting.RED);
                default -> Component.translatable("megastructure.sandstorm.status.standby").withStyle(ChatFormatting.GRAY);
            };
            Component blocksHint = Component.translatable("megastructure.sandstorm." + bp.getId() + ".blocks").withStyle(ChatFormatting.GRAY);
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.translatable("megastructure.sandstorm." + bp.getId()).withStyle(ChatFormatting.GOLD),
                    Component.literal("§bProgresso: §f" + built + "/" + total + " (" + pct + "%)"),
                    status,
                    blocksHint
            ), mouseX, mouseY);
        }
    }
}
