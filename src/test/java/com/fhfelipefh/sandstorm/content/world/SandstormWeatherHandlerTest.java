package com.fhfelipefh.sandstorm.content.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandstormWeatherHandlerTest {

    @BeforeEach
    void setUp() {
        SandstormWeatherHandler.stopSandstorm();
        SandstormWeatherHandler.setNextSandstormGameTime(1000);
    }

    @Test
    void shouldTriggerSandstormManually() {
        SandstormWeatherHandler.triggerSandstorm(500, 0.9);
        assertTrue(SandstormWeatherHandler.getWeather().isActive());
        assertEquals(500, SandstormWeatherHandler.getWeather().getRemainingTicks());
        assertEquals(0.9, SandstormWeatherHandler.getWeather().getTargetIntensity(), 0.001);
    }

    @Test
    void shouldTriggerAutomaticallyWhenGameTimeExceedsThreshold() {
        SandstormWeatherHandler.handleServerTick(500);
        assertFalse(SandstormWeatherHandler.getWeather().isActive());

        SandstormWeatherHandler.handleServerTick(1000);
        assertTrue(SandstormWeatherHandler.getWeather().isActive());
        assertEquals(15000, SandstormWeatherHandler.getNextSandstormGameTime());
    }

    @Test
    void stopSandstormShouldHaltWeather() {
        SandstormWeatherHandler.triggerSandstorm(100, 0.8);
        SandstormWeatherHandler.stopSandstorm();
        assertEquals(0, SandstormWeatherHandler.getWeather().getRemainingTicks());
    }
}
