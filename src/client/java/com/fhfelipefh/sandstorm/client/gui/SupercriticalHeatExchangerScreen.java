package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.SupercriticalHeatExchangerMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class SupercriticalHeatExchangerScreen extends AbstractContainerScreen<SupercriticalHeatExchangerMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public SupercriticalHeatExchangerScreen(SupercriticalHeatExchangerMenu menu, Inventory playerInventory, Component title) {
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
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderTelemetry(extractor);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 20, 0xFFFFE082);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CHASSIS_WIDTH - 16, 0xFF94A3B8);
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

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA0D0808);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFFD97706);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFFD97706);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFFD97706);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFFD97706);

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFFF59E0B);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + 1, x + CHASSIS_WIDTH - 1, y + 4, 0xFFF59E0B);
        extractor.fill(x + 1, y + CHASSIS_HEIGHT - 4, x + 4, y + CHASSIS_HEIGHT - 1, 0xFFF59E0B);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + CHASSIS_HEIGHT - 4, x + CHASSIS_WIDTH - 1, y + CHASSIS_HEIGHT - 1, 0xFFF59E0B);

        extractor.fill(x + 4, y + 3, x + CHASSIS_WIDTH - 4, y + 16, 0xEE1E0E0A);
        extractor.fill(x + 4, y + 16, x + CHASSIS_WIDTH - 4, y + 17, 0x66EA580C);

        extractor.fill(x + 10, y + 19, x + 52, y + 68, 0x5508111E);
        extractor.fill(x + 10, y + 19, x + 52, y + 20, 0xFF1E3A5F);
        extractor.fill(x + 10, y + 67, x + 52, y + 68, 0xFF1E3A5F);
        extractor.fill(x + 10, y + 19, x + 11, y + 68, 0xFF1E3A5F);
        extractor.fill(x + 51, y + 19, x + 52, y + 68, 0xFF1E3A5F);

        extractor.fill(x + 56, y + 19, x + 120, y + 68, 0x551E0A08);
        extractor.fill(x + 56, y + 19, x + 120, y + 20, 0xFF7C2D12);
        extractor.fill(x + 56, y + 67, x + 120, y + 68, 0xFF7C2D12);
        extractor.fill(x + 56, y + 19, x + 57, y + 68, 0xFF7C2D12);
        extractor.fill(x + 119, y + 19, x + 120, y + 68, 0xFF7C2D12);

        extractor.fill(x + 124, y + 19, x + 166, y + 68, 0x551E1408);
        extractor.fill(x + 124, y + 19, x + 166, y + 20, 0xFF78350F);
        extractor.fill(x + 124, y + 67, x + 166, y + 68, 0xFF78350F);
        extractor.fill(x + 124, y + 19, x + 125, y + 68, 0xFF78350F);
        extractor.fill(x + 165, y + 19, x + 166, y + 68, 0xFF78350F);

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            boolean isMachineSlot = slot.index < 4;
            int borderColor;
            if (slot.index == 0) {
                borderColor = 0xFF0284C7;
            } else if (slot.index == 1) {
                borderColor = 0xFF1E3A5F;
            } else if (slot.index == 2) {
                borderColor = 0xFFFF5722;
            } else if (slot.index == 3) {
                borderColor = 0xFFF59E0B;
            } else {
                borderColor = 0xFF1E293B;
            }
            int bgColor = isMachineSlot ? 0xDD120C14 : 0xAA080C14;

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }

        int tx = x + 14;
        int ty = y + 22;
        int tw = 12;
        int th = 43;
        extractor.fill(tx - 1, ty - 1, tx + tw + 1, ty + th + 1, 0xFF1E3A5F);
        extractor.fill(tx, ty, tx + tw, ty + th, 0xFF050B14);
        int fluidH = this.menu.getWaterScaled(th);
        if (fluidH > 0) {
            extractor.fill(tx, ty + th - fluidH, tx + tw, ty + th, 0xFF0284C7);
            extractor.fill(tx, ty + th - fluidH, tx + tw, ty + th - fluidH + 1, 0xFF38BDF8);
        }
        extractor.fill(tx + tw - 3, ty + th / 4, tx + tw, ty + th / 4 + 1, 0x881E3A5F);
        extractor.fill(tx + tw - 4, ty + th / 2, tx + tw, ty + th / 2 + 1, 0x881E3A5F);
        extractor.fill(tx + tw - 3, ty + 3 * th / 4, tx + tw, ty + 3 * th / 4 + 1, 0x881E3A5F);

        drawAdaptiveText(extractor, Component.literal("▼"), x + 37, y + 41, 10, 0xFF0284C7);

        int ex = x + 150;
        int ey = y + 22;
        int ew = 12;
        int eh = 43;
        extractor.fill(ex - 1, ey - 1, ex + ew + 1, ey + eh + 1, 0xFF78350F);
        extractor.fill(ex, ey, ex + ew, ey + eh, 0xFF0E0905);
        int maxEnergy = Math.max(1, this.menu.getMaxEnergy());
        int energyH = (int) (((long) this.menu.getStoredEnergy() * eh) / maxEnergy);
        if (energyH > 0) {
            extractor.fill(ex, ey + eh - energyH, ex + ew, ey + eh, 0xFFD97706);
            extractor.fill(ex, ey + eh - energyH, ex + ew, ey + eh - energyH + 1, 0xFFFDE047);
        }
        extractor.fill(ex, ey + eh / 4, ex + 3, ey + eh / 4 + 1, 0x8878350F);
        extractor.fill(ex, ey + eh / 2, ex + 4, ey + eh / 2 + 1, 0x8878350F);
        extractor.fill(ex, ey + 3 * eh / 4, ex + 3, ey + 3 * eh / 4 + 1, 0x8878350F);

        int px = x + 60;
        int py = y + 57;
        int pw = 56;
        int ph = 8;
        extractor.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, 0xFF475569);
        extractor.fill(px, py, px + pw, py + ph, 0xFF0B101B);
        int steamW = (int) (pw * Math.min(1.0f, this.menu.getSteamPressure() / 100.0f));
        if (steamW > 0) {
            extractor.fill(px, py, px + steamW, py + ph, 0xFFFF7043);
            extractor.fill(px, py, px + steamW, py + 1, 0xFFFFAB91);
        }

        int chevronColor = this.menu.isOperating() ? 0xFFFF9800 : 0xFF475569;
        drawAdaptiveText(extractor, Component.literal("▶"), x + 86, y + 39, 6, chevronColor);
    }

    private void renderTelemetry(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        Component statusComp;
        int statusColor;
        if (this.menu.isOperating()) {
            statusComp = Component.translatable("gui.sandstorm.heat_exchanger.status_operating");
            statusColor = 0xFF10B981;
        } else if (this.menu.getWaterAmount() <= 0) {
            statusComp = Component.translatable("gui.sandstorm.heat_exchanger.status_no_water");
            statusColor = 0xFFEF4444;
        } else {
            statusComp = Component.translatable("gui.sandstorm.heat_exchanger.status_full");
            statusColor = 0xFF00E5FF;
        }

        int badgeW = 56;
        int badgeX = x + 60;
        int badgeY = y + 21;
        extractor.fill(badgeX, badgeY, badgeX + badgeW, badgeY + 11, 0xCC0D0505);
        extractor.fill(badgeX, badgeY, badgeX + badgeW, badgeY + 1, statusColor);
        extractor.fill(badgeX, badgeY + 10, badgeX + badgeW, badgeY + 11, statusColor);
        extractor.fill(badgeX, badgeY, badgeX + 1, badgeY + 11, statusColor);
        extractor.fill(badgeX + badgeW - 1, badgeY, badgeX + badgeW, badgeY + 11, statusColor);
        drawAdaptiveText(extractor, statusComp, badgeX + 3, badgeY + 2, badgeW - 6, statusColor);

        int genRate = this.menu.getCurrentGenRate();
        Component genComp = Component.literal("+" + NumberFormat.compact(genRate) + " J/t");
        drawAdaptiveText(extractor, Component.literal("GERAÇÃO"), x + 126, y + 22, 22, 0xFF94A3B8);
        drawAdaptiveText(extractor, genComp, x + 126, y + 32, 22, 0xFFFBBF24);

        int steam = this.menu.getSteamPressure();
        drawAdaptiveText(extractor, Component.literal("VAPOR"), x + 126, y + 44, 22, 0xFF94A3B8);
        drawAdaptiveText(extractor, Component.literal(steam + "%"), x + 126, y + 54, 22, 0xFFFF7043);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 13 && mouseX <= x + 27 && mouseY >= y + 21 && mouseY <= y + 66) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.heat_exchanger.water_title"),
                    Component.literal(this.menu.getWaterAmount() + " / 8,000 mB")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 149 && mouseX <= x + 163 && mouseY >= y + 21 && mouseY <= y + 66) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.heat_exchanger.energy_title"),
                    Component.literal(NumberFormat.compact(this.menu.getStoredEnergy()) + " / " + NumberFormat.compact(this.menu.getMaxEnergy()) + " J")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 59 && mouseX <= x + 117 && mouseY >= y + 56 && mouseY <= y + 66) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.heat_exchanger.steam_title"),
                    Component.literal(this.menu.getSteamPressure() + "%")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 67 && mouseX <= x + 85 && mouseY >= y + 34 && mouseY <= y + 52) {
            if (!this.menu.slots.get(2).hasItem()) {
                List<Component> tip = List.of(
                        Component.translatable("gui.sandstorm.heat_exchanger.core_slot_title"),
                        Component.translatable("gui.sandstorm.heat_exchanger.core_slot_desc")
                );
                extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
                return;
            }
        }

        if (mouseX >= x + 91 && mouseX <= x + 109 && mouseY >= y + 34 && mouseY <= y + 52) {
            if (!this.menu.slots.get(3).hasItem()) {
                List<Component> tip = List.of(
                        Component.translatable("gui.sandstorm.heat_exchanger.battery_slot_title"),
                        Component.translatable("gui.sandstorm.heat_exchanger.battery_slot_desc")
                );
                extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            }
        }
    }
}
