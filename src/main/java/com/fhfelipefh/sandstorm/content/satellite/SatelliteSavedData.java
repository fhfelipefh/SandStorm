package com.fhfelipefh.sandstorm.content.satellite;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SatelliteSavedData extends SavedData {

    public static final Codec<SatelliteSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("satellite_active", false).forGetter(SatelliteSavedData::isSatelliteActive),
            Codec.LONG.optionalFieldOf("launch_game_time", 0L).forGetter(SatelliteSavedData::getLaunchGameTime)
    ).apply(instance, SatelliteSavedData::new));

    public static final SavedDataType<SatelliteSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("satellite_network"),
            SatelliteSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private boolean satelliteActive;
    private long launchGameTime;

    public SatelliteSavedData() {
        this(false, 0L);
    }

    public SatelliteSavedData(boolean satelliteActive, long launchGameTime) {
        this.satelliteActive = satelliteActive;
        this.launchGameTime = launchGameTime;
    }

    public boolean isSatelliteActive() {
        return satelliteActive;
    }

    public void setSatelliteActive(boolean satelliteActive) {
        this.satelliteActive = satelliteActive;
        setDirty();
    }

    public long getLaunchGameTime() {
        return launchGameTime;
    }

    public void setLaunchGameTime(long launchGameTime) {
        this.launchGameTime = launchGameTime;
        setDirty();
    }
}
