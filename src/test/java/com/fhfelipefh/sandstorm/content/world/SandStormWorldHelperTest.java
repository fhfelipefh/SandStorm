package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormWorldHelperTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @AfterEach
    void tearDown() {
        SandStormWorldHelper.setForcedSandStormWorldForTesting(null);
    }

    @Test
    void verifyDesertPlanetNoiseKey() {
        assertNotNull(SandStormWorldHelper.DESERT_PLANET_NOISE);
        assertEquals(Registries.NOISE_SETTINGS, SandStormWorldHelper.DESERT_PLANET_NOISE.registryKey());
        assertEquals("sandstorm", SandStormWorldHelper.DESERT_PLANET_NOISE.identifier().getNamespace());
        assertEquals("desert_planet", SandStormWorldHelper.DESERT_PLANET_NOISE.identifier().getPath());
    }

    @Test
    void shouldReturnFalseForNullLevelAndServer() {
        Level nullLevel = null;
        MinecraftServer nullServer = null;
        assertFalse(SandStormWorldHelper.isSandStormWorld(nullLevel));
        assertFalse(SandStormWorldHelper.isSandStormWorld(nullServer));
        assertFalse(SandStormWorldHelper.isSandStormGenerator(null));
    }

    @Test
    void shouldRespectForcedFlagForTesting() {
        Level nullLevel = null;
        MinecraftServer nullServer = null;

        SandStormWorldHelper.setForcedSandStormWorldForTesting(false);
        assertFalse(SandStormWorldHelper.isSandStormWorld(nullLevel));
        assertFalse(SandStormWorldHelper.isSandStormWorld(nullServer));

        SandStormWorldHelper.setForcedSandStormWorldForTesting(true);
        assertTrue(SandStormWorldHelper.isSandStormWorld(nullLevel));
        assertTrue(SandStormWorldHelper.isSandStormWorld(nullServer));
    }
}
