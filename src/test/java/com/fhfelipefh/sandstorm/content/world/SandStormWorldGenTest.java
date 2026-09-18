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
    private static final Path CONFIGURED_DIR = WORLDGEN_DIR.resolve("configured_feature");
    private static final Path PLACED_DIR = WORLDGEN_DIR.resolve("placed_feature");
    private static final Path LEGACY_FEATURE_DIR = WORLDGEN_DIR.resolve("feature");

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
    void shouldInitializeWorldGenWithoutException() {
        assertDoesNotThrow(SandStormWorldGen::initialize);
    }

    @ParameterizedTest
    @ValueSource(strings = {"brackish_aquifer", "buried_tech_ruins", "ancient_data_core"})
    void shouldHaveValidConfiguredFeatureJson(String featureName) throws IOException {
        Path jsonPath = CONFIGURED_DIR.resolve(featureName + ".json");
        assertTrue(Files.exists(jsonPath), "Missing configured feature: " + jsonPath);

        try (FileReader reader = new FileReader(jsonPath.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            assertTrue(parsed.isJsonObject());
            JsonObject json = parsed.getAsJsonObject();
            assertTrue(json.has("type"));
            assertEquals("minecraft:ore", json.get("type").getAsString());
            assertFalse(json.has("config"));
            assertTrue(json.has("targets"));
            assertTrue(json.getAsJsonArray("targets").size() > 0);
            json.getAsJsonArray("targets").forEach(targetElem -> {
                assertTrue(targetElem.isJsonObject());
                JsonObject target = targetElem.getAsJsonObject();
                assertTrue(target.has("state"));
                JsonObject state = target.getAsJsonObject("state");
                assertFalse(state.has("Name"));
                assertTrue(state.has("id"));
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
    void shouldNotContainConflictingLegacyFeatureDirectory() {
        assertFalse(Files.exists(LEGACY_FEATURE_DIR), "Legacy worldgen/feature directory causes crash and must not exist");
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
}
