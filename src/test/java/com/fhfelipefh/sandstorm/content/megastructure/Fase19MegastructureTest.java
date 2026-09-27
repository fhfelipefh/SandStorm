package com.fhfelipefh.sandstorm.content.megastructure;

import com.fhfelipefh.sandstorm.content.block.MegastructureConstructorBlock;
import com.fhfelipefh.sandstorm.content.block.entity.MegastructureConstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.defense.KineticShieldTracker;
import com.fhfelipefh.sandstorm.content.entity.BuilderDroneEntity;
import com.fhfelipefh.sandstorm.content.gui.MegastructureConstructorMenu;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase19MegastructureTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testMegastructureBlueprintCatalog() {
        MegastructureBlueprint[] blueprints = MegastructureBlueprint.values();
        assertEquals(4, blueprints.length);

        assertEquals(MegastructureBlueprint.BIOSPHERE_DOME, MegastructureBlueprint.byIndex(0));
        assertEquals(MegastructureBlueprint.PLANETARY_CITADEL, MegastructureBlueprint.byIndex(1));
        assertEquals(MegastructureBlueprint.ORBITAL_LAUNCH_SILO, MegastructureBlueprint.byIndex(2));
        assertEquals(MegastructureBlueprint.DESERT_TECH_PYRAMID, MegastructureBlueprint.byIndex(3));
        assertEquals(MegastructureBlueprint.BIOSPHERE_DOME, MegastructureBlueprint.byIndex(99));

        for (MegastructureBlueprint bp : blueprints) {
            assertNotNull(bp.getId());
            assertNotNull(bp.getDisplayName());
            assertTrue(bp.getSizeX() > 0);
            assertTrue(bp.getSizeY() > 0);
            assertTrue(bp.getSizeZ() > 0);

            List<MegastructureBlueprint.BlockPlacement> placements = bp.getPlacements();
            assertNotNull(placements);
            assertFalse(placements.isEmpty());

            for (int i = 1; i < placements.size(); i++) {
                int prevY = placements.get(i - 1).relativePos().getY();
                int currY = placements.get(i).relativePos().getY();
                assertTrue(currY >= prevY, "Placements must be sorted strictly layer-by-layer Y ascending: " + bp.getId());
            }
        }
    }

    @Test
    void testBlockAndEntityRegistrationKeys() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("megastructure_constructor"));
        assertNotNull(blockKey);
        assertEquals("sandstorm", blockKey.identifier().getNamespace());
        assertEquals("megastructure_constructor", blockKey.identifier().getPath());

        assertNotNull(SandStormSoundEvents.MEGASTRUCTURE_CONSTRUCTOR_LASER);
        assertNotNull(SandStormSoundEvents.MEGASTRUCTURE_LAYER_COMPLETE);
        assertNotNull(SandStormSoundEvents.MEGASTRUCTURE_COMPLETE);
    }

    @Test
    void testConstructorBlockProperties() {
        assertEquals("facing", MegastructureConstructorBlock.FACING.getName());
        assertEquals("powered", MegastructureConstructorBlock.POWERED.getName());
        assertEquals("working", MegastructureConstructorBlock.WORKING.getName());
    }

    @Test
    void testConstructorBlockEntityDataAndProgress() {
        MegastructureConstructorBlockEntity be = new MegastructureConstructorBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        assertEquals(500000, be.getMaxEnergy());
        assertEquals(0, be.getEnergy());
        assertEquals(MegastructureBlueprint.BIOSPHERE_DOME, be.getBlueprint());
        assertEquals(MegastructureConstructorBlockEntity.STATE_IDLE, be.getBuildState());
        assertEquals(0, be.getPlacementIndex());
        assertTrue(be.getTotalPlacements() > 0);
        assertEquals(0, be.getPlacementPercent());

        be.setEnergy(100000);
        assertEquals(100000, be.getEnergy());

        be.setBlueprintIndex(1);
        assertEquals(MegastructureBlueprint.PLANETARY_CITADEL, be.getBlueprint());
        assertEquals(0, be.getPlacementIndex());

        be.setBuildSpeedMode(2);
        assertEquals(2, be.getBuildSpeedMode());

        be.setBuildState(MegastructureConstructorBlockEntity.STATE_BUILDING);
        assertEquals(MegastructureConstructorBlockEntity.STATE_BUILDING, be.getBuildState());

        BlockPos targetRel = be.getCurrentTargetRelPos();
        assertNotNull(targetRel);
        assertEquals(18, be.getContainerSize());
        assertEquals(18, be.getSlotsForFace(Direction.NORTH).length);
    }

    @Test
    void testBuilderDroneAttributes() {
        AttributeSupplier.Builder builder = BuilderDroneEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(30.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.35, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(0.40, supplier.getBaseValue(Attributes.FLYING_SPEED), 0.001);
        assertEquals(48.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
    }

    @Test
    void testConstructorMenuSlotLayoutAndTelemetry() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(18);
        SimpleContainerData data = new SimpleContainerData(11);
        data.set(0, 250000);
        data.set(1, 500000);
        data.set(2, 50);
        data.set(3, 100);
        data.set(4, 1);
        data.set(5, 2);
        data.set(6, 0);
        data.set(7, 45);
        data.set(8, 180);
        data.set(9, 3);
        data.set(10, 1);

        MegastructureConstructorMenu menu = new MegastructureConstructorMenu(null, 1, playerInv, container, data);

        assertEquals(54, menu.slots.size());
        assertEquals(250000, menu.getEnergy());
        assertEquals(500000, menu.getMaxEnergy());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isBuilding());
        assertFalse(menu.isDone());
        assertFalse(menu.isPausedStorm());
        assertTrue(menu.getLaserActive());
        assertEquals(45, menu.getConstructedBlocks());
        assertEquals(180, menu.getTotalBlocks());
        assertEquals(25, menu.getCompletionPercentage());
        assertEquals("Biosphere Dome", menu.getBlueprintName());
        assertEquals(26, menu.getEnergyScaled(52));
        assertEquals(13, menu.getProgressScaled(52));

        assertTrue(menu.slots.get(0).mayPlace(ItemStack.EMPTY));
        assertEquals(18, menu.slots.stream().filter(s -> s.container == container).count());
    }

    @Test
    void testSafeZoneRegistrationOnCompletion() {
        ResourceKey<Level> dim = Level.OVERWORLD;
        BlockPos center = new BlockPos(200, 64, 200);
        KineticShieldTracker.clearAll();
        assertFalse(KineticShieldTracker.isInsideShield(dim, center));

        KineticShieldTracker.registerShield(dim, center, 32.0);
        assertTrue(KineticShieldTracker.isInsideShield(dim, center));
        assertTrue(KineticShieldTracker.isInsideShield(dim, center.offset(15, 0, 15)));
        assertFalse(KineticShieldTracker.isInsideShield(dim, center.offset(40, 0, 40)));

        KineticShieldTracker.unregisterShield(dim, center);
        assertFalse(KineticShieldTracker.isInsideShield(dim, center));
    }
}
