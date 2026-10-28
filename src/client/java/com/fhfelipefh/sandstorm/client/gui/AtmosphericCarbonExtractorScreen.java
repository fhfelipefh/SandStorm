package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.AtmosphericCarbonExtractorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class AtmosphericCarbonExtractorScreen extends BaseMachineScreen<AtmosphericCarbonExtractorMenu> {
    public AtmosphericCarbonExtractorScreen(AtmosphericCarbonExtractorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:atmospheric_carbon_extractor";
    }

    @Override
    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 70 && mouseX <= x + 110 && mouseY >= y + 26 && mouseY <= y + 54;
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderElectrolyzerPreview(extractor);
    }

    private void renderElectrolyzerPreview(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int cx1 = x + 70;
        int cy1 = y + 26;
        int cx2 = x + 110;
        int cy2 = y + 54;

        boolean diamondMode = this.menu.isDiamondMode();
        int borderColor = diamondMode ? 0xFF00E5FF : 0xFF37474F;
        int bgColor = diamondMode ? 0xF00D1B2A : 0xF01A1C23;

        extractor.fill(cx1 - 1, cy1 - 1, cx2 + 1, cy2 + 1, borderColor);
        extractor.fill(cx1, cy1, cx2, cy2, bgColor);

        boolean processing = this.menu.isProcessing();
        int maxProg = this.menu.getMaxProgress();
        int prog = this.menu.getProgress();
        float ratio = maxProg > 0 ? (float) prog / (float) maxProg : 0.0f;
        int boost = this.menu.getCombustionBoost();

        if (boost > 0) {
            int flameX = cx2 - 7;
            int flameY = cy1 + 2;
            extractor.fill(flameX, flameY + 1, flameX + 4, flameY + 5, 0xFFFF5722);
            extractor.fill(flameX + 1, flameY, flameX + 3, flameY + 3, 0xFFFFD54F);
            extractor.fill(flameX + 1, flameY + 2, flameX + 3, flameY + 4, 0xFFFF7043);
        }

        if (processing) {
            long ticks = System.currentTimeMillis() / 40;
            int fanOffset = (int) (ticks % 10);
            int fanColor = 0xFF00E5FF;
            extractor.fill(cx1 + 8 + fanOffset, cy1 + 2, cx1 + 11 + fanOffset, cy1 + 5, fanColor);

            if (diamondMode) {
                int beamX = (cx1 + cx2) / 2;
                extractor.fill(beamX - 1, cy1 + 6, beamX + 1, cy2 - 10, 0xCC00E5FF);
                extractor.fill(beamX, cy1 + 6, beamX + 1, cy2 - 10, 0xFFFFFFFF);

                int crystalGrowth = (int) (ratio * 12.0f);
                if (crystalGrowth > 0) {
                    int crystalY = cy2 - 4 - crystalGrowth;
                    int midX = (cx1 + cx2) / 2;
                    extractor.fill(midX - crystalGrowth / 2, crystalY, midX + crystalGrowth / 2 + 1, cy2 - 3, 0xFF00E5FF);
                    extractor.fill(midX - 1, crystalY + 1, midX + 1, cy2 - 4, 0xFFE0F7FA);
                }

                int sparkOffset = (int) (Math.sin(ticks * 0.4) * 8.0);
                extractor.fill(cx1 + 18 + sparkOffset, cy1 + 12, cx1 + 20 + sparkOffset, cy1 + 14, 0xFFE0F7FA);
            } else {
                extractor.fill(cx1 + 4, cy2 - 8, cx2 - 4, cy2 - 2, 0xFFBF360C);
                extractor.fill(cx1 + 5, cy2 - 7, cx2 - 5, cy2 - 3, 0xFFFF7043);

                int depositWidth = (int) (ratio * 26.0f);
                if (depositWidth > 0) {
                    extractor.fill(cx1 + 7, cy2 - 6, cx1 + 7 + depositWidth, cy2 - 4, 0xFF212121);
                }

                int bubbleX = (int) (cx1 + 10 + (Math.sin(ticks * 0.2) + 1.0) * 8.0);
                extractor.fill(bubbleX, cy1 + 8, bubbleX + 2, cy1 + 10, 0xAAFFFFFF);
            }
        } else if (hasOutputReady()) {
            int glowColor = diamondMode ? 0xFF00E5FF : 0xFF00E676;
            int innerGlow = diamondMode ? 0xFFE0F7FA : 0xFFB9F6CA;
            extractor.fill(cx1 + 8, cy1 + 8, cx2 - 8, cy2 - 8, glowColor);
            extractor.fill(cx1 + 10, cy1 + 10, cx2 - 10, cy2 - 10, innerGlow);
        }
    }

    @Override
    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (isMouseOverProgress(mouseX, mouseY, x, y)) {
            int maxProg = this.menu.getMaxProgress();
            int pct = maxProg > 0 ? (this.menu.getProgress() * 100 / maxProg) : 0;
            String status = this.menu.isProcessing() ? " §a[PROCESSANDO]" : " §7[EM ESPERA]";
            if (!this.menu.isProcessing() && hasOutputReady()) {
                pct = 100;
                status = " §a[PRONTO]";
            }
            int boost = this.menu.getCombustionBoost();
            if (boost > 0) {
                List<Component> tip = List.of(
                        Component.literal("§bProgresso: §f" + pct + "%" + status),
                        Component.literal("§6🔥 ").append(Component.translatable("tooltip.sandstorm.extractor.thermal_draft")),
                        Component.literal("§e+").append(Component.literal((boost * 100) + "% ")).append(Component.translatable("tooltip.sandstorm.extractor.thermal_speed"))
                );
                extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
                return;
            }
        }
        super.renderCustomTooltips(extractor, mouseX, mouseY);
    }
}
