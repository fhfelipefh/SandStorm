package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NutrientTerraformingTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testNutrientBombItemKey() {
        ResourceKey<Item> key = SandStormMod.itemKey("nutrient_bomb");
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals("nutrient_bomb", key.identifier().getPath());
    }

    @Test
    void testNutrientBombEntityKey() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("nutrient_bomb"));
        assertNotNull(key);
        assertEquals("sandstorm:nutrient_bomb", key.identifier().toString());
    }

    @Test
    void testIsSandBlockDetection() {
        assertTrue(NutrientTerraformingManager.isSandBlock(Blocks.SAND));
        assertTrue(NutrientTerraformingManager.isSandBlock(Blocks.RED_SAND));
        assertTrue(NutrientTerraformingManager.isSandBlock(Blocks.SANDSTONE));
        assertTrue(NutrientTerraformingManager.isSandBlock(Blocks.SMOOTH_SANDSTONE));
        assertTrue(NutrientTerraformingManager.isSandBlock(Blocks.CUT_SANDSTONE));
        assertTrue(NutrientTerraformingManager.isSandBlock(Blocks.RED_SANDSTONE));
        assertTrue(NutrientTerraformingManager.isSandBlock(Blocks.SUSPICIOUS_SAND));

        assertFalse(NutrientTerraformingManager.isSandBlock(Blocks.DIRT));
        assertFalse(NutrientTerraformingManager.isSandBlock(Blocks.GRASS_BLOCK));
        assertFalse(NutrientTerraformingManager.isSandBlock(Blocks.STONE));
        assertFalse(NutrientTerraformingManager.isSandBlock(Blocks.AIR));
    }

    @Test
    void testInoculatedTaskLifecycle() {
        NutrientTerraformingManager.clear();
        assertEquals(0, NutrientTerraformingManager.getActiveTaskCount());

        BlockPos target = new BlockPos(10, 64, 10);
        NutrientTerraformingManager.InoculatedTask task = new NutrientTerraformingManager.InoculatedTask(
                Level.OVERWORLD,
                target,
                NutrientTerraformingManager.Stage.SAND_TO_DIRT,
                100L
        );

        assertEquals(target, task.getPos());
        assertEquals(Level.OVERWORLD, task.getDimension());
        assertEquals(NutrientTerraformingManager.Stage.SAND_TO_DIRT, task.getStage());
        assertEquals(100L, task.getTriggerTick());

        task.setStage(NutrientTerraformingManager.Stage.DIRT_TO_GRASS);
        task.setTriggerTick(200L);
        assertEquals(NutrientTerraformingManager.Stage.DIRT_TO_GRASS, task.getStage());
        assertEquals(200L, task.getTriggerTick());

        task.setStage(NutrientTerraformingManager.Stage.GRASS_TO_FOLIAGE);
        assertEquals(NutrientTerraformingManager.Stage.GRASS_TO_FOLIAGE, task.getStage());
    }
}
