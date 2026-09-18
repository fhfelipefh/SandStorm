package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpaceshipLandingManagerTest {

    @Test
    void shouldInitializeWithoutException() {
        assertDoesNotThrow(SpaceshipLandingManager::initialize);
    }

    @Test
    void shouldManageCachedCabinSpawnPos() {
        assertNotNull(SpaceshipLandingManager.getCabinSpawnPos());
        BlockPos customPos = new BlockPos(0, 80, 0);
        SpaceshipLandingManager.setCachedCabinSpawnPos(customPos);
        assertEquals(customPos, SpaceshipLandingManager.getCabinSpawnPos());
    }
}
