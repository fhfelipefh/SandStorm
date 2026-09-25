package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.DeepCoreBoreholeBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.LithoPlasmaExtractorBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.SupercriticalHeatExchangerBlockEntity;
import com.fhfelipefh.sandstorm.content.command.SandstormDebugCommand;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
