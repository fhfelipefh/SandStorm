package com.fhfelipefh.sandstorm.architecture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelParentIntegrityArchitectureTest {

    private static final Path ASSETS_ROOT = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path BLOCK_MODELS_DIR = ASSETS_ROOT.resolve("models").resolve("block");
    private static final Path ITEM_MODELS_DIR = ASSETS_ROOT.resolve("models").resolve("item");

    private static final Set<String> FORBIDDEN_VANILLA_PARENTS = Set.of(
            "minecraft:block/fence_gate",
            "minecraft:block/fence_gate_open",
            "minecraft:block/door_bottom",
            "minecraft:block/door_bottom_rh",
            "minecraft:block/door_top",
            "minecraft:block/door_top_rh"
    );

    @Test
    void allModelParentsMustExistInModOrVanilla() throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        assertModelParentsInDir(BLOCK_MODELS_DIR, cl);
        assertModelParentsInDir(ITEM_MODELS_DIR, cl);
    }

    private void assertModelParentsInDir(Path dir, ClassLoader cl) throws IOException {
        assertTrue(Files.exists(dir));
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(p -> p.toString().endsWith(".json")).forEach(p -> {
                try (FileReader reader = new FileReader(p.toFile())) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (!parsed.isJsonObject()) {
                        return;
                    }
                    JsonObject obj = parsed.getAsJsonObject();
                    if (!obj.has("parent")) {
                        return;
                    }
                    String parent = obj.get("parent").getAsString();
                    assertFalse(FORBIDDEN_VANILLA_PARENTS.contains(parent),
                            "Model " + p.getFileName() + " references known invalid vanilla parent: " + parent);

                    if (parent.startsWith("sandstorm:block/")) {
                        String rel = parent.substring("sandstorm:block/".length()) + ".json";
                        assertTrue(Files.exists(BLOCK_MODELS_DIR.resolve(rel)),
                                "Model " + p.getFileName() + " references missing sandstorm block model: " + rel);
                    } else if (parent.startsWith("sandstorm:item/")) {
                        String rel = parent.substring("sandstorm:item/".length()) + ".json";
                        assertTrue(Files.exists(ITEM_MODELS_DIR.resolve(rel)),
                                "Model " + p.getFileName() + " references missing sandstorm item model: " + rel);
                    } else if (parent.startsWith("minecraft:")) {
                        String resPath = "assets/minecraft/models/" + parent.substring("minecraft:".length()) + ".json";
                        URL res = cl.getResource(resPath);
                        assertNotNull(res, "Model " + p.getFileName() + " references missing vanilla model: " + parent + " (" + resPath + ")");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
