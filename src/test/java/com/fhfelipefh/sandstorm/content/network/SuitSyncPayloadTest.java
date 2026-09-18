package com.fhfelipefh.sandstorm.content.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SuitSyncPayloadTest {

    @Test
    void shouldHoldPayloadFieldsCorrectly() {
        SuitSyncPayload payload = new SuitSyncPayload(50000L, 100000L, 37.05, 4);

        assertEquals(50000L, payload.storedEnergy());
        assertEquals(100000L, payload.capacity());
        assertEquals(37.05, payload.temperature(), 0.001);
        assertEquals(4, payload.armorCount());
        assertEquals(SuitSyncPayload.TYPE, payload.type());
        assertNotNull(SuitSyncPayload.STREAM_CODEC);
        assertEquals("sandstorm:suit_sync", SuitSyncPayload.TYPE.id().toString());
    }
}
