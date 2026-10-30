package com.fhfelipefh.sandstorm.content.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ShowcasePistonPowerArchitectureTest {
    private static final Path SHOWCASE_AUTOMATION = Path.of("src/main/java/com/fhfelipefh/sandstorm/content/world/ShowcaseAutomation.java");

    @Test
    void everyShowcasePistonMustUseThePoweredRedstoneControl() throws IOException {
        String source = Files.readString(SHOWCASE_AUTOMATION);

        assertEquals(6, countOccurrences(source, "installPistonPowerControl(level, p"));
        assertTrue(source.contains("Blocks.REDSTONE_WIRE.defaultBlockState()"));
        assertTrue(source.contains("controlBlock.above(), Blocks.LEVER.defaultBlockState()"));
        assertTrue(source.contains("pistonPos.north(), Blocks.REDSTONE_WIRE.defaultBlockState()"));
    }

    private int countOccurrences(String source, String target) {
        int occurrences = 0;
        int index = 0;
        while ((index = source.indexOf(target, index)) >= 0) {
            occurrences++;
            index += target.length();
        }
        return occurrences;
    }
}
