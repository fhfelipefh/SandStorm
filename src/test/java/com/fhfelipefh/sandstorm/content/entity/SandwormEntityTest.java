package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SandwormEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldBuildEnhancedAttributesForApexThreat() {
        AttributeSupplier.Builder builder = SandwormEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(300.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(18.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(12.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(0.32, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
    }

    @Test
    void shouldValidateSandwormCombatLootItemsAndQuestPrerequisites() {
        QuestData seismicBait = QuestRegistry.getQuest("seismic_bait");
        assertNotNull(seismicBait);
        assertEquals(List.of("seismic_thumper"), seismicBait.prerequisiteIds());
        assertEquals("sandstorm.thumper_activated", seismicBait.conditionTag());
        assertEquals(SandStormMod.id("thumper"), seismicBait.iconId());

        QuestData sandwormHunt = QuestRegistry.getQuest("sandworm_hunt");
        assertNotNull(sandwormHunt);
        assertEquals(List.of("seismic_bait"), sandwormHunt.prerequisiteIds());
        assertEquals("sandstorm.kill_sandworm", sandwormHunt.conditionTag());
        assertEquals(SandStormMod.id("sandworm_spawn_egg"), sandwormHunt.iconId());

        QuestData sandwormHarvest = QuestRegistry.getQuest("sandworm_harvest");
        assertNotNull(sandwormHarvest);
        assertEquals(List.of("sandworm_hunt"), sandwormHarvest.prerequisiteIds());
        assertEquals(SandStormMod.id("sandworm_chitin"), sandwormHarvest.requiredItemId());
        assertEquals(SandStormMod.id("sandworm_chitin"), sandwormHarvest.iconId());
        assertEquals(SandStormMod.id("silicon_wafer"), sandwormHarvest.rewardItemId());
        assertEquals(6, sandwormHarvest.rewardCount());
    }
}
