package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceSuitItemTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldDefineSpaceSuitConstantsAndDurability() {
        assertEquals(25, SpaceSuitItem.BASE_DURABILITY_FACTOR);
        assertEquals(Rarity.UNCOMMON, SpaceSuitItem.SUIT_RARITY);

        int helmetDurability = ArmorType.HELMET.getDurability(SpaceSuitItem.BASE_DURABILITY_FACTOR);
        int chestplateDurability = ArmorType.CHESTPLATE.getDurability(SpaceSuitItem.BASE_DURABILITY_FACTOR);
        int leggingsDurability = ArmorType.LEGGINGS.getDurability(SpaceSuitItem.BASE_DURABILITY_FACTOR);
        int bootsDurability = ArmorType.BOOTS.getDurability(SpaceSuitItem.BASE_DURABILITY_FACTOR);

        assertTrue(chestplateDurability > helmetDurability);
        assertTrue(leggingsDurability > bootsDurability);
        assertTrue(helmetDurability > 0);
        assertTrue(bootsDurability > 0);
    }
}
