package com.fhfelipefh.sandstorm.client.hud;

import com.fhfelipefh.sandstorm.content.entity.CyberSteedEntity;
import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Locale;

public class MachineCockpitHudOverlay implements HudElement {

    public static void initialize() {
        HudElementRegistry.addLast(SandStormMod.id("machine_cockpit_hud"), new MachineCockpitHudOverlay());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        Entity vehicle = client.player.getVehicle();
        if (vehicle instanceof ExcavatorVehicleEntity excavator) {
            renderExcavatorHud(extractor, client, excavator);
        } else if (vehicle instanceof MegazordEntity megazord) {
            renderMegazordHud(extractor, client, megazord);
        } else if (vehicle instanceof CyberSteedEntity steed) {
            renderCyberSteedHud(extractor, steed);
        }
    }

    private void renderExcavatorHud(GuiGraphicsExtractor extractor, Minecraft client, ExcavatorVehicleEntity excavator) {
        int screenWidth = extractor.guiWidth();
        int margin = 8;
        float scale = Math.min(1.0f, screenWidth / 180.0f);
        int x = Math.max((int) (margin / scale), (int) (screenWidth / scale) - 148 - (int) (margin / scale));
        int y = (int) (margin / scale);
        int barW = 54;

        extractor.pose().pushMatrix();
        extractor.pose().scale(scale, scale);

        float health = excavator.getHealth();
        float maxHealth = excavator.getMaxHealth();
        float healthPct = maxHealth > 0.0f ? Math.clamp(health / maxHealth, 0.0f, 1.0f) : 0.0f;
        int healthColor = healthPct > 0.5f ? 0xFF00E676 : (healthPct > 0.25f ? 0xFFFFD600 : 0xFFFF1744);
        long energy = excavator.getEnergyStorage().getStoredEnergy();
        long capacity = excavator.getEnergyStorage().getCapacity();
        float energyPct = capacity > 0 ? Math.clamp((float) energy / (float) capacity, 0.0f, 1.0f) : 0.0f;
        int energyColor = energyPct > 0.2f ? 0xFF00B0FF : 0xFFFF9100;

        extractor.text(client.font, Component.literal("\u00A7bESCAVADEIRA MK-I"), x, y, 0xFFFFFFFF);
        extractor.text(client.font, Component.literal(String.format(Locale.ROOT, "HP %.0f%%", healthPct * 100.0f)), x, y + 11, healthColor);
        extractor.text(client.font, Component.literal(String.format(Locale.ROOT, "PWR %.0f%%", energyPct * 100.0f)), x + 70, y + 11, energyColor);
        extractor.fill(x, y + 21, x + barW, y + 23, 0x66444444);
        extractor.fill(x, y + 21, x + (int) (barW * healthPct), y + 23, healthColor);
        extractor.fill(x + 70, y + 21, x + 70 + barW, y + 23, 0x66444444);
        extractor.fill(x + 70, y + 21, x + 70 + (int) (barW * energyPct), y + 23, energyColor);
        String drillStatus = energy >= 15L ? "\u00A7aBROCA ATIVA" : "\u00A7cBROCA SEM ENERGIA";
        extractor.text(client.font, Component.literal(drillStatus), x, y + 27, 0xFFFFFFFF);
        extractor.pose().popMatrix();
    }

    private void renderMegazordHud(GuiGraphicsExtractor extractor, Minecraft client, MegazordEntity megazord) {
        int screenWidth = extractor.guiWidth();
        int margin = 8;
        float scale = Math.min(1.0f, screenWidth / 190.0f);
        int x = Math.max((int) (margin / scale), (int) (screenWidth / scale) - 158 - (int) (margin / scale));
        int y = (int) (margin / scale);
        int barW = 58;

        extractor.pose().pushMatrix();
        extractor.pose().scale(scale, scale);
        int healthColor;
        float health = megazord.getHealth();
        float maxHealth = megazord.getMaxHealth();
        float healthPct = maxHealth > 0.0f ? Math.clamp(health / maxHealth, 0.0f, 1.0f) : 0.0f;
        healthColor = healthPct > 0.5f ? 0xFF00E676 : (healthPct > 0.25f ? 0xFFFFD600 : 0xFFFF1744);
        long energy = megazord.getEnergyStorage().getStoredEnergy();
        long capacity = megazord.getEnergyStorage().getCapacity();
        float energyPct = capacity > 0 ? Math.clamp((float) energy / (float) capacity, 0.0f, 1.0f) : 0.0f;
        int energyColor = megazord.hasOverdriveModule() ? 0xFFFF1744 : 0xFF00E5FF;

        extractor.text(client.font, Component.literal(String.format(Locale.ROOT, "HP %.0f%%", healthPct * 100.0f)), x, y, healthColor);
        extractor.text(client.font, Component.literal(String.format(Locale.ROOT, "PWR %.0f%%", energyPct * 100.0f)), x + 70, y, energyColor);
        extractor.fill(x, y + 10, x + barW, y + 12, 0x66444444);
        extractor.fill(x, y + 10, x + (int) (barW * healthPct), y + 12, healthColor);
        extractor.fill(x + 70, y + 10, x + 70 + barW, y + 12, 0x66444444);
        extractor.fill(x + 70, y + 10, x + 70 + (int) (barW * energyPct), y + 12, energyColor);
        extractor.pose().popMatrix();
    }

    private void renderCyberSteedHud(GuiGraphicsExtractor extractor, CyberSteedEntity steed) {
        int screenWidth = extractor.guiWidth();
        int margin = 8;
        float scale = Math.min(1.0f, screenWidth / 180.0f);
        int dockWidth = 125;
        int dockHeight = 18;
        int x = Math.max((int) (margin / scale), (int) (screenWidth / scale) - dockWidth - (int) (margin / scale));
        int y = (int) (margin / scale);

        extractor.pose().pushMatrix();
        extractor.pose().scale(scale, scale);

        boolean deactivated = steed.isDeactivated();
        int borderColor = deactivated ? 0xAAFF1744 : 0x5500E5FF;
        int backColor = 0x990A0E14;

        extractor.fill(x, y, x + dockWidth, y + dockHeight, backColor);
        extractor.fill(x, y, x + dockWidth, y + 1, borderColor);
        extractor.fill(x, y + dockHeight - 1, x + dockWidth, y + dockHeight, borderColor);
        extractor.fill(x, y, x + 1, y + dockHeight, borderColor);
        extractor.fill(x + dockWidth - 1, y, x + dockWidth, y + dockHeight, borderColor);

        float health = steed.getHealth();
        float maxHealth = steed.getMaxHealth();
        float healthPct = maxHealth > 0.0f ? Math.clamp(health / maxHealth, 0.0f, 1.0f) : 0.0f;
        int healthColor = deactivated ? 0xFF555555 : (healthPct > 0.5f ? 0xFF00E676 : (healthPct > 0.25f ? 0xFFFFD600 : 0xFFFF1744));

        int curX = x + 4;
        int midY = y + 5;
        drawShieldIcon(extractor, curX, midY, healthColor);
        curX += 11;

        int pipCount = 10;
        int activePips = (int) Math.ceil(healthPct * pipCount);
        for (int i = 0; i < pipCount; i++) {
            int pipColor = (i < activePips) ? healthColor : 0x22FFFFFF;
            extractor.fill(curX + i * 4, midY + 1, curX + i * 4 + 3, midY + 7, pipColor);
        }
        curX += (pipCount * 4) + 3;

        extractor.fill(curX, y + 3, curX + 1, y + dockHeight - 3, 0x33FFFFFF);
        curX += 4;

        drawCanisterIcon(extractor, curX, midY - 1, steed.isCanisterExploded(), deactivated);
        curX += 12;

        extractor.fill(curX, y + 3, curX + 1, y + dockHeight - 3, 0x33FFFFFF);
        curX += 4;

        drawUpgradeSocket(extractor, curX, midY - 1, steed.hasSonicTurret(), 0xFF00E5FF, 1);
        curX += 11;
        drawUpgradeSocket(extractor, curX, midY - 1, steed.hasBatteryPack(), 0xFFFFD600, 2);
        curX += 11;
        drawUpgradeSocket(extractor, curX, midY - 1, steed.hasSpeedOverdrive(), 0xFF00E676, 3);
        curX += 11;
        drawUpgradeSocket(extractor, curX, midY - 1, steed.hasCargoPanniers(), 0xFFECEFF1, 4);

        extractor.pose().popMatrix();
    }

    private void drawShieldIcon(GuiGraphicsExtractor extractor, int x, int y, int color) {
        extractor.fill(x + 1, y, x + 7, y + 1, color);
        extractor.fill(x, y + 1, x + 8, y + 5, color);
        extractor.fill(x + 1, y + 5, x + 7, y + 6, color);
        extractor.fill(x + 2, y + 6, x + 6, y + 7, color);
        extractor.fill(x + 3, y + 7, x + 5, y + 8, color);
        extractor.fill(x + 2, y + 2, x + 6, y + 4, 0xFF0A0E14);
        extractor.fill(x + 3, y + 4, x + 5, y + 6, 0xFF0A0E14);
    }

    private void drawCanisterIcon(GuiGraphicsExtractor extractor, int x, int y, boolean exploded, boolean deactivated) {
        extractor.fill(x + 2, y, x + 6, y + 1, 0xFF78909C);
        extractor.fill(x + 1, y + 1, x + 7, y + 9, 0x4437474F);
        extractor.fill(x + 2, y + 9, x + 6, y + 10, 0xFF78909C);
        if (exploded) {
            extractor.fill(x + 2, y + 3, x + 4, y + 5, 0xFFFF1744);
            extractor.fill(x + 4, y + 5, x + 6, y + 7, 0xFFFF1744);
            extractor.fill(x + 4, y + 3, x + 6, y + 5, 0xFFFF1744);
            extractor.fill(x + 2, y + 5, x + 4, y + 7, 0xFFFF1744);
        } else {
            int fluidColor = deactivated ? 0xFF555555 : 0xFFFF9100;
            int coreColor = deactivated ? 0xFF777777 : 0xFFFFD54F;
            extractor.fill(x + 2, y + 3, x + 6, y + 8, fluidColor);
            extractor.fill(x + 3, y + 4, x + 5, y + 7, coreColor);
        }
    }

    private void drawUpgradeSocket(GuiGraphicsExtractor extractor, int x, int y, boolean installed, int activeColor, int type) {
        int borderColor = installed ? (activeColor & 0x77FFFFFF) : 0x22FFFFFF;
        int socketBack = installed ? 0x66101923 : 0x22101923;
        extractor.fill(x, y, x + 10, y + 10, socketBack);
        extractor.fill(x, y, x + 10, y + 1, borderColor);
        extractor.fill(x, y + 9, x + 10, y + 10, borderColor);
        extractor.fill(x, y, x + 1, y + 10, borderColor);
        extractor.fill(x + 9, y, x + 10, y + 10, borderColor);

        if (!installed) {
            extractor.fill(x + 4, y + 4, x + 6, y + 6, 0x33FFFFFF);
            return;
        }

        switch (type) {
            case 1 -> {
                extractor.fill(x + 2, y + 4, x + 4, y + 6, activeColor);
                extractor.fill(x + 5, y + 3, x + 6, y + 7, activeColor);
                extractor.fill(x + 7, y + 2, x + 8, y + 8, activeColor);
            }
            case 2 -> {
                extractor.fill(x + 4, y + 1, x + 7, y + 4, activeColor);
                extractor.fill(x + 3, y + 4, x + 6, y + 6, activeColor);
                extractor.fill(x + 2, y + 6, x + 5, y + 9, activeColor);
            }
            case 3 -> {
                extractor.fill(x + 2, y + 2, x + 3, y + 4, activeColor);
                extractor.fill(x + 3, y + 4, x + 5, y + 6, activeColor);
                extractor.fill(x + 2, y + 6, x + 4, y + 8, activeColor);
                extractor.fill(x + 5, y + 2, x + 6, y + 4, activeColor);
                extractor.fill(x + 6, y + 4, x + 8, y + 6, activeColor);
                extractor.fill(x + 5, y + 6, x + 7, y + 8, activeColor);
            }
            case 4 -> {
                extractor.fill(x + 3, y + 1, x + 7, y + 2, activeColor);
                extractor.fill(x + 1, y + 3, x + 9, y + 8, activeColor);
                extractor.fill(x + 4, y + 4, x + 6, y + 6, 0xFF101923);
            }
            default -> {}
        }
    }
}
