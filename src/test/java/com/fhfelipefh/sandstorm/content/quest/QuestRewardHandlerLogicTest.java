package com.fhfelipefh.sandstorm.content.quest;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestRewardHandlerLogicTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testRootQuestPrerequisitesAlwaysMet() {
        PlayerQuestSavedData data = new PlayerQuestSavedData();
        UUID player = UUID.randomUUID();
        QuestData root = QuestRegistry.getQuest("suit_diagnostics");

        assertTrue(QuestRewardHandler.arePrerequisitesMet(player, root, data));
    }

    @Test
    void testSinglePrerequisiteProgression() {
        PlayerQuestSavedData data = new PlayerQuestSavedData();
        UUID player = UUID.randomUUID();
        QuestData sandstone = QuestRegistry.getQuest("compact_sandstone");

        assertFalse(QuestRewardHandler.arePrerequisitesMet(player, sandstone, data));

        data.markClaimed(player, "suit_diagnostics");
        assertTrue(QuestRewardHandler.arePrerequisitesMet(player, sandstone, data));
    }

    @Test
    void testMultiPrerequisiteProgression() {
        PlayerQuestSavedData data = new PlayerQuestSavedData();
        UUID player = UUID.randomUUID();
        QuestData terraformer = QuestRegistry.getQuest("planet_terraformer");

        assertFalse(QuestRewardHandler.arePrerequisitesMet(player, terraformer, data));

        data.markClaimed(player, "atmospheric_probe");
        assertFalse(QuestRewardHandler.arePrerequisitesMet(player, terraformer, data));

        data.markClaimed(player, "mecha_assembly");
        assertTrue(QuestRewardHandler.arePrerequisitesMet(player, terraformer, data));
    }
}
