package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExcavatorVehicleEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildExcavatorAttributes() {
        AttributeSupplier.Builder builder = ExcavatorVehicleEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(120.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.22, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(16.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(0.8, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
    }

    @Test
    void shouldDefineStandardBatteryCapacity() {
        assertEquals(50000L, ExcavatorVehicleEntity.DEFAULT_BATTERY_CAPACITY);
    }
}
