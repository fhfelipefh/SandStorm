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

    private static long lastStoredEnergy = -1;
    private static long lastCapacity = -1;
    private static double lastTemperature = -999.0;

    private static Component cachedEnergyComp = Component.empty();
    private static Component cachedTempComp = Component.empty();
    private static int cachedBatWidth = 0;
    private static int cachedTempWidth = 0;
    private static int cachedMaxTextWidth = 0;
    private static int cachedBatteryColor = 0xFF55FF55;
    private static int cachedTempColor = 0xFF00E5FF;

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
        if (weather.isActive() && weather.getIntensity() > 0.02) {
            int alpha = (int) Math.clamp(weather.getIntensity() * 25, 0, 32);
            int sandColor = (alpha << 24) | 0xC29B62;
            extractor.fill(0, 0, screenWidth, screenHeight, sandColor);
            renderSandGrains(extractor, client, screenWidth, screenHeight, weather.getIntensity());
        }

        boolean energyChanged = storedEnergy != lastStoredEnergy || capacity != lastCapacity;
        if (energyChanged) {
            lastStoredEnergy = storedEnergy;
            lastCapacity = capacity;
            double energyPercent = capacity > 0 ? ((double) storedEnergy * 100.0 / (double) capacity) : 0.0;
            String energyText = String.format(Locale.ROOT, "BAT: %.2f%%", energyPercent);
            cachedEnergyComp = Component.literal(energyText);
            cachedBatWidth = client.font.width(energyText);
            cachedBatteryColor = getBatteryColor(energyPercent);
        }

        boolean tempChanged = Math.abs(temperature - lastTemperature) >= 0.01;
        if (tempChanged) {
            lastTemperature = temperature;
            String tempText = String.format(Locale.ROOT, "TEMP: %.2f °C", temperature);
            cachedTempComp = Component.literal(tempText);
            cachedTempWidth = client.font.width(tempText);
            cachedTempColor = getTemperatureColor(temperature);
        }

        if (energyChanged || tempChanged) {
            cachedMaxTextWidth = Math.max(cachedBatWidth, cachedTempWidth);
        }

        int maxTextWidth = cachedMaxTextWidth;
        int margin = 8;
        int x = screenWidth - maxTextWidth - margin;
        int y = screenHeight - 34;

        int hotbarRight = (screenWidth / 2) + 95;
        if (x < hotbarRight) {
            if (screenHeight > 160) {
                y = screenHeight - 54;
            } else {
                y = margin;
            }
            x = Math.max(margin, screenWidth - maxTextWidth - margin);
        }

        float maxAllowed = screenWidth - (margin * 2f);
        if (maxTextWidth > maxAllowed && maxAllowed > 0) {
            float scale = maxAllowed / (float) maxTextWidth;
            extractor.pose().pushMatrix();
            extractor.pose().translate(margin, y);
            extractor.pose().scale(scale, scale);
            extractor.text(client.font, cachedEnergyComp, 0, 0, cachedBatteryColor);
            extractor.text(client.font, cachedTempComp, 0, 11, cachedTempColor);
            extractor.pose().popMatrix();
        } else {
            extractor.text(client.font, cachedEnergyComp, x, y, cachedBatteryColor);
            extractor.text(client.font, cachedTempComp, x, y + 11, cachedTempColor);
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

    private static void renderSandGrains(GuiGraphicsExtractor extractor, Minecraft client, int width, int height, double intensity) {
        if (client.player == null) {
            return;
        }
        int tick = client.player.tickCount;
        int grainCount = (int) (6 + intensity * 14);
        for (int i = 0; i < grainCount; i++) {
            int seed = (i * 37) ^ 0x5DEECE66;
            int speed = 8 + (seed % 14);
            int streakLength = 4 + (seed % 14);
            int startX = (int) ((seed + (long) tick * speed) % (width + streakLength + 40)) - streakLength;
            int x = width - startX;
            int y = Math.abs((seed * 31 + i * 17) % Math.max(1, height));

            int grainAlpha = (int) Math.clamp(intensity * (30 + (seed % 30)), 15, 60);
            int colorIndex = (seed >> 3) & 3;
            int rgb = switch (colorIndex) {
                case 0 -> 0xD8B880;
                case 1 -> 0xC29B62;
                case 2 -> 0xE0C896;
                default -> 0xA88048;
            };
            int grainColor = (grainAlpha << 24) | rgb;
            extractor.fill(x, y, x + streakLength, y + 1, grainColor);
            if ((seed & 1) == 0) {
                extractor.fill(x + 1, y + 1, x + (streakLength / 2) + 1, y + 2, ((grainAlpha / 2) << 24) | rgb);
            }
        }
    }

    public static SuitPowerComponent getClientSuit() {
        return CLIENT_SUIT;
    }
}
