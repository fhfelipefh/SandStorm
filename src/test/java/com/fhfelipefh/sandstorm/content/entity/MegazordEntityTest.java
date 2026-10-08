package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MegazordEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    @Test
    void shouldBuildMegazordAttributes() {
        AttributeSupplier.Builder builder = MegazordEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(500.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.26, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(0.50, supplier.getBaseValue(Attributes.FLYING_SPEED), 0.001);
        assertEquals(25.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
        assertEquals(30.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(2.0, supplier.getBaseValue(Attributes.STEP_HEIGHT), 0.001);
    }

    @Test
    void shouldKeepPilotProfileCenteredAndInsideCockpit() {
        Vec3 attachment = MegazordPilotProfile.passengerAttachment(1.0f);
        assertEquals(0.0, attachment.x, 0.001);
        assertEquals(MegazordPilotProfile.COCKPIT_FLOOR_Y, attachment.y, 0.001);
        assertEquals(MegazordPilotProfile.COCKPIT_FORWARD_Z, attachment.z, 0.001);
        assertEquals(MegazordPilotProfile.COCKPIT_FLOOR_Y * 1.5, MegazordPilotProfile.passengerAttachment(1.5f).y, 0.001);
    }

    @Test
    void shouldDefineStandardSpecifications() {
        assertEquals(100000L, MegazordEntity.DEFAULT_BATTERY_CAPACITY);
        assertEquals(250000L, MegazordEntity.OVERDRIVE_BATTERY_CAPACITY);
        assertEquals(16.0, MegazordEntity.SHOCKWAVE_RADIUS, 0.001);
        assertEquals(24.0, MegazordEntity.OVERDRIVE_SHOCKWAVE_RADIUS, 0.001);
        assertEquals(25.0f, MegazordEntity.DEFAULT_SHOCKWAVE_DAMAGE, 0.001f);
        assertEquals(45.0f, MegazordEntity.OVERDRIVE_SHOCKWAVE_DAMAGE, 0.001f);
    }

    @Test
    void shouldElevateRidingPassengerToCockpitHeight() throws Exception {
        Unsafe unsafe = getUnsafe();
        MegazordEntity megazord = (MegazordEntity) unsafe.allocateInstance(MegazordEntity.class);

        Method method = MegazordEntity.class.getDeclaredMethod(
                "getPassengerAttachmentPoint",
                Entity.class,
                EntityDimensions.class,
                float.class
        );
        method.setAccessible(true);

        Vec3 point = (Vec3) method.invoke(megazord, null, null, 1.0f);
        assertEquals(0.0, point.x, 0.001);
        assertEquals(2.68, point.y, 0.001);
        assertEquals(0.35, point.z, 0.001);

        Vec3 scaledPoint = (Vec3) method.invoke(megazord, null, null, 1.5f);
        assertEquals(0.0, scaledPoint.x, 0.001);
        assertEquals(4.02, scaledPoint.y, 0.001);
        assertEquals(0.525, scaledPoint.z, 0.001);
    }
}
