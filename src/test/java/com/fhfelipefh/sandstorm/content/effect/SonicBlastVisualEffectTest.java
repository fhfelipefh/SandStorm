package com.fhfelipefh.sandstorm.content.effect;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SonicBlastVisualEffectTest {

    @Test
    void shouldCalculateRayStepPointsCorrectly() {
        Vec3 origin = new Vec3(10.0, 64.0, 10.0);
        Vec3 direction = new Vec3(1.0, 0.0, 0.0);
        double range = 24.0;

        Vec3 end = origin.add(direction.scale(range));
        assertEquals(34.0, end.x, 0.001);
        assertEquals(64.0, end.y, 0.001);
        assertEquals(10.0, end.z, 0.001);
        assertEquals(24.0, origin.distanceTo(end), 0.001);

        int count = 0;
        double lastRingDist = 0.0;
        int ringCount = 0;
        for (double d = 0.8; d <= range; d += SonicBlastVisualEffect.DEFAULT_STEP) {
            count++;
            if (d - lastRingDist >= SonicBlastVisualEffect.SONIC_RING_INTERVAL) {
                ringCount++;
                lastRingDist = d;
            }
        }

        assertTrue(count >= 30);
        assertTrue(ringCount >= 7);
    }

    @Test
    void shouldMaintainCollinearDirectionAlongBeam() {
        Vec3 origin = new Vec3(0.0, 70.0, 0.0);
        Vec3 direction = new Vec3(0.0, 0.0, 1.0).normalize();

        for (double d = 0.8; d <= 10.0; d += SonicBlastVisualEffect.DEFAULT_STEP) {
            Vec3 point = origin.add(direction.scale(d));
            assertEquals(0.0, point.x, 0.001);
            assertEquals(70.0, point.y, 0.001);
            assertEquals(d, point.z, 0.001);
        }
    }
}
