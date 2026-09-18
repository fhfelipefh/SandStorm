package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record SyncPlayerQuestsPayload(List<String> claimedQuestIds) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncPlayerQuestsPayload> TYPE = new CustomPacketPayload.Type<>(SandStormMod.id("sync_player_quests"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerQuestsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.claimedQuestIds().size());
                for (String id : payload.claimedQuestIds()) {
                    buf.writeUtf(id);
                }
            },
            buf -> {
                int count = buf.readInt();
                List<String> list = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    list.add(buf.readUtf());
                }
                return new SyncPlayerQuestsPayload(list);
            }
    );

    @Override
    public CustomPacketPayload.Type<SyncPlayerQuestsPayload> type() {
        return TYPE;
    }
}
