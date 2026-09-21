package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.gamerules.GameRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VanillaMonsterSuppressionHandlerTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testNullEntityNotSuppressed() {
        assertFalse(VanillaMonsterSuppressionHandler.shouldSuppressEntity(null));
    }

    @Test
    void testAllowedTagConstant() {
        assertEquals("sandstorm.allowed", VanillaMonsterSuppressionHandler.ALLOWED_TAG);
    }

    @Test
    void testGameRuleKeysConfigured() {
        assertNotNull(GameRules.SPAWN_MOBS);
        assertNotNull(GameRules.SPAWN_MONSTERS);
        assertNotNull(GameRules.SPAWN_PATROLS);
        assertNotNull(GameRules.SPAWN_PHANTOMS);
        assertNotNull(GameRules.SPAWN_WANDERING_TRADERS);
        assertNotNull(GameRules.SPAWN_WARDENS);
    }

    @Test
    void testApplyGameRulesNullSafety() {
        assertDoesNotThrow(() -> VanillaMonsterSuppressionHandler.applyGameRules(null));
    }

    @Test
    void testInitializeWithoutException() {
        assertDoesNotThrow(VanillaMonsterSuppressionHandler::initialize);
    }
}
