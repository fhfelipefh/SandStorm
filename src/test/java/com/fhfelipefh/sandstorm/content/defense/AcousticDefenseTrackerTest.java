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

class AcousticDefenseTrackerTest {

    private ResourceKey<Level> overworld;
    private ResourceKey<Level> theNether;

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void setUp() {
        overworld = Level.OVERWORLD;
        theNether = Level.NETHER;
        AcousticDefenseTracker.clearAll();
    }

    @Test
    void shouldReturnFalseWhenNoPylonsRegistered() {
        BlockPos pos = new BlockPos(100, 64, 100);
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(overworld, pos));
    }

    @Test
    void shouldDetectPositionInsidePylonRadius() {
        BlockPos pylonPos = new BlockPos(100, 64, 100);
        AcousticDefenseTracker.registerPylon(overworld, pylonPos, 32.0);

        BlockPos targetInside = new BlockPos(115, 64, 100);
        assertTrue(AcousticDefenseTracker.isInsideAcousticDamping(overworld, targetInside));

        BlockPos targetAtBoundary = new BlockPos(132, 64, 100);
        assertTrue(AcousticDefenseTracker.isInsideAcousticDamping(overworld, targetAtBoundary));

        BlockPos targetOutside = new BlockPos(133, 64, 100);
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(overworld, targetOutside));
    }

    @Test
    void shouldIsolatePylonsByDimension() {
        BlockPos pylonPos = new BlockPos(100, 64, 100);
        AcousticDefenseTracker.registerPylon(overworld, pylonPos, 32.0);

        BlockPos target = new BlockPos(110, 64, 100);
        assertTrue(AcousticDefenseTracker.isInsideAcousticDamping(overworld, target));
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(theNether, target));
    }

    @Test
    void shouldUnregisterPylonCorrectly() {
        BlockPos pylonPos = new BlockPos(100, 64, 100);
        AcousticDefenseTracker.registerPylon(overworld, pylonPos, 32.0);

        BlockPos target = new BlockPos(110, 64, 100);
        assertTrue(AcousticDefenseTracker.isInsideAcousticDamping(overworld, target));

        AcousticDefenseTracker.unregisterPylon(overworld, pylonPos);
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(overworld, target));
    }

    @Test
    void shouldHandleMultiplePylonsAndClearAll() {
        BlockPos pylon1 = new BlockPos(0, 64, 0);
        BlockPos pylon2 = new BlockPos(500, 64, 500);

        AcousticDefenseTracker.registerPylon(overworld, pylon1, 20.0);
        AcousticDefenseTracker.registerPylon(overworld, pylon2, 40.0);

        assertTrue(AcousticDefenseTracker.isInsideAcousticDamping(overworld, new BlockPos(10, 64, 0)));
        assertTrue(AcousticDefenseTracker.isInsideAcousticDamping(overworld, new BlockPos(520, 64, 500)));
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(overworld, new BlockPos(250, 64, 250)));

        AcousticDefenseTracker.clearAll();
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(overworld, new BlockPos(10, 64, 0)));
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(overworld, new BlockPos(520, 64, 500)));
    }
}
