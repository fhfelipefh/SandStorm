package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record SyncAnomalyRadarPayload(
        int playerX,
        int playerY,
        int playerZ,
        float playerYaw,
        boolean ionInterference,
        List<AnomalyPing> pings
) implements CustomPacketPayload {

    public record AnomalyPing(int x, int y, int z, String type) {}

    public static final CustomPacketPayload.Type<SyncAnomalyRadarPayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("sync_anomaly_radar"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAnomalyRadarPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.playerX());
                buf.writeInt(payload.playerY());
                buf.writeInt(payload.playerZ());
                buf.writeFloat(payload.playerYaw());
                buf.writeBoolean(payload.ionInterference());
                buf.writeInt(payload.pings().size());
                for (AnomalyPing ping : payload.pings()) {
                    buf.writeInt(ping.x());
                    buf.writeInt(ping.y());
                    buf.writeInt(ping.z());
                    buf.writeUtf(ping.type());
                }
            },
            buf -> {
                int px = buf.readInt();
                int py = buf.readInt();
                int pz = buf.readInt();
                float yaw = buf.readFloat();
                boolean ion = buf.readBoolean();
                int count = buf.readInt();
                List<AnomalyPing> pings = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    pings.add(new AnomalyPing(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUtf()));
                }
                return new SyncAnomalyRadarPayload(px, py, pz, yaw, ion, pings);
            }
    );

    @Override
    public CustomPacketPayload.Type<SyncAnomalyRadarPayload> type() {
        return TYPE;
    }
}

