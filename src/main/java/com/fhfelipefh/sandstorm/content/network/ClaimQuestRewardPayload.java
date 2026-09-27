package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record ClaimQuestRewardPayload(String questId, List<String> clientConditions) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClaimQuestRewardPayload> TYPE = new CustomPacketPayload.Type<>(SandStormMod.id("claim_quest_reward"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimQuestRewardPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeUtf(payload.questId());
                buf.writeInt(payload.clientConditions().size());
                for (String cond : payload.clientConditions()) {
                    buf.writeUtf(cond);
                }
            },
            buf -> {
                String id = buf.readUtf();
                int count = buf.readInt();
                List<String> conditions = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    conditions.add(buf.readUtf());
                }
                return new ClaimQuestRewardPayload(id, conditions);
            }
    );

    public ClaimQuestRewardPayload(String questId) {
        this(questId, List.of());
    }

    @Override
    public CustomPacketPayload.Type<ClaimQuestRewardPayload> type() {
        return TYPE;
    }
}
