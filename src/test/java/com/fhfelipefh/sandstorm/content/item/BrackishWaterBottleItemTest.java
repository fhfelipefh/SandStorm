package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrackishWaterBottleItemTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldHaveLowNutritionAndDirectEdibility() {
        assertEquals(1, BrackishWaterBottleItem.BRACKISH_FOOD.nutrition());
        assertEquals(0.1f, BrackishWaterBottleItem.BRACKISH_FOOD.saturation(), 0.001f);
        assertTrue(BrackishWaterBottleItem.BRACKISH_FOOD.canAlwaysEat());
    }
}
