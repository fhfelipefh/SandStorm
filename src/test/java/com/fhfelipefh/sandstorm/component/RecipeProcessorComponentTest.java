package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeProcessorComponentTest {

    private RecipeProcessorComponent processor;
    private EnergyStorageComponent energy;

    @BeforeEach
    void setUp() {
        processor = new RecipeProcessorComponent(20, 5);
        energy = new EnergyStorageComponent(1000, 500, 100);
        energy.receiveEnergy(500);
    }

    @Test
    void initialStateShouldBeIdle() {
        assertFalse(processor.isProcessing());
        assertFalse(processor.isComplete());
        assertEquals(0, processor.getCurrentProgressTicks());
        assertEquals(0.0, processor.getProgressPercentage(), 0.001);
    }

    @Test
    void startOperationShouldSetTicksAndEnergy() {
        processor.startOperation(40, 8);
        assertTrue(processor.isProcessing());
        assertEquals(40, processor.getTotalOperationTicks());
        assertEquals(8, processor.getEnergyCostPerTick());
    }

    @Test
    void canProcessRequiresActiveOperationAndSufficientEnergy() {
        assertFalse(processor.canProcess(energy));

        processor.startOperation(10, 5);
        assertTrue(processor.canProcess(energy));

        EnergyStorageComponent empty = new EnergyStorageComponent(1000, 100, 100);
        assertFalse(processor.canProcess(empty));
    }

    @Test
    void tickShouldConsumeEnergyAndAdvanceProgress() {
        processor.startOperation(10, 5);
        processor.tick(energy);

        assertEquals(495, energy.getStoredEnergy());
        assertEquals(1, processor.getCurrentProgressTicks());
        assertEquals(10.0, processor.getProgressPercentage(), 0.001);
    }

    @Test
    void progressShouldCompleteWhenDurationReached() {
        processor.startOperation(3, 5);
        processor.tick(energy);
        processor.tick(energy);
        processor.tick(energy);

        assertFalse(processor.isProcessing());
        assertTrue(processor.isComplete());
        assertEquals(100.0, processor.getProgressPercentage(), 0.001);
    }

    @Test
    void resetShouldClearProgressAndState() {
        processor.startOperation(10, 5);
        processor.tick(energy);
        processor.reset();

        assertFalse(processor.isProcessing());
        assertFalse(processor.isComplete());
        assertEquals(0, processor.getCurrentProgressTicks());
    }
}
