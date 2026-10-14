package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockTextureAndResolutionArchitectureTest {

    private static final Path ASSETS_ROOT = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path BLOCKSTATES_DIR = ASSETS_ROOT.resolve("blockstates");
    private static final Path MODELS_BLOCK_DIR = ASSETS_ROOT.resolve("models").resolve("block");
    private static final Path TEXTURES_BLOCK_DIR = ASSETS_ROOT.resolve("textures").resolve("block");
    private static final Path TEXTURES_ITEM_DIR = ASSETS_ROOT.resolve("textures").resolve("item");
    private static final Path BLOCKS_JAVA_FILE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");

    private static final Pattern REGISTER_PATTERN = Pattern.compile("register\\(\"([a-z0-9_]+)\",");
    private static final byte[] PNG_HEADER = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    @Test
    void allRegisteredBlocksMustHaveCompleteBlockstateModelAndTextureChain() throws IOException {
        assertTrue(Files.exists(BLOCKS_JAVA_FILE), "SandStormBlocks.java must exist");
        String content = Files.readString(BLOCKS_JAVA_FILE);
        Matcher matcher = REGISTER_PATTERN.matcher(content);
        Set<String> registeredBlocks = new HashSet<>();
        while (matcher.find()) {
            registeredBlocks.add(matcher.group(1));
        }
        assertFalse(registeredBlocks.isEmpty(), "Must detect registered blocks in SandStormBlocks.java");

        List<String> errors = new ArrayList<>();
        for (String blockId : registeredBlocks) {
            Path blockstateFile = BLOCKSTATES_DIR.resolve(blockId + ".json");
            if (!Files.exists(blockstateFile)) {
                errors.add("Block 'sandstorm:" + blockId + "' is missing blockstate file: " + blockstateFile);
                continue;
            }
            try (FileReader reader = new FileReader(blockstateFile.toFile())) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (!parsed.isJsonObject()) {
                    errors.add("Blockstate for 'sandstorm:" + blockId + "' is not a valid JSON object");
                    continue;
                }
                JsonObject root = parsed.getAsJsonObject();
                Set<String> referencedModels = new HashSet<>();
                if (root.has("variants")) {
                    JsonObject variants = root.getAsJsonObject("variants");
                    for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                        extractModelsFromVariant(entry.getValue(), referencedModels);
                    }
                } else if (root.has("multipart")) {
                    JsonArray parts = root.getAsJsonArray("multipart");
                    for (JsonElement part : parts) {
                        if (part.isJsonObject() && part.getAsJsonObject().has("apply")) {
                            extractModelsFromVariant(part.getAsJsonObject().get("apply"), referencedModels);
                        }
                    }
                } else {
                    errors.add("Blockstate for 'sandstorm:" + blockId + "' has neither 'variants' nor 'multipart'");
                }

                for (String modelRef : referencedModels) {
                    if (modelRef.startsWith("sandstorm:block/")) {
                        String modelFile = modelRef.substring("sandstorm:block/".length()) + ".json";
                        Path modelPath = MODELS_BLOCK_DIR.resolve(modelFile);
                        if (!Files.exists(modelPath)) {
                            errors.add("Block 'sandstorm:" + blockId + "' references missing model: " + modelPath);
                        }
                    }
                }

                Path itemDef = ASSETS_ROOT.resolve("items").resolve(blockId + ".json");
                if (!Files.exists(itemDef)) {
                    errors.add("Block 'sandstorm:" + blockId + "' is missing 1.21.4 item definition: " + itemDef);
                }
            } catch (Exception ex) {
                errors.add("Error parsing blockstate for 'sandstorm:" + blockId + "': " + ex.getMessage());
            }
        }

        assertTrue(errors.isEmpty(), "Found block registration issues:\n" + String.join("\n", errors));
    }

    private void extractModelsFromVariant(JsonElement element, Set<String> models) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("model")) {
                models.add(obj.get("model").getAsString());
            }
        } else if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                extractModelsFromVariant(item, models);
            }
        }
    }

    @Test
    void allBlockTexturesMustMeetMinecraftResolutionAndProportionStandards() throws IOException {
        assertTrue(Files.exists(TEXTURES_BLOCK_DIR), "Block textures dir must exist");
        List<String> errors = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(TEXTURES_BLOCK_DIR)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".png"))
                    .forEach(path -> validateTextureStandards(path, "Block", errors));
        }
        assertTrue(errors.isEmpty(), "Found non-standard block textures:\n" + String.join("\n", errors));
    }

    @Test
    void allItemTexturesMustMeetMinecraftResolutionAndProportionStandards() throws IOException {
        assertTrue(Files.exists(TEXTURES_ITEM_DIR), "Item textures dir must exist");
        List<String> errors = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(TEXTURES_ITEM_DIR)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".png"))
                    .forEach(path -> validateTextureStandards(path, "Item", errors));
        }
        assertTrue(errors.isEmpty(), "Found non-standard item textures:\n" + String.join("\n", errors));
    }

    private void validateTextureStandards(Path path, String category, List<String> errors) {
        String filename = path.getFileName().toString();
        try {
            long size = Files.size(path);
            if (size < 8) {
                errors.add(category + " texture '" + filename + "' is empty or too small (" + size + " bytes)");
                return;
            }
            byte[] header = new byte[8];
            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                int read = fis.read(header);
                if (read != 8 || !Arrays.equals(PNG_HEADER, header)) {
                    errors.add(category + " texture '" + filename + "' has invalid PNG signature header (corrupted file)");
                    return;
                }
            }
            BufferedImage image = ImageIO.read(path.toFile());
            if (image == null) {
                errors.add(category + " texture '" + filename + "' could not be decoded as an image");
                return;
            }
            int width = image.getWidth();
            int height = image.getHeight();
            if (width < 16 || height < 16) {
                errors.add(category + " texture '" + filename + "' is below minimum resolution (found " + width + "x" + height + ", minimum is 16x16)");
                return;
            }
            boolean isPowerOfTwoWidth = (width & (width - 1)) == 0;
            if (!isPowerOfTwoWidth) {
                errors.add(category + " texture '" + filename + "' width is not a power of 2 (found " + width + "px)");
                return;
            }
            boolean isSquare = (width == height);
            boolean isVerticalStrip = (height % width == 0);
            if (!isSquare && !isVerticalStrip) {
                errors.add(category + " texture '" + filename + "' has non-standard aspect ratio (" + width + "x" + height + ", expected square or vertical animation strip)");
            }
            boolean isAllTransparent = true;
            for (int x = 0; x < width && isAllTransparent; x++) {
                for (int y = 0; y < height; y++) {
                    int alpha = (image.getRGB(x, y) >> 24) & 0xFF;
                    if (alpha > 0) {
                        isAllTransparent = false;
                        break;
                    }
                }
            }
            if (isAllTransparent) {
                errors.add(category + " texture '" + filename + "' is 100% transparent (completely invisible)");
            }
            if (hasBakedCheckerboard(image)) {
                errors.add(category + " texture '" + filename + "' contains a baked-in fake transparency checkerboard pattern");
            }
        } catch (IOException e) {
            errors.add(category + " texture '" + filename + "' failed to read: " + e.getMessage());
        }
    }

    private boolean hasBakedCheckerboard(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] blockSizes = new int[]{1, 2, 4};
        for (int blockSize : blockSizes) {
            int checkerPairs = 0;
            for (int y = 0; y < height - blockSize; y += blockSize) {
                for (int x = 0; x < width - blockSize; x += blockSize) {
                    int argb1 = image.getRGB(x, y);
                    int argb2 = image.getRGB(x + blockSize, y);
                    int argb3 = image.getRGB(x, y + blockSize);
                    int argbDiag = image.getRGB(x + blockSize, y + blockSize);

                    if (isNeutralOpaque(argb1) && isNeutralOpaque(argb2) && isNeutralOpaque(argb3) && isNeutralOpaque(argbDiag)) {
                        int r1 = (argb1 >> 16) & 0xFF;
                        int r2 = (argb2 >> 16) & 0xFF;
                        int r3 = (argb3 >> 16) & 0xFF;
                        int rDiag = (argbDiag >> 16) & 0xFF;

                        int diffH = Math.abs(r1 - r2);
                        int diffV = Math.abs(r1 - r3);
                        int diffDiag = Math.abs(r1 - rDiag);

                        if (diffH >= 20 && diffH <= 65 && diffV >= 20 && diffV <= 65 && diffDiag <= 5) {
                            checkerPairs++;
                        }
                    }
                }
            }
            if (checkerPairs >= 8) {
                return true;
            }
        }
        return false;
    }

    private boolean isNeutralOpaque(int argb) {
        int a = (argb >> 24) & 0xFF;
        if (a < 255) {
            return false;
        }
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return r >= 180 && Math.abs(r - g) <= 2 && Math.abs(g - b) <= 2;
    }
}
