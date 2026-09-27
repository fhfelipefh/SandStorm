package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.DesalinationFilterMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DesalinationFilterScreen extends BaseMachineScreen<DesalinationFilterMenu> {
    public DesalinationFilterScreen(DesalinationFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:desalination_filter";
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderFluidTanks(extractor);
    }

    private void renderFluidTanks(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int inX = x + 28;
        int inY = y + 20;
        int tankW = 10;
        int tankH = 44;

        extractor.fill(inX - 1, inY - 1, inX + tankW + 1, inY + tankH + 1, 0xFF1E293B);
        extractor.fill(inX, inY, inX + tankW, inY + tankH, 0xFF05080E);
        int inScaled = this.menu.getWaterInputScaled(tankH);
        if (inScaled > 0) {
            extractor.fill(inX + 1, inY + tankH - inScaled, inX + tankW - 1, inY + tankH, 0xFF0097A7);
        }

        int outX = x + 138;
        int outY = y + 20;
        extractor.fill(outX - 1, outY - 1, outX + tankW + 1, outY + tankH + 1, 0xFF1E293B);
        extractor.fill(outX, outY, outX + tankW, outY + tankH, 0xFF05080E);
        int outScaled = this.menu.getWaterOutputScaled(tankH);
        if (outScaled > 0) {
            extractor.fill(outX + 1, outY + tankH - outScaled, outX + tankW - 1, outY + tankH, 0xFF00E5FF);
        }
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        super.renderCustomTooltips(extractor, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 27 && mouseX <= x + 39 && mouseY >= y + 19 && mouseY <= y + 65) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.desal_filter.in_tank"),
                    Component.literal(this.menu.getWaterInput() + " / " + this.menu.getMaxWater() + " mB")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 137 && mouseX <= x + 149 && mouseY >= y + 19 && mouseY <= y + 65) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.desal_filter.out_tank"),
                    Component.literal(this.menu.getWaterOutput() + " / " + this.menu.getMaxWater() + " mB")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 44 && mouseX <= x + 60 && mouseY >= y + 37 && mouseY <= y + 53) {
            if (!this.menu.slots.get(0).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.translatable("gui.sandstorm.desal_filter.water_in"), mouseX, mouseY);
            }
        } else if (mouseX >= x + 80 && mouseX <= x + 96 && mouseY >= y + 58 && mouseY <= y + 74) {
            if (!this.menu.slots.get(4).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.translatable("gui.sandstorm.desal_filter.cartridge"), mouseX, mouseY);
            }
        } else if (mouseX >= x + 116 && mouseX <= x + 132 && mouseY >= y + 26 && mouseY <= y + 42) {
            if (!this.menu.slots.get(1).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.translatable("gui.sandstorm.desal_filter.water_out"), mouseX, mouseY);
            }
        } else if (mouseX >= x + 116 && mouseX <= x + 132 && mouseY >= y + 48 && mouseY <= y + 64) {
            if (!this.menu.slots.get(2).hasItem()) {
                extractor.setTooltipForNextFrame(this.font, Component.translatable("gui.sandstorm.desal_filter.salt_out"), mouseX, mouseY);
            }
        }
    }
}
