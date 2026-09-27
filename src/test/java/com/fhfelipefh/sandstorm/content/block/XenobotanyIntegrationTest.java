package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.HeavySapBottleItem;
import com.fhfelipefh.sandstorm.content.item.SamplingSyringeItem;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XenobotanyIntegrationTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hydroponic_chamber",
            "xeno_grass_block",
            "heavy_sap_cactus",
            "salinized_sand",
            "halophyte_plant"
    })
    void shouldCreateValidBlockKeysForXenobotanyBlocks(String blockPath) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id(blockPath));
        assertNotNull(key);
        assertEquals(Registries.BLOCK, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(blockPath, key.identifier().getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "xeno_grass_seeds",
            "sampling_syringe",
            "heavy_sap_bottle",
            "flexible_biopolymer"
    })
    void shouldCreateValidItemKeysForXenobotanyItems(String itemPath) {
        ResourceKey<Item> key = SandStormMod.itemKey(itemPath);
        assertNotNull(key);
        assertEquals(Registries.ITEM, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(itemPath, key.identifier().getPath());
    }

    @Test
    void shouldValidateHeavySapCactusProperties() {
        assertEquals("sap_level", HeavySapCactusBlock.SAP_LEVEL.getName());
        assertEquals(4, HeavySapCactusBlock.SAP_LEVEL.getPossibleValues().size());
        assertTrue(HeavySapCactusBlock.SAP_LEVEL.getPossibleValues().contains(0));
        assertTrue(HeavySapCactusBlock.SAP_LEVEL.getPossibleValues().contains(3));

        assertEquals("age", HeavySapCactusBlock.AGE.getName());
        assertEquals(16, HeavySapCactusBlock.AGE.getPossibleValues().size());
    }

    @Test
    void shouldValidateHalophytePlantProperties() {
        assertEquals("age", HalophytePlantBlock.AGE.getName());
        assertEquals(4, HalophytePlantBlock.AGE.getPossibleValues().size());
        assertTrue(HalophytePlantBlock.AGE.getPossibleValues().contains(0));
        assertTrue(HalophytePlantBlock.AGE.getPossibleValues().contains(3));
    }

    @Test
    void shouldValidateHydroponicChamberProperties() {
        assertEquals("facing", HydroponicChamberBlock.FACING.getName());
        assertTrue(HydroponicChamberBlock.FACING.getPossibleValues().contains(Direction.NORTH));
        assertTrue(HydroponicChamberBlock.FACING.getPossibleValues().contains(Direction.SOUTH));
        assertTrue(HydroponicChamberBlock.FACING.getPossibleValues().contains(Direction.EAST));
        assertTrue(HydroponicChamberBlock.FACING.getPossibleValues().contains(Direction.WEST));

        assertEquals("lit", HydroponicChamberBlock.LIT.getName());
        assertEquals(2, HydroponicChamberBlock.LIT.getPossibleValues().size());
    }

    @Test
    void shouldValidateSamplingSyringeProperties() {
        assertEquals(64, SamplingSyringeItem.MAX_DURABILITY);
    }

    @Test
    void shouldValidateHeavySapBottleFoodProperties() {
        assertEquals(6, HeavySapBottleItem.SAP_FOOD.nutrition());
        assertEquals(0.8f, HeavySapBottleItem.SAP_FOOD.saturation());
    }
}
