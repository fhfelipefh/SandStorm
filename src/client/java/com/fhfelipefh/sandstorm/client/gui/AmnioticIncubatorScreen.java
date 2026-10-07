package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.AmnioticIncubatorMenu;
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

public class AmnioticIncubatorScreen extends AbstractContainerScreen<AmnioticIncubatorMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public AmnioticIncubatorScreen(AmnioticIncubatorMenu menu, Inventory playerInventory, Component title) {
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

        int b0x = x + 72;
        int b0y = y + 22;
        if (mx >= b0x && mx <= b0x + 46 && my >= b0y && my <= b0y + 18) {
            playButtonClick(AmnioticIncubatorMenu.BUTTON_CYCLE_SPECIES);
            return true;
        }

        int b1x = x + 120;
        int b1y = y + 22;
        if (mx >= b1x && mx <= b1x + 48 && my >= b1y && my <= b1y + 18) {
            playButtonClick(AmnioticIncubatorMenu.BUTTON_TOGGLE_AUTO);
            return true;
        }

        int b2x = x + 72;
        int b2y = y + 42;
        if (mx >= b2x && mx <= b2x + 46 && my >= b2y && my <= b2y + 18) {
            playButtonClick(AmnioticIncubatorMenu.BUTTON_CYCLE_QUOTA);
            return true;
        }

        int b3x = x + 120;
        int b3y = y + 42;
        if (mx >= b3x && mx <= b3x + 48 && my >= b3y && my <= b3y + 18) {
            playButtonClick(AmnioticIncubatorMenu.BUTTON_TOGGLE_START);
            return true;
        }

        return super.mouseClicked(event, isDouble);
    }

    private void playButtonClick(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderControlsAndGauges(extractor, mouseX, mouseY);
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
        int gy = y + 15;
        int gw = 156;
        int gh = 4;
        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int energyW = this.menu.getEnergyScaled(gw);
        if (energyW > 0) {
            extractor.fill(gx, gy, gx + energyW, gy + gh, 0xFF00E5FF);
        }

        int wx = x + 10;
        int wy = y + 24;
        int ww = 10;
        int wh = 46;
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

    private void renderControlsAndGauges(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        Component statusText = Component.translatable(this.menu.isActive() ? "gui.sandstorm.incubator.status_incubating" : "gui.sandstorm.incubator.status_standby");
        int statusColor = this.menu.isActive() ? 0xFF22C55E : 0xFF94A3B8;
        drawAdaptiveText(extractor, statusText, x + 108, y + 5, 58, statusColor);

        int b0x = x + 72;
        int b0y = y + 22;
        int b0w = 46;
        int b0h = 18;
        boolean b0Hover = mouseX >= b0x && mouseX <= b0x + b0w && mouseY >= b0y && mouseY <= b0y + b0h;
        renderButtonBox(extractor, b0x, b0y, b0w, b0h, b0Hover ? 0xFF0E7490 : 0xFF155E75, 0xFF00E5FF);
        String speciesNameKey = getSpeciesKey(this.menu.getSpeciesIndex());
        drawAdaptiveText(extractor, Component.translatable(speciesNameKey), b0x + 4, b0y + 5, b0w - 8, 0xFFE0F2FE);

        int b1x = x + 120;
        int b1y = y + 22;
        int b1w = 48;
        int b1h = 18;
        boolean b1Hover = mouseX >= b1x && mouseX <= b1x + b1w && mouseY >= b1y && mouseY <= b1y + b1h;
        boolean auto = this.menu.isAutoEcologicalMode();
        renderButtonBox(extractor, b1x, b1y, b1w, b1h, auto ? (b1Hover ? 0xFF166534 : 0xFF15803D) : (b1Hover ? 0xFF334155 : 0xFF1E293B), auto ? 0xFF4ADE80 : 0xFF64748B);
        Component b1Text = Component.translatable(auto ? "gui.sandstorm.incubator.btn_auto_on" : "gui.sandstorm.incubator.btn_auto_off");
        drawAdaptiveText(extractor, b1Text, b1x + 4, b1y + 5, b1w - 8, auto ? 0xFFDCFCE7 : 0xFF94A3B8);

        int b2x = x + 72;
        int b2y = y + 42;
        int b2w = 46;
        int b2h = 18;
        boolean b2Hover = mouseX >= b2x && mouseX <= b2x + b2w && mouseY >= b2y && mouseY <= b2y + b2h;
        renderButtonBox(extractor, b2x, b2y, b2w, b2h, b2Hover ? 0xFF78350F : 0xFF92400E, 0xFFF59E0B);
        Component b2Text = Component.translatable("gui.sandstorm.incubator.btn_quota", this.menu.getTargetPopulationQuota());
        drawAdaptiveText(extractor, b2Text, b2x + 4, b2y + 5, b2w - 8, 0xFFFEF3C7);

        int b3x = x + 120;
        int b3y = y + 42;
        int b3w = 48;
        int b3h = 18;
        boolean b3Hover = mouseX >= b3x && mouseX <= b3x + b3w && mouseY >= b3y && mouseY <= b3y + b3h;
        boolean active = this.menu.isActive();
        renderButtonBox(extractor, b3x, b3y, b3w, b3h, active ? (b3Hover ? 0xFF991B1B : 0xFFB91C1C) : (b3Hover ? 0xFF1E3A8A : 0xFF1D4ED8), active ? 0xFFF87171 : 0xFF60A5FA);
        Component b3Text = Component.translatable(active ? "gui.sandstorm.incubator.btn_pause" : "gui.sandstorm.incubator.btn_start");
        drawAdaptiveText(extractor, b3Text, b3x + 4, b3y + 5, b3w - 8, 0xFFEFF6FF);

        int px = x + 72;
        int py = y + 63;
        int pw = 96;
        int ph = 5;
        extractor.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, 0xFF1E293B);
        extractor.fill(px, py, px + pw, py + ph, 0xFF05080E);
        int progW = this.menu.getProgressScaled(pw);
        if (progW > 0) {
            extractor.fill(px, py, px + progW, py + ph, 0xFF10B981);
        }
    }

    private void renderButtonBox(GuiGraphicsExtractor extractor, int bx, int by, int bw, int bh, int bg, int border) {
        extractor.fill(bx, by, bx + bw, by + bh, bg);
        extractor.fill(bx, by, bx + bw, by + 1, border);
        extractor.fill(bx, by + bh - 1, bx + bw, by + bh, border);
        extractor.fill(bx, by, bx + 1, by + bh, border);
        extractor.fill(bx + bw - 1, by, bx + bw, by + bh, border);
    }

    private String getSpeciesKey(int index) {
        return switch (index) {
            case 1 -> "gui.sandstorm.incubator.species_sheep";
            case 2 -> "gui.sandstorm.incubator.species_wolf";
            case 3 -> "gui.sandstorm.incubator.species_polar_bear";
            case 4 -> "gui.sandstorm.incubator.species_frog";
            case 5 -> "gui.sandstorm.incubator.species_rabbit";
            default -> "gui.sandstorm.incubator.species_cow";
        };
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 10 && mouseX <= x + 166 && mouseY >= y + 14 && mouseY <= y + 20) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.incubator.energy_title"),
                    Component.translatable("gui.sandstorm.incubator.energy_desc",
                            NumberFormat.compact(this.menu.getEnergy()),
                            NumberFormat.compact(this.menu.getMaxEnergy()))
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 10 && mouseX <= x + 20 && mouseY >= y + 24 && mouseY <= y + 70) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.incubator.water_title"),
                    Component.translatable("gui.sandstorm.incubator.water_desc", this.menu.getWaterAmount(), this.menu.getMaxWater())
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 50 && mouseX <= x + 66 && mouseY >= y + 39 && mouseY <= y + 55) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.incubator.biomass_title"),
                    Component.translatable("gui.sandstorm.incubator.biomass_desc", this.menu.getBiomassUnits(), this.menu.getMaxBiomass())
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 72 && mouseX <= x + 168 && mouseY >= y + 62 && mouseY <= y + 69) {
            int pct = this.menu.getMaxProgressTicks() > 0 ? (int) ((long) this.menu.getProgressTicks() * 100 / this.menu.getMaxProgressTicks()) : 0;
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.incubator.progress_title"),
                    Component.translatable("gui.sandstorm.incubator.progress_desc", pct)
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }
    }
}
