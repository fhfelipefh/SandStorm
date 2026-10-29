package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import com.fhfelipefh.sandstorm.content.item.CyborgUpgradeItem.CyborgUpgradeType;
import com.fhfelipefh.sandstorm.content.megastructure.MegastructureBlueprint;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScreenEnumBoundsSafetyArchitectureTest {

    private static final Path TELEMETRY_SCREEN = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui", "CyborgTelemetryScreen.java"
    );

    private static final Path SPIRE_SCREEN = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui", "HoloTacticalSpireScreen.java"
    );

    private static final Path GUI_DIR = Path.of(
            "src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui"
    );

    private static final Pattern ARRAY_STRING_ELEMENTS = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern ARRAY_HEX_ELEMENTS = Pattern.compile("0x[0-9A-Fa-f]+");

    @Test
    void cyborgUpgradeTypeArrayBoundsSynchronization() throws IOException {
        assertTrue(Files.exists(TELEMETRY_SCREEN), "CyborgTelemetryScreen.java must exist");
        String content = Files.readString(TELEMETRY_SCREEN);

        int upgradeCount = CyborgUpgradeType.values().length;
        assertEquals(5, upgradeCount, "CyborgUpgradeType must define 5 upgrade types");

        assertTrue(content.contains("badgeLabels"), "badgeLabels must be declared");
        assertTrue(content.contains("activeColors"), "activeColors must be declared");
        assertTrue(content.contains("activeBgs"), "activeBgs must be declared");
        assertTrue(content.contains("upgradeNames"), "upgradeNames must be declared");
        assertTrue(content.contains("upgradeEffects"), "upgradeEffects must be declared");

        assertTrue(content.contains("i < badgeLabels.length"), "badgeLabels access must be defensively bounded");
        assertTrue(content.contains("i < activeColors.length"), "activeColors access must be defensively bounded");
        assertTrue(content.contains("i < activeBgs.length"), "activeBgs access must be defensively bounded");
        assertTrue(content.contains("i < upgradeNames.length"), "upgradeNames access must be defensively bounded");
        assertTrue(content.contains("i < upgradeEffects.length"), "upgradeEffects access must be defensively bounded");

        int badgeCount = countMatches(content, "badgeLabels = new String\\[\\]\\{([^}]+)\\}", ARRAY_STRING_ELEMENTS);
        int colorsCount = countMatches(content, "activeColors = new int\\[\\]\\{([^}]+)\\}", ARRAY_HEX_ELEMENTS);
        int bgsCount = countMatches(content, "activeBgs = new int\\[\\]\\{([^}]+)\\}", ARRAY_HEX_ELEMENTS);
        int namesCount = countMatches(content, "upgradeNames = new String\\[\\]\\{([^}]+)\\}", ARRAY_STRING_ELEMENTS);
        int effectsCount = countMatches(content, "upgradeEffects = new String\\[\\]\\{([^}]+)\\}", ARRAY_STRING_ELEMENTS);

        assertTrue(badgeCount >= upgradeCount, "badgeLabels size (" + badgeCount + ") must match or exceed upgradeCount (" + upgradeCount + ")");
        assertTrue(colorsCount >= upgradeCount, "activeColors size (" + colorsCount + ") must match or exceed upgradeCount (" + upgradeCount + ")");
        assertTrue(bgsCount >= upgradeCount, "activeBgs size (" + bgsCount + ") must match or exceed upgradeCount (" + upgradeCount + ")");
        assertTrue(namesCount >= upgradeCount, "upgradeNames size (" + namesCount + ") must match or exceed upgradeCount (" + upgradeCount + ")");
        assertTrue(effectsCount >= upgradeCount, "upgradeEffects size (" + effectsCount + ") must match or exceed upgradeCount (" + upgradeCount + ")");
    }

    @Test
    void cyborgRoutineArrayBoundsSynchronization() throws IOException {
        assertTrue(Files.exists(TELEMETRY_SCREEN), "CyborgTelemetryScreen.java must exist");
        String content = Files.readString(TELEMETRY_SCREEN);

        int routineCount = CyborgRoutine.values().length;
        assertEquals(5, routineCount, "CyborgRoutine must have 5 routines");

        assertTrue(content.contains("i < labels.length"), "Routine labels access must be defensively bounded");
        assertTrue(content.contains("i < routineTooltips.size()"), "Routine tooltip access must be defensively bounded");

        int labelsCount = countMatches(content, "labels = new String\\[\\]\\{([^}]+)\\}", ARRAY_STRING_ELEMENTS);
        assertTrue(labelsCount >= routineCount, "Routine labels size (" + labelsCount + ") must match or exceed routineCount (" + routineCount + ")");
    }

    @Test
    void holoTacticalSpireArrayBoundsSynchronization() throws IOException {
        assertTrue(Files.exists(SPIRE_SCREEN), "HoloTacticalSpireScreen.java must exist");
        String content = Files.readString(SPIRE_SCREEN);

        assertTrue(content.contains("i < orderNames.length"), "orderNames access must be defensively bounded");
        assertTrue(content.contains("i < borderColors.length"), "borderColors access must be defensively bounded");
        assertTrue(content.contains("i < descs.size()"), "Tactical order tooltips access must be defensively bounded");

        int ordersCount = countMatches(content, "orderNames = \\{([^}]+)\\}", ARRAY_STRING_ELEMENTS);
        int colorsCount = countMatches(content, "borderColors = \\{([^}]+)\\}", ARRAY_HEX_ELEMENTS);

        assertEquals(ordersCount, colorsCount, "orderNames and borderColors must have the exact same size");
        assertTrue(ordersCount >= 4, "Spire orders count must have at least 4 tactical directives");
    }

    @Test
    void enumFromOrdinalSafetyAgainstOutOfBounds() {
        for (int i = -5; i <= 20; i++) {
            CyborgRoutine routine = CyborgRoutine.fromOrdinal(i);
            assertNotNull(routine, "CyborgRoutine.fromOrdinal(" + i + ") must not be null");

            CyborgSpecialty specialty = CyborgSpecialty.fromOrdinal(i);
            assertNotNull(specialty, "CyborgSpecialty.fromOrdinal(" + i + ") must not be null");

            MegastructureBlueprint blueprint = MegastructureBlueprint.byIndex(i);
            assertNotNull(blueprint, "MegastructureBlueprint.byIndex(" + i + ") must not be null");
        }

        assertEquals(CyborgRoutine.AUTONOMOUS_WORK, CyborgRoutine.fromOrdinal(-1));
        assertEquals(CyborgRoutine.AUTONOMOUS_WORK, CyborgRoutine.fromOrdinal(999));
        assertEquals(CyborgSpecialty.EXCAVATOR, CyborgSpecialty.fromOrdinal(-1));
        assertEquals(CyborgSpecialty.EXCAVATOR, CyborgSpecialty.fromOrdinal(999));
        assertEquals(MegastructureBlueprint.BIOSPHERE_DOME, MegastructureBlueprint.byIndex(-1));
        assertEquals(MegastructureBlueprint.BIOSPHERE_DOME, MegastructureBlueprint.byIndex(999));
    }

    @Test
    void cyborgUpgradeTypesMustHaveUniqueBitmasksAndIds() {
        Set<Integer> masks = new HashSet<>();
        Set<String> ids = new HashSet<>();

        for (CyborgUpgradeType type : CyborgUpgradeType.values()) {
            int mask = type.getMaskBit();
            assertTrue(mask > 0, "Upgrade mask must be positive");
            assertEquals(0, mask & (mask - 1), "Upgrade mask must be a single power of 2");
            assertTrue(masks.add(mask), "Upgrade bitmask must be unique: " + mask);

            String id = type.getId();
            assertNotNull(id, "Upgrade ID must not be null");
            assertNotEquals("", id.trim(), "Upgrade ID must not be blank");
            assertTrue(ids.add(id), "Upgrade ID must be unique: " + id);
        }
    }

    @Test
    void allClientScreensMustGuardEnumArrayAccess() throws IOException {
        List<String> violations = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(GUI_DIR)) {
            stream.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    if (content.contains(".values().length") || content.contains(".values()")) {
                        if (content.contains("badgeLabels[i]") && !content.contains("i < badgeLabels.length")) {
                            violations.add(path.getFileName() + " accesses badgeLabels[i] without bounds check");
                        }
                        if (content.contains("activeColors[i]") && !content.contains("i < activeColors.length")) {
                            violations.add(path.getFileName() + " accesses activeColors[i] without bounds check");
                        }
                        if (content.contains("upgradeNames[i]") && !content.contains("i < upgradeNames.length")) {
                            violations.add(path.getFileName() + " accesses upgradeNames[i] without bounds check");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        assertTrue(violations.isEmpty(), "Found unguarded array access in screens:\n" + String.join("\n", violations));
    }

    private int countMatches(String content, String blockRegex, Pattern elementPattern) {
        Pattern blockPattern = Pattern.compile(blockRegex);
        Matcher blockMatcher = blockPattern.matcher(content);
        if (!blockMatcher.find()) {
            return 0;
        }
        String block = blockMatcher.group(1);
        Matcher elementMatcher = elementPattern.matcher(block);
        int count = 0;
        while (elementMatcher.find()) {
            count++;
        }
        return count;
    }
}

