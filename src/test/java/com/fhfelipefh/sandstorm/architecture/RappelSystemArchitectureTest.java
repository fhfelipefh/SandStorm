package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RappelSystemArchitectureTest {

    private static final Path ITEMS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SandStormItems.java");
    private static final Path BLOCKS_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "SandStormBlocks.java");
    private static final Path ENTITIES_JAVA = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "entity", "SandStormEntities.java");
    private static final Path CLIENT_JAVA = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java");

    @Test
    void rappelRegistrationsMustBePresentInCoreJava() throws IOException {
        String items = Files.readString(ITEMS_JAVA);
        String blocks = Files.readString(BLOCKS_JAVA);
        String entities = Files.readString(ENTITIES_JAVA);
        String client = Files.readString(CLIENT_JAVA);

        assertTrue(items.contains("SYNTHETIC_FIBER"), "SYNTHETIC_FIBER must be registered");
        assertTrue(items.contains("RAPPEL_WINCH"), "RAPPEL_WINCH must be registered");
        assertTrue(blocks.contains("RAPPEL_ANCHOR"), "RAPPEL_ANCHOR must be registered");
        assertTrue(blocks.contains("RAPPEL_ROPE"), "RAPPEL_ROPE must be registered");
        assertTrue(blocks.contains("RAPPEL_ANCHOR_BE"), "RAPPEL_ANCHOR_BE must be registered");
        assertTrue(entities.contains("RAPPEL_HARNESS"), "RAPPEL_HARNESS must be registered");

        assertTrue(items.contains("entries.accept(SYNTHETIC_FIBER);"), "SYNTHETIC_FIBER must be in creative tab");
        assertTrue(items.contains("entries.accept(RAPPEL_WINCH);"), "RAPPEL_WINCH must be in creative tab");
        assertTrue(items.contains("entries.accept(SandStormBlocks.RAPPEL_ANCHOR);"), "RAPPEL_ANCHOR must be in creative tab");
        assertTrue(items.contains("entries.accept(SandStormBlocks.RAPPEL_ROPE);"), "RAPPEL_ROPE must be in creative tab");

        assertTrue(client.contains("RappelAnchorBlockEntityRenderer"), "RappelAnchorBlockEntityRenderer must be registered");
        assertTrue(client.contains("RappelHarnessRenderer"), "RappelHarnessRenderer must be registered");
    }

    @Test
    void rappelAssetsAndTexturesMustPhysicallyExist() {
        List<Path> requiredFiles = List.of(
                Path.of("src", "main", "resources", "assets", "sandstorm", "items", "synthetic_fiber.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "items", "rappel_winch.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "items", "rappel_anchor.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "items", "rappel_rope.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "synthetic_fiber.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "rappel_winch.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "rappel_anchor.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "rappel_rope.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "rappel_anchor.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "rappel_rope.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "blockstates", "rappel_anchor.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "blockstates", "rappel_rope.json"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "synthetic_fiber.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "rappel_winch.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "rappel_anchor.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "rappel_rope.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "rappel_harness", "rappel_harness.png"),
                Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "rappel_anchor", "rappel_anchor_mechanism.png")
        );

        for (Path file : requiredFiles) {
            assertTrue(Files.exists(file), "Asset file must exist: " + file);
        }
    }

    @Test
    void rappelRecipesMustPhysicallyExist() {
        List<Path> requiredRecipes = List.of(
                Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "synthetic_fiber_from_cactus.json"),
                Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "synthetic_fiber_from_string.json"),
                Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "synthetic_fiber_from_ancient_reed.json"),
                Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "synthetic_fiber_from_dune_ephedra.json"),
                Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "rappel_winch.json")
        );

        for (Path file : requiredRecipes) {
            assertTrue(Files.exists(file), "Recipe file must exist: " + file);
        }
    }
}
