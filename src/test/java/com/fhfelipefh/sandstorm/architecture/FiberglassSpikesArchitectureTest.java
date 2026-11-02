package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FiberglassSpikesArchitectureTest {

    private static final Path BLOCK_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "FiberglassSpikesBlock.java");
    private static final Path BLOCKS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
    private static final Path ITEMS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
    private static final Path BLOCKSTATE_JSON = Path.of("src", "main", "resources", "assets", "sandstorm", "blockstates", "fiberglass_spikes.json");
    private static final Path MODEL_BLOCK_JSON = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "fiberglass_spikes.json");
    private static final Path MODEL_ITEM_JSON = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "fiberglass_spikes.json");
    private static final Path ITEM_DEF_JSON = Path.of("src", "main", "resources", "assets", "sandstorm", "items", "fiberglass_spikes.json");
    private static final Path TEXTURE_PNG = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "fiberglass_spikes.png");
    private static final Path LOOT_TABLE_JSON = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "blocks", "fiberglass_spikes.json");
    private static final Path RECIPE_COPPER_JSON = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "fiberglass_spikes_from_copper.json");
    private static final Path RECIPE_IRON_JSON = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "fiberglass_spikes_from_iron.json");
    private static final Path EN_US_LANG = Path.of("src", "main", "resources", "assets", "sandstorm", "lang", "en_us.json");
    private static final Path PT_BR_LANG = Path.of("src", "main", "resources", "assets", "sandstorm", "lang", "pt_br.json");

    @Test
    void verifyFiberglassSpikesFilesAndMechanics() throws IOException {
        assertTrue(Files.exists(BLOCK_JAVA), "FiberglassSpikesBlock.java must exist");
        assertTrue(Files.exists(BLOCKSTATE_JSON), "Blockstate JSON must exist");
        assertTrue(Files.exists(MODEL_BLOCK_JSON), "Model block JSON must exist");
        assertTrue(Files.exists(MODEL_ITEM_JSON), "Model item JSON must exist");
        assertTrue(Files.exists(ITEM_DEF_JSON), "Item definition JSON must exist");
        assertTrue(Files.exists(TEXTURE_PNG), "Texture PNG must exist");
        assertTrue(Files.size(TEXTURE_PNG) > 0, "Texture PNG must not be empty");
        assertTrue(Files.exists(LOOT_TABLE_JSON), "Loot table JSON must exist");
        assertTrue(Files.exists(RECIPE_COPPER_JSON), "Copper recipe JSON must exist");
        assertTrue(Files.exists(RECIPE_IRON_JSON), "Iron recipe JSON must exist");

        String blockContent = Files.readString(BLOCK_JAVA);
        assertTrue(blockContent.contains("IntegerProperty WEAR = IntegerProperty.create(\"wear\", 0, 4);"), "Must declare WEAR property 0..4");
        assertTrue(blockContent.contains("isShiftKeyDown()"), "Must support sneaking bypass");
        assertTrue(blockContent.contains("makeStuckInBlock"), "Must trap and slow entities inside");
        assertTrue(blockContent.contains("destroyBlock"), "Must break when maximum wear is reached");

        String blocksContent = Files.readString(BLOCKS_JAVA);
        assertTrue(blocksContent.contains("FIBERGLASS_SPIKES"), "Must be registered in SandStormBlocks");

        String itemsContent = Files.readString(ITEMS_JAVA);
        assertTrue(itemsContent.contains("entries.accept(SandStormBlocks.FIBERGLASS_SPIKES);"), "Must be in creative tab");

        String enLang = Files.readString(EN_US_LANG);
        assertTrue(enLang.contains("\"block.sandstorm.fiberglass_spikes\": \"Fiberglass Spikes\""), "Must have en_us translation");

        String ptLang = Files.readString(PT_BR_LANG);
        assertTrue(ptLang.contains("\"block.sandstorm.fiberglass_spikes\": \"Espinhos de Fibra de Vidro\""), "Must have pt_br translation");
    }
}

