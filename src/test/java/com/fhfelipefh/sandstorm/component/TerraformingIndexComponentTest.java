package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerraformingIndexComponentTest {
    private TerraformingIndexComponent terraforming;

    @BeforeEach
    void setUp() {
        terraforming = new TerraformingIndexComponent();
    }

    @Test
    void shouldInitializeAtZeroProgress() {
        assertEquals(0.0, terraforming.getProgress(), 0.001);
        assertEquals(TerraformingIndexComponent.Stage.ARID_DESERT, terraforming.getStage());
        assertEquals(6, terraforming.getDomeRadius());
        assertEquals(5.0, terraforming.getHumidity(), 0.001);
        assertEquals(48.0, terraforming.getTemperatureCelsius(), 0.001);
        assertFalse(terraforming.isWormDispersalActive());
        assertFalse(terraforming.isComplete());
    }

    @Test
    void shouldAdvanceStagesWithProgress() {
        terraforming.addProgress(30.0);
        assertEquals(30.0, terraforming.getProgress(), 0.001);
        assertEquals(TerraformingIndexComponent.Stage.CONDENSATION_INITIATED, terraforming.getStage());
        assertEquals(10, terraforming.getDomeRadius());
        assertFalse(terraforming.isWormDispersalActive());

        terraforming.addProgress(25.0);
        assertEquals(55.0, terraforming.getProgress(), 0.001);
        assertEquals(TerraformingIndexComponent.Stage.PRECIPITATION_ACTIVE, terraforming.getStage());
        assertEquals(14, terraforming.getDomeRadius());
        assertTrue(terraforming.isWormDispersalActive());

        terraforming.addProgress(30.0);
        assertEquals(85.0, terraforming.getProgress(), 0.001);
        assertEquals(TerraformingIndexComponent.Stage.LUSH_BIOSPHERE, terraforming.getStage());
        assertEquals(18, terraforming.getDomeRadius());
        assertTrue(terraforming.isWormDispersalActive());
    }

    @Test
    void shouldClampProgressAtOneHundred() {
        terraforming.addProgress(150.0);
        assertEquals(100.0, terraforming.getProgress(), 0.001);
        assertEquals(85.0, terraforming.getHumidity(), 0.001);
        assertEquals(22.0, terraforming.getTemperatureCelsius(), 0.001);
        assertTrue(terraforming.isComplete());
        assertTrue(terraforming.isWormDispersalActive());
    }

    @Test
    void shouldIgnoreNegativeProgressIncrements() {
        terraforming.addProgress(50.0);
        terraforming.addProgress(-20.0);
        assertEquals(50.0, terraforming.getProgress(), 0.001);
    }
}
