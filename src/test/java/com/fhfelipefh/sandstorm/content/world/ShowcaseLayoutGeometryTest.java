package com.fhfelipefh.sandstorm.content.world;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowcaseLayoutGeometryTest {

    @BeforeAll
    static void bootstrapRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void majorAttractionsMustNeverShareFootprint() {
        List<ShowcaseAutomation.ShowcaseFootprint> footprints = ShowcaseAutomation.getShowcaseAttractionFootprints();

        for (int index = 0; index < footprints.size(); index++) {
            for (int otherIndex = index + 1; otherIndex < footprints.size(); otherIndex++) {
                ShowcaseAutomation.ShowcaseFootprint first = footprints.get(index);
                ShowcaseAutomation.ShowcaseFootprint second = footprints.get(otherIndex);
                assertFalse(first.intersects(second), () -> first.name() + " overlaps " + second.name());
            }
        }
    }

    @Test
    void layoutValidatorMustAcceptTheDeclaredPlan() {
        assertDoesNotThrow(ShowcaseAutomation::validateShowcaseLayout);
    }

    @Test
    void pistonFieldMustHaveAVisibleBufferBeforeTheRappelStation() {
        List<ShowcaseAutomation.ShowcaseFootprint> footprints = ShowcaseAutomation.getShowcaseAttractionFootprints();
        ShowcaseAutomation.ShowcaseFootprint pistons = footprints.stream()
                .filter(footprint -> footprint.name().equals("Campo de Pistões"))
                .findFirst()
                .orElseThrow();
        ShowcaseAutomation.ShowcaseFootprint rappel = footprints.stream()
                .filter(footprint -> footprint.name().equals("Estação de Rapel"))
                .findFirst()
                .orElseThrow();

        assertTrue(pistons.maxZ() <= rappel.minZ() - 16);
    }
}
