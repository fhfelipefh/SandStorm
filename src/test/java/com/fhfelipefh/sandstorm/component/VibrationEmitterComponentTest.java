package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VibrationEmitterComponentTest {

    @Test
    void shouldInitializeWithZeroIntensity() {
        VibrationEmitterComponent emitter = new VibrationEmitterComponent(40.0, 100.0);

        assertEquals(40.0, emitter.getBaseFrequency(), 0.001);
        assertEquals(100.0, emitter.getMaxIntensity(), 0.001);
        assertEquals(0.0, emitter.getCurrentIntensity(), 0.001);
    }

    @Test
    void shouldThrowWhenNegativeFrequencyOrIntensity() {
        assertThrows(IllegalArgumentException.class, () -> new VibrationEmitterComponent(-5.0, 100.0));
        assertThrows(IllegalArgumentException.class, () -> new VibrationEmitterComponent(40.0, -10.0));
    }

    @Test
    void shouldEmitAndCapAtMaxIntensity() {
        VibrationEmitterComponent emitter = new VibrationEmitterComponent(40.0, 50.0);

        emitter.emitVibration(30.0);
        assertEquals(30.0, emitter.getCurrentIntensity(), 0.001);

        emitter.emitVibration(30.0);
        assertEquals(50.0, emitter.getCurrentIntensity(), 0.001);
    }

    @Test
    void shouldDecayIntensity() {
        VibrationEmitterComponent emitter = new VibrationEmitterComponent(40.0, 50.0);
        emitter.emitVibration(40.0);

        emitter.decay(15.0);
        assertEquals(25.0, emitter.getCurrentIntensity(), 0.001);

        emitter.decay(30.0);
        assertEquals(0.0, emitter.getCurrentIntensity(), 0.001);
    }

    @Test
    void shouldCalculateEffectiveVibrationWithDampening() {
        VibrationEmitterComponent emitter = new VibrationEmitterComponent(40.0, 100.0);
        emitter.emitVibration(80.0);

        double effective = emitter.calculateEffectiveVibration(0.5);
        assertEquals(40.0, effective, 0.001);

        assertTrue(emitter.exceedsThreshold(35.0, 0.5));
        assertFalse(emitter.exceedsThreshold(45.0, 0.5));
    }
}
