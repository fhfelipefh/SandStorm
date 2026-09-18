package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeismicSurvivalHandlerTest {

    @AfterEach
    void tearDown() {
        SandstormWeatherHandler.resetWeather();
    }

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

    @Test
    void movementVibrationMultiplierShouldBeFullWhenWeatherInactive() {
        SandstormWeatherHandler.resetWeather();
        assertEquals(1.0, SeismicSurvivalHandler.getMovementVibrationMultiplier(), 0.001);
    }

    @Test
    void movementVibrationMultiplierShouldBeHalvedDuringActiveStorm() {
        SandstormWeatherHandler.triggerSandstorm(100, 1.0);
        for (int i = 0; i < 50; i++) {
            SandstormWeatherHandler.getWeather().tick();
        }
        assertEquals(0.50, SeismicSurvivalHandler.getMovementVibrationMultiplier(), 0.001);
    }
}
