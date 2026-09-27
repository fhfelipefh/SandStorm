package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WptRelayTowerTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void setUp() {
        WptRelayTowerManager.clear();
        WirelessSolarReceiverManager.clear();
    }

    @Test
    void testRegistrationAndUnregistration() {
        BlockPos pos = new BlockPos(100, 64, 100);
        WptRelayTowerManager.registerTower(Level.OVERWORLD, pos, 80L);
        assertEquals(1, WptRelayTowerManager.getTowers(Level.OVERWORLD).size());
        assertEquals(80L, WptRelayTowerManager.getTowers(Level.OVERWORLD).get(pos));

        WptRelayTowerManager.unregisterTower(Level.OVERWORLD, pos);
        assertEquals(0, WptRelayTowerManager.getTowers(Level.OVERWORLD).size());
    }

    @Test
    void testCoverageRadius128Blocks() {
        BlockPos towerPos = new BlockPos(0, 64, 0);
        WptRelayTowerManager.registerTower(Level.OVERWORLD, towerPos, 100L);

        BlockPos withinCoverage = new BlockPos(100, 64, 0);
        assertTrue(WptRelayTowerManager.isPositionCovered(Level.OVERWORLD, withinCoverage));
        assertEquals(100L, WptRelayTowerManager.getWptChargeAt(Level.OVERWORLD, withinCoverage));

        BlockPos edgeCoverage = new BlockPos(128, 64, 0);
        assertTrue(WptRelayTowerManager.isPositionCovered(Level.OVERWORLD, edgeCoverage));

        BlockPos outsideCoverage = new BlockPos(129, 64, 0);
        assertFalse(WptRelayTowerManager.isPositionCovered(Level.OVERWORLD, outsideCoverage));
        assertEquals(0L, WptRelayTowerManager.getWptChargeAt(Level.OVERWORLD, outsideCoverage));
    }

    @Test
    void testBlockStateProperties() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("wpt_relay_tower"));
        assertNotNull(blockKey);
        assertEquals("wpt_relay_tower", blockKey.identifier().getPath());

        ResourceKey<BlockEntityType<?>> beKey = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, SandStormMod.id("wpt_relay_tower"));
        assertNotNull(beKey);
        assertEquals("wpt_relay_tower", beKey.identifier().getPath());

        assertNotNull(WptRelayTowerBlock.ACTIVE);
    }
}
