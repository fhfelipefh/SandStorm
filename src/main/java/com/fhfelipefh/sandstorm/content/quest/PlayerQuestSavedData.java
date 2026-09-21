package com.fhfelipefh.sandstorm.content.quest;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerQuestSavedData extends SavedData {

    public record Entry(UUID uuid, List<String> claimed) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("uuid").forGetter(Entry::uuid),
                Codec.STRING.listOf().fieldOf("claimed").forGetter(Entry::claimed)
        ).apply(instance, Entry::new));
    }

    public static final Codec<PlayerQuestSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().optionalFieldOf("players", List.of()).forGetter(PlayerQuestSavedData::getEntries)
    ).apply(instance, PlayerQuestSavedData::fromEntries));

    public static final SavedDataType<PlayerQuestSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("player_quests"),
            PlayerQuestSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private final Map<UUID, Set<String>> claimedMap = new HashMap<>();

    public PlayerQuestSavedData() {
    }

    public static PlayerQuestSavedData fromEntries(List<Entry> entries) {
        PlayerQuestSavedData data = new PlayerQuestSavedData();
        for (Entry entry : entries) {
            data.claimedMap.put(entry.uuid(), new HashSet<>(entry.claimed()));
        }
        return data;
    }

    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>();
        for (Map.Entry<UUID, Set<String>> e : claimedMap.entrySet()) {
            entries.add(new Entry(e.getKey(), new ArrayList<>(e.getValue())));
        }
        return entries;
    }

    public boolean isClaimed(UUID playerUuid, String questId) {
        Set<String> set = claimedMap.get(playerUuid);
        return set != null && set.contains(questId);
    }

    public boolean markClaimed(UUID playerUuid, String questId) {
        Set<String> set = claimedMap.computeIfAbsent(playerUuid, k -> new HashSet<>());
        boolean added = set.add(questId);
        if (added) {
            setDirty();
        }
        return added;
    }

    public Set<String> getClaimedQuests(UUID playerUuid) {
        Set<String> set = claimedMap.get(playerUuid);
        return set != null ? Collections.unmodifiableSet(set) : Set.of();
    }

    public static PlayerQuestSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public static PlayerQuestSavedData get(ServerLevel level) {
        return get(level.getServer());
    }
}
