package com.fhfelipefh.sandstorm.content.world;

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
    public void testConfiguredFeaturesDoNotUseOldConfigWrapper() throws IOException {
        Path dataDir = Paths.get("src", "main", "resources", "data", "sandstorm", "worldgen");
        if (!Files.exists(dataDir)) {
            return; // Skip if directory doesn't exist
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
                
                // In Minecraft 1.18+, the "config" wrapper was removed for configured features and placed features.
                // It should be flattened into the root of the JSON object.
                assertFalse(
                        jsonObject.has("config"),
                        String.format("File %s uses outdated 'config' wrapper! The properties should be placed directly at the root of the JSON for Minecraft 1.18+.", jsonPath)
                );
            }
        } catch (Exception e) {
            fail("Failed to parse JSON file: " + jsonPath + " - " + e.getMessage());
        }
    }
}
