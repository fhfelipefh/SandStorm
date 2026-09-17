package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandwormEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildEnhancedAttributesForApexThreat() {
        AttributeSupplier.Builder builder = SandwormEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(300.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(18.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(12.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(0.32, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
    }
}
