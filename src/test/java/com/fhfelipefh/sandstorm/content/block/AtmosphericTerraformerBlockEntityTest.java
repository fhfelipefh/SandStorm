package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtmosphericTerraformerBlockEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldInitializeWithDefaultTier1Specifications() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        assertEquals(1, terraformer.getTier());
        assertEquals(24, terraformer.getRadius());
        assertEquals(250000L, terraformer.getMaxEnergy());
        assertEquals(50000, terraformer.getMaxWater());
        assertEquals(40, terraformer.getEnergyCostPerTick());
        assertEquals(2, terraformer.getWaterCostPerTick());
        assertEquals(1, terraformer.getConversionsPerTick());
        assertEquals(7, terraformer.getContainerSize());
        assertFalse(terraformer.isActive());
    }

    @Test
    void shouldUpdateSpecificationsAcrossTiers() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        terraformer.setTier(2);
        assertEquals(2, terraformer.getTier());
        assertEquals(48, terraformer.getRadius());
        assertEquals(500000L, terraformer.getMaxEnergy());
        assertEquals(100000, terraformer.getMaxWater());
        assertEquals(100, terraformer.getEnergyCostPerTick());
        assertEquals(4, terraformer.getWaterCostPerTick());
        assertEquals(2, terraformer.getConversionsPerTick());

        terraformer.setTier(3);
        assertEquals(3, terraformer.getTier());
        assertEquals(80, terraformer.getRadius());
        assertEquals(1000000L, terraformer.getMaxEnergy());
        assertEquals(200000, terraformer.getMaxWater());
        assertEquals(250, terraformer.getEnergyCostPerTick());
        assertEquals(8, terraformer.getWaterCostPerTick());
        assertEquals(4, terraformer.getConversionsPerTick());

        terraformer.cycleTier();
        assertEquals(1, terraformer.getTier());
    }

    @Test
    void shouldManageWaterAndResourceBuffers() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        assertEquals(10000, terraformer.addWater(10000));
        assertEquals(10000, terraformer.getWaterAmount());

        assertEquals(40000, terraformer.addWater(100000));
        assertEquals(50000, terraformer.getWaterAmount());

        terraformer.setWaterAmount(25000);
        assertEquals(25000, terraformer.getWaterAmount());

        terraformer.addMineralUnits(150);
        assertEquals(150, terraformer.getMineralUnits());

        terraformer.addSeedUnits(75);
        assertEquals(75, terraformer.getSeedUnits());
    }

    @Test
    void shouldConfigureSlotsAndAutomationFaces() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        int[] topSlots = terraformer.getSlotsForFace(Direction.UP);
        assertEquals(5, topSlots.length);
        assertEquals(AtmosphericTerraformerBlockEntity.SLOT_WATER_IN, topSlots[0]);

        int[] bottomSlots = terraformer.getSlotsForFace(Direction.DOWN);
        assertEquals(1, bottomSlots.length);
        assertEquals(AtmosphericTerraformerBlockEntity.SLOT_WATER_OUT, bottomSlots[0]);

        int[] sideSlots = terraformer.getSlotsForFace(Direction.NORTH);
        assertEquals(5, sideSlots.length);

        assertTrue(terraformer.canTakeItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_WATER_OUT, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(terraformer.canTakeItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_WATER_IN, ItemStack.EMPTY, Direction.DOWN));
        assertFalse(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_WATER_OUT, ItemStack.EMPTY, Direction.UP));
    }

    @Test
    void shouldCorrectlyIdentifyAridAndTransformableBlocks() {
        assertTrue(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.SAND.defaultBlockState()));
        assertTrue(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.RED_SAND.defaultBlockState()));
        assertTrue(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.SUSPICIOUS_SAND.defaultBlockState()));
        assertTrue(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.SANDSTONE.defaultBlockState()));
        assertTrue(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.SMOOTH_SANDSTONE.defaultBlockState()));
        assertTrue(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.RED_SANDSTONE.defaultBlockState()));

        assertFalse(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.DIRT.defaultBlockState()));
        assertFalse(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.GRASS_BLOCK.defaultBlockState()));
        assertFalse(AtmosphericTerraformerBlockEntity.isAridBlock(Blocks.STONE.defaultBlockState()));

        assertTrue(AtmosphericTerraformerBlockEntity.isDirtBlock(Blocks.DIRT.defaultBlockState()));
        assertTrue(AtmosphericTerraformerBlockEntity.isDirtBlock(Blocks.COARSE_DIRT.defaultBlockState()));
        assertTrue(AtmosphericTerraformerBlockEntity.isDirtBlock(Blocks.ROOTED_DIRT.defaultBlockState()));

        assertTrue(AtmosphericTerraformerBlockEntity.isGrassBlock(Blocks.GRASS_BLOCK.defaultBlockState()));
    }

    @Test
    void shouldRecognizeValidSeedsAndSaplings() {
        assertTrue(AtmosphericTerraformerBlockEntity.isSeedItem(new ItemStack(Items.WHEAT_SEEDS)));
        assertTrue(AtmosphericTerraformerBlockEntity.isSeedItem(new ItemStack(Items.BEETROOT_SEEDS)));
        assertTrue(AtmosphericTerraformerBlockEntity.isSeedItem(new ItemStack(Items.PUMPKIN_SEEDS)));
        assertFalse(AtmosphericTerraformerBlockEntity.isSeedItem(new ItemStack(Items.DIRT)));

        assertTrue(AtmosphericTerraformerBlockEntity.isSaplingItem(new ItemStack(Items.OAK_SAPLING)));
        assertTrue(AtmosphericTerraformerBlockEntity.isSaplingItem(new ItemStack(Items.ACACIA_SAPLING)));
        assertTrue(AtmosphericTerraformerBlockEntity.isSaplingItem(new ItemStack(Items.MANGROVE_PROPAGULE)));
        assertFalse(AtmosphericTerraformerBlockEntity.isSaplingItem(new ItemStack(Items.STICK)));
    }

    @Test
    void shouldManageLightningAndDissipation() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        assertFalse(terraformer.isLightningEnabled());
        terraformer.setLightningEnabled(true);
        assertTrue(terraformer.isLightningEnabled());
        terraformer.toggleLightning();
        assertFalse(terraformer.isLightningEnabled());

        assertEquals(300, AtmosphericTerraformerBlockEntity.DISSIPATION_DURATION);
        assertFalse(terraformer.isDissipating());
        assertEquals(0, terraformer.getDissipatingTicks());

        terraformer.setDissipatingTicks(150);
        assertTrue(terraformer.isDissipating());
        assertEquals(150, terraformer.getDissipatingTicks());
    }
}

