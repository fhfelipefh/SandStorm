package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowcaseSpatialIsolationArchitectureTest {

    private static final Path SHOWCASE_AUTOMATION_JAVA = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java"
    );

    @Test
    void sandwormMustHaveDedicatedColosseumAndNeverSpawnInEntityWing() throws IOException {
        assertTrue(Files.exists(SHOWCASE_AUTOMATION_JAVA), "ShowcaseAutomation.java must exist");
        String content = Files.readString(SHOWCASE_AUTOMATION_JAVA);

        assertTrue(content.contains("buildSandwormColosseum"),
                "ShowcaseAutomation must provide dedicated buildSandwormColosseum");
        assertTrue(content.contains("offset(0, 0, -170)"),
                "Sandworm Colosseum must be isolated in the far northern sector at Z = -170");

        int entityWingIndex = content.indexOf("void buildEntityWing(");
        assertTrue(entityWingIndex >= 0);
        int nextMethodIndex = content.indexOf("void placeEntityPedestal(", entityWingIndex);
        assertTrue(nextMethodIndex > entityWingIndex);
        String entityWingBody = content.substring(entityWingIndex, nextMethodIndex);

        assertFalse(entityWingBody.contains("SandwormEntity"),
                "buildEntityWing must never spawn SandwormEntity next to drones or vehicles");
        assertFalse(entityWingBody.contains("-82.0"),
                "buildEntityWing must not use Z = -82 which clashes with the cyborg complex");
    }

    @Test
    void defenseRangeAndCyborgFacilityMustMaintainMinimumSpatialClearance() throws IOException {
        assertTrue(Files.exists(SHOWCASE_AUTOMATION_JAVA), "ShowcaseAutomation.java must exist");
        String content = Files.readString(SHOWCASE_AUTOMATION_JAVA);

        assertTrue(content.contains("buildDefenseAndBallisticsRange(level, center.offset(-65, 0, -50))"),
                "Defense range must be anchored west at X = -65 to prevent overlap with the central complex");

        assertTrue(content.contains("buildIsolatedTestingChamber(level, -61, -47"),
                "Eastern defense chambers must terminate at X = -47 or further west");

        assertTrue(content.contains("int xMin = -20;"),
                "Cyborg facility western boundary is X = -20, guaranteeing >= 27 blocks of clearance");

        assertFalse(content.contains("buildIsolatedTestingChamber(level, -32, -18"),
                "Must not use old colliding chamber coordinates (-32 to -18) that clipped the cyborg facility");
    }

    @Test
    void transitTerminalMustProvideTeleportToAllMajorSectors() throws IOException {
        assertTrue(Files.exists(SHOWCASE_AUTOMATION_JAVA), "ShowcaseAutomation.java must exist");
        String content = Files.readString(SHOWCASE_AUTOMATION_JAVA);

        assertTrue(content.contains("Setor Norte: Defesa & Balística"),
                "Transit terminal must offer defense range teleport");
        assertTrue(content.contains("Setor Norte: Arena do Colosso (Sandworm)"),
                "Transit terminal must offer dedicated sandworm colosseum teleport");
        assertTrue(content.contains("Setor Norte: Complexo de Ciborgues"),
                "Transit terminal must offer cyborg facility teleport");
    }
}

