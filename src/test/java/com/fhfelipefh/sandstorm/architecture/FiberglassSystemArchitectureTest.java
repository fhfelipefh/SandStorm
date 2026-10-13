package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.entity.FiberglassChemicalPipeBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.StormWindTurbineBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipeRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FiberglassSystemArchitectureTest {

    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data", "sandstorm");
    private static final Path MINECRAFT_DATA_DIR = Path.of("src", "main", "resources", "data", "minecraft");

    private static final List<String> FIBERGLASS_BLOCK_IDS = List.of(
            "storm_wind_turbine",
            "fiberglass_corrugated_sheet",
            "fiberglass_corrugated_slab",
            "fiberglass_corrugated_pane",
            "fiberglass_chemical_pipe"
    );

    private static final List<String> FIBERGLASS_ITEM_IDS = List.of(
            "fiberglass_turbine_blade",
            "composite_recurve_bow",
            "sand_depth_probe"
    );

    @BeforeAll
    static void init() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

        assertNotNull(SandStormBlocks.STORM_WIND_TURBINE);
        assertNotNull(SandStormItems.FIBERGLASS_TURBINE_BLADE);

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            Item blockItem = block.asItem();
            if (blockItem != null && !blockItem.builtInRegistryHolder().areComponentsBound()) {
                blockItem.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    @Test
    void allFiberglassBlocksMustBeRegisteredAndConfigured() {
        assertNotNull(SandStormBlocks.STORM_WIND_TURBINE);
        assertNotNull(SandStormBlocks.FIBERGLASS_CORRUGATED_SHEET);
        assertNotNull(SandStormBlocks.FIBERGLASS_CORRUGATED_SLAB);
        assertNotNull(SandStormBlocks.FIBERGLASS_CORRUGATED_PANE);
        assertNotNull(SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE);

        assertNotNull(SandStormBlocks.STORM_WIND_TURBINE_BE);
        assertNotNull(SandStormBlocks.FIBERGLASS_CHEMICAL_PIPE_BE);

        assertEquals(25000L, StormWindTurbineBlockEntity.TURBINE_CAPACITY);
        assertEquals(250L, StormWindTurbineBlockEntity.TURBINE_MAX_EXTRACT);
        assertEquals(12L, StormWindTurbineBlockEntity.BASE_GENERATION);

        assertEquals(16200L, FiberglassChemicalPipeBlockEntity.TRANSFER_RATE);
    }

    @Test
    void allFiberglassItemsMustBeRegistered() {
        assertNotNull(SandStormItems.FIBERGLASS_TURBINE_BLADE);
        assertNotNull(SandStormItems.COMPOSITE_RECURVE_BOW);
        assertNotNull(SandStormItems.SAND_DEPTH_PROBE);
    }

    @Test
    void allFiberglassBlocksMustHaveCompleteAssetChain() {
        Path blockstatesDir = ASSETS_DIR.resolve("blockstates");
        Path modelsBlockDir = ASSETS_DIR.resolve("models").resolve("block");
        Path itemsDir = ASSETS_DIR.resolve("items");
        Path texturesBlockDir = ASSETS_DIR.resolve("textures").resolve("block");

        for (String id : FIBERGLASS_BLOCK_IDS) {
            Path blockstate = blockstatesDir.resolve(id + ".json");
            assertTrue(Files.exists(blockstate), "Missing blockstate: " + blockstate);

            Path itemDef = itemsDir.resolve(id + ".json");
            assertTrue(Files.exists(itemDef), "Missing item definition: " + itemDef);

            if (!id.equals("fiberglass_corrugated_pane")) {
                Path blockModel = modelsBlockDir.resolve(id + ".json");
                assertTrue(Files.exists(blockModel), "Missing block model: " + blockModel);
            } else {
                assertTrue(Files.exists(modelsBlockDir.resolve("fiberglass_corrugated_pane_post.json")));
            }
        }

        assertTrue(Files.exists(texturesBlockDir.resolve("storm_wind_turbine.png")));
        assertTrue(Files.exists(texturesBlockDir.resolve("storm_wind_turbine_top.png")));
        assertTrue(Files.exists(texturesBlockDir.resolve("fiberglass_corrugated_sheet.png")));
        assertTrue(Files.exists(texturesBlockDir.resolve("fiberglass_chemical_pipe.png")));
    }

    @Test
    void allFiberglassItemsMustHaveCompleteAssetChain() {
        Path itemsDir = ASSETS_DIR.resolve("items");
        Path modelsItemDir = ASSETS_DIR.resolve("models").resolve("item");
        Path texturesItemDir = ASSETS_DIR.resolve("textures").resolve("item");

        for (String id : FIBERGLASS_ITEM_IDS) {
            Path itemDef = itemsDir.resolve(id + ".json");
            assertTrue(Files.exists(itemDef), "Missing item definition: " + itemDef);

            Path modelItem = modelsItemDir.resolve(id + ".json");
            assertTrue(Files.exists(modelItem), "Missing item model: " + modelItem);

            Path texture = texturesItemDir.resolve(id + ".png");
            assertTrue(Files.exists(texture), "Missing item texture: " + texture);
        }

        assertTrue(Files.exists(modelsItemDir.resolve("composite_recurve_bow_pulling_0.json")));
        assertTrue(Files.exists(modelsItemDir.resolve("composite_recurve_bow_pulling_1.json")));
        assertTrue(Files.exists(modelsItemDir.resolve("composite_recurve_bow_pulling_2.json")));
        assertTrue(Files.exists(texturesItemDir.resolve("composite_recurve_bow_pulling_0.png")));
        assertTrue(Files.exists(texturesItemDir.resolve("composite_recurve_bow_pulling_1.png")));
        assertTrue(Files.exists(texturesItemDir.resolve("composite_recurve_bow_pulling_2.png")));
    }

    @Test
    void allFiberglassBlocksMustHaveLootTablesAndMineableTags() throws IOException {
        Path lootTablesDir = DATA_DIR.resolve("loot_table").resolve("blocks");
        for (String id : FIBERGLASS_BLOCK_IDS) {
            Path lootTable = lootTablesDir.resolve(id + ".json");
            assertTrue(Files.exists(lootTable), "Missing loot table: " + lootTable);
        }

        Path pickaxeTag = MINECRAFT_DATA_DIR.resolve("tags").resolve("block").resolve("mineable").resolve("pickaxe.json");
        assertTrue(Files.exists(pickaxeTag));

        try (FileReader reader = new FileReader(pickaxeTag.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            Set<String> tagValues = json.getAsJsonArray("values").asList().stream()
                    .map(JsonElement::getAsString)
                    .collect(Collectors.toSet());

            for (String id : FIBERGLASS_BLOCK_IDS) {
                assertTrue(tagValues.contains("sandstorm:" + id), "Block missing from pickaxe tag: sandstorm:" + id);
            }
        }
    }

    @Test
    void allFiberglassRecipesMustExist() {
        Path recipesDir = DATA_DIR.resolve("recipe");
        assertTrue(Files.exists(recipesDir.resolve("storm_wind_turbine.json")));
        assertTrue(Files.exists(recipesDir.resolve("fiberglass_turbine_blade.json")));
        assertTrue(Files.exists(recipesDir.resolve("fiberglass_corrugated_sheet.json")));
        assertTrue(Files.exists(recipesDir.resolve("fiberglass_corrugated_slab.json")));
        assertTrue(Files.exists(recipesDir.resolve("fiberglass_corrugated_pane.json")));
        assertTrue(Files.exists(recipesDir.resolve("fiberglass_chemical_pipe.json")));
        assertTrue(Files.exists(recipesDir.resolve("composite_recurve_bow.json")));
        assertTrue(Files.exists(recipesDir.resolve("sand_depth_probe.json")));

        List<MachineRecipe> printerRecipes = MachineRecipeRegistry.getRecipes("sandstorm:printer_3d");
        assertTrue(printerRecipes.stream().anyMatch(r -> r.getOutput().is(SandStormItems.FIBERGLASS_TURBINE_BLADE)));
        assertTrue(printerRecipes.stream().anyMatch(r -> r.getOutput().is(SandStormBlocks.FIBERGLASS_CORRUGATED_SHEET.asItem())));
    }

    @Test
    void allFiberglassKeysMustExistInAllLanguageFiles() throws IOException {
        Path langDir = ASSETS_DIR.resolve("lang");
        List<String> langFiles = List.of("en_us.json", "pt_br.json", "es_es.json");

        List<String> expectedKeys = List.of(
                "item.sandstorm.fiberglass_turbine_blade",
                "item.sandstorm.composite_recurve_bow",
                "item.sandstorm.sand_depth_probe",
                "block.sandstorm.storm_wind_turbine",
                "block.sandstorm.fiberglass_corrugated_sheet",
                "block.sandstorm.fiberglass_corrugated_slab",
                "block.sandstorm.fiberglass_corrugated_pane",
                "block.sandstorm.fiberglass_chemical_pipe",
                "tooltip.sandstorm.storm_wind_turbine.desc1",
                "tooltip.sandstorm.storm_wind_turbine.desc2",
                "tooltip.sandstorm.fiberglass_corrugated_sheet.desc1",
                "tooltip.sandstorm.fiberglass_corrugated_sheet.desc2",
                "tooltip.sandstorm.fiberglass_corrugated_slab.desc1",
                "tooltip.sandstorm.fiberglass_corrugated_slab.desc2",
                "tooltip.sandstorm.fiberglass_corrugated_pane.desc1",
                "tooltip.sandstorm.fiberglass_corrugated_pane.desc2",
                "tooltip.sandstorm.fiberglass_chemical_pipe.desc1",
                "tooltip.sandstorm.fiberglass_chemical_pipe.desc2",
                "tooltip.sandstorm.composite_recurve_bow.desc1",
                "tooltip.sandstorm.composite_recurve_bow.desc2",
                "tooltip.sandstorm.sand_depth_probe.desc1",
                "tooltip.sandstorm.sand_depth_probe.desc2",
                "message.sandstorm.sand_depth_probe.dense",
                "message.sandstorm.sand_depth_probe.cavity",
                "message.sandstorm.sand_depth_probe.loose",
                "message.sandstorm.sand_depth_probe.solid"
        );

        for (String langFile : langFiles) {
            Path path = langDir.resolve(langFile);
            try (FileReader reader = new FileReader(path.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (String key : expectedKeys) {
                    assertTrue(json.has(key), langFile + " is missing key: " + key);
                    String val = json.get(key).getAsString();
                    assertFalse(val.isBlank(), langFile + " has blank key: " + key);
                    assertTrue(val.length() <= 50, langFile + " key too long (>50): " + key + " = " + val);
                    assertFalse(val.contains("\n") || val.contains("\r"), langFile + " contains raw newline: " + key);
                }
            }
        }
    }
}
