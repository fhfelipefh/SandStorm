package com.fhfelipefh.sandstorm.content.world.structure;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbandonedOutpostGeneratorTest {

    private static final Path GENERATOR_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "content", "world", "structure", "AbandonedOutpostGenerator.java"
    );

    @Test
    void neverReferenceFinishedSuitUpgradeItemsInGenerator() throws IOException {
        String content = Files.readString(GENERATOR_PATH);
        assertFalse(content.contains("SUIT_UPGRADE_BATTERY"));
        assertFalse(content.contains("SUIT_UPGRADE_THERMAL"));
        assertFalse(content.contains("SUIT_UPGRADE_SEISMIC"));
        assertFalse(content.contains("SUIT_UPGRADE_VISOR"));
        assertFalse(content.contains("SUIT_UPGRADE_JETPACK"));
        assertFalse(content.contains("suit_upgrade"));
    }

    @Test
    void shouldHaveMutuallyExclusiveArchetypesForPartialComponents() throws IOException {
        String content = Files.readString(GENERATOR_PATH);
        assertTrue(content.contains("int archetype = random.nextInt("));
        assertTrue(content.contains("populateChest"));
        assertTrue(content.contains("REDSTONE_BLOCK"));
        assertTrue(content.contains("CIRCUIT_BOARD"));
        assertTrue(content.contains("BLUE_ICE"));
        assertTrue(content.contains("MAGMA_BLOCK"));
        assertTrue(content.contains("AMETHYST_SHARD"));
        assertTrue(content.contains("TINTED_GLASS"));
        assertTrue(content.contains("NANO_ACTUATOR"));
        assertTrue(content.contains("SANDWORM_CHITIN"));
        assertTrue(content.contains("TECH_DISC"));
        assertTrue(content.contains("PROPELLANT_CARTRIDGE"));
    }
}
