package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SandstormSavedData extends SavedData {

    public static final long DEFAULT_INITIAL_DELAY = 24000L;

    public static final Codec<SandstormSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.optionalFieldOf("next_storm_game_time", DEFAULT_INITIAL_DELAY).forGetter(SandstormSavedData::getNextSandstormGameTime),
            Codec.BOOL.optionalFieldOf("first_storm_triggered", false).forGetter(SandstormSavedData::isFirstStormTriggered)
    ).apply(instance, SandstormSavedData::new));

    public static final SavedDataType<SandstormSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("sandstorm_weather"),
            SandstormSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private long nextSandstormGameTime;
    private boolean firstStormTriggered;

    public SandstormSavedData() {
        this(DEFAULT_INITIAL_DELAY, false);
    }

    public SandstormSavedData(long nextSandstormGameTime, boolean firstStormTriggered) {
        this.nextSandstormGameTime = Math.max(DEFAULT_INITIAL_DELAY, nextSandstormGameTime);
        this.firstStormTriggered = firstStormTriggered;
    }

    public long getNextSandstormGameTime() {
        return nextSandstormGameTime;
    }

    public void setNextSandstormGameTime(long nextSandstormGameTime) {
        this.nextSandstormGameTime = nextSandstormGameTime;
        setDirty();
    }

    public boolean isFirstStormTriggered() {
        return firstStormTriggered;
    }

    public void setFirstStormTriggered(boolean firstStormTriggered) {
        this.firstStormTriggered = firstStormTriggered;
        setDirty();
    }
}
