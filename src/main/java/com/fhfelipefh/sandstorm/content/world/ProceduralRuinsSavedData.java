package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProceduralRuinsSavedData extends SavedData {

    public static final Codec<ProceduralRuinsSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.listOf().optionalFieldOf("processed_chunks", List.of()).forGetter(ProceduralRuinsSavedData::getProcessedChunksList)
    ).apply(instance, ProceduralRuinsSavedData::fromList));

    public static final SavedDataType<ProceduralRuinsSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("procedural_ruins"),
            ProceduralRuinsSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private final Set<Long> processedChunks = new HashSet<>();

    public ProceduralRuinsSavedData() {
    }

    public static ProceduralRuinsSavedData fromList(List<Long> list) {
        ProceduralRuinsSavedData data = new ProceduralRuinsSavedData();
        data.processedChunks.addAll(list);
        return data;
    }

    public List<Long> getProcessedChunksList() {
        return new ArrayList<>(processedChunks);
    }

    public boolean isChunkProcessed(long chunkKey) {
        return processedChunks.contains(chunkKey);
    }

    public void markChunkProcessed(long chunkKey) {
        if (processedChunks.add(chunkKey)) {
            setDirty();
        }
    }

    public static ProceduralRuinsSavedData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        if (overworld == null) {
            return new ProceduralRuinsSavedData();
        }
        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }
}
