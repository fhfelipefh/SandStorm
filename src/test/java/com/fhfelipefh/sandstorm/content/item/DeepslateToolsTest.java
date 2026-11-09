package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.survival.TechnologyToolRestrictionHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.BlockTags;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeepslateToolsTest {

    private static final Path DATA_DIR = Path.of("src", "main", "resources", "data");
    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");

    @BeforeAll
    static void setup() throws Exception {
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
        assertNotNull(SandStormItems.DEEPSLATE_PICKAXE);
        assertNotNull(SandStormItems.DEEPSLATE_MULTITOOL);

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
    void shouldHaveCorrectToolMaterialProperties() {
        assertNotNull(SandStormItems.DEEPSLATE_TOOL_MATERIAL);
        assertEquals(1650, SandStormItems.DEEPSLATE_TOOL_MATERIAL.durability());
        assertEquals(9.0f, SandStormItems.DEEPSLATE_TOOL_MATERIAL.speed());
        assertEquals(3.0f, SandStormItems.DEEPSLATE_TOOL_MATERIAL.attackDamageBonus());
        assertEquals(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, SandStormItems.DEEPSLATE_TOOL_MATERIAL.incorrectBlocksForDrops());
    }

    @Test
    void shouldAuthorizeDeepslateMiningTools() {
        assertTrue(TechnologyToolRestrictionHandler.isAuthorizedMiningTool("sandstorm", "deepslate_pickaxe"));
        assertTrue(TechnologyToolRestrictionHandler.isAuthorizedMiningTool("sandstorm", "deepslate_paxel"));
        assertTrue(TechnologyToolRestrictionHandler.isAuthorizedMiningTool("sandstorm", "deepslate_multitool"));
        assertTrue(TechnologyToolRestrictionHandler.isAuthorizedMiningTool("sandstorm", "silicon_pickaxe"));
        assertFalse(TechnologyToolRestrictionHandler.isAuthorizedMiningTool("sandstorm", "silicon_shovel"));
        assertFalse(TechnologyToolRestrictionHandler.isAuthorizedMiningTool("minecraft", "diamond_pickaxe"));
        assertFalse(TechnologyToolRestrictionHandler.isAuthorizedMiningTool("minecraft", "iron_pickaxe"));
        assertFalse(TechnologyToolRestrictionHandler.isAuthorizedMiningTool(null, "deepslate_pickaxe"));
    }

    @Test
    void shouldHaveCompleteAndValidRecipesForDeepslateTools() throws IOException {
        Path plateRecipePath = DATA_DIR.resolve("sandstorm").resolve("recipe").resolve("deepslate_composite_plate.json");
        Path pickaxeRecipePath = DATA_DIR.resolve("sandstorm").resolve("recipe").resolve("deepslate_pickaxe.json");
        Path paxelRecipePath = DATA_DIR.resolve("sandstorm").resolve("recipe").resolve("deepslate_paxel.json");
        Path multiToolRecipePath = DATA_DIR.resolve("sandstorm").resolve("recipe").resolve("deepslate_multitool.json");

        assertTrue(Files.exists(plateRecipePath));
        assertTrue(Files.exists(pickaxeRecipePath));
        assertTrue(Files.exists(paxelRecipePath));
        assertTrue(Files.exists(multiToolRecipePath));

        try (FileReader reader = new FileReader(plateRecipePath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals("minecraft:crafting_shaped", json.get("type").getAsString());
            assertEquals("sandstorm:deepslate_composite_plate", json.getAsJsonObject("result").get("id").getAsString());
            assertEquals(2, json.getAsJsonObject("result").get("count").getAsInt());
        }

        try (FileReader reader = new FileReader(pickaxeRecipePath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals("minecraft:crafting_shaped", json.get("type").getAsString());
            assertEquals("sandstorm:deepslate_pickaxe", json.getAsJsonObject("result").get("id").getAsString());
            assertEquals(1, json.getAsJsonObject("result").get("count").getAsInt());
        }

        try (FileReader reader = new FileReader(paxelRecipePath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals("minecraft:crafting_shapeless", json.get("type").getAsString());
            assertEquals("sandstorm:deepslate_paxel", json.getAsJsonObject("result").get("id").getAsString());
            assertEquals(1, json.getAsJsonObject("result").get("count").getAsInt());
        }

        try (FileReader reader = new FileReader(multiToolRecipePath.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals("minecraft:crafting_shapeless", json.get("type").getAsString());
            assertEquals("sandstorm:deepslate_multitool", json.getAsJsonObject("result").get("id").getAsString());
            assertEquals(1, json.getAsJsonObject("result").get("count").getAsInt());
        }
    }

    @Test
    void shouldHaveItemTagsCorrectlyConfigured() throws IOException {
        Path pickaxesTag = DATA_DIR.resolve("minecraft").resolve("tags").resolve("item").resolve("pickaxes.json");
        Path shovelsTag = DATA_DIR.resolve("minecraft").resolve("tags").resolve("item").resolve("shovels.json");
        Path axesTag = DATA_DIR.resolve("minecraft").resolve("tags").resolve("item").resolve("axes.json");
        Path repairTag = DATA_DIR.resolve("sandstorm").resolve("tags").resolve("item").resolve("deepslate_tool_materials.json");

        assertTrue(Files.exists(pickaxesTag));
        assertTrue(Files.exists(shovelsTag));
        assertTrue(Files.exists(axesTag));
        assertTrue(Files.exists(repairTag));

        String pickaxeContent = Files.readString(pickaxesTag);
        assertTrue(pickaxeContent.contains("sandstorm:deepslate_pickaxe"));
        assertTrue(pickaxeContent.contains("sandstorm:deepslate_paxel"));
        assertTrue(pickaxeContent.contains("sandstorm:deepslate_multitool"));

        String shovelContent = Files.readString(shovelsTag);
        assertTrue(shovelContent.contains("sandstorm:deepslate_paxel"));
        assertTrue(shovelContent.contains("sandstorm:deepslate_multitool"));

        String axesContent = Files.readString(axesTag);
        assertTrue(axesContent.contains("sandstorm:deepslate_multitool"));

        String repairContent = Files.readString(repairTag);
        assertTrue(repairContent.contains("sandstorm:deepslate_composite_plate"));
    }

    @Test
    void shouldHaveMatchingItemAssetsAndTextures() throws IOException {
        String[] itemNames = {
                "deepslate_composite_plate",
                "deepslate_pickaxe",
                "deepslate_paxel",
                "deepslate_multitool"
        };

        for (String name : itemNames) {
            Path itemDef = ASSETS_DIR.resolve("items").resolve(name + ".json");
            Path itemModel = ASSETS_DIR.resolve("models").resolve("item").resolve(name + ".json");
            Path texture = ASSETS_DIR.resolve("textures").resolve("item").resolve(name + ".png");

            assertTrue(Files.exists(itemDef));
            assertTrue(Files.exists(itemModel));
            assertTrue(Files.exists(texture));
            assertTrue(Files.size(texture) > 100);

            try (FileReader reader = new FileReader(itemDef.toFile())) {
                JsonElement parsed = JsonParser.parseReader(reader);
                assertTrue(parsed.isJsonObject());
                JsonObject obj = parsed.getAsJsonObject();
                assertEquals("sandstorm:item/" + name, obj.getAsJsonObject("model").get("model").getAsString());
            }
        }
    }
}
