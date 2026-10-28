package com.fhfelipefh.sandstorm.content.energy;

import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericCarbonExtractorBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.AtmosphericCarbonExtractorMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElectricFurnaceAndCarbonTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testResourceKeys() {
        ResourceKey<Block> furnaceBlockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("electric_arc_furnace"));
        ResourceKey<Item> furnaceItemKey = ResourceKey.create(Registries.ITEM, SandStormMod.id("electric_arc_furnace"));
        ResourceKey<Block> extractorBlockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("atmospheric_carbon_extractor"));
        ResourceKey<Item> extractorItemKey = ResourceKey.create(Registries.ITEM, SandStormMod.id("atmospheric_carbon_extractor"));

        assertEquals("electric_arc_furnace", furnaceBlockKey.identifier().getPath());
        assertEquals("electric_arc_furnace", furnaceItemKey.identifier().getPath());
        assertEquals("atmospheric_carbon_extractor", extractorBlockKey.identifier().getPath());
        assertEquals("atmospheric_carbon_extractor", extractorItemKey.identifier().getPath());
    }

    @Test
    void testAtmosphericCarbonExtractorMenuAndCombustion() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(3);
        SimpleContainerData data = new SimpleContainerData(7);
        data.set(0, 5000);
        data.set(1, 10000);
        data.set(2, 600);
        data.set(3, 1200);
        data.set(4, 1);
        data.set(5, 1);
        data.set(6, 2);

        AtmosphericCarbonExtractorMenu menu = new AtmosphericCarbonExtractorMenu(null, 1, playerInv, container, data);
        assertEquals(39, menu.slots.size());
        assertEquals(5000, menu.getEnergy());
        assertEquals(10000, menu.getMaxEnergy());
        assertEquals(600, menu.getProgress());
        assertEquals(1200, menu.getMaxProgress());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isProcessing());
        assertEquals(2, menu.getCombustionBoost());
        assertEquals(50, menu.getEnergyScaled(100));
        assertEquals(50, menu.getProgressScaled(100));

        assertFalse(menu.isDiamondMode());
        assertTrue(menu.slots.get(0).mayPlace(new ItemStack(Items.DIAMOND)));
        assertFalse(menu.slots.get(0).mayPlace(new ItemStack(Items.COAL)));
        assertFalse(menu.slots.get(1).mayPlace(new ItemStack(Items.COAL)));
        assertFalse(menu.slots.get(1).mayPlace(new ItemStack(Items.DIAMOND)));

        container.setItem(0, new ItemStack(Items.DIAMOND));
        assertTrue(menu.isDiamondMode());

        assertTrue(AtmosphericCarbonExtractorBlockEntity.isCombustionSource(Blocks.FIRE.defaultBlockState()));
        assertTrue(AtmosphericCarbonExtractorBlockEntity.isCombustionSource(Blocks.SOUL_FIRE.defaultBlockState()));
        assertTrue(AtmosphericCarbonExtractorBlockEntity.isCombustionSource(Blocks.CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, true)));
        assertFalse(AtmosphericCarbonExtractorBlockEntity.isCombustionSource(Blocks.CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, false)));
        assertTrue(AtmosphericCarbonExtractorBlockEntity.isCombustionSource(Blocks.LAVA.defaultBlockState()));
        assertTrue(AtmosphericCarbonExtractorBlockEntity.isCombustionSource(Blocks.MAGMA_BLOCK.defaultBlockState()));
        assertFalse(AtmosphericCarbonExtractorBlockEntity.isCombustionSource(Blocks.STONE.defaultBlockState()));
    }

    @Test
    void testRecipesAndLootTablesFiles() throws IOException {
        Path recipe1 = Path.of("src/main/resources/data/sandstorm/recipe/electric_arc_furnace.json");
        Path recipe2 = Path.of("src/main/resources/data/sandstorm/recipe/atmospheric_carbon_extractor.json");
        assertTrue(Files.exists(recipe1));
        assertTrue(Files.exists(recipe2));

        String recipe1Content = Files.readString(recipe1);
        assertTrue(recipe1Content.contains("sandstorm:electric_arc_furnace"));
        assertTrue(recipe1Content.contains("sandstorm:sandstone_furnace"));
        assertTrue(recipe1Content.contains("minecraft:copper_ingot"));

        String recipe2Content = Files.readString(recipe2);
        assertTrue(recipe2Content.contains("sandstorm:atmospheric_carbon_extractor"));
        assertTrue(recipe2Content.contains("minecraft:iron_bars"));
        assertTrue(recipe2Content.contains("minecraft:copper_ingot"));

        Path loot1 = Path.of("src/main/resources/data/sandstorm/loot_table/blocks/electric_arc_furnace.json");
        Path loot2 = Path.of("src/main/resources/data/sandstorm/loot_table/blocks/atmospheric_carbon_extractor.json");
        assertTrue(Files.exists(loot1));
        assertTrue(Files.exists(loot2));

        Path pickaxeTag = Path.of("src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json");
        String pickaxeContent = Files.readString(pickaxeTag);
        assertTrue(pickaxeContent.contains("sandstorm:electric_arc_furnace"));
        assertTrue(pickaxeContent.contains("sandstorm:atmospheric_carbon_extractor"));
    }

    @Test
    void testAssetIntegrity() throws IOException {
        List<String> textureFiles = List.of(
                "electric_arc_furnace_bottom.png",
                "electric_arc_furnace_top.png",
                "electric_arc_furnace_side.png",
                "electric_arc_furnace_front.png",
                "electric_arc_furnace_front_on.png",
                "atmospheric_carbon_extractor_bottom.png",
                "atmospheric_carbon_extractor_top.png",
                "atmospheric_carbon_extractor_side.png",
                "atmospheric_carbon_extractor_front.png",
                "atmospheric_carbon_extractor_front_on.png"
        );

        byte[] pngHeader = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        Path textureBase = Path.of("src/main/resources/assets/sandstorm/textures/block");

        for (String file : textureFiles) {
            Path path = textureBase.resolve(file);
            assertTrue(Files.exists(path), "Texture must exist: " + file);
            byte[] bytes = Files.readAllBytes(path);
            assertTrue(bytes.length >= 16, "Texture must not be empty: " + file);
            for (int i = 0; i < pngHeader.length; i++) {
                assertEquals(pngHeader[i], bytes[i], "Invalid PNG signature in " + file);
            }
        }

        List<Path> jsonAssets = List.of(
                Path.of("src/main/resources/assets/sandstorm/blockstates/electric_arc_furnace.json"),
                Path.of("src/main/resources/assets/sandstorm/models/block/electric_arc_furnace.json"),
                Path.of("src/main/resources/assets/sandstorm/models/block/electric_arc_furnace_on.json"),
                Path.of("src/main/resources/assets/sandstorm/models/item/electric_arc_furnace.json"),
                Path.of("src/main/resources/assets/sandstorm/items/electric_arc_furnace.json"),
                Path.of("src/main/resources/assets/sandstorm/blockstates/atmospheric_carbon_extractor.json"),
                Path.of("src/main/resources/assets/sandstorm/models/block/atmospheric_carbon_extractor.json"),
                Path.of("src/main/resources/assets/sandstorm/models/block/atmospheric_carbon_extractor_on.json"),
                Path.of("src/main/resources/assets/sandstorm/models/item/atmospheric_carbon_extractor.json"),
                Path.of("src/main/resources/assets/sandstorm/items/atmospheric_carbon_extractor.json")
        );

        for (Path p : jsonAssets) {
            assertTrue(Files.exists(p), "Asset file must exist: " + p);
            assertTrue(Files.size(p) > 0, "Asset file must not be empty: " + p);
        }
    }
}
