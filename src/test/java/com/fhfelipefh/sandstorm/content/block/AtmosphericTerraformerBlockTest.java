package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtmosphericTerraformerBlockTest {

    @Test
    void shouldDefineAtmosphericTerraformerSpecifications() {
        assertEquals(250000L, AtmosphericTerraformerBlock.DEFAULT_CAPACITY);
        assertEquals(2500L, AtmosphericTerraformerBlock.DEFAULT_TRANSFER_RATE);
        assertEquals(100000L, AtmosphericTerraformerBlock.INITIAL_CHARGE);
        assertEquals(2000L, AtmosphericTerraformerBlock.ENERGY_PER_CYCLE);

        EnergyStorageComponent storage = new EnergyStorageComponent(
                AtmosphericTerraformerBlock.DEFAULT_CAPACITY,
                AtmosphericTerraformerBlock.DEFAULT_TRANSFER_RATE
        );
        storage.setStoredEnergy(AtmosphericTerraformerBlock.INITIAL_CHARGE);

        assertEquals(100000L, storage.getStoredEnergy());
        assertTrue(storage.hasEnergy(AtmosphericTerraformerBlock.ENERGY_PER_CYCLE));
    }
}
