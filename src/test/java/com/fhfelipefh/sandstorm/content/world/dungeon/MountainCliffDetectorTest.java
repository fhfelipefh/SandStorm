package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MountainCliffDetectorTest {

    @Test
    void shouldAcceptIdealMountainSlopeWithFullEncasement() {
        BlockPos entranceOrigin = new BlockPos(100, 65, 100);
        Direction outwardFacing = Direction.SOUTH;
        int width = 28;
        int height = 24;
        int depth = 35;
        int minOverburden = 5;

        MountainCliffDetector.HeightSampler idealMountain = (x, z) -> {
            if (z > 100) {
                return 65;
            } else {
                return 115;
            }
        };

        boolean accepted = MountainCliffDetector.validateEncasement(
                idealMountain,
                entranceOrigin,
                outwardFacing,
                width,
                height,
                depth,
                minOverburden
        );

        assertTrue(accepted, "Ideal deep mountain should be validated successfully");
    }

    @Test
    void shouldRejectWhenMountainDropsIntoValleyBehind() {
        BlockPos entranceOrigin = new BlockPos(100, 65, 100);
        Direction outwardFacing = Direction.SOUTH;
        int width = 28;
        int height = 24;
        int depth = 35;
        int minOverburden = 5;

        MountainCliffDetector.HeightSampler narrowRidge = (x, z) -> {
            if (z > 100) {
                return 65;
            } else if (z < 80) {
                return 70;
            } else {
                return 115;
            }
        };

        boolean accepted = MountainCliffDetector.validateEncasement(
                narrowRidge,
                entranceOrigin,
                outwardFacing,
                width,
                height,
                depth,
                minOverburden
        );

        assertFalse(accepted, "Mountain with insufficient depth must be rejected");
    }

    @Test
    void shouldRejectWhenCeilingOverburdenIsTooLow() {
        BlockPos entranceOrigin = new BlockPos(100, 65, 100);
        Direction outwardFacing = Direction.SOUTH;
        int width = 28;
        int height = 24;
        int depth = 35;
        int minOverburden = 5;

        MountainCliffDetector.HeightSampler lowHill = (x, z) -> {
            if (z > 100) {
                return 65;
            } else {
                return 75;
            }
        };

        boolean accepted = MountainCliffDetector.validateEncasement(
                lowHill,
                entranceOrigin,
                outwardFacing,
                width,
                height,
                depth,
                minOverburden
        );

        assertFalse(accepted, "Mountain with insufficient height/ceiling must be rejected");
    }

    @Test
    void shouldRejectWhenLateralSideDropsOffCliff() {
        BlockPos entranceOrigin = new BlockPos(100, 65, 100);
        Direction outwardFacing = Direction.SOUTH;
        int width = 28;
        int height = 24;
        int depth = 35;
        int minOverburden = 5;

        MountainCliffDetector.HeightSampler narrowPillar = (x, z) -> {
            if (z > 100) {
                return 65;
            } else if (x > 110) {
                return 65;
            } else {
                return 115;
            }
        };

        boolean accepted = MountainCliffDetector.validateEncasement(
                narrowPillar,
                entranceOrigin,
                outwardFacing,
                width,
                height,
                depth,
                minOverburden
        );

        assertFalse(accepted, "Mountain with insufficient width must be rejected");
    }

    @Test
    void shouldRejectWhenFrontCanyonIsObstructed() {
        BlockPos entranceOrigin = new BlockPos(100, 65, 100);
        Direction outwardFacing = Direction.SOUTH;
        int width = 28;
        int height = 24;
        int depth = 35;
        int minOverburden = 5;

        MountainCliffDetector.HeightSampler buriedFront = (x, z) -> 120;

        boolean accepted = MountainCliffDetector.validateEncasement(
                buriedFront,
                entranceOrigin,
                outwardFacing,
                width,
                height,
                depth,
                minOverburden
        );

        assertFalse(accepted, "Site with buried/obstructed front must be rejected");
    }
}
