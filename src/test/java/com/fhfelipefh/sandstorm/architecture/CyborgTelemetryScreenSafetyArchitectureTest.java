package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.item.CyborgUpgradeItem;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyborgTelemetryScreenSafetyArchitectureTest {

    private static final Path TELEMETRY_SCREEN_JAVA = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui", "CyborgTelemetryScreen.java"
    );

    @Test
    void screenMustCoverAllCyborgUpgradeTypesWithoutOutOfBounds() throws IOException {
        assertTrue(Files.exists(TELEMETRY_SCREEN_JAVA), "CyborgTelemetryScreen.java must exist");
        String content = Files.readString(TELEMETRY_SCREEN_JAVA);

        int typeCount = CyborgUpgradeItem.CyborgUpgradeType.values().length;
        assertEquals(5, typeCount, "CyborgUpgradeType must have 5 upgrades");

        assertTrue(content.contains("String[] badgeLabels = new String[]{\"AC\", \"CR\", \"LR\", \"PZ\", \"SS\"};"),
                "badgeLabels array must contain all 5 upgrade badge acronyms");

        assertTrue(content.contains("\"Sensor Sísmico Subdérmico\""),
                "upgradeNames must contain Subdermal Tremor Sensor");

        assertTrue(content.contains("Detecção Sísmica de Sandworms"),
                "upgradeEffects must contain tremor detection effect");

        assertTrue(content.contains("types.length * 21"),
                "Badge panel height must dynamically scale with upgrade count");
    }
}

