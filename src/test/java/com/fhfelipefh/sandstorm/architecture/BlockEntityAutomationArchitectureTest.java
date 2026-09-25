package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockEntityAutomationArchitectureTest {

    private static final Pattern SLOT_PATTERN = Pattern.compile(
            "public\\s+static\\s+final\\s+int\\s+(SLOT_[A-Z0-9_]+)\\s*=\\s*(\\d+);"
    );

    @Test
    void allMachineBlockEntitiesMustHaveUniqueSlotIndices() throws IOException {
        Path beDir = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "entity");
        assertTrue(Files.exists(beDir), "Block entity directory must exist");

        List<String> violations = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(beDir)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    Matcher matcher = SLOT_PATTERN.matcher(content);
                    Map<Integer, String> seenSlots = new HashMap<>();

                    while (matcher.find()) {
                        String slotName = matcher.group(1);
                        int slotIndex = Integer.parseInt(matcher.group(2));

                        if (seenSlots.containsKey(slotIndex)) {
                            violations.add(path.getFileName() + ": duplicate slot index " + slotIndex
                                    + " used by both " + seenSlots.get(slotIndex) + " and " + slotName);
                        } else {
                            seenSlots.put(slotIndex, slotName);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found duplicate slot index violations:\n" + String.join("\n", violations));
    }

    @Test
    void worldlyContainersMustImplementFaceAutomationFiltering() throws IOException {
        Path beDir = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "block", "entity");
        assertTrue(Files.exists(beDir), "Block entity directory must exist");

        List<String> violations = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(beDir)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    boolean isWorldly = content.contains("implements WorldlyContainer") || content.contains("extends BaseMachineBlockEntity");
                    boolean isBaseMachine = path.getFileName().toString().equals("BaseMachineBlockEntity.java");

                    if (isWorldly && !isBaseMachine) {
                        boolean hasCanPlace = content.contains("canPlaceItemThroughFace");
                        boolean hasCanTake = content.contains("canTakeItemThroughFace");

                        if (!hasCanPlace) {
                            violations.add(path.getFileName() + " must implement canPlaceItemThroughFace to prevent invalid automation insertion");
                        }
                        if (!hasCanTake) {
                            violations.add(path.getFileName() + " must implement canTakeItemThroughFace to control hopper extraction");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(), "Found automation face filtering violations:\n" + String.join("\n", violations));
    }
}
