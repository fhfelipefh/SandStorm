package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeConflictPreventionArchitectureTest {

    private static final Path RECIPES_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");

    @Test
    void allSandstormRecipesMustHaveUniqueInputs() throws IOException {
        assertTrue(Files.exists(RECIPES_DIR), "Recipes directory must exist");

        Map<String, List<String>> shapelessInputs = new HashMap<>();
        Map<String, List<String>> shapedInputs = new HashMap<>();
        Map<String, List<String>> smeltingInputs = new HashMap<>();
        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(RECIPES_DIR)) {
            stream.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try (FileReader reader = new FileReader(path.toFile())) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    String type = json.has("type") ? json.get("type").getAsString() : "";
                    String filename = path.getFileName().toString();

                    if ("minecraft:crafting_shapeless".equals(type) && json.has("ingredients")) {
                        JsonArray ingredients = json.getAsJsonArray("ingredients");
                        List<String> sortedIngs = new ArrayList<>();
                        for (JsonElement ing : ingredients) {
                            if (ing.isJsonPrimitive()) {
                                sortedIngs.add(ing.getAsString());
                            } else if (ing.isJsonObject() && ing.getAsJsonObject().has("item")) {
                                sortedIngs.add(ing.getAsJsonObject().get("item").getAsString());
                            }
                        }
                        Collections.sort(sortedIngs);
                        String key = String.join("+", sortedIngs);
                        shapelessInputs.computeIfAbsent(key, k -> new ArrayList<>()).add(filename);
                    } else if ("minecraft:crafting_shaped".equals(type) && json.has("pattern") && json.has("key")) {
                        JsonArray pattern = json.getAsJsonArray("pattern");
                        JsonObject keys = json.getAsJsonObject("key");
                        StringBuilder gridBuilder = new StringBuilder();
                        for (JsonElement rowElem : pattern) {
                            String row = rowElem.getAsString();
                            for (char c : row.toCharArray()) {
                                String charKey = String.valueOf(c);
                                if (keys.has(charKey)) {
                                    JsonElement val = keys.get(charKey);
                                    if (val.isJsonPrimitive()) {
                                        gridBuilder.append(val.getAsString());
                                    } else if (val.isJsonObject() && val.getAsJsonObject().has("item")) {
                                        gridBuilder.append(val.getAsJsonObject().get("item").getAsString());
                                    }
                                } else {
                                    gridBuilder.append("EMPTY");
                                }
                                gridBuilder.append(",");
                            }
                            gridBuilder.append(";");
                        }
                        String key = gridBuilder.toString();
                        shapedInputs.computeIfAbsent(key, k -> new ArrayList<>()).add(filename);
                    } else if (("minecraft:smelting".equals(type) || "minecraft:blasting".equals(type)) && json.has("ingredient")) {
                        JsonElement ing = json.get("ingredient");
                        String ingId = ing.isJsonPrimitive() ? ing.getAsString() : (ing.isJsonObject() && ing.getAsJsonObject().has("item") ? ing.getAsJsonObject().get("item").getAsString() : "");
                        String key = type + ":" + ingId;
                        smeltingInputs.computeIfAbsent(key, k -> new ArrayList<>()).add(filename);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        for (Map.Entry<String, List<String>> entry : shapelessInputs.entrySet()) {
            if (entry.getValue().size() > 1) {
                violations.add("Shapeless collision for [" + entry.getKey() + "]: " + entry.getValue());
            }
        }

        for (Map.Entry<String, List<String>> entry : shapedInputs.entrySet()) {
            if (entry.getValue().size() > 1) {
                violations.add("Shaped collision: " + entry.getValue());
            }
        }

        for (Map.Entry<String, List<String>> entry : smeltingInputs.entrySet()) {
            if (entry.getValue().size() > 1) {
                violations.add("Smelting collision for [" + entry.getKey() + "]: " + entry.getValue());
            }
        }

        assertTrue(violations.isEmpty(), "Found recipe input conflicts in data/sandstorm/recipe:\n" + String.join("\n", violations));
    }
}
