package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.block.SandstoneWorkbenchBlock;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CraftingTableBlock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechnologyToolRestrictionHandlerTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "wooden_pickaxe",
            "stone_pickaxe",
            "iron_pickaxe",
            "golden_pickaxe",
            "diamond_pickaxe",
            "netherite_pickaxe",
            "wooden_axe",
            "stone_axe",
            "iron_axe",
            "diamond_axe",
            "wooden_sword",
            "stone_sword",
            "iron_sword",
            "diamond_sword",
            "wooden_hoe",
            "stone_hoe",
            "iron_hoe",
            "diamond_hoe"
    })
    void shouldIdentifyRestrictedVanillaToolsByPath(String toolPath) {
        assertTrue(TechnologyToolRestrictionHandler.isRestrictedToolName(toolPath));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "wooden_shovel",
            "stone_shovel",
            "iron_shovel",
            "golden_shovel",
            "diamond_shovel",
            "netherite_shovel"
    })
    void shouldPermitAllShovelsForSandGathering(String shovelName) {
        assertFalse(TechnologyToolRestrictionHandler.isRestrictedToolName(shovelName));
        assertTrue(TechnologyToolRestrictionHandler.isShovel(shovelName));
    }

    @Test
    void shouldCorrectlyRestrictVanillaPickaxesAndPermitShovels() {
        assertTrue(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.WOODEN_PICKAXE));
        assertTrue(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.IRON_PICKAXE));
        assertTrue(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.DIAMOND_PICKAXE));

        assertFalse(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.WOODEN_SHOVEL));
        assertFalse(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.IRON_SHOVEL));
        assertFalse(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.DIAMOND_SHOVEL));
    }

    @Test
    void shouldNotRestrictNonVanillaOrNonToolItems() {
        assertFalse(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(null));
        assertFalse(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.SAND));
        assertFalse(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.RED_SAND));
        assertFalse(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.BREAD));
        assertFalse(TechnologyToolRestrictionHandler.isRestrictedVanillaTool(Items.GLASS_BOTTLE));
    }

    @Test
    void shouldRecognizeShovelNamesCorrectly() {
        assertTrue(TechnologyToolRestrictionHandler.isShovel("iron_shovel"));
        assertTrue(TechnologyToolRestrictionHandler.isShovel("diamond_shovel"));
        assertFalse(TechnologyToolRestrictionHandler.isShovel("diamond_pickaxe"));
        assertFalse(TechnologyToolRestrictionHandler.isShovel("sand"));
        assertFalse(TechnologyToolRestrictionHandler.isShovel(null));
    }

    @Test
    void shouldNotConsiderUnmountedPlayerAsPilotingRobot() {
        assertFalse(TechnologyToolRestrictionHandler.isPilotingMiningRobot(null));
    }

    @Test
    void shouldSafelyHandleNullStateInRoboticsRequirementCheck() {
        assertFalse(TechnologyToolRestrictionHandler.requiresRoboticsToBreak(null));
    }

    @Test
    void shouldNotRequireRoboticsToBreakWorkbenches() {
        assertFalse(TechnologyToolRestrictionHandler.requiresRoboticsToBreak(Blocks.CRAFTING_TABLE.defaultBlockState()));
        assertTrue(CraftingTableBlock.class.isAssignableFrom(SandstoneWorkbenchBlock.class));
    }
}
