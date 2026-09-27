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

class GraphicOptimizersCompatibilityArchitectureTest {

    @Test
    void fabricModJsonMustSuggestSodiumIrisAndLithium() throws IOException {
        Path fabricModJson = Path.of("src", "main", "resources", "fabric.mod.json");
        assertTrue(Files.exists(fabricModJson), "fabric.mod.json must exist");

        String content = Files.readString(fabricModJson);
        assertTrue(content.contains("\"suggests\""), "fabric.mod.json must declare suggests block for graphic optimizers");
        assertTrue(content.contains("\"sodium\""), "fabric.mod.json must declare official compatibility for sodium");
        assertTrue(content.contains("\"iris\""), "fabric.mod.json must declare official compatibility for iris");
        assertTrue(content.contains("\"lithium\""), "fabric.mod.json must declare official compatibility for lithium");
    }

    @Test
    void fogAndLightmapMixinsMustDeclareOptimizedPriorityForSodiumAndIris() throws IOException {
        Path fogMixin = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "SandstormFogMixin.java");
        assertTrue(Files.exists(fogMixin), "SandstormFogMixin.java must exist");
        String fogContent = Files.readString(fogMixin);
        assertTrue(fogContent.contains("priority = 1050"), "SandstormFogMixin must declare priority = 1050 to override Sodium fog parameters");
        assertFalse(fogContent.contains("@Overwrite"), "SandstormFogMixin must not use @Overwrite to preserve Sodium chunk/fog pipelines");

        Path lightMixin = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "mixin", "FlashlightLightMixin.java");
        assertTrue(Files.exists(lightMixin), "FlashlightLightMixin.java must exist");
        String lightContent = Files.readString(lightMixin);
        assertTrue(lightContent.contains("priority = 1050"), "FlashlightLightMixin must declare priority = 1050 for lightmap compatibility with Sodium/Iris");
        assertFalse(lightContent.contains("@Overwrite"), "FlashlightLightMixin must not use @Overwrite");
    }

    @Test
    void blockEntityRenderersMustUseSubmitNodeCollectorArchitecture() throws IOException {
        Path rendererDir = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "renderer");
        assertTrue(Files.exists(rendererDir), "Renderer directory must exist");

        List<String> violations = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(rendererDir)) {
            paths.filter(p -> p.toString().endsWith("BlockEntityRenderer.java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    if (!content.contains("SubmitNodeCollector")) {
                        violations.add(path + " does not use SubmitNodeCollector (required for Sodium/Iris batching)");
                    }
                    if (content.contains("RenderSystem.enableBlend()") || content.contains("RenderSystem.disableBlend()")) {
                        violations.add(path + " contains raw RenderSystem blend calls that leak into Sodium/Iris pipeline");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found block entity renderer compatibility violations:\n" + String.join("\n", violations));
    }

    @Test
    void proceduralRuinsGenerationMustBeLithiumSafe() throws IOException {
        Path ruinsManager = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ProceduralRuinsManager.java");
        assertTrue(Files.exists(ruinsManager), "ProceduralRuinsManager.java must exist");

        String content = Files.readString(ruinsManager);
        assertFalse(content.contains("level.getChunk("), "ProceduralRuinsManager must not perform re-entrant getChunk calls inside chunk load listeners (breaks Lithium cache)");
        assertTrue(content.contains("chunk.getHeight("), "ProceduralRuinsManager must use chunk.getHeight directly for Lithium heightmap compatibility");
        assertTrue(content.contains("level.getServer().execute("), "ProceduralRuinsManager must defer structure generation to the server main loop");
    }
}
