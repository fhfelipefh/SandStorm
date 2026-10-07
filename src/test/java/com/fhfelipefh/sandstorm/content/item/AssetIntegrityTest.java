package com.fhfelipefh.sandstorm.content.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetIntegrityTest {

    private static final Path ASSETS_ROOT = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path BLOCKSTATES_DIR = ASSETS_ROOT.resolve("blockstates");
    private static final Path MODELS_BLOCK_DIR = ASSETS_ROOT.resolve("models").resolve("block");
    private static final Path MODELS_ITEM_DIR = ASSETS_ROOT.resolve("models").resolve("item");
    private static final Path ITEMS_DIR = ASSETS_ROOT.resolve("items");
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
                    if (root.has("variants")) {
                        JsonObject variants = root.getAsJsonObject("variants");
                        for (Map.Entry<String, JsonElement> variant : variants.entrySet()) {
                            assertReferencedBlockModel(variant.getValue().getAsJsonObject(), variant.getKey());
                        }
                    } else {
                        assertTrue(root.has("multipart"), "Blockstate must have variants or multipart: " + statePath);
                        for (JsonElement part : root.getAsJsonArray("multipart")) {
                            assertReferencedBlockModel(part.getAsJsonObject().getAsJsonObject("apply"), "multipart");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void assertReferencedBlockModel(JsonObject modelObject, String variantName) {
        assertTrue(modelObject.has("model"), "Variant must specify model: " + variantName);
        String modelRef = modelObject.get("model").getAsString();
        assertTrue(modelRef.startsWith("sandstorm:block/"), "Model must reference sandstorm:block/: " + modelRef);
        String modelFile = modelRef.substring("sandstorm:block/".length()) + ".json";
        Path resolvedModel = MODELS_BLOCK_DIR.resolve(modelFile);
        assertTrue(Files.exists(resolvedModel), "Referenced block model must exist on disk: " + resolvedModel);
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

    @Test
    void shouldHaveValidItemAssetDefinitions() throws IOException {
        assertTrue(Files.exists(ITEMS_DIR));

        try (Stream<Path> itemFiles = Files.list(ITEMS_DIR)) {
            itemFiles.filter(p -> p.toString().endsWith(".json")).forEach(itemPath -> {
                try (FileReader reader = new FileReader(itemPath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    assertTrue(parsed.isJsonObject(), "Item definition must be a JsonObject: " + itemPath);
                    JsonObject root = parsed.getAsJsonObject();
                    assertTrue(root.has("model"), "Item definition must have model property: " + itemPath);
                    JsonObject modelObj = root.getAsJsonObject("model");
                    assertTrue(modelObj.has("type"), "Model must specify type: " + itemPath);
                    assertEquals("minecraft:model", modelObj.get("type").getAsString());
                    assertTrue(modelObj.has("model"), "Model must specify target model: " + itemPath);
                    String modelRef = modelObj.get("model").getAsString();
                    assertTrue(modelRef.startsWith("sandstorm:item/"));
                    String modelFile = modelRef.substring("sandstorm:item/".length()) + ".json";
                    Path resolvedModel = MODELS_ITEM_DIR.resolve(modelFile);
                    assertTrue(Files.exists(resolvedModel), "Referenced item model must exist on disk: " + resolvedModel);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    void everyItemDefinitionMustHaveACorrespondingItemTexture() throws IOException {
        assertTrue(Files.exists(ITEMS_DIR));

        Path itemTexturesDir = TEXTURES_DIR.resolve("item");
        Path blockTexturesDir = TEXTURES_DIR.resolve("block");
        assertTrue(Files.exists(itemTexturesDir));

        try (Stream<Path> itemFiles = Files.list(ITEMS_DIR)) {
            itemFiles.filter(p -> p.toString().endsWith(".json")).forEach(itemPath -> {
                String itemName = itemPath.getFileName().toString().replace(".json", "");
                Path modelPath = MODELS_ITEM_DIR.resolve(itemName + ".json");
                assertTrue(Files.exists(modelPath),
                        "Item '" + itemName + "' must have models/item/" + itemName + ".json (would show as barrier in JEI)");

                try (FileReader reader = new FileReader(modelPath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    assertTrue(parsed.isJsonObject());
                    JsonObject json = parsed.getAsJsonObject();

                    boolean hasResolvedTexture = false;

                    if (json.has("textures")) {
                        JsonObject textures = json.getAsJsonObject("textures");
                        String texRef = null;
                        if (textures.has("layer0")) {
                            texRef = textures.get("layer0").getAsString();
                        } else if (textures.has("texture")) {
                            texRef = textures.get("texture").getAsString();
                        }
                        if (texRef != null) {
                            Path texFile = null;
                            if (texRef.startsWith("sandstorm:item/")) {
                                texFile = itemTexturesDir.resolve(texRef.substring("sandstorm:item/".length()) + ".png");
                            } else if (texRef.startsWith("sandstorm:block/")) {
                                texFile = blockTexturesDir.resolve(texRef.substring("sandstorm:block/".length()) + ".png");
                            }
                            if (texFile != null) {
                                assertTrue(Files.exists(texFile),
                                        "Item '" + itemName + "' -> textura ausente: " + texFile + " (icone de barreira no JEI)");
                                byte[] header = new byte[8];
                                try (FileInputStream fis = new FileInputStream(texFile.toFile())) {
                                    assertEquals(8, fis.read(header));
                                    assertArrayEquals(PNG_HEADER, header,
                                            "Textura de '" + itemName + "' tem cabecalho PNG invalido (arquivo corrompido)");
                                }
                                hasResolvedTexture = true;
                            }
                        }
                    }

                    boolean hasInlineElements = json.has("elements");
                    String parent = json.has("parent") ? json.get("parent").getAsString() : "";
                    boolean usesBlockParent = parent.startsWith("sandstorm:block/");
                    boolean usesVanillaParent = parent.startsWith("minecraft:item/") || parent.startsWith("minecraft:block/");

                    assertTrue(hasResolvedTexture || usesBlockParent || usesVanillaParent || hasInlineElements,
                            "Item '" + itemName + "' sem textura resolvida, sem parent valido e sem elementos 3D (icone de barreira no JEI)");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    void generatedItemTexturesMustHaveTransparency() throws IOException {
        assertTrue(Files.exists(MODELS_ITEM_DIR));

        try (Stream<Path> modelFiles = Files.list(MODELS_ITEM_DIR)) {
            modelFiles.filter(p -> p.toString().endsWith(".json")).forEach(modelPath -> {
                try (FileReader reader = new FileReader(modelPath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (!parsed.isJsonObject()) {
                        return;
                    }
                    JsonObject json = parsed.getAsJsonObject();
                    String parent = json.has("parent") ? json.get("parent").getAsString() : "";
                    if (!parent.equals("minecraft:item/generated") && !parent.equals("minecraft:item/handheld")) {
                        return;
                    }

                    if (!json.has("textures")) {
                        return;
                    }
                    JsonObject textures = json.getAsJsonObject("textures");
                    if (!textures.has("layer0")) {
                        return;
                    }

                    String texRef = textures.get("layer0").getAsString();
                    Path texFile = null;
                    if (texRef.startsWith("sandstorm:item/")) {
                        texFile = TEXTURES_DIR.resolve("item").resolve(texRef.substring("sandstorm:item/".length()) + ".png");
                    } else if (texRef.startsWith("sandstorm:block/")) {
                        texFile = TEXTURES_DIR.resolve("block").resolve(texRef.substring("sandstorm:block/".length()) + ".png");
                    }

                    if (texFile != null && Files.exists(texFile)) {
                        BufferedImage img = ImageIO.read(texFile.toFile());
                        assertNotNull(img, "Failed to read image: " + texFile);
                        int total = img.getWidth() * img.getHeight();
                        int transparent = 0;
                        for (int y = 0; y < img.getHeight(); y++) {
                            for (int x = 0; x < img.getWidth(); x++) {
                                int alpha = (img.getRGB(x, y) >> 24) & 0xFF;
                                if (alpha < 128) {
                                    transparent++;
                                }
                            }
                        }
                        assertTrue(transparent >= total * 0.10,
                                "Generated 2D item model '" + modelPath.getFileName() + "' points to texture with no transparent sections: " + texRef);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
