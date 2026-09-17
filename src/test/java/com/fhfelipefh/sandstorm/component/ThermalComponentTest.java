package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThermalComponentTest {

    @Test
    void shouldInitializeAtOptimalTemperature() {
        ThermalComponent thermal = new ThermalComponent(37.0, 10.0, 50.0);

        assertEquals(37.0, thermal.getCurrentTemperature(), 0.001);
        assertTrue(thermal.isSafe());
        assertFalse(thermal.isOverheating());
        assertFalse(thermal.isFreezing());
    }

    @Test
    void shouldThrowWhenThresholdsAreInconsistent() {
        assertThrows(IllegalArgumentException.class, () -> new ThermalComponent(60.0, 10.0, 50.0));
    }

    @Test
    void shouldAdjustTemperatureTowardAmbientWithInsulation() {
        ThermalComponent thermal = new ThermalComponent(37.0, 0.0, 60.0);

        thermal.adjustTemperature(57.0, 0.5);
        assertEquals(47.0, thermal.getCurrentTemperature(), 0.001);
    }

    @Test
    void shouldRegulateTowardOptimalTemperature() {
        ThermalComponent thermal = new ThermalComponent(37.0, 0.0, 60.0);
        thermal.setCurrentTemperature(45.0);

        double regulated = thermal.regulateTowardOptimal(5.0);
        assertEquals(5.0, regulated, 0.001);
        assertEquals(40.0, thermal.getCurrentTemperature(), 0.001);

        thermal.regulateTowardOptimal(10.0);
        assertEquals(37.0, thermal.getCurrentTemperature(), 0.001);
    }

    @Test
    void shouldDetectOverheatingAndFreezing() {
        ThermalComponent thermal = new ThermalComponent(37.0, 15.0, 45.0);

        thermal.setCurrentTemperature(50.0);
        assertTrue(thermal.isOverheating());
        assertFalse(thermal.isSafe());

        thermal.setCurrentTemperature(10.0);
        assertTrue(thermal.isFreezing());
        assertFalse(thermal.isSafe());
    }
}
