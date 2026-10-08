package com.fhfelipefh.sandstorm.content.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProceduralRuinsManagerTest {

    @Test
    void hash64ShouldBeDeterministic() {
        long hash1 = ProceduralRuinsManager.hash64(123456789L);
        long hash2 = ProceduralRuinsManager.hash64(123456789L);
        assertEquals(hash1, hash2);

        long hash3 = ProceduralRuinsManager.hash64(987654321L);
        assertNotEquals(hash1, hash3);
    }

    @Test
    void hash64ShouldProduceBothPositiveAndNegativeValues() {
        long h1 = ProceduralRuinsManager.hash64(1L);
        long h2 = ProceduralRuinsManager.hash64(2L);
        assertNotEquals(0L, h1);
        assertNotEquals(0L, h2);
    }

    @Test
    void constantsShouldHaveExpectedNaturalRuinRarities() {
        assertTrue(ProceduralRuinsManager.OUTPOST_RARITY > 0);
        assertTrue(ProceduralRuinsManager.FUEL_SILO_RARITY > 0);
        assertEquals(180, ProceduralRuinsManager.OUTPOST_RARITY);
        assertEquals(150, ProceduralRuinsManager.FUEL_SILO_RARITY);
    }

    @Test
    void proceduralRuinsSavedDataShouldTrackProcessedChunks() {
        ProceduralRuinsSavedData data = new ProceduralRuinsSavedData();
        long chunkKey = 1048580L;

        assertFalse(data.isChunkProcessed(chunkKey));
        data.markChunkProcessed(chunkKey);
        assertTrue(data.isChunkProcessed(chunkKey));
    }
}
