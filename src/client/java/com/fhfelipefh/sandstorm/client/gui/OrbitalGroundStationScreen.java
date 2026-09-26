package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.OrbitalGroundStationMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class OrbitalGroundStationScreen extends AbstractContainerScreen<OrbitalGroundStationMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 180;

    public OrbitalGroundStationScreen(OrbitalGroundStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 88;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int bx = this.leftPos + 98;
        int by = this.topPos + 70;
        int bw = 70;
        int bh = 14;

        if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
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
        renderRadarGlobe(extractor);
        renderConstellationBadges(extractor);
        renderKineticButton(extractor, mouseX, mouseY);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        extractor.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF00E5FF, false);
        extractor.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF78909C, false);
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA070B14);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        int gx = x + 8;
        int gy = y + 16;
        int gw = 160;
        int gh = 5;
        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int maxE = Math.max(1, this.menu.getMaxEnergy());
        int curE = Math.min(maxE, this.menu.getStoredEnergy());
        int energyW = (int) ((long) curE * gw / maxE);
        if (energyW > 0) {
            extractor.fill(gx, gy, gx + energyW, gy + gh, 0xFF00E5FF);
        }

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF00E5FF);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
        }
    }

    private void renderRadarGlobe(GuiGraphicsExtractor extractor) {
        int cx = this.leftPos + 32;
        int cy = this.topPos + 50;
        int r = 24;

        extractor.fill(cx - r, cy - r, cx + r, cy + r, 0xFF0B1320);
        extractor.fill(cx - r, cy, cx + r, cy + 1, 0x4400E5FF);
        extractor.fill(cx, cy - r, cx + 1, cy + r, 0x4400E5FF);

        long time = System.currentTimeMillis() / 80L;
        double angle1 = Math.toRadians(time % 360);
        int sat1X = cx + (int) (Math.cos(angle1) * 18);
        int sat1Y = cy + (int) (Math.sin(angle1) * 10);
        extractor.fill(sat1X - 1, sat1Y - 1, sat1X + 2, sat1Y + 2, 0xFFFFD700);

        if (this.menu.isLanceActive()) {
            double angle2 = Math.toRadians((time * 2 + 180) % 360);
            int sat2X = cx + (int) (Math.cos(angle2) * 14);
            int sat2Y = cy + (int) (Math.sin(angle2) * 14);
            extractor.fill(sat2X - 1, sat2Y - 1, sat2X + 2, sat2Y + 2, 0xFFFF1744);
        }

        int count = this.menu.getSatelliteCount();
        extractor.text(this.font, Component.literal(count + " SAT"), cx - 14, cy + r + 3, 0xFF00E5FF, false);
    }

    private void renderConstellationBadges(GuiGraphicsExtractor extractor) {
        int x = this.leftPos + 64;
        int y = this.topPos + 25;

        extractor.text(this.font, Component.literal("RECON: " + (this.menu.isWeatherActive() ? "ON" : "OFF")), x, y, this.menu.isWeatherActive() ? 0xFF00E676 : 0xFF546E7A, false);
        extractor.text(this.font, Component.literal("SOLAR: " + (this.menu.isSolarActive() ? "ON" : "OFF")), x, y + 10, this.menu.isSolarActive() ? 0xFFFFD700 : 0xFF546E7A, false);
        extractor.text(this.font, Component.literal("SAR GEO: " + (this.menu.isSarActive() ? "ON" : "OFF")), x, y + 20, this.menu.isSarActive() ? 0xFF00B0FF : 0xFF546E7A, false);
        extractor.text(this.font, Component.literal("CINÉTICO: " + (this.menu.isLanceActive() ? "ARM" : "OFF")), x, y + 30, this.menu.isLanceActive() ? 0xFFFF1744 : 0xFF546E7A, false);

        int secs = this.menu.getSecondsToStorm();
        String stormLabel = secs > 0 ? "STORM: " + secs + "s" : "RADAR LIMPO";
        int stormColor = secs > 0 && secs < 60 ? 0xFFFF1744 : (secs > 0 ? 0xFFFF9100 : 0xFF78909C);
        extractor.text(this.font, Component.literal(stormLabel), x, y + 42, stormColor, false);
    }

    private void renderKineticButton(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int bx = this.leftPos + 98;
        int by = this.topPos + 70;
        int bw = 70;
        int bh = 14;

        boolean canFire = this.menu.isLanceActive() && this.menu.getStoredEnergy() >= 100000;
        boolean hover = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;

        int borderColor = canFire ? (hover ? 0xFFFF5252 : 0xFFFF1744) : 0xFF37474F;
        int bgColor = canFire ? (hover ? 0xFF4A1010 : 0xFF2A0808) : 0xFF182026;
        int textColor = canFire ? 0xFFFFFFFF : 0xFF546E7A;

        extractor.fill(bx, by, bx + bw, by + bh, bgColor);
        extractor.fill(bx, by, bx + bw, by + 1, borderColor);
        extractor.fill(bx, by + bh - 1, bx + bw, by + bh, borderColor);
        extractor.fill(bx, by, bx + 1, by + bh, borderColor);
        extractor.fill(bx + bw - 1, by, bx + bw, by + bh, borderColor);

        extractor.text(this.font, Component.literal("DISPARO"), bx + 14, by + 3, textColor, false);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 16 && mouseY <= y + 21) {
            String energyText = "Energia: " + NumberFormat.compact(this.menu.getStoredEnergy()) + " / "
                    + NumberFormat.compact(this.menu.getMaxEnergy()) + " J";
            extractor.setTooltipForNextFrame(this.font, Component.literal(energyText), mouseX, mouseY);
        }

        int bx = x + 98;
        int by = y + 70;
        if (mouseX >= bx && mouseX <= bx + 70 && mouseY >= by && mouseY <= by + 14) {
            String tip = this.menu.isLanceActive() ? (this.menu.getStoredEnergy() >= 100000 ? "Disparar Lança Cinética Orbital (Custo: 100.000 J)" : "Energia Insuficiente (Requer 100.000 J)") : "Lança Cinética Inativa no Espaço";
            extractor.setTooltipForNextFrame(this.font, Component.literal(tip), mouseX, mouseY);
        }
    }
}
