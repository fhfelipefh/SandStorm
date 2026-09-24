package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Fase16BiomesAndFeaturesTest {

    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data", "sandstorm");

    @Test
    void shouldRegisterPhase16BlocksAndItems() {
        String[] blockIds = {
                "fulgurite_glass",
                "electrified_sand",
                "fossilized_amber",
                "thermal_spring_stone",
                "ancient_reed_block"
        };
        for (String id : blockIds) {
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id(id));
            assertNotNull(key);
            assertEquals("sandstorm", key.identifier().getNamespace());
            assertEquals(id, key.identifier().getPath());
        }

        String[] itemIds = {
                "ancient_seed",
                "ancient_reed"
        };
        for (String id : itemIds) {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, SandStormMod.id(id));
            assertNotNull(key);
            assertEquals("sandstorm", key.identifier().getNamespace());
            assertEquals(id, key.identifier().getPath());
        }
    }

    @Test
    void shouldHaveValidPhase16PlacedFeatureKeys() {
        assertNotNull(SandStormWorldGen.FULGURITE_MONOLITH_KEY);
        assertEquals(Registries.PLACED_FEATURE, SandStormWorldGen.FULGURITE_MONOLITH_KEY.registryKey());
        assertEquals("sandstorm", SandStormWorldGen.FULGURITE_MONOLITH_KEY.identifier().getNamespace());
        assertEquals("fulgurite_monolith", SandStormWorldGen.FULGURITE_MONOLITH_KEY.identifier().getPath());

        assertNotNull(SandStormWorldGen.FOSSILIZED_OASIS_KEY);
        assertEquals(Registries.PLACED_FEATURE, SandStormWorldGen.FOSSILIZED_OASIS_KEY.registryKey());
        assertEquals("sandstorm", SandStormWorldGen.FOSSILIZED_OASIS_KEY.identifier().getNamespace());
        assertEquals("fossilized_oasis", SandStormWorldGen.FOSSILIZED_OASIS_KEY.identifier().getPath());
    }

    @Test
    void shouldHaveValidLootTablesForPhase16Blocks() throws IOException {
        String[] blocks = {
                "fulgurite_glass",
                "electrified_sand",
                "fossilized_amber",
                "thermal_spring_stone",
                "ancient_reed_block"
        };

        for (String block : blocks) {
            Path lootTablePath = DATA_DIR.resolve("loot_table").resolve("blocks").resolve(block + ".json");
            assertTrue(Files.exists(lootTablePath), "Loot table missing for " + block + " at " + lootTablePath);

            try (FileReader reader = new FileReader(lootTablePath.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                assertTrue(json.has("type"));
                assertTrue(json.has("pools"));
                assertTrue(json.getAsJsonArray("pools").size() > 0);
            }
        }
    }

    @Test
    void shouldHaveCompleteAssetsForPhase16() {
        String[] blocks = {
                "fulgurite_glass",
                "electrified_sand",
                "fossilized_amber",
                "thermal_spring_stone",
                "ancient_reed_block"
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

        String[] items = {
                "ancient_seed",
                "ancient_reed"
        };

        for (String item : items) {
            Path itemModel = ASSETS_DIR.resolve("models").resolve("item").resolve(item + ".json");
            Path itemDef = ASSETS_DIR.resolve("items").resolve(item + ".json");
            Path texture = ASSETS_DIR.resolve("textures").resolve("item").resolve(item + ".png");

            assertTrue(Files.exists(itemModel), "Missing item model: " + itemModel);
            assertTrue(Files.exists(itemDef), "Missing item 1.21.4 definition: " + itemDef);
            assertTrue(Files.exists(texture), "Missing item texture: " + texture);
        }
    }
}
