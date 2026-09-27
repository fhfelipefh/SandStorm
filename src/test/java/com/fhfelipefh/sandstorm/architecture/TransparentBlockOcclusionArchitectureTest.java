package com.fhfelipefh.sandstorm.architecture;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransparentBlockOcclusionArchitectureTest {

    private static final Path BLOCK_MODELS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block");
    private static final Path BLOCKS_JAVA_FILE = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");

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
}
