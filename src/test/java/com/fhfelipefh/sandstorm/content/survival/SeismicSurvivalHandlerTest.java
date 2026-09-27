package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeismicSurvivalHandlerTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @AfterEach
    void tearDown() {
        SandstormWeatherHandler.resetWeather();
    }

    @Test
    void trackerShouldBeInitializedWithDefaultSafeZone() {
        assertNotNull(SeismicSurvivalHandler.getTracker());
        assertEquals(96.0, SeismicSurvivalHandler.getTracker().getSafeZoneRadiusBlocks(), 0.001);
        assertTrue(SeismicSurvivalHandler.getTracker().isInsideSafeZone(0, 0));
    }

    @Test
    void recordVibrationShouldRegisterInTracker() {
        SeismicSurvivalHandler.getTracker().reset();
        SeismicSurvivalHandler.recordVibration(12, 14, 45.0);
        assertEquals(45.0, SeismicSurvivalHandler.getTracker().getVibration(12, 14), 0.001);
    }

    @Test
    void movementVibrationMultiplierShouldBeFullWhenWeatherInactive() {
        SandstormWeatherHandler.resetWeather();
        assertEquals(1.0, SeismicSurvivalHandler.getMovementVibrationMultiplier(), 0.001);
    }

    @Test
    void movementVibrationMultiplierShouldBeHalvedDuringActiveStorm() {
        SandstormWeatherHandler.triggerSandstorm(100, 1.0);
        for (int i = 0; i < 50; i++) {
            SandstormWeatherHandler.getWeather().tick();
        }
        assertEquals(0.50, SeismicSurvivalHandler.getMovementVibrationMultiplier(), 0.001);
    }

    @Test
    void shouldRecognizeSafeBlocksAndLooseSand() {
        assertTrue(SeismicSurvivalHandler.isSeismicSafeBlock(Blocks.SANDSTONE.defaultBlockState()));
        assertTrue(SeismicSurvivalHandler.isSeismicSafeBlock(Blocks.CUT_SANDSTONE.defaultBlockState()));
        assertTrue(SeismicSurvivalHandler.isSeismicSafeBlock(Blocks.STONE.defaultBlockState()));
        assertTrue(SeismicSurvivalHandler.isSeismicSafeBlock(Blocks.COBBLESTONE.defaultBlockState()));
        assertFalse(SeismicSurvivalHandler.isSeismicSafeBlock(Blocks.SAND.defaultBlockState()));
        assertFalse(SeismicSurvivalHandler.isSeismicSafeBlock(Blocks.RED_SAND.defaultBlockState()));
        assertFalse(SeismicSurvivalHandler.isSeismicSafeBlock(null));
    }
}
