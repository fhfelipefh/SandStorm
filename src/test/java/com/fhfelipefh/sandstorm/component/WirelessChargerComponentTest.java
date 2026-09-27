package com.fhfelipefh.sandstorm.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WirelessChargerComponentTest {

    @Test
    void shouldInitializeWithCorrectTierProperties() {
        WirelessChargerComponent tier1 = new WirelessChargerComponent(1);
        assertEquals(1, tier1.getTier());
        assertEquals(24.0, tier1.getBaseRadius(), 0.001);
        assertEquals(50L, tier1.getBaseTransferRate());

        WirelessChargerComponent tier2 = new WirelessChargerComponent(2);
        assertEquals(2, tier2.getTier());
        assertEquals(36.0, tier2.getBaseRadius(), 0.001);
        assertEquals(100L, tier2.getBaseTransferRate());
    }

    @Test
    void shouldCalculateSunFactorCorrectly() {
        WirelessChargerComponent charger = new WirelessChargerComponent(1);

        assertFalse(charger.canHarvestSunlight(false, true));
        assertFalse(charger.canHarvestSunlight(true, false));
        assertTrue(charger.canHarvestSunlight(true, true));

        assertEquals(0.0, charger.calculateSunFactor(false, true, 0, 1.0), 0.001);
        assertEquals(0.0, charger.calculateSunFactor(true, false, 0, 1.0), 0.001);

        double peakSun = charger.calculateSunFactor(true, true, 0, 1.0);
        assertEquals(1.0, peakSun, 0.001);

        double stormSun = charger.calculateSunFactor(true, true, 0, 0.25);
        assertEquals(0.25, stormSun, 0.001);
    }

    @Test
    void shouldScaleEffectiveRadiusWithSunIntensity() {
        WirelessChargerComponent charger = new WirelessChargerComponent(1);

        assertEquals(0.0, charger.calculateEffectiveRadius(false, true, 0, 1.0), 0.001);
        assertEquals(24.0, charger.calculateEffectiveRadius(true, true, 0, 1.0), 0.001);

        WirelessChargerComponent chargerTier2 = new WirelessChargerComponent(2);
        assertEquals(36.0, chargerTier2.calculateEffectiveRadius(true, true, 0, 1.0), 0.001);
    }

    @Test
    void shouldChargeSuitDirectly() {
        WirelessChargerComponent charger = new WirelessChargerComponent(1);
        SuitPowerComponent suit = new SuitPowerComponent(1000, 5, 1, 1);
        suit.getEnergyStorage().setStoredEnergy(0);

        long charged = charger.chargeSuit(suit, 50L);
        assertEquals(50L, charged);
        assertEquals(50L, suit.getEnergyStorage().getStoredEnergy());
    }

    @Test
    void shouldMaintainStableTemperatureWhenBatteryIsActive() {
        SuitPowerComponent suit = new SuitPowerComponent(1000, 5, 1, 1);
        suit.getEnergyStorage().setStoredEnergy(500);
        suit.updateEquippedArmorCount(4);

        suit.tick(false, 48.0, false);
        assertEquals(37.0, suit.getThermal().getCurrentTemperature(), 0.001);
        assertTrue(suit.getEnergyStorage().getStoredEnergy() < 500);
    }

    @Test
    void shouldDriftTemperatureWhenBatteryRunsOut() {
        SuitPowerComponent suit = new SuitPowerComponent(1000, 0, 0, 0);
        suit.getEnergyStorage().setStoredEnergy(0);
        suit.updateEquippedArmorCount(4);

        suit.tick(false, 48.0, false);
        assertTrue(suit.getThermal().getCurrentTemperature() > 37.0);
    }
}
