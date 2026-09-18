package com.fhfelipefh.sandstorm.content.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormWorldGenTest {

    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data");
    private static final Path WORLDGEN_DIR = DATA_DIR.resolve("sandstorm").resolve("worldgen");
    private static final Path FEATURE_DIR = WORLDGEN_DIR.resolve("feature");
    private static final Path PLACED_DIR = WORLDGEN_DIR.resolve("placed_feature");
    private static final Path LEGACY_CONFIGURED_FEATURE_DIR = WORLDGEN_DIR.resolve("configured_feature");

    private static final Path OVERWORLD_DIMENSION_JSON =
            DATA_DIR.resolve("minecraft").resolve("dimension").resolve("overworld.json");
    private static final Path NORMAL_WORLD_PRESET_JSON =
            DATA_DIR.resolve("minecraft").resolve("worldgen").resolve("world_preset").resolve("normal.json");

    @Test
    void shouldDefineValidPlacedFeatureKeys() {
        assertNotNull(SandStormWorldGen.BRACKISH_AQUIFER_KEY);
        assertEquals(Registries.PLACED_FEATURE, SandStormWorldGen.BRACKISH_AQUIFER_KEY.registryKey());
        assertEquals("sandstorm", SandStormWorldGen.BRACKISH_AQUIFER_KEY.identifier().getNamespace());
        assertEquals("brackish_aquifer", SandStormWorldGen.BRACKISH_AQUIFER_KEY.identifier().getPath());

        assertNotNull(SandStormWorldGen.BURIED_TECH_RUINS_KEY);
        assertEquals(Registries.PLACED_FEATURE, SandStormWorldGen.BURIED_TECH_RUINS_KEY.registryKey());
        assertEquals("sandstorm", SandStormWorldGen.BURIED_TECH_RUINS_KEY.identifier().getNamespace());
        assertEquals("buried_tech_ruins", SandStormWorldGen.BURIED_TECH_RUINS_KEY.identifier().getPath());

        assertNotNull(SandStormWorldGen.ANCIENT_DATA_CORE_KEY);
        assertEquals(Registries.PLACED_FEATURE, SandStormWorldGen.ANCIENT_DATA_CORE_KEY.registryKey());
        assertEquals("sandstorm", SandStormWorldGen.ANCIENT_DATA_CORE_KEY.identifier().getNamespace());
        assertEquals("ancient_data_core", SandStormWorldGen.ANCIENT_DATA_CORE_KEY.identifier().getPath());
    }

    @Test
    void shouldInitializeWorldGenWithoutException() throws Exception {
        try {
            net.minecraft.SharedConstants.getCurrentVersion();
        } catch (IllegalStateException e) {
            net.minecraft.SharedConstants.setVersion(net.minecraft.DetectedVersion.BUILT_IN);
        }
        net.minecraft.server.Bootstrap.bootStrap();
        
        com.mojang.serialization.Codec<?> featureCodec = null;
        for (net.minecraft.resources.RegistryDataLoader.RegistryData<?> rd : net.minecraft.resources.RegistryDataLoader.WORLD_REGISTRIES) {
            if (rd.key().identifier().getPath().equals("worldgen/feature")) {
                featureCodec = rd.elementCodec();
            }
        }
        com.mojang.serialization.Codec<?> noiseSettingsCodec = null;
        com.mojang.serialization.Codec<?> biomeCodec = null;
        for (net.minecraft.resources.RegistryDataLoader.RegistryData<?> rd : net.minecraft.resources.RegistryDataLoader.WORLD_REGISTRIES) {
            if (rd.key().identifier().getPath().equals("worldgen/noise_settings")) {
                noiseSettingsCodec = rd.elementCodec();
            }
            if (rd.key().identifier().getPath().equals("worldgen/biome")) {
                biomeCodec = rd.elementCodec();
            }
        }
        assertNotNull(noiseSettingsCodec, "noiseSettingsCodec must exist");
        assertNotNull(biomeCodec, "biomeCodec must exist");
        assertNotNull(featureCodec, "featureCodec for worldgen/feature must exist");

        String testValidWithId = """
        {
          "type": "minecraft:ore",
          "discard_chance_on_air_exposure": 0.0,
          "size": 4,
          "targets": [
            {
              "state": {
                "id": "minecraft:stone"
              },
              "target": {
                "block": "minecraft:sand",
                "predicate_type": "minecraft:block_match"
              }
            }
          ]
        }
        """;

        com.mojang.serialization.DataResult<?> res = featureCodec.parse(com.mojang.serialization.JsonOps.INSTANCE, JsonParser.parseString(testValidWithId));
        assertTrue(res.result().isPresent(), "Parsing should succeed with valid vanilla block: " + res);
        assertDoesNotThrow(SandStormWorldGen::initialize);
    }

    @ParameterizedTest
    @ValueSource(strings = {"brackish_aquifer", "buried_tech_ruins", "ancient_data_core"})
    void shouldHaveValidFeatureJson(String featureName) throws IOException {
        Path jsonPath = FEATURE_DIR.resolve(featureName + ".json");
        assertTrue(Files.exists(jsonPath), "Missing feature: " + jsonPath);

        try (FileReader reader = new FileReader(jsonPath.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            assertTrue(parsed.isJsonObject());
            JsonObject json = parsed.getAsJsonObject();
            assertTrue(json.has("type"));
            assertEquals("minecraft:ore", json.get("type").getAsString());
            assertFalse(json.has("config"), "Feature JSON in Minecraft 26.3 must not have a 'config' wrapper");
            assertTrue(json.has("size"));
            assertTrue(json.has("discard_chance_on_air_exposure"));
            assertTrue(json.has("targets"));
            assertTrue(json.getAsJsonArray("targets").size() > 0);
            json.getAsJsonArray("targets").forEach(targetElem -> {
                assertTrue(targetElem.isJsonObject());
                JsonObject target = targetElem.getAsJsonObject();
                assertTrue(target.has("state"));
                JsonObject state = target.getAsJsonObject("state");
                assertTrue(state.has("id"), "Block state must specify 'id'");
                assertFalse(state.has("Name"), "Block state must not use legacy 'Name'");
                assertTrue(target.has("target"));
                JsonObject targetCondition = target.getAsJsonObject("target");
                assertTrue(targetCondition.has("predicate_type"));
            });
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"brackish_aquifer", "buried_tech_ruins", "ancient_data_core"})
    void shouldHaveValidPlacedFeatureJson(String featureName) throws IOException {
        Path jsonPath = PLACED_DIR.resolve(featureName + ".json");
        assertTrue(Files.exists(jsonPath), "Missing placed feature: " + jsonPath);

        try (FileReader reader = new FileReader(jsonPath.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            assertTrue(parsed.isJsonObject());
            JsonObject json = parsed.getAsJsonObject();
            assertTrue(json.has("feature"));
            assertEquals("sandstorm:" + featureName, json.get("feature").getAsString());
            assertTrue(json.has("placement"));
            assertTrue(json.getAsJsonArray("placement").size() > 0);
        }
    }

    @Test
    void shouldNotContainLegacyConfiguredFeatureDirectory() {
        assertFalse(Files.exists(LEGACY_CONFIGURED_FEATURE_DIR), "Legacy worldgen/configured_feature directory is not loaded in 26.3 and must not exist");
        assertTrue(Files.exists(FEATURE_DIR), "worldgen/feature directory is required in Minecraft 26.3");
    }

    @Test
    void shouldEnforceDesertDimensionConfiguration() throws IOException {
        assertTrue(Files.exists(OVERWORLD_DIMENSION_JSON), "Overworld dimension override must exist");

        try (FileReader reader = new FileReader(OVERWORLD_DIMENSION_JSON.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            assertTrue(parsed.isJsonObject());
            JsonObject root = parsed.getAsJsonObject();

            assertTrue(root.has("type"));
            assertEquals("minecraft:overworld", root.get("type").getAsString());

            assertTrue(root.has("generator"));
            JsonObject generator = root.getAsJsonObject("generator");
            assertEquals("minecraft:noise", generator.get("type").getAsString());

            assertTrue(generator.has("biome_source"));
            JsonObject biomeSource = generator.getAsJsonObject("biome_source");
            assertEquals("minecraft:fixed", biomeSource.get("type").getAsString());
            assertEquals("minecraft:desert", biomeSource.get("biome").getAsString());
        }
    }

    @Test
    void shouldEnforceDesertWorldPresetConfiguration() throws IOException {
        assertTrue(Files.exists(NORMAL_WORLD_PRESET_JSON), "Normal world preset override must exist");

        try (FileReader reader = new FileReader(NORMAL_WORLD_PRESET_JSON.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            assertTrue(parsed.isJsonObject());
            JsonObject root = parsed.getAsJsonObject();

            assertTrue(root.has("dimensions"));
            JsonObject dimensions = root.getAsJsonObject("dimensions");
            assertTrue(dimensions.has("minecraft:overworld"));

            JsonObject overworld = dimensions.getAsJsonObject("minecraft:overworld");
            assertEquals("minecraft:overworld", overworld.get("type").getAsString());

            assertTrue(overworld.has("generator"));
            JsonObject generator = overworld.getAsJsonObject("generator");
            assertEquals("minecraft:noise", generator.get("type").getAsString());

            assertTrue(generator.has("biome_source"));
            JsonObject biomeSource = generator.getAsJsonObject("biome_source");
            assertEquals("minecraft:fixed", biomeSource.get("type").getAsString());
            assertEquals("minecraft:desert", biomeSource.get("biome").getAsString());
        }
    }

    private static final Path OVERWORLD_NOISE_SETTINGS_JSON =
            DATA_DIR.resolve("minecraft").resolve("worldgen").resolve("noise_settings").resolve("overworld.json");
    private static final Path DESERT_BIOME_JSON =
            DATA_DIR.resolve("minecraft").resolve("worldgen").resolve("biome").resolve("desert.json");

    @Test
    void shouldEnforceAridOverworldNoiseSettings() throws IOException {
        assertTrue(Files.exists(OVERWORLD_NOISE_SETTINGS_JSON), "Overworld noise settings override must exist");

        try (FileReader reader = new FileReader(OVERWORLD_NOISE_SETTINGS_JSON.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(json.has("sea_level"));
            assertEquals(-64, json.get("sea_level").getAsInt(), "Sea level must be set to -64 to eliminate surface oceans");
            assertTrue(json.has("default_fluid"));
            assertEquals("minecraft:air", json.get("default_fluid").getAsString(), "Default fluid must be air to prevent flooded basins and caves");
        }
    }

    @Test
    void shouldEnforceWaterlessAndSterileDesertBiome() throws IOException {
        assertTrue(Files.exists(DESERT_BIOME_JSON), "Desert biome override must exist");

        try (FileReader reader = new FileReader(DESERT_BIOME_JSON.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String jsonString = json.toString();
            assertFalse(jsonString.contains("minecraft:desert_well"), "Desert well must be removed to avoid surface water generation");
            assertFalse(jsonString.contains("minecraft:spring_water"), "Spring water must be removed to avoid water cascades");
            assertFalse(jsonString.contains("minecraft:underwater_magma"), "Underwater magma must be removed");
            assertFalse(jsonString.contains("minecraft:patch_cactus_desert"), "Cactus must be removed for desolate lore");
            assertFalse(jsonString.contains("minecraft:flower_default"), "Flowers must be removed for desolate lore");
            assertFalse(jsonString.contains("minecraft:patch_dead_bush_2"), "Dead bush must be removed for desolate lore");
            assertFalse(jsonString.contains("minecraft:patch_dry_grass_desert"), "Dry grass must be removed for desolate lore");
            assertTrue(json.has("features"));
            assertEquals(0, json.getAsJsonArray("features").get(9).getAsJsonArray().size(), "Vegetal decoration step must be empty");
        }
    }
}
