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
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelGuiDisplayArchitectureTest {

    private static final Path MODELS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models");

    @Test
    void customModelsMustExplicitlyDefineGuiDisplayRotation() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(MODELS_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(MODELS_DIR)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    checkModelFile(path, violations);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found models with missing or invalid display.gui rotation:\n" + String.join("\n", violations));
    }

    private void checkModelFile(Path path, List<String> violations) throws IOException {
        try (FileReader reader = new FileReader(path.toFile())) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return;
            }
            JsonObject model = element.getAsJsonObject();
            boolean hasCustomElements = model.has("elements");
            boolean hasBlockParent = model.has("parent") && "minecraft:block/block".equals(model.get("parent").getAsString());
            boolean hasDisplay = model.has("display");

            if (hasCustomElements || hasBlockParent || hasDisplay) {
                if (!model.has("display")) {
                    violations.add(path + " -> Missing 'display' object. Custom models must define display.gui with rotation.");
                    return;
                }
                JsonObject display = model.getAsJsonObject("display");
                if (!display.has("gui")) {
                    violations.add(path + " -> Missing 'display.gui' object. Custom models must define display.gui with rotation.");
                    return;
                }
                JsonObject gui = display.getAsJsonObject("gui");
                if (!gui.has("rotation")) {
                    violations.add(path + " -> Missing 'display.gui.rotation'. An isometric 3D rotation must always be explicitly defined.");
                    return;
                }
                JsonElement rotationElement = gui.get("rotation");
                if (!rotationElement.isJsonArray()) {
                    violations.add(path + " -> 'display.gui.rotation' must be a JSON array.");
                    return;
                }
                JsonArray rotationArray = rotationElement.getAsJsonArray();
                if (rotationArray.size() != 3) {
                    violations.add(path + " -> 'display.gui.rotation' must contain exactly 3 numeric angles [x, y, z].");
                    return;
                }
                for (int i = 0; i < 3; i++) {
                    JsonElement val = rotationArray.get(i);
                    if (!val.isJsonPrimitive() || !val.getAsJsonPrimitive().isNumber()) {
                        violations.add(path + " -> 'display.gui.rotation[" + i + "]' must be a numeric angle.");
                    }
                }
            }
        }
    }
}
