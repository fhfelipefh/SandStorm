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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MineableTagCompletenessArchitectureTest {

    private static final Pattern REGISTER_PATTERN = Pattern.compile(
            "register\\(\"([a-z0-9_]+)\","
    );

    private static final Set<String> EXEMPT_BLOCKS = Set.of(
            "brackish_aquifer",
            "morphing_fluid_transition"
    );

    @Test
    void allRegisteredBlocksMustBeInMineableTags() throws IOException {
        Path blocksFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
        Path mineableDir = Path.of("src", "main", "resources", "data", "minecraft", "tags", "block", "mineable");

        assertTrue(Files.exists(blocksFile), "SandStormBlocks.java must exist");
        assertTrue(Files.exists(mineableDir), "minecraft mineable tags directory must exist");

        String blocksContent = Files.readString(blocksFile);
        Set<String> registeredBlocks = new HashSet<>();
        Matcher matcher = REGISTER_PATTERN.matcher(blocksContent);
        while (matcher.find()) {
            String blockId = matcher.group(1);
            if (!EXEMPT_BLOCKS.contains(blockId)) {
                registeredBlocks.add(blockId);
            }
        }

        assertFalse(registeredBlocks.isEmpty(), "Must find registered blocks in SandStormBlocks.java");

        Set<String> taggedBlocks = new HashSet<>();
        List<String> tagViolations = new ArrayList<>();

        try (Stream<Path> tagPaths = Files.walk(mineableDir)) {
            tagPaths.filter(p -> p.toString().endsWith(".json")).forEach(tagPath -> {
                try (FileReader reader = new FileReader(tagPath.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (parsed.isJsonObject()) {
                        JsonObject obj = parsed.getAsJsonObject();
                        if (obj.has("values")) {
                            JsonArray values = obj.getAsJsonArray("values");
                            for (JsonElement val : values) {
                                String tagValue = val.getAsString();
                                if (tagValue.startsWith("sandstorm:")) {
                                    String id = tagValue.substring("sandstorm:".length());
                                    taggedBlocks.add(id);
                                    if (!registeredBlocks.contains(id) && !EXEMPT_BLOCKS.contains(id)) {
                                        tagViolations.add(tagPath.getFileName() + " references unregistered block: " + tagValue);
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        List<String> missingViolations = new ArrayList<>();
        for (String blockId : registeredBlocks) {
            if (!taggedBlocks.contains(blockId)) {
                missingViolations.add("Block 'sandstorm:" + blockId + "' is registered but missing from all minecraft:mineable/* tags.");
            }
        }

        assertTrue(tagViolations.isEmpty(), "Found invalid block references in mineable tags:\n" + String.join("\n", tagViolations));
        assertTrue(missingViolations.isEmpty(), "Found blocks missing from mineable tags:\n" + String.join("\n", missingViolations));
    }
}
