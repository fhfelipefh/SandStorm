package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnergyStorageComponentTest {

    @Test
    void shouldInitializeWithZeroStoredEnergy() {
        EnergyStorageComponent storage = new EnergyStorageComponent(1000, 50);

        assertEquals(1000, storage.getCapacity());
        assertEquals(0, storage.getStoredEnergy());
        assertTrue(storage.isEmpty());
        assertFalse(storage.isFull());
    }

    @Test
    void shouldThrowWhenCapacityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new EnergyStorageComponent(-10, 50));
    }

    @Test
    void shouldReceiveEnergyWithinRateAndCapacity() {
        EnergyStorageComponent storage = new EnergyStorageComponent(100, 20);

        long receivedFirst = storage.receiveEnergy(15);
        assertEquals(15, receivedFirst);
        assertEquals(15, storage.getStoredEnergy());

        long receivedCappedByRate = storage.receiveEnergy(30);
        assertEquals(20, receivedCappedByRate);
        assertEquals(35, storage.getStoredEnergy());
    }

    @Test
    void shouldNotExceedCapacityOnReceive() {
        EnergyStorageComponent storage = new EnergyStorageComponent(50, 100);

        long received = storage.receiveEnergy(80);
        assertEquals(50, received);
        assertEquals(50, storage.getStoredEnergy());
        assertTrue(storage.isFull());
    }

    @Test
    void shouldExtractEnergyWithinRateAndAvailable() {
        EnergyStorageComponent storage = new EnergyStorageComponent(100, 50);
        storage.setStoredEnergy(50);

        long extracted = storage.extractEnergy(20);
        assertEquals(20, extracted);
        assertEquals(30, storage.getStoredEnergy());

        long extractedLimitedByRate = storage.extractEnergy(60);
        assertEquals(30, extractedLimitedByRate);
        assertEquals(0, storage.getStoredEnergy());
        assertTrue(storage.isEmpty());
    }

    @Test
    void shouldTransferEnergyBetweenStorages() {
        EnergyStorageComponent source = new EnergyStorageComponent(100, 50);
        EnergyStorageComponent target = new EnergyStorageComponent(100, 50);

        source.setStoredEnergy(80);

        long transferred = source.transferTo(target, 40);
        assertEquals(40, transferred);
        assertEquals(40, source.getStoredEnergy());
        assertEquals(40, target.getStoredEnergy());
    }

    @Test
    void shouldCalculateEnergyRatioCorrectly() {
        EnergyStorageComponent storage = new EnergyStorageComponent(200, 100);
        storage.setStoredEnergy(100);

        assertEquals(0.5, storage.getEnergyRatio(), 0.001);
    }
}
