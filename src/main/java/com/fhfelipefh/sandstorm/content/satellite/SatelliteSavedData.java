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
            Codec.LONG.optionalFieldOf("launch_game_time", 0L).forGetter(SatelliteSavedData::getLaunchGameTime),
            Codec.BOOL.optionalFieldOf("weather_recon_active", false).forGetter(SatelliteSavedData::isWeatherReconActive),
            Codec.BOOL.optionalFieldOf("solar_reflector_active", false).forGetter(SatelliteSavedData::isSolarReflectorActive),
            Codec.BOOL.optionalFieldOf("sar_geological_active", false).forGetter(SatelliteSavedData::isSarGeologicalActive),
            Codec.BOOL.optionalFieldOf("kinetic_lance_active", false).forGetter(SatelliteSavedData::isKineticLanceActive),
            Codec.INT.optionalFieldOf("satellite_count", 0).forGetter(SatelliteSavedData::getSatelliteCount)
    ).apply(instance, SatelliteSavedData::new));

    public static final SavedDataType<SatelliteSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("satellite_network"),
            SatelliteSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private boolean satelliteActive;
    private long launchGameTime;
    private boolean weatherReconActive;
    private boolean solarReflectorActive;
    private boolean sarGeologicalActive;
    private boolean kineticLanceActive;
    private int satelliteCount;

    public SatelliteSavedData() {
        this(false, 0L, false, false, false, false, 0);
    }

    public SatelliteSavedData(boolean satelliteActive, long launchGameTime) {
        this(satelliteActive, launchGameTime, false, false, false, false, satelliteActive ? 1 : 0);
    }

    public SatelliteSavedData(boolean satelliteActive, long launchGameTime, boolean weatherReconActive, boolean solarReflectorActive, boolean sarGeologicalActive, boolean kineticLanceActive, int satelliteCount) {
        this.satelliteActive = satelliteActive;
        this.launchGameTime = launchGameTime;
        this.weatherReconActive = weatherReconActive;
        this.solarReflectorActive = solarReflectorActive;
        this.sarGeologicalActive = sarGeologicalActive;
        this.kineticLanceActive = kineticLanceActive;
        this.satelliteCount = satelliteCount;
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

    public boolean isWeatherReconActive() {
        return weatherReconActive;
    }

    public void setWeatherReconActive(boolean active) {
        this.weatherReconActive = active;
        if (active) {
            this.satelliteActive = true;
        }
        setDirty();
    }

    public boolean isSolarReflectorActive() {
        return solarReflectorActive;
    }

    public void setSolarReflectorActive(boolean active) {
        this.solarReflectorActive = active;
        if (active) {
            this.satelliteActive = true;
        }
        setDirty();
    }

    public boolean isSarGeologicalActive() {
        return sarGeologicalActive;
    }

    public void setSarGeologicalActive(boolean active) {
        this.sarGeologicalActive = active;
        if (active) {
            this.satelliteActive = true;
        }
        setDirty();
    }

    public boolean isKineticLanceActive() {
        return kineticLanceActive;
    }

    public void setKineticLanceActive(boolean active) {
        this.kineticLanceActive = active;
        if (active) {
            this.satelliteActive = true;
        }
        setDirty();
    }

    public int getSatelliteCount() {
        return satelliteCount;
    }

    public void setSatelliteCount(int count) {
        this.satelliteCount = count;
        setDirty();
    }

    public void incrementSatelliteCount() {
        this.satelliteCount++;
        this.satelliteActive = true;
        setDirty();
    }
}
