package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.network.SyncAnomalyRadarPayload;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnomalyRadarItemTest {

    @Test
    void shouldDefineRadarSpecifications() {
        assertEquals(20, AnomalyRadarItem.COOLDOWN_TICKS);
        assertEquals(96.0, AnomalyRadarItem.SCAN_RADIUS, 0.001);
    }

    @Test
    void shouldEncodeAndDecodeRadarPayload() {
        List<SyncAnomalyRadarPayload.AnomalyPing> pings = List.of(
                new SyncAnomalyRadarPayload.AnomalyPing(100, 64, 200, "buried_ruins"),
                new SyncAnomalyRadarPayload.AnomalyPing(120, 70, 250, "fuel_silo")
        );
        SyncAnomalyRadarPayload original = new SyncAnomalyRadarPayload(50, 65, 100, 180.0f, true, pings);

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), null);
        SyncAnomalyRadarPayload.STREAM_CODEC.encode(buf, original);
        SyncAnomalyRadarPayload decoded = SyncAnomalyRadarPayload.STREAM_CODEC.decode(buf);

        assertEquals(50, decoded.playerX());
        assertEquals(65, decoded.playerY());
        assertEquals(100, decoded.playerZ());
        assertEquals(180.0f, decoded.playerYaw(), 0.01f);
        assertTrue(decoded.ionInterference());
        assertEquals(2, decoded.pings().size());
        assertEquals("buried_ruins", decoded.pings().get(0).type());
        assertEquals("fuel_silo", decoded.pings().get(1).type());
    }
}
