package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FlashlightTogglePayload(boolean enabled) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FlashlightTogglePayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("flashlight_toggle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FlashlightTogglePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeBoolean(payload.enabled()),
            buf -> new FlashlightTogglePayload(buf.readBoolean())
    );

    @Override
    public CustomPacketPayload.Type<FlashlightTogglePayload> type() {
        return TYPE;
    }
}
