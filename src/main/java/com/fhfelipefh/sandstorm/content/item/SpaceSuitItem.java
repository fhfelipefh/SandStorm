package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

public class SpaceSuitItem extends Item {
    public static final int BASE_DURABILITY_FACTOR = 25;
    public static final Rarity SUIT_RARITY = Rarity.UNCOMMON;

    private final ArmorType armorType;

    public SpaceSuitItem(ArmorType armorType, Properties properties) {
        super(properties
                .humanoidArmor(ArmorMaterials.IRON, armorType)
                .durability(armorType.getDurability(BASE_DURABILITY_FACTOR))
                .rarity(SUIT_RARITY));
        this.armorType = armorType;
    }

    public ArmorType getArmorType() {
        return armorType;
    }
}
