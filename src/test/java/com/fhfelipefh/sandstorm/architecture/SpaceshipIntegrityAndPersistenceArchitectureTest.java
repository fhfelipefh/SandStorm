package com.fhfelipefh.sandstorm.architecture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceshipIntegrityAndPersistenceArchitectureTest {

    @Test
    void allStructureNbtPalettesMustUseMinecraftStandardNameKey() throws IOException {
        Path structureDir = Path.of("src", "main", "resources", "data", "sandstorm", "structure");
        assertTrue(Files.exists(structureDir), "Structure directory must exist");

        List<String> violations = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(structureDir)) {
            stream.filter(p -> p.toString().endsWith(".nbt")).forEach(path -> {
                try {
                    CompoundTag root = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
                    ListTag palette = root.getListOrEmpty("palette");
                    if (!palette.isEmpty()) {
                        for (int i = 0; i < palette.size(); i++) {
                            CompoundTag entry = palette.getCompoundOrEmpty(i);
                            String name = entry.getStringOr("Name", "");
                            if (name.isEmpty()) {
                                violations.add(path.getFileName() + ": palette index " + i + " is missing standard 'Name' tag");
                            } else if (!name.contains(":")) {
                                violations.add(path.getFileName() + ": palette index " + i + " has invalid namespace format: " + name);
                            }
                        }
                    }
                    ListTag blocks = root.getListOrEmpty("blocks");
                    if (blocks.isEmpty()) {
                        violations.add(path.getFileName() + ": missing 'blocks' tag or has 0 blocks");
                    }
                } catch (IOException e) {
                    violations.add(path.getFileName() + ": failed to read compressed NBT: " + e.getMessage());
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found NBT palette structure violations:\n" + String.join("\n", violations));
    }

    @Test
    void spaceshipLandingManagerMustEnforceChunkPreloadingAndDirtyMarking() throws IOException {
        Path file = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "SpaceshipLandingManager.java");
        assertTrue(Files.exists(file), "SpaceshipLandingManager.java must exist");

        String content = Files.readString(file);
        assertTrue(content.contains("loadSpaceshipChunks("),
                "SpaceshipLandingManager must pre-load chunks surrounding the spaceship to prevent async race conditions.");
        assertTrue(content.contains("markSpaceshipChunksDirty("),
                "SpaceshipLandingManager must provide a helper to flag chunk unsaved state.");
        assertTrue(content.contains("markUnsaved()"),
                "SpaceshipLandingManager must call markUnsaved() on LevelChunk to guarantee persistence across server reloads.");
    }

    @Test
    void spaceshipLandingManagerMustEnforceIntegrityCheckAndSelfHealing() throws IOException {
        Path file = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "SpaceshipLandingManager.java");
        assertTrue(Files.exists(file), "SpaceshipLandingManager.java must exist");

        String content = Files.readString(file);
        assertTrue(content.contains("isSpaceshipIntact("),
                "SpaceshipLandingManager must implement isSpaceshipIntact to verify structural presence.");
        assertTrue(content.contains("rebuildSpaceship("),
                "SpaceshipLandingManager must implement rebuildSpaceship for automatic restoration.");
        assertTrue(content.contains("!isSpaceshipIntact("),
                "SpaceshipLandingManager must trigger self-healing rebuild when ship structural integrity fails.");
        assertTrue(content.contains("getDesignVersion() < 4"),
                "SpaceshipLandingManager must check for designVersion < 4 to heal legacy world saves.");
    }

    @Test
    void spaceshipLandingManagerMustPreserveCabinRoofIntegrity() throws IOException {
        Path file = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "SpaceshipLandingManager.java");
        assertTrue(Files.exists(file), "SpaceshipLandingManager.java must exist");

        String content = Files.readString(file);
        assertFalse(content.contains("int dx = -3; dx <= 3; dx++"),
                "SpaceshipLandingManager must not carve a wide 7x7 hole through the cabin roof for solar receiver clearance.");
        assertTrue(content.contains("int dx = -1; dx <= 1; dx++"),
                "SpaceshipLandingManager must restrict sky clearance to a tight 3x3 skylight directly over the solar receiver.");
    }

    @Test
    void spaceshipSavedDataMustPersistDesignVersionAndDirtyFlag() throws IOException {
        Path file = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "SpaceshipSavedData.java");
        assertTrue(Files.exists(file), "SpaceshipSavedData.java must exist");

        String content = Files.readString(file);
        assertTrue(content.contains("design_version"),
                "SpaceshipSavedData CODEC must serialize and deserialize design_version.");
        assertTrue(content.contains("public void setDesignVersion(int designVersion)"),
                "SpaceshipSavedData must expose setDesignVersion.");
        assertTrue(content.contains("setDirty()"),
                "SpaceshipSavedData must invoke native setDirty() on mutators for asynchronous persistence.");
    }
}
