package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldTypeLoreIsolationArchitectureTest {

    private static final List<String> LORE_HANDLERS = List.of(
            "src/main/java/com/fhfelipefh/sandstorm/content/survival/FusedSpaceSuitHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/world/SpaceshipLandingManager.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/survival/MultiplayerSpawnHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/world/VanillaMonsterSuppressionHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/survival/BedRestrictionHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/survival/TechnologyToolRestrictionHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/survival/MagicSuppressionHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/world/DimensionPortalRestrictionHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/world/SandstormWeatherHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/survival/SeismicSurvivalHandler.java",
            "src/main/java/com/fhfelipefh/sandstorm/content/survival/SuitSurvivalHandler.java"
    );

    @Test
    void shouldEnsureSandStormWorldHelperExists() {
        Path helper = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "SandStormWorldHelper.java");
        assertTrue(Files.exists(helper));
    }

    @Test
    void shouldEnsureAllLoreHandlersCheckSandStormWorldType() throws IOException {
        for (String relativePath : LORE_HANDLERS) {
            Path path = Path.of(relativePath);
            assertTrue(Files.exists(path), "File must exist: " + relativePath);
            String content = Files.readString(path);
            assertTrue(
                    content.contains("SandStormWorldHelper.isSandStormWorld"),
                    "Lore handler " + path.getFileName() + " must condition restrictions on SandStormWorldHelper.isSandStormWorld to preserve compatibility with other world types."
            );
        }
    }
}
