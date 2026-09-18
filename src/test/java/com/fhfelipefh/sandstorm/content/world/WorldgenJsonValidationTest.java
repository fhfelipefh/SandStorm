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
                    assertTrue(
                            jsonObject.has("config"),
                            String.format("File %s is missing the 'config' wrapper! Configured features must have properties inside 'config'.", jsonPath)
                    );
                    jsonObject = jsonObject.getAsJsonObject("config");
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
                            assertTrue(stateObj.has("Name"), "Target state must specify 'Name' in " + jsonPath);
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


