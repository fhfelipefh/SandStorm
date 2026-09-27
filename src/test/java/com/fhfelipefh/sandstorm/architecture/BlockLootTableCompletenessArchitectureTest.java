package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockLootTableCompletenessArchitectureTest {

    private static final Pattern REGISTER_PATTERN = Pattern.compile(
            "register\\(\"([a-z0-9_]+)\","
    );

    private static final Set<String> EXEMPT_BLOCKS = Set.of(
            "brackish_aquifer"
    );

    @Test
    void allRegisteredSolidBlocksMustHaveLootTables() throws IOException {
        Path blocksFile = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
        Path lootTablesDir = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "blocks");

        assertTrue(Files.exists(blocksFile), "SandStormBlocks.java must exist");
        assertTrue(Files.exists(lootTablesDir), "Block loot tables directory must exist");

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

        List<String> missingLootTables = new ArrayList<>();
        for (String blockId : registeredBlocks) {
            Path lootTableFile = lootTablesDir.resolve(blockId + ".json");
            if (!Files.exists(lootTableFile)) {
                missingLootTables.add("Missing block loot table for 'sandstorm:" + blockId + "': expected " + lootTableFile);
            }
        }

        assertTrue(missingLootTables.isEmpty(), "Found blocks missing loot tables:\n" + String.join("\n", missingLootTables));
    }
}
