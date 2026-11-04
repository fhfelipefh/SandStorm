package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WpcAndPistonEarlyGameTest {

    private static final Path RECIPES_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");
    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data");

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        Field holders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holders.setAccessible(true);
        for (MappedRegistry<?> registry : List.of((MappedRegistry<?>) BuiltInRegistries.BLOCK,
                (MappedRegistry<?>) BuiltInRegistries.ITEM, (MappedRegistry<?>) BuiltInRegistries.BLOCK_ENTITY_TYPE,
                (MappedRegistry<?>) BuiltInRegistries.ENTITY_TYPE)) {
            frozen.set(registry, false);
            if (holders.get(registry) == null) {
                holders.set(registry, new IdentityHashMap<>());
            }
        }
        frozen.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);
    }

    @Test
    void salvagedPolymerAndWpcPlanksMustBeProperlyRegistered() {
        assertNotNull(SandStormItems.SALVAGED_POLYMER);
        assertNotNull(SandStormBlocks.WPC_PLANKS);
        assertEquals("salvaged_polymer", BuiltInRegistries.ITEM.getKey(SandStormItems.SALVAGED_POLYMER).getPath());
        assertEquals("wpc_planks", BuiltInRegistries.BLOCK.getKey(SandStormBlocks.WPC_PLANKS).getPath());
    }

    @Test
    void pistonFromSandstoneMustUseSandstoneSlabAndNoPlanks() throws IOException {
        Path recipePath = RECIPES_DIR.resolve("piston_from_sandstone.json");
        assertTrue(Files.exists(recipePath));

        try (FileReader reader = new FileReader(recipePath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject key = json.getAsJsonObject("key");
            assertEquals("minecraft:sandstone_slab", key.get("P").getAsString());
            assertFalse(json.toString().contains("oak_planks"));
            assertEquals("minecraft:piston", json.getAsJsonObject("result").get("id").getAsString());
        }
    }

    @Test
    void pistonFromScrapMustBeCraftableWithScrapMetal() throws IOException {
        Path recipePath = RECIPES_DIR.resolve("piston_from_scrap.json");
        assertTrue(Files.exists(recipePath));

        try (FileReader reader = new FileReader(recipePath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject key = json.getAsJsonObject("key");
            assertEquals("sandstorm:scrap_metal", key.get("P").getAsString());
            assertEquals("minecraft:piston", json.getAsJsonObject("result").get("id").getAsString());
        }
    }

    @Test
    void wpcPlanksRecipeMustUseSalvagedPolymerAndSand() throws IOException {
        Path recipePath = RECIPES_DIR.resolve("wpc_planks.json");
        assertTrue(Files.exists(recipePath));

        try (FileReader reader = new FileReader(recipePath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject key = json.getAsJsonObject("key");
            assertEquals("sandstorm:salvaged_polymer", key.get("P").getAsString());
            assertEquals("minecraft:sand", key.get("S").getAsString());
            JsonObject result = json.getAsJsonObject("result");
            assertEquals("sandstorm:wpc_planks", result.get("id").getAsString());
            assertEquals(4, result.get("count").getAsInt());
        }
    }

    @Test
    void wpcPlanksMustBeRegisteredInMinecraftPlanksTags() throws IOException {
        Path blockPlanksTag = DATA_DIR.resolve("minecraft").resolve("tags").resolve("block").resolve("planks.json");
        Path itemPlanksTag = DATA_DIR.resolve("minecraft").resolve("tags").resolve("item").resolve("planks.json");

        assertTrue(Files.exists(blockPlanksTag));
        assertTrue(Files.exists(itemPlanksTag));

        try (FileReader reader = new FileReader(blockPlanksTag.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray values = json.getAsJsonArray("values");
            List<String> entries = new ArrayList<>();
            for (JsonElement e : values) {
                entries.add(e.getAsString());
            }
            assertTrue(entries.contains("sandstorm:wpc_planks"));
        }

        try (FileReader reader = new FileReader(itemPlanksTag.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray values = json.getAsJsonArray("values");
            List<String> entries = new ArrayList<>();
            for (JsonElement e : values) {
                entries.add(e.getAsString());
            }
            assertTrue(entries.contains("sandstorm:wpc_planks"));
        }
    }

    @Test
    void pistonConventionTagMustIncludePistons() throws IOException {
        Path pistonTag = DATA_DIR.resolve("c").resolve("tags").resolve("item").resolve("pistons.json");
        assertTrue(Files.exists(pistonTag));

        try (FileReader reader = new FileReader(pistonTag.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray values = json.getAsJsonArray("values");
            List<String> entries = new ArrayList<>();
            for (JsonElement e : values) {
                entries.add(e.getAsString());
            }
            assertTrue(entries.contains("minecraft:piston"));
            assertTrue(entries.contains("sandstorm:compact_slab_piston"));
            assertTrue(entries.contains("sandstorm:heavy_kinetic_piston"));
        }
    }

    @Test
    void allPistonRecipesMustAcceptPistonsTag() throws IOException {
        List<String> pistonRecipes = List.of(
                "printer_3d.json",
                "thumper.json",
                "autonomous_sonic_turret.json",
                "compact_slab_piston.json",
                "crushing_spike_gate.json",
                "heavy_kinetic_piston.json",
                "hydro_ballast_pump.json",
                "kinetic_ejector_trap.json",
                "retractable_spike_wall.json"
        );

        for (String filename : pistonRecipes) {
            Path p = RECIPES_DIR.resolve(filename);
            assertTrue(Files.exists(p), "Missing recipe: " + filename);
            try (FileReader reader = new FileReader(p.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject key = json.getAsJsonObject("key");
                boolean hasPistonTag = false;
                for (String k : key.keySet()) {
                    if ("#c:pistons".equals(key.get(k).getAsString())) {
                        hasPistonTag = true;
                        break;
                    }
                }
                assertTrue(hasPistonTag, filename + " should use #c:pistons");
            }
        }
    }
}

