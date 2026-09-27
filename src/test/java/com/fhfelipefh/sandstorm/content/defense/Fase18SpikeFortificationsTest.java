package com.fhfelipefh.sandstorm.content.defense;

import com.fhfelipefh.sandstorm.content.block.CorrosiveChitinSpikeWallBlock;
import com.fhfelipefh.sandstorm.content.block.CrushingSpikeGateBlock;
import com.fhfelipefh.sandstorm.content.block.ElectrifiedSpikeBarrierBlock;
import com.fhfelipefh.sandstorm.content.block.KineticFloorSpikesBlock;
import com.fhfelipefh.sandstorm.content.block.RetractableSpikeWallBlock;
import com.fhfelipefh.sandstorm.content.block.TitaniumSpikeWallBlock;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Fase18SpikeFortificationsTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data", "sandstorm");
    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    private static final String[] SPIKE_BLOCKS = {
            "titanium_spike_wall",
            "retractable_spike_wall",
            "electrified_spike_barrier",
            "corrosive_chitin_spike_wall",
            "kinetic_floor_spikes",
            "crushing_spike_gate"
    };

    @Test
    void shouldRegisterPhase18BlockKeys() {
        for (String blockId : SPIKE_BLOCKS) {
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id(blockId));
            assertNotNull(key);
            assertEquals("sandstorm", key.identifier().getNamespace());
            assertEquals(blockId, key.identifier().getPath());
        }
    }

    @Test
    void shouldDefineCorrectBlockProperties() {
        assertNotNull(TitaniumSpikeWallBlock.FACING);
        assertNotNull(RetractableSpikeWallBlock.FACING);
        assertNotNull(RetractableSpikeWallBlock.POWERED);
        assertNotNull(ElectrifiedSpikeBarrierBlock.FACING);
        assertNotNull(ElectrifiedSpikeBarrierBlock.POWERED);
        assertNotNull(CorrosiveChitinSpikeWallBlock.FACING);
        assertNotNull(KineticFloorSpikesBlock.TRIGGERED);
        assertNotNull(CrushingSpikeGateBlock.FACING);
        assertNotNull(CrushingSpikeGateBlock.OPEN);
    }

    @Test
    void shouldHaveCompleteAssetsForPhase18() throws IOException {
        for (String block : SPIKE_BLOCKS) {
            Path blockstate = ASSETS_DIR.resolve("blockstates").resolve(block + ".json");
            Path blockModel = ASSETS_DIR.resolve("models").resolve("block").resolve(block + ".json");
            Path itemModel = ASSETS_DIR.resolve("models").resolve("item").resolve(block + ".json");
            Path itemDef = ASSETS_DIR.resolve("items").resolve(block + ".json");
            Path texture = ASSETS_DIR.resolve("textures").resolve("block").resolve(block + ".png");

            assertTrue(Files.exists(blockstate), "Missing blockstate: " + blockstate);
            assertTrue(Files.exists(blockModel), "Missing block model: " + blockModel);
            assertTrue(Files.exists(itemModel), "Missing item model: " + itemModel);
            assertTrue(Files.exists(itemDef), "Missing item 1.21.4 definition: " + itemDef);
            assertTrue(Files.exists(texture), "Missing texture: " + texture);

            byte[] bytes = Files.readAllBytes(texture);
            assertTrue(bytes.length >= 8, "PNG file too small: " + texture);
            byte[] signature = new byte[8];
            System.arraycopy(bytes, 0, signature, 0, 8);
            assertArrayEquals(PNG_SIGNATURE, signature, "Invalid PNG header signature in: " + texture);
        }
    }

    @Test
    void shouldHaveValidLootTablesForPhase18() throws IOException {
        for (String block : SPIKE_BLOCKS) {
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
    void shouldHaveValidRecipesForPhase18() throws IOException {
        for (String block : SPIKE_BLOCKS) {
            Path recipePath = DATA_DIR.resolve("recipe").resolve(block + ".json");
            assertTrue(Files.exists(recipePath), "Missing recipe: " + recipePath);

            try (FileReader reader = new FileReader(recipePath.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                assertTrue(json.has("type"));
                assertTrue(json.has("result"));
            }
        }
    }

    @Test
    void shouldHaveLangEntriesAcrossAllSupportedLanguages() throws IOException {
        String[] langFiles = {"pt_br.json", "en_us.json", "es_es.json"};
        for (String langFile : langFiles) {
            Path langPath = ASSETS_DIR.resolve("lang").resolve(langFile);
            assertTrue(Files.exists(langPath), "Missing lang file: " + langPath);

            try (FileReader reader = new FileReader(langPath.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (String block : SPIKE_BLOCKS) {
                    String blockKey = "block.sandstorm." + block;
                    String itemKey = "item.sandstorm." + block;
                    String tooltipKey = "tooltip.sandstorm." + block + "_desc";

                    assertTrue(json.has(blockKey), "Missing " + blockKey + " in " + langFile);
                    assertTrue(json.has(itemKey), "Missing " + itemKey + " in " + langFile);
                    assertTrue(json.has(tooltipKey), "Missing " + tooltipKey + " in " + langFile);
                }
            }
        }
    }
}
