package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PiezoQuartzTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testPiezoQuartzBlock() {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id("piezo_quartz_block"));
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals("piezo_quartz_block", key.identifier().getPath());
    }

    @Test
    void testBuddingPiezoQuartz() {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id("budding_piezo_quartz"));
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals("budding_piezo_quartz", key.identifier().getPath());
    }

    @Test
    void testPiezoQuartzClusterProperties() {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id("piezo_quartz_cluster"));
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals("piezo_quartz_cluster", key.identifier().getPath());

        assertEquals("facing", PiezoQuartzClusterBlock.FACING.getName());
        assertEquals("waterlogged", PiezoQuartzClusterBlock.WATERLOGGED.getName());
        assertTrue(PiezoQuartzClusterBlock.WATERLOGGED.getPossibleValues().contains(true));
        assertTrue(PiezoQuartzClusterBlock.WATERLOGGED.getPossibleValues().contains(false));
    }

    @Test
    void testItemKeys() {
        ResourceKey<Item> shardKey = SandStormMod.itemKey("piezo_quartz_shard");
        assertNotNull(shardKey);
        assertEquals("sandstorm", shardKey.identifier().getNamespace());
        assertEquals("piezo_quartz_shard", shardKey.identifier().getPath());

        ResourceKey<Item> resonatorKey = SandStormMod.itemKey("piezo_resonator");
        assertNotNull(resonatorKey);
        assertEquals("sandstorm", resonatorKey.identifier().getNamespace());
        assertEquals("piezo_resonator", resonatorKey.identifier().getPath());

        ResourceKey<Item> bucketKey = SandStormMod.itemKey("pressurized_fossil_fluid_bucket");
        assertNotNull(bucketKey);
        assertEquals("sandstorm", bucketKey.identifier().getNamespace());
        assertEquals("pressurized_fossil_fluid_bucket", bucketKey.identifier().getPath());
    }
}
