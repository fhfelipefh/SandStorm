package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.CryoRevivalVatMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class CryoRevivalVatScreen extends AbstractContainerScreen<CryoRevivalVatMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public CryoRevivalVatScreen(CryoRevivalVatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 10;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 74;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderDiagnostics(extractor);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, 156, 0xFFBAE6FD);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CHASSIS_WIDTH - 16, 0xFF78909C);
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA041019);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        for (Slot slot : this.menu.slots) {
            int slotX = x + slot.x;
            int slotY = y + slot.y;
            extractor.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF0284C7);
            extractor.fill(slotX, slotY, slotX + 16, slotY + 16, 0xAA030B12);
        }

        int tx = x + 12;
        int ty = y + 20;
        int tw = 16;
        int th = 48;
        extractor.fill(tx - 1, ty - 1, tx + tw + 1, ty + th + 1, 0xFF0284C7);
        extractor.fill(tx, ty, tx + tw, ty + th, 0xFF030D17);

        int fluidHeight = this.menu.getFluidScaled(th);
        if (fluidHeight > 0) {
            int fy = ty + th - fluidHeight;
            extractor.fill(tx, fy, tx + tw, ty + th, 0xFF00B0FF);
            extractor.fill(tx, fy, tx + tw, fy + 1, 0xFFE0F2FE);
        }

        int px = x + 58;
        int py = y + 36;
        int pw = 68;
        int ph = 14;
        extractor.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, 0xFF0284C7);
        extractor.fill(px, py, px + pw, py + ph, 0xFF051522);

        int progressWidth = this.menu.getProgressScaled(pw);
        if (progressWidth > 0) {
            extractor.fill(px, py, px + progressWidth, py + ph, 0xFF00E5FF);
            extractor.fill(px, py, px + progressWidth, py + 1, 0xFFE0F7FA);
        }
    }

    private void renderDiagnostics(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        drawAdaptiveText(extractor, Component.literal("AMOSTRA"), x + 34, y + 23, 28, 0xFF7DD3FC);
        drawAdaptiveText(extractor, Component.literal("GEL/SAL"), x + 72, y + 45, 28, 0xFF38BDF8);
        drawAdaptiveText(extractor, Component.literal("PRODUTO"), x + 128, y + 23, 30, 0xFF34D399);

        int progress = this.menu.getProgress();
        int status = this.menu.getStatus();
        String percentText = progress > 0 ? progress + "%" : "CRIO";
        drawAdaptiveText(extractor, Component.literal(percentText), x + 84, y + 24, 26, 0xFFBAE6FD);

        Component statusComponent = switch (status) {
            case 1 -> Component.translatable("gui.sandstorm.cryo_revival_vat.status_incubating", progress);
            case 2 -> Component.translatable("gui.sandstorm.cryo_revival_vat.status_need_coolant");
            case 3 -> Component.translatable("gui.sandstorm.cryo_revival_vat.status_ready");
            default -> Component.translatable("gui.sandstorm.cryo_revival_vat.status_idle");
        };
        int statusColor = switch (status) {
            case 1 -> 0xFF38BDF8;
            case 2 -> 0xFFF59E0B;
            case 3 -> 0xFF10B981;
            default -> 0xFF94A3B8;
        };
        drawAdaptiveText(extractor, statusComponent, x + 38, y + 73, 130, statusColor);

        int temp = this.menu.getTemperature();
        Component tempComponent = Component.translatable("gui.sandstorm.cryo_revival_vat.temp", temp);
        drawAdaptiveText(extractor, tempComponent, x + 12, y + 73, 24, 0xFFE0F2FE);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int tx = x + 12;
        int ty = y + 20;
        if (mouseX >= tx && mouseX <= tx + 16 && mouseY >= ty && mouseY <= ty + 48) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.sandstorm.cryo_revival_vat.coolant_tank",
                    NumberFormat.compact(this.menu.getFluidAmount()),
                    NumberFormat.compact(this.menu.getMaxFluid())));
            tooltip.add(Component.literal("Compatível com tubos Fabric API"));
            tooltip.add(Component.literal("Consumo: 250 mB por ciclo"));
            extractor.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }

        int px = x + 58;
        int py = y + 36;
        if (mouseX >= px && mouseX <= px + 68 && mouseY >= py && mouseY <= py + 14) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Câmara de Incubação Criogênica"));
            tooltip.add(Component.literal("Âmbar + Permafrost -> Núcleo Bio-Neural"));
            tooltip.add(Component.literal("Sementes Xeno + Sal -> Célula Crio-Trealose"));
            tooltip.add(Component.literal("Sementes Vanilla + Sal -> 2x Sementes Xeno"));
            tooltip.add(Component.literal("Espinho de Gelo + Sal -> Bio-Refrigerante"));
            extractor.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
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
}

