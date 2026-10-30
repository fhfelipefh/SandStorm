package com.fhfelipefh.sandstorm.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ApexMachineGeometryArchitectureTest {
    private static final Path ENTITY_REGISTRY = Path.of("src/main/java/com/fhfelipefh/sandstorm/content/entity/SandStormEntities.java");

    @Test
    void apexMachineHitboxesMustMatchTheirRenderedFootprints() throws IOException {
        String registry = Files.readString(ENTITY_REGISTRY);

        assertTrue(registry.contains(".sized(1.9f, 1.75f)"), "Grazer hitbox must contain its antlers and legs");
        assertTrue(registry.contains(".sized(1.9f, 1.9f)"), "Fanghorn hitbox must contain its antlers and legs");
        assertTrue(registry.contains(".sized(1.75f, 1.75f)"), "Frostclaw hitbox must match its compact quadruped model");
        assertTrue(registry.contains(".sized(2.3f, 0.85f)"), "Glinthawk hitbox must contain its wings without invisible height");
        assertTrue(registry.contains(".sized(2.9f, 1.65f)"), "Waterwing hitbox must contain its wings and landing gear");
    }
}
