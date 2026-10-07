package com.fhfelipefh.sandstorm.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandStormConfigTest {

    @BeforeEach
    void setUp() {
        SandStormConfig.resetDefaults();
    }

    @Test
    void shouldHaveSensibleDefaultValues() {
        assertEquals(64, SandStormConfig.getCryogenicChillerRadius());
        assertEquals(50L, SandStormConfig.getCryogenicChillerEnergyCost());
        assertEquals(1200, SandStormConfig.getAmnioticIncubatorCycleTicks());
        assertEquals(80L, SandStormConfig.getAmnioticIncubatorEnergyCost());
        assertEquals(64, SandStormConfig.getAtmosphericTerraformerRadius());
    }

    @Test
    void shouldClampChillerRadiusWithinSafeBoundaries() {
        SandStormConfig.setCryogenicChillerRadius(128);
        assertEquals(128, SandStormConfig.getCryogenicChillerRadius());

        SandStormConfig.setCryogenicChillerRadius(2);
        assertEquals(8, SandStormConfig.getCryogenicChillerRadius());

        SandStormConfig.setCryogenicChillerRadius(9999);
        assertEquals(512, SandStormConfig.getCryogenicChillerRadius());
    }

    @Test
    void shouldUpdateAndPersistConfigValues() {
        SandStormConfig.setCryogenicChillerRadius(96);
        SandStormConfig.setCryogenicChillerEnergyCost(100L);
        SandStormConfig.setAmnioticIncubatorCycleTicks(600);
        SandStormConfig.setAmnioticIncubatorEnergyCost(120L);
        SandStormConfig.setAtmosphericTerraformerRadius(80);

        SandStormConfig.save();
        SandStormConfig.load();

        assertEquals(96, SandStormConfig.getCryogenicChillerRadius());
        assertEquals(100L, SandStormConfig.getCryogenicChillerEnergyCost());
        assertEquals(600, SandStormConfig.getAmnioticIncubatorCycleTicks());
        assertEquals(120L, SandStormConfig.getAmnioticIncubatorEnergyCost());
        assertEquals(80, SandStormConfig.getAtmosphericTerraformerRadius());
    }

    @Test
    void shouldResetToDefaultValues() {
        SandStormConfig.setCryogenicChillerRadius(150);
        assertTrue(SandStormConfig.getCryogenicChillerRadius() > 64);

        SandStormConfig.resetDefaults();
        assertEquals(64, SandStormConfig.getCryogenicChillerRadius());
    }
}
