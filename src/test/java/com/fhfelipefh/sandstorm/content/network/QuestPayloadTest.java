package com.fhfelipefh.sandstorm.content.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestPayloadTest {

    @Test
    void testClaimQuestRewardPayloadTypeAndCodec() {
        ClaimQuestRewardPayload payload = new ClaimQuestRewardPayload("suit_diagnostics");
        assertEquals("suit_diagnostics", payload.questId());
        assertEquals(ClaimQuestRewardPayload.TYPE, payload.type());
        assertNotNull(ClaimQuestRewardPayload.STREAM_CODEC);
        assertEquals("sandstorm:claim_quest_reward", ClaimQuestRewardPayload.TYPE.id().toString());

        ByteBuf underlying = Unpooled.buffer();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(underlying, null);
        ClaimQuestRewardPayload.STREAM_CODEC.encode(buf, payload);

        ClaimQuestRewardPayload decoded = ClaimQuestRewardPayload.STREAM_CODEC.decode(buf);
        assertEquals("suit_diagnostics", decoded.questId());
    }

    @Test
    void testSyncPlayerQuestsPayloadTypeAndCodec() {
        SyncPlayerQuestsPayload payload = new SyncPlayerQuestsPayload(
                List.of("quest_1", "quest_2", "quest_3"),
                List.of("sandstorm.battery_60")
        );
        assertEquals(List.of("quest_1", "quest_2", "quest_3"), payload.claimedQuestIds());
        assertEquals(List.of("sandstorm.battery_60"), payload.completedConditions());
        assertEquals(SyncPlayerQuestsPayload.TYPE, payload.type());
        assertNotNull(SyncPlayerQuestsPayload.STREAM_CODEC);
        assertEquals("sandstorm:sync_player_quests", SyncPlayerQuestsPayload.TYPE.id().toString());

        ByteBuf underlying = Unpooled.buffer();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(underlying, null);
        SyncPlayerQuestsPayload.STREAM_CODEC.encode(buf, payload);

        SyncPlayerQuestsPayload decoded = SyncPlayerQuestsPayload.STREAM_CODEC.decode(buf);
        assertEquals(List.of("quest_1", "quest_2", "quest_3"), decoded.claimedQuestIds());
        assertEquals(List.of("sandstorm.battery_60"), decoded.completedConditions());
    }
}
