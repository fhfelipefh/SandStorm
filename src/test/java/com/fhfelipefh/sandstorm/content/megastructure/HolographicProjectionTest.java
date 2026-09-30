package com.fhfelipefh.sandstorm.content.megastructure;

import com.fhfelipefh.sandstorm.client.renderer.MegastructureConstructorRenderState;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.MegastructureConstructorMenu;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HolographicProjectionTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testBlueprintSpatialBoundsCalculation() {
        for (MegastructureBlueprint bp : MegastructureBlueprint.values()) {
            BlockPos min = bp.getMinPos();
            BlockPos max = bp.getMaxPos();
            assertNotNull(min);
            assertNotNull(max);
            assertTrue(min.getX() <= max.getX());
            assertTrue(min.getY() <= max.getY());
            assertTrue(min.getZ() <= max.getZ());

            List<MegastructureBlueprint.BlockPlacement> placements = bp.getPlacements();
            assertNotNull(placements);
            assertFalse(placements.isEmpty());

            for (MegastructureBlueprint.BlockPlacement placement : placements) {
                BlockPos pos = placement.relativePos();
                assertTrue(pos.getX() >= min.getX() && pos.getX() <= max.getX());
                assertTrue(pos.getY() >= min.getY() && pos.getY() <= max.getY());
                assertTrue(pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ());
            }
        }
    }

    @Test
    void testConstructorBlockEntityProjectionNetworkSync() {
        MegastructureConstructorBlockEntity be = new MegastructureConstructorBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        CompoundTag initialTag = be.getUpdateTag(null);
        assertEquals(0, initialTag.getIntOr("blueprintIndex", -1));
        assertEquals(MegastructureConstructorBlockEntity.STATE_IDLE, initialTag.getIntOr("buildState", -1));

        be.setBlueprintIndex(1);
        assertEquals(1, be.getBlueprintIndex());
        assertEquals(MegastructureBlueprint.PLANETARY_CITADEL, be.getBlueprint());
        assertEquals(1, be.getUpdateTag(null).getIntOr("blueprintIndex", -1));

        be.setBlueprintIndex(2);
        assertEquals(2, be.getBlueprintIndex());
        assertEquals(MegastructureBlueprint.ORBITAL_LAUNCH_SILO, be.getBlueprint());
        assertEquals(2, be.getUpdateTag(null).getIntOr("blueprintIndex", -1));

        be.setBlueprintIndex(3);
        assertEquals(3, be.getBlueprintIndex());
        assertEquals(MegastructureBlueprint.DESERT_TECH_PYRAMID, be.getBlueprint());
        assertEquals(3, be.getUpdateTag(null).getIntOr("blueprintIndex", -1));
    }

    @Test
    void testConstructorMenuBlueprintSwitchingViaButtons() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(18);
        SimpleContainerData data = new SimpleContainerData(11);
        MegastructureConstructorMenu menu = new MegastructureConstructorMenu(null, 1, playerInv, container, data);

        assertTrue(menu.clickMenuButton(null, 1));
        assertEquals(1, menu.getBlueprintIndex());
        assertEquals("Planetary Citadel", menu.getBlueprintName());

        assertTrue(menu.clickMenuButton(null, 2));
        assertEquals(2, menu.getBlueprintIndex());
        assertEquals("Orbital Launch Silo", menu.getBlueprintName());

        assertTrue(menu.clickMenuButton(null, 3));
        assertEquals(3, menu.getBlueprintIndex());
        assertEquals("Desert Tech Pyramid", menu.getBlueprintName());

        assertTrue(menu.clickMenuButton(null, 0));
        assertEquals(0, menu.getBlueprintIndex());
        assertEquals("Biosphere Dome", menu.getBlueprintName());

        assertFalse(menu.clickMenuButton(null, -1));
        assertFalse(menu.clickMenuButton(null, 4));
    }

    @Test
    void testHologramRenderStateDataIntegrity() {
        MegastructureConstructorRenderState state = new MegastructureConstructorRenderState();
        assertEquals(Direction.NORTH, state.facing);
        assertEquals(0.0f, state.animationTicks);

        state.blueprint = MegastructureBlueprint.BIOSPHERE_DOME;
        state.minPos = state.blueprint.getMinPos();
        state.maxPos = state.blueprint.getMaxPos();

        assertNotNull(state.minPos);
        assertNotNull(state.maxPos);
        assertEquals(state.blueprint.getMinPos(), state.minPos);
        assertEquals(state.blueprint.getMaxPos(), state.maxPos);
    }
}
