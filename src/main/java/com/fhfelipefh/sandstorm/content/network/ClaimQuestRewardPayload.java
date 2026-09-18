package com.fhfelipefh.sandstorm.content.network;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClaimQuestRewardPayload(String questId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClaimQuestRewardPayload> TYPE = new CustomPacketPayload.Type<>(SandStormMod.id("claim_quest_reward"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimQuestRewardPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeUtf(payload.questId()),
            buf -> new ClaimQuestRewardPayload(buf.readUtf())
    );

    @Override
    public CustomPacketPayload.Type<ClaimQuestRewardPayload> type() {
        return TYPE;
    }
}
