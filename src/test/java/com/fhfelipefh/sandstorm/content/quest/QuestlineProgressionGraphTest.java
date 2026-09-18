package com.fhfelipefh.sandstorm.content.quest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestlineProgressionGraphTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testQuestRegistryNotEmptyAndContainsFiveChapters() {
        Map<String, QuestData> allQuests = QuestRegistry.getAllQuests();
        assertFalse(allQuests.isEmpty());

        for (int ch = 1; ch <= 5; ch++) {
            List<QuestData> chapterQuests = QuestRegistry.getQuestsForChapter(ch);
            assertFalse(chapterQuests.isEmpty(), "Chapter " + ch + " must have at least one quest");
        }
    }

    @Test
    void testQuestlineIsDirectedAcyclicGraph() {
        Map<String, QuestData> allQuests = QuestRegistry.getAllQuests();
        Map<String, Integer> visited = new HashMap<>();

        for (String id : allQuests.keySet()) {
            visited.put(id, 0);
        }

        for (String id : allQuests.keySet()) {
            if (visited.get(id) == 0) {
                assertFalse(hasCycle(id, allQuests, visited), "Cycle detected involving quest: " + id);
            }
        }
    }

    private boolean hasCycle(String current, Map<String, QuestData> quests, Map<String, Integer> visited) {
        visited.put(current, 1);
        QuestData data = quests.get(current);
        if (data != null) {
            for (String preId : data.prerequisiteIds()) {
                assertTrue(quests.containsKey(preId), "Missing prerequisite quest: " + preId + " required by " + current);
                int state = visited.getOrDefault(preId, 0);
                if (state == 1) {
                    return true;
                }
                if (state == 0 && hasCycle(preId, quests, visited)) {
                    return true;
                }
            }
        }
        visited.put(current, 2);
        return false;
    }

    @Test
    void testAllQuestsAreReachableFromStartingState() {
        Map<String, QuestData> allQuests = QuestRegistry.getAllQuests();
        Set<String> unlocked = new HashSet<>();

        for (QuestData q : allQuests.values()) {
            if (q.prerequisiteIds().isEmpty()) {
                unlocked.add(q.id());
            }
        }
        assertFalse(unlocked.isEmpty(), "Must have at least one root quest with no prerequisites");

        boolean progress = true;
        while (progress) {
            progress = false;
            for (QuestData q : allQuests.values()) {
                if (!unlocked.contains(q.id()) && unlocked.containsAll(q.prerequisiteIds())) {
                    unlocked.add(q.id());
                    progress = true;
                }
            }
        }

        assertEquals(allQuests.keySet(), unlocked, "Every quest in the progression tree must be reachable");
    }

    @Test
    void testAllQuestsHaveValidNonEmptyFields() {
        Map<String, QuestData> allQuests = QuestRegistry.getAllQuests();
        for (QuestData q : allQuests.values()) {
            assertNotNull(q.id());
            assertFalse(q.id().isBlank());
            assertTrue(q.chapter() >= 1 && q.chapter() <= 5);
            assertNotNull(q.titleKey());
            assertNotNull(q.taskKey());
            assertNotNull(q.noteKey());
            assertNotNull(q.iconId(), "Icon ID must not be null for " + q.id());
            assertNotNull(q.requiredItemId(), "Required item ID must not be null for " + q.id());
            assertNotNull(q.rewardItemId(), "Reward item ID must not be null for " + q.id());
            assertTrue(q.rewardCount() > 0, "Reward count must be > 0 for " + q.id());
        }
    }

    @Test
    void testContingencyRecipesExistAndAreValid() throws IOException {
        Path recipeDir = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");

        List<String> requiredRecipes = List.of(
                "crafting_table_from_sandstone.json",
                "stick_from_sandstone.json",
                "stick_from_scrap_metal.json",
                "survival_datapad.json"
        );

        for (String recipeName : requiredRecipes) {
            Path file = recipeDir.resolve(recipeName);
            assertTrue(Files.exists(file), "Missing essential contingency recipe: " + recipeName);

            try (FileReader reader = new FileReader(file.toFile())) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                assertTrue(obj.has("type"), "Recipe must have 'type': " + recipeName);
                assertTrue(obj.has("result"), "Recipe must have 'result': " + recipeName);
            }
        }
    }
}
