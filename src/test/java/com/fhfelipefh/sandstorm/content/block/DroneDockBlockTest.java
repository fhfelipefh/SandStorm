package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DroneDockBlockTest {

    @Test
    void shouldDefineStandardDockSpecifications() {
        assertEquals(50000L, DroneDockBlock.DEFAULT_CAPACITY);
        assertEquals(500L, DroneDockBlock.DEFAULT_TRANSFER_RATE);
        assertEquals(25000L, DroneDockBlock.INITIAL_CHARGE);

        EnergyStorageComponent storage = new EnergyStorageComponent(
                DroneDockBlock.DEFAULT_CAPACITY,
                DroneDockBlock.DEFAULT_TRANSFER_RATE
        );
        storage.setStoredEnergy(DroneDockBlock.INITIAL_CHARGE);

        assertEquals(25000L, storage.getStoredEnergy());
        assertTrue(storage.hasEnergy(1000L));
    }
}
