package com.fhfelipefh.sandstorm.content.world;

import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceshipSavedDataTest {

    @Test
    void shouldInitializeWithDefaultValues() {
        SpaceshipSavedData data = new SpaceshipSavedData();
        assertFalse(data.isPlaced());
        assertEquals(0, data.getCabinX());
        assertEquals(64, data.getCabinY());
        assertEquals(0, data.getCabinZ());
        assertEquals(new BlockPos(0, 64, 0), data.getCabinPos());
    }

    @Test
    void shouldUpdatePlacementAndCoordinates() {
        SpaceshipSavedData data = new SpaceshipSavedData();
        data.setPlaced(true);
        data.setCabinPos(0, 72, 0);

        assertTrue(data.isPlaced());
        assertEquals(0, data.getCabinX());
        assertEquals(72, data.getCabinY());
        assertEquals(0, data.getCabinZ());
        assertEquals(new BlockPos(0, 72, 0), data.getCabinPos());
        assertTrue(data.isDirty());
    }

    @Test
    void shouldSerializeAndDeserializeViaCodec() {
        SpaceshipSavedData original = new SpaceshipSavedData(true, 0, 75, 0);
        DataResult<Tag> encodeResult = SpaceshipSavedData.CODEC.encodeStart(NbtOps.INSTANCE, original);
        assertTrue(encodeResult.isSuccess());

        Tag encodedTag = encodeResult.getOrThrow();
        DataResult<SpaceshipSavedData> decodeResult = SpaceshipSavedData.CODEC.parse(NbtOps.INSTANCE, encodedTag);
        assertTrue(decodeResult.isSuccess());

        SpaceshipSavedData decoded = decodeResult.getOrThrow();
        assertTrue(decoded.isPlaced());
        assertEquals(0, decoded.getCabinX());
        assertEquals(75, decoded.getCabinY());
        assertEquals(0, decoded.getCabinZ());
    }
}
