package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.IrrigationPumpMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class IrrigationPumpScreen extends BaseMachineScreen<IrrigationPumpMenu> {
    public IrrigationPumpScreen(IrrigationPumpMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:irrigation_pump";
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderFluidAndNutrientTanks(extractor);
        renderDashboardInfo(extractor);
    }

    private void renderFluidAndNutrientTanks(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int waterX = x + 46;
        int waterY = y + 20;
        int tankW = 10;
        int tankH = 48;

        extractor.fill(waterX - 1, waterY - 1, waterX + tankW + 1, waterY + tankH + 1, 0xFF1E293B);
        extractor.fill(waterX, waterY, waterX + tankW, waterY + tankH, 0xFF05080E);
        int waterScaled = this.menu.getWaterScaled(tankH - 2);
        if (waterScaled > 0) {
            extractor.fill(waterX + 1, waterY + tankH - 1 - waterScaled, waterX + tankW - 1, waterY + tankH - 1, 0xFF00E5FF);
        }

        int nutrX = x + 100;
        int nutrY = y + 20;
        extractor.fill(nutrX - 1, nutrY - 1, nutrX + tankW + 1, nutrY + tankH + 1, 0xFF1E293B);
        extractor.fill(nutrX, nutrY, nutrX + tankW, nutrY + tankH, 0xFF05080E);
        int nutrScaled = this.menu.getNutrientsScaled(tankH - 2);
        if (nutrScaled > 0) {
            extractor.fill(nutrX + 1, nutrY + tankH - 1 - nutrScaled, nutrX + tankW - 1, nutrY + tankH - 1, 0xFF2ECC71);
        }
    }

    private void renderDashboardInfo(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int count = this.menu.getConnectedSprinklers();
        int radius = this.menu.getPressureRadius();

        Component spText = Component.translatable("gui.sandstorm.irrigation_pump.sprinklers", count);
        Component reachText = Component.translatable("gui.sandstorm.irrigation_pump.reach", radius);

        extractor.text(this.font, spText, x + 60, y + 16, 0xFF80D8FF, false);
        extractor.text(this.font, reachText, x + 60, y + 58, 0xFFA5D6A7, false);
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        super.renderCustomTooltips(extractor, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 45 && mouseX <= x + 57 && mouseY >= y + 19 && mouseY <= y + 69) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.irrigation_pump.water_tank"),
                    Component.translatable("gui.sandstorm.irrigation_pump.water", this.menu.getWaterAmount(), this.menu.getMaxWater())
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        } else if (mouseX >= x + 99 && mouseX <= x + 111 && mouseY >= y + 19 && mouseY <= y + 69) {
            List<Component> tip = List.of(
                    Component.translatable("gui.sandstorm.irrigation_pump.nutrient_tank"),
                    Component.translatable("gui.sandstorm.irrigation_pump.nutrients", this.menu.getNutrients(), this.menu.getMaxNutrients())
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }
    }
}
