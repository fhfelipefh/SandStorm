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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipePatternValidationArchitectureTest {

    private static final Path RECIPES_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");

    @Test
    void allShapedRecipesMustHaveExactKeyAndPatternCorrespondence() throws IOException {
        assertTrue(Files.exists(RECIPES_DIR), "Recipes directory must exist");

        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(RECIPES_DIR)) {
            stream.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try (FileReader reader = new FileReader(path.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (!parsed.isJsonObject()) {
                        return;
                    }
                    JsonObject obj = parsed.getAsJsonObject();
                    if (!obj.has("type") || !"minecraft:crafting_shaped".equals(obj.get("type").getAsString())) {
                        return;
                    }

                    JsonObject keyObj = obj.has("key") && obj.get("key").isJsonObject()
                            ? obj.getAsJsonObject("key")
                            : new JsonObject();
                    Set<String> definedKeys = new HashSet<>(keyObj.keySet());

                    Set<String> usedSymbols = new HashSet<>();
                    if (obj.has("pattern") && obj.get("pattern").isJsonArray()) {
                        JsonArray patternArr = obj.getAsJsonArray("pattern");
                        for (JsonElement rowEl : patternArr) {
                            String row = rowEl.getAsString();
                            for (char c : row.toCharArray()) {
                                if (c != ' ') {
                                    usedSymbols.add(String.valueOf(c));
                                }
                            }
                        }
                    }

                    Set<String> unusedKeys = new HashSet<>(definedKeys);
                    unusedKeys.removeAll(usedSymbols);
                    if (!unusedKeys.isEmpty()) {
                        violations.add(path.getFileName() + " defines keys unused in pattern: " + unusedKeys);
                    }

                    Set<String> missingKeys = new HashSet<>(usedSymbols);
                    missingKeys.removeAll(definedKeys);
                    if (!missingKeys.isEmpty()) {
                        violations.add(path.getFileName() + " uses symbols in pattern missing in key map: " + missingKeys);
                    }
                } catch (Exception e) {
                    violations.add(path.getFileName() + " failed to parse: " + e.getMessage());
                }
            });
        }

        assertTrue(violations.isEmpty(), "Shaped recipe pattern/key mismatches found:\n" + String.join("\n", violations));
    }
}

