package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrackishWaterComponentTest {

    private BrackishWaterComponent rawWater;

    @BeforeEach
    void setUp() {
        rawWater = new BrackishWaterComponent(8.5, 0.45, 1000);
    }

    @Test
    void rawAquiferWaterShouldNotBeSafeForDirectConsumption() {
        assertFalse(rawWater.isSafeForDirectConsumption());
        assertEquals(8.5, rawWater.getSalinityPercentage(), 0.001);
        assertEquals(0.45, rawWater.getMineralSedimentLevel(), 0.001);
        assertEquals(1000, rawWater.getVolumeMillibuckets());
    }

    @Test
    void cleanWaterBelowThresholdShouldBeSafeForConsumption() {
        BrackishWaterComponent potable = new BrackishWaterComponent(0.2, 0.01, 1000);
        assertTrue(potable.isSafeForDirectConsumption());
        assertEquals(0, potable.calculateToxicityDurationTicks());
    }

    @Test
    void toxicityDurationTicksShouldReflectSalinityAndSediment() {
        int duration = rawWater.calculateToxicityDurationTicks();
        assertEquals(345, duration);
    }

    @Test
    void filtrationShouldYieldPotableWaterAndSaltByproduct() {
        BrackishWaterComponent.FilterResult result = rawWater.filter(0.9);
        assertEquals(855, result.potableWaterOutputMb());
        assertEquals(76.5, result.extractedSaltGrams(), 0.01);
        assertEquals(0.85, result.residualSalinity(), 0.01);
    }

    @Test
    void perfectFiltrationShouldLeaveZeroResidualSalinity() {
        BrackishWaterComponent.FilterResult result = rawWater.filter(1.0);
        assertEquals(950, result.potableWaterOutputMb());
        assertEquals(85.0, result.extractedSaltGrams(), 0.01);
        assertEquals(0.0, result.residualSalinity(), 0.001);
    }
}
