package com.fhfelipefh.sandstorm.content.survival;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BedRestrictionHandlerTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "white_bed",
            "orange_bed",
            "magenta_bed",
            "light_blue_bed",
            "yellow_bed",
            "lime_bed",
            "pink_bed",
            "gray_bed",
            "light_gray_bed",
            "cyan_bed",
            "purple_bed",
            "blue_bed",
            "brown_bed",
            "green_bed",
            "red_bed",
            "black_bed"
    })
    void shouldIdentifyAllVanillaBedPaths(String bedPath) {
        assertTrue(BedRestrictionHandler.isBedPath(bedPath));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "sand",
            "red_sand",
            "stone",
            "iron_pickaxe",
            "wooden_door",
            "crafting_table"
    })
    void shouldNotIdentifyNonBedPaths(String nonBedPath) {
        assertFalse(BedRestrictionHandler.isBedPath(nonBedPath));
    }

    @Test
    void shouldHandleNullPathSafely() {
        assertFalse(BedRestrictionHandler.isBedPath(null));
    }

    @Test
    void shouldIdentifyVanillaBedIdentifiers() {
        assertTrue(BedRestrictionHandler.isBedIdentifier(Identifier.fromNamespaceAndPath("minecraft", "white_bed")));
        assertTrue(BedRestrictionHandler.isBedIdentifier(Identifier.fromNamespaceAndPath("minecraft", "red_bed")));
        assertFalse(BedRestrictionHandler.isBedIdentifier(Identifier.fromNamespaceAndPath("minecraft", "sand")));
        assertFalse(BedRestrictionHandler.isBedIdentifier(Identifier.fromNamespaceAndPath("other_mod", "white_bed")));
        assertFalse(BedRestrictionHandler.isBedIdentifier(null));
    }

    @Test
    void shouldIdentifyVanillaBedItems() {
        Item whiteBed = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft", "white_bed"));
        Item redBed = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft", "red_bed"));
        Item blackBed = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft", "black_bed"));

        assertTrue(BedRestrictionHandler.isBedItem(whiteBed));
        assertTrue(BedRestrictionHandler.isBedItem(redBed));
        assertTrue(BedRestrictionHandler.isBedItem(blackBed));

        assertFalse(BedRestrictionHandler.isBedItem(Items.SAND));
        assertFalse(BedRestrictionHandler.isBedItem(Items.IRON_INGOT));
        assertFalse(BedRestrictionHandler.isBedItem(Items.BREAD));
        assertFalse(BedRestrictionHandler.isBedItem(null));
    }

    @Test
    void shouldIdentifyVanillaBedBlocks() {
        Block whiteBedBlock = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", "white_bed"));
        Block redBedBlock = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", "red_bed"));

        assertTrue(BedRestrictionHandler.isBedBlock(whiteBedBlock.defaultBlockState()));
        assertTrue(BedRestrictionHandler.isBedBlock(redBedBlock.defaultBlockState()));

        assertFalse(BedRestrictionHandler.isBedBlock(Blocks.SAND.defaultBlockState()));
        assertFalse(BedRestrictionHandler.isBedBlock(Blocks.STONE.defaultBlockState()));
        assertFalse(BedRestrictionHandler.isBedBlock(null));
    }
}
