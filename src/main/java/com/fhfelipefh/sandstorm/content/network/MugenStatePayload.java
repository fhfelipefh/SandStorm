package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MugenStatePayload(int entityId, boolean active, boolean impactFlash) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MugenStatePayload> TYPE = new CustomPacketPayload.Type<>(SandStormMod.id("mugen_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MugenStatePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.entityId());
                buf.writeBoolean(payload.active());
                buf.writeBoolean(payload.impactFlash());
            },
            buf -> new MugenStatePayload(buf.readInt(), buf.readBoolean(), buf.readBoolean())
    );

    @Override
    public CustomPacketPayload.Type<MugenStatePayload> type() {
        return TYPE;
    }
}
