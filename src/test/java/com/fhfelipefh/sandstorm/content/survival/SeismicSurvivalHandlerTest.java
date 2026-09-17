package com.fhfelipefh.sandstorm.content.survival;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeismicSurvivalHandlerTest {

    @Test
    void trackerShouldBeInitializedWithDefaultSafeZone() {
        assertNotNull(SeismicSurvivalHandler.getTracker());
        assertEquals(96.0, SeismicSurvivalHandler.getTracker().getSafeZoneRadiusBlocks(), 0.001);
        assertTrue(SeismicSurvivalHandler.getTracker().isInsideSafeZone(0, 0));
    }

    @Test
    void recordVibrationShouldRegisterInTracker() {
        SeismicSurvivalHandler.getTracker().reset();
        SeismicSurvivalHandler.recordVibration(12, 14, 45.0);
        assertEquals(45.0, SeismicSurvivalHandler.getTracker().getVibration(12, 14), 0.001);
    }
}
