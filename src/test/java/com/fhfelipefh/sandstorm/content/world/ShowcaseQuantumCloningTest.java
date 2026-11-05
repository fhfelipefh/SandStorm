package com.fhfelipefh.sandstorm.content.world;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowcaseQuantumCloningTest {

    @BeforeAll
    static void bootstrapRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void cloningStationMustBeRegisteredInShowcaseFootprints() {
        List<ShowcaseAutomation.ShowcaseFootprint> footprints = ShowcaseAutomation.getShowcaseAttractionFootprints();
        ShowcaseAutomation.ShowcaseFootprint cloneSector = footprints.stream()
                .filter(footprint -> footprint.name().equals("Laboratório de Clonagem"))
                .findFirst()
                .orElse(null);

        assertNotNull(cloneSector);
        assertEquals(80, cloneSector.minX());
        assertEquals(104, cloneSector.maxX());
        assertEquals(34, cloneSector.minZ());
        assertEquals(54, cloneSector.maxZ());
    }

    @Test
    void cloningStationMustNeverOverlapOtherAttractions() {
        List<ShowcaseAutomation.ShowcaseFootprint> footprints = ShowcaseAutomation.getShowcaseAttractionFootprints();
        ShowcaseAutomation.ShowcaseFootprint cloneSector = footprints.stream()
                .filter(footprint -> footprint.name().equals("Laboratório de Clonagem"))
                .findFirst()
                .orElseThrow();

        for (ShowcaseAutomation.ShowcaseFootprint other : footprints) {
            if (!other.name().equals(cloneSector.name())) {
                assertFalse(cloneSector.intersects(other), () -> cloneSector.name() + " intersects " + other.name());
            }
        }
    }

    @Test
    void layoutValidationMustAcceptCloningStation() {
        assertDoesNotThrow(ShowcaseAutomation::validateShowcaseLayout);
    }

    @Test
    void showcaseAutomationMustDeclareCloningCommandsAndStations() throws IOException {
        Path showcaseSource = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "world", "ShowcaseAutomation.java");
        assertTrue(Files.exists(showcaseSource));
        String code = Files.readString(showcaseSource);

        assertTrue(code.contains("executeCloneCommand"));
        assertTrue(code.contains("buildQuantumCloningStation"));
        assertTrue(code.contains("Commands.literal(\"clone\")"));
        assertTrue(code.contains("Commands.literal(\"sleeper\")"));
        assertTrue(code.contains("Setor Leste: Laboratório de Clonagem"));
        assertTrue(code.contains("Câmaras Quânticas & Vidas Hardcore"));
        assertTrue(code.contains("Câmara Alfa (2ª Vida)"));
        assertTrue(code.contains("Câmara Beta (3ª Vida)"));
        assertTrue(code.contains("Câmara Gamma (Pronta p/ 4ª Vida)"));
        assertTrue(code.contains("CHITOSAN_EXTRACT"));
        assertTrue(code.contains("TREHALOSE_SUGAR"));
        assertTrue(code.contains("OSMOLYTE_GLYCEROL"));
        assertTrue(code.contains("POTABLE_WATER_BOTTLE"));
        assertTrue(code.contains("QUANTUM_MIND_MATRIX"));
    }
}
