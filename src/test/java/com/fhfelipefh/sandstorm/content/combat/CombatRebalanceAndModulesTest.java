package com.fhfelipefh.sandstorm.content.combat;

import com.fhfelipefh.sandstorm.component.ExoSuitChipComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.content.survival.PlayerSuitSavedData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatRebalanceAndModulesTest {

    @Test
    void testSuitCapacityScaling() {
        SuitPowerComponent suit = new SuitPowerComponent();
        assertEquals(0, suit.getCapacityTier());
        assertEquals(100000L, suit.getEnergyStorage().getCapacity());

        suit.setCapacityTier(1);
        assertEquals(1, suit.getCapacityTier());
        assertEquals(200000L, suit.getEnergyStorage().getCapacity());

        suit.setCapacityTier(2);
        assertEquals(2, suit.getCapacityTier());
        assertEquals(400000L, suit.getEnergyStorage().getCapacity());

        suit.setCapacityTier(3);
        assertEquals(3, suit.getCapacityTier());
        assertEquals(800000L, suit.getEnergyStorage().getCapacity());

        suit.setCapacityTier(20);
        assertEquals(20, suit.getCapacityTier());
        assertFalse(suit.addCapacityTier());
    }

    @Test
    void testEvasiveReflexThrusterEnum() {
        ExoSuitChipComponent chip = ExoSuitChipComponent.EVASIVE_REFLEX_THRUSTER;
        assertNotNull(chip);
        assertEquals("tactical_dodge_module", chip.getId());
        assertEquals("item.sandstorm.tactical_dodge_module", chip.getNameKey());
    }

    @Test
    void testPlayerSuitSavedDataCapacityTier() {
        PlayerSuitSavedData data = new PlayerSuitSavedData();
        UUID uuid = UUID.randomUUID();
        assertEquals(0, data.getCapacityTier(uuid));

        data.setCapacityTier(uuid, 5);
        assertEquals(5, data.getCapacityTier(uuid));

        data.setCapacityTier(uuid, 12);
        assertEquals(12, data.getCapacityTier(uuid));
    }

    @Test
    void testDatapadQuestsOrderingAndPrerequisites() {
        List<QuestData> chapter4 = QuestRegistry.getQuestsForChapter(4);
        assertNotNull(chapter4);

        int idxBattery = -1;
        int idxExpander = -1;
        int idxEvasion = -1;
        int idxBlade = -1;
        int idxSonic = -1;
        int idxThumper = -1;
        int idxBait = -1;
        int idxHunt = -1;

        for (int i = 0; i < chapter4.size(); i++) {
            QuestData q = chapter4.get(i);
            if ("portable_battery_pack".equals(q.id())) idxBattery = i;
            if ("suit_capacity_expander_quest".equals(q.id())) idxExpander = i;
            if ("tactical_evasion".equals(q.id())) idxEvasion = i;
            if ("energized_vibro_blade_quest".equals(q.id())) idxBlade = i;
            if ("sonic_defense".equals(q.id())) idxSonic = i;
            if ("seismic_thumper".equals(q.id())) idxThumper = i;
            if ("seismic_bait".equals(q.id())) idxBait = i;
            if ("sandworm_hunt".equals(q.id())) idxHunt = i;
        }

        assertTrue(idxBattery >= 0, "portable_battery_pack must exist in chapter 4");
        assertTrue(idxExpander >= 0, "suit_capacity_expander_quest must exist in chapter 4");
        assertTrue(idxEvasion >= 0, "tactical_evasion must exist in chapter 4");
        assertTrue(idxBlade >= 0, "energized_vibro_blade_quest must exist in chapter 4");
        assertTrue(idxThumper >= 0, "seismic_thumper must exist in chapter 4");
        assertTrue(idxHunt >= 0, "sandworm_hunt must exist in chapter 4");

        assertTrue(idxBattery < idxThumper, "Battery must come before thumper");
        assertTrue(idxExpander < idxThumper, "Expander must come before thumper");
        assertTrue(idxEvasion < idxThumper, "Evasion must come before thumper");
        assertTrue(idxBlade < idxThumper, "Blade must come before thumper");
        assertTrue(idxSonic < idxThumper, "Sonic defense must come before thumper");
        assertTrue(idxThumper < idxBait, "Thumper must come before bait");
        assertTrue(idxBait < idxHunt, "Bait must come before hunt");

        QuestData thumperQuest = QuestRegistry.getQuest("seismic_thumper");
        assertTrue(thumperQuest.prerequisiteIds().contains("energized_vibro_blade_quest"));
        assertTrue(thumperQuest.prerequisiteIds().contains("tactical_evasion"));

        QuestData huntQuest = QuestRegistry.getQuest("sandworm_hunt");
        assertTrue(huntQuest.prerequisiteIds().contains("seismic_bait"));
        assertTrue(huntQuest.prerequisiteIds().contains("energized_vibro_blade_quest"));
        assertTrue(huntQuest.prerequisiteIds().contains("tactical_evasion"));
    }

    @Test
    void testHoundQuestsInChapter3() {
        List<QuestData> chapter3 = QuestRegistry.getQuestsForChapter(3);
        assertNotNull(chapter3);

        int idxDog = -1;
        int idxArmor = -1;
        int idxClaws = -1;
        int idxDodge = -1;

        for (int i = 0; i < chapter3.size(); i++) {
            QuestData q = chapter3.get(i);
            if ("cyber_hound_companion".equals(q.id())) idxDog = i;
            if ("hound_armor_upgrade".equals(q.id())) idxArmor = i;
            if ("hound_claws_upgrade".equals(q.id())) idxClaws = i;
            if ("hound_dodge_upgrade".equals(q.id())) idxDodge = i;
        }

        assertTrue(idxDog >= 0);
        assertTrue(idxArmor >= 0);
        assertTrue(idxClaws >= 0);
        assertTrue(idxDodge >= 0);

        assertTrue(idxDog < idxArmor);
        assertTrue(idxArmor < idxClaws);
        assertTrue(idxClaws < idxDodge);
    }
}

