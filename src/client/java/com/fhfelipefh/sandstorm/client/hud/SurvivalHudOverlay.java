package com.fhfelipefh.sandstorm.client.hud;

import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class SurvivalHudOverlay implements HudElement {
    private static final SuitPowerComponent CLIENT_SUIT = new SuitPowerComponent();

    public static void initialize() {
        HudElementRegistry.addLast(SandStormMod.id("survival_hud"), new SurvivalHudOverlay());
    }

    public static void updateSuitData(long storedEnergy, long capacity, double temperature, int armorCount) {
        CLIENT_SUIT.getEnergyStorage().setStoredEnergy(storedEnergy);
        CLIENT_SUIT.getThermal().setCurrentTemperature(temperature);
        CLIENT_SUIT.updateEquippedArmorCount(armorCount);
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

        SandstormWeatherComponent weather = SandstormWeatherHandler.getWeather();
        if (weather.isActive()) {
            int alpha = (int) Math.clamp(weather.getIntensity() * 140, 0, 160);
            int sandColor = (alpha << 24) | 0xC29B62;
            extractor.fill(0, 0, screenWidth, screenHeight, sandColor);
        }

        double energyPercent = capacity > 0 ? ((double) storedEnergy * 100.0 / (double) capacity) : 0.0;
        String energyText = String.format(Locale.ROOT, "BAT: %.2f%%", energyPercent);
        String tempText = String.format(Locale.ROOT, "TEMP: %.2f °C", temperature);
        String stormText = weather.isActive() ? "STORM: " + (int) (weather.getIntensity() * 100) + "%" : null;

        int batWidth = client.font.width(energyText);
        int tempWidth = client.font.width(tempText);
        int stormWidth = stormText != null ? client.font.width(stormText) : 0;
        int maxTextWidth = Math.max(batWidth, Math.max(tempWidth, stormWidth));

        int margin = 8;
        int x = screenWidth - maxTextWidth - margin;
        int y = screenHeight - 45;

        int hotbarRight = (screenWidth / 2) + 95;
        if (x < hotbarRight) {
            if (screenHeight > 160) {
                y = screenHeight - 65;
            } else {
                y = margin;
            }
            x = Math.max(margin, screenWidth - maxTextWidth - margin);
        }

        int batteryColor = getBatteryColor(energyPercent);
        int tempColor = getTemperatureColor(temperature);

        float maxAllowed = screenWidth - (margin * 2f);
        if (maxTextWidth > maxAllowed && maxAllowed > 0) {
            float scale = maxAllowed / (float) maxTextWidth;
            extractor.pose().pushMatrix();
            extractor.pose().translate(margin, y);
            extractor.pose().scale(scale, scale);
            extractor.text(client.font, Component.literal(energyText), 0, 0, batteryColor);
            extractor.text(client.font, Component.literal(tempText), 0, 11, tempColor);
            if (stormText != null) {
                extractor.text(client.font, Component.literal(stormText), 0, 22, 0xFFFF5555);
            }
            extractor.pose().popMatrix();
        } else {
            extractor.text(client.font, Component.literal(energyText), x, y, batteryColor);
            extractor.text(client.font, Component.literal(tempText), x, y + 11, tempColor);
            if (stormText != null) {
                extractor.text(client.font, Component.literal(stormText), x, y + 22, 0xFFFF5555);
            }
        }
    }

    public static int getBatteryColor(double percent) {
        if (percent > 30.0) {
            return 0xFF55FF55;
        } else if (percent > 15.0) {
            float t = (float) ((percent - 15.0) / 15.0);
            return lerpColor(0xFFFFA500, 0xFF55FF55, t);
        } else {
            float t = (float) (percent / 15.0);
            return lerpColor(0xFFFF3333, 0xFFFFA500, t);
        }
    }

    public static int getTemperatureColor(double temp) {
        if (temp >= 45.0) {
            float t = (float) Math.clamp((temp - 45.0) / 5.0, 0.0, 1.0);
            return lerpColor(0xFFFFA500, 0xFFFF2222, t);
        } else if (temp >= 38.0) {
            float t = (float) Math.clamp((temp - 38.0) / 7.0, 0.0, 1.0);
            return lerpColor(0xFF55FF55, 0xFFFFA500, t);
        } else if (temp >= 24.0) {
            float t = (float) Math.clamp((temp - 24.0) / 14.0, 0.0, 1.0);
            return lerpColor(0xFF00E5FF, 0xFF55FF55, t);
        } else {
            float t = (float) Math.clamp((temp - 5.0) / 19.0, 0.0, 1.0);
            return lerpColor(0xFF2979FF, 0xFF00E5FF, t);
        }
    }

    public static int lerpColor(int colorA, int colorB, float t) {
        float factor = Math.clamp(t, 0.0f, 1.0f);
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;
        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;
        int r = Math.round(rA + (rB - rA) * factor);
        int g = Math.round(gA + (gB - gA) * factor);
        int b = Math.round(bA + (bB - bA) * factor);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static SuitPowerComponent getClientSuit() {
        return CLIENT_SUIT;
    }
}
