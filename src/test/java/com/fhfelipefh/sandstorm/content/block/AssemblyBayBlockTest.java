package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssemblyBayBlockTest {

    @Test
    void shouldDefineAssemblyBaySpecifications() {
        assertEquals(100000L, AssemblyBayBlock.DEFAULT_CAPACITY);
        assertEquals(1000L, AssemblyBayBlock.DEFAULT_TRANSFER_RATE);
        assertEquals(50000L, AssemblyBayBlock.INITIAL_CHARGE);

        EnergyStorageComponent storage = new EnergyStorageComponent(
                AssemblyBayBlock.DEFAULT_CAPACITY,
                AssemblyBayBlock.DEFAULT_TRANSFER_RATE
        );
        storage.setStoredEnergy(AssemblyBayBlock.INITIAL_CHARGE);

        assertEquals(50000L, storage.getStoredEnergy());
        assertTrue(storage.hasEnergy(5000L));
    }
}
