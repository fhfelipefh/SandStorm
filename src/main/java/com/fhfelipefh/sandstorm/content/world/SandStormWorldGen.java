package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class SandStormWorldGen {

    public static final ResourceKey<PlacedFeature> BRACKISH_AQUIFER_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, SandStormMod.id("brackish_aquifer"));

    public static final ResourceKey<PlacedFeature> BURIED_TECH_RUINS_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, SandStormMod.id("buried_tech_ruins"));

    public static final ResourceKey<PlacedFeature> ANCIENT_DATA_CORE_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, SandStormMod.id("ancient_data_core"));

    public static final ResourceKey<PlacedFeature> PIEZO_CAVERN_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, SandStormMod.id("piezo_cavern"));

    public static final ResourceKey<PlacedFeature> FULGURITE_MONOLITH_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, SandStormMod.id("fulgurite_monolith"));

    public static final ResourceKey<PlacedFeature> FOSSILIZED_OASIS_KEY =
            ResourceKey.create(Registries.PLACED_FEATURE, SandStormMod.id("fossilized_oasis"));

    public static void initialize() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                BRACKISH_AQUIFER_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_DECORATION,
                BURIED_TECH_RUINS_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_DECORATION,
                ANCIENT_DATA_CORE_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_DECORATION,
                PIEZO_CAVERN_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                FULGURITE_MONOLITH_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                FOSSILIZED_OASIS_KEY
        );
    }
}
