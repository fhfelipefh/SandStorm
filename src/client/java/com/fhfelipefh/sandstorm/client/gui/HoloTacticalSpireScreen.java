package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.HoloTacticalSpireMenu;
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

public class HoloTacticalSpireScreen extends AbstractContainerScreen<HoloTacticalSpireMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 180;

    public HoloTacticalSpireScreen(HoloTacticalSpireMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 87;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        for (int i = 0; i < 4; i++) {
            int bx = x + 88;
            int by = y + 26 + i * 14;
            int bw = 80;
            int bh = 12;
            if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderHoloRadar(extractor, delta);
        renderTacticalButtons(extractor, mouseX, mouseY);
        renderSpireTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 16, 0xFF00E5FF);
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

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + 1, x + CHASSIS_WIDTH - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + CHASSIS_HEIGHT - 4, x + 4, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + CHASSIS_HEIGHT - 4, x + CHASSIS_WIDTH - 1, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);

        extractor.fill(x + 4, y + 4, x + CHASSIS_WIDTH - 4, y + 15, 0xDD0D1522);
        extractor.fill(x + 4, y + 15, x + CHASSIS_WIDTH - 4, y + 16, 0x8800E5FF);

        int gx = x + 8;
        int gy = y + 17;
        int gw = 160;
        int gh = 6;
        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int energyW = this.menu.getEnergyScaled(gw);
        if (energyW > 0) {
            extractor.fill(gx, gy, gx + energyW, gy + gh, 0xFF00E5FF);
        }

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
        }
    }

    private void renderHoloRadar(GuiGraphicsExtractor extractor, float delta) {
        int x = this.leftPos;
        int y = this.topPos;

        int rx = x + 8;
        int ry = y + 25;
        int rw = 74;
        int rh = 58;

        extractor.fill(rx - 1, ry - 1, rx + rw + 1, ry + rh + 1, 0xFF1E293B);
        extractor.fill(rx, ry, rx + rw, ry + rh, 0xFF040A10);

        int cx = rx + rw / 2;
        int cy = ry + rh / 2;

        extractor.fill(rx + 2, cy, rx + rw - 2, cy + 1, 0x4400E5FF);
        extractor.fill(cx, ry + 2, cx + 1, ry + rh - 2, 0x4400E5FF);

        extractor.fill(cx - 12, cy - 12, cx + 13, cy - 11, 0x3300E5FF);
        extractor.fill(cx - 12, cy + 12, cx + 13, cy + 13, 0x3300E5FF);
        extractor.fill(cx - 12, cy - 12, cx - 11, cy + 13, 0x3300E5FF);
        extractor.fill(cx + 12, cy - 12, cx + 13, cy + 13, 0x3300E5FF);

        extractor.fill(cx - 24, cy - 24, cx + 25, cy - 23, 0x2200E5FF);
        extractor.fill(cx - 24, cy + 24, cx + 25, cy + 25, 0x2200E5FF);
        extractor.fill(cx - 24, cy - 24, cx - 23, cy + 25, 0x2200E5FF);
        extractor.fill(cx + 24, cy - 24, cx + 25, cy + 25, 0x2200E5FF);

        long time = System.currentTimeMillis();
        double angle = (time % 2400) / 2400.0 * 2.0 * Math.PI;
        int sweepX = cx + (int) (Math.cos(angle) * 26);
        int sweepY = cy + (int) (Math.sin(angle) * 24);
        extractor.fill(cx, cy, sweepX, sweepY, 0x5500E5FF);

        extractor.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFF00E5FF);

        int count = this.menu.getConnectedCyborgsCount();
        for (int i = 0; i < Math.min(count, 8); i++) {
            double nodeAngle = (i * 0.785) + (time % 10000) / 10000.0 * 0.5;
            int dist = 14 + (i * 3) % 12;
            int nx = cx + (int) (Math.cos(nodeAngle) * dist);
            int ny = cy + (int) (Math.sin(nodeAngle) * dist);
            int nodeColor = (i % 2 == 0) ? 0xFF00E676 : 0xFFFFB300;
            extractor.fill(nx - 1, ny - 1, nx + 1, ny + 1, nodeColor);
        }

        String countText = "CIBORGUES: " + count;
        extractor.text(this.font, Component.literal(countText), rx + 3, ry + 2, 0xFF76FF03, false);
    }

    private void renderTacticalButtons(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        String[] orderNames = {
                "CONVERGÊNCIA",
                "EVACUAÇÃO",
                "COORDENAÇÃO",
                "STANDBY"
        };

        int[] borderColors = {
                0xFF00E5FF,
                0xFFFF1744,
                0xFF00E676,
                0xFFFFB300
        };

        int activeOrder = this.menu.getActiveTacticalOrder();

        for (int i = 0; i < 4; i++) {
            int bx = x + 88;
            int by = y + 26 + i * 14;
            int bw = 80;
            int bh = 12;

            boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;
            boolean selected = (i == activeOrder);

            int bg = selected ? 0xDD1E293B : (hovered ? 0x991E293B : 0x55080C14);
            int border = selected ? borderColors[i] : (hovered ? 0xFFAAAAAA : 0xFF37474F);

            extractor.fill(bx, by, bx + bw, by + bh, bg);
            extractor.fill(bx, by, bx + bw, by + 1, border);
            extractor.fill(bx, by + bh - 1, bx + bw, by + bh, border);
            extractor.fill(bx, by, bx + 1, by + bh, border);
            extractor.fill(bx + bw - 1, by, bx + bw, by + bh, border);

            String text = (selected ? "▶ " : "") + orderNames[i];
            int textColor = selected ? borderColors[i] : (hovered ? 0xFFFFFFFF : 0xFF90A4AE);
            extractor.text(this.font, Component.literal(text), bx + 4, by + 2, textColor, false);
        }

        int sx = x + 88;
        int sy = y + 84;
        boolean threat = this.menu.isSeismicThreat();
        String sMsg = threat ? "§c⚠ ALERTA SÍSMICO" : "§a● SÍSMICO: SEGURO";
        extractor.text(this.font, Component.literal(sMsg), sx, sy, threat ? 0xFFFF1744 : 0xFF76FF03, false);
    }

    private void renderSpireTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 17 && mouseY <= y + 23) {
            Component tip = Component.literal("Capacitor WPT Torre: " + NumberFormat.compact(this.menu.getStoredEnergy()) + " / " + NumberFormat.compact(this.menu.getMaxEnergy()) + " J");
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 8 && mouseX <= x + 82 && mouseY >= y + 25 && mouseY <= y + 83) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§bRadar Holo-Tático (Raio: 64m)"),
                    Component.literal("§7Detecta telemetria de ciborgues"),
                    Component.literal("§7e anomalias sísmicas.")
            ), mouseX, mouseY);
        } else {
            for (int i = 0; i < 4; i++) {
                int bx = x + 88;
                int by = y + 26 + i * 14;
                if (mouseX >= bx && mouseX <= bx + 80 && mouseY >= by && mouseY <= by + 12) {
                    List<List<Component>> descs = List.of(
                            List.of(
                                    Component.literal("§bConvergência"),
                                    Component.literal("§7Todos os ciborgues livres"),
                                    Component.literal("§7convergem ao local.")
                            ),
                            List.of(
                                    Component.literal("§cEvacuação"),
                                    Component.literal("§7Força retorno imediato de"),
                                    Component.literal("§7todo o enxame às docas.")
                            ),
                            List.of(
                                    Component.literal("§aCoordenação"),
                                    Component.literal("§7Otimização equilibrada"),
                                    Component.literal("§7de tarefas sem bloqueios.")
                            ),
                            List.of(
                                    Component.literal("§eModo Standby"),
                                    Component.literal("§7Pausa imediata de rotinas"),
                                    Component.literal("§7e economia de energia WPT.")
                            )
                    );
                    extractor.setComponentTooltipForNextFrame(this.font, descs.get(i), mouseX, mouseY);
                    break;
                }
            }
        }
    }
}
