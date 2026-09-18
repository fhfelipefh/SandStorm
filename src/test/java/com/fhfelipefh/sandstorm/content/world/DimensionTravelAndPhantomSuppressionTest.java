package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DimensionTravelAndPhantomSuppressionTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldDetectNetherIgnitionAttempt() {
        assertTrue(DimensionPortalRestrictionHandler.isNetherIgnition(
                Blocks.OBSIDIAN,
                Items.FLINT_AND_STEEL
        ));
        assertTrue(DimensionPortalRestrictionHandler.isNetherIgnition(
                Blocks.OBSIDIAN,
                Items.FIRE_CHARGE
        ));
        assertFalse(DimensionPortalRestrictionHandler.isNetherIgnition(
                Blocks.OBSIDIAN,
                Items.STICK
        ));
        assertFalse(DimensionPortalRestrictionHandler.isNetherIgnition(
                Blocks.SANDSTONE,
                Items.FLINT_AND_STEEL
        ));
        assertFalse(DimensionPortalRestrictionHandler.isNetherIgnition(
                null,
                Items.FLINT_AND_STEEL
        ));
        assertFalse(DimensionPortalRestrictionHandler.isNetherIgnition(
                Blocks.OBSIDIAN,
                null
        ));
    }

    @Test
    void shouldDetectEndPortalActivationAttempt() {
        assertTrue(DimensionPortalRestrictionHandler.isEndPortalActivation(
                Blocks.END_PORTAL_FRAME,
                Items.ENDER_EYE
        ));
        assertFalse(DimensionPortalRestrictionHandler.isEndPortalActivation(
                Blocks.END_PORTAL_FRAME,
                Items.STICK
        ));
        assertFalse(DimensionPortalRestrictionHandler.isEndPortalActivation(
                Blocks.OBSIDIAN,
                Items.ENDER_EYE
        ));
        assertFalse(DimensionPortalRestrictionHandler.isEndPortalActivation(
                null,
                Items.ENDER_EYE
        ));
        assertFalse(DimensionPortalRestrictionHandler.isEndPortalActivation(
                Blocks.END_PORTAL_FRAME,
                null
        ));
    }

    @Test
    void shouldIdentifyForbiddenDimensions() {
        assertTrue(DimensionPortalRestrictionHandler.isForbiddenDimension(Level.NETHER));
        assertTrue(DimensionPortalRestrictionHandler.isForbiddenDimension(Level.END));
        assertFalse(DimensionPortalRestrictionHandler.isForbiddenDimension(Level.OVERWORLD));
        assertFalse(DimensionPortalRestrictionHandler.isForbiddenDimension(null));
    }
}
