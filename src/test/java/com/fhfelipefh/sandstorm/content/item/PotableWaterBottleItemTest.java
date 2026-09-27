package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PotableWaterBottleItemTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldProvideHydrationAndNutrition() {
        assertEquals(6, PotableWaterBottleItem.POTABLE_FOOD.nutrition());
        assertEquals(8.0f, PotableWaterBottleItem.POTABLE_FOOD.saturation(), 0.001f);
        assertTrue(PotableWaterBottleItem.POTABLE_FOOD.canAlwaysEat());
    }
}
