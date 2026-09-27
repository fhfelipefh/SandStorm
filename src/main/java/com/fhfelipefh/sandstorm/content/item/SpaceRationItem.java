package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SpaceRationItem extends Item {
    public static final FoodProperties SPACE_RATION_FOOD = new FoodProperties(10, 15.0f, true);

    public SpaceRationItem(Properties properties) {
        super(properties
                .food(SPACE_RATION_FOOD)
                .stacksTo(64)
                .rarity(Rarity.RARE));
    }
}
