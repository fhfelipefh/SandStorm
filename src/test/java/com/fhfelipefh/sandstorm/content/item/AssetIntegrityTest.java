package com.fhfelipefh.sandstorm.content.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetIntegrityTest {

    private static final Path ASSETS_ROOT = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path MODELS_ITEM_DIR = ASSETS_ROOT.resolve("models").resolve("item");
    private static final Path TEXTURES_DIR = ASSETS_ROOT.resolve("textures");
    private static final Path BLOCKBENCH_DIR = Path.of("models", "blockbench");

    private static final byte[] PNG_HEADER = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    @Test
    void shouldHaveValidJsonAndReferencedPngsForItemModels() throws IOException {
        assertTrue(Files.exists(MODELS_ITEM_DIR));

        try (Stream<Path> modelFiles = Files.list(MODELS_ITEM_DIR)) {
            modelFiles.filter(p -> p.toString().endsWith(".json")).forEach(modelPath -> {
                try (FileReader reader = new FileReader(modelPath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    assertTrue(parsed.isJsonObject());
                    JsonObject jsonObject = parsed.getAsJsonObject();

                    if (jsonObject.has("textures")) {
                        JsonObject textures = jsonObject.getAsJsonObject("textures");
                        for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                            String textureRef = entry.getValue().getAsString();
                            if (textureRef.startsWith("sandstorm:")) {
                                String relativePath = textureRef.substring("sandstorm:".length()) + ".png";
                                Path textureFile = TEXTURES_DIR.resolve(relativePath);
                                assertTrue(Files.exists(textureFile));

                                byte[] header = new byte[8];
                                try (FileInputStream fis = new FileInputStream(textureFile.toFile())) {
                                    int read = fis.read(header);
                                    assertTrue(read >= 8);
                                    assertArrayEquals(PNG_HEADER, header);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    void shouldHaveValidBlockbenchModelForRawSilicon() throws IOException {
        Path bbmodelPath = BLOCKBENCH_DIR.resolve("raw_silicon.bbmodel");
        assertTrue(Files.exists(bbmodelPath));

        try (FileReader reader = new FileReader(bbmodelPath.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            assertTrue(parsed.isJsonObject());
            JsonObject json = parsed.getAsJsonObject();
            assertTrue(json.has("meta"));
            assertNotNull(json.getAsJsonObject("meta").get("format_version"));
        }
    }

    @Test
    void shouldHaveVisualAssetsForCoreSiliconAndCircuitBoard() {
        Path siliconModel = MODELS_ITEM_DIR.resolve("raw_silicon.json");
        Path siliconTexture = TEXTURES_DIR.resolve("item").resolve("raw_silicon.png");
        Path circuitModel = MODELS_ITEM_DIR.resolve("circuit_board.json");
        Path circuitTexture = TEXTURES_DIR.resolve("item").resolve("circuit_board.png");

        assertTrue(Files.exists(siliconModel));
        assertTrue(Files.exists(siliconTexture));
        assertTrue(Files.exists(circuitModel));
        assertTrue(Files.exists(circuitTexture));
    }
}
