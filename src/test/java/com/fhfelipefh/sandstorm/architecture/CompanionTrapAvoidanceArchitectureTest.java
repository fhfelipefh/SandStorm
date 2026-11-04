package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.block.KineticFloorSpikesBlock;
import com.fhfelipefh.sandstorm.content.block.NeurotoxicSpikesBlock;
import com.fhfelipefh.sandstorm.content.block.SerratedJawTrapBlock;
import com.fhfelipefh.sandstorm.content.entity.CyberHoundEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberSteedEntity;
import com.fhfelipefh.sandstorm.content.entity.ai.CompanionGroundPathNavigation;
import com.fhfelipefh.sandstorm.content.entity.ai.CompanionTrapHelper;
import com.fhfelipefh.sandstorm.content.entity.ai.CompanionWalkNodeEvaluator;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanionTrapAvoidanceArchitectureTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testTrapHierarchyAndHelperSafety() {
        assertTrue(Block.class.isAssignableFrom(KineticFloorSpikesBlock.class));
        assertTrue(Block.class.isAssignableFrom(NeurotoxicSpikesBlock.class));
        assertTrue(Block.class.isAssignableFrom(SerratedJawTrapBlock.class));

        assertFalse(CompanionTrapHelper.isSpikeTrapBlock(null));
        assertFalse(CompanionTrapHelper.isTrapOrHazardBlock(null));
        assertFalse(CompanionTrapHelper.isTamedCompanion(null));
    }

    @Test
    void testCompanionNavigationClasses() {
        assertTrue(GroundPathNavigation.class.isAssignableFrom(CompanionGroundPathNavigation.class));
        assertTrue(WalkNodeEvaluator.class.isAssignableFrom(CompanionWalkNodeEvaluator.class));
    }

    @Test
    void testEntityNavigationOverrides() throws Exception {
        Method houndNav = CyberHoundEntity.class.getDeclaredMethod("createNavigation", Level.class);
        assertNotNull(houndNav);
        assertEquals(PathNavigation.class, houndNav.getReturnType());

        Method steedNav = CyberSteedEntity.class.getDeclaredMethod("createNavigation", Level.class);
        assertNotNull(steedNav);
        assertEquals(PathNavigation.class, steedNav.getReturnType());
    }

    @Test
    void testVersionIncrementedTo89() throws Exception {
        Path gradleProps = Path.of("gradle.properties");
        assertTrue(Files.exists(gradleProps));
        String content = Files.readString(gradleProps);
        assertTrue(content.contains("version=1.19.89"), "gradle.properties must be 1.19.89");
    }
}
