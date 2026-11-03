package com.fhfelipefh.sandstorm.client.hud;

import com.fhfelipefh.sandstorm.content.block.entity.BaseMachineBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.DewCondenserBlockEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class MachineInspectionHudOverlay implements HudElement {

    public static void initialize() {
        HudElementRegistry.addLast(SandStormMod.id("machine_inspection_hud"), new MachineInspectionHudOverlay());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) {
            return;
        }

        if (!(client.hitResult instanceof BlockHitResult hitResult) || hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockEntity be = client.level.getBlockEntity(pos);
        if (be == null) {
            return;
        }

        if (be instanceof BaseMachineBlockEntity machine) {
            renderMachineHud(extractor, client, machine);
        } else if (be instanceof DewCondenserBlockEntity condenser) {
            renderCondenserHud(extractor, client, condenser);
        }
    }

    private void renderMachineHud(GuiGraphicsExtractor extractor, Minecraft client, BaseMachineBlockEntity machine) {
        int screenWidth = extractor.guiWidth();
        int screenHeight = extractor.guiHeight();
        float scale = Math.min(1.0f, screenWidth / 200.0f);
        int width = 160;
        int height = 36;
        int x = Math.max(4, (int) ((screenWidth / scale - width) / 2));
        int y = Math.max(4, Math.min((int) (12 / scale), (int) (screenHeight / scale - height - 4)));

        extractor.pose().pushMatrix();
        extractor.pose().scale(scale, scale);

        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF00E5FF);
        extractor.fill(x, y, x + width, y + height, 0xDD0A0E17);

        Component title = machine.getBlockState().getBlock().getName();
        extractor.text(client.font, title, x + 6, y + 4, 0xFF00E5FF);

        int energy = machine.getEnergy();
        int maxEnergy = machine.getMaxEnergy();
        String energyText = energy + " / " + maxEnergy + " J";
        extractor.text(client.font, Component.literal(energyText), x + 6, y + 15, 0xFF00B0FF);

        int prog = machine.getProgress();
        int maxProg = machine.getMaxProgress();
        int pct = maxProg > 0 ? (prog * 100 / maxProg) : 0;
        String statusText = machine.isProcessing() ? pct + "% [PROCESSANDO]" : "[OCIOSO]";
        int statusColor = machine.isProcessing() ? 0xFF00E676 : 0xFF78909C;
        extractor.text(client.font, Component.literal(statusText), x + 6, y + 25, statusColor);

        int barX = x + 104;
        int barY = y + 25;
        int barW = 50;
        int barH = 7;
        extractor.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF1E293B);
        extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF05080E);
        int fillW = maxProg > 0 ? (prog * barW / maxProg) : 0;
        if (fillW > 0) {
            extractor.fill(barX, barY, barX + fillW, barY + barH, 0xFF00E5FF);
        }

        extractor.pose().popMatrix();
    }

    private void renderCondenserHud(GuiGraphicsExtractor extractor, Minecraft client, DewCondenserBlockEntity condenser) {
        int screenWidth = extractor.guiWidth();
        int screenHeight = extractor.guiHeight();
        float scale = Math.min(1.0f, screenWidth / 200.0f);
        int width = 160;
        int height = 28;
        int x = Math.max(4, (int) ((screenWidth / scale - width) / 2));
        int y = Math.max(4, Math.min((int) (12 / scale), (int) (screenHeight / scale - height - 4)));

        extractor.pose().pushMatrix();
        extractor.pose().scale(scale, scale);

        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF00E5FF);
        extractor.fill(x, y, x + width, y + height, 0xDD0A0E17);

        Component title = condenser.getBlockState().getBlock().getName();
        extractor.text(client.font, title, x + 6, y + 4, 0xFF00E5FF);

        int water = condenser.getWaterAmount();
        String waterText = water + " / " + DewCondenserBlockEntity.MAX_WATER + " mB";
        extractor.text(client.font, Component.literal(waterText), x + 6, y + 15, 0xFF00B0FF);

        extractor.pose().popMatrix();
    }
}
