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
    private static final int CHASSIS_WIDTH = 176;
    private static final int CHASSIS_HEIGHT = 204;

    public QuantumSleeperScreen(QuantumSleeperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, CHASSIS_WIDTH, CHASSIS_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 112;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        int btnX = x + 104;
        int btnY = y + 16;
        int btnW = 44;
        int btnH = 18;

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
        renderHoloTerminal(extractor, mouseX, mouseY);
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

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA0A0E17);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        extractor.fill(x + 4, y + 4, x + CHASSIS_WIDTH - 4, y + 16, 0xDD101824);
        extractor.fill(x + 4, y + 16, x + CHASSIS_WIDTH - 4, y + 17, 0x8800E5FF);

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xAA080C14);
        }

        int nx = x + 152;
        int ny = y + 18;
        extractor.fill(nx - 1, ny - 1, nx + 17, ny + 17, 0xFF00E676);
        extractor.fill(nx, ny, nx + 16, ny + 16, 0xAA082014);
    }

    private void renderHoloTerminal(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int btnX = x + 104;
        int btnY = y + 16;
        int btnW = 44;
        int btnH = 18;

        boolean hasClone = this.menu.hasClone();
        int btnBg = hasClone ? 0xDD004D40 : 0xDD1B5E20;
        int btnBorder = hasClone ? 0xFF00E5FF : 0xFF00E676;
        Component btnText = hasClone ? Component.translatable("gui.sandstorm.quantum_sleeper.ego_cast") : Component.translatable("gui.sandstorm.quantum_sleeper.synthesize");

        extractor.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
        extractor.fill(btnX, btnY, btnX + btnW, btnY + 1, btnBorder);
        extractor.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, btnBorder);
        extractor.fill(btnX, btnY + 1, btnX + 1, btnY + btnH, btnBorder);
        extractor.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, btnBorder);

        int textW = this.font.width(btnText);
        extractor.text(this.font, btnText, btnX + (btnW - textW) / 2, btnY + 5, 0xFFFFFFFF, false);

        int energy = this.menu.getStoredEnergy();
        int maxEnergy = this.menu.getMaxEnergy();
        int nutrients = this.menu.getBioNutrients();
        int pods = this.menu.getConnectedPodsCount();

        int energyPixels = this.menu.getEnergyScaled(30);
        extractor.fill(x + 104, y + 36, x + 134, y + 38, 0xFF1E293B);
        extractor.fill(x + 104, y + 36, x + 104 + energyPixels, y + 38, 0xFF00E5FF);

        int nutPixels = this.menu.getNutrientsScaled(30);
        extractor.fill(x + 138, y + 36, x + 168, y + 38, 0xFF1E293B);
        extractor.fill(x + 138, y + 36, x + 138 + nutPixels, y + 38, 0xFF00E676);

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
        } else if (mouseX >= x + 104 && mouseX <= x + 134 && mouseY >= y + 36 && mouseY <= y + 38) {
            extractor.setTooltipForNextFrame(this.font, Component.translatable("gui.sandstorm.quantum_sleeper.energy", NumberFormat.compact(energy), NumberFormat.compact(maxEnergy)), mouseX, mouseY);
        } else if (mouseX >= x + 138 && mouseX <= x + 168 && mouseY >= y + 36 && mouseY <= y + 38) {
            extractor.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_title", nutrients),
                    Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_desc1"),
                    Component.translatable("gui.sandstorm.quantum_sleeper.nutrients_desc2")
            ), mouseX, mouseY);
        }
    }
}
