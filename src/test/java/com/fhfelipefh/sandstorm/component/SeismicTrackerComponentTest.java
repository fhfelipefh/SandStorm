package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeismicTrackerComponentTest {

    private SeismicTrackerComponent tracker;

    @BeforeEach
    void setUp() {
        tracker = new SeismicTrackerComponent(0, 0, 96.0);
    }

    @Test
    void shouldRecognizeSafeZoneBoundaries() {
        assertTrue(tracker.isInsideSafeZone(0, 0));
        assertTrue(tracker.isInsideSafeZone(50, 50));
        assertTrue(tracker.isInsideSafeZone(96, 0));
        assertFalse(tracker.isInsideSafeZone(97, 0));
        assertFalse(tracker.isInsideSafeZone(100, 100));
    }

    @Test
    void shouldAccumulateAndRetrieveVibrationsByChunk() {
        tracker.addVibration(10, 10, 25.0);
        tracker.addVibration(10, 10, 35.0);
        assertEquals(60.0, tracker.getVibration(10, 10), 0.001);
        assertEquals(0.0, tracker.getVibration(5, 5), 0.001);
    }

    @Test
    void shouldDecayVibrationsAndRemoveZeroValues() {
        tracker.addVibration(10, 10, 10.0);
        tracker.decayAll(4.0);
        assertEquals(6.0, tracker.getVibration(10, 10), 0.001);

        tracker.decayAll(7.0);
        assertEquals(0.0, tracker.getVibration(10, 10), 0.001);
        assertTrue(tracker.getActiveVibrations().isEmpty());
    }

    @Test
    void shouldTriggerWormAttackOutsideSafeZoneWhenThresholdExceeded() {
        tracker.addVibration(20, 20, 100.0);
        assertTrue(tracker.isWormAttackTriggered(20, 20, 80.0));
        assertFalse(tracker.isWormAttackTriggered(20, 20, 120.0));
    }

    @Test
    void shouldNotTriggerWormAttackInsideSafeZoneEvenWithHighVibration() {
        tracker.addVibration(0, 0, 500.0);
        assertFalse(tracker.isWormAttackTriggered(0, 0, 50.0));
    }

    @Test
    void shouldClearAndResetVibrations() {
        tracker.addVibration(15, 15, 50.0);
        tracker.clearChunkVibration(15, 15);
        assertEquals(0.0, tracker.getVibration(15, 15), 0.001);

        tracker.addVibration(20, 20, 50.0);
        tracker.reset();
        assertEquals(0.0, tracker.getVibration(20, 20), 0.001);
    }
}
