package com.fhfelipefh.sandstorm.content.world;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrialChamberTechnologicalIntegrationTest {

    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    @Test
    void trialChambersMustBeEnabledInDesertBiome() throws IOException {
        Path tagPath = Path.of("src", "main", "resources", "data", "minecraft", "tags", "worldgen", "biome", "has_structure", "trial_chambers.json");
        assertTrue(Files.exists(tagPath), "trial_chambers.json must exist");
        try (FileReader reader = new FileReader(tagPath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(json.has("replace") && json.get("replace").getAsBoolean());
            assertEquals(1, json.getAsJsonArray("values").size());
            assertEquals("minecraft:desert", json.getAsJsonArray("values").get(0).getAsString());
        }
    }

    @Test
    void technologicalLangOverridesMustExist() throws IOException {
        List<String> langs = List.of("en_us.json", "pt_br.json", "es_es.json");
        for (String lang : langs) {
            Path langPath = Path.of("src", "main", "resources", "assets", "minecraft", "lang", lang);
            assertTrue(Files.exists(langPath), "Language file must exist: " + lang);
            String content = Files.readString(langPath);
            assertTrue(content.contains("item.minecraft.mace"));
            assertTrue(content.contains("item.minecraft.breeze_rod"));
            assertTrue(content.contains("item.minecraft.wind_charge"));
            assertTrue(content.contains("entity.minecraft.breeze"));
            assertTrue(content.contains("block.minecraft.trial_spawner"));
            assertTrue(content.contains("block.minecraft.vault"));
        }
    }

    @Test
    void technologicalTexturesMustBeValidPngs() throws IOException {
        List<Path> textures = List.of(
                Path.of("src", "main", "resources", "assets", "minecraft", "textures", "item", "mace.png"),
                Path.of("src", "main", "resources", "assets", "minecraft", "textures", "item", "breeze_rod.png"),
                Path.of("src", "main", "resources", "assets", "minecraft", "textures", "item", "wind_charge.png"),
                Path.of("src", "main", "resources", "assets", "minecraft", "textures", "entity", "breeze", "breeze.png"),
                Path.of("src", "main", "resources", "assets", "minecraft", "textures", "entity", "breeze", "breeze_wind.png")
        );

        for (Path p : textures) {
            assertTrue(Files.exists(p), "Texture must exist: " + p);
            byte[] bytes = Files.readAllBytes(p);
            assertTrue(bytes.length > 8, "Texture must not be empty: " + p);
            for (int i = 0; i < 8; i++) {
                assertEquals(PNG_SIGNATURE[i], bytes[i], "Invalid PNG signature in " + p);
            }
        }
    }

    @Test
    void suitGliderModuleRecipeFromActuatorMustBeValid() throws IOException {
        Path recipePath = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "suit_glider_module_from_actuator.json");
        assertTrue(Files.exists(recipePath));
        String content = Files.readString(recipePath);
        assertTrue(content.contains("minecraft:breeze_rod"));
        assertTrue(content.contains("sandstorm:suit_glider_module"));
    }

    @Test
    void showcaseMustIncludeTrialChamberEquipment() throws IOException {
        Path showcasePath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java");
        assertTrue(Files.exists(showcasePath));
        String content = Files.readString(showcasePath);
        assertTrue(content.contains("Items.MACE"));
        assertTrue(content.contains("Items.WIND_CHARGE"));
        assertTrue(content.contains("Items.BREEZE_ROD"));
        assertTrue(content.contains("Items.TRIAL_KEY"));
        assertTrue(content.contains("Items.OMINOUS_TRIAL_KEY"));
    }
}

