package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record SyncPlayerQuestsPayload(List<String> claimedQuestIds, List<String> completedConditions, List<String> readyQuestIds) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncPlayerQuestsPayload> TYPE = new CustomPacketPayload.Type<>(SandStormMod.id("sync_player_quests"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerQuestsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.claimedQuestIds().size());
                for (String id : payload.claimedQuestIds()) {
                    buf.writeUtf(id);
                }
                buf.writeInt(payload.completedConditions().size());
                for (String cond : payload.completedConditions()) {
                    buf.writeUtf(cond);
                }
                buf.writeInt(payload.readyQuestIds().size());
                for (String r : payload.readyQuestIds()) {
                    buf.writeUtf(r);
                }
            },
            buf -> {
                int count = buf.readInt();
                List<String> list = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    list.add(buf.readUtf());
                }
                int condCount = buf.readInt();
                List<String> condList = new ArrayList<>(condCount);
                for (int i = 0; i < condCount; i++) {
                    condList.add(buf.readUtf());
                }
                int readyCount = buf.readInt();
                List<String> readyList = new ArrayList<>(readyCount);
                for (int i = 0; i < readyCount; i++) {
                    readyList.add(buf.readUtf());
                }
                return new SyncPlayerQuestsPayload(list, condList, readyList);
            }
    );

    public SyncPlayerQuestsPayload(List<String> claimedQuestIds, List<String> completedConditions) {
        this(claimedQuestIds, completedConditions, List.of());
    }

    public SyncPlayerQuestsPayload(List<String> claimedQuestIds) {
        this(claimedQuestIds, List.of(), List.of());
    }

    @Override
    public CustomPacketPayload.Type<SyncPlayerQuestsPayload> type() {
        return TYPE;
    }
}
