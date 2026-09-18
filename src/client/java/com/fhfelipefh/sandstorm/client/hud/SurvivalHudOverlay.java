package com.fhfelipefh.sandstorm.client.hud;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class SurvivalHudOverlay implements HudElement {
    private static final SuitPowerComponent CLIENT_SUIT = new SuitPowerComponent();

    public static void initialize() {
        HudElementRegistry.addLast(SandStormMod.id("survival_hud"), new SurvivalHudOverlay());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        long storedEnergy = CLIENT_SUIT.getEnergyStorage().getStoredEnergy();
        long capacity = CLIENT_SUIT.getEnergyStorage().getCapacity();
        double temperature = CLIENT_SUIT.getThermal().getCurrentTemperature();

        int screenWidth = extractor.guiWidth();
        int screenHeight = extractor.guiHeight();

        com.fhfelipefh.sandstorm.component.SandstormWeatherComponent weather = com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler.getWeather();
        if (weather.isActive()) {
            int alpha = (int) Math.clamp(weather.getIntensity() * 140, 0, 160);
            int sandColor = (alpha << 24) | 0xC29B62;
            extractor.fill(0, 0, screenWidth, screenHeight, sandColor);
        }

        int x = screenWidth - 120;
        int y = screenHeight - 45;

        String energyText = "BAT: " + (storedEnergy * 100 / Math.max(1, capacity)) + "%";
        String tempText = "TEMP: " + String.format("%.1f", temperature) + " C";

        extractor.text(client.font, Component.literal(energyText), x, y, 0xFF55FF55);
        extractor.text(client.font, Component.literal(tempText), x, y + 11, 0xFFFFA500);

        if (weather.isActive()) {
            String stormText = "STORM: " + (int) (weather.getIntensity() * 100) + "%";
            extractor.text(client.font, Component.literal(stormText), x, y + 22, 0xFFFF5555);
        }
    }

    public static SuitPowerComponent getClientSuit() {
        return CLIENT_SUIT;
    }
}
