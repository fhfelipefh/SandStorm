package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AncientBunkerLightingArchitectureTest {

    private static final Path CATALOG_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "content", "world", "dungeon", "AncientBunkerCatalog.java"
    );

    private static final Path GENERATOR_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "content", "world", "structure", "AncientBunkerGenerator.java"
    );

    @Test
    void redstoneLampsInCatalogMustBeInitializedAsLit() throws IOException {
        String content = Files.readString(CATALOG_PATH);
        assertTrue(content.contains("Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true)"));
        assertFalse(content.contains("Blocks.REDSTONE_LAMP.defaultBlockState());"));
    }

    @Test
    void redstoneBlocksInCatalogMustTriggerNeighborUpdates() throws IOException {
        String content = Files.readString(CATALOG_PATH);
        assertTrue(content.contains("Blocks.REDSTONE_BLOCK.defaultBlockState(), 3)"));
        assertFalse(content.contains("Blocks.REDSTONE_BLOCK.defaultBlockState());"));
    }

    @Test
    void redstoneLampsInGeneratorMustBeInitializedAsLit() throws IOException {
        String content = Files.readString(GENERATOR_PATH);
        assertTrue(content.contains("Blocks.REDSTONE_LAMP.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3)"));
        assertFalse(content.contains("Blocks.REDSTONE_LAMP.defaultBlockState(), 2)"));
    }

    @Test
    void redstoneBlocksInGeneratorMustTriggerNeighborUpdates() throws IOException {
        String content = Files.readString(GENERATOR_PATH);
        assertTrue(content.contains("Blocks.REDSTONE_BLOCK.defaultBlockState(), 3)"));
        assertFalse(content.contains("Blocks.REDSTONE_BLOCK.defaultBlockState(), 2)"));
    }
}
