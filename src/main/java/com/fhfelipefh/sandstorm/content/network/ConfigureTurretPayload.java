package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record ConfigureTurretPayload(
        BlockPos pos,
        int filterMode,
        int targetingStrategy,
        List<String> selectedEntityTypes
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConfigureTurretPayload> TYPE =
            new CustomPacketPayload.Type<>(SandStormMod.id("configure_turret"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigureTurretPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBlockPos(payload.pos());
                buf.writeVarInt(payload.filterMode());
                buf.writeVarInt(payload.targetingStrategy());
                buf.writeVarInt(payload.selectedEntityTypes().size());
                for (String entityId : payload.selectedEntityTypes()) {
                    buf.writeUtf(entityId);
                }
            },
            buf -> {
                BlockPos pos = buf.readBlockPos();
                int filterMode = buf.readVarInt();
                int strategy = buf.readVarInt();
                int size = buf.readVarInt();
                List<String> entities = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    entities.add(buf.readUtf());
                }
                return new ConfigureTurretPayload(pos, filterMode, strategy, entities);
            }
    );

    @Override
    public CustomPacketPayload.Type<ConfigureTurretPayload> type() {
        return TYPE;
    }
}
