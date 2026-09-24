package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.gui.BioreactorVatMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
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

public class Fase21ExtremophileMycologyTest {

    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data", "sandstorm");

    @Test
    void shouldRegisterPhase21BlockKeysAndMenus() {
        String[] blocks = {
                "radiotrophic_mycelium",
                "chitinolytic_fungus",
                "cryo_xerophilic_lichen",
                "halophyte_succulent",
                "dune_ephedra",
                "bioreactor_vat"
        };

        for (String blockId : blocks) {
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id(blockId));
            assertNotNull(key);
            assertEquals("sandstorm", key.identifier().getNamespace());
            assertEquals(blockId, key.identifier().getPath());
        }

        ResourceKey<MenuType<?>> menuKey = ResourceKey.create(Registries.MENU, SandStormMod.id("bioreactor_vat"));
        assertNotNull(menuKey);
        assertEquals("sandstorm", menuKey.identifier().getNamespace());
        assertEquals("bioreactor_vat", menuKey.identifier().getPath());
    }

    @Test
    void shouldRegisterPhase21MetaboliteItemKeys() {
        String[] items = {
                "radioprotective_melanin",
                "chitosan_extract",
                "trehalose_sugar",
                "osmolyte_glycerol",
                "neuroactive_alkaloids"
        };

        for (String itemId : items) {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, SandStormMod.id(itemId));
            assertNotNull(key);
            assertEquals("sandstorm", key.identifier().getNamespace());
            assertEquals(itemId, key.identifier().getPath());
        }
    }

    @Test
    void shouldCreateBioreactorVatMenuAndVerifySlots() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(4);
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 500);
        data.set(1, 10000);
        data.set(2, 40);
        data.set(3, 200);
        data.set(4, 1);
        data.set(5, 1);

        BioreactorVatMenu menu = new BioreactorVatMenu(null, 1, playerInv, container, data);
        assertEquals(40, menu.slots.size());
        assertEquals(500, menu.getEnergy());
        assertEquals(10000, menu.getMaxEnergy());
        assertEquals(40, menu.getProgress());
        assertEquals(200, menu.getMaxProgress());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isProcessing());
        assertFalse(menu.slots.get(3).mayPlace(ItemStack.EMPTY));
    }

    @Test
    void shouldHaveCompleteAssetsForPhase21Blocks() {
        String[] blocks = {
                "radiotrophic_mycelium",
                "chitinolytic_fungus",
                "cryo_xerophilic_lichen",
                "halophyte_succulent",
                "dune_ephedra",
                "bioreactor_vat"
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
    void shouldHaveCompleteAssetsForPhase21MetaboliteItems() {
        String[] items = {
                "radioprotective_melanin",
                "chitosan_extract",
                "trehalose_sugar",
                "osmolyte_glycerol",
                "neuroactive_alkaloids"
        };

        for (String item : items) {
            Path itemModel = ASSETS_DIR.resolve("models").resolve("item").resolve(item + ".json");
            Path itemDef = ASSETS_DIR.resolve("items").resolve(item + ".json");

            assertTrue(Files.exists(itemModel), "Missing item model: " + itemModel);
            assertTrue(Files.exists(itemDef), "Missing item 1.21.4 definition: " + itemDef);
        }
    }

    @Test
    void shouldHaveValidLootTablesForPhase21() throws IOException {
        String[] blocks = {
                "radiotrophic_mycelium",
                "chitinolytic_fungus",
                "cryo_xerophilic_lichen",
                "halophyte_succulent",
                "dune_ephedra",
                "bioreactor_vat"
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

    @Test
    void shouldHaveValidRecipeForBioreactorVat() throws IOException {
        Path recipePath = DATA_DIR.resolve("recipe").resolve("bioreactor_vat.json");
        assertTrue(Files.exists(recipePath), "Missing recipe: " + recipePath);

        try (FileReader reader = new FileReader(recipePath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertTrue(json.has("type"));
            assertTrue(json.has("result"));
            assertEquals("sandstorm:bioreactor_vat", json.getAsJsonObject("result").get("id").getAsString());
        }
    }
}
