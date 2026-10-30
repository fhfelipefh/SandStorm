package com.fhfelipefh.sandstorm.content.world;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ShowcaseLayoutGeometryTest {

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
}
