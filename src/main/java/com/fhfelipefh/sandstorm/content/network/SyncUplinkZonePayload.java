package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncUplinkZonePayload(
        BlockPos cornerA,
        BlockPos cornerB,
        int modeOrdinal,
        BlockPos moveTarget
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncUplinkZonePayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("sync_uplink_zone"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncUplinkZonePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBoolean(payload.cornerA() != null);
                if (payload.cornerA() != null) {
                    buf.writeBlockPos(payload.cornerA());
                }
                buf.writeBoolean(payload.cornerB() != null);
                if (payload.cornerB() != null) {
                    buf.writeBlockPos(payload.cornerB());
                }
                buf.writeVarInt(payload.modeOrdinal());
                buf.writeBoolean(payload.moveTarget() != null);
                if (payload.moveTarget() != null) {
                    buf.writeBlockPos(payload.moveTarget());
                }
            },
            buf -> {
                BlockPos cornerA = buf.readBoolean() ? buf.readBlockPos() : null;
                BlockPos cornerB = buf.readBoolean() ? buf.readBlockPos() : null;
                int modeOrdinal = buf.readVarInt();
                BlockPos moveTarget = buf.readBoolean() ? buf.readBlockPos() : null;
                return new SyncUplinkZonePayload(cornerA, cornerB, modeOrdinal, moveTarget);
            }
    );

    public SyncUplinkZonePayload(BlockPos cornerA, BlockPos cornerB, int modeOrdinal) {
        this(cornerA, cornerB, modeOrdinal, null);
    }

    @Override
    public CustomPacketPayload.Type<SyncUplinkZonePayload> type() {
        return TYPE;
    }
}
