package com.fhfelipefh.sandstorm.content.quest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.List;

public record QuestData(
        String id,
        int chapter,
        String titleKey,
        String taskKey,
        String noteKey,
        Identifier iconId,
        Identifier requiredItemId,
        List<String> prerequisiteIds,
        Identifier rewardItemId,
        int rewardCount
) {
    public Item getIconItem() {
        return BuiltInRegistries.ITEM.getValue(iconId);
    }

    public Item getRequiredItem() {
        return BuiltInRegistries.ITEM.getValue(requiredItemId);
    }

    public Item getRewardItem() {
        return BuiltInRegistries.ITEM.getValue(rewardItemId);
    }
}
