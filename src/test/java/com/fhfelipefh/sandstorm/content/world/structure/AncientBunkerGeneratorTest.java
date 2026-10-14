package com.fhfelipefh.sandstorm.content.world.structure;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AncientBunkerGeneratorTest {

    private static final Path GENERATOR_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "content", "world", "structure", "AncientBunkerGenerator.java"
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
    void shouldContainExpectedTechBlocksAndComponents() throws IOException {
        String content = Files.readString(GENERATOR_PATH);
        assertTrue(content.contains("ANCIENT_DATA_CORE"));
        assertTrue(content.contains("BURIED_TECH_RUINS"));
        assertTrue(content.contains("SOLID_STATE_ACCUMULATOR"));
        assertTrue(content.contains("GRID_MONITOR_CONSOLE"));
        assertTrue(content.contains("SMART_FLUID_PIPE"));
        assertTrue(content.contains("NANITE_FABRICATOR"));
        assertTrue(content.contains("PRINTER_3D"));
        assertTrue(content.contains("TITANIUM_SPIKE_WALL"));
        assertTrue(content.contains("FULGURITE_GLASS"));
        assertTrue(content.contains("PIEZO_QUARTZ_BLOCK"));
        assertTrue(content.contains("populateChest"));
        assertTrue(content.contains("TECH_DISC"));
        assertTrue(content.contains("NANO_ACTUATOR"));
        assertTrue(content.contains("CIRCUIT_BOARD"));
        assertTrue(content.contains("RAW_SILICON"));
        assertTrue(content.contains("SILICON_WAFER"));
        assertTrue(content.contains("SCRAP_METAL"));
    }

    @Test
    void shouldCapFoundationDepthToPreventCascadingChunkLoads() throws IOException {
        String content = Files.readString(GENERATOR_PATH);
        assertTrue(content.contains("Math.max(level.getMinY()"));
        assertFalse(content.contains("while ("));
    }
}
