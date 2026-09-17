package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

public class SpaceSuitItem extends Item {
    private final ArmorType armorType;

    public SpaceSuitItem(ArmorType armorType) {
        super(new Item.Properties()
                .humanoidArmor(ArmorMaterials.IRON, armorType)
                .durability(armorType.getDurability(25))
                .rarity(Rarity.UNCOMMON));
        this.armorType = armorType;
    }

    public ArmorType getArmorType() {
        return armorType;
    }
}
