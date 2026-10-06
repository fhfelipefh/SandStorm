package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CallGolemPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CallGolemPayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("call_golem"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CallGolemPayload> STREAM_CODEC = StreamCodec.unit(new CallGolemPayload());

    @Override
    public CustomPacketPayload.Type<CallGolemPayload> type() {
        return TYPE;
    }
}
