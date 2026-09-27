package com.fhfelipefh.sandstorm.content.survival;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandDropHandlerTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldRecognizeAllSandBlockStates() {
        assertTrue(TechnologyToolRestrictionHandler.isSandBlock(Blocks.SAND.defaultBlockState()));
        assertTrue(TechnologyToolRestrictionHandler.isSandBlock(Blocks.RED_SAND.defaultBlockState()));
        assertTrue(TechnologyToolRestrictionHandler.isSandBlock(Blocks.SUSPICIOUS_SAND.defaultBlockState()));

        assertFalse(TechnologyToolRestrictionHandler.isSandBlock(Blocks.STONE.defaultBlockState()));
        assertFalse(TechnologyToolRestrictionHandler.isSandBlock(Blocks.DIRT.defaultBlockState()));
        assertFalse(TechnologyToolRestrictionHandler.isSandBlock(null));
    }

    @Test
    void shouldPermitBreakingSandWithoutPlayerOrRobotics() {
        assertTrue(TechnologyToolRestrictionHandler.canPlayerBreakBlock(null, Blocks.SAND.defaultBlockState()));
        assertFalse(TechnologyToolRestrictionHandler.requiresRoboticsToBreak(Blocks.SAND.defaultBlockState()));
        assertFalse(TechnologyToolRestrictionHandler.requiresRoboticsToBreak(Blocks.RED_SAND.defaultBlockState()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/data/sandstorm/loot_table/blocks/salinized_sand.json",
            "/data/sandstorm/loot_tables/blocks/salinized_sand.json"
    })
    void shouldValidateSalinizedSandLootTableStructure(String path) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("minecraft:sand"));
            assertTrue(json.contains("sandstorm:mineral_salt"));
            assertTrue(json.contains("minecraft:survives_explosion"));
        }
    }
}
