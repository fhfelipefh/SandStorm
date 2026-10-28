package com.fhfelipefh.sandstorm.content.tracker;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TransponderTrackerSavedData extends SavedData {

    public record TransponderRecord(UUID id, String name, String dimension, int x, int y, int z, boolean isBlock) {
        public static final Codec<TransponderRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("id").forGetter(TransponderRecord::id),
                Codec.STRING.fieldOf("name").forGetter(TransponderRecord::name),
                Codec.STRING.fieldOf("dimension").forGetter(TransponderRecord::dimension),
                Codec.INT.fieldOf("x").forGetter(TransponderRecord::x),
                Codec.INT.fieldOf("y").forGetter(TransponderRecord::y),
                Codec.INT.fieldOf("z").forGetter(TransponderRecord::z),
                Codec.BOOL.fieldOf("is_block").forGetter(TransponderRecord::isBlock)
        ).apply(instance, TransponderRecord::new));
    }

    public static final Codec<TransponderTrackerSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TransponderRecord.CODEC.listOf().optionalFieldOf("records", List.of()).forGetter(TransponderTrackerSavedData::getRecords)
    ).apply(instance, TransponderTrackerSavedData::new));

    public static final SavedDataType<TransponderTrackerSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("transponder_tracker"),
            TransponderTrackerSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private final Map<UUID, TransponderRecord> records = new HashMap<>();

    public TransponderTrackerSavedData() {
    }

    public TransponderTrackerSavedData(List<TransponderRecord> recordList) {
        for (TransponderRecord rec : recordList) {
            this.records.put(rec.id(), rec);
        }
    }

    public static TransponderTrackerSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public List<TransponderRecord> getRecords() {
        return new ArrayList<>(this.records.values());
    }

    public List<TransponderRecord> getRecordsForDimension(String dimension) {
        List<TransponderRecord> result = new ArrayList<>();
        for (TransponderRecord rec : this.records.values()) {
            if (rec.dimension().equals(dimension)) {
                result.add(rec);
            }
        }
        return result;
    }

    public TransponderRecord getRecord(UUID id) {
        return this.records.get(id);
    }

    public void register(UUID id, String name, String dimension, BlockPos pos, boolean isBlock) {
        this.records.put(id, new TransponderRecord(id, name, dimension, pos.getX(), pos.getY(), pos.getZ(), isBlock));
        setDirty();
    }

    public void unregister(UUID id) {
        if (this.records.remove(id) != null) {
            setDirty();
        }
    }

    public void updatePosition(UUID id, BlockPos pos) {
        TransponderRecord old = this.records.get(id);
        if (old != null && (old.x() != pos.getX() || old.y() != pos.getY() || old.z() != pos.getZ())) {
            this.records.put(id, new TransponderRecord(old.id(), old.name(), old.dimension(), pos.getX(), pos.getY(), pos.getZ(), old.isBlock()));
            setDirty();
        }
    }
}

