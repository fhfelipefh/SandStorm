package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.QuantumSleeperMenu;
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

public class QuantumSleeperScreen extends AbstractContainerScreen<QuantumSleeperMenu> {
    private static final int CHASSIS_WIDTH = 216;
    private static final int CHASSIS_HEIGHT = 220;

    public QuantumSleeperScreen(QuantumSleeperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, CHASSIS_WIDTH, CHASSIS_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 5;
        this.inventoryLabelX = 27;
        this.inventoryLabelY = 124;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        int btnX = x + 58;
        int btnY = y + 70;
        int btnW = 84;
        int btnH = 22;

        if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                if (!this.menu.hasClone()) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                } else {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_TELEPORT, 1.2f));
                }
            }
            return true;
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderGhostSlotIcons(extractor);
        renderCenterHoloPod(extractor);
        renderSideBarsAndIcons(extractor);
        renderTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 16, 0xFF00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CHASSIS_WIDTH - 54, 0xFF78909C);
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

        extractor.fill(x + 4, y + 4, x + CHASSIS_WIDTH - 4, y + 16, 0xDD101824);
        extractor.fill(x + 4, y + 16, x + CHASSIS_WIDTH - 4, y + 17, 0x8800E5FF);

        for (Slot slot : this.menu.slots) {
            if (slot.x >= 0 && slot.y >= 0) {
                int sx = x + slot.x;
                int sy = y + slot.y;
                extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
                extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
            }
        }

        int nx = x + 176;
        int ny = y + 62;
        extractor.fill(nx - 1, ny - 1, nx + 17, ny + 17, 0xFF00E676);
        extractor.fill(nx, ny, nx + 16, ny + 16, 0xAA082014);

        extractor.fill(x + 25, y + 99, x + 191, y + 100, 0x4400E5FF);
    }

    private void renderGhostSlotIcons(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        drawGhostOutline(extractor, x + 12, y + 20, 39);
        drawGhostOutline(extractor, x + 12, y + 40, 38);
        drawGhostOutline(extractor, x + 12, y + 60, 37);
        drawGhostOutline(extractor, x + 12, y + 80, 36);
        drawGhostOutline(extractor, x + 32, y + 50, 40);

        Slot nutSlot = this.menu.slots.get(QuantumSleeperMenu.NUTRIENT_SLOT);
        if (!nutSlot.hasItem()) {
            drawVialIcon(extractor, x + 180, y + 65);
        }
    }

    private void drawGhostOutline(GuiGraphicsExtractor extractor, int sx, int sy, int slotIdx) {
        Slot s = this.menu.slots.get(slotIdx);
        if (s.hasItem()) {
            return;
        }
        int c = 0x4400E5FF;
        switch (slotIdx) {
            case 39 -> {
                extractor.fill(sx + 4, sy + 3, sx + 12, sy + 5, c);
                extractor.fill(sx + 3, sy + 5, sx + 13, sy + 11, c);
                extractor.fill(sx + 5, sy + 11, sx + 11, sy + 13, c);
            }
            case 38 -> {
                extractor.fill(sx + 3, sy + 3, sx + 13, sy + 6, c);
                extractor.fill(sx + 4, sy + 6, sx + 12, sy + 12, c);
                extractor.fill(sx + 5, sy + 12, sx + 11, sy + 13, c);
            }
            case 37 -> {
                extractor.fill(sx + 4, sy + 3, sx + 12, sy + 6, c);
                extractor.fill(sx + 4, sy + 6, sx + 7, sy + 13, c);
                extractor.fill(sx + 9, sy + 6, sx + 12, sy + 13, c);
            }
            case 36 -> {
                extractor.fill(sx + 4, sy + 4, sx + 7, sy + 10, c);
                extractor.fill(sx + 9, sy + 4, sx + 12, sy + 10, c);
                extractor.fill(sx + 3, sy + 10, sx + 7, sy + 12, c);
                extractor.fill(sx + 9, sy + 10, sx + 13, sy + 12, c);
            }
            case 40 -> {
                extractor.fill(sx + 4, sy + 3, sx + 12, sy + 5, c);
                extractor.fill(sx + 3, sy + 5, sx + 13, sy + 10, c);
                extractor.fill(sx + 5, sy + 10, sx + 11, sy + 12, c);
                extractor.fill(sx + 7, sy + 12, sx + 9, sy + 13, c);
            }
            default -> {}
        }
    }

    private void renderCenterHoloPod(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int px = x + 54;
        int py = y + 19;
        int pw = 92;
        int ph = 77;

        extractor.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, 0xFF1E293B);
        extractor.fill(px, py, px + pw, py + ph, 0xEE080D14);

        int ox1 = px + 4;
        int oy1 = py + 4;
        int ox2 = px + pw - 4;
        int oy2 = py + 26;
        int ow = ox2 - ox1;

        extractor.fill(ox1 - 1, oy1 - 1, ox2 + 1, oy2 + 1, 0x8800E676);
        extractor.fill(ox1, oy1, ox2, oy2, 0xF003120A);
        extractor.fill(ox1, oy1 + 7, ox2, oy1 + 8, 0x3000E676);
        extractor.fill(ox1, oy1 + 14, ox2, oy1 + 15, 0x3000E676);

        boolean hasClone = this.menu.hasClone();
        int tick = (int) ((System.currentTimeMillis() / 50) % ow);
        int baselineY = oy1 + 11;

        for (int p = 0; p < ow; p++) {
            int waveY = baselineY;
            if (hasClone) {
                int phase = (p + tick) % 24;
                if (phase == 6) {
                    waveY -= 2;
                } else if (phase == 8) {
                    waveY += 2;
                } else if (phase == 10) {
                    waveY -= 8;
                } else if (phase == 12) {
                    waveY += 5;
                } else if (phase == 15) {
                    waveY -= 2;
                }
            } else {
                int phase = (p + tick) % 32;
                if (phase == 16) {
                    waveY -= 2;
                } else if (phase == 18) {
                    waveY += 2;
                }
            }
            int color = (p == tick) ? 0xFFFFFFFF : (hasClone ? 0xFF00FF88 : 0xFF00B0FF);
            extractor.fill(ox1 + p, waveY, ox1 + p + 1, waveY + 1, color);
        }

        int ledX = px + 8;
        int ledY = py + 34;
        int ledColor = hasClone ? 0xFF00E676 : 0xFFFFB300;
        extractor.fill(ledX, ledY, ledX + 4, ledY + 4, ledColor);
        extractor.fill(ledX + 1, ledY + 1, ledX + 3, ledY + 3, 0xFFFFFFFF);

        Component statusBadge = hasClone
                ? Component.literal("CLONE 100%")
                : Component.literal("DISPONÍVEL");
        int badgeColor = hasClone ? 0xFF00E676 : 0xFFFFB300;
        extractor.text(this.font, statusBadge, ledX + 8, ledY - 2, badgeColor, false);

        int btnX = px + 4;
        int btnY = py + 49;
        int btnW = pw - 8;
        int btnH = 22;

        boolean hovered = isHovered(btnX, btnY, btnW, btnH);
        int btnBg = hasClone
                ? (hovered ? 0xFF00796B : 0xDD004D40)
                : (hovered ? 0xFF2E7D32 : 0xDD1B5E20);
        int btnBorder = hasClone
                ? (hovered ? 0xFF80D8FF : 0xFF00E5FF)
                : (hovered ? 0xFF69F0AE : 0xFF00E676);
        Component btnText = hasClone
                ? Component.translatable("gui.sandstorm.quantum_sleeper.ego_cast")
                : Component.translatable("gui.sandstorm.quantum_sleeper.synthesize");

        extractor.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
        extractor.fill(btnX, btnY, btnX + btnW, btnY + 1, btnBorder);
        extractor.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, btnBorder);
        extractor.fill(btnX, btnY + 1, btnX + 1, btnY + btnH, btnBorder);
        extractor.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, btnBorder);

        int textW = this.font.width(btnText);
        int textX = textW < btnW - 4 ? btnX + (btnW - textW) / 2 : btnX + 2;
        drawAdaptiveText(extractor, btnText, textX, btnY + 7, btnW - 4, 0xFFFFFFFF);
    }

    private void renderSideBarsAndIcons(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        drawLightningIcon(extractor, x + 154, y + 21);

        int ex = x + 168;
        int ey = y + 21;
        int ew = 38;
        int eh = 12;

        extractor.fill(ex - 1, ey - 1, ex + ew + 1, ey + eh + 1, 0xFF1E293B);
        extractor.fill(ex, ey, ex + ew, ey + eh, 0xFF05080E);
        int energyW = this.menu.getEnergyScaled(ew);
        if (energyW > 0) {
            extractor.fill(ex, ey, ex + energyW, ey + eh, 0xFF00E5FF);
            extractor.fill(ex, ey, ex + energyW, ey + 1, 0xFF80D8FF);
        }
        String energyStr = NumberFormat.compact(this.menu.getStoredEnergy()) + "J";
        int eStrW = this.font.width(energyStr);
        extractor.text(this.font, Component.literal(energyStr), ex + (ew - eStrW) / 2, ey + 2, 0xFFFFFFFF, false);

        drawVialIcon(extractor, x + 154, y + 41);

        int nx = x + 168;
        int ny = y + 41;
        int nw = 38;
        int nh = 12;

        extractor.fill(nx - 1, ny - 1, nx + nw + 1, ny + nh + 1, 0xFF1E293B);
        extractor.fill(nx, ny, nx + nw, ny + nh, 0xFF05080E);
        int nutW = this.menu.getNutrientsScaled(nw);
        if (nutW > 0) {
            extractor.fill(nx, ny, nx + nutW, ny + nh, 0xFF00E676);
            extractor.fill(nx, ny, nx + nutW, ny + 1, 0xFF69F0AE);
        }
        String nutStr = this.menu.getBioNutrients() + "%";
        int nStrW = this.font.width(nutStr);
        extractor.text(this.font, Component.literal(nutStr), nx + (nw - nStrW) / 2, ny + 2, 0xFFFFFFFF, false);
    }

    private void drawLightningIcon(GuiGraphicsExtractor extractor, int x, int y) {
        int c = 0xFF00E5FF;
        int w = 0xFFFFFFFF;
        extractor.fill(x + 4, y, x + 6, y + 2, w);
        extractor.fill(x + 3, y + 2, x + 5, y + 4, c);
        extractor.fill(x + 2, y + 4, x + 4, y + 5, c);
        extractor.fill(x + 1, y + 5, x + 7, y + 6, w);
        extractor.fill(x + 3, y + 6, x + 5, y + 8, c);
        extractor.fill(x + 2, y + 8, x + 4, y + 10, c);
        extractor.fill(x + 1, y + 10, x + 2, y + 12, w);
    }

    private void drawVialIcon(GuiGraphicsExtractor extractor, int x, int y) {
        int g = 0xFF00E676;
        int w = 0xFFFFFFFF;
        extractor.fill(x + 2, y, x + 6, y + 1, g);
        extractor.fill(x + 3, y + 1, x + 5, y + 4, g);
        extractor.fill(x + 2, y + 4, x + 6, y + 5, g);
        extractor.fill(x + 1, y + 5, x + 7, y + 9, g);
        extractor.fill(x + 2, y + 7, x + 4, y + 8, w);
        extractor.fill(x + 2, y + 9, x + 6, y + 10, g);
    }

    private boolean isHovered(int bx, int by, int bw, int bh) {
        if (this.minecraft == null) {
            return false;
        }
        double mx = this.minecraft.mouseHandler.xpos() * (double) this.minecraft.getWindow().getGuiScaledWidth() / (double) this.minecraft.getWindow().getScreenWidth();
        double my = this.minecraft.mouseHandler.ypos() * (double) this.minecraft.getWindow().getGuiScaledHeight() / (double) this.minecraft.getWindow().getScreenHeight();
        return mx >= bx && mx <= bx + bw && my >= by && my <= by + bh;
    }

    private void renderTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int btnX = x + 58;
        int btnY = y + 68;
        int btnW = 84;
        int btnH = 22;

        boolean hasClone = this.menu.hasClone();
        int pods = this.menu.getConnectedPodsCount();
        int energy = this.menu.getStoredEnergy();
        int maxEnergy = this.menu.getMaxEnergy();
        int nutrients = this.menu.getBioNutrients();

        if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
            List<Component> tip = hasClone
                    ? List.of(
                            Component.translatable("gui.sandstorm.quantum_sleeper.transfer_title"),
                            Component.translatable("gui.sandstorm.quantum_sleeper.transfer_desc", pods)
                    )
                    : List.of(
                            Component.translatable("gui.sandstorm.quantum_sleeper.synthesize_title"),
                            Component.translatable("gui.sandstorm.quantum_sleeper.synthesize_desc")
                    );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 152 && mouseX <= x + 208 && mouseY >= y + 19 && mouseY <= y + 35) {
            extractor.setTooltipForNextFrame(this.font, Component.translatable("gui.sandstorm.quantum_sleeper.energy", NumberFormat.compact(energy), NumberFormat.compact(maxEnergy)), mouseX, mouseY);
        } else if (mouseX >= x + 152 && mouseX <= x + 208 && mouseY >= y + 39 && mouseY <= y + 55) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_title", nutrients),
                    Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_desc1"),
                    Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_desc2")
            ), mouseX, mouseY);
        } else if (mouseX >= x + 176 && mouseX <= x + 192 && mouseY >= y + 62 && mouseY <= y + 78) {
            Slot slot = this.menu.slots.get(QuantumSleeperMenu.NUTRIENT_SLOT);
            if (!slot.hasItem()) {
                extractor.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_title", nutrients),
                        Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_desc1"),
                        Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_desc2")
                ), mouseX, mouseY);
            }
        } else if (mouseX >= x + 25 && mouseX <= x + 191 && mouseY >= y + 100 && mouseY <= y + 120) {
            extractor.setTooltipForNextFrame(this.font, Component.literal("Suprimentos de Emergência do Clone"), mouseX, mouseY);
        }
    }
}
