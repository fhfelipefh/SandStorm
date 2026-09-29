package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.MappedRegistry;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtmosphericTerraformerBlockEntityTest {

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

        assertNotNull(SandStormItems.POTABLE_WATER_BOTTLE);

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

    @Test
    void shouldProvideFluidStorageCapacityAndProperties() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        Storage<FluidVariant> storage = terraformer.getFluidStorage(Direction.UP);
        assertNotNull(storage);
        assertTrue(storage.supportsInsertion());
        assertFalse(storage.supportsExtraction());

        Iterator<StorageView<FluidVariant>> iterator = storage.iterator();
        assertTrue(iterator.hasNext());
        StorageView<FluidVariant> view = iterator.next();
        assertNotNull(view);
        assertEquals(0L, view.getAmount());
        assertEquals((long) terraformer.getMaxWater() * (FluidConstants.BUCKET / 1000), view.getCapacity());
        assertTrue(view.isResourceBlank());
    }

    @Test
    void shouldValidateItemPlacementRulesAcrossFaces() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_WATER_IN, new ItemStack(Items.WATER_BUCKET), Direction.UP));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_WATER_IN, new ItemStack(SandStormItems.POTABLE_WATER_BOTTLE), Direction.UP));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_WATER_IN, new ItemStack(SandStormItems.BRACKISH_WATER_BOTTLE), Direction.UP));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_WATER_IN, new ItemStack(Items.POTION), Direction.UP));
        assertFalse(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_WATER_IN, new ItemStack(Items.DIRT), Direction.UP));

        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_MINERAL, new ItemStack(SandStormItems.MINERAL_SALT), Direction.UP));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_MINERAL, new ItemStack(Items.BONE_MEAL), Direction.UP));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_MINERAL, new ItemStack(SandStormItems.RAW_LITHIUM_SALTS), Direction.UP));
        assertFalse(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_MINERAL, new ItemStack(Items.STICK), Direction.UP));

        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_SEEDS, new ItemStack(Items.WHEAT_SEEDS), Direction.UP));
        assertFalse(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_SEEDS, new ItemStack(Items.IRON_INGOT), Direction.UP));

        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_SAPLINGS, new ItemStack(Items.OAK_SAPLING), Direction.UP));
        assertFalse(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_SAPLINGS, new ItemStack(Items.DIAMOND), Direction.UP));

        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_FUEL, new ItemStack(SandStormItems.ELECTRIC_COMPONENT), Direction.UP));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_FUEL, new ItemStack(Items.REDSTONE), Direction.UP));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_FUEL, new ItemStack(Items.REDSTONE_BLOCK), Direction.UP));
        assertFalse(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_FUEL, new ItemStack(Items.COAL), Direction.UP));

        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(SandStormItems.CIRCUIT_BOARD), Direction.NORTH));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(SandStormItems.TECH_DISC), Direction.NORTH));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(SandStormItems.QUANTUM_MIND_MATRIX), Direction.NORTH));
        assertTrue(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(SandStormItems.SUPERCONDUCTOR_TOROID), Direction.NORTH));
        assertFalse(terraformer.canPlaceItemThroughFace(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, new ItemStack(Items.NETHER_STAR), Direction.NORTH));
    }

    @Test
    void shouldSyncContainerDataCorrectly() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        ContainerData data = terraformer.getDataAccess();
        assertEquals(15, data.getCount());

        assertEquals(1, data.get(11));
        data.set(11, 2);
        assertEquals(2, terraformer.getTier());
        assertEquals(2, data.get(11));

        assertEquals(0, data.get(13));
        data.set(13, 1);
        assertTrue(terraformer.isLightningEnabled());
        assertEquals(1, data.get(13));
    }

    @Test
    void shouldSetupTierWithAppropriateResources() {
        AtmosphericTerraformerBlockEntity terraformer = new AtmosphericTerraformerBlockEntity(
                BlockEntityTypes.BARREL,
                BlockPos.ZERO,
                Blocks.BARREL.defaultBlockState()
        );

        terraformer.setupTier(3);
        assertEquals(3, terraformer.getTier());
        assertEquals(terraformer.getMaxEnergy(), terraformer.getEnergy());
        assertEquals(terraformer.getMaxWater(), terraformer.getWaterAmount());
        assertEquals(1024, terraformer.getMineralUnits());
        assertEquals(512, terraformer.getSeedUnits());
        assertEquals(SandStormItems.QUANTUM_MIND_MATRIX, terraformer.getItem(AtmosphericTerraformerBlockEntity.SLOT_UPGRADE).getItem());
        assertEquals(SandStormItems.MINERAL_SALT, terraformer.getItem(AtmosphericTerraformerBlockEntity.SLOT_MINERAL).getItem());
        assertEquals(Items.OAK_SAPLING, terraformer.getItem(AtmosphericTerraformerBlockEntity.SLOT_SAPLINGS).getItem());
    }
}
