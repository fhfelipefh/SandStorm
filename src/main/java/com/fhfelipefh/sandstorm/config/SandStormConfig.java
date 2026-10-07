package com.fhfelipefh.sandstorm.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SandStormConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int MIN_RADIUS = 8;
    private static final int MAX_RADIUS = 512;
    private static final long MIN_ENERGY_COST = 0L;
    private static final long MAX_ENERGY_COST = 100000L;
    private static final int MIN_INCUBATOR_TICKS = 20;
    private static final int MAX_INCUBATOR_TICKS = 72000;

    private static ConfigData data = new ConfigData();

    public static class ConfigData {
        public int cryogenicChillerRadius = 64;
        public long cryogenicChillerEnergyCostPerTick = 50L;
        public int amnioticIncubatorCycleDurationTicks = 1200;
        public long amnioticIncubatorEnergyCostPerTick = 80L;
        public int atmosphericTerraformerRadius = 64;
    }

    public static void load() {
        Path configPath = getConfigPath();
        if (!Files.exists(configPath)) {
            data = new ConfigData();
            save();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            ConfigData loaded = GSON.fromJson(reader, ConfigData.class);
            if (loaded != null) {
                data = loaded;
                sanitize();
            } else {
                data = new ConfigData();
                save();
            }
        } catch (IOException e) {
            data = new ConfigData();
        }
    }

    public static void save() {
        Path configPath = getConfigPath();
        try {
            Path parent = configPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static void resetDefaults() {
        data = new ConfigData();
        save();
    }

    private static void sanitize() {
        data.cryogenicChillerRadius = clamp(data.cryogenicChillerRadius, MIN_RADIUS, MAX_RADIUS);
        data.cryogenicChillerEnergyCostPerTick = clamp(data.cryogenicChillerEnergyCostPerTick, MIN_ENERGY_COST, MAX_ENERGY_COST);
        data.amnioticIncubatorCycleDurationTicks = clamp(data.amnioticIncubatorCycleDurationTicks, MIN_INCUBATOR_TICKS, MAX_INCUBATOR_TICKS);
        data.amnioticIncubatorEnergyCostPerTick = clamp(data.amnioticIncubatorEnergyCostPerTick, MIN_ENERGY_COST, MAX_ENERGY_COST);
        data.atmosphericTerraformerRadius = clamp(data.atmosphericTerraformerRadius, MIN_RADIUS, MAX_RADIUS);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    private static Path getConfigPath() {
        try {
            return FabricLoader.getInstance().getConfigDir().resolve("sandstorm.json");
        } catch (Throwable ignored) {
            return Path.of("config", "sandstorm.json");
        }
    }

    public static int getCryogenicChillerRadius() {
        return data.cryogenicChillerRadius;
    }

    public static void setCryogenicChillerRadius(int radius) {
        data.cryogenicChillerRadius = clamp(radius, MIN_RADIUS, MAX_RADIUS);
    }

    public static long getCryogenicChillerEnergyCost() {
        return data.cryogenicChillerEnergyCostPerTick;
    }

    public static void setCryogenicChillerEnergyCost(long cost) {
        data.cryogenicChillerEnergyCostPerTick = clamp(cost, MIN_ENERGY_COST, MAX_ENERGY_COST);
    }

    public static int getAmnioticIncubatorCycleTicks() {
        return data.amnioticIncubatorCycleDurationTicks;
    }

    public static void setAmnioticIncubatorCycleTicks(int ticks) {
        data.amnioticIncubatorCycleDurationTicks = clamp(ticks, MIN_INCUBATOR_TICKS, MAX_INCUBATOR_TICKS);
    }

    public static long getAmnioticIncubatorEnergyCost() {
        return data.amnioticIncubatorEnergyCostPerTick;
    }

    public static void setAmnioticIncubatorEnergyCost(long cost) {
        data.amnioticIncubatorEnergyCostPerTick = clamp(cost, MIN_ENERGY_COST, MAX_ENERGY_COST);
    }

    public static int getAtmosphericTerraformerRadius() {
        return data.atmosphericTerraformerRadius;
    }

    public static void setAtmosphericTerraformerRadius(int radius) {
        data.atmosphericTerraformerRadius = clamp(radius, MIN_RADIUS, MAX_RADIUS);
    }
}
