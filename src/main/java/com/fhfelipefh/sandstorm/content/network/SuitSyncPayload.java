package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SuitSyncPayload(long storedEnergy, long capacity, double temperature, int armorCount) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SuitSyncPayload> TYPE = new CustomPacketPayload.Type<>(SandStormMod.id("suit_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SuitSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeLong(payload.storedEnergy());
                buf.writeLong(payload.capacity());
                buf.writeDouble(payload.temperature());
                buf.writeInt(payload.armorCount());
            },
            buf -> new SuitSyncPayload(buf.readLong(), buf.readLong(), buf.readDouble(), buf.readInt())
    );

    @Override
    public CustomPacketPayload.Type<SuitSyncPayload> type() {
        return TYPE;
    }
}
