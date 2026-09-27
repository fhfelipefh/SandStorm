package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import com.fhfelipefh.sandstorm.content.gui.CyborgTelemetryMenu;
import com.fhfelipefh.sandstorm.content.item.CyborgUpgradeItem;
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

public class CyborgTelemetryScreen extends AbstractContainerScreen<CyborgTelemetryMenu> {
    private static final int CHASSIS_WIDTH = 224;
    private static final int CHASSIS_HEIGHT = 214;

    public CyborgTelemetryScreen(CyborgTelemetryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, CHASSIS_WIDTH, CHASSIS_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 12;
        this.titleLabelY = 7;
        this.inventoryLabelX = 31;
        this.inventoryLabelY = 112;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        for (int i = 0; i < 5; i++) {
            int bx = x + 9 + i * 42;
            int bw = 39;
            int by = y + 38;
            int bh = 17;
            if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }
        }

        int dx = x + 104;
        int dy = y + 108;
        int dw = 54;
        int dh = 14;
        if (mx >= dx && mx <= dx + dw && my >= dy && my <= dy + dh) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 5);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.1f));
            }
            return true;
        }

        int cx = x + 162;
        int cy = y + 108;
        int cw = 54;
        int ch = 14;
        if (mx >= cx && mx <= cx + cw && my >= cy && my <= cy + ch) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 6);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.9f));
            }
            return true;
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderTelemetryGauges(extractor);
        renderRoutineButtons(extractor, mouseX, mouseY);
        renderQuickActionButtons(extractor, mouseX, mouseY);
        renderUpgradeBadges(extractor, mouseX, mouseY);
        renderTelemetryTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 24, 0xFF00E5FF);
        drawAdaptiveText(extractor, Component.translatable("gui.sandstorm.cyborg_telemetry.storage"), 31, 59, 65, 0xFF78909C);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CHASSIS_WIDTH - 24, 0xFF78909C);
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

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA0A0E17);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + 1, x + CHASSIS_WIDTH - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + CHASSIS_HEIGHT - 4, x + 4, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + CHASSIS_HEIGHT - 4, x + CHASSIS_WIDTH - 1, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);

        extractor.fill(x + 4, y + 4, x + CHASSIS_WIDTH - 4, y + 18, 0xDD101824);
        extractor.fill(x + 4, y + 18, x + CHASSIS_WIDTH - 4, y + 19, 0x8800E5FF);

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
        int gy = y + 21;
        int gh = 14;

        int gx = x + 9;
        int gw = 50;
        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);
        int energy = this.menu.getEnergy();
        int maxEnergy = this.menu.getMaxEnergy();
        int energyW = maxEnergy > 0 ? (int) ((long) energy * gw / maxEnergy) : 0;
        if (energyW > 0) {
            extractor.fill(gx, gy, gx + Math.min(energyW, gw), gy + gh, 0xFF00838F);
        }
        String energyStr = NumberFormat.compact(energy) + " J";
        int eTextW = this.font.width(energyStr);
        extractor.text(this.font, Component.literal(energyStr), gx + (gw - eTextW) / 2, gy + 3, 0xFF00E5FF, false);

        int cx = x + 62;
        int cw = 48;
        extractor.fill(cx - 1, gy - 1, cx + cw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(cx, gy, cx + cw, gy + gh, 0xFF05080E);
        int coolant = this.menu.getCoolant();
        int maxCoolant = this.menu.getMaxCoolant();
        int coolantW = maxCoolant > 0 ? (coolant * cw / maxCoolant) : 0;
        if (coolantW > 0) {
            extractor.fill(cx, gy, cx + Math.min(coolantW, cw), gy + gh, 0xFF0277BD);
        }
        String coolantStr = coolant + " mB";
        int cTextW = this.font.width(coolantStr);
        extractor.text(this.font, Component.literal(coolantStr), cx + (cw - cTextW) / 2, gy + 3, 0xFFB3E5FC, false);

        int hx = x + 113;
        int hw = 44;
        extractor.fill(hx - 1, gy - 1, hx + hw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(hx, gy, hx + hw, gy + gh, 0xFF05080E);
        int integrity = this.menu.getIntegrity();
        int integrityW = integrity * hw / 100;
        int integrityBarColor = integrity > 50 ? 0xFF2E7D32 : (integrity > 25 ? 0xFFF57F17 : 0xFFC62828);
        int integrityTextColor = integrity > 50 ? 0xFF69F0AE : (integrity > 25 ? 0xFFFFD54F : 0xFFFF5252);
        if (integrityW > 0) {
            extractor.fill(hx, gy, hx + Math.min(integrityW, hw), gy + gh, integrityBarColor);
        }
        String intStr = integrity + "%";
        int iTextW = this.font.width(intStr);
        extractor.text(this.font, Component.literal(intStr), hx + (hw - iTextW) / 2, gy + 3, integrityTextColor, false);

        int sx = x + 160;
        int sw = 55;
        CyborgSpecialty specialty = this.menu.getSpecialty();
        String specShort = switch (specialty) {
            case EXCAVATOR -> "ESCAV";
            case HARVESTER -> "AGRÍC";
            case BUILDER -> "CONSTR";
        };
        extractor.fill(sx - 1, gy - 1, sx + sw + 1, gy + gh + 1, 0xFF2E7D32);
        extractor.fill(sx, gy, sx + sw, gy + gh, 0xFF0D2818);
        int sTextW = this.font.width(specShort);
        extractor.text(this.font, Component.literal(specShort), sx + (sw - sTextW) / 2, gy + 3, 0xFF76FF03, false);
    }

    private void renderRoutineButtons(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        CyborgRoutine currentRoutine = this.menu.getRoutine();

        String[] labels = new String[]{"AUTO", "SEGUIR", "PATRUL", "DOCA", "ESPERA"};
        for (int i = 0; i < 5; i++) {
            int bx = x + 9 + i * 42;
            int bw = 39;
            int by = y + 38;
            int bh = 17;

            boolean isCurrent = currentRoutine.ordinal() == i;
            boolean hovered = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;

            int border = isCurrent ? 0xFF00E5FF : (hovered ? 0xFF80D8FF : 0xFF334155);
            int bg = isCurrent ? 0xFF0E3348 : (hovered ? 0xFF18293B : 0xFF0A0F18);
            int textCol = isCurrent ? 0xFF00E5FF : (hovered ? 0xFFFFFFFF : 0xFF94A3B8);

            extractor.fill(bx, by, bx + bw, by + bh, bg);
            extractor.fill(bx, by, bx + bw, by + 1, border);
            extractor.fill(bx, by + bh - 1, bx + bw, by + bh, border);
            extractor.fill(bx, by, bx + 1, by + bh, border);
            extractor.fill(bx + bw - 1, by, bx + bw, by + bh, border);

            int textW = this.font.width(labels[i]);
            int tx = bx + (bw - textW) / 2;
            int ty = by + 4;
            extractor.text(this.font, Component.literal(labels[i]), tx, ty, textCol, false);
        }
    }

    private void renderQuickActionButtons(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int dx = x + 104;
        int dy = y + 108;
        int dw = 54;
        int dh = 14;
        boolean dHovered = mouseX >= dx && mouseX <= dx + dw && mouseY >= dy && mouseY <= dy + dh;
        extractor.fill(dx, dy, dx + dw, dy + dh, dHovered ? 0xFF153347 : 0xFF0C1D2A);
        extractor.fill(dx, dy, dx + dw, dy + 1, dHovered ? 0xFF00E5FF : 0xFF2B5B75);
        extractor.fill(dx, dy + dh - 1, dx + dw, dy + dh, dHovered ? 0xFF00E5FF : 0xFF2B5B75);
        extractor.fill(dx, dy, dx + 1, dy + dh, dHovered ? 0xFF00E5FF : 0xFF2B5B75);
        extractor.fill(dx + dw - 1, dy, dx + dw, dy + dh, dHovered ? 0xFF00E5FF : 0xFF2B5B75);
        int dTextW = this.font.width("DESPEJAR");
        extractor.text(this.font, Component.literal("DESPEJAR"), dx + (dw - dTextW) / 2, dy + 3, dHovered ? 0xFF00E5FF : 0xFF80CBC4, false);

        int cx = x + 162;
        int cy = y + 108;
        int cw = 54;
        int ch = 14;
        boolean cHovered = mouseX >= cx && mouseX <= cx + cw && mouseY >= cy && mouseY <= cy + ch;
        extractor.fill(cx, cy, cx + cw, cy + ch, cHovered ? 0xFF421C22 : 0xFF261014);
        extractor.fill(cx, cy, cx + cw, cy + 1, cHovered ? 0xFFFF5252 : 0xFF7F2B33);
        extractor.fill(cx, cy + ch - 1, cx + cw, cy + ch, cHovered ? 0xFFFF5252 : 0xFF7F2B33);
        extractor.fill(cx, cy, cx + 1, cy + ch, cHovered ? 0xFFFF5252 : 0xFF7F2B33);
        extractor.fill(cx + cw - 1, cy, cx + cw, cy + ch, cHovered ? 0xFFFF5252 : 0xFF7F2B33);
        int cTextW = this.font.width("LIMPAR");
        extractor.text(this.font, Component.literal("LIMPAR"), cx + (cw - cTextW) / 2, dy + 3, cHovered ? 0xFFFF8A80 : 0xFFEF5350, false);
    }

    private void renderUpgradeBadges(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos + CHASSIS_WIDTH + 3;
        int y = this.topPos + 21;
        int width = 24;
        int height = 88;

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
            int by = y + 3 + i * 21;
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
        int gy = y + 21;

        if (mouseX >= x + 9 && mouseX <= x + 59 && mouseY >= gy && mouseY <= gy + 14) {
            Component tip = Component.literal("Energia WPT: " + this.menu.getEnergy() + " / " + this.menu.getMaxEnergy() + " J");
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 62 && mouseX <= x + 110 && mouseY >= gy && mouseY <= gy + 14) {
            Component tip = Component.literal("Bio-Refrigerante: " + this.menu.getCoolant() + " / " + this.menu.getMaxCoolant() + " mB");
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 113 && mouseX <= x + 157 && mouseY >= gy && mouseY <= gy + 14) {
            Component tip = Component.literal("Integridade Miomérica: " + this.menu.getIntegrity() + "%");
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 160 && mouseX <= x + 215 && mouseY >= gy && mouseY <= gy + 14) {
            String specName = switch (this.menu.getSpecialty()) {
                case BUILDER -> "Ciborgue Construtor";
                case EXCAVATOR -> "Ciborgue Escavador";
                case HARVESTER -> "Ciborgue Agrícola";
            };
            Component tip = Component.literal("Especialidade: " + specName);
            extractor.setTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else {
            for (int i = 0; i < 5; i++) {
                int bx = x + 9 + i * 42;
                int bw = 39;
                int by = y + 38;
                int bh = 17;
                if (mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh) {
                    List<List<Component>> routineTooltips = List.of(
                            List.of(
                                    Component.literal("§bRotina de Trabalho Autônomo"),
                                    Component.literal("§7Executa tarefas de acordo com"),
                                    Component.literal("§7a especialidade (Mineração,"),
                                    Component.literal("§7Agricultura ou Construção).")
                            ),
                            List.of(
                                    Component.literal("§bSeguir Operador"),
                                    Component.literal("§7Acompanha o operador de perto,"),
                                    Component.literal("§7adapta velocidade ao sprint e"),
                                    Component.literal("§7defende contra ameaças.")
                            ),
                            List.of(
                                    Component.literal("§bPatrulhar Perímetro"),
                                    Component.literal("§7Circula na área demarcada e"),
                                    Component.literal("§7alerta sobre anomalias"),
                                    Component.literal("§7sísmicas e ameaças.")
                            ),
                            List.of(
                                    Component.literal("§bRetornar à Doca"),
                                    Component.literal("§7Retorna imediatamente à doca"),
                                    Component.literal("§7mais próxima para recarga e"),
                                    Component.literal("§7descarregamento de carga.")
                            ),
                            List.of(
                                    Component.literal("§bModo de Espera (Standby)"),
                                    Component.literal("§7Permanece imóvel no local"),
                                    Component.literal("§7aguardando ordens remotas.")
                            )
                    );
                    extractor.setComponentTooltipForNextFrame(this.font, routineTooltips.get(i), mouseX, mouseY);
                    return;
                }
            }

            int dx = x + 104;
            int dy = y + 108;
            int dw = 54;
            int dh = 14;
            if (mouseX >= dx && mouseX <= dx + dw && mouseY >= dy && mouseY <= dy + dh) {
                extractor.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("§bDespejar Itens"),
                        Component.literal("§7Transfere todos os itens"),
                        Component.literal("§7do androide para seu inventário.")
                ), mouseX, mouseY);
                return;
            }

            int cx = x + 162;
            int cy = y + 108;
            int cw = 54;
            int ch = 14;
            if (mouseX >= cx && mouseX <= cx + cw && mouseY >= cy && mouseY <= cy + ch) {
                extractor.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("§cLimpar Zona de Trabalho"),
                        Component.literal("§7Remove a área demarcada de"),
                        Component.literal("§7mineração ou construção.")
                ), mouseX, mouseY);
                return;
            }

            int ux = this.leftPos + CHASSIS_WIDTH + 3;
            int uy = this.topPos + 21;
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
                int by = uy + 3 + i * 21;
                int bw = 18;
                int bh = 18;
                if (mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh) {
                    boolean installed = this.menu.hasUpgrade(types[i]);
                    if (installed) {
                        extractor.setComponentTooltipForNextFrame(this.font, List.of(
                                Component.literal("§a[INSTALADO] §b" + upgradeNames[i]),
                                Component.literal("§7" + upgradeEffects[i])
                        ), mouseX, mouseY);
                    } else {
                        extractor.setComponentTooltipForNextFrame(this.font, List.of(
                                Component.literal("§8[VAZIO] §7Slot de Módulo:"),
                                Component.literal("§f" + upgradeNames[i]),
                                Component.literal("§8Clique com o item no ciborgue.")
                        ), mouseX, mouseY);
                    }
                    return;
                }
            }
        }
    }
}
