package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.CyborgDockingStationMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CyborgDockingStationScreen extends AbstractContainerScreen<CyborgDockingStationMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 180;

    public CyborgDockingStationScreen(CyborgDockingStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 86;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        if (this.menu.isDocked()) {
            int bx = x + 104;
            int by = y + 62;
            int bw = 64;
            int bh = 14;
            if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
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
        renderHoloTerminal(extractor, mouseX, mouseY);
        renderDockTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        extractor.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF00E5FF, false);
        extractor.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF78909C, false);
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA0A0E17);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + 1, x + CHASSIS_WIDTH - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + CHASSIS_HEIGHT - 4, x + 4, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + CHASSIS_HEIGHT - 4, x + CHASSIS_WIDTH - 1, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);

        extractor.fill(x + 4, y + 4, x + CHASSIS_WIDTH - 4, y + 17, 0xDD101824);
        extractor.fill(x + 4, y + 17, x + CHASSIS_WIDTH - 4, y + 18, 0x8800E5FF);

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
        }
    }

    private void renderHoloTerminal(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int gx = x + 8;
        int gy = y + 21;
        int gw = 160;
        int gh = 10;

        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int dockEnergyW = this.menu.getDockEnergyScaled(gw);
        if (dockEnergyW > 0) {
            extractor.fill(gx, gy, gx + dockEnergyW, gy + gh, 0xFF00E5FF);
        }
        String dockEnergyStr = "Capacitor: " + NumberFormat.compact(this.menu.getDockEnergy()) + " / " + NumberFormat.compact(this.menu.getMaxDockEnergy()) + " J";
        extractor.text(this.font, Component.literal(dockEnergyStr), gx + 4, gy + 1, 0xFFFFFFFF, false);

        int tx = x + 8;
        int ty = y + 34;
        int tw = 160;
        int th = 46;

        extractor.fill(tx - 1, ty - 1, tx + tw + 1, ty + th + 1, 0xFF1E293B);
        extractor.fill(tx, ty, tx + tw, ty + th, 0xEE080D14);

        if (this.menu.isDocked()) {
            extractor.fill(tx, ty, tx + tw, ty + 12, 0x4476FF03);
            extractor.fill(tx, ty + 12, tx + tw, ty + 13, 0xFF76FF03);
            extractor.text(this.font, Component.literal("§a● CIBORGUE ACOPLADO [WPT ATIVO]"), tx + 4, ty + 2, 0xFF76FF03, false);

            int cx = tx + 4;
            int cy = ty + 16;
            int cw = 92;
            int ch = 10;

            extractor.fill(cx - 1, cy - 1, cx + cw + 1, cy + ch + 1, 0xFF1E293B);
            extractor.fill(cx, cy, cx + cw, cy + ch, 0xFF05080E);
            int cybEnergyW = this.menu.getCyborgEnergyScaled(cw);
            if (cybEnergyW > 0) {
                extractor.fill(cx, cy, cx + cybEnergyW, cy + ch, 0xFF00E5FF);
            }
            String cEnergyStr = NumberFormat.compact(this.menu.getCyborgEnergy()) + " J";
            extractor.text(this.font, Component.literal(cEnergyStr), cx + 2, cy + 1, 0xFFE0F7FA, false);

            int iy = ty + 29;
            extractor.fill(cx - 1, iy - 1, cx + cw + 1, iy + ch + 1, 0xFF1E293B);
            extractor.fill(cx, iy, cx + cw, iy + ch, 0xFF05080E);
            int cybIntW = this.menu.getCyborgIntegrityScaled(cw);
            int intColor = this.menu.getCyborgIntegrity() > 50 ? 0xFF00E676 : 0xFFFFB300;
            if (cybIntW > 0) {
                extractor.fill(cx, iy, cx + cybIntW, iy + ch, intColor);
            }
            String cIntStr = "Miômeros: " + this.menu.getCyborgIntegrity() + "%";
            extractor.text(this.font, Component.literal(cIntStr), cx + 2, iy + 1, 0xFFFFFFFF, false);

            int bx = tx + 100;
            int by = ty + 16;
            int bw = 56;
            int bh = 24;
            boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;

            int btnBorder = hovered ? 0xFFFF5252 : 0xFFD32F2F;
            int btnBg = hovered ? 0xDD5C1414 : 0xAA3B0D0D;

            extractor.fill(bx, by, bx + bw, by + bh, btnBg);
            extractor.fill(bx, by, bx + bw, by + 1, btnBorder);
            extractor.fill(bx, by + bh - 1, bx + bw, by + bh, btnBorder);
            extractor.fill(bx, by, bx + 1, by + bh, btnBorder);
            extractor.fill(bx + bw - 1, by, bx + bw, by + bh, btnBorder);

            String btnText = "DESACOPLAR";
            int twBtn = this.font.width(btnText);
            extractor.text(this.font, Component.literal(btnText), bx + (bw - twBtn) / 2, by + 8, 0xFFFFFFFF, false);
        } else {
            extractor.fill(tx, ty, tx + tw, ty + 12, 0x44FFD600);
            extractor.fill(tx, ty + 12, tx + tw, ty + 13, 0xFFFFD600);
            extractor.text(this.font, Component.literal("§e○ STANDBY - AGUARDANDO ENXAME"), tx + 4, ty + 2, 0xFFFFD600, false);

            extractor.text(this.font, Component.literal("§7Nenhum ciborgue acoplado."), tx + 6, ty + 18, 0xFF90A4AE, false);
            extractor.text(this.font, Component.literal("§8Ciborgues com baixa energia"), tx + 6, ty + 28, 0xFF607D8B, false);
            extractor.text(this.font, Component.literal("§8se acoplam automaticamente."), tx + 6, ty + 37, 0xFF607D8B, false);
        }
    }

    private void renderDockTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 21 && mouseY <= y + 31) {
            Component tip = Component.literal("Capacitor WPT Doca: " + this.menu.getDockEnergy() + " / " + this.menu.getMaxDockEnergy() + " J");
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (this.menu.isDocked()) {
            if (mouseX >= x + 12 && mouseX <= x + 104 && mouseY >= y + 50 && mouseY <= y + 60) {
                Component tip = Component.literal("Bateria Ciborgue: " + this.menu.getCyborgEnergy() + " / " + this.menu.getCyborgMaxEnergy() + " J");
                extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            } else if (mouseX >= x + 12 && mouseX <= x + 104 && mouseY >= y + 63 && mouseY <= y + 73) {
                Component tip = Component.literal("Integridade Miomérica: " + this.menu.getCyborgIntegrity() + "%");
                extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            } else if (mouseX >= x + 108 && mouseX <= x + 164 && mouseY >= y + 50 && mouseY <= y + 74) {
                Component tip = Component.literal("§cForçar desacoplamento imediato do ciborgue.");
                extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            }
        }
    }
}
