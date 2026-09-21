package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SandstormWeatherPayload(boolean active, double intensity) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SandstormWeatherPayload> TYPE = new CustomPacketPayload.Type<>(SandStormMod.id("sandstorm_weather"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SandstormWeatherPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBoolean(payload.active());
                buf.writeDouble(payload.intensity());
            },
            buf -> new SandstormWeatherPayload(buf.readBoolean(), buf.readDouble())
    );

    @Override
    public CustomPacketPayload.Type<SandstormWeatherPayload> type() {
        return TYPE;
    }
}
