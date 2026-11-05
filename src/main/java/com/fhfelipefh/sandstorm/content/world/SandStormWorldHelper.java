package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.Locale;

public final class SandStormWorldHelper {

    public static final ResourceKey<NoiseGeneratorSettings> DESERT_PLANET_NOISE =
            ResourceKey.create(Registries.NOISE_SETTINGS, SandStormMod.id("desert_planet"));

    private static Boolean forcedSandStormWorldForTesting = null;

    private SandStormWorldHelper() {
    }

    public static boolean isSandStormWorld(Level level) {
        if (forcedSandStormWorldForTesting != null) {
            return forcedSandStormWorldForTesting;
        }
        if (level == null) {
            return false;
        }
        if (ShowcaseAutomation.isShowcaseActive()) {
            return true;
        }
        if (level instanceof ServerLevel serverLevel) {
            MinecraftServer server = serverLevel.getServer();
            if (server != null && isShowcaseServer(server)) {
                return true;
            }
            if (serverLevel.dimension() == Level.OVERWORLD) {
                return isSandStormGenerator(serverLevel.getChunkSource().getGenerator());
            }
            if (server != null) {
                ServerLevel overworld = server.overworld();
                if (overworld != null) {
                    return isSandStormGenerator(overworld.getChunkSource().getGenerator());
                }
            }
        }
        MinecraftServer server = level.getServer();
        if (server != null) {
            return isSandStormWorld(server);
        }
        return false;
    }

    public static boolean isSandStormWorld(MinecraftServer server) {
        if (forcedSandStormWorldForTesting != null) {
            return forcedSandStormWorldForTesting;
        }
        if (server == null) {
            return false;
        }
        if (ShowcaseAutomation.isShowcaseActive()) {
            return true;
        }
        if (isShowcaseServer(server)) {
            return true;
        }
        ServerLevel overworld = server.overworld();
        if (overworld != null) {
            return isSandStormGenerator(overworld.getChunkSource().getGenerator());
        }
        return false;
    }

    private static boolean isShowcaseServer(MinecraftServer server) {
        if (server.getWorldData() != null && server.getWorldData().getLevelName() != null) {
            return server.getWorldData().getLevelName().toLowerCase(Locale.ROOT).contains("showcase");
        }
        return false;
    }

    public static boolean isSandStormGenerator(ChunkGenerator generator) {
        if (generator instanceof NoiseBasedChunkGenerator noiseGen) {
            return noiseGen.stable(DESERT_PLANET_NOISE);
        }
        return false;
    }

    public static void setForcedSandStormWorldForTesting(Boolean forced) {
        forcedSandStormWorldForTesting = forced;
    }
}
