package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuitPowerComponentTest {

    @Test
    void shouldInitializeWithDefaultValues() {
        SuitPowerComponent suit = new SuitPowerComponent();

        assertEquals(100000, suit.getEnergyStorage().getCapacity());
        assertEquals(0, suit.getEnergyStorage().getStoredEnergy());
        assertEquals(0, suit.getEquippedArmorCount());
        assertFalse(suit.isFullSuitEquipped());
        assertFalse(suit.hasLifeSupportActive());
    }

    @Test
    void shouldTrackFullSuitEquipped() {
        SuitPowerComponent suit = new SuitPowerComponent();

        suit.updateEquippedArmorCount(3);
        assertFalse(suit.isFullSuitEquipped());

        suit.updateEquippedArmorCount(4);
        assertTrue(suit.isFullSuitEquipped());
    }

    @Test
    void shouldRechargeFromSunlightWhenAboveGround() {
        SuitPowerComponent suit = new SuitPowerComponent(1000, 50, 5, 10);

        suit.tick(true, 37.0, false);
        assertEquals(50, suit.getEnergyStorage().getStoredEnergy());

        suit.tick(true, 37.0, true);
        assertEquals(50, suit.getEnergyStorage().getStoredEnergy());
    }

    @Test
    void shouldConsumeIdleEnergyWhenArmorEquipped() {
        SuitPowerComponent suit = new SuitPowerComponent(1000, 0, 10, 5);
        suit.getEnergyStorage().setStoredEnergy(100);
        suit.updateEquippedArmorCount(2);

        suit.tick(false, 37.0, true);
        assertEquals(90, suit.getEnergyStorage().getStoredEnergy());
    }

    @Test
    void shouldRegulateTemperatureWhenExtremeAndHasEnergy() {
        SuitPowerComponent suit = new SuitPowerComponent(1000, 0, 0, 10);
        suit.getEnergyStorage().setStoredEnergy(100);
        suit.updateEquippedArmorCount(4);
        suit.getThermal().setCurrentTemperature(55.0);

        suit.tick(false, 55.0, false);

        assertEquals(90, suit.getEnergyStorage().getStoredEnergy());
        assertTrue(suit.getThermal().getCurrentTemperature() < 55.0);
    }

    @Test
    void shouldRequireFullSuitAndEnergyForActiveLifeSupport() {
        SuitPowerComponent suit = new SuitPowerComponent(1000, 0, 0, 0);

        suit.getEnergyStorage().setStoredEnergy(500);
        suit.updateEquippedArmorCount(3);
        assertFalse(suit.hasLifeSupportActive());

        suit.updateEquippedArmorCount(4);
        assertTrue(suit.hasLifeSupportActive());

        suit.getEnergyStorage().setStoredEnergy(0);
        assertFalse(suit.hasLifeSupportActive());
    }

    @Test
    void shouldScaleInsulationFactorWithArmorPieces() {
        SuitPowerComponent suit = new SuitPowerComponent();

        suit.updateEquippedArmorCount(0);
        assertEquals(0.0, suit.calculateInsulationFactor(), 0.001);

        suit.updateEquippedArmorCount(4);
        assertEquals(0.88, suit.calculateInsulationFactor(), 0.001);
    }
}
