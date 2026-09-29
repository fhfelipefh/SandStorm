package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MagneticInterferencePayload(int durationTicks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MagneticInterferencePayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("magnetic_interference"));

    public static final StreamCodec<ByteBuf, MagneticInterferencePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    MagneticInterferencePayload::durationTicks,
                    MagneticInterferencePayload::new
            );

    @Override
    public CustomPacketPayload.Type<MagneticInterferencePayload> type() {
        return TYPE;
    }
}
