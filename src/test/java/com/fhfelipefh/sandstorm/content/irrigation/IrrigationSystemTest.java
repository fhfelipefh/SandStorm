package com.fhfelipefh.sandstorm.content.irrigation;

import com.fhfelipefh.sandstorm.content.block.IrrigationHoseBlock;
import com.fhfelipefh.sandstorm.content.block.IrrigationPumpBlock;
import com.fhfelipefh.sandstorm.content.block.IrrigationSprinklerBlock;
import com.fhfelipefh.sandstorm.content.block.entity.IrrigationSprinklerBlockEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IrrigationSystemTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "irrigation_pump",
            "irrigation_hose",
            "irrigation_sprinkler"
    })
    void shouldCreateValidBlockKeysForIrrigationBlocks(String blockPath) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id(blockPath));
        assertNotNull(key);
        assertEquals(Registries.BLOCK, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(blockPath, key.identifier().getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "irrigation_pump",
            "irrigation_hose",
            "irrigation_sprinkler"
    })
    void shouldCreateValidItemKeysForIrrigationItems(String itemPath) {
        ResourceKey<Item> key = SandStormMod.itemKey(itemPath);
        assertNotNull(key);
        assertEquals(Registries.ITEM, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(itemPath, key.identifier().getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "irrigation_pump",
            "irrigation_sprinkler"
    })
    void shouldCreateValidBlockEntityKeysForIrrigation(String entityPath) {
        ResourceKey<BlockEntityType<?>> key = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, SandStormMod.id(entityPath));
        assertNotNull(key);
        assertEquals(Registries.BLOCK_ENTITY_TYPE, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(entityPath, key.identifier().getPath());
    }

    @Test
    void shouldCreateValidMenuKeyForIrrigationPump() {
        ResourceKey<MenuType<?>> key = ResourceKey.create(Registries.MENU, SandStormMod.id("irrigation_pump_menu"));
        assertNotNull(key);
        assertEquals(Registries.MENU, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals("irrigation_pump_menu", key.identifier().getPath());
    }

    @Test
    void shouldValidateHoseBlockProperties() {
        assertEquals("north", IrrigationHoseBlock.NORTH.getName());
        assertEquals("south", IrrigationHoseBlock.SOUTH.getName());
        assertEquals("east", IrrigationHoseBlock.EAST.getName());
        assertEquals("west", IrrigationHoseBlock.WEST.getName());
        assertEquals("up", IrrigationHoseBlock.UP.getName());
        assertEquals(5, IrrigationHoseBlock.PROPERTY_BY_DIRECTION.size());
    }

    @Test
    void shouldValidateSprinklerBlockProperties() {
        assertEquals("active", IrrigationSprinklerBlock.ACTIVE.getName());
        assertEquals(2, IrrigationSprinklerBlock.ACTIVE.getPossibleValues().size());
        assertTrue(IrrigationSprinklerBlock.ACTIVE.getPossibleValues().contains(true));
        assertTrue(IrrigationSprinklerBlock.ACTIVE.getPossibleValues().contains(false));
    }

    @Test
    void shouldValidatePumpBlockProperties() {
        assertEquals("active", IrrigationPumpBlock.ACTIVE.getName());
        assertEquals("facing", IrrigationPumpBlock.FACING.getName());
        assertEquals(2, IrrigationPumpBlock.ACTIVE.getPossibleValues().size());
        assertEquals(4, IrrigationPumpBlock.FACING.getPossibleValues().size());
        assertTrue(IrrigationPumpBlock.FACING.getPossibleValues().contains(Direction.NORTH));
        assertTrue(IrrigationPumpBlock.FACING.getPossibleValues().contains(Direction.SOUTH));
        assertTrue(IrrigationPumpBlock.FACING.getPossibleValues().contains(Direction.EAST));
        assertTrue(IrrigationPumpBlock.FACING.getPossibleValues().contains(Direction.WEST));
    }

    @Test
    void testPressureRadiusScalingWithSprinklerCount() {
        assertEquals(15, computeReach(1));
        assertEquals(11, computeReach(2));
        assertEquals(9, computeReach(3));
        assertEquals(8, computeReach(4));
        assertEquals(5, computeReach(9));
        assertEquals(4, computeReach(16));
        assertEquals(3, computeReach(25));
        assertEquals(3, computeReach(100));
    }

    private static int computeReach(int count) {
        return Math.max(3, (int) Math.round(15.0 / Math.sqrt(count)));
    }

    @Test
    void testBiosphereBarriersBlocked() {
        assertTrue(IrrigationSprinklerBlockEntity.isBiosphereBarrier(Blocks.TINTED_GLASS));
        assertTrue(IrrigationSprinklerBlockEntity.isBiosphereBarrier(Blocks.IRON_BLOCK));
        assertTrue(IrrigationSprinklerBlockEntity.isBiosphereBarrier(Blocks.GLASS));
        assertFalse(IrrigationSprinklerBlockEntity.isBiosphereBarrier(Blocks.SAND));
        assertFalse(IrrigationSprinklerBlockEntity.isBiosphereBarrier(Blocks.AIR));
    }

    @Test
    void testObstacleDetection() {
        assertFalse(IrrigationSprinklerBlockEntity.isObstacle(Blocks.AIR.defaultBlockState()));
        assertFalse(IrrigationSprinklerBlockEntity.isObstacle(Blocks.WATER.defaultBlockState()));
        assertFalse(IrrigationSprinklerBlockEntity.isObstacle(Blocks.SHORT_GRASS.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isObstacle(Blocks.COBBLESTONE.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isObstacle(Blocks.STONE_BRICKS.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isObstacle(Blocks.TINTED_GLASS.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isObstacle(Blocks.GLASS.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isObstacle(Blocks.IRON_BLOCK.defaultBlockState()));
    }

    @Test
    void testAridBlockIdentification() {
        assertTrue(IrrigationSprinklerBlockEntity.isAridBlock(Blocks.SAND.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isAridBlock(Blocks.RED_SAND.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isAridBlock(Blocks.SANDSTONE.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isAridBlock(Blocks.SMOOTH_SANDSTONE.defaultBlockState()));
        assertFalse(IrrigationSprinklerBlockEntity.isAridBlock(Blocks.DIRT.defaultBlockState()));
        assertFalse(IrrigationSprinklerBlockEntity.isAridBlock(Blocks.GRASS_BLOCK.defaultBlockState()));
        assertFalse(IrrigationSprinklerBlockEntity.isAridBlock(Blocks.STONE.defaultBlockState()));
        assertFalse(IrrigationSprinklerBlockEntity.isAridBlock(Blocks.WATER.defaultBlockState()));
    }

    @Test
    void testDirtAndGrassBlockIdentification() {
        assertTrue(IrrigationSprinklerBlockEntity.isDirtBlock(Blocks.DIRT.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isDirtBlock(Blocks.COARSE_DIRT.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isDirtBlock(Blocks.ROOTED_DIRT.defaultBlockState()));
        assertFalse(IrrigationSprinklerBlockEntity.isDirtBlock(Blocks.SAND.defaultBlockState()));
        assertTrue(IrrigationSprinklerBlockEntity.isGrassBlock(Blocks.GRASS_BLOCK.defaultBlockState()));
        assertFalse(IrrigationSprinklerBlockEntity.isGrassBlock(Blocks.DIRT.defaultBlockState()));
    }
}
