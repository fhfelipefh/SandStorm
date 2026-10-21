package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.handler.HeavyMachineryCameraHandler;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeavyMachineryCameraHandlerTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldDampenRotationalYawAndPitch() {
        HeavyMachineryCameraHandler.reset();

        float[] first = HeavyMachineryCameraHandler.applyRotationalLag(90.0f, 10.0f, 0.0);
        assertEquals(90.0f, first[0], 0.001f);
        assertEquals(10.0f, first[1], 0.001f);

        float[] turned = HeavyMachineryCameraHandler.applyRotationalLag(120.0f, 20.0f, 0.0);
        assertTrue(turned[0] < 120.0f);
        assertTrue(turned[1] < 20.0f);
    }

    @Test
    void shouldCalculateMegazordMechanicalBobbingWhenMoving() {
        HeavyMachineryCameraHandler.reset();
        Vec3 delta = new Vec3(0.25, 0.0, 0.25);

        Vec3 bobbing = HeavyMachineryCameraHandler.calculateBobbing(
                delta, true, 14, 0.0f, 0.0f,
                HeavyMachineryCameraHandler.MEGAZORD_STEP_PERIOD,
                0.045f, 0.065f, 0.025f, 3.5f
        );
        assertNotNull(bobbing);
        assertTrue(Math.abs(bobbing.y) > 0.0001);
    }

    @Test
    void shouldCalculateExcavatorMechanicalBobbingWhenMoving() {
        HeavyMachineryCameraHandler.reset();
        Vec3 delta = new Vec3(0.18, 0.0, 0.18);

        Vec3 bobbing = HeavyMachineryCameraHandler.calculateBobbing(
                delta, true, 4, 45.0f, 0.0f,
                HeavyMachineryCameraHandler.EXCAVATOR_TREAD_PERIOD,
                0.030f, 0.040f, 0.020f, 4.0f
        );
        assertNotNull(bobbing);
        assertTrue(Math.abs(bobbing.y) > 0.0001);
    }

    @Test
    void shouldReturnZeroBobbingWhenStationary() {
        HeavyMachineryCameraHandler.reset();
        Vec3 delta = Vec3.ZERO;

        Vec3 bobbing = HeavyMachineryCameraHandler.calculateBobbing(
                delta, true, 0, 0.0f, 0.0f,
                HeavyMachineryCameraHandler.MEGAZORD_STEP_PERIOD,
                0.045f, 0.065f, 0.025f, 3.5f
        );
        assertEquals(0.0, bobbing.x, 0.0001);
        assertEquals(0.0, bobbing.y, 0.0001);
        assertEquals(0.0, bobbing.z, 0.0001);
    }
}
