package com.fhfelipefh.sandstorm.content.clone;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
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

public class CloneNetworkSavedData extends SavedData {

    public record ClonePodRecord(BlockPos pos, String dimensionId, String name, boolean hasClone, long lastSeen) {
        public static final Codec<ClonePodRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(ClonePodRecord::pos),
                Codec.STRING.fieldOf("dimension").forGetter(ClonePodRecord::dimensionId),
                Codec.STRING.fieldOf("name").forGetter(ClonePodRecord::name),
                Codec.BOOL.fieldOf("hasClone").forGetter(ClonePodRecord::hasClone),
                Codec.LONG.fieldOf("lastSeen").forGetter(ClonePodRecord::lastSeen)
        ).apply(instance, ClonePodRecord::new));
    }

    public record PlayerEntry(UUID uuid, List<ClonePodRecord> pods) {
        public static final Codec<PlayerEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("uuid").forGetter(PlayerEntry::uuid),
                ClonePodRecord.CODEC.listOf().optionalFieldOf("pods", List.of()).forGetter(PlayerEntry::pods)
        ).apply(instance, PlayerEntry::new));
    }

    public static final Codec<CloneNetworkSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerEntry.CODEC.listOf().optionalFieldOf("players", List.of()).forGetter(CloneNetworkSavedData::getEntries)
    ).apply(instance, CloneNetworkSavedData::fromEntries));

    public static final SavedDataType<CloneNetworkSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("clone_network"),
            CloneNetworkSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private final Map<UUID, List<ClonePodRecord>> playerPods = new HashMap<>();

    public CloneNetworkSavedData() {
    }

    public static CloneNetworkSavedData fromEntries(List<PlayerEntry> entries) {
        CloneNetworkSavedData data = new CloneNetworkSavedData();
        for (PlayerEntry entry : entries) {
            data.playerPods.put(entry.uuid(), new ArrayList<>(entry.pods()));
        }
        return data;
    }

    public List<PlayerEntry> getEntries() {
        List<PlayerEntry> entries = new ArrayList<>();
        for (Map.Entry<UUID, List<ClonePodRecord>> entry : this.playerPods.entrySet()) {
            entries.add(new PlayerEntry(entry.getKey(), new ArrayList<>(entry.getValue())));
        }
        return entries;
    }

    public static CloneNetworkSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public void registerPod(UUID owner, BlockPos pos, String dimensionId, String name, boolean hasClone, long gameTime) {
        if (owner == null || pos == null || dimensionId == null) {
            return;
        }
        List<ClonePodRecord> records = this.playerPods.computeIfAbsent(owner, k -> new ArrayList<>());
        records.removeIf(r -> r.pos().equals(pos) && r.dimensionId().equals(dimensionId));
        records.add(new ClonePodRecord(pos, dimensionId, name == null || name.isBlank() ? "Sleeper Pod" : name, hasClone, gameTime));
        this.setDirty();
    }

    public void unregisterPod(BlockPos pos, String dimensionId) {
        if (pos == null || dimensionId == null) {
            return;
        }
        boolean modified = false;
        for (List<ClonePodRecord> records : this.playerPods.values()) {
            if (records.removeIf(r -> r.pos().equals(pos) && r.dimensionId().equals(dimensionId))) {
                modified = true;
            }
        }
        if (modified) {
            this.setDirty();
        }
    }

    public void updateCloneStatus(UUID owner, BlockPos pos, String dimensionId, boolean hasClone, long gameTime) {
        if (owner == null || pos == null || dimensionId == null) {
            return;
        }
        List<ClonePodRecord> records = this.playerPods.get(owner);
        if (records == null) {
            return;
        }
        for (int i = 0; i < records.size(); i++) {
            ClonePodRecord record = records.get(i);
            if (record.pos().equals(pos) && record.dimensionId().equals(dimensionId)) {
                records.set(i, new ClonePodRecord(pos, dimensionId, record.name(), hasClone, gameTime));
                this.setDirty();
                return;
            }
        }
    }

    public List<ClonePodRecord> getPodsForPlayer(UUID owner) {
        if (owner == null) {
            return Collections.emptyList();
        }
        List<ClonePodRecord> records = this.playerPods.get(owner);
        return records == null ? Collections.emptyList() : Collections.unmodifiableList(records);
    }

    public Optional<ClonePodRecord> findNearestReadyPod(UUID owner, BlockPos referencePos, String dimensionId) {
        if (owner == null || referencePos == null || dimensionId == null) {
            return Optional.empty();
        }
        List<ClonePodRecord> records = this.playerPods.get(owner);
        if (records == null || records.isEmpty()) {
            return Optional.empty();
        }
        ClonePodRecord best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (ClonePodRecord record : records) {
            if (record.hasClone() && record.dimensionId().equals(dimensionId)) {
                double distSq = referencePos.distSqr(record.pos());
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                    best = record;
                }
            }
        }
        return Optional.ofNullable(best);
    }

    public Optional<ClonePodRecord> findTargetPodForTransfer(UUID owner, BlockPos currentPos, String dimensionId) {
        if (owner == null || currentPos == null || dimensionId == null) {
            return Optional.empty();
        }
        List<ClonePodRecord> records = this.playerPods.get(owner);
        if (records == null || records.isEmpty()) {
            return Optional.empty();
        }
        for (ClonePodRecord record : records) {
            if (record.hasClone() && record.dimensionId().equals(dimensionId) && !record.pos().equals(currentPos)) {
                return Optional.of(record);
            }
        }
        return Optional.empty();
    }
}
