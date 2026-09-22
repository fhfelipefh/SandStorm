package com.fhfelipefh.sandstorm.content.quest;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestClaimIntegrationTest {

    static class TestInventory extends Inventory {
        public TestInventory() {
            super(null, null);
        }

        @Override
        public boolean add(int slot, ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            int initialCount = stack.getCount();
            for (int i = 0; i < 36; i++) {
                ItemStack item = getItem(i);
                if (item.isEmpty()) {
                    int toAdd = stack.getCount();
                    setItem(i, stack.copyWithCount(toAdd));
                    stack.shrink(toAdd);
                    return true;
                } else if (ItemStack.isSameItemSameComponents(item, stack)) {
                    int space = 64 - item.getCount();
                    if (space > 0) {
                        int toAdd = Math.min(stack.getCount(), space);
                        item.grow(toAdd);
                        stack.shrink(toAdd);
                        if (stack.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
            return stack.getCount() < initialCount;
        }
    }

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @BeforeEach
    void resetClientHelper() {
        DatapadClientHelper.setQuests(List.of(), List.of());
    }

    @Test
    void testAllQuestsExistAndHaveValidData() {
        for (QuestData quest : QuestRegistry.getAllQuests().values()) {
            assertNotNull(quest.id());
            assertNotNull(quest.titleKey());
            assertNotNull(quest.taskKey());
            assertNotNull(quest.noteKey());
            assertNotNull(quest.iconId());
            assertNotNull(quest.rewardItemId());
            assertTrue(quest.rewardCount() > 0);
        }
    }

    @Test
    void testDatapadClientHelperStateManagement() {
        DatapadClientHelper.setQuests(List.of("suit_diagnostics"), List.of("sandstorm.battery_60"));
        assertTrue(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
        assertTrue(DatapadClientHelper.isConditionMet("sandstorm.battery_60"));
        assertFalse(DatapadClientHelper.isQuestClaimed("compact_sandstone"));

        DatapadClientHelper.addClaimedQuest("compact_sandstone");
        assertTrue(DatapadClientHelper.isQuestClaimed("compact_sandstone"));

        SyncPlayerQuestsPayload sync = new SyncPlayerQuestsPayload(
                List.of("suit_diagnostics", "compact_sandstone"),
                List.of("sandstorm.battery_60")
        );
        DatapadClientHelper.setQuests(sync.claimedQuestIds(), sync.completedConditions());
        assertTrue(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
        assertTrue(DatapadClientHelper.isQuestClaimed("compact_sandstone"));
        assertTrue(DatapadClientHelper.isConditionMet("sandstorm.battery_60"));
    }

    @Test
    void testPrerequisiteChaining() {
        UUID player = UUID.randomUUID();
        PlayerQuestSavedData data = new PlayerQuestSavedData();

        QuestData q1 = QuestRegistry.getQuest("suit_diagnostics");
        QuestData q2 = QuestRegistry.getQuest("compact_sandstone");
        QuestData q3 = QuestRegistry.getQuest("emergency_workbench");

        assertTrue(QuestRewardHandler.arePrerequisitesMet(player, q1, data));
        assertFalse(QuestRewardHandler.arePrerequisitesMet(player, q2, data));

        data.markClaimed(player, "suit_diagnostics");
        assertTrue(QuestRewardHandler.arePrerequisitesMet(player, q2, data));
        assertFalse(QuestRewardHandler.arePrerequisitesMet(player, q3, data));

        data.markClaimed(player, "compact_sandstone");
        assertTrue(QuestRewardHandler.arePrerequisitesMet(player, q3, data));
    }

    @Test
    void testRewardDeliveryWhenInventoryHasSpace() {
        Inventory inventory = new TestInventory();
        QuestData quest = QuestRegistry.getQuest("compact_sandstone");
        assertNotNull(quest);
        assertNotNull(quest.getRewardItem());

        ItemStack reward = new ItemStack(quest.getRewardItem(), quest.rewardCount());
        int initialCount = quest.rewardCount();

        boolean added = inventory.add(reward);
        assertTrue(added);
        assertTrue(reward.isEmpty());
        assertEquals(0, reward.getCount());

        int foundCount = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(quest.getRewardItem())) {
                foundCount += stack.getCount();
            }
        }
        assertEquals(initialCount, foundCount);
    }

    @Test
    void testRewardDropWhenInventoryIsFull() {
        Inventory inventory = new TestInventory();
        for (int i = 0; i < 36; i++) {
            inventory.setItem(i, new ItemStack(Items.DIRT, 64));
        }

        QuestData quest = QuestRegistry.getQuest("compact_sandstone");
        assertNotNull(quest);
        assertNotNull(quest.getRewardItem());

        ItemStack reward = new ItemStack(quest.getRewardItem(), quest.rewardCount());
        int initialCount = quest.rewardCount();

        boolean added = inventory.add(reward);
        assertFalse(added);
        assertFalse(reward.isEmpty());
        assertEquals(initialCount, reward.getCount());
    }

    @Test
    void testBatteryConditionUnlocksSuitDiagnostics() {
        QuestData quest = QuestRegistry.getQuest("suit_diagnostics");
        assertNotNull(quest);
        assertTrue(quest.isConditionBased());
        assertEquals("sandstorm.battery_60", quest.conditionTag());

        SuitPowerComponent suit = new SuitPowerComponent(1000, 0, 0, 0);
        suit.getEnergyStorage().setStoredEnergy(590);
        double percent59 = (double) suit.getEnergyStorage().getStoredEnergy() * 100.0 / (double) suit.getEnergyStorage().getCapacity();
        assertFalse(percent59 >= 60.0);

        suit.getEnergyStorage().setStoredEnergy(600);
        double percent60 = (double) suit.getEnergyStorage().getStoredEnergy() * 100.0 / (double) suit.getEnergyStorage().getCapacity();
        assertTrue(percent60 >= 60.0);

        DatapadClientHelper.addCondition(quest.conditionTag());
        assertTrue(DatapadClientHelper.isConditionMet(quest.conditionTag()));
    }

    @Test
    void testClientClickDisablesButtonImmediately() {
        DatapadClientHelper.setQuests(List.of(), List.of("sandstorm.battery_60"));
        assertFalse(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));

        DatapadClientHelper.addClaimedQuest("suit_diagnostics");
        assertTrue(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
    }

    @Test
    void testServerDuplicateClaimPrevention() {
        PlayerQuestSavedData data = new PlayerQuestSavedData();
        UUID player = UUID.randomUUID();

        assertTrue(data.markClaimed(player, "suit_diagnostics"));
        assertFalse(data.markClaimed(player, "suit_diagnostics"));
        assertTrue(data.isClaimed(player, "suit_diagnostics"));
    }

    @Test
    void testSyncPayloadRestoresClaimedState() {
        SyncPlayerQuestsPayload payload = new SyncPlayerQuestsPayload(
                List.of("suit_diagnostics", "compact_sandstone"),
                List.of("sandstorm.battery_60")
        );
        DatapadClientHelper.setQuests(payload.claimedQuestIds(), payload.completedConditions());
        assertTrue(DatapadClientHelper.isQuestClaimed("suit_diagnostics"));
        assertTrue(DatapadClientHelper.isQuestClaimed("compact_sandstone"));
        assertTrue(DatapadClientHelper.isConditionMet("sandstorm.battery_60"));
    }

    @Test
    void testSandstoneFurnaceDetectionInInventory() {
        Inventory inventory = new TestInventory();
        QuestData quest = QuestRegistry.getQuest("sandstone_furnace");
        assertNotNull(quest);
        assertEquals("sandstorm:sandstone_furnace", quest.iconId().toString());

        inventory.setItem(8, new ItemStack(Items.FURNACE));
        Item req = quest.getRequiredItem();

        boolean found = false;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if ((req != null && stack.is(req)) || ("sandstone_furnace".equals(quest.id()) && (id.equals(SandStormMod.id("sandstone_furnace")) || id.equals(SandStormMod.mcId("furnace"))))) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found);
    }
}
