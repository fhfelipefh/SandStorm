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
                    fail(String.format("File %s is located in legacy 'configured_feature' directory, which is not loaded in Minecraft 26.3! Use 'worldgen/feature' instead.", jsonPath));
                }

                if (jsonPath.toString().contains("worldgen") && jsonPath.toString().contains("feature") && !jsonPath.toString().contains("placed_feature")) {
                    assertTrue(
                            !jsonObject.has("config"),
                            String.format("File %s must NOT have a 'config' wrapper in Minecraft 26.3. Properties must be at the root.", jsonPath)
                    );
                }

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
                            assertTrue(stateObj.has("id"), "Target state must specify 'id' in " + jsonPath);
                            assertTrue(!stateObj.has("Name"), "Target state must not use legacy 'Name' in " + jsonPath);
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


}


