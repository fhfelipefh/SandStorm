package com.fhfelipefh.sandstorm.content.defense;

import com.fhfelipefh.sandstorm.content.block.entity.KineticShieldGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KineticShieldGeneratorTest {

    @BeforeEach
    void setUp() {
        KineticShieldTracker.clearAll();
    }

    @Test
    void shouldDefineShieldSpecifications() {
        assertEquals(20.0, KineticShieldGeneratorBlockEntity.SHIELD_RADIUS, 0.001);
        assertEquals(5, KineticShieldGeneratorBlockEntity.UPKEEP_COST);
        assertEquals(50, KineticShieldGeneratorBlockEntity.DEFLECTION_COST);
    }

    @Test
    void shouldTrackActiveShieldPositionsAndRadii() {
        ResourceKey<Level> overworld = Level.OVERWORLD;
        BlockPos center = new BlockPos(100, 64, 100);

        assertFalse(KineticShieldTracker.isInsideShield(overworld, center));

        KineticShieldTracker.registerShield(overworld, center, KineticShieldGeneratorBlockEntity.SHIELD_RADIUS);
        assertTrue(KineticShieldTracker.isInsideShield(overworld, center));

        BlockPos insideEdge = new BlockPos(115, 64, 100);
        assertTrue(KineticShieldTracker.isInsideShield(overworld, insideEdge));

        BlockPos outside = new BlockPos(125, 64, 100);
        assertFalse(KineticShieldTracker.isInsideShield(overworld, outside));

        KineticShieldTracker.unregisterShield(overworld, center);
        assertFalse(KineticShieldTracker.isInsideShield(overworld, center));
    }

    @Test
    void shouldIsolateShieldsByDimension() {
        ResourceKey<Level> overworld = Level.OVERWORLD;
        ResourceKey<Level> nether = Level.NETHER;
        BlockPos pos = new BlockPos(0, 70, 0);

        KineticShieldTracker.registerShield(overworld, pos, 20.0);
        assertTrue(KineticShieldTracker.isInsideShield(overworld, pos));
        assertFalse(KineticShieldTracker.isInsideShield(nether, pos));

        KineticShieldTracker.clearAll();
        assertFalse(KineticShieldTracker.isInsideShield(overworld, pos));
    }
}
