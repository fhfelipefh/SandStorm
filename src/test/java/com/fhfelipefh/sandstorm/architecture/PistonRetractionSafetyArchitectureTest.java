package com.fhfelipefh.sandstorm.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class PistonRetractionSafetyArchitectureTest {
    private static final List<Path> PISTON_BLOCK_FILES = List.of(
            Path.of("src/main/java/com/fhfelipefh/sandstorm/content/block/HeavyKineticPistonBlock.java"),
            Path.of("src/main/java/com/fhfelipefh/sandstorm/content/block/CompactSlabPistonBlock.java"),
            Path.of("src/main/java/com/fhfelipefh/sandstorm/content/block/TectonicKineticPistonBlock.java")
    );

    private static final List<Path> PISTON_HEAD_FILES = List.of(
            Path.of("src/main/java/com/fhfelipefh/sandstorm/content/block/HeavyKineticPistonHeadBlock.java"),
            Path.of("src/main/java/com/fhfelipefh/sandstorm/content/block/CompactSlabPistonHeadBlock.java"),
            Path.of("src/main/java/com/fhfelipefh/sandstorm/content/block/TectonicKineticPistonHeadBlock.java")
    );

    @Test
    void pistonBlocksMustUnextendBaseBeforeClearingHead() throws IOException {
        for (Path file : PISTON_BLOCK_FILES) {
            String content = Files.readString(file);
            int contractIdx = Math.max(content.indexOf("id == TRIGGER_CONTRACT"), content.indexOf("id == 1"));
            assertTrue(contractIdx > 0, "Piston contraction handler missing in " + file);
            int setUnextendedIdx = content.indexOf("state.setValue(EXTENDED, false)", contractIdx);
            int removeHeadIdx = content.indexOf("removeBlock(headPos", contractIdx);
            assertTrue(setUnextendedIdx > 0, "Unextending base state missing in " + file);
            assertTrue(removeHeadIdx > 0, "Removing head missing in " + file);
            assertTrue(setUnextendedIdx < removeHeadIdx, "Base block must be marked unextended before head is removed in " + file);
        }
    }

    @Test
    void pistonHeadsMustGuardAgainstDestructionWhenMovedByPiston() throws IOException {
        for (Path file : PISTON_HEAD_FILES) {
            String content = Files.readString(file);
            int affectNeighborsIdx = content.indexOf("affectNeighborsAfterRemoval");
            assertTrue(affectNeighborsIdx > 0, "affectNeighborsAfterRemoval missing in " + file);
            int guardIdx = content.indexOf("if (!movedByPiston)", affectNeighborsIdx);
            assertTrue(guardIdx > 0, "movedByPiston guard missing in " + file);
            int destroyIdx = content.indexOf("destroyBlock(basePos", affectNeighborsIdx);
            assertTrue(destroyIdx > guardIdx, "destroyBlock must be guarded by movedByPiston in " + file);
        }
    }
}
