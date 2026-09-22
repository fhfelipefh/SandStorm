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
            Entry.CODEC.listOf().optionalFieldOf("suits", List.of()).forGetter(PlayerSuitSavedData::getEntries)
    ).apply(instance, PlayerSuitSavedData::fromEntries));

    public static final SavedDataType<PlayerSuitSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("player_suits"),
            PlayerSuitSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private final Map<UUID, Entry> suitMap = new HashMap<>();

    public PlayerSuitSavedData() {
    }

    public static PlayerSuitSavedData fromEntries(List<Entry> entries) {
        PlayerSuitSavedData data = new PlayerSuitSavedData();
        for (Entry entry : entries) {
            data.suitMap.put(entry.uuid(), entry);
        }
        return data;
    }

    public List<Entry> getEntries() {
        return new ArrayList<>(suitMap.values());
    }

    public Entry getSuitData(UUID playerUuid) {
        return suitMap.get(playerUuid);
    }

    public boolean hasUpgrade(UUID playerUuid, String upgrade) {
        Entry entry = suitMap.get(playerUuid);
        return entry != null && entry.upgrades().contains(upgrade);
    }

    public boolean addUpgrade(UUID playerUuid, String upgrade) {
        Entry existing = suitMap.get(playerUuid);
        if (existing == null) {
            suitMap.put(playerUuid, new Entry(playerUuid, 50000, 37.0, List.of(upgrade)));
            setDirty();
            return true;
        }
        if (existing.upgrades().contains(upgrade)) {
            return false;
        }
        List<String> updated = new ArrayList<>(existing.upgrades());
        updated.add(upgrade);
        suitMap.put(playerUuid, new Entry(existing.uuid(), existing.energy(), existing.temperature(), updated));
        setDirty();
        return true;
    }

    public void setSuitData(UUID playerUuid, long energy, double temperature) {
        Entry existing = suitMap.get(playerUuid);
        List<String> upgrades = existing != null ? existing.upgrades() : List.of();
        if (existing == null || existing.energy() != energy || Math.abs(existing.temperature() - temperature) >= 0.05) {
            suitMap.put(playerUuid, new Entry(playerUuid, energy, temperature, upgrades));
            setDirty();
        }
    }

    public void removeSuitData(UUID playerUuid) {
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
