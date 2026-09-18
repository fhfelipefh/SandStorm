package com.fhfelipefh.sandstorm.content.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

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
    private static final Path BLOCKSTATES_DIR = ASSETS_ROOT.resolve("blockstates");
    private static final Path MODELS_BLOCK_DIR = ASSETS_ROOT.resolve("models").resolve("block");
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

                    if (jsonObject.has("parent")) {
                        String parent = jsonObject.get("parent").getAsString();
                        if (parent.startsWith("sandstorm:block/")) {
                            String blockModelName = parent.substring("sandstorm:block/".length()) + ".json";
                            Path blockModelFile = MODELS_BLOCK_DIR.resolve(blockModelName);
                            assertTrue(Files.exists(blockModelFile), "Referenced block model must exist: " + blockModelFile);
                        }
                    }

                    if (jsonObject.has("textures")) {
                        JsonObject textures = jsonObject.getAsJsonObject("textures");
                        for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                            String textureRef = entry.getValue().getAsString();
                            if (textureRef.startsWith("sandstorm:")) {
                                String relativePath = textureRef.substring("sandstorm:".length()) + ".png";
                                Path textureFile = TEXTURES_DIR.resolve(relativePath);
                                assertTrue(Files.exists(textureFile), "Referenced item texture must exist: " + textureFile);

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
    void shouldHaveValidBlockModelsAndTextures() throws IOException {
        assertTrue(Files.exists(MODELS_BLOCK_DIR));

        try (Stream<Path> modelFiles = Files.list(MODELS_BLOCK_DIR)) {
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
                                assertTrue(Files.exists(textureFile), "Referenced block texture must exist: " + textureFile);

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
    void shouldHaveValidBlockstatesPointingToExistingModels() throws IOException {
        assertTrue(Files.exists(BLOCKSTATES_DIR));

        try (Stream<Path> stateFiles = Files.list(BLOCKSTATES_DIR)) {
            stateFiles.filter(p -> p.toString().endsWith(".json")).forEach(statePath -> {
                try (FileReader reader = new FileReader(statePath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    assertTrue(parsed.isJsonObject());
                    JsonObject root = parsed.getAsJsonObject();
                    assertTrue(root.has("variants"), "Blockstate must have variants: " + statePath);
                    JsonObject variants = root.getAsJsonObject("variants");

                    for (Map.Entry<String, JsonElement> variant : variants.entrySet()) {
                        JsonObject variantObj = variant.getValue().getAsJsonObject();
                        assertTrue(variantObj.has("model"), "Variant must specify model: " + variant.getKey());
                        String modelRef = variantObj.get("model").getAsString();
                        assertTrue(modelRef.startsWith("sandstorm:block/"), "Model must reference sandstorm:block/: " + modelRef);
                        String modelFile = modelRef.substring("sandstorm:block/".length()) + ".json";
                        Path resolvedModel = MODELS_BLOCK_DIR.resolve(modelFile);
                        assertTrue(Files.exists(resolvedModel), "Referenced block model must exist on disk: " + resolvedModel);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    void shouldHaveValidBlockbenchModels() throws IOException {
        assertTrue(Files.exists(BLOCKBENCH_DIR));

        String[] requiredModels = {
                "raw_silicon",
                "thumper",
                "atmospheric_terraformer",
                "sandworm",
                "printer_3d",
                "desalination_filter",
                "nanite_fabricator",
                "ancient_data_core",
                "buried_tech_ruins",
                "drone_dock",
                "assembly_bay",
                "cargo_drone",
                "excavator_vehicle",
                "megazord"
        };

        for (String modelName : requiredModels) {
            Path bbmodelPath = BLOCKBENCH_DIR.resolve(modelName + ".bbmodel");
            assertTrue(Files.exists(bbmodelPath), "Blockbench model must exist: " + modelName);

            try (FileReader reader = new FileReader(bbmodelPath.toFile())) {
                JsonElement parsed = JsonParser.parseReader(reader);
                assertTrue(parsed.isJsonObject(), "Blockbench model must be valid JSON: " + modelName);
                JsonObject json = parsed.getAsJsonObject();
                assertTrue(json.has("meta"), "Blockbench model must have meta: " + modelName);
                assertNotNull(json.getAsJsonObject("meta").get("format_version"), "Format version required: " + modelName);
                assertTrue(json.has("elements"), "Blockbench model must have elements: " + modelName);
                assertTrue(json.getAsJsonArray("elements").size() > 0, "Model must have elements: " + modelName);
            }
        }
    }
}
