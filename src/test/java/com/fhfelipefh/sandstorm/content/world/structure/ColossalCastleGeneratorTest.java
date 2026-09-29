package com.fhfelipefh.sandstorm.content.world.structure;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColossalCastleGeneratorTest {

    private static final Path GENERATOR_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "content", "world", "structure", "ColossalCastleGenerator.java"
    );

    @Test
    void shouldVerifyGeneratorFileExistsAndHasArchitecture() throws IOException {
        assertTrue(Files.exists(GENERATOR_PATH));
        String content = Files.readString(GENERATOR_PATH);
        assertTrue(content.contains("generateFoundationAndClearing"));
        assertTrue(content.contains("generateOuterCurtainWalls"));
        assertTrue(content.contains("generateCornerBastions"));
        assertTrue(content.contains("generateSouthGatehouse"));
        assertTrue(content.contains("generateColossalKeep"));
        assertTrue(content.contains("generateCelestialSpire"));
        assertTrue(content.contains("generateFoliageAndDetails"));
    }

    @Test
    void shouldVerifyMaterialsAndFeaturesArePresent() throws IOException {
        String content = Files.readString(GENERATOR_PATH);
        assertTrue(content.contains("STONE_BRICKS"));
        assertTrue(content.contains("MOSSY_STONE_BRICKS"));
        assertTrue(content.contains("CRACKED_STONE_BRICKS"));
        assertTrue(content.contains("CHISELED_STONE_BRICKS"));
        assertTrue(content.contains("POLISHED_BLACKSTONE"));
        assertTrue(content.contains("SPRUCE_PLANKS"));
        assertTrue(content.contains("DARK_OAK_PLANKS"));
        assertTrue(content.contains("GOLD_BLOCK"));
        assertTrue(content.contains("POLISHED_BLACKSTONE_STAIRS"));
        assertTrue(content.contains("IRON_BARS"));
        assertTrue(content.contains("VINE"));
        assertTrue(content.contains("OAK_LEAVES"));
        assertTrue(content.contains("ADVANCED_REFRACTION_LENS"));
        assertFalse(content.contains("SUIT_UPGRADE_BATTERY"));
    }
}
