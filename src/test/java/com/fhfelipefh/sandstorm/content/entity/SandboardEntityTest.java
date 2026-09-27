package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandboardEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildSandboardAttributes() {
        AttributeSupplier.Builder builder = SandboardEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(20.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.35, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(2.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
    }
}
