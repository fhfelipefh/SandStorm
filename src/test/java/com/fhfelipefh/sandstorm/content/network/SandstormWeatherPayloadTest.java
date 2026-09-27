package com.fhfelipefh.sandstorm.content.network;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandstormWeatherPayloadTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testPayloadProperties() {
        SandstormWeatherPayload payload = new SandstormWeatherPayload(true, 0.85);

        assertTrue(payload.active());
        assertEquals(0.85, payload.intensity(), 0.001);
        assertEquals(SandstormWeatherPayload.TYPE, payload.type());
        assertNotNull(SandstormWeatherPayload.STREAM_CODEC);
        assertEquals("sandstorm:sandstorm_weather", SandstormWeatherPayload.TYPE.id().toString());
    }
}
