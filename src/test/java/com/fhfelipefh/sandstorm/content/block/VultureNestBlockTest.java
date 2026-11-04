package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VultureNestBlockTest {

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        Field holders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holders.setAccessible(true);
        for (MappedRegistry<?> registry : List.of((MappedRegistry<?>) BuiltInRegistries.BLOCK,
                (MappedRegistry<?>) BuiltInRegistries.ITEM, (MappedRegistry<?>) BuiltInRegistries.BLOCK_ENTITY_TYPE,
                (MappedRegistry<?>) BuiltInRegistries.ENTITY_TYPE)) {
            frozen.set(registry, false);
            if (holders.get(registry) == null) {
                holders.set(registry, new IdentityHashMap<>());
            }
        }
        frozen.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);
    }

    @Test
    void vultureNestBlockMustBeProperlyConfigured() {
        assertNotNull(SandStormBlocks.VULTURE_NEST);
        assertEquals(SoundType.COPPER, SandStormBlocks.VULTURE_NEST.defaultBlockState().getSoundType());

        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id("vulture_nest"));
        assertNotNull(key);
        assertEquals("vulture_nest", key.identifier().getPath());
        assertEquals("sandstorm", key.identifier().getNamespace());
    }

    @Test
    void vultureNestBlockShapeMustBeHalfSlabHeight() {
        VoxelShape shape = VultureNestBlock.SHAPE;
        assertNotNull(shape);
        assertFalse(shape.isEmpty());
        assertEquals(0.0, shape.min(Direction.Axis.Y), 0.001);
        assertEquals(0.5, shape.max(Direction.Axis.Y), 0.001);
    }

    @Test
    void questDropsMustYieldCraftingIngredientsNotFinalQuestItem() {
        QuestData shovelQuest = QuestRegistry.getQuest("silicon_shovel");
        assertNotNull(shovelQuest);
        List<VultureNestBlock.IngredientReward> shovelIngredients = VultureNestBlock.getCraftingIngredientsForQuest(shovelQuest);
        assertFalse(shovelIngredients.isEmpty());
        boolean hasRawSilicon = shovelIngredients.stream().anyMatch(s -> s.item() == SandStormItems.RAW_SILICON);
        boolean hasToolBase = shovelIngredients.stream().anyMatch(s -> s.item() == SandStormItems.TOOL_BASE);
        boolean hasShovel = shovelIngredients.stream().anyMatch(s -> s.item() == SandStormItems.SILICON_SHOVEL);
        assertTrue(hasRawSilicon);
        assertTrue(hasToolBase);
        assertFalse(hasShovel);

        QuestData furnaceQuest = QuestRegistry.getQuest("sandstone_furnace");
        assertNotNull(furnaceQuest);
        List<VultureNestBlock.IngredientReward> furnaceIngredients = VultureNestBlock.getCraftingIngredientsForQuest(furnaceQuest);
        assertTrue(furnaceIngredients.stream().anyMatch(s -> s.item() == Blocks.SANDSTONE.asItem()));

        QuestData circuitQuest = QuestRegistry.getQuest("circuit_manufacturing");
        assertNotNull(circuitQuest);
        List<VultureNestBlock.IngredientReward> circuitIngredients = VultureNestBlock.getCraftingIngredientsForQuest(circuitQuest);
        assertTrue(circuitIngredients.stream().anyMatch(s -> s.item() == SandStormItems.SILICON_WAFER));
        assertTrue(circuitIngredients.stream().anyMatch(s -> s.item() == SandStormItems.ELECTRIC_COMPONENT));
    }
}
