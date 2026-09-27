package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerDeadlockPreventionArchitectureTest {

    @Test
    void noSaveAndJoinInTickHandlers() throws IOException {
        Path srcDir = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm");
        assertTrue(Files.exists(srcDir), "Source directory must exist");

        List<String> violations = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(srcDir)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    if (content.contains("saveAndJoin()")) {
                        violations.add(path + " contains 'saveAndJoin()'. Native setDirty() should be used instead to prevent IO deadlocks on Server Thread.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found saveAndJoin() violations:\n" + String.join("\n", violations));
    }

    @Test
    void proceduralRuinsManagerMustUseTickEventsAndIsLoaded() throws IOException {
        Path ruinsManager = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ProceduralRuinsManager.java");
        assertTrue(Files.exists(ruinsManager), "ProceduralRuinsManager.java must exist");

        String content = Files.readString(ruinsManager);
        assertTrue(content.contains("ServerTickEvents.END_SERVER_TICK"), "ProceduralRuinsManager must defer generation using ServerTickEvents to avoid chunk lock race conditions.");
        assertTrue(content.contains("PENDING_RUINS"), "ProceduralRuinsManager must maintain a queue of pending ruins.");
        assertTrue(content.contains("isLoaded("), "ProceduralRuinsManager must verify level.isLoaded() before generation to prevent deadlocks during async chunk loading.");
    }

    @Test
    void spaceshipLandingMustCapFoundationDepth() throws IOException {
        Path landingManager = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "SpaceshipLandingManager.java");
        assertTrue(Files.exists(landingManager), "SpaceshipLandingManager.java must exist");

        String content = Files.readString(landingManager);
        assertTrue(content.contains("Math.max("), "SpaceshipLandingManager must cap foundation depth using Math.max to prevent cascading chunk loading to world floor.");
        assertFalse(content.contains("while (p.getY() >= level.getMinY()) {"), "SpaceshipLandingManager must not loop foundation placement down to level.getMinY() unchecked.");
    }
}
