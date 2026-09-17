package com.fhfelipefh.sandstorm.metrics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameMetricsTrackerTest {

    @Test
    void shouldTrackEnergyGenerationAndConsumption() {
        GameMetricsTracker tracker = new GameMetricsTracker();

        tracker.recordEnergyGenerated(1000);
        tracker.recordEnergyConsumed(400);

        assertEquals(1000, tracker.getTotalEnergyGenerated());
        assertEquals(400, tracker.getTotalEnergyConsumed());
        assertEquals(0.4, tracker.calculateEnergyEfficiencyRatio(), 0.001);
    }

    @Test
    void shouldTrackWaterFiltering() {
        GameMetricsTracker tracker = new GameMetricsTracker();

        tracker.recordWaterFiltered(2500);

        assertEquals(2500, tracker.getTotalWaterFilteredMillibuckets());
    }

    @Test
    void shouldCalculateTerraformingProgress() {
        GameMetricsTracker tracker = new GameMetricsTracker();

        tracker.recordTerraformedBlock();
        tracker.recordTerraformedBlock();

        assertEquals(2, tracker.getTotalSandBlocksTerraformed());
        assertEquals(0.2, tracker.calculateTerraformingProgress(10), 0.001);
    }

    @Test
    void shouldTrackWormAttacksAndRuins() {
        GameMetricsTracker tracker = new GameMetricsTracker();

        tracker.recordWormAttack();
        tracker.recordRuinDiscovered();

        assertEquals(1, tracker.getWormAttacksEncountered());
        assertEquals(1, tracker.getAncientRuinsDiscovered());
    }
}
