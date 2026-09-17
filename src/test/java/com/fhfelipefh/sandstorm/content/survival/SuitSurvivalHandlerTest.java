package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
}
