package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.world.item.Item;

public class SamplingSyringeItem extends Item {
    public static final int MAX_DURABILITY = 64;

    public SamplingSyringeItem(Properties properties) {
        super(properties.durability(MAX_DURABILITY));
    }
}
