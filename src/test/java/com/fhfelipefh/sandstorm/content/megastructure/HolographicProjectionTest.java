package com.fhfelipefh.sandstorm.content.megastructure;

import com.fhfelipefh.sandstorm.client.renderer.MegastructureConstructorRenderState;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.MegastructureConstructorMenu;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
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

    @Test
    void testBlueprintMaterialCostsCalculation() {
        for (MegastructureBlueprint bp : MegastructureBlueprint.values()) {
            List<MegastructureBlueprint.MaterialCost> costs = bp.getMaterialCosts();
            assertNotNull(costs);
            assertFalse(costs.isEmpty());

            int total = 0;
            for (MegastructureBlueprint.MaterialCost cost : costs) {
                assertNotNull(cost.item());
                assertTrue(cost.count() > 0);
                total += cost.count();
            }
            assertEquals(bp.getPlacements().size(), total);
            assertEquals(costs, bp.getMaterialCosts());
        }
    }

    @Test
    void testConstructorMaterialReadinessCalculation() {
        MegastructureConstructorBlockEntity be = new MegastructureConstructorBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );
        assertEquals(0, be.getMaterialReadinessPercent());

        MegastructureBlueprint bp = be.getBlueprint();
        List<MegastructureBlueprint.MaterialCost> costs = bp.getMaterialCosts();
        MegastructureBlueprint.MaterialCost firstCost = costs.getFirst();

        be.setItem(0, new ItemStack(firstCost.item(), 10));
        assertTrue(be.countAvailableItems(firstCost.item()) >= 10);
        assertTrue(be.getMaterialReadinessPercent() >= 0);

        int slot = 0;
        for (MegastructureBlueprint.MaterialCost cost : costs) {
            int remaining = cost.count();
            while (remaining > 0 && slot < 18) {
                int toPut = Math.min(remaining, 64);
                be.setItem(slot++, new ItemStack(cost.item(), toPut));
                remaining -= toPut;
            }
        }
        assertTrue(be.getMaterialReadinessPercent() > 0);
    }

    @Test
    void testConstructorMenuBufferItemCountAndReadiness() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(18);
        SimpleContainerData data11 = new SimpleContainerData(11);
        MegastructureConstructorMenu menu11 = new MegastructureConstructorMenu(null, 1, playerInv, container, data11);
        assertEquals(0, menu11.getMaterialReadinessPercent());

        SimpleContainerData data12 = new SimpleContainerData(12);
        data12.set(11, 75);
        MegastructureConstructorMenu menu12 = new MegastructureConstructorMenu(null, 2, playerInv, container, data12);
        assertEquals(75, menu12.getMaterialReadinessPercent());

        MegastructureBlueprint bp = menu12.getBlueprint();
        MegastructureBlueprint.MaterialCost firstCost = bp.getMaterialCosts().getFirst();
        assertEquals(0, menu12.countInBuffer(firstCost.item()));

        ItemStack stack = new ItemStack(firstCost.item(), 15);
        stack.set(DataComponents.MAX_STACK_SIZE, 64);
        container.setItem(0, stack);
        assertEquals(15, menu12.countInBuffer(firstCost.item()));
    }
}
