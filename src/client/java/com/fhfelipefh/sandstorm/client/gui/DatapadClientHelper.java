package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.item.SurvivalDatapadItem;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatapadClientHelper {
    private static final Set<String> CLAIMED_QUESTS = new HashSet<>();
    private static final Set<String> COMPLETED_CONDITIONS = new HashSet<>();

    public static void initialize() {
        SurvivalDatapadItem.setClientScreenOpener(() -> {
            Minecraft client = Minecraft.getInstance();
            if (client != null) {
                client.setScreenAndShow(new SurvivalDatapadScreen());
            }
        });
    }

    public static void setQuests(List<String> questIds, List<String> conditionTags) {
        CLAIMED_QUESTS.clear();
        CLAIMED_QUESTS.addAll(questIds);
        COMPLETED_CONDITIONS.clear();
        COMPLETED_CONDITIONS.addAll(conditionTags);
    }

    public static void setClaimedQuests(List<String> questIds) {
        setQuests(questIds, List.of());
    }

    public static void setCompletedConditions(List<String> conditionTags) {
        COMPLETED_CONDITIONS.clear();
        COMPLETED_CONDITIONS.addAll(conditionTags);
    }

    public static boolean isQuestClaimed(String questId) {
        return CLAIMED_QUESTS.contains(questId);
    }

    public static boolean isConditionMet(String conditionTag) {
        return COMPLETED_CONDITIONS.contains(conditionTag);
    }

    public static Set<String> getClaimedQuests() {
        return Collections.unmodifiableSet(CLAIMED_QUESTS);
    }

    public static Set<String> getCompletedConditions() {
        return Collections.unmodifiableSet(COMPLETED_CONDITIONS);
    }
}
