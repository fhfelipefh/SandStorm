package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.SeismicTrackerComponent;
import com.fhfelipefh.sandstorm.content.survival.SeismicSurvivalHandler;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnemyDamageIntegrationTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldValidateSandwormLethalityAndArmorBalance() {
        AttributeSupplier sandwormAttrs = SandwormEntity.createAttributes().build();
        double baseDamage = sandwormAttrs.getBaseValue(Attributes.ATTACK_DAMAGE);
        double maxHealth = sandwormAttrs.getBaseValue(Attributes.MAX_HEALTH);
        double armor = sandwormAttrs.getBaseValue(Attributes.ARMOR);

        assertEquals(18.0, baseDamage, 0.001);
        assertEquals(300.0, maxHealth, 0.001);
        assertEquals(12.0, armor, 0.001);

        int helmetArmor = ArmorMaterials.IRON.defense().get(ArmorType.HELMET);
        int chestplateArmor = ArmorMaterials.IRON.defense().get(ArmorType.CHESTPLATE);
        int leggingsArmor = ArmorMaterials.IRON.defense().get(ArmorType.LEGGINGS);
        int bootsArmor = ArmorMaterials.IRON.defense().get(ArmorType.BOOTS);

        int totalSuitArmor = helmetArmor + chestplateArmor + leggingsArmor + bootsArmor;
        assertEquals(15, totalSuitArmor);

        float defense = (float) totalSuitArmor;
        float factor = 2.0f;
        float reduction = Math.min(20.0f, Math.max(defense / 5.0f, defense - (float) baseDamage / factor));
        float mitigatedDamage = (float) baseDamage * (1.0f - reduction / 25.0f);

        assertTrue(mitigatedDamage < 15.0f);
        assertTrue(mitigatedDamage > 8.0f);
        assertTrue(20.0f - mitigatedDamage > 5.0f);
    }

    @Test
    void shouldValidateMegazordHeavyCombatSuperiorityOverSandworm() {
        AttributeSupplier megazordAttrs = MegazordEntity.createAttributes().build();
        double megazordDamage = megazordAttrs.getBaseValue(Attributes.ATTACK_DAMAGE);
        double megazordArmor = megazordAttrs.getBaseValue(Attributes.ARMOR);
        double megazordHealth = megazordAttrs.getBaseValue(Attributes.MAX_HEALTH);

        assertEquals(30.0, megazordDamage, 0.001);
        assertEquals(25.0, megazordArmor, 0.001);
        assertEquals(500.0, megazordHealth, 0.001);

        double sandwormDamage = 18.0;
        double reductionFactor = Math.min(20.0, Math.max(megazordArmor / 5.0, megazordArmor - sandwormDamage / 2.0));
        double damageToMegazord = sandwormDamage * (1.0 - reductionFactor / 25.0);

        assertTrue(damageToMegazord < 7.0);
        assertTrue(megazordHealth / damageToMegazord > 70);
    }

    @Test
    void shouldValidateSafeZonePreventsEnemyAttacks() {
        SeismicTrackerComponent tracker = SeismicSurvivalHandler.getTracker();
        tracker.reset();

        assertTrue(tracker.isInsideSafeZone(0, 0));
        assertTrue(tracker.isInsideSafeZone(50, 50));
        assertFalse(tracker.isInsideSafeZone(200, 200));

        SeismicTrackerComponent customTracker = new SeismicTrackerComponent(500, 500, 50.0);
        assertTrue(customTracker.isInsideSafeZone(510, 510));
        assertFalse(customTracker.isInsideSafeZone(0, 0));
    }
}
