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
        Path dataDir = Paths.get("src", "main", "resources", "data", "sandstorm", "worldgen");
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
                assertFalse(
                        jsonObject.has("config"),
                        String.format("File %s uses outdated 'config' wrapper! The properties should be placed directly at the root of the JSON for Minecraft 1.18+.", jsonPath)
                );

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
                            assertFalse(stateObj.has("Name"), "Target state object must not specify 'Name' in " + jsonPath);
                            assertTrue(stateObj.get("id").getAsString().contains(":"), "Target state id must include namespace in " + jsonPath);
                        } else if (stateElem.isJsonPrimitive()) {
                            assertTrue(stateElem.getAsString().contains(":"), "Target state string must include namespace in " + jsonPath);
                        } else {
                            fail("Invalid state format in " + jsonPath);
                        }

                        assertTrue(targetObj.has("target"), "Target must specify target predicate in " + jsonPath);
                        JsonObject predicateObj = targetObj.getAsJsonObject("target");
                        assertTrue(predicateObj.has("predicate_type"), "Predicate must specify predicate_type in " + jsonPath);
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
}


