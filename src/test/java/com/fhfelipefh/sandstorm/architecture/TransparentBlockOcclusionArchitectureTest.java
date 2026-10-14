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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransparentBlockOcclusionArchitectureTest {

    private static final Path BLOCK_MODELS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block");
    private static final Path BLOCKSTATES_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "blockstates");
    private static final Path BLOCKS_JAVA_FILE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");

    private static final Set<String> VANILLA_FULL_CUBE_PARENTS = Set.of(
            "minecraft:block/cube",
            "minecraft:block/cube_all",
            "minecraft:block/cube_column",
            "minecraft:block/cube_bottom_top",
            "minecraft:block/orientable",
            "minecraft:block/orientable_with_bottom"
    );

    private static final Pattern BLOCK_REGISTRATION_PATTERN = Pattern.compile(
            "public\\s+static\\s+final\\s+(\\w+)\\s+(\\w+)\\s*=\\s*register\\(\\s*\"([^\"]+)\",\\s*(.+?)\\);",
            Pattern.DOTALL
    );

    @Test
    void transparentAndCutoutBlockModelsMustNotOccludeNeighbors() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(BLOCK_MODELS_DIR) || !Files.exists(BLOCKS_JAVA_FILE)) {
            return;
        }

        String blocksCode = Files.readString(BLOCKS_JAVA_FILE);

        try (Stream<Path> paths = Files.walk(BLOCK_MODELS_DIR)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    checkBlockOcclusion(path, blocksCode, violations);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found transparent/cutout blocks that occlude neighbor faces (causing X-Ray):\n" + String.join("\n", violations));
    }

    @Test
    void allNonFullCubeAndComplexModelBlocksMustHaveNoOcclusion() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(BLOCKSTATES_DIR) || !Files.exists(BLOCKS_JAVA_FILE)) {
            return;
        }

        String blocksCode = Files.readString(BLOCKS_JAVA_FILE);
        Map<String, String> registeredBlocks = parseRegisteredBlocks(blocksCode);

        for (Map.Entry<String, String> entry : registeredBlocks.entrySet()) {
            String blockId = entry.getKey();
            String blockDef = entry.getValue();
            boolean hasNoOcclusion = blockDef.contains("noOcclusion()");

            Path blockstatePath = BLOCKSTATES_DIR.resolve(blockId + ".json");
            List<String> models = new ArrayList<>();
            if (Files.exists(blockstatePath)) {
                models.addAll(extractModelsFromBlockstate(blockstatePath));
            } else {
                models.add("sandstorm:block/" + blockId);
            }

            for (String modelRef : models) {
                if (!isFullOpaqueCube(modelRef) && !hasNoOcclusion) {
                    violations.add("Block '" + blockId + "' uses non-full cube model '" + modelRef + "' but lacks .noOcclusion() in SandStormBlocks.java (causes X-Ray culling bugs)");
                    break;
                }
            }
        }

        assertTrue(violations.isEmpty(), "Found blocks with non-full-cube models missing .noOcclusion():\n" + String.join("\n", violations));
    }

    @Test
    void nonCubeBlockClassesMustHaveNoOcclusion() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(BLOCKS_JAVA_FILE)) {
            return;
        }

        String blocksCode = Files.readString(BLOCKS_JAVA_FILE);
        Matcher matcher = BLOCK_REGISTRATION_PATTERN.matcher(blocksCode);

        Set<String> nonCubeKeywords = Set.of(
                "StairBlock", "SlabBlock", "WallBlock", "FenceBlock", "FenceGateBlock",
                "Spike", "Plant", "Cactus", "Pipe", "Hose", "Cable", "Rope", "Anchor", "Door", "Window", "Pane", "Cluster"
        );

        while (matcher.find()) {
            String className = matcher.group(1);
            String blockId = matcher.group(3);
            String blockDef = matcher.group(4);

            boolean isNonCubeClass = false;
            for (String keyword : nonCubeKeywords) {
                if (className.contains(keyword)) {
                    isNonCubeClass = true;
                    break;
                }
            }

            if (isNonCubeClass && !blockDef.contains("noOcclusion()")) {
                violations.add("Block '" + blockId + "' of class '" + className + "' represents non-cube geometry but is missing .noOcclusion()");
            }
        }

        assertTrue(violations.isEmpty(), "Found non-cube block classes without .noOcclusion():\n" + String.join("\n", violations));
    }

    private void checkBlockOcclusion(Path modelPath, String blocksCode, List<String> violations) throws IOException {
        try (FileReader reader = new FileReader(modelPath.toFile())) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return;
            }
            JsonObject model = element.getAsJsonObject();
            boolean isTranslucent = model.has("render_type") && "minecraft:translucent".equals(model.get("render_type").getAsString());
            boolean isCutout = model.has("render_type") && "minecraft:cutout".equals(model.get("render_type").getAsString());
            boolean hasGlassTexture = model.toString().contains("glass");

            if (isTranslucent || isCutout || hasGlassTexture) {
                String fileName = modelPath.getFileName().toString();
                String blockName = fileName.substring(0, fileName.lastIndexOf('.'));

                Pattern blockPattern = Pattern.compile("register\\s*\\(\\s*\"" + Pattern.quote(blockName) + "\"\\s*,\\s*new\\s+\\w+\\s*\\(([^;]+?)\\)\\s*\\);", Pattern.DOTALL);
                Matcher matcher = blockPattern.matcher(blocksCode);

                if (matcher.find()) {
                    String blockPropsDef = matcher.group(1);
                    if (!blockPropsDef.contains("noOcclusion()")) {
                        violations.add("Block '" + blockName + "' is defined with translucent/cutout/glass properties in " + modelPath + " but is missing .noOcclusion() in SandStormBlocks.java");
                    }
                }
            }
        }
    }

    private Map<String, String> parseRegisteredBlocks(String blocksCode) {
        Map<String, String> map = new HashMap<>();
        Matcher matcher = BLOCK_REGISTRATION_PATTERN.matcher(blocksCode);
        while (matcher.find()) {
            map.put(matcher.group(3), matcher.group(4));
        }
        return map;
    }

    private Set<String> extractModelsFromBlockstate(Path path) {
        Set<String> set = new HashSet<>();
        try (FileReader reader = new FileReader(path.toFile())) {
            JsonElement el = JsonParser.parseReader(reader);
            if (!el.isJsonObject()) {
                return set;
            }
            JsonObject obj = el.getAsJsonObject();
            if (obj.has("variants") && obj.get("variants").isJsonObject()) {
                JsonObject variants = obj.getAsJsonObject("variants");
                for (String key : variants.keySet()) {
                    JsonElement v = variants.get(key);
                    collectModels(v, set);
                }
            }
            if (obj.has("multipart") && obj.get("multipart").isJsonArray()) {
                JsonArray parts = obj.getAsJsonArray("multipart");
                for (JsonElement part : parts) {
                    if (part.isJsonObject() && part.getAsJsonObject().has("apply")) {
                        collectModels(part.getAsJsonObject().get("apply"), set);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return set;
    }

    private void collectModels(JsonElement el, Set<String> set) {
        if (el.isJsonObject()) {
            JsonObject o = el.getAsJsonObject();
            if (o.has("model")) {
                set.add(o.get("model").getAsString());
            }
        } else if (el.isJsonArray()) {
            for (JsonElement item : el.getAsJsonArray()) {
                collectModels(item, set);
            }
        }
    }

    private boolean isFullOpaqueCube(String modelRef) {
        if (modelRef.startsWith("minecraft:")) {
            return VANILLA_FULL_CUBE_PARENTS.contains(modelRef);
        }

        String localName = modelRef.replace("sandstorm:block/", "");
        if (!localName.endsWith(".json")) {
            localName += ".json";
        }

        Path modelFile = BLOCK_MODELS_DIR.resolve(localName);
        if (!Files.exists(modelFile)) {
            return false;
        }

        try (FileReader reader = new FileReader(modelFile.toFile())) {
            JsonElement el = JsonParser.parseReader(reader);
            if (!el.isJsonObject()) {
                return false;
            }
            JsonObject model = el.getAsJsonObject();
            if (model.has("render_type")) {
                String rt = model.get("render_type").getAsString();
                if ("minecraft:cutout".equals(rt) || "minecraft:translucent".equals(rt)) {
                    return false;
                }
            }

            if (model.has("parent")) {
                String parent = model.get("parent").getAsString();
                if (VANILLA_FULL_CUBE_PARENTS.contains(parent)) {
                    return true;
                }
                if (parent.startsWith("sandstorm:block/")) {
                    return isFullOpaqueCube(parent);
                }
                return false;
            }

            if (model.has("elements")) {
                JsonArray elements = model.getAsJsonArray("elements");
                if (elements.size() == 1 && elements.get(0).isJsonObject()) {
                    JsonObject first = elements.get(0).getAsJsonObject();
                    if (first.has("from") && first.has("to")) {
                        JsonArray from = first.getAsJsonArray("from");
                        JsonArray to = first.getAsJsonArray("to");
                        boolean isZero = from.get(0).getAsInt() == 0 && from.get(1).getAsInt() == 0 && from.get(2).getAsInt() == 0;
                        boolean isSixteen = to.get(0).getAsInt() == 16 && to.get(1).getAsInt() == 16 && to.get(2).getAsInt() == 16;
                        return isZero && isSixteen;
                    }
                }
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }

        return false;
    }
}
