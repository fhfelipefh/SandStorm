package com.fhfelipefh.sandstorm.content.defense;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KineticShieldTrackerTest {

    private ResourceKey<Level> overworld;
    private ResourceKey<Level> theEnd;

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void setUp() {
        overworld = Level.OVERWORLD;
        theEnd = Level.END;
        KineticShieldTracker.clearAll();
    }

    @Test
    void shouldReturnFalseWhenNoShieldsRegistered() {
        BlockPos pos = new BlockPos(200, 64, 200);
        assertFalse(KineticShieldTracker.isInsideShield(overworld, pos));
    }

    @Test
    void shouldDetectPositionInsideShieldRadius() {
        BlockPos generatorPos = new BlockPos(200, 64, 200);
        KineticShieldTracker.registerShield(overworld, generatorPos, 48.0);

        BlockPos targetInside = new BlockPos(220, 64, 200);
        assertTrue(KineticShieldTracker.isInsideShield(overworld, targetInside));

        BlockPos targetAtBoundary = new BlockPos(248, 64, 200);
        assertTrue(KineticShieldTracker.isInsideShield(overworld, targetAtBoundary));

        BlockPos targetOutside = new BlockPos(249, 64, 200);
        assertFalse(KineticShieldTracker.isInsideShield(overworld, targetOutside));
    }

    @Test
    void shouldIsolateShieldsByDimension() {
        BlockPos generatorPos = new BlockPos(200, 64, 200);
        KineticShieldTracker.registerShield(overworld, generatorPos, 48.0);

        BlockPos target = new BlockPos(210, 64, 200);
        assertTrue(KineticShieldTracker.isInsideShield(overworld, target));
        assertFalse(KineticShieldTracker.isInsideShield(theEnd, target));
    }

    @Test
    void shouldUnregisterShieldCorrectly() {
        BlockPos generatorPos = new BlockPos(200, 64, 200);
        KineticShieldTracker.registerShield(overworld, generatorPos, 48.0);

        BlockPos target = new BlockPos(210, 64, 200);
        assertTrue(KineticShieldTracker.isInsideShield(overworld, target));

        KineticShieldTracker.unregisterShield(overworld, generatorPos);
        assertFalse(KineticShieldTracker.isInsideShield(overworld, target));
    }

    @Test
    void shouldHandleMultipleShieldsAndClearAll() {
        BlockPos shield1 = new BlockPos(100, 64, 100);
        BlockPos shield2 = new BlockPos(800, 64, 800);

        KineticShieldTracker.registerShield(overworld, shield1, 30.0);
        KineticShieldTracker.registerShield(overworld, shield2, 50.0);

        assertTrue(KineticShieldTracker.isInsideShield(overworld, new BlockPos(115, 64, 100)));
        assertTrue(KineticShieldTracker.isInsideShield(overworld, new BlockPos(820, 64, 800)));
        assertFalse(KineticShieldTracker.isInsideShield(overworld, new BlockPos(400, 64, 400)));

        KineticShieldTracker.clearAll();
        assertFalse(KineticShieldTracker.isInsideShield(overworld, new BlockPos(115, 64, 100)));
        assertFalse(KineticShieldTracker.isInsideShield(overworld, new BlockPos(820, 64, 800)));
    }
}
