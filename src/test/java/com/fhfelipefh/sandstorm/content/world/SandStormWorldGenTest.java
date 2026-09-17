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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormWorldGenTest {

    private static final Path WORLDGEN_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "worldgen");
    private static final Path CONFIGURED_DIR = WORLDGEN_DIR.resolve("configured_feature");
    private static final Path PLACED_DIR = WORLDGEN_DIR.resolve("placed_feature");

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
            assertTrue(json.has("config"));
            JsonObject config = json.getAsJsonObject("config");
            assertTrue(config.has("targets"));
            assertTrue(config.getAsJsonArray("targets").size() > 0);
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
}
