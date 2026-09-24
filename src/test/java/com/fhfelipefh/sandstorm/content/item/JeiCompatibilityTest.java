package com.fhfelipefh.sandstorm.content.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JeiCompatibilityTest {

    private static final Path RECIPES_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9_.-]+$");

    @Test
    void shouldHaveRecipeDirectory() {
        assertTrue(Files.exists(RECIPES_DIR));
    }

    @Test
    void allRecipesMustBeValidJsonAndFollowMinecraftStandards() throws IOException {
        try (Stream<Path> files = Files.list(RECIPES_DIR)) {
            files.filter(p -> p.toString().endsWith(".json")).forEach(recipePath -> {
                try (FileReader reader = new FileReader(recipePath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    assertTrue(parsed.isJsonObject(), "Recipe must be a JSON object: " + recipePath);
                    JsonObject json = parsed.getAsJsonObject();

                    assertTrue(json.has("type"), "Recipe must define type: " + recipePath);
                    String type = json.get("type").getAsString();
                    assertTrue(IDENTIFIER_PATTERN.matcher(type).matches(), "Invalid type identifier: " + type);

                    assertTrue(json.has("result"), "Recipe must define result: " + recipePath);
                    JsonObject result = json.getAsJsonObject("result");
                    assertTrue(result.has("id"), "Result must specify id: " + recipePath);
                    String resultId = result.get("id").getAsString();
                    assertTrue(IDENTIFIER_PATTERN.matcher(resultId).matches(), "Invalid result id: " + resultId);

                    if (type.equals("minecraft:crafting_shaped")) {
                        validateShapedRecipe(json, recipePath);
                    } else if (type.equals("minecraft:crafting_shapeless")) {
                        validateShapelessRecipe(json, recipePath);
                    } else if (type.equals("minecraft:smelting") || type.equals("minecraft:blasting")) {
                        validateSmeltingRecipe(json, recipePath);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void validateShapedRecipe(JsonObject json, Path path) {
        assertTrue(json.has("pattern"), "Shaped recipe must have pattern: " + path);
        JsonArray pattern = json.getAsJsonArray("pattern");
        assertTrue(pattern.size() > 0 && pattern.size() <= 3, "Pattern height must be 1-3: " + path);

        assertTrue(json.has("key"), "Shaped recipe must have key map: " + path);
        JsonObject keyMap = json.getAsJsonObject("key");

        for (Map.Entry<String, JsonElement> entry : keyMap.entrySet()) {
            String symbol = entry.getKey();
            assertEqualsOrSingleChar(symbol);
            String itemId = entry.getValue().getAsString();
            assertTrue(IDENTIFIER_PATTERN.matcher(itemId).matches(), "Invalid key item identifier: " + itemId);
        }

        for (JsonElement row : pattern) {
            String rowStr = row.getAsString();
            assertTrue(rowStr.length() > 0 && rowStr.length() <= 3, "Pattern width must be 1-3: " + path);
            for (char ch : rowStr.toCharArray()) {
                if (ch != ' ') {
                    assertTrue(keyMap.has(String.valueOf(ch)), "Pattern references undeclared key '" + ch + "' in: " + path);
                }
            }
        }
    }

    private void assertEqualsOrSingleChar(String symbol) {
        assertTrue(symbol.length() == 1, "Key symbol must be single character: " + symbol);
    }

    private void validateShapelessRecipe(JsonObject json, Path path) {
        assertTrue(json.has("ingredients"), "Shapeless recipe must have ingredients: " + path);
        JsonArray ingredients = json.getAsJsonArray("ingredients");
        assertTrue(ingredients.size() > 0 && ingredients.size() <= 9, "Ingredients count must be 1-9: " + path);
        for (JsonElement ing : ingredients) {
            String itemId = ing.getAsString();
            assertTrue(IDENTIFIER_PATTERN.matcher(itemId).matches(), "Invalid ingredient identifier: " + itemId);
        }
    }

    private void validateSmeltingRecipe(JsonObject json, Path path) {
        assertTrue(json.has("ingredient"), "Smelting recipe must have ingredient: " + path);
        String ingredient = json.get("ingredient").getAsString();
        assertTrue(IDENTIFIER_PATTERN.matcher(ingredient).matches(), "Invalid smelting ingredient: " + ingredient);
        assertTrue(json.has("cookingtime"), "Smelting recipe must have cookingtime: " + path);
        assertTrue(json.get("cookingtime").getAsInt() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "sandstorm:raw_silicon",
            "sandstorm:tool_base",
            "sandstorm:electric_component",
            "sandstorm:silicon_pickaxe",
            "sandstorm:circuit_board",
            "sandstorm:silicon_wafer",
            "sandstorm:nano_actuator",
            "sandstorm:space_ration",
            "sandstorm:potable_water_bottle",
            "sandstorm:brackish_water_bottle",
            "sandstorm:space_suit_helmet",
            "sandstorm:space_suit_chestplate",
            "sandstorm:space_suit_leggings",
            "sandstorm:space_suit_boots",
            "sandstorm:thumper",
            "sandstorm:desalination_filter",
            "sandstorm:printer_3d",
            "sandstorm:nanite_fabricator",
            "sandstorm:atmospheric_terraformer",
            "sandstorm:sonic_cannon",
            "sandstorm:anomaly_radar",
            "sandstorm:atmospheric_analyzer"
    })
    void itemIdentifierMustBeSafeForJeiIndexing(String identifier) {
        assertNotNull(identifier);
        assertFalse(identifier.isBlank());
        assertTrue(IDENTIFIER_PATTERN.matcher(identifier).matches());
        assertTrue(identifier.startsWith("sandstorm:"));
    }
}
