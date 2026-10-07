package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextureAndModelReferenceArchitectureTest {
    private static final Path ASSETS_ROOT = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path MODELS_ROOT = ASSETS_ROOT.resolve("models");
    private static final Path TEXTURES_ROOT = ASSETS_ROOT.resolve("textures");
    private static final Pattern CLIENT_TEXTURE_PATTERN = Pattern.compile("SandStormMod\\.id\\(\\\"textures/([^\\\"]+\\.png)\\\"\\)");


    @Test
    void everyClientRendererTextureReferenceMustResolveToReadablePng() throws IOException {
        List<Path> clientSources = collectFiles(Path.of("src", "client", "java"), ".java");
        assertFalse(clientSources.isEmpty());
        for (Path source : clientSources) {
            String content = Files.readString(source);
            assertTextureReferencesResolve(source, content, CLIENT_TEXTURE_PATTERN);
        }
    }

    @Test
    void everyJsonTextureReferenceMustResolveToReadablePng() throws IOException {
        for (Path jsonPath : collectFiles(ASSETS_ROOT, ".json")) {
            JsonElement root = JsonParser.parseString(Files.readString(jsonPath));
            collectJsonTextureReferences(jsonPath, root, "");
        }
    }

    @Test
    void everySandstormModelReferenceMustResolve() throws IOException {
        for (Path jsonPath : collectFiles(ASSETS_ROOT, ".json")) {
            JsonElement root = JsonParser.parseString(Files.readString(jsonPath));
            collectJsonModelReferences(jsonPath, root);
        }
    }

    private void assertTextureReferencesResolve(Path source, String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            Path texture = TEXTURES_ROOT.resolve(matcher.group(1));
            assertReadablePng(texture, source.toString());
        }
    }

    private void collectJsonTextureReferences(Path jsonPath, JsonElement element, String key) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                collectJsonTextureReferences(jsonPath, entry.getValue(), entry.getKey());
            }
            return;
        }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collectJsonTextureReferences(jsonPath, child, key);
            }
            return;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            return;
        }
        String value = element.getAsString();
        if (isTextureKey(key) && value.startsWith("sandstorm:")) {
            String texturePath = value.substring("sandstorm:".length());
            Path directTexture = TEXTURES_ROOT.resolve(texturePath + ".png");
            if (Files.exists(directTexture)) {
                assertReadablePng(directTexture, jsonPath.toString());
            } else if (jsonPath.normalize().toString().contains("assets" + File.separator + "sandstorm" + File.separator + "equipment")) {
                assertEquipmentTextureResolves(texturePath, jsonPath);
            } else {
                assertReadablePng(directTexture, jsonPath.toString());
            }
        }
    }

    private void collectJsonModelReferences(Path jsonPath, JsonElement element) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                if (isModelKey(entry.getKey())) {
                    assertModelReference(jsonPath, entry.getValue());
                }
                collectJsonModelReferences(jsonPath, entry.getValue());
            }
            return;
        }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collectJsonModelReferences(jsonPath, child);
            }
        }
    }

    private void assertModelReference(Path jsonPath, JsonElement element) {
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            return;
        }
        String value = element.getAsString();
        if (value.startsWith("sandstorm:block/") || value.startsWith("sandstorm:item/")) {
            String modelPath = value.substring("sandstorm:".length());
            Path model = MODELS_ROOT.resolve(modelPath + ".json");
            assertTrue(Files.exists(model), "Referenced model must exist: " + model + " from " + jsonPath);
        }
    }

    private boolean isModelKey(String key) {
        return key.equals("parent") || key.equals("model");
    }

    private void assertEquipmentTextureResolves(String texturePath, Path source) {
        String fileName = Path.of(texturePath).getFileName().toString() + ".png";
        try (var stream = Files.walk(TEXTURES_ROOT)) {
            boolean found = stream.filter(Files::isRegularFile)
                    .anyMatch(path -> path.getFileName().toString().equals(fileName));
            assertTrue(found, "Equipment texture must exist somewhere under textures: " + fileName + " from " + source);
        } catch (IOException exception) {
            throw new AssertionError("Could not scan equipment textures for " + fileName, exception);
        }
    }

    private boolean isTextureKey(String key) {
        return key.equals("texture") || key.equals("layer0") || key.equals("layer1") || key.equals("layer2") || key.equals("particle");
    }

    private void assertReadablePng(Path texture, String source) {
        assertTrue(Files.exists(texture), "Referenced texture must exist: " + texture + " from " + source);
        try {
            BufferedImage image = ImageIO.read(texture.toFile());
            assertNotNull(image, "Referenced texture must be a readable PNG: " + texture + " from " + source);
            assertTrue(image.getWidth() > 0 && image.getHeight() > 0, "Referenced texture must have dimensions: " + texture);
        } catch (IOException exception) {
            throw new AssertionError("Could not read referenced texture: " + texture + " from " + source, exception);
        }
    }

    private List<Path> collectFiles(Path root, String suffix) throws IOException {
        List<Path> files = new ArrayList<>();
        try (var stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(suffix)).forEach(files::add);
        }
        return files;
    }
}
