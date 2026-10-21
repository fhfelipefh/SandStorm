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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandboardEntityTest {

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
    void shouldBuildSandboardAttributes() {
        AttributeSupplier.Builder builder = SandboardEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(20.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.35, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(2.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
    }

    @Test
    void shouldProvideCorrectPassengerAttachmentPoint() throws Exception {
        Unsafe unsafe = getUnsafe();
        SandboardEntity sandboard = (SandboardEntity) unsafe.allocateInstance(SandboardEntity.class);

        Method method = SandboardEntity.class.getDeclaredMethod(
                "getPassengerAttachmentPoint",
                Entity.class,
                EntityDimensions.class,
                float.class
        );
        method.setAccessible(true);

        Vec3 attachment = (Vec3) method.invoke(sandboard, null, null, 1.0f);
        assertEquals(0.0, attachment.x, 0.001);
        assertEquals(0.15, attachment.y, 0.001);
        assertEquals(0.0, attachment.z, 0.001);
    }

    @Test
    void shouldDisableVanillaHorseHearts() throws Exception {
        Unsafe unsafe = getUnsafe();
        SandboardEntity sandboard = (SandboardEntity) unsafe.allocateInstance(SandboardEntity.class);

        Method method = SandboardEntity.class.getDeclaredMethod("showVehicleHealth");
        method.setAccessible(true);
        assertFalse((Boolean) method.invoke(sandboard));
    }

    @Test
    void shouldPermitJumpDuringCoyoteTimeWindow() throws Exception {
        Unsafe unsafe = getUnsafe();
        SandboardEntity sandboard = (SandboardEntity) unsafe.allocateInstance(SandboardEntity.class);

        sandboard.updateJumpBufferAndCoyote(false, true);
        assertEquals(2, sandboard.getCoyoteTicks());

        sandboard.updateJumpBufferAndCoyote(false, false);
        assertEquals(1, sandboard.getCoyoteTicks());

        boolean jumped = sandboard.updateJumpBufferAndCoyote(true, false);
        assertTrue(jumped);
        assertEquals(0, sandboard.getCoyoteTicks());
        assertEquals(0, sandboard.getJumpBufferTicks());
    }

    @Test
    void shouldExpireCoyoteTimeAfterTwoTicksInAir() throws Exception {
        Unsafe unsafe = getUnsafe();
        SandboardEntity sandboard = (SandboardEntity) unsafe.allocateInstance(SandboardEntity.class);

        sandboard.updateJumpBufferAndCoyote(false, true);
        sandboard.updateJumpBufferAndCoyote(false, false);
        sandboard.updateJumpBufferAndCoyote(false, false);
        assertEquals(0, sandboard.getCoyoteTicks());

        boolean jumped = sandboard.updateJumpBufferAndCoyote(true, false);
        assertFalse(jumped);
    }

    @Test
    void shouldBufferJumpCommandBeforeGroundContact() throws Exception {
        Unsafe unsafe = getUnsafe();
        SandboardEntity sandboard = (SandboardEntity) unsafe.allocateInstance(SandboardEntity.class);

        sandboard.setCoyoteTicks(0);
        boolean inAirJump = sandboard.updateJumpBufferAndCoyote(true, false);
        assertFalse(inAirJump);
        assertEquals(1, sandboard.getJumpBufferTicks());

        boolean landedJump = sandboard.updateJumpBufferAndCoyote(false, true);
        assertTrue(landedJump);
        assertEquals(0, sandboard.getJumpBufferTicks());
        assertEquals(0, sandboard.getCoyoteTicks());
    }
}
