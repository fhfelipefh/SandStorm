package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.gamerules.GameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InfiniteWaterSuppressionTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testWaterSourceConversionGameRuleExists() {
        assertNotNull(GameRules.WATER_SOURCE_CONVERSION);
    }

    @Test
    void testApplyGameRulesNullSafety() {
        assertDoesNotThrow(() -> VanillaMonsterSuppressionHandler.applyGameRules(null));
    }
}
