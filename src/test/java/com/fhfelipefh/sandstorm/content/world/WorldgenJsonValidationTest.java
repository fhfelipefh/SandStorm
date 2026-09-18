package com.fhfelipefh.sandstorm.content.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class WorldgenJsonValidationTest {

    @Test
    public void testWorldgenJsonFilesStrictSchema() throws IOException {
        Path dataDir = Paths.get("src", "main", "resources", "data", "sandstorm");
        if (!Files.exists(dataDir)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(dataDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".json"))
                 .forEach(this::validateWorldgenJson);
        }
    }

    private void validateWorldgenJson(Path jsonPath) {
        try {
            String content = Files.readString(jsonPath);
            JsonElement element = JsonParser.parseString(content);

            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                
                if (jsonPath.toString().contains("configured_feature")) {
                    assertFalse(
                            jsonObject.has("config"),
                            String.format("File %s uses outdated 'config' wrapper! The properties should be placed directly at the root of the JSON for Minecraft 1.18+.", jsonPath)
                    );
                }

                checkLegacyBlockStateName(element, jsonPath);

                if (jsonObject.has("targets")) {
                    JsonArray targets = jsonObject.getAsJsonArray("targets");
                    assertTrue(targets.size() > 0, "Targets list cannot be empty in " + jsonPath);
                    for (JsonElement targetElem : targets) {
                        assertTrue(targetElem.isJsonObject(), "Target element must be an object in " + jsonPath);
                        JsonObject targetObj = targetElem.getAsJsonObject();
                        assertTrue(targetObj.has("state"), "Target must specify state in " + jsonPath);
                        
                        JsonElement stateElem = targetObj.get("state");
                        if (stateElem.isJsonObject()) {
                            JsonObject stateObj = stateElem.getAsJsonObject();
                            assertTrue(stateObj.has("id"), "Target state must use 'id' instead of 'Name' in " + jsonPath);
                        }
                    }
                }

                if (jsonObject.has("placement")) {
                    assertTrue(jsonObject.has("feature"), "Placed feature must specify 'feature' in " + jsonPath);
                    assertTrue(jsonObject.get("feature").getAsString().startsWith("sandstorm:"), "Feature reference must start with 'sandstorm:' in " + jsonPath);
                    assertTrue(jsonObject.getAsJsonArray("placement").size() > 0, "Placement modifiers cannot be empty in " + jsonPath);
                }
            }
        } catch (Exception e) {
            fail("Failed to parse JSON file: " + jsonPath + " - " + e.getMessage());
        }
    }

    private void checkLegacyBlockStateName(JsonElement element, Path jsonPath) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("state") && obj.get("state").isJsonObject()) {
                JsonObject stateObj = obj.getAsJsonObject("state");
                assertFalse(stateObj.has("Name"), "Found legacy 'Name' key in block state definition in " + jsonPath + ". Use 'id' instead for Minecraft 1.21.2+.");
            }
            for (String key : obj.keySet()) {
                checkLegacyBlockStateName(obj.get(key), jsonPath);
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                checkLegacyBlockStateName(child, jsonPath);
            }
        }
    }
}


