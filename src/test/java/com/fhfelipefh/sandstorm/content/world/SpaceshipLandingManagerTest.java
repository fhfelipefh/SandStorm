package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceshipLandingManagerTest {

    @Test
    void shouldInitializeWithoutException() {
        assertDoesNotThrow(SpaceshipLandingManager::initialize);
    }

    @Test
    void shouldManageCachedCabinSpawnPos() {
        assertNotNull(SpaceshipLandingManager.getCabinSpawnPos());
        assertTrue(SpaceshipLandingManager.getCabinSpawnPos().getY() >= 64);
        BlockPos customPos = new BlockPos(0, 80, 0);
        SpaceshipLandingManager.setCachedCabinSpawnPos(customPos);
        assertEquals(customPos, SpaceshipLandingManager.getCabinSpawnPos());
    }

    @Test
    void shouldHaveDefaultSpawnPosAtSafeSurfaceLevel() {
        BlockPos pos = SpaceshipLandingManager.getCabinSpawnPos();
        assertNotNull(pos);
        assertTrue(pos.getY() >= 64);
    }

    @Test
    void shouldCheckDistanceToSpaceship() {
        BlockPos cabinPos = SpaceshipLandingManager.getCabinSpawnPos();
        assertTrue(SpaceshipLandingManager.isNearSpaceship(cabinPos));
        assertTrue(SpaceshipLandingManager.isNearSpaceship(cabinPos.offset(50, 0, 50)));
        assertFalse(SpaceshipLandingManager.isNearSpaceship(cabinPos.offset(300, 0, 300)));
    }
}
