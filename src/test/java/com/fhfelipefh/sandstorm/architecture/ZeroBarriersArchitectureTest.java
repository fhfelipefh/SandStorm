package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

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

import static org.junit.jupiter.api.Assertions.assertTrue;

class ZeroBarriersArchitectureTest {

    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data");
    private static final Path ITEMS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
    private static final Path BLOCKS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
    private static final Path ASSETS_ITEMS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "items");
    private static final Path ASSETS_MODELS_ITEM_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item");
    private static final Path ASSETS_MODELS_BLOCK_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block");
    private static final Path ASSETS_TEXTURES_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "textures");

    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final Pattern FIELD_PATTERN = Pattern.compile("public\\s+static\\s+final\\s+([A-Za-z0-9_<>]+)\\s+([A-Z0-9_]+)\\s*=\\s*([^;]+);", Pattern.DOTALL);
    private static final Pattern STRING_PATTERN = Pattern.compile("\"([^\"]+)\"");

    @Test
    void noRecipeInAnyDataFolderMayEverOutputOrReferenceBarrier() throws IOException {
        List<String> violations = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(DATA_DIR)) {
            stream.filter(p -> p.toString().endsWith(".json") && p.getParent() != null && p.getParent().getFileName().toString().equals("recipe"))
                    .forEach(recipePath -> {
                        try (FileReader reader = new FileReader(recipePath.toFile())) {
                            JsonElement parsed = JsonParser.parseReader(reader);
                            if (!parsed.isJsonObject()) {
                                return;
                            }
                            JsonObject json = parsed.getAsJsonObject();

                            if (json.has("result")) {
                                JsonElement resElem = json.get("result");
                                String resultId = "";
                                if (resElem.isJsonObject()) {
                                    JsonObject resObj = resElem.getAsJsonObject();
                                    if (resObj.has("id")) {
                                        resultId = resObj.get("id").getAsString();
                                    }
                                } else if (resElem.isJsonPrimitive()) {
                                    resultId = resElem.getAsString();
                                }
                                if ("minecraft:barrier".equals(resultId)) {
                                    violations.add("Recipe outputs barrier: " + recipePath);
                                }
                            }

                            if (json.has("ingredients")) {
                                JsonArray ingredients = json.getAsJsonArray("ingredients");
                                for (JsonElement ing : ingredients) {
                                    checkBarrierIngredient(ing, recipePath, violations);
                                }
                            }

                            if (json.has("ingredient")) {
                                checkBarrierIngredient(json.get("ingredient"), recipePath, violations);
                            }

                            if (json.has("key")) {
                                JsonObject keys = json.getAsJsonObject("key");
                                for (Map.Entry<String, JsonElement> entry : keys.entrySet()) {
                                    checkBarrierIngredient(entry.getValue(), recipePath, violations);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        assertTrue(violations.isEmpty(), "Found recipes referencing or outputting minecraft:barrier:\n" + String.join("\n", violations));
    }

    private void checkBarrierIngredient(JsonElement element, Path recipePath, List<String> violations) {
        if (element == null) {
            return;
        }
        if (element.isJsonPrimitive() && "minecraft:barrier".equals(element.getAsString())) {
            violations.add("Recipe has barrier ingredient: " + recipePath);
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("item") && "minecraft:barrier".equals(obj.get("item").getAsString())) {
                violations.add("Recipe has barrier ingredient: " + recipePath);
            }
        }
    }

    @Test
    void everyRegisteredItemAndBlockMustHaveCompleteAssetPipelineWithoutFallbacks() throws IOException {
        Set<String> itemPaths = extractRegisteredPaths(ITEMS_JAVA);
        Set<String> blockPaths = extractRegisteredPaths(BLOCKS_JAVA);

        Set<String> allPaths = new HashSet<>(itemPaths);
        allPaths.addAll(blockPaths);

        List<String> violations = new ArrayList<>();

        for (String path : allPaths) {
            Path itemDef = ASSETS_ITEMS_DIR.resolve(path + ".json");
            if (!Files.exists(itemDef)) {
                violations.add("Missing assets/sandstorm/items/" + path + ".json for registered entry");
                continue;
            }

            try (FileReader reader = new FileReader(itemDef.toFile())) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (!parsed.isJsonObject()) {
                    violations.add("Invalid JSON in " + itemDef);
                    continue;
                }
                JsonObject root = parsed.getAsJsonObject();
                if (!root.has("model") || !root.get("model").isJsonObject()) {
                    violations.add("Missing 'model' object in " + itemDef);
                    continue;
                }
                JsonObject modelObj = root.getAsJsonObject("model");
                if (!modelObj.has("model")) {
                    violations.add("Missing 'model.model' reference in " + itemDef);
                    continue;
                }

                String modelRef = modelObj.get("model").getAsString();
                validateModelAndTextures(modelRef, itemDef, violations);
            }
        }

        assertTrue(violations.isEmpty(), "Found missing or broken assets for registered items/blocks:\n" + String.join("\n", violations));
    }

    private Set<String> extractRegisteredPaths(Path javaFile) throws IOException {
        Set<String> paths = new HashSet<>();
        String content = Files.readString(javaFile);
        Matcher matcher = FIELD_PATTERN.matcher(content);
        while (matcher.find()) {
            String fieldName = matcher.group(2);
            String body = matcher.group(3);
            if ("SANDSTORM_TAB".equals(fieldName)) {
                continue;
            }
            Matcher strMatcher = STRING_PATTERN.matcher(body);
            if (strMatcher.find()) {
                paths.add(strMatcher.group(1));
            }
        }
        return paths;
    }

    private void validateModelAndTextures(String modelRef, Path sourceFile, List<String> violations) {
        if (modelRef.startsWith("sandstorm:item/")) {
            String relative = modelRef.substring("sandstorm:item/".length()) + ".json";
            Path modelPath = ASSETS_MODELS_ITEM_DIR.resolve(relative);
            if (!Files.exists(modelPath)) {
                violations.add(sourceFile.getFileName() + " references missing item model: " + modelPath);
                return;
            }
            inspectModelFile(modelPath, violations);
        } else if (modelRef.startsWith("sandstorm:block/")) {
            String relative = modelRef.substring("sandstorm:block/".length()) + ".json";
            Path modelPath = ASSETS_MODELS_BLOCK_DIR.resolve(relative);
            if (!Files.exists(modelPath)) {
                violations.add(sourceFile.getFileName() + " references missing block model: " + modelPath);
                return;
            }
            inspectModelFile(modelPath, violations);
        }
    }

    private void inspectModelFile(Path modelPath, List<String> violations) {
        try (FileReader reader = new FileReader(modelPath.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (!parsed.isJsonObject()) {
                violations.add("Invalid JSON in model: " + modelPath);
                return;
            }
            JsonObject modelObj = parsed.getAsJsonObject();

            if (modelObj.has("parent")) {
                String parent = modelObj.get("parent").getAsString();
                if (parent.startsWith("sandstorm:block/")) {
                    String blockRel = parent.substring("sandstorm:block/".length()) + ".json";
                    Path blockModelPath = ASSETS_MODELS_BLOCK_DIR.resolve(blockRel);
                    if (!Files.exists(blockModelPath)) {
                        violations.add(modelPath.getFileName() + " references missing parent block model: " + blockModelPath);
                    }
                }
            }

            if (modelObj.has("textures")) {
                JsonObject textures = modelObj.getAsJsonObject("textures");
                for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                    String texRef = entry.getValue().getAsString();
                    if (texRef.startsWith("sandstorm:")) {
                        String relPng = texRef.substring("sandstorm:".length()) + ".png";
                        Path pngPath = ASSETS_TEXTURES_DIR.resolve(relPng);
                        validatePngFile(pngPath, modelPath, violations);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validatePngFile(Path pngPath, Path modelSource, List<String> violations) {
        if (!Files.exists(pngPath)) {
            violations.add(modelSource.getFileName() + " references missing texture: " + pngPath);
            return;
        }

        try {
            long size = Files.size(pngPath);
            if (size < 67) {
                violations.add("Texture is too small or 0-byte: " + pngPath + " (" + size + " bytes)");
                return;
            }

            byte[] header = new byte[8];
            try (FileInputStream in = new FileInputStream(pngPath.toFile())) {
                int read = in.read(header);
                if (read < 8 || !Arrays.equals(header, PNG_SIGNATURE)) {
                    violations.add("Texture has corrupted PNG signature: " + pngPath);
                }
            }
        } catch (IOException e) {
            violations.add("IO error reading texture: " + pngPath + " (" + e.getMessage() + ")");
        }
    }
}
