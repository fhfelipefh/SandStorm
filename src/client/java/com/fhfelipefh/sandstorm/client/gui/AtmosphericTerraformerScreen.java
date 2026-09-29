package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.AtmosphericTerraformerMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class AtmosphericTerraformerScreen extends AbstractContainerScreen<AtmosphericTerraformerMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public AtmosphericTerraformerScreen(AtmosphericTerraformerMenu menu, Inventory playerInventory, Component title) {
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

        int b0x = x + 106;
        int b0y = y + 25;
        if (mx >= b0x && mx <= b0x + 62 && my >= b0y && my <= b0y + 20) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, AtmosphericTerraformerMenu.BUTTON_TOGGLE_LIGHTNING);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        int b1x = x + 106;
        int b1y = y + 49;
        if (mx >= b1x && mx <= b1x + 62 && my >= b1y && my <= b1y + 20) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, AtmosphericTerraformerMenu.BUTTON_CYCLE_TIER);
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
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, 95, 0xFF00E5FF);
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

        int gx = x + 10;
        int gy = y + 16;
        int gw = 156;
        int gh = 5;
        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int energyW = this.menu.getEnergyScaled(gw);
        if (energyW > 0) {
            extractor.fill(gx, gy, gx + energyW, gy + gh, 0xFF00E5FF);
        }

        int wx = x + 10;
        int wy = y + 25;
        int ww = 10;
        int wh = 44;
        extractor.fill(wx - 1, wy - 1, wx + ww + 1, wy + wh + 1, 0xFF1E293B);
        extractor.fill(wx, wy, wx + ww, wy + wh, 0xFF05080E);
        int waterH = this.menu.getWaterScaled(wh);
        if (waterH > 0) {
            extractor.fill(wx, wy + wh - waterH, wx + ww, wy + wh, 0xFF0284C7);
        }

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

        Component statusText;
        int statusColor;
        if (this.menu.isActive()) {
            statusText = Component.translatable("gui.sandstorm.terraformer.status_active");
            statusColor = 0xFF22C55E;
        } else if (this.menu.isDissipating()) {
            statusText = Component.translatable("gui.sandstorm.terraformer.status_dissipating");
            statusColor = 0xFFEAB308;
        } else {
            statusText = Component.translatable("gui.sandstorm.terraformer.status_waiting");
            statusColor = 0xFF94A3B8;
        }
        drawAdaptiveText(extractor, statusText, x + 108, y + 5, 58, statusColor);

        int b0x = x + 106;
        int b0y = y + 25;
        int b0w = 62;
        int b0h = 20;
        boolean b0Hover = mouseX >= b0x && mouseX <= b0x + b0w && mouseY >= b0y && mouseY <= b0y + b0h;
        boolean lightningOn = this.menu.isLightningEnabled();
        int b0Bg = lightningOn ? (b0Hover ? 0xFF0E7490 : 0xFF155E75) : (b0Hover ? 0xFF334155 : 0xFF1E293B);
        int b0Border = lightningOn ? 0xFF00E5FF : 0xFF475569;
        extractor.fill(b0x, b0y, b0x + b0w, b0y + b0h, b0Bg);
        extractor.fill(b0x, b0y, b0x + b0w, b0y + 1, b0Border);
        extractor.fill(b0x, b0y + b0h - 1, b0x + b0w, b0y + b0h, b0Border);
        extractor.fill(b0x, b0y, b0x + 1, b0y + b0h, b0Border);
        extractor.fill(b0x + b0w - 1, b0y, b0x + b0w, b0y + b0h, b0Border);

        Component b0Text = Component.translatable(lightningOn ? "gui.sandstorm.terraformer.lightning_on" : "gui.sandstorm.terraformer.lightning_off");
        int b0TextColor = lightningOn ? 0xFFE0F2FE : 0xFF94A3B8;
        int tw0 = this.font.width(b0Text);
        extractor.text(this.font, b0Text, b0x + (b0w - tw0) / 2, b0y + 6, b0TextColor, false);

        int b1x = x + 106;
        int b1y = y + 49;
        int b1w = 62;
        int b1h = 20;
        boolean b1Hover = mouseX >= b1x && mouseX <= b1x + b1w && mouseY >= b1y && mouseY <= b1y + b1h;
        int b1Bg = b1Hover ? 0xFF78350F : 0xFF1E293B;
        int b1Border = 0xFFF59E0B;
        extractor.fill(b1x, b1y, b1x + b1w, b1y + b1h, b1Bg);
        extractor.fill(b1x, b1y, b1x + b1w, b1y + 1, b1Border);
        extractor.fill(b1x, b1y + b1h - 1, b1x + b1w, b1y + b1h, b1Border);
        extractor.fill(b1x, b1y, b1x + 1, b1y + b1h, b1Border);
        extractor.fill(b1x + b1w - 1, b1y, b1x + b1w, b1y + b1h, b1Border);

        Component b1Text = Component.translatable("gui.sandstorm.terraformer.tier", this.menu.getTier());
        int tw1 = this.font.width(b1Text);
        extractor.text(this.font, b1Text, b1x + (b1w - tw1) / 2, b1y + 6, 0xFFFDE68A, false);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 10 && mouseX <= x + 166 && mouseY >= y + 16 && mouseY <= y + 21) {
            Component energyTip = Component.translatable("gui.sandstorm.terraformer.energy",
                    NumberFormat.compact(this.menu.getEnergy()),
                    NumberFormat.compact(this.menu.getMaxEnergy()));
            List<Component> tip = List.of(energyTip);
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 10 && mouseX <= x + 20 && mouseY >= y + 25 && mouseY <= y + 69) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.terraformer.water_title"),
                    Component.translatable("gui.sandstorm.terraformer.water_amount", this.menu.getWaterAmount(), this.menu.getMaxWater())
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        int b0x = x + 106;
        int b0y = y + 25;
        if (mouseX >= b0x && mouseX <= b0x + 62 && mouseY >= b0y && mouseY <= b0y + 20) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.terraformer.lightning_title"),
                    Component.translatable(this.menu.isLightningEnabled()
                            ? "gui.sandstorm.terraformer.lightning_desc_enabled"
                            : "gui.sandstorm.terraformer.lightning_desc_disabled"),
                    Component.translatable("gui.sandstorm.terraformer.click_toggle")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        int b1x = x + 106;
        int b1y = y + 49;
        if (mouseX >= b1x && mouseX <= b1x + 62 && mouseY >= b1y && mouseY <= b1y + 20) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.terraformer.range_title"),
                    Component.translatable("gui.sandstorm.terraformer.range_desc", this.menu.getRadius()),
                    Component.translatable("gui.sandstorm.terraformer.tier_click")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }
    }
}
