package com.fhfelipefh.sandstorm.content.survival;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerSuitSavedData extends SavedData {

    public record Entry(UUID uuid, long energy, double temperature, List<String> upgrades) {
        public Entry(UUID uuid, long energy, double temperature) {
            this(uuid, energy, temperature, List.of());
        }

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("uuid").forGetter(Entry::uuid),
                Codec.LONG.fieldOf("energy").forGetter(Entry::energy),
                Codec.DOUBLE.fieldOf("temperature").forGetter(Entry::temperature),
                Codec.STRING.listOf().optionalFieldOf("upgrades", List.of()).forGetter(Entry::upgrades)
        ).apply(instance, Entry::new));
    }

    public static final Codec<PlayerSuitSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().optionalFieldOf("suits", List.of()).forGetter(PlayerSuitSavedData::getEntries),
            Entry.CODEC.optionalFieldOf("singleplayer").forGetter(PlayerSuitSavedData::getSingleplayerEntryOptional)
    ).apply(instance, PlayerSuitSavedData::fromEntriesAndSingleplayer));

    public static final SavedDataType<PlayerSuitSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("player_suits"),
            PlayerSuitSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private final Map<UUID, Entry> suitMap = new HashMap<>();
    private Entry singleplayerEntry;

    public PlayerSuitSavedData() {
    }

    public static PlayerSuitSavedData fromEntries(List<Entry> entries) {
        return fromEntriesAndSingleplayer(entries, Optional.empty());
    }

    public static PlayerSuitSavedData fromEntriesAndSingleplayer(List<Entry> entries, Optional<Entry> singleplayer) {
        PlayerSuitSavedData data = new PlayerSuitSavedData();
        for (Entry entry : entries) {
            data.suitMap.put(entry.uuid(), entry);
        }
        if (singleplayer.isPresent()) {
            data.singleplayerEntry = singleplayer.get();
        } else if (!entries.isEmpty()) {
            data.singleplayerEntry = entries.get(entries.size() - 1);
        }
        return data;
    }

    public List<Entry> getEntries() {
        return new ArrayList<>(suitMap.values());
    }

    public Optional<Entry> getSingleplayerEntryOptional() {
        return Optional.ofNullable(singleplayerEntry);
    }

    public Entry getSingleplayerSuitData() {
        return singleplayerEntry;
    }

    public void setSingleplayerSuitData(long energy, double temperature) {
        List<String> upgrades = singleplayerEntry != null ? singleplayerEntry.upgrades() : List.of();
        UUID uuid = singleplayerEntry != null ? singleplayerEntry.uuid() : UUID.nameUUIDFromBytes("singleplayer".getBytes());
        if (singleplayerEntry == null || singleplayerEntry.energy() != energy || Math.abs(singleplayerEntry.temperature() - temperature) >= 0.05) {
            singleplayerEntry = new Entry(uuid, energy, temperature, upgrades);
            setDirty();
        }
    }

    public Entry getSuitData(UUID playerUuid) {
        Entry entry = suitMap.get(playerUuid);
        if (entry == null) {
            return singleplayerEntry;
        }
        return entry;
    }

    public boolean hasUpgrade(UUID playerUuid, String upgrade) {
        Entry entry = suitMap.get(playerUuid);
        if (entry != null && entry.upgrades().contains(upgrade)) {
            return true;
        }
        if (singleplayerEntry != null && singleplayerEntry.upgrades().contains(upgrade)) {
            return true;
        }
        return false;
    }

    public boolean addUpgrade(UUID playerUuid, String upgrade) {
        Entry existing = suitMap.get(playerUuid);
        if (existing == null && singleplayerEntry != null) {
            existing = singleplayerEntry;
        }
        if (existing == null) {
            Entry newEntry = new Entry(playerUuid, 50000, 37.0, List.of(upgrade));
            suitMap.put(playerUuid, newEntry);
            singleplayerEntry = newEntry;
            setDirty();
            return true;
        }
        if (existing.upgrades().contains(upgrade)) {
            return false;
        }
        List<String> updated = new ArrayList<>(existing.upgrades());
        updated.add(upgrade);
        Entry updatedEntry = new Entry(existing.uuid(), existing.energy(), existing.temperature(), updated);
        suitMap.put(playerUuid, updatedEntry);
        singleplayerEntry = updatedEntry;
        setDirty();
        return true;
    }

    public void setSuitData(UUID playerUuid, long energy, double temperature) {
        Entry existing = suitMap.get(playerUuid);
        if (existing == null && singleplayerEntry != null) {
            existing = singleplayerEntry;
        }
        List<String> upgrades = existing != null ? existing.upgrades() : List.of();
        if (existing == null || existing.energy() != energy || Math.abs(existing.temperature() - temperature) >= 0.05) {
            Entry newEntry = new Entry(playerUuid, energy, temperature, upgrades);
            suitMap.put(playerUuid, newEntry);
            singleplayerEntry = newEntry;
            setDirty();
        }
    }

    public void removeSuitData(UUID playerUuid) {
        if (singleplayerEntry != null && singleplayerEntry.uuid().equals(playerUuid)) {
            singleplayerEntry = null;
        }
        if (suitMap.remove(playerUuid) != null) {
            setDirty();
        }
    }

    public Map<UUID, Entry> getAllSuitData() {
        return Collections.unmodifiableMap(suitMap);
    }

    public static PlayerSuitSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public static PlayerSuitSavedData get(ServerLevel level) {
        return get(level.getServer());
    }
}
