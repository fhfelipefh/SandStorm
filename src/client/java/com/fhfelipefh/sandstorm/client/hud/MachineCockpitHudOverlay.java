package com.fhfelipefh.sandstorm.client.hud;

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
}
