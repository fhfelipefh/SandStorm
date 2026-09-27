package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandstormWeatherComponentTest {

    private SandstormWeatherComponent weather;

    @BeforeEach
    void setUp() {
        weather = new SandstormWeatherComponent();
    }

    @Test
    void initialStateShouldBeInactive() {
        assertFalse(weather.isActive());
        assertEquals(0.0, weather.getIntensity(), 0.001);
        assertEquals(1.0, weather.getSolarEfficiencyMultiplier(), 0.001);
        assertEquals(1.0, weather.getFogDistanceMultiplier(), 0.001);
        assertEquals(1.0, weather.getVibrationDampingFactor(), 0.001);
    }

    @Test
    void startSandstormShouldActivateAndSetRemainingTicks() {
        weather.startSandstorm(100, 0.8);
        assertTrue(weather.isActive());
        assertEquals(100, weather.getRemainingTicks());
        assertEquals(0.8, weather.getTargetIntensity(), 0.001);
    }

    @Test
    void tickShouldGraduallyInterpolateIntensityTowardsTarget() {
        weather.startSandstorm(50, 0.1);
        assertEquals(0.0, weather.getIntensity(), 0.001);

        weather.tick();
        assertEquals(0.02, weather.getIntensity(), 0.001);

        weather.tick();
        assertEquals(0.04, weather.getIntensity(), 0.001);
    }

    @Test
    void solarEfficiencyMultiplierShouldDecreaseWithIntensity() {
        weather.startSandstorm(100, 1.0);
        for (int i = 0; i < 50; i++) {
            weather.tick();
        }
        assertEquals(1.0, weather.getIntensity(), 0.001);
        assertEquals(0.15, weather.getSolarEfficiencyMultiplier(), 0.001);
    }

    @Test
    void fogDistanceMultiplierShouldDecreaseWithIntensity() {
        weather.startSandstorm(100, 1.0);
        for (int i = 0; i < 50; i++) {
            weather.tick();
        }
        assertEquals(0.20, weather.getFogDistanceMultiplier(), 0.001);
    }

    @Test
    void vibrationDampingFactorShouldReflectMuffledVibrations() {
        weather.startSandstorm(100, 1.0);
        for (int i = 0; i < 50; i++) {
            weather.tick();
        }
        assertEquals(0.50, weather.getVibrationDampingFactor(), 0.001);
    }

    @Test
    void externalAudioDampingFactorShouldReflectMuffledAudio() {
        weather.startSandstorm(100, 1.0);
        for (int i = 0; i < 50; i++) {
            weather.tick();
        }
        assertEquals(0.60, weather.getExternalAudioDampingFactor(), 0.001);
    }

    @Test
    void sandDamageShouldTriggerAtOrAbove75PercentIntensity() {
        weather.startSandstorm(100, 0.7);
        for (int i = 0; i < 40; i++) {
            weather.tick();
        }
        assertFalse(weather.canCauseSandDamage());

        weather.startSandstorm(100, 0.8);
        for (int i = 0; i < 40; i++) {
            weather.tick();
        }
        assertTrue(weather.canCauseSandDamage());
    }

    @Test
    void stopSandstormShouldResetTargetAndFadeOut() {
        weather.startSandstorm(100, 0.5);
        for (int i = 0; i < 25; i++) {
            weather.tick();
        }
        assertEquals(0.5, weather.getIntensity(), 0.001);

        weather.stopSandstorm();
        assertEquals(0, weather.getRemainingTicks());
        assertEquals(0.0, weather.getTargetIntensity(), 0.001);

        weather.tick();
        assertEquals(0.48, weather.getIntensity(), 0.001);
    }

    @Test
    void expirationShouldTransitionActiveToFalse() {
        weather.startSandstorm(2, 0.5);
        weather.tick();
        weather.tick();
        weather.tick();
        assertEquals(0, weather.getRemainingTicks());
        assertEquals(0.0, weather.getTargetIntensity(), 0.001);
    }
}
