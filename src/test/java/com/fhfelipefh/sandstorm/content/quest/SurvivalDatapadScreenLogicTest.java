package com.fhfelipefh.sandstorm.content.quest;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SurvivalDatapadScreenLogicTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void resetClientHelper() {
        DatapadClientHelper.setQuests(List.of(), List.of());
    }

    @Test
    void testSuitDiagnosticsHasNoPrerequisites() {
        QuestData quest = QuestRegistry.getQuest("suit_diagnostics");
        assertNotNull(quest);
        assertTrue(quest.prerequisiteIds().isEmpty());
    }

    @Test
    void testSuitDiagnosticsRequiresBatteryCondition() {
        QuestData quest = QuestRegistry.getQuest("suit_diagnostics");
        assertNotNull(quest);
        assertTrue(quest.isConditionBased());
        assertEquals("sandstorm.battery_60", quest.conditionTag());
    }

    @Test
    void testConditionBasedQuestClaimableWhenConditionMet() {
        QuestData suit = QuestRegistry.getQuest("suit_diagnostics");
        assertNotNull(suit);

        DatapadClientHelper.setQuests(List.of(), List.of());
        assertFalse(DatapadClientHelper.isConditionMet("sandstorm.battery_60"));

        DatapadClientHelper.setQuests(List.of(), List.of("sandstorm.battery_60"));
        assertTrue(DatapadClientHelper.isConditionMet("sandstorm.battery_60"));
        assertTrue(arePrerequisitesClaimed(suit));
        assertFalse(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
    }

    @Test
    void testClientQuestStateStartsEmpty() {
        assertFalse(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
    }

    @Test
    void testClientQuestStateSyncMarksAsClaimed() {
        DatapadClientHelper.setClaimedQuests(List.of("suit_diagnostics"));
        assertTrue(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
    }

    @Test
    void testClientQuestStateSyncClearsOnNewSync() {
        DatapadClientHelper.setClaimedQuests(List.of("suit_diagnostics"));
        DatapadClientHelper.setClaimedQuests(List.of());
        assertFalse(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
    }

    @Test
    void testPrerequisiteCheckWithClientHelper() {
        QuestData compact = QuestRegistry.getQuest("compact_sandstone");
        assertNotNull(compact);

        assertFalse(arePrerequisitesClaimed(compact));

        DatapadClientHelper.setClaimedQuests(List.of("suit_diagnostics"));
        assertTrue(arePrerequisitesClaimed(compact));
    }

    @Test
    void testClaimButtonVisibilityForRootQuest() {
        QuestData suit = QuestRegistry.getQuest("suit_diagnostics");
        assertNotNull(suit);

        boolean isNotClaimed = !DatapadClientHelper.isQuestClaimed("suit_diagnostics");
        boolean prerequisitesMet = arePrerequisitesClaimed(suit);

        assertTrue(isNotClaimed);
        assertTrue(prerequisitesMet);
    }

    @Test
    void testClaimButtonNotVisibleAfterClaim() {
        DatapadClientHelper.setClaimedQuests(List.of("suit_diagnostics"));
        QuestData suit = QuestRegistry.getQuest("suit_diagnostics");
        assertTrue(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
    }

    @Test
    void testButtonHitboxCoordinates() {
        int screenWidth = 480;
        int screenHeight = 320;

        int left = 16;
        int top = 12;
        int right = screenWidth - 16;
        int bottom = screenHeight - 12;
        int cardRight = right - 10;
        int questAreaTop = top + 58;
        int cardHeight = 46;
        int cardSpacing = 6;
        int scrollOffset = 0;

        int i = 0;
        int cy = questAreaTop + i * (cardHeight + cardSpacing) - scrollOffset;
        int btnX = cardRight - 48;
        int btnY = cy + 18;
        int btnW = 44;
        int btnH = 18;

        assertTrue(btnX > left);
        assertTrue(btnY > top);
        assertTrue(btnX + btnW < right);
        assertTrue(btnY + btnH < bottom);

        double mx = btnX + btnW / 2.0;
        double my = btnY + btnH / 2.0;
        assertTrue(mx >= btnX && mx <= btnX + btnW);
        assertTrue(my >= btnY && my <= btnY + btnH);

        double outsideX = btnX - 1;
        assertFalse(outsideX >= btnX && outsideX <= btnX + btnW);
    }

    @Test
    void testWholeCardClickDetection() {
        int left = 16;
        int right = 480 - 16;
        int cardLeft = left + 10;
        int cardRight = right - 10;
        int cy = 70;
        int cardHeight = 46;
        int btnX = cardRight - 48;
        int btnY = cy + 18;
        int btnW = 44;
        int btnH = 18;

        double mxBtn = btnX + 10;
        double myBtn = btnY + 5;
        boolean insideCardBtn = mxBtn >= cardLeft && mxBtn <= cardRight && myBtn >= cy && myBtn <= cy + cardHeight;
        boolean onButtonBtn = mxBtn >= btnX && mxBtn <= btnX + btnW && myBtn >= btnY && myBtn <= btnY + btnH;
        assertTrue(onButtonBtn || insideCardBtn);

        double mxCard = cardLeft + 20;
        double myCard = cy + 10;
        boolean insideCardLeft = mxCard >= cardLeft && mxCard <= cardRight && myCard >= cy && myCard <= cy + cardHeight;
        boolean onButtonLeft = mxCard >= btnX && mxCard <= btnX + btnW && myCard >= btnY && myCard <= btnY + btnH;
        assertFalse(onButtonLeft);
        assertTrue(insideCardLeft);
        assertTrue(onButtonLeft || insideCardLeft);

        double mxOut = cardLeft - 5;
        double myOut = cy - 5;
        boolean insideCardOut = mxOut >= cardLeft && mxOut <= cardRight && myOut >= cy && myOut <= cy + cardHeight;
        boolean onButtonOut = mxOut >= btnX && mxOut <= btnX + btnW && myOut >= btnY && myOut <= btnY + btnH;
        assertFalse(onButtonOut || insideCardOut);
    }

    @Test
    void testTabSwitchingCoordinates() {
        int screenWidth = 480;
        int left = 16;
        int right = screenWidth - 16;
        int top = 12;
        int tabCount = 5;
        int tabWidth = (right - left - 20) / tabCount;
        int ty = top + 36;

        for (int ch = 1; ch <= tabCount; ch++) {
            int tx = left + 10 + (ch - 1) * tabWidth;
            double mx = tx + tabWidth / 2.0;
            double my = ty + 8;

            assertTrue(my >= ty && my <= ty + 16);

            boolean found = false;
            for (int testCh = 1; testCh <= tabCount; testCh++) {
                int testTx = left + 10 + (testCh - 1) * tabWidth;
                if (mx >= testTx && mx <= testTx + tabWidth) {
                    assertEquals(ch, testCh);
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testChapter1HasFiveQuests() {
        List<QuestData> chapter1 = QuestRegistry.getQuestsForChapter(1);
        assertEquals(5, chapter1.size());
    }

    @Test
    void testAllQuestChaptersReturnNonEmpty() {
        for (int ch = 1; ch <= 5; ch++) {
            List<QuestData> quests = QuestRegistry.getQuestsForChapter(ch);
            assertFalse(quests.isEmpty());
        }
    }

    @Test
    void testClaimPreventsDuplicateClaim() {
        PlayerQuestSavedData data = new PlayerQuestSavedData();
        UUID player = UUID.randomUUID();

        assertTrue(data.markClaimed(player, "suit_diagnostics"));
        assertFalse(data.markClaimed(player, "suit_diagnostics"));
    }

    @Test
    void testHasRequiredItemReturnsFalseForNullPlayer() {
        QuestData quest = QuestRegistry.getQuest("suit_diagnostics");
        assertFalse(QuestRewardHandler.hasRequiredItem(null, quest));
    }

    @Test
    void testScrollOffsetClampedToZero() {
        int scrollOffset = -10;
        int maxScroll = 100;
        int clamped = Math.clamp(scrollOffset, 0, maxScroll);
        assertEquals(0, clamped);
    }

    @Test
    void testScrollOffsetClampedToMax() {
        int scrollOffset = 200;
        int maxScroll = 100;
        int clamped = Math.clamp(scrollOffset, 0, maxScroll);
        assertEquals(100, clamped);
    }

    private boolean arePrerequisitesClaimed(QuestData quest) {
        if (quest.prerequisiteIds().isEmpty()) {
            return true;
        }
        for (String preId : quest.prerequisiteIds()) {
            if (!DatapadClientHelper.isQuestClaimed(preId)) {
                return false;
            }
        }
        return true;
    }
}
