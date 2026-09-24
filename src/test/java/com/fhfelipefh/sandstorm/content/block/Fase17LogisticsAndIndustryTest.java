package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.gui.AutoAssemblyLineMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Fase17LogisticsAndIndustryTest {

    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data", "sandstorm");

    @Test
    void shouldRegisterPhase17BlockKeysAndMenus() {
        String[] blocks = {
                "sand_maglev_rail",
                "habitat_dome",
                "auto_assembly_line"
        };

        for (String blockId : blocks) {
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id(blockId));
            assertNotNull(key);
            assertEquals("sandstorm", key.identifier().getNamespace());
            assertEquals(blockId, key.identifier().getPath());
        }

        ResourceKey<MenuType<?>> menuKey = ResourceKey.create(Registries.MENU, SandStormMod.id("auto_assembly_line"));
        assertNotNull(menuKey);
        assertEquals("sandstorm", menuKey.identifier().getNamespace());
        assertEquals("auto_assembly_line", menuKey.identifier().getPath());
    }

    @Test
    void shouldCreateAutoAssemblyLineMenuAndVerifySlots() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(3);
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 1000);
        data.set(1, 20000);
        data.set(2, 30);
        data.set(3, 60);

        AutoAssemblyLineMenu menu = new AutoAssemblyLineMenu(null, 1, playerInv, container, data);
        assertEquals(39, menu.slots.size());
        assertEquals(1000, menu.getEnergy());
        assertEquals(20000, menu.getMaxEnergy());
        assertEquals(30, menu.getProgress());
        assertEquals(60, menu.getMaxProgress());
        assertFalse(menu.slots.get(1).mayPlace(ItemStack.EMPTY));
    }

    @Test
    void shouldHaveCompleteAssetsForPhase17() {
        String[] blocks = {
                "sand_maglev_rail",
                "habitat_dome",
                "auto_assembly_line"
        };

        for (String block : blocks) {
            Path blockstate = ASSETS_DIR.resolve("blockstates").resolve(block + ".json");
            Path blockModel = ASSETS_DIR.resolve("models").resolve("block").resolve(block + ".json");
            Path itemModel = ASSETS_DIR.resolve("models").resolve("item").resolve(block + ".json");
            Path itemDef = ASSETS_DIR.resolve("items").resolve(block + ".json");

            assertTrue(Files.exists(blockstate), "Missing blockstate: " + blockstate);
            assertTrue(Files.exists(blockModel), "Missing block model: " + blockModel);
            assertTrue(Files.exists(itemModel), "Missing item model: " + itemModel);
            assertTrue(Files.exists(itemDef), "Missing item 1.21.4 definition: " + itemDef);
        }
    }

    @Test
    void shouldHaveValidRecipesForPhase17() throws IOException {
        String[] recipes = {
                "sand_maglev_rail",
                "habitat_dome",
                "auto_assembly_line"
        };

        for (String recipeName : recipes) {
            Path recipePath = DATA_DIR.resolve("recipe").resolve(recipeName + ".json");
            assertTrue(Files.exists(recipePath), "Missing recipe: " + recipePath);

            try (FileReader reader = new FileReader(recipePath.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                assertTrue(json.has("type"));
                assertTrue(json.has("result"));
            }
        }
    }

    @Test
    void shouldHaveValidLootTablesForPhase17() throws IOException {
        String[] blocks = {
                "sand_maglev_rail",
                "habitat_dome",
                "auto_assembly_line"
        };

        for (String block : blocks) {
            Path lootPath = DATA_DIR.resolve("loot_table").resolve("blocks").resolve(block + ".json");
            assertTrue(Files.exists(lootPath), "Missing loot table: " + lootPath);

            try (FileReader reader = new FileReader(lootPath.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                assertTrue(json.has("type"));
                assertTrue(json.has("pools"));
                assertTrue(json.getAsJsonArray("pools").size() > 0);
            }
        }
    }
}
