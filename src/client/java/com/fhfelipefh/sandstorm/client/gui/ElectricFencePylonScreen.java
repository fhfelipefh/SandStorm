package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.ElectricFencePylonMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class ElectricFencePylonScreen extends AbstractContainerScreen<ElectricFencePylonMenu> {
    public ElectricFencePylonScreen(ElectricFencePylonMenu menu, Inventory playerInventory, Component title) {
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

        int buttonX = this.leftPos + 14;
        int buttonY = this.topPos + 42;
        int buttonW = 54;
        int buttonH = 18;

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
            extractor.fill(barX, barY, barX + filledW, barY + barH, 0xFF00E5FF);
        }

        renderSlotFrame(extractor, x + 80, y + 42);

        int btnX = x + 14;
        int btnY = y + 42;
        extractor.fill(btnX, btnY, btnX + 54, btnY + 18, 0xFF1E293B);
        extractor.fill(btnX + 1, btnY + 1, btnX + 53, btnY + 17, 0xFF0F172A);

        Component modeLabel = switch (this.menu.getMode()) {
            case 0 -> Component.translatable("gui.sandstorm.electric_fence.btn_redstone");
            case 1 -> Component.translatable("gui.sandstorm.electric_fence.btn_on");
            case 2 -> Component.translatable("gui.sandstorm.electric_fence.btn_off");
            default -> Component.literal("MODE");
        };
        int textW = this.font.width(modeLabel);
        int modeColor = switch (this.menu.getMode()) {
            case 0 -> 0xFFFF7043;
            case 1 -> 0xFF00E5FF;
            case 2 -> 0xFF78909C;
            default -> 0xFFFFFFFF;
        };
        extractor.text(this.font, modeLabel, btnX + (54 - textW) / 2, btnY + 5, modeColor, false);

        int statusX = x + 106;
        int statusY = y + 42;
        Component statusComp;
        int statusColor;
        if (this.menu.isConnected()) {
            statusComp = Component.translatable("gui.sandstorm.electric_fence.status_connected", this.menu.getConnectedCount());
            statusColor = 0xFF00E676;
        } else if (this.menu.isArmed()) {
            statusComp = Component.translatable("gui.sandstorm.electric_fence.status_armed");
            statusColor = 0xFFFFD600;
        } else {
            statusComp = Component.translatable("gui.sandstorm.electric_fence.status_offline");
            statusColor = 0xFF78909C;
        }
        drawAdaptiveText(extractor, statusComp, statusX, statusY + 5, 60, statusColor);
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
            Component tooltip = Component.translatable("gui.sandstorm.electric_fence.energy", NumberFormat.compact(stored), NumberFormat.compact(max), Math.round(this.menu.getChargePercentage() * 100f));
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }

        if (mouseX >= x + 14 && mouseX <= x + 68 && mouseY >= y + 42 && mouseY <= y + 60) {
            List<Component> modeDesc = switch (this.menu.getMode()) {
                case 0 -> List.of(
                        Component.translatable("gui.sandstorm.electric_fence.mode_redstone_title"),
                        Component.translatable("gui.sandstorm.electric_fence.mode_redstone_desc1"),
                        Component.translatable("gui.sandstorm.electric_fence.mode_redstone_desc2")
                );
                case 1 -> List.of(
                        Component.translatable("gui.sandstorm.electric_fence.mode_on_title"),
                        Component.translatable("gui.sandstorm.electric_fence.mode_on_desc")
                );
                case 2 -> List.of(
                        Component.translatable("gui.sandstorm.electric_fence.mode_off_title"),
                        Component.translatable("gui.sandstorm.electric_fence.mode_off_desc")
                );
                default -> List.of();
            };
            if (!modeDesc.isEmpty()) {
                extractor.setComponentTooltipForNextFrame(this.font, modeDesc, mouseX, mouseY);
            }
        }
    }
}
