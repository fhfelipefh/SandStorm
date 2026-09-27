package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.network.FlashlightTogglePayload;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlashlightAndSurvivalItemTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldTrackFlashlightStateServer() {
        UUID playerId = UUID.randomUUID();
        assertFalse(FlashlightStateServer.isActive(playerId));
        assertEquals(0, FlashlightStateServer.getMode(playerId));

        FlashlightStateServer.setFlashlight(playerId, true);
        assertTrue(FlashlightStateServer.isActive(playerId));
        assertEquals(1, FlashlightStateServer.getMode(playerId));

        FlashlightStateServer.setFlashlightMode(playerId, 2);
        assertTrue(FlashlightStateServer.isActive(playerId));
        assertEquals(2, FlashlightStateServer.getMode(playerId));

        FlashlightStateServer.setFlashlight(playerId, false);
        assertFalse(FlashlightStateServer.isActive(playerId));
        assertEquals(0, FlashlightStateServer.getMode(playerId));

        FlashlightStateServer.setFlashlightMode(playerId, 1);
        FlashlightStateServer.removePlayer(playerId);
        assertFalse(FlashlightStateServer.isActive(playerId));
    }

    @Test
    void shouldCreateFlashlightTogglePayload() {
        FlashlightTogglePayload payloadTrue = new FlashlightTogglePayload(true);
        assertTrue(payloadTrue.enabled());
        assertEquals(1, payloadTrue.mode());
        assertEquals(FlashlightTogglePayload.TYPE, payloadTrue.type());

        FlashlightTogglePayload payloadFalse = new FlashlightTogglePayload(false);
        assertFalse(payloadFalse.enabled());
        assertEquals(0, payloadFalse.mode());

        FlashlightTogglePayload payloadCustom = new FlashlightTogglePayload(3);
        assertTrue(payloadCustom.enabled());
        assertEquals(3, payloadCustom.mode());
    }
}
