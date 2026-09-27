package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class SuitSurvivalHandlerTest {

    private final UUID testUuid = UUID.randomUUID();

    @AfterEach
    void tearDown() {
        SuitSurvivalHandler.removePlayer(testUuid);
    }

    @Test
    void shouldCreateNewSuitComponentForNewPlayer() {
        SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(testUuid);
        assertNotNull(suit);
    }

    @Test
    void shouldReuseExistingSuitComponentForSamePlayer() {
        SuitPowerComponent suit1 = SuitSurvivalHandler.getOrCreateSuit(testUuid);
        SuitPowerComponent suit2 = SuitSurvivalHandler.getOrCreateSuit(testUuid);
        assertSame(suit1, suit2);
    }

    @Test
    void shouldRemoveSuitComponentOnPlayerRemoval() {
        SuitPowerComponent suit1 = SuitSurvivalHandler.getOrCreateSuit(testUuid);
        SuitSurvivalHandler.removePlayer(testUuid);
        SuitPowerComponent suit2 = SuitSurvivalHandler.getOrCreateSuit(testUuid);
        assertNotSame(suit1, suit2);
    }

    @Test
    void shouldRestoreFullBatteryFromSavedDataOnPlayerReturn() {
        PlayerSuitSavedData savedData = new PlayerSuitSavedData();
        long fullBattery = 100000L;
        double savedTemp = 42.0;

        savedData.setSuitData(testUuid, fullBattery, savedTemp);

        SuitPowerComponent suit = new SuitPowerComponent();
        PlayerSuitSavedData.Entry entry = savedData.getSuitData(testUuid);
        assertNotNull(entry);
        suit.getEnergyStorage().setStoredEnergy(entry.energy());
        suit.getThermal().setCurrentTemperature(entry.temperature());

        assertEquals(100000L, suit.getEnergyStorage().getStoredEnergy());
        assertEquals(1.0, suit.getEnergyStorage().getEnergyRatio(), 0.001);
        assertEquals(42.0, suit.getThermal().getCurrentTemperature(), 0.001);
    }

    @Test
    void shouldRetainFullBatteryAcrossMultipleSessions() {
        PlayerSuitSavedData savedData = new PlayerSuitSavedData();
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();

        savedData.setSuitData(player1, 100000L, 37.0);
        savedData.setSuitData(player2, 75000L, 45.0);

        PlayerSuitSavedData.Entry p1Entry = savedData.getSuitData(player1);
        PlayerSuitSavedData.Entry p2Entry = savedData.getSuitData(player2);

        assertNotNull(p1Entry);
        assertNotNull(p2Entry);
        assertEquals(100000L, p1Entry.energy());
        assertEquals(75000L, p2Entry.energy());

        savedData.setSuitData(player1, 95000L, 38.0);
        assertEquals(95000L, savedData.getSuitData(player1).energy());
        assertEquals(38.0, savedData.getSuitData(player1).temperature(), 0.001);
        assertEquals(75000L, savedData.getSuitData(player2).energy());
    }
}
