package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SpaceshipSavedData extends SavedData {

    public static final Codec<SpaceshipSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("placed", false).forGetter(SpaceshipSavedData::isPlaced),
            Codec.INT.optionalFieldOf("cabin_x", 0).forGetter(SpaceshipSavedData::getCabinX),
            Codec.INT.optionalFieldOf("cabin_y", 64).forGetter(SpaceshipSavedData::getCabinY),
            Codec.INT.optionalFieldOf("cabin_z", 0).forGetter(SpaceshipSavedData::getCabinZ)
    ).apply(instance, SpaceshipSavedData::new));

    public static final SavedDataType<SpaceshipSavedData> TYPE = new SavedDataType<>(
            SandStormMod.id("spaceship_crash_site"),
            SpaceshipSavedData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private boolean placed;
    private int cabinX;
    private int cabinY;
    private int cabinZ;

    public SpaceshipSavedData() {
        this(false, 0, 64, 0);
    }

    public SpaceshipSavedData(boolean placed, int cabinX, int cabinY, int cabinZ) {
        this.placed = placed;
        this.cabinX = cabinX;
        this.cabinY = cabinY;
        this.cabinZ = cabinZ;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
        setDirty();
    }

    public int getCabinX() {
        return cabinX;
    }

    public int getCabinY() {
        return cabinY;
    }

    public int getCabinZ() {
        return cabinZ;
    }

    public BlockPos getCabinPos() {
        return new BlockPos(cabinX, cabinY, cabinZ);
    }

    public void setCabinPos(int x, int y, int z) {
        this.cabinX = x;
        this.cabinY = y;
        this.cabinZ = z;
        setDirty();
    }
}
