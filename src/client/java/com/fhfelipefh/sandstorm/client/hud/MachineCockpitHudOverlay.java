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
        int screenHeight = extractor.guiHeight();
        int baseWidth = 175;
        int baseHeight = 50;
        int margin = 8;

        float maxAllowedW = Math.max(50.0f, screenWidth - (margin * 2.0f));
        float maxAllowedH = Math.max(30.0f, screenHeight - (margin * 2.0f));
        float scale = 1.0f;
        if (baseWidth > maxAllowedW || baseHeight > maxAllowedH) {
            scale = Math.min(maxAllowedW / (float) baseWidth, maxAllowedH / (float) baseHeight);
        }

        int x = margin;
        int y = (int) Math.max(margin, screenHeight - (baseHeight * scale) - margin);

        extractor.pose().pushMatrix();
        extractor.pose().translate(x, y);
        if (scale < 1.0f) {
            extractor.pose().scale(scale, scale);
        }

        extractor.fill(0, 0, baseWidth, baseHeight, 0xCC07111A);
        extractor.fill(1, 1, baseWidth - 1, baseHeight - 1, 0xDD0D1824);
        extractor.fill(0, 0, baseWidth, 1, 0xFF00E5FF);
        extractor.fill(0, baseHeight - 1, baseWidth, baseHeight, 0xFF00E5FF);
        extractor.fill(0, 0, 1, baseHeight, 0xFF00E5FF);
        extractor.fill(baseWidth - 1, 0, baseWidth, baseHeight, 0xFF00E5FF);

        Component header = Component.literal("\u00A7b[\u00A7fESCAVADEIRA MK-I\u00A7b] \u00A77OPERACIONAL");
        extractor.text(client.font, header, 4, 4, 0xFFFFFFFF);

        float health = excavator.getHealth();
        float maxHealth = excavator.getMaxHealth();
        float healthPct = maxHealth > 0.0f ? Math.clamp(health / maxHealth, 0.0f, 1.0f) : 0.0f;
        int healthColor = healthPct > 0.5f ? 0xFF00E676 : (healthPct > 0.25f ? 0xFFFFD600 : 0xFFFF1744);

        int barW = 105;
        int fillHealth = (int) (barW * healthPct);
        extractor.fill(4, 15, 4 + barW, 21, 0xFF15222E);
        if (fillHealth > 0) {
            extractor.fill(4, 15, 4 + fillHealth, 21, healthColor);
        }
        String healthStr = String.format(Locale.ROOT, "CASCO %.0f%%", healthPct * 100.0f);
        extractor.text(client.font, Component.literal(healthStr), 114, 14, healthColor);

        long energy = excavator.getEnergyStorage().getStoredEnergy();
        long capacity = excavator.getEnergyStorage().getCapacity();
        float energyPct = capacity > 0 ? Math.clamp((float) energy / (float) capacity, 0.0f, 1.0f) : 0.0f;
        int energyColor = energyPct > 0.2f ? 0xFF00B0FF : 0xFFFF9100;

        int fillEnergy = (int) (barW * energyPct);
        extractor.fill(4, 25, 4 + barW, 31, 0xFF15222E);
        if (fillEnergy > 0) {
            extractor.fill(4, 25, 4 + fillEnergy, 31, energyColor);
        }
        String energyStr = String.format(Locale.ROOT, "PWR %.0f%%", energyPct * 100.0f);
        extractor.text(client.font, Component.literal(energyStr), 114, 24, energyColor);

        String drillTxt = energy >= 15L ? "\u00A7aBROCA: ATIVA" : "\u00A7cBROCA: SEM ENERGIA";
        String depthTxt = String.format(Locale.ROOT, "\u00A73PROF: Y=%d", excavator.getBlockY());
        Component subStatus = Component.literal(drillTxt + " \u00A78| " + depthTxt);
        extractor.text(client.font, subStatus, 4, 36, 0xFFFFFFFF);

        extractor.pose().popMatrix();
    }

    private void renderMegazordHud(GuiGraphicsExtractor extractor, Minecraft client, MegazordEntity megazord) {
        int screenWidth = extractor.guiWidth();
        int screenHeight = extractor.guiHeight();
        int baseWidth = 185;
        int baseHeight = 58;
        int margin = 8;

        float maxAllowedW = Math.max(50.0f, screenWidth - (margin * 2.0f));
        float maxAllowedH = Math.max(30.0f, screenHeight - (margin * 2.0f));
        float scale = 1.0f;
        if (baseWidth > maxAllowedW || baseHeight > maxAllowedH) {
            scale = Math.min(maxAllowedW / (float) baseWidth, maxAllowedH / (float) baseHeight);
        }

        int x = margin;
        int y = (int) Math.max(margin, screenHeight - (baseHeight * scale) - margin);

        extractor.pose().pushMatrix();
        extractor.pose().translate(x, y);
        if (scale < 1.0f) {
            extractor.pose().scale(scale, scale);
        }

        extractor.fill(0, 0, baseWidth, baseHeight, 0xCC0A0F1A);
        extractor.fill(1, 1, baseWidth - 1, baseHeight - 1, 0xDD101626);
        boolean overdrive = megazord.hasOverdriveModule();
        int borderCol = overdrive ? 0xFFFF1744 : 0xFFFFAB00;
        extractor.fill(0, 0, baseWidth, 1, borderCol);
        extractor.fill(0, baseHeight - 1, baseWidth, baseHeight, borderCol);
        extractor.fill(0, 0, 1, baseHeight, borderCol);
        extractor.fill(baseWidth - 1, 0, baseWidth, baseHeight, borderCol);

        Component header = Component.literal("\u00A76[\u00A7fTIT\u00C3 MEGAZORD\u00A76] \u00A7e" + megazord.getVariant().name());
        extractor.text(client.font, header, 4, 4, 0xFFFFFFFF);

        float health = megazord.getHealth();
        float maxHealth = megazord.getMaxHealth();
        float healthPct = maxHealth > 0.0f ? Math.clamp(health / maxHealth, 0.0f, 1.0f) : 0.0f;
        int healthColor = healthPct > 0.5f ? 0xFF00E676 : (healthPct > 0.25f ? 0xFFFFD600 : 0xFFFF1744);

        int barW = 110;
        int fillHealth = (int) (barW * healthPct);
        extractor.fill(4, 15, 4 + barW, 21, 0xFF1A1A2E);
        if (fillHealth > 0) {
            extractor.fill(4, 15, 4 + fillHealth, 21, healthColor);
        }
        String healthStr = String.format(Locale.ROOT, "CASCO %.0f%%", healthPct * 100.0f);
        extractor.text(client.font, Component.literal(healthStr), 119, 14, healthColor);

        long energy = megazord.getEnergyStorage().getStoredEnergy();
        long capacity = megazord.getEnergyStorage().getCapacity();
        float energyPct = capacity > 0 ? Math.clamp((float) energy / (float) capacity, 0.0f, 1.0f) : 0.0f;
        int energyColor = overdrive ? 0xFFFF1744 : 0xFF00E5FF;

        int fillEnergy = (int) (barW * energyPct);
        extractor.fill(4, 25, 4 + barW, 31, 0xFF1A1A2E);
        if (fillEnergy > 0) {
            extractor.fill(4, 25, 4 + fillEnergy, 31, energyColor);
        }
        String energyStr = String.format(Locale.ROOT, overdrive ? "OVERDRIVE" : "CORE %.0f%%", energyPct * 100.0f);
        extractor.text(client.font, Component.literal(energyStr), 119, 24, energyColor);

        boolean flying = megazord.hasFlightModule() && !megazord.onGround();
        String propMode = flying ? "\u00A7bVTOL VOO" : "\u00A77TERRESTRE";
        String altTxt = String.format(Locale.ROOT, "\u00A7eALT: Y=%d", megazord.getBlockY());
        Component flightComp = Component.literal(propMode + " \u00A78| " + altTxt);
        extractor.text(client.font, flightComp, 4, 35, 0xFFFFFFFF);

        Component cockpitComp = Component.literal("\u00A7a[COCKPIT: ELEVA\u00C7\u00C3O +3.85m]");
        extractor.text(client.font, cockpitComp, 4, 46, 0xFF00E676);

        extractor.pose().popMatrix();
    }
}
