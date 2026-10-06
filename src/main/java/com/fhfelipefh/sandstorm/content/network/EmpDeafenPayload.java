package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EmpDeafenPayload(int durationTicks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EmpDeafenPayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("emp_deafen"));

    public static final StreamCodec<ByteBuf, EmpDeafenPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    EmpDeafenPayload::durationTicks,
                    EmpDeafenPayload::new
            );

    @Override
    public CustomPacketPayload.Type<EmpDeafenPayload> type() {
        return TYPE;
    }
}
