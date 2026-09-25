package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.client.gui.SurvivalDatapadScreen;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestProgressionArchitectureTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void dagStructureAndAcyclicity() {
        Map<String, QuestData> allQuests = QuestRegistry.getAllQuests();
        assertFalse(allQuests.isEmpty(), "QuestRegistry must not be empty");

        Map<String, Integer> visited = new HashMap<>();
        for (String id : allQuests.keySet()) {
            visited.put(id, 0);
        }

        for (String id : allQuests.keySet()) {
            if (visited.get(id) == 0) {
                assertFalse(checkCycle(id, allQuests, visited), "Cycle detected in questline graph involving quest: " + id);
            }
        }
    }

    private boolean checkCycle(String current, Map<String, QuestData> quests, Map<String, Integer> visited) {
        visited.put(current, 1);
        QuestData data = quests.get(current);
        if (data != null) {
            for (String preId : data.prerequisiteIds()) {
                assertTrue(quests.containsKey(preId), "Missing prerequisite quest '" + preId + "' required by '" + current + "'");
                int state = visited.getOrDefault(preId, 0);
                if (state == 1) {
                    return true;
                }
                if (state == 0 && checkCycle(preId, quests, visited)) {
                    return true;
                }
            }
        }
        visited.put(current, 2);
        return false;
    }

    @Test
    void allQuestsRequiredAndRewardItemsAreValid() {
        Map<String, QuestData> allQuests = QuestRegistry.getAllQuests();
        for (QuestData q : allQuests.values()) {
            assertNotNull(q.id(), "Quest id must not be null");
            assertFalse(q.id().isBlank(), "Quest id must not be blank");
            assertNotNull(q.titleKey(), "Title key must not be null for " + q.id());
            assertNotNull(q.taskKey(), "Task key must not be null for " + q.id());
            assertNotNull(q.noteKey(), "Note key must not be null for " + q.id());

            assertNotNull(q.iconId(), "Icon ID must not be null for " + q.id());
            assertValidItemIdentifier(q.iconId());

            assertNotNull(q.requiredItemId(), "Required item ID must not be null for " + q.id());
            assertValidItemIdentifier(q.requiredItemId());

            assertNotNull(q.rewardItemId(), "Reward item ID must not be null for " + q.id());
            assertValidItemIdentifier(q.rewardItemId());
            assertTrue(q.rewardCount() > 0, "Reward count must be > 0 for " + q.id());
        }
    }

    private void assertValidItemIdentifier(Identifier id) {
        if ("minecraft".equals(id.getNamespace())) {
            assertTrue(BuiltInRegistries.ITEM.containsKey(id), "Vanilla item not found: " + id);
        } else if ("sandstorm".equals(id.getNamespace())) {
            Path itemAsset = Path.of("src", "main", "resources", "assets", "sandstorm", "items", id.getPath() + ".json");
            assertTrue(Files.exists(itemAsset), "SandStorm item asset definition not found for: " + id);
        }
    }

    @Test
    void serverAndClientItemMatchingParity() {
        QuestData compact = QuestRegistry.getQuest("compact_sandstone");
        assertNotNull(compact);
        List<ItemStack> sandstoneVariants = List.of(
                new ItemStack(Items.SANDSTONE),
                new ItemStack(Items.RED_SANDSTONE),
                new ItemStack(Items.SMOOTH_SANDSTONE),
                new ItemStack(Items.CUT_SANDSTONE),
                new ItemStack(Items.CHISELED_SANDSTONE)
        );
        for (ItemStack stack : sandstoneVariants) {
            boolean server = QuestRewardHandler.matchesQuestItem(compact, stack, null);
            boolean client = SurvivalDatapadScreen.matchesQuestItem(compact, stack, null);
            assertEquals(server, client, "Parity failure for compact_sandstone");
            assertTrue(server, "compact_sandstone should match " + stack);
        }

        QuestData workbench = QuestRegistry.getQuest("emergency_workbench");
        assertNotNull(workbench);
        ItemStack craftingTable = new ItemStack(Items.CRAFTING_TABLE);
        boolean serverWb = QuestRewardHandler.matchesQuestItem(workbench, craftingTable, null);
        boolean clientWb = SurvivalDatapadScreen.matchesQuestItem(workbench, craftingTable, null);
        assertEquals(serverWb, clientWb, "Parity failure for emergency_workbench");
        assertTrue(serverWb, "emergency_workbench should match crafting table");

        QuestData furnace = QuestRegistry.getQuest("sandstone_furnace");
        assertNotNull(furnace);
        ItemStack vanillaFurnace = new ItemStack(Items.FURNACE);
        boolean serverFn = QuestRewardHandler.matchesQuestItem(furnace, vanillaFurnace, null);
        boolean clientFn = SurvivalDatapadScreen.matchesQuestItem(furnace, vanillaFurnace, null);
        assertEquals(serverFn, clientFn, "Parity failure for sandstone_furnace");
        assertTrue(serverFn, "sandstone_furnace should match furnace");

        QuestData canister = QuestRegistry.getQuest("glass_canister");
        assertNotNull(canister);
        List<ItemStack> bottleVariants = List.of(
                new ItemStack(Items.GLASS_BOTTLE),
                new ItemStack(Items.POTION)
        );
        for (ItemStack stack : bottleVariants) {
            boolean server = QuestRewardHandler.matchesQuestItem(canister, stack, null);
            boolean client = SurvivalDatapadScreen.matchesQuestItem(canister, stack, null);
            assertEquals(server, client, "Parity failure for glass_canister");
            assertTrue(server, "glass_canister should match " + stack);
        }

        QuestData genesis = QuestRegistry.getQuest("planetary_genesis");
        assertNotNull(genesis);
        List<ItemStack> genesisVariants = List.of(
                new ItemStack(Items.GRASS_BLOCK),
                new ItemStack(Items.DIRT)
        );
        for (ItemStack stack : genesisVariants) {
            boolean server = QuestRewardHandler.matchesQuestItem(genesis, stack, null);
            boolean client = SurvivalDatapadScreen.matchesQuestItem(genesis, stack, null);
            assertEquals(server, client, "Parity failure for planetary_genesis");
            assertTrue(server, "planetary_genesis should match " + stack);
        }
    }

    @Test
    void everyQuestHasObtainablePath() throws IOException {
        Set<String> obtainable = new HashSet<>();

        obtainable.addAll(List.of(
                "minecraft:sand",
                "minecraft:red_sand",
                "minecraft:sandstone",
                "minecraft:red_sandstone",
                "minecraft:smooth_sandstone",
                "minecraft:cut_sandstone",
                "minecraft:chiseled_sandstone",
                "minecraft:coal",
                "minecraft:torch",
                "minecraft:glass_bottle",
                "minecraft:potion",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:crafting_table",
                "minecraft:furnace",
                "minecraft:iron_ingot",
                "minecraft:copper_ingot",
                "minecraft:gold_ingot",
                "minecraft:piston",
                "sandstorm:space_suit_helmet",
                "sandstorm:space_suit_chestplate",
                "sandstorm:space_suit_leggings",
                "sandstorm:space_suit_boots",
                "sandstorm:space_ration",
                "sandstorm:sandworm_chitin",
                "sandstorm:sandworm_tooth",
                "sandstorm:ancient_seed",
                "sandstorm:brackish_water_bottle",
                "sandstorm:potable_water_bottle",
                "sandstorm:heavy_sap_bottle",
                "sandstorm:xeno_grass_seeds",
                "sandstorm:xeno_grass_block"
        ));

        Path recipeDir = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");
        if (Files.exists(recipeDir)) {
            try (Stream<Path> stream = Files.walk(recipeDir)) {
                stream.filter(p -> p.toString().endsWith(".json")).forEach(p -> {
                    try (FileReader reader = new FileReader(p.toFile())) {
                        JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                        if (obj.has("result")) {
                            JsonElement res = obj.get("result");
                            if (res.isJsonObject()) {
                                JsonObject resObj = res.getAsJsonObject();
                                if (resObj.has("id")) {
                                    obtainable.add(resObj.get("id").getAsString());
                                } else if (resObj.has("item")) {
                                    obtainable.add(resObj.get("item").getAsString());
                                }
                            } else if (res.isJsonPrimitive()) {
                                obtainable.add(res.getAsString());
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        Path lootDir = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "blocks");
        if (Files.exists(lootDir)) {
            try (Stream<Path> stream = Files.walk(lootDir)) {
                stream.filter(p -> p.toString().endsWith(".json")).forEach(p -> {
                    try (FileReader reader = new FileReader(p.toFile())) {
                        JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                        extractLootEntries(obj, obtainable);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        List<String> unobtainable = new ArrayList<>();
        Map<String, QuestData> allQuests = QuestRegistry.getAllQuests();
        for (QuestData q : allQuests.values()) {
            if (q.isConditionBased()) {
                assertTrue(q.conditionTag() != null && !q.conditionTag().isBlank(), "Condition tag cannot be empty for " + q.id());
                continue;
            }

            Identifier reqId = q.requiredItemId();
            String reqStr = reqId.toString();

            boolean hasPath = obtainable.contains(reqStr);
            if (!hasPath) {
                if ("compact_sandstone".equals(q.id()) && (obtainable.contains("minecraft:sandstone") || obtainable.contains("minecraft:red_sandstone"))) {
                    hasPath = true;
                } else if ("emergency_workbench".equals(q.id()) && obtainable.contains("minecraft:crafting_table")) {
                    hasPath = true;
                } else if ("sandstone_furnace".equals(q.id()) && obtainable.contains("minecraft:furnace")) {
                    hasPath = true;
                } else if ("glass_canister".equals(q.id()) && obtainable.contains("minecraft:glass_bottle")) {
                    hasPath = true;
                } else if ("planetary_genesis".equals(q.id()) && (obtainable.contains("minecraft:dirt") || obtainable.contains("sandstorm:xeno_grass_seeds"))) {
                    hasPath = true;
                }
            }

            if (!hasPath) {
                unobtainable.add("Quest '" + q.id() + "' requires '" + reqStr + "' but has no obtainable recipe, loot drop, or alternative.");
            }
        }

        assertTrue(unobtainable.isEmpty(), "Found quests with unobtainable requirements:\n" + String.join("\n", unobtainable));
    }

    private void extractLootEntries(JsonObject obj, Set<String> target) {
        if (!obj.has("pools")) {
            return;
        }
        JsonArray pools = obj.getAsJsonArray("pools");
        for (JsonElement poolEl : pools) {
            if (!poolEl.isJsonObject()) {
                continue;
            }
            JsonObject pool = poolEl.getAsJsonObject();
            if (pool.has("entries")) {
                scanEntries(pool.getAsJsonArray("entries"), target);
            }
        }
    }

    private void scanEntries(JsonArray entries, Set<String> target) {
        for (JsonElement el : entries) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject entry = el.getAsJsonObject();
            if (entry.has("name")) {
                target.add(entry.get("name").getAsString());
            }
            if (entry.has("children")) {
                scanEntries(entry.getAsJsonArray("children"), target);
            }
        }
    }

    @Test
    void finalVictoryQuestReachableFromRoots() {
        Map<String, QuestData> allQuests = QuestRegistry.getAllQuests();
        Set<String> claimed = new HashSet<>();

        boolean progress = true;
        while (progress) {
            progress = false;
            for (QuestData quest : allQuests.values()) {
                if (claimed.contains(quest.id())) {
                    continue;
                }
                boolean prereqsClaimed = true;
                for (String preId : quest.prerequisiteIds()) {
                    if (!claimed.contains(preId)) {
                        prereqsClaimed = false;
                        break;
                    }
                }
                if (prereqsClaimed) {
                    claimed.add(quest.id());
                    progress = true;
                }
            }
        }

        assertTrue(claimed.contains("planetary_genesis"), "The final endgame quest 'planetary_genesis' must be reachable");
        assertEquals(allQuests.size(), claimed.size(), "All quests must be reachable from starting root quests without softlocks");
    }
}
