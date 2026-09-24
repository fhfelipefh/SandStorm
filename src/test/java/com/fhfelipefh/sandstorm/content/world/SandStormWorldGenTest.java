package com.fhfelipefh.sandstorm.content.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.Bootstrap;
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
    private static final Path OVERWORLD_NOISE_SETTINGS_JSON =
            DATA_DIR.resolve("minecraft").resolve("worldgen").resolve("noise_settings").resolve("overworld.json");
    private static final Path DESERT_PLANET_WORLD_PRESET_JSON =
            DATA_DIR.resolve("sandstorm").resolve("worldgen").resolve("world_preset").resolve("desert_planet.json");
    private static final Path DESERT_PLANET_NOISE_SETTINGS_JSON =
            DATA_DIR.resolve("sandstorm").resolve("worldgen").resolve("noise_settings").resolve("desert_planet.json");
    private static final Path NORMAL_WORLD_PRESET_TAG_JSON =
            DATA_DIR.resolve("minecraft").resolve("tags").resolve("worldgen").resolve("world_preset").resolve("normal.json");

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

        assertNotNull(SandStormWorldGen.PIEZO_CAVERN_KEY);
        assertEquals(Registries.PLACED_FEATURE, SandStormWorldGen.PIEZO_CAVERN_KEY.registryKey());
        assertEquals("sandstorm", SandStormWorldGen.PIEZO_CAVERN_KEY.identifier().getNamespace());
        assertEquals("piezo_cavern", SandStormWorldGen.PIEZO_CAVERN_KEY.identifier().getPath());

        assertNotNull(SandStormWorldGen.FULGURITE_MONOLITH_KEY);
        assertEquals(Registries.PLACED_FEATURE, SandStormWorldGen.FULGURITE_MONOLITH_KEY.registryKey());
        assertEquals("sandstorm", SandStormWorldGen.FULGURITE_MONOLITH_KEY.identifier().getNamespace());
        assertEquals("fulgurite_monolith", SandStormWorldGen.FULGURITE_MONOLITH_KEY.identifier().getPath());

        assertNotNull(SandStormWorldGen.FOSSILIZED_OASIS_KEY);
        assertEquals(Registries.PLACED_FEATURE, SandStormWorldGen.FOSSILIZED_OASIS_KEY.registryKey());
        assertEquals("sandstorm", SandStormWorldGen.FOSSILIZED_OASIS_KEY.identifier().getNamespace());
        assertEquals("fossilized_oasis", SandStormWorldGen.FOSSILIZED_OASIS_KEY.identifier().getPath());
    }

    @Test
    void shouldInitializeWorldGenWithoutException() throws Exception {
        try {
            SharedConstants.getCurrentVersion();
        } catch (IllegalStateException e) {
            SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        }
        Bootstrap.bootStrap();
        
        Codec<?> featureCodec = null;
        for (RegistryDataLoader.RegistryData<?> rd : RegistryDataLoader.WORLD_REGISTRIES) {
            if (rd.key().identifier().getPath().equals("worldgen/feature")) {
                featureCodec = rd.elementCodec();
            }
        }
        Codec<?> noiseSettingsCodec = null;
        Codec<?> biomeCodec = null;
        for (RegistryDataLoader.RegistryData<?> rd : RegistryDataLoader.WORLD_REGISTRIES) {
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

        DataResult<?> res = featureCodec.parse(JsonOps.INSTANCE, JsonParser.parseString(testValidWithId));
        assertTrue(res.result().isPresent(), "Parsing should succeed with valid vanilla block: " + res);
        assertDoesNotThrow(SandStormWorldGen::initialize);
    }

    @ParameterizedTest
    @ValueSource(strings = {"brackish_aquifer", "buried_tech_ruins", "ancient_data_core", "piezo_cavern", "fulgurite_monolith", "fossilized_oasis"})
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
    @ValueSource(strings = {"brackish_aquifer", "buried_tech_ruins", "ancient_data_core", "piezo_cavern", "fulgurite_monolith", "fossilized_oasis"})
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
    void shouldDefineValidWorldPresetKey() {
        assertNotNull(SandStormWorldPresets.DESERT_PLANET);
        assertEquals(Registries.WORLD_PRESET, SandStormWorldPresets.DESERT_PLANET.registryKey());
        assertEquals("sandstorm", SandStormWorldPresets.DESERT_PLANET.identifier().getNamespace());
        assertEquals("desert_planet", SandStormWorldPresets.DESERT_PLANET.identifier().getPath());
    }

    @Test
    void shouldNotOverrideVanillaOverworldDimensionOrNormalPreset() {
        assertFalse(Files.exists(OVERWORLD_DIMENSION_JSON), "Vanilla overworld dimension override must not exist to decouple from vanilla");
        assertFalse(Files.exists(NORMAL_WORLD_PRESET_JSON), "Vanilla normal world preset override must not exist to decouple from vanilla");
        assertFalse(Files.exists(OVERWORLD_NOISE_SETTINGS_JSON), "Vanilla overworld noise settings override must not exist to decouple from vanilla");
    }

    @Test
    void shouldEnforceDedicatedDesertPlanetWorldPreset() throws IOException {
        assertTrue(Files.exists(DESERT_PLANET_WORLD_PRESET_JSON), "Desert planet world preset must exist");

        try (FileReader reader = new FileReader(DESERT_PLANET_WORLD_PRESET_JSON.toFile())) {
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
            assertEquals("sandstorm:desert_planet", generator.get("settings").getAsString());

            assertTrue(generator.has("biome_source"));
            JsonObject biomeSource = generator.getAsJsonObject("biome_source");
            assertEquals("minecraft:fixed", biomeSource.get("type").getAsString());
            assertEquals("minecraft:desert", biomeSource.get("biome").getAsString());
        }
    }

    @Test
    void shouldRegisterDesertPlanetInNormalWorldPresetTag() throws IOException {
        assertTrue(Files.exists(NORMAL_WORLD_PRESET_TAG_JSON), "World preset tag normal.json must exist");

        try (FileReader reader = new FileReader(NORMAL_WORLD_PRESET_TAG_JSON.toFile())) {
            JsonObject tag = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(tag.has("replace"));
            assertFalse(tag.get("replace").getAsBoolean(), "Tag must not replace existing presets");
            assertTrue(tag.has("values"));
            assertTrue(tag.getAsJsonArray("values").toString().contains("sandstorm:desert_planet"), "Tag must contain sandstorm:desert_planet");
        }
    }

    private static final Path DESERT_BIOME_JSON =
            DATA_DIR.resolve("minecraft").resolve("worldgen").resolve("biome").resolve("desert.json");
    private static final Path HAS_STRUCTURE_TAG_DIR =
            DATA_DIR.resolve("minecraft").resolve("tags").resolve("worldgen").resolve("biome").resolve("has_structure");

    @Test
    void shouldEnforceAridDesertPlanetNoiseSettings() throws IOException {
        assertTrue(Files.exists(DESERT_PLANET_NOISE_SETTINGS_JSON), "Desert planet noise settings must exist");

        try (FileReader reader = new FileReader(DESERT_PLANET_NOISE_SETTINGS_JSON.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(json.has("sea_level"));
            assertEquals(-64, json.get("sea_level").getAsInt(), "Sea level must be set to -64 to eliminate surface oceans");
            assertTrue(json.has("default_fluid"));
            assertEquals("minecraft:air", json.get("default_fluid").getAsString(), "Default fluid must be air to prevent flooded basins and caves");
            assertTrue(json.get("disable_mob_generation").getAsBoolean(), "Mob generation must be disabled in noise settings");
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
            assertFalse(jsonString.contains("minecraft:monster_room"), "Monster room dungeons must be removed");
            assertTrue(json.has("features"));
            assertEquals(0, json.getAsJsonArray("features").get(9).getAsJsonArray().size(), "Vegetal decoration step must be empty");

            JsonObject naturalSpawns = json.getAsJsonObject("attributes").getAsJsonObject("minecraft:gameplay/natural_mob_spawns");
            JsonObject spawnsByCategory = naturalSpawns.getAsJsonObject("argument").getAsJsonObject("spawns_by_category");
            assertEquals(0, spawnsByCategory.getAsJsonArray("creature").size(), "Creature spawns (rabbits, camels) must be empty");
            assertEquals(0, spawnsByCategory.getAsJsonArray("monster").size(), "Monster spawns must be empty");
            assertEquals(0, spawnsByCategory.getAsJsonArray("ambient").size(), "Ambient spawns must be empty");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ancient_city", "desert_pyramid", "mineshaft", "mineshaft_mesa",
            "stronghold", "trial_chambers", "ruined_portal_desert", "ruined_portal_standard",
            "village_desert", "village_plains", "village_savanna", "village_snowy", "village_taiga",
            "pillager_outpost", "woodland_mansion", "jungle_temple", "swamp_hut", "igloo"
    })
    void shouldDisableVanillaStructures(String structureName) throws IOException {
        Path tagPath = HAS_STRUCTURE_TAG_DIR.resolve(structureName + ".json");
        assertTrue(Files.exists(tagPath), "Structure tag override must exist: " + structureName);
        try (FileReader reader = new FileReader(tagPath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(json.has("replace") && json.get("replace").getAsBoolean());
            assertEquals(0, json.getAsJsonArray("values").size(), structureName + " structure must have no allowed biomes");
        }
    }
}
