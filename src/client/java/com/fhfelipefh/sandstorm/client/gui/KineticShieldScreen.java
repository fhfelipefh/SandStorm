package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.KineticShieldMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class KineticShieldScreen extends AbstractContainerScreen<KineticShieldMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 166;

    public KineticShieldScreen(KineticShieldMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 10;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        int bx = x + 62;
        int by = y + 58;
        int bw = 76;
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
        renderRadarAndTelemetry(extractor, mouseX, mouseY);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 20, 0xFF00E5FF);
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

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA08111A);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + 1, x + CHASSIS_WIDTH - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + CHASSIS_HEIGHT - 4, x + 4, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + CHASSIS_HEIGHT - 4, x + CHASSIS_WIDTH - 1, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);

        int gx = x + 8;
        int gy = y + 15;
        int gw = 160;
        int gh = 4;
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

    private void renderRadarAndTelemetry(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int rcx = x + 34;
        int rcy = y + 44;
        int r = 18;
        boolean active = this.menu.isShieldActive();

        extractor.fill(rcx - r - 2, rcy - r - 2, rcx + r + 2, rcy + r + 2, 0xFF050D18);
        extractor.fill(rcx - r - 2, rcy - r - 2, rcx + r + 2, rcy - r - 1, 0xFF1E293B);
        extractor.fill(rcx - r - 2, rcy + r + 1, rcx + r + 2, rcy + r + 2, 0xFF1E293B);
        extractor.fill(rcx - r - 2, rcy - r - 2, rcx - r - 1, rcy + r + 2, 0xFF1E293B);
        extractor.fill(rcx + r + 1, rcy - r - 2, rcx + r + 2, rcy + r + 2, 0xFF1E293B);

        int ringColor = active ? 0xFF00E5FF : 0xFF334155;
        extractor.fill(rcx - r, rcy, rcx + r, rcy + 1, ringColor);
        extractor.fill(rcx, rcy - r, rcx + 1, rcy + r, ringColor);
        extractor.fill(rcx - 1, rcy - 1, rcx + 2, rcy + 2, 0xFF00E5FF);

        if (active) {
            long time = System.currentTimeMillis() / 40;
            double angle = (time % 100) * (Math.PI * 2 / 100.0);
            int sweepX = rcx + (int) (Math.cos(angle) * (r - 4));
            int sweepY = rcy + (int) (Math.sin(angle) * (r - 4));
            extractor.fill(sweepX - 1, sweepY - 1, sweepX + 2, sweepY + 2, 0xFF38BDF8);
        }

        String statusText;
        int statusColor;
        if (!this.menu.isUserEnabled()) {
            statusText = "STATUS: DESLIGADO";
            statusColor = 0xFFF59E0B;
        } else if (this.menu.getStoredEnergy() < 5) {
            statusText = "SEM ENERGIA";
            statusColor = 0xFFEF4444;
        } else {
            statusText = "STATUS: ATIVO [20m]";
            statusColor = 0xFF00E676;
        }
        drawAdaptiveText(extractor, Component.literal(statusText), x + 62, y + 24, 76, statusColor);

        String defText = String.format("DEFLEXÕES: %d", this.menu.getTotalDeflections());
        drawAdaptiveText(extractor, Component.literal(defText), x + 62, y + 35, 76, 0xFF94A3B8);

        drawAdaptiveText(extractor, Component.literal("BARREIRA: 20m"), x + 62, y + 46, 76, 0xFF38BDF8);

        drawAdaptiveText(extractor, Component.literal("BATERIA"), x + 142, y + 26, 26, 0xFF64748B);

        int bx = x + 62;
        int by = y + 58;
        int bw = 76;
        int bh = 14;
        boolean userOn = this.menu.isUserEnabled();
        boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;

        int btnBorder = userOn ? (hovered ? 0xFFFF5252 : 0xFFEF4444) : (hovered ? 0xFF00E5FF : 0xFF0284C7);
        int btnBg = userOn ? (hovered ? 0xDD5C1414 : 0xAA3B0D0D) : (hovered ? 0xDD0D324D : 0xAA081F30);
        int btnTextCol = hovered ? 0xFFFFFFFF : 0xFFE2E8F0;

        extractor.fill(bx, by, bx + bw, by + bh, btnBg);
        extractor.fill(bx, by, bx + bw, by + 1, btnBorder);
        extractor.fill(bx, by + bh - 1, bx + bw, by + bh, btnBorder);
        extractor.fill(bx, by + 1, bx + 1, by + bh, btnBorder);
        extractor.fill(bx + bw - 1, by, bx + bw, by + bh, btnBorder);

        String btnLabel = userOn ? "DESATIVAR" : "ATIVAR";
        int tw = this.font.width(btnLabel);
        extractor.text(this.font, Component.literal(btnLabel), bx + (bw - tw) / 2, by + 3, btnTextCol, false);
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 14 && mouseY <= y + 20) {
            String wptInfo = this.menu.isWptConnected() ? " §a[⚡ WPT Ativa]" : " §7[Sem WPT]";
            String energyTip = String.format("Energia: %s / %s J%s", NumberFormat.compact(this.menu.getStoredEnergy()), NumberFormat.compact(this.menu.getMaxEnergy()), wptInfo);
            extractor.setTooltipForNextFrame(this.font, Component.literal(energyTip), mouseX, mouseY);
        } else if (mouseX >= x + 14 && mouseX <= x + 54 && mouseY >= y + 24 && mouseY <= y + 64) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§bCampo Defensivo Cinético"),
                    Component.literal("§7Raio de Projeção: 20 metros"),
                    Component.literal("§7Deflete projéteis balísticos e"),
                    Component.literal("§7amortece vibrações sísmicas.")
            ), mouseX, mouseY);
        } else if (mouseX >= x + 147 && mouseX <= x + 165 && mouseY >= y + 39 && mouseY <= y + 57) {
            if (!this.menu.slots.get(0).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.literal("Slot de Bateria / Célula de Energia"), mouseX, mouseY);
            }
        }
    }
}
