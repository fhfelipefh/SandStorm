package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonCeilingAndLadderIntegrityArchitectureTest {

    @Test
    void cargoHoistShaftMustNotCarveCeilingOrBreakLadder() throws IOException {
        Path catalogPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "MillennialRepositoryCatalog.java");
        assertTrue(Files.exists(catalogPath), "MillennialRepositoryCatalog.java must exist");
        String content = Files.readString(catalogPath);

        assertFalse(content.contains("ly <= 5; ly++) {\n                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR"),
                "CargoHoistShaft must never carve ceiling ly=5 to AIR");
        assertFalse(content.contains("setLocalBlock(level, origin, rotation, lx, 1, 1, size, Blocks.IRON_BARS"),
                "Iron bars must not overwrite ladder column at (2, 1, 1)");
        assertTrue(content.contains("Blocks.IRON_BLOCK"),
                "CargoHoistShaft must mount an iron block winch motor for hoist chain");
        assertTrue(content.contains("SandStormBlocks.REINFORCED_CONCRETE"),
                "CargoHoistShaft must reinforce ceiling with reinforced concrete");
    }

    @Test
    void showcaseMustBuildRoofCapOverDungeons() throws IOException {
        Path showcasePath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java");
        assertTrue(Files.exists(showcasePath), "ShowcaseAutomation.java must exist");
        String content = Files.readString(showcasePath);

        assertTrue(content.contains("buildRoofCap("),
                "ShowcaseAutomation must seal dungeons with buildRoofCap");
    }

    @Test
    void repositoryDungeonMustFilterVerticalTransitionsPerFloor() throws IOException {
        Path dungeonPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "dungeon", "MillennialRepositoryDungeon.java");
        assertTrue(Files.exists(dungeonPath), "MillennialRepositoryDungeon.java must exist");
        String content = Files.readString(dungeonPath);

        assertTrue(content.contains("repository_stairwell_up"),
                "MillennialRepositoryDungeon must filter out repository_stairwell_up on floor 0");
        assertTrue(content.contains("repository_stairwell_down"),
                "MillennialRepositoryDungeon must filter out repository_stairwell_down on bottom floor");
    }
}
