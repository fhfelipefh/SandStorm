package com.fhfelipefh.sandstorm.content.quest;

import com.mojang.serialization.DataResult;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerQuestSavedDataTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testMarkClaimedAndIsClaimed() {
        PlayerQuestSavedData data = new PlayerQuestSavedData();
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();

        assertFalse(data.isClaimed(player1, "suit_diagnostics"));
        assertFalse(data.isClaimed(player2, "suit_diagnostics"));

        boolean firstMark = data.markClaimed(player1, "suit_diagnostics");
        assertTrue(firstMark);
        assertTrue(data.isClaimed(player1, "suit_diagnostics"));
        assertFalse(data.isClaimed(player2, "suit_diagnostics"));

        boolean secondMark = data.markClaimed(player1, "suit_diagnostics");
        assertFalse(secondMark);

        data.markClaimed(player1, "compact_sandstone");
        Set<String> claimed1 = data.getClaimedQuests(player1);
        assertEquals(Set.of("suit_diagnostics", "compact_sandstone"), claimed1);
        assertTrue(data.getClaimedQuests(player2).isEmpty());
    }

    @Test
    void testCodecRoundTripSerialization() {
        PlayerQuestSavedData original = new PlayerQuestSavedData();
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();

        original.markClaimed(player1, "suit_diagnostics");
        original.markClaimed(player1, "compact_sandstone");
        original.markClaimed(player2, "emergency_workbench");

        DataResult<Tag> encodeResult = PlayerQuestSavedData.CODEC.encodeStart(NbtOps.INSTANCE, original);
        assertTrue(encodeResult.isSuccess());

        Tag tag = encodeResult.getOrThrow();
        DataResult<PlayerQuestSavedData> decodeResult = PlayerQuestSavedData.CODEC.parse(NbtOps.INSTANCE, tag);
        assertTrue(decodeResult.isSuccess());

        PlayerQuestSavedData decoded = decodeResult.getOrThrow();
        assertTrue(decoded.isClaimed(player1, "suit_diagnostics"));
        assertTrue(decoded.isClaimed(player1, "compact_sandstone"));
        assertFalse(decoded.isClaimed(player1, "emergency_workbench"));

        assertTrue(decoded.isClaimed(player2, "emergency_workbench"));
        assertFalse(decoded.isClaimed(player2, "suit_diagnostics"));
    }

    @Test
    void testConditionCompletedTrackingAndSerialization() {
        PlayerQuestSavedData original = new PlayerQuestSavedData();
        UUID player1 = UUID.randomUUID();

        assertFalse(original.isConditionCompleted(player1, "sandstorm.battery_60"));
        assertTrue(original.markConditionCompleted(player1, "sandstorm.battery_60"));
        assertFalse(original.markConditionCompleted(player1, "sandstorm.battery_60"));
        assertTrue(original.isConditionCompleted(player1, "sandstorm.battery_60"));
        assertEquals(Set.of("sandstorm.battery_60"), original.getCompletedConditions(player1));

        DataResult<Tag> encodeResult = PlayerQuestSavedData.CODEC.encodeStart(NbtOps.INSTANCE, original);
        assertTrue(encodeResult.isSuccess());

        Tag tag = encodeResult.getOrThrow();
        DataResult<PlayerQuestSavedData> decodeResult = PlayerQuestSavedData.CODEC.parse(NbtOps.INSTANCE, tag);
        assertTrue(decodeResult.isSuccess());

        PlayerQuestSavedData decoded = decodeResult.getOrThrow();
        assertTrue(decoded.isConditionCompleted(player1, "sandstorm.battery_60"));
        assertEquals(Set.of("sandstorm.battery_60"), decoded.getCompletedConditions(player1));
    }

    @Test
    void testFromEntriesDirect() {
        UUID player1 = UUID.randomUUID();
        PlayerQuestSavedData.Entry entry = new PlayerQuestSavedData.Entry(player1, List.of("quest_a", "quest_b"));
        PlayerQuestSavedData data = PlayerQuestSavedData.fromEntries(List.of(entry));

        assertTrue(data.isClaimed(player1, "quest_a"));
        assertTrue(data.isClaimed(player1, "quest_b"));
        assertFalse(data.isClaimed(player1, "quest_c"));
        assertEquals(1, data.getEntries().size());
    }
}
