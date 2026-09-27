package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceRationItemTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldHaveEnhancedNutritionAndSaturation() {
        assertEquals(10, SpaceRationItem.SPACE_RATION_FOOD.nutrition());
        assertEquals(15.0f, SpaceRationItem.SPACE_RATION_FOOD.saturation());
        assertTrue(SpaceRationItem.SPACE_RATION_FOOD.canAlwaysEat());
    }
}

