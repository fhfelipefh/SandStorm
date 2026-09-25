package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreBoreholeBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.LithoPlasmaExtractorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SupercriticalHeatExchangerBlockEntity;
import com.fhfelipefh.sandstorm.content.command.SandstormDebugCommand;
import com.fhfelipefh.sandstorm.content.gui.DeepCoreBoreholeMenu;
import com.fhfelipefh.sandstorm.content.gui.LithoPlasmaExtractorMenu;
import com.fhfelipefh.sandstorm.content.gui.SupercriticalHeatExchangerMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase31GeothermalWellTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldRegisterPhase31BlockAndItemKeys() {
        ResourceKey<Block> boreholeBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("deep_core_borehole"));
        ResourceKey<Block> extractorBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("litho_plasma_extractor"));
        ResourceKey<Block> exchangerBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("supercritical_heat_exchanger"));

        ResourceKey<Item> boreholeItem = SandStormMod.itemKey("deep_core_borehole");
        ResourceKey<Item> extractorItem = SandStormMod.itemKey("litho_plasma_extractor");
        ResourceKey<Item> exchangerItem = SandStormMod.itemKey("supercritical_heat_exchanger");
        ResourceKey<Item> drillBitItem = SandStormMod.itemKey("geothermal_core_drill_bit");
        ResourceKey<Item> lithiumSaltsItem = SandStormMod.itemKey("raw_lithium_salts");
        ResourceKey<Item> lithiumCapsuleItem = SandStormMod.itemKey("superheated_lithium_capsule");
        ResourceKey<Item> mantleAlloyItem = SandStormMod.itemKey("mantle_alloy_ingot");
        ResourceKey<Item> radiatorFinItem = SandStormMod.itemKey("thermal_radiator_fin");

        assertNotNull(boreholeBlock);
        assertNotNull(extractorBlock);
        assertNotNull(exchangerBlock);

        assertNotNull(boreholeItem);
        assertNotNull(extractorItem);
        assertNotNull(exchangerItem);
        assertNotNull(drillBitItem);
        assertNotNull(lithiumSaltsItem);
        assertNotNull(lithiumCapsuleItem);
        assertNotNull(mantleAlloyItem);
        assertNotNull(radiatorFinItem);

        assertEquals("deep_core_borehole", boreholeBlock.identifier().getPath());
        assertEquals("litho_plasma_extractor", extractorBlock.identifier().getPath());
        assertEquals("supercritical_heat_exchanger", exchangerBlock.identifier().getPath());
        assertEquals("geothermal_core_drill_bit", drillBitItem.identifier().getPath());
        assertEquals("raw_lithium_salts", lithiumSaltsItem.identifier().getPath());
        assertEquals("superheated_lithium_capsule", lithiumCapsuleItem.identifier().getPath());
        assertEquals("mantle_alloy_ingot", mantleAlloyItem.identifier().getPath());
        assertEquals("thermal_radiator_fin", radiatorFinItem.identifier().getPath());
    }

    @Test
    void shouldValidateBoreholeConstantsAndState() {
        assertEquals(500000, DeepCoreBoreholeBlockEntity.MAX_ENERGY);
        assertEquals(250, DeepCoreBoreholeBlockEntity.ENERGY_COST_PER_TICK);
        assertEquals(8000, DeepCoreBoreholeBlockEntity.MAX_FLUID);
        assertEquals(100, DeepCoreBoreholeBlockEntity.CYCLE_TICKS);
        assertEquals(0, DeepCoreBoreholeBlockEntity.SLOT_DRILL_BIT);
        assertEquals(1, DeepCoreBoreholeBlockEntity.SLOT_COOLANT_IN);
        assertEquals(2, DeepCoreBoreholeBlockEntity.SLOT_COOLANT_OUT);
        assertEquals(3, DeepCoreBoreholeBlockEntity.OUTPUT_START);
        assertEquals(9, DeepCoreBoreholeBlockEntity.OUTPUT_COUNT);

        DeepCoreBoreholeBlockEntity be = new DeepCoreBoreholeBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        be.setItem(0, new ItemStack(Items.IRON_INGOT, 1));
        assertEquals(1, be.getItem(0).getCount());
        assertTrue(be.getItem(0).is(Items.IRON_INGOT));
    }

    @Test
    void shouldValidateDeepCoreBoreholeInventoryAndAutomationRules() {
        DeepCoreBoreholeBlockEntity be = new DeepCoreBoreholeBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(12, be.getContainerSize());
        assertTrue(be.isEmpty());

        be.setItem(0, new ItemStack(Items.IRON_INGOT, 2));
        assertFalse(be.isEmpty());
        assertEquals(2, be.getItem(0).getCount());

        ItemStack removed = be.removeItem(0, 1);
        assertEquals(1, removed.getCount());
        assertEquals(1, be.getItem(0).getCount());

        ItemStack taken = be.removeItemNoUpdate(0);
        assertEquals(1, taken.getCount());
        assertTrue(be.getItem(0).isEmpty());

        be.setItem(3, new ItemStack(Items.DIAMOND, 5));
        be.clearContent();
        assertTrue(be.isEmpty());

        assertArrayEquals(new int[]{0, 1}, be.getSlotsForFace(Direction.UP));
        assertArrayEquals(new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, be.getSlotsForFace(Direction.DOWN));
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, be.getSlotsForFace(Direction.NORTH));

        assertTrue(be.canPlaceItemThroughFace(1, new ItemStack(Items.WATER_BUCKET), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(1, new ItemStack(Items.LAVA_BUCKET), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(2, new ItemStack(Items.BUCKET), Direction.DOWN));
        assertFalse(be.canPlaceItemThroughFace(3, new ItemStack(Items.DIAMOND), Direction.DOWN));

        assertFalse(be.canTakeItemThroughFace(0, new ItemStack(Items.STICK), Direction.UP));
        assertFalse(be.canTakeItemThroughFace(1, new ItemStack(Items.WATER_BUCKET), Direction.UP));
        assertTrue(be.canTakeItemThroughFace(2, new ItemStack(Items.BUCKET), Direction.DOWN));
        assertTrue(be.canTakeItemThroughFace(3, new ItemStack(Items.DIAMOND), Direction.DOWN));
    }

    @Test
    void shouldValidateDeepCoreBoreholeStateAndDataAccess() {
        DeepCoreBoreholeBlockEntity be = new DeepCoreBoreholeBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        be.setStoredEnergy(200000);
        assertEquals(200000, be.getStoredEnergy());
        be.setStoredEnergy(-100);
        assertEquals(0, be.getStoredEnergy());
        be.setStoredEnergy(999999);
        assertEquals(DeepCoreBoreholeBlockEntity.MAX_ENERGY, be.getStoredEnergy());

        be.setFluidAmount(3000);
        assertEquals(3000, be.getFluidAmount());
        be.setFluidAmount(-50);
        assertEquals(0, be.getFluidAmount());
        be.setFluidAmount(25000);
        assertEquals(DeepCoreBoreholeBlockEntity.MAX_FLUID, be.getFluidAmount());

        be.setCurrentDepth(-58);
        assertEquals(-58, be.getCurrentDepth());
        assertEquals(300, be.getTemperature());
        assertEquals(1, be.getPressure());

        be.setProgress(35);
        assertEquals(35, be.getProgress());

        ContainerData data = be.getDataAccess();
        assertEquals(12, data.getCount());
        data.set(0, 1234);
        data.set(1, 2);
        int expectedEnergy = (2 << 16) | 1234;
        assertEquals(expectedEnergy, be.getStoredEnergy());

        data.set(4, 45);
        assertEquals(45, data.get(4));

        data.set(6, -42);
        assertEquals(-42, be.getCurrentDepth());

        data.set(7, 500);
        assertEquals(500, be.getTemperature());

        data.set(8, 25);
        assertEquals(25, be.getPressure());

        data.set(9, 2000);
        assertEquals(2000, be.getFluidAmount());

        assertEquals(DeepCoreBoreholeBlockEntity.MAX_ENERGY & 0xFFFF, data.get(2));
        assertEquals((DeepCoreBoreholeBlockEntity.MAX_ENERGY >> 16) & 0xFFFF, data.get(3));
        assertEquals(DeepCoreBoreholeBlockEntity.CYCLE_TICKS, data.get(5));
    }

    @Test
    void shouldValidateLithoPlasmaExtractorConstantsAndState() {
        assertEquals(250000, LithoPlasmaExtractorBlockEntity.MAX_ENERGY);
        assertEquals(150, LithoPlasmaExtractorBlockEntity.ENERGY_COST_PER_TICK);
        assertEquals(120, LithoPlasmaExtractorBlockEntity.CYCLE_TICKS);
        assertEquals(0, LithoPlasmaExtractorBlockEntity.SLOT_SALT_IN);
        assertEquals(1, LithoPlasmaExtractorBlockEntity.SLOT_CANISTER_IN);
        assertEquals(2, LithoPlasmaExtractorBlockEntity.SLOT_BATTERY);
        assertEquals(3, LithoPlasmaExtractorBlockEntity.SLOT_LITHIUM_OUT);
        assertEquals(4, LithoPlasmaExtractorBlockEntity.SLOT_ALLOY_OUT);
        assertEquals(5, LithoPlasmaExtractorBlockEntity.SLOT_BYPRODUCT_OUT);

        LithoPlasmaExtractorBlockEntity be = new LithoPlasmaExtractorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        be.setItem(0, new ItemStack(Items.SUGAR, 16));
        assertEquals(16, be.getItem(0).getCount());
        assertTrue(be.getItem(0).is(Items.SUGAR));
    }

    @Test
    void shouldValidateLithoPlasmaExtractorInventoryAndAutomationRules() {
        LithoPlasmaExtractorBlockEntity be = new LithoPlasmaExtractorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(6, be.getContainerSize());
        assertTrue(be.isEmpty());

        be.setItem(0, new ItemStack(Items.SUGAR, 10));
        assertFalse(be.isEmpty());
        assertEquals(10, be.getItem(0).getCount());

        ItemStack removed = be.removeItem(0, 4);
        assertEquals(4, removed.getCount());
        assertEquals(6, be.getItem(0).getCount());

        ItemStack taken = be.removeItemNoUpdate(0);
        assertEquals(6, taken.getCount());
        assertTrue(be.getItem(0).isEmpty());

        assertArrayEquals(new int[]{0, 1}, be.getSlotsForFace(Direction.UP));
        assertArrayEquals(new int[]{3, 4, 5}, be.getSlotsForFace(Direction.DOWN));
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5}, be.getSlotsForFace(Direction.NORTH));

        assertTrue(be.canPlaceItemThroughFace(2, new ItemStack(Items.REDSTONE), Direction.NORTH));
        assertTrue(be.canPlaceItemThroughFace(2, new ItemStack(Items.REDSTONE_BLOCK), Direction.NORTH));
        assertFalse(be.canPlaceItemThroughFace(2, new ItemStack(Items.DIRT), Direction.NORTH));
        assertFalse(be.canPlaceItemThroughFace(3, new ItemStack(Items.DIAMOND), Direction.DOWN));

        assertFalse(be.canTakeItemThroughFace(0, new ItemStack(Items.SUGAR), Direction.UP));
        assertFalse(be.canTakeItemThroughFace(1, new ItemStack(Items.GLASS_BOTTLE), Direction.UP));
        assertTrue(be.canTakeItemThroughFace(2, new ItemStack(Items.REDSTONE), Direction.NORTH));
        assertTrue(be.canTakeItemThroughFace(3, new ItemStack(Items.DIAMOND), Direction.DOWN));
        assertTrue(be.canTakeItemThroughFace(4, new ItemStack(Items.IRON_INGOT), Direction.DOWN));
        assertTrue(be.canTakeItemThroughFace(5, new ItemStack(Items.REDSTONE), Direction.DOWN));
    }

    @Test
    void shouldValidateLithoPlasmaExtractorStateAndDataAccess() {
        LithoPlasmaExtractorBlockEntity be = new LithoPlasmaExtractorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        be.setStoredEnergy(120000);
        assertEquals(120000, be.getStoredEnergy());
        be.setProgress(60);
        assertEquals(60, be.getProgress());
        assertEquals(0, be.getPlasmaConcentration());

        ContainerData data = be.getDataAccess();
        assertEquals(8, data.getCount());

        data.set(0, 4321);
        data.set(1, 1);
        int expectedEnergy = (1 << 16) | 4321;
        assertEquals(expectedEnergy, be.getStoredEnergy());

        data.set(4, 90);
        assertEquals(90, be.getProgress());

        data.set(6, 75);
        assertEquals(75, be.getPlasmaConcentration());

        assertEquals(LithoPlasmaExtractorBlockEntity.MAX_ENERGY & 0xFFFF, data.get(2));
        assertEquals((LithoPlasmaExtractorBlockEntity.MAX_ENERGY >> 16) & 0xFFFF, data.get(3));
        assertEquals(LithoPlasmaExtractorBlockEntity.CYCLE_TICKS, data.get(5));
    }

    @Test
    void shouldValidateSupercriticalHeatExchangerConstantsAndState() {
        assertEquals(1000000, SupercriticalHeatExchangerBlockEntity.MAX_ENERGY);
        assertEquals(8000, SupercriticalHeatExchangerBlockEntity.MAX_WATER);
        assertEquals(2500, SupercriticalHeatExchangerBlockEntity.BASE_GEN_RATE);
        assertEquals(5000, SupercriticalHeatExchangerBlockEntity.FIN_GEN_RATE);
        assertEquals(10000, SupercriticalHeatExchangerBlockEntity.LITHIUM_GEN_RATE);
        assertEquals(0, SupercriticalHeatExchangerBlockEntity.SLOT_WATER_IN);
        assertEquals(1, SupercriticalHeatExchangerBlockEntity.SLOT_WATER_OUT);
        assertEquals(2, SupercriticalHeatExchangerBlockEntity.SLOT_THERMAL_CORE);
        assertEquals(3, SupercriticalHeatExchangerBlockEntity.SLOT_BATTERY);

        SupercriticalHeatExchangerBlockEntity be = new SupercriticalHeatExchangerBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        be.setItem(0, new ItemStack(Items.WATER_BUCKET, 1));
        assertEquals(1, be.getItem(0).getCount());
        assertTrue(be.getItem(0).is(Items.WATER_BUCKET));
    }

    @Test
    void shouldValidateSupercriticalHeatExchangerInventoryAndAutomationRules() {
        SupercriticalHeatExchangerBlockEntity be = new SupercriticalHeatExchangerBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(4, be.getContainerSize());
        assertTrue(be.isEmpty());

        assertArrayEquals(new int[]{0, 2}, be.getSlotsForFace(Direction.UP));
        assertArrayEquals(new int[]{1, 3}, be.getSlotsForFace(Direction.DOWN));
        assertArrayEquals(new int[]{0, 1, 2, 3}, be.getSlotsForFace(Direction.NORTH));

        assertTrue(be.canPlaceItemThroughFace(0, new ItemStack(Items.WATER_BUCKET), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(0, new ItemStack(Items.LAVA_BUCKET), Direction.UP));
        assertFalse(be.canPlaceItemThroughFace(1, new ItemStack(Items.BUCKET), Direction.DOWN));
        assertTrue(be.canPlaceItemThroughFace(3, new ItemStack(Items.REDSTONE), Direction.NORTH));
        assertTrue(be.canPlaceItemThroughFace(3, new ItemStack(Items.REDSTONE_BLOCK), Direction.NORTH));
        assertFalse(be.canPlaceItemThroughFace(3, new ItemStack(Items.DIRT), Direction.NORTH));

        assertFalse(be.canTakeItemThroughFace(0, new ItemStack(Items.WATER_BUCKET), Direction.UP));
        assertTrue(be.canTakeItemThroughFace(1, new ItemStack(Items.BUCKET), Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(2, new ItemStack(Items.IRON_INGOT), Direction.UP));
        assertTrue(be.canTakeItemThroughFace(3, new ItemStack(Items.REDSTONE), Direction.DOWN));
    }

    @Test
    void shouldValidateSupercriticalHeatExchangerStateAndDataAccess() {
        SupercriticalHeatExchangerBlockEntity be = new SupercriticalHeatExchangerBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        be.setStoredEnergy(750000);
        assertEquals(750000, be.getStoredEnergy());
        be.setStoredEnergy(-20);
        assertEquals(0, be.getStoredEnergy());
        be.setStoredEnergy(2000000);
        assertEquals(SupercriticalHeatExchangerBlockEntity.MAX_ENERGY, be.getStoredEnergy());

        be.setWaterAmount(3000);
        assertEquals(3000, be.getWaterAmount());
        be.setWaterAmount(-10);
        assertEquals(0, be.getWaterAmount());
        be.setWaterAmount(15000);
        assertEquals(SupercriticalHeatExchangerBlockEntity.MAX_WATER, be.getWaterAmount());

        assertEquals(0, be.getCurrentGenRate());
        assertEquals(0, be.getSteamPressure());

        be.setWaterAmount(2000);
        be.setStoredEnergy(100000);
        assertTrue(be.isOperating());

        ContainerData data = be.getDataAccess();
        assertEquals(8, data.getCount());

        data.set(0, 5678);
        data.set(1, 3);
        int expectedEnergy = (3 << 16) | 5678;
        assertEquals(expectedEnergy, be.getStoredEnergy());

        data.set(4, 4000);
        assertEquals(4000, be.getWaterAmount());

        data.set(5, 5000);
        assertEquals(5000, be.getCurrentGenRate());

        data.set(6, 80);
        assertEquals(80, be.getSteamPressure());

        assertEquals(SupercriticalHeatExchangerBlockEntity.MAX_ENERGY & 0xFFFF, data.get(2));
        assertEquals((SupercriticalHeatExchangerBlockEntity.MAX_ENERGY >> 16) & 0xFFFF, data.get(3));
    }

    @Test
    void shouldValidateSupercriticalHeatExchangerManagerTracking() {
        SupercriticalHeatExchangerManager.clear();
        BlockPos pos = new BlockPos(100, 64, 100);

        SupercriticalHeatExchangerManager.registerExchanger(Level.OVERWORLD, pos, 5000);
        assertEquals(5000, SupercriticalHeatExchangerManager.getGenRate(Level.OVERWORLD, pos));

        Map<BlockPos, Integer> map = SupercriticalHeatExchangerManager.getExchangers(Level.OVERWORLD);
        assertNotNull(map);
        assertTrue(map.containsKey(pos));
        assertEquals(5000, map.get(pos));

        SupercriticalHeatExchangerManager.unregisterExchanger(Level.OVERWORLD, pos);
        assertEquals(0, SupercriticalHeatExchangerManager.getGenRate(Level.OVERWORLD, pos));
        assertFalse(SupercriticalHeatExchangerManager.getExchangers(Level.OVERWORLD).containsKey(pos));

        SupercriticalHeatExchangerManager.clear();
    }

    @Test
    void shouldValidatePhase31BlockDefinitionsAndStates() {
        assertEquals("facing", DeepCoreBoreholeBlock.FACING.getName());
        assertEquals("lit", DeepCoreBoreholeBlock.LIT.getName());
        assertEquals("facing", LithoPlasmaExtractorBlock.FACING.getName());
        assertEquals("lit", LithoPlasmaExtractorBlock.LIT.getName());
        assertEquals("facing", SupercriticalHeatExchangerBlock.FACING.getName());
        assertEquals("lit", SupercriticalHeatExchangerBlock.LIT.getName());
    }

    @Test
    void shouldValidatePhase31MenusInstantiationAndSlots() {
        Inventory playerInv = new Inventory(null, null);

        SimpleContainer boreholeContainer = new SimpleContainer(12);
        SimpleContainerData boreholeData = new SimpleContainerData(12);
        boreholeData.set(0, 1000);
        boreholeData.set(1, 0);
        boreholeData.set(2, 500000 & 0xFFFF);
        boreholeData.set(3, (500000 >> 16) & 0xFFFF);
        boreholeData.set(4, 50);
        boreholeData.set(5, 100);
        boreholeData.set(6, -30);
        boreholeData.set(7, 350);
        boreholeData.set(8, 2);
        boreholeData.set(9, 4000);
        boreholeData.set(10, 1);
        boreholeData.set(11, 1);

        DeepCoreBoreholeMenu boreholeMenu = new DeepCoreBoreholeMenu(null, 1, playerInv, null, boreholeContainer, boreholeData);
        assertEquals(48, boreholeMenu.slots.size());
        assertEquals(1000, boreholeMenu.getStoredEnergy());
        assertEquals(500000, boreholeMenu.getMaxEnergy());
        assertEquals(50, boreholeMenu.getProgress());
        assertEquals(100, boreholeMenu.getCycleTicks());
        assertEquals(-30, boreholeMenu.getCurrentDepth());
        assertEquals(350, boreholeMenu.getTemperature());
        assertEquals(2, boreholeMenu.getPressure());
        assertEquals(4000, boreholeMenu.getFluidAmount());
        assertTrue(boreholeMenu.isDrilling());
        assertTrue(boreholeMenu.hasDrillBit());
        assertTrue(boreholeMenu.slots.get(1).mayPlace(new ItemStack(Items.WATER_BUCKET)));
        assertFalse(boreholeMenu.slots.get(1).mayPlace(new ItemStack(Items.LAVA_BUCKET)));
        assertFalse(boreholeMenu.slots.get(2).mayPlace(new ItemStack(Items.BUCKET)));
        assertFalse(boreholeMenu.slots.get(3).mayPlace(new ItemStack(Items.DIAMOND)));

        SimpleContainer extractorContainer = new SimpleContainer(6);
        SimpleContainerData extractorData = new SimpleContainerData(8);
        extractorData.set(0, 2000);
        extractorData.set(1, 0);
        extractorData.set(2, 250000 & 0xFFFF);
        extractorData.set(3, (250000 >> 16) & 0xFFFF);
        extractorData.set(4, 60);
        extractorData.set(5, 120);
        extractorData.set(6, 50);
        extractorData.set(7, 1);

        LithoPlasmaExtractorMenu extractorMenu = new LithoPlasmaExtractorMenu(null, 1, playerInv, null, extractorContainer, extractorData);
        assertEquals(42, extractorMenu.slots.size());
        assertEquals(2000, extractorMenu.getStoredEnergy());
        assertEquals(250000, extractorMenu.getMaxEnergy());
        assertEquals(60, extractorMenu.getProgress());
        assertEquals(120, extractorMenu.getCycleTicks());
        assertEquals(50, extractorMenu.getPlasmaConcentration());
        assertTrue(extractorMenu.isExtracting());
        assertTrue(extractorMenu.slots.get(2).mayPlace(new ItemStack(Items.REDSTONE)));
        assertFalse(extractorMenu.slots.get(2).mayPlace(new ItemStack(Items.DIRT)));
        assertFalse(extractorMenu.slots.get(3).mayPlace(new ItemStack(Items.DIAMOND)));

        SimpleContainer exchangerContainer = new SimpleContainer(4);
        SimpleContainerData exchangerData = new SimpleContainerData(8);
        exchangerData.set(0, 5000);
        exchangerData.set(1, 0);
        exchangerData.set(2, 1000000 & 0xFFFF);
        exchangerData.set(3, (1000000 >> 16) & 0xFFFF);
        exchangerData.set(4, 3000);
        exchangerData.set(5, 2500);
        exchangerData.set(6, 40);
        exchangerData.set(7, 1);

        SupercriticalHeatExchangerMenu exchangerMenu = new SupercriticalHeatExchangerMenu(null, 1, playerInv, null, exchangerContainer, exchangerData);
        assertEquals(40, exchangerMenu.slots.size());
        assertEquals(5000, exchangerMenu.getStoredEnergy());
        assertEquals(1000000, exchangerMenu.getMaxEnergy());
        assertEquals(3000, exchangerMenu.getWaterAmount());
        assertEquals(2500, exchangerMenu.getCurrentGenRate());
        assertEquals(40, exchangerMenu.getSteamPressure());
        assertTrue(exchangerMenu.isOperating());
        assertTrue(exchangerMenu.slots.get(0).mayPlace(new ItemStack(Items.WATER_BUCKET)));
        assertFalse(exchangerMenu.slots.get(0).mayPlace(new ItemStack(Items.LAVA_BUCKET)));
        assertFalse(exchangerMenu.slots.get(1).mayPlace(new ItemStack(Items.BUCKET)));
        assertTrue(exchangerMenu.slots.get(3).mayPlace(new ItemStack(Items.REDSTONE)));
        assertFalse(exchangerMenu.slots.get(3).mayPlace(new ItemStack(Items.DIRT)));
    }

    @Test
    void shouldValidatePhase31DataAndRecipeFilesExist() {
        assertTrue(new File("src/main/resources/assets/sandstorm/blockstates/deep_core_borehole.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/blockstates/litho_plasma_extractor.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/blockstates/supercritical_heat_exchanger.json").exists());

        assertTrue(new File("src/main/resources/assets/sandstorm/models/block/deep_core_borehole.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/block/deep_core_borehole_lit.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/block/litho_plasma_extractor.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/block/litho_plasma_extractor_lit.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/block/supercritical_heat_exchanger.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/block/supercritical_heat_exchanger_lit.json").exists());

        assertTrue(new File("src/main/resources/assets/sandstorm/items/deep_core_borehole.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/litho_plasma_extractor.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/supercritical_heat_exchanger.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/geothermal_core_drill_bit.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/raw_lithium_salts.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/superheated_lithium_capsule.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/mantle_alloy_ingot.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/thermal_radiator_fin.json").exists());

        assertTrue(new File("src/main/resources/data/sandstorm/recipe/deep_core_borehole.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/litho_plasma_extractor.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/supercritical_heat_exchanger.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/geothermal_core_drill_bit.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/superheated_lithium_capsule.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/mantle_alloy_ingot.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/thermal_radiator_fin.json").exists());

        assertTrue(new File("src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json").exists());
    }

    @Test
    void shouldIncludePhase31InDebugCommandSuite() {
        assertTrue(SandstormDebugCommand.getSupportedPhases().contains("31"));
        assertTrue(SandstormDebugCommand.getSupportedFacilities().contains("geothermal_well"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/main/resources/assets/sandstorm/textures/block/deep_core_borehole_front.png",
            "src/main/resources/assets/sandstorm/textures/block/deep_core_borehole_front_lit.png",
            "src/main/resources/assets/sandstorm/textures/block/deep_core_borehole_side.png",
            "src/main/resources/assets/sandstorm/textures/block/deep_core_borehole_top.png",
            "src/main/resources/assets/sandstorm/textures/block/litho_plasma_extractor_front.png",
            "src/main/resources/assets/sandstorm/textures/block/litho_plasma_extractor_front_lit.png",
            "src/main/resources/assets/sandstorm/textures/block/litho_plasma_extractor_side.png",
            "src/main/resources/assets/sandstorm/textures/block/litho_plasma_extractor_top.png",
            "src/main/resources/assets/sandstorm/textures/block/supercritical_heat_exchanger_front.png",
            "src/main/resources/assets/sandstorm/textures/block/supercritical_heat_exchanger_front_lit.png",
            "src/main/resources/assets/sandstorm/textures/block/supercritical_heat_exchanger_side.png",
            "src/main/resources/assets/sandstorm/textures/block/supercritical_heat_exchanger_top.png",
            "src/main/resources/assets/sandstorm/textures/item/deep_core_borehole.png",
            "src/main/resources/assets/sandstorm/textures/item/geothermal_core_drill_bit.png",
            "src/main/resources/assets/sandstorm/textures/item/litho_plasma_extractor.png",
            "src/main/resources/assets/sandstorm/textures/item/mantle_alloy_ingot.png",
            "src/main/resources/assets/sandstorm/textures/item/raw_lithium_salts.png",
            "src/main/resources/assets/sandstorm/textures/item/supercritical_heat_exchanger.png",
            "src/main/resources/assets/sandstorm/textures/item/superheated_lithium_capsule.png",
            "src/main/resources/assets/sandstorm/textures/item/thermal_radiator_fin.png"
    })
    void shouldValidatePhase31PngIntegrity(String path) throws IOException {
        File file = new File(path);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = fis.read(header);
            assertEquals(8, read);
            byte[] expectedPngHeader = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            assertArrayEquals(expectedPngHeader, header);
        }
    }
}
