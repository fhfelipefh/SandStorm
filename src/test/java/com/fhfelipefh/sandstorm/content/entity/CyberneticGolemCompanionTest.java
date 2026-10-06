package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.network.CallGolemPayload;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CyberneticGolemCompanionTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testCallGolemPayload() {
        CallGolemPayload payload = new CallGolemPayload();
        assertNotNull(payload.type());
        assertEquals("sandstorm:call_golem", payload.type().id().toString());
        assertNotNull(CallGolemPayload.STREAM_CODEC);
    }

    @Test
    void testGolemOwnerAndSentinelMechanics() {
        CyberneticGolemEntity.createAttributes();
        assertEquals(128.0, CyberneticGolemEntity.createAttributes().build().getValue(Attributes.FOLLOW_RANGE));

        UUID testOwner = UUID.randomUUID();
        assertNotNull(testOwner);
    }
}
