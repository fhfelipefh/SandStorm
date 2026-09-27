package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.survival.BedRestrictionHandler;
import com.fhfelipefh.sandstorm.content.survival.FusedSpaceSuitHandler;
import com.fhfelipefh.sandstorm.content.survival.MagicSuppressionHandler;
import com.fhfelipefh.sandstorm.content.survival.TechnologyToolRestrictionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NonSandStormWorldCompatibilityTest {

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
    void shouldNotEnforceToolRestrictionsInNonSandStormWorld() {
        SandStormWorldHelper.setForcedSandStormWorldForTesting(false);
        assertTrue(TechnologyToolRestrictionHandler.canPlayerBreakBlock(null, Blocks.STONE.defaultBlockState()));
    }

    @Test
    void shouldNotSuppressEntitiesInNonSandStormWorld() {
        SandStormWorldHelper.setForcedSandStormWorldForTesting(false);
        assertFalse(VanillaMonsterSuppressionHandler.shouldSuppressEntity(null));
    }

    @Test
    void shouldNotEnforceFusedSuitInNonSandStormWorld() {
        SandStormWorldHelper.setForcedSandStormWorldForTesting(false);
        assertDoesNotThrow(() -> FusedSpaceSuitHandler.enforceFusedSuit(null));
    }

    @Test
    void shouldPreserveIdentificationUtilitiesAcrossWorldTypes() {
        assertTrue(BedRestrictionHandler.isBedPath("white_bed"));
        assertTrue(TechnologyToolRestrictionHandler.isRestrictedToolName("diamond_pickaxe"));
        assertTrue(MagicSuppressionHandler.shouldSuppressBlock(Blocks.ENCHANTING_TABLE));
    }
}
