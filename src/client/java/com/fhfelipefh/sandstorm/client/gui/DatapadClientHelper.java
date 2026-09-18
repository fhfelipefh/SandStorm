package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.item.SurvivalDatapadItem;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatapadClientHelper {
    private static final Set<String> CLAIMED_QUESTS = new HashSet<>();

    public static void initialize() {
        SurvivalDatapadItem.setClientScreenOpener(() -> {
            Minecraft client = Minecraft.getInstance();
            if (client != null) {
                client.setScreenAndShow(new SurvivalDatapadScreen());
            }
        });
    }

    public static void setClaimedQuests(List<String> questIds) {
        CLAIMED_QUESTS.clear();
        CLAIMED_QUESTS.addAll(questIds);
    }

    public static boolean isQuestClaimed(String questId) {
        return CLAIMED_QUESTS.contains(questId);
    }

    public static Set<String> getClaimedQuests() {
        return Collections.unmodifiableSet(CLAIMED_QUESTS);
    }
}
