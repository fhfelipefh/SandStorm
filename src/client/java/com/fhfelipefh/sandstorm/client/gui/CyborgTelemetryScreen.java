package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import com.fhfelipefh.sandstorm.content.gui.CyborgTelemetryMenu;
import com.fhfelipefh.sandstorm.content.item.CyborgUpgradeItem;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CyborgTelemetryScreen extends AbstractContainerScreen<CyborgTelemetryMenu> {
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 186;

    public CyborgTelemetryScreen(CyborgTelemetryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 5;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 93;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        for (int i = 0; i < 4; i++) {
            int bx = x + 8 + i * 40;
            int bw = 38;
            int by = y + 37;
            int bh = 13;
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
        renderTelemetryGauges(extractor);
        renderRoutineButtons(extractor, mouseX, mouseY);
        renderUpgradeBadges(extractor, mouseX, mouseY);
        renderTelemetryTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        CyborgSpecialty specialty = this.menu.getSpecialty();
        String specialtyLabel = specialty.name();
        Component specText = Component.literal(specialtyLabel);
        extractor.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF00E5FF, false);
        int specW = this.font.width(specText);
        extractor.text(this.font, specText, CHASSIS_WIDTH - specW - 8, this.titleLabelY, 0xFF76FF03, false);
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

        extractor.fill(x + 4, y + 4, x + CHASSIS_WIDTH - 4, y + 16, 0xDD101824);
        extractor.fill(x + 4, y + 16, x + CHASSIS_WIDTH - 4, y + 17, 0x8800E5FF);

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            boolean isCyborgSlot = slot.index < CyborgTelemetryMenu.CYBORG_SLOTS;
            int borderColor = isCyborgSlot ? 0xFF00E5FF : 0xFF1E293B;
            int bgColor = isCyborgSlot ? 0xDD0D131F : 0xAA080C14;

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }
    }

    private void renderTelemetryGauges(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int gx = x + 8;
        int gy = y + 19;
        int gw = 48;
        int gh = 14;

        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int energy = this.menu.getEnergy();
        int maxEnergy = this.menu.getMaxEnergy();
        int energyW = maxEnergy > 0 ? (int) ((long) energy * gw / maxEnergy) : 0;
        if (energyW > 0) {
            extractor.fill(gx, gy, gx + energyW, gy + gh, 0xFF00E5FF);
        }
        String energyStr = NumberFormat.compact(energy) + " J";
        extractor.text(this.font, Component.literal(energyStr), gx + 3, gy + 3, 0xFFFFFFFF, false);

        int cx = x + 62;
        int cw = 48;
        extractor.fill(cx - 1, gy - 1, cx + cw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(cx, gy, cx + cw, gy + gh, 0xFF05080E);
        int coolant = this.menu.getCoolant();
        int maxCoolant = this.menu.getMaxCoolant();
        int coolantW = maxCoolant > 0 ? (coolant * cw / maxCoolant) : 0;
        if (coolantW > 0) {
            extractor.fill(cx, gy, cx + coolantW, gy + gh, 0xFF0288D1);
        }
        String coolantStr = coolant + " mB";
        extractor.text(this.font, Component.literal(coolantStr), cx + 3, gy + 3, 0xFFB3E5FC, false);

        int hx = x + 116;
        int hw = 36;
        extractor.fill(hx - 1, gy - 1, hx + hw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(hx, gy, hx + hw, gy + gh, 0xFF05080E);
        int integrity = this.menu.getIntegrity();
        int integrityW = integrity * hw / 100;
        int integrityColor = integrity > 50 ? 0xFF00E676 : (integrity > 25 ? 0xFFFFB300 : 0xFFFF1744);
        if (integrityW > 0) {
            extractor.fill(hx, gy, hx + integrityW, gy + gh, integrityColor);
        }
        String intStr = integrity + "%";
        extractor.text(this.font, Component.literal(intStr), hx + 3, gy + 3, 0xFFFFFFFF, false);

        int vx = x + 156;
        int vw = 12;
        int vh = 14;
        extractor.fill(vx - 1, gy - 1, vx + vw + 1, gy + vh + 1, 0xFF37474F);
        extractor.fill(vx, gy, vx + vw, gy + vh, 0xFF000000);
        int visorColor = getVisorHex(this.menu.getVisorColor());
        extractor.fill(vx + 2, gy + 3, vx + vw - 2, gy + vh - 3, visorColor);
    }

    private int getVisorHex(int visorCode) {
        if (visorCode == 1) {
            return 0xFFFFB300;
        }
        if (visorCode == 2) {
            return 0xFFFF1744;
        }
        if (visorCode == 3) {
            return 0xFFD500F9;
        }
        return 0xFF00E5FF;
    }

    private void renderRoutineButtons(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        CyborgRoutine currentRoutine = this.menu.getRoutine();

        String[] labels = new String[]{"AUTO", "SEGUIR", "PATRULHA", "DOCA"};
        for (int i = 0; i < 4; i++) {
            int bx = x + 8 + i * 40;
            int bw = 38;
            int by = y + 37;
            int bh = 13;

            boolean isCurrent = currentRoutine.ordinal() == i;
            boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;

            int border = isCurrent ? 0xFF00E5FF : (hovered ? 0xFF80D8FF : 0xFF334155);
            int bg = isCurrent ? 0xFF0D2838 : (hovered ? 0xFF14202C : 0xFF0A0F18);
            int textCol = isCurrent ? 0xFF00E5FF : (hovered ? 0xFFFFFFFF : 0xFF90A4AE);

            extractor.fill(bx, by, bx + bw, by + bh, bg);
            extractor.fill(bx, by, bx + bw, by + 1, border);
            extractor.fill(bx, by + bh - 1, bx + bw, by + bh, border);
            extractor.fill(bx, by, bx + 1, by + bh, border);
            extractor.fill(bx + bw - 1, by, bx + bw, by + bh, border);

            int textW = this.font.width(labels[i]);
            int tx = bx + (bw - textW) / 2;
            int ty = by + 3;
            extractor.text(this.font, Component.literal(labels[i]), tx, ty, textCol, false);
        }
    }

    private void renderUpgradeBadges(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos + CHASSIS_WIDTH + 2;
        int y = this.topPos + 19;
        int width = 24;
        int height = 90;

        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFA0A0E17);
        extractor.fill(x, y, x + width, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + height - 1, x + width, y + height, 0xFF00E5FF);

        CyborgUpgradeItem.CyborgUpgradeType[] types = CyborgUpgradeItem.CyborgUpgradeType.values();
        String[] badgeLabels = new String[]{"AC", "CR", "LR", "PZ"};
        int[] activeColors = new int[]{0xFF76FF03, 0xFF00E5FF, 0xFFFFD600, 0xFFD500F9};
        int[] activeBgs = new int[]{0x33103810, 0x3300384D, 0x334D3C00, 0x333D004D};

        for (int i = 0; i < types.length; i++) {
            boolean installed = this.menu.hasUpgrade(types[i]);
            int bx = x + 3;
            int by = y + 4 + i * 21;
            int bw = 18;
            int bh = 18;

            int border = installed ? activeColors[i] : 0xFF1E293B;
            int bg = installed ? activeBgs[i] : 0xFF080C14;
            int textCol = installed ? activeColors[i] : 0xFF475569;

            extractor.fill(bx - 1, by - 1, bx + bw + 1, by + bh + 1, border);
            extractor.fill(bx, by, bx + bw, by + bh, bg);

            int textW = this.font.width(badgeLabels[i]);
            int tx = bx + (bw - textW) / 2;
            int ty = by + 5;
            extractor.text(this.font, Component.literal(badgeLabels[i]), tx, ty, textCol, false);
        }
    }

    private void renderTelemetryTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 8 && mouseX <= x + 56 && mouseY >= y + 19 && mouseY <= y + 33) {
            Component tip = Component.literal("Energia WPT: " + this.menu.getEnergy() + " / " + this.menu.getMaxEnergy() + " J");
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 62 && mouseX <= x + 110 && mouseY >= y + 19 && mouseY <= y + 33) {
            Component tip = Component.literal("Bio-Refrigerante: " + this.menu.getCoolant() + " / " + this.menu.getMaxCoolant() + " mB");
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 116 && mouseX <= x + 152 && mouseY >= y + 19 && mouseY <= y + 33) {
            Component tip = Component.literal("Integridade Miomérica: " + this.menu.getIntegrity() + "%");
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 156 && mouseX <= x + 168 && mouseY >= y + 19 && mouseY <= y + 33) {
            Component tip = Component.literal("Visor Óptico: " + this.menu.getRoutine().name());
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else {
            int ux = this.leftPos + CHASSIS_WIDTH + 2;
            int uy = this.topPos + 19;
            CyborgUpgradeItem.CyborgUpgradeType[] types = CyborgUpgradeItem.CyborgUpgradeType.values();
            String[] upgradeNames = new String[]{
                "Blindagem de Quitina Ácida",
                "Célula Crio-Trealose",
                "Lente LiDAR Longo Alcance",
                "Propulsor Hover Piezoelétrico"
            };
            String[] upgradeEffects = new String[]{
                "+50% Resistência Dano/Ácido & Imunidade Veneno",
                "Energia Expandida: 150.000 J (3x Buffer)",
                "Radar Operacional Expandido: 96m (3x Alcance)",
                "Supressão Total Dano de Queda & Levitação"
            };

            for (int i = 0; i < types.length; i++) {
                int bx = ux + 3;
                int by = uy + 4 + i * 21;
                int bw = 18;
                int bh = 18;
                if (mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh) {
                    boolean installed = this.menu.hasUpgrade(types[i]);
                    if (installed) {
                        Component tip = Component.literal("§a[INSTALADO] §b" + upgradeNames[i] + "\n§7" + upgradeEffects[i]);
                        extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
                    } else {
                        Component tip = Component.literal("§8[VAZIO] §7Slot de Módulo: " + upgradeNames[i] + "\n§8Clique com o item no ciborgue para instalar.");
                        extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
                    }
                    return;
                }
            }
        }
    }
}
