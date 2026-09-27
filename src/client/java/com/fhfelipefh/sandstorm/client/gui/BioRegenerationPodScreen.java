package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.BioRegenerationPodMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BioRegenerationPodScreen extends BaseMachineScreen<BioRegenerationPodMenu> {
    public BioRegenerationPodScreen(BioRegenerationPodMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "sandstorm:bio_regeneration_pod";
    }

    @Override
    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 72 && mouseX <= x + 128 && mouseY >= y + 26 && mouseY <= y + 58;
    }

    @Override
    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderEcgMonitor(extractor);
        renderFluidTank(extractor, mouseX, mouseY);
    }

    private void renderEcgMonitor(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int mx1 = x + 72;
        int my1 = y + 24;
        int mx2 = x + 128;
        int my2 = y + 58;

        extractor.fill(mx1 - 1, my1 - 1, mx2 + 1, my2 + 1, 0xFF00E676);
        extractor.fill(mx1, my1, mx2, my2, 0xF003120A);

        extractor.fill(mx1, my1 + 8, mx2, my1 + 9, 0x4000E676);
        extractor.fill(mx1, my1 + 17, mx2, my1 + 18, 0x4000E676);
        extractor.fill(mx1, my1 + 25, mx2, my1 + 26, 0x4000E676);

        boolean occupied = this.menu.isOccupied();
        int heartRate = this.menu.getHeartRate();
        int tick = (int) ((System.currentTimeMillis() / 45) % (mx2 - mx1));

        int baselineY = my1 + 17;
        for (int px = 0; px < (mx2 - mx1); px++) {
            int waveY = baselineY;
            if (occupied) {
                int phase = (px + tick) % 28;
                if (phase == 8) {
                    waveY -= 2;
                } else if (phase == 10) {
                    waveY += 2;
                } else if (phase == 12) {
                    waveY -= 11;
                } else if (phase == 14) {
                    waveY += 6;
                } else if (phase == 18) {
                    waveY -= 3;
                }
            }

            int color = (px == tick) ? 0xFFFFFFFF : (occupied ? 0xFF00FF88 : 0xFF558B2F);
            extractor.fill(mx1 + px, waveY, mx1 + px + 1, waveY + 1, color);
        }

        int mw = mx2 - mx1;
        String topText = occupied ? this.menu.getHealthPercent() + "%" : "LIVRE";
        int topWidth = this.font.width(topText);
        int topColor = occupied ? 0xFF00E5FF : 0xFF81C784;
        extractor.text(this.font, Component.literal(topText), mx1 + (mw - topWidth) / 2, my1 + 2, topColor, false);

        if (occupied) {
            String bpmText = heartRate + " BPM";
            int bpmWidth = this.font.width(bpmText);
            extractor.text(this.font, Component.literal(bpmText), mx1 + (mw - bpmWidth) / 2, my2 - 10, 0xFF00E676, false);
        }
    }

    private void renderFluidTank(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        int fx1 = x + 154;
        int fy1 = y + 17;
        int fx2 = x + 168;
        int fy2 = y + 69;

        extractor.fill(fx1 - 1, fy1 - 1, fx2 + 1, fy2 + 1, 0xFF0277BD);
        extractor.fill(fx1, fy1, fx2, fy2, 0xF005111B);

        int fluidScaled = this.menu.getFluidScaled(fy2 - fy1);
        if (fluidScaled > 0) {
            int fillTop = fy2 - fluidScaled;
            extractor.fill(fx1, fillTop, fx2, fy2, 0xFF00B0FF);
            extractor.fill(fx1, fillTop, fx2, fillTop + 1, 0xFF80D8FF);
        }

        if (mouseX >= fx1 && mouseX <= fx2 && mouseY >= fy1 && mouseY <= fy2) {
            Component tooltip = Component.translatable("tooltip.sandstorm.fluid_tank_storage", this.menu.getFluidAmount(), this.menu.getMaxFluid());
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }
}
