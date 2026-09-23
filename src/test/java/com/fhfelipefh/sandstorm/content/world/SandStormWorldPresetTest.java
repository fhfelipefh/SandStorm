package com.fhfelipefh.sandstorm.content.world;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormWorldPresetTest {

    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data");
    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets");

    private static final Path DESERT_PLANET_PRESET =
            DATA_DIR.resolve("sandstorm").resolve("worldgen").resolve("world_preset").resolve("desert_planet.json");
    private static final Path DESERT_PLANET_NOISE_SETTINGS =
            DATA_DIR.resolve("sandstorm").resolve("worldgen").resolve("noise_settings").resolve("desert_planet.json");
    private static final Path NORMAL_PRESET_TAG =
            DATA_DIR.resolve("minecraft").resolve("tags").resolve("worldgen").resolve("world_preset").resolve("normal.json");

    private static final Path VANILLA_OVERWORLD_DIMENSION =
            DATA_DIR.resolve("minecraft").resolve("dimension").resolve("overworld.json");
    private static final Path VANILLA_NORMAL_PRESET =
            DATA_DIR.resolve("minecraft").resolve("worldgen").resolve("world_preset").resolve("normal.json");
    private static final Path VANILLA_OVERWORLD_NOISE_SETTINGS =
            DATA_DIR.resolve("minecraft").resolve("worldgen").resolve("noise_settings").resolve("overworld.json");

    @Test
    void shouldDefineValidDesertPlanetKey() {
        assertNotNull(SandStormWorldPresets.DESERT_PLANET);
        assertEquals(Registries.WORLD_PRESET, SandStormWorldPresets.DESERT_PLANET.registryKey());
        assertEquals("sandstorm", SandStormWorldPresets.DESERT_PLANET.identifier().getNamespace());
        assertEquals("desert_planet", SandStormWorldPresets.DESERT_PLANET.identifier().getPath());
    }

    @Test
    void shouldNotContainVanillaDestructiveOverrides() {
        assertFalse(Files.exists(VANILLA_OVERWORLD_DIMENSION));
        assertFalse(Files.exists(VANILLA_NORMAL_PRESET));
        assertFalse(Files.exists(VANILLA_OVERWORLD_NOISE_SETTINGS));
    }

    @Test
    void shouldHaveValidDesertPlanetPresetStructure() throws IOException {
        assertTrue(Files.exists(DESERT_PLANET_PRESET));

        try (FileReader reader = new FileReader(DESERT_PLANET_PRESET.toFile())) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(root.has("dimensions"));
            JsonObject dimensions = root.getAsJsonObject("dimensions");

            assertTrue(dimensions.has("minecraft:overworld"));
            JsonObject overworld = dimensions.getAsJsonObject("minecraft:overworld");
            assertEquals("minecraft:overworld", overworld.get("type").getAsString());

            JsonObject generator = overworld.getAsJsonObject("generator");
            assertEquals("minecraft:noise", generator.get("type").getAsString());
            assertEquals("sandstorm:desert_planet", generator.get("settings").getAsString());

            JsonObject biomeSource = generator.getAsJsonObject("biome_source");
            assertEquals("minecraft:fixed", biomeSource.get("type").getAsString());
            assertEquals("minecraft:desert", biomeSource.get("biome").getAsString());

            assertTrue(dimensions.has("minecraft:the_nether"));
            assertTrue(dimensions.has("minecraft:the_end"));
        }
    }

    @Test
    void shouldHaveValidNoiseSettingsStructure() throws IOException {
        assertTrue(Files.exists(DESERT_PLANET_NOISE_SETTINGS));

        try (FileReader reader = new FileReader(DESERT_PLANET_NOISE_SETTINGS.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals(-64, json.get("sea_level").getAsInt());
            assertEquals("minecraft:air", json.get("default_fluid").getAsString());
            assertTrue(json.get("disable_mob_generation").getAsBoolean());
        }
    }

    @Test
    void shouldRegisterInNormalWorldPresetTag() throws IOException {
        assertTrue(Files.exists(NORMAL_PRESET_TAG));

        try (FileReader reader = new FileReader(NORMAL_PRESET_TAG.toFile())) {
            JsonObject tag = JsonParser.parseReader(reader).getAsJsonObject();
            assertFalse(tag.get("replace").getAsBoolean());
            assertTrue(tag.getAsJsonArray("values").toString().contains("sandstorm:desert_planet"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"pt_br.json", "en_us.json", "es_es.json"})
    void shouldContainTranslationKeys(String langFileName) throws IOException {
        Path langPath = ASSETS_DIR.resolve("sandstorm").resolve("lang").resolve(langFileName);
        assertTrue(Files.exists(langPath));

        try (FileReader reader = new FileReader(langPath.toFile())) {
            JsonObject lang = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(lang.has("generator.sandstorm.desert_planet"));
            assertTrue(lang.has("generator.sandstorm.desert_planet.description"));
            assertEquals("Sandstorm", lang.get("generator.sandstorm.desert_planet").getAsString());
            assertFalse(lang.get("generator.sandstorm.desert_planet.description").getAsString().isBlank());
        }
    }
}
