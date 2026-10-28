package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FurnaceTiersAndCrateTest {

    @Test
    void shouldDefineResourceKeysCorrectly() {
        ResourceKey<Block> crateKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("sandstone_crate"));
        assertEquals("sandstone_crate", crateKey.identifier().getPath());

        ResourceKey<Block> insKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("insulated_furnace"));
        assertEquals("insulated_furnace", insKey.identifier().getPath());

        ResourceKey<Block> steelKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("steel_furnace"));
        assertEquals("steel_furnace", steelKey.identifier().getPath());
    }

    @Test
    void shouldHaveValidDesalinationRecipe() throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/desalination_filter.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("minecraft:bucket"));
            assertTrue(json.contains("#c:sandstones"));
        }
    }

    @Test
    void shouldHaveBrackishAquiferDroppingSalt() throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/loot_table/blocks/brackish_aquifer.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:mineral_salt"));
        }
    }

    @Test
    void shouldHaveValidCrateAndFurnaceRecipes() throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/sandstone_crate.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:sandstone_crate"));
        }
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/insulated_furnace.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:insulated_furnace"));
        }
        try (InputStream stream = getClass().getResourceAsStream("/data/sandstorm/recipe/steel_furnace.json")) {
            assertNotNull(stream);
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("sandstorm:steel_furnace"));
        }
    }
}
