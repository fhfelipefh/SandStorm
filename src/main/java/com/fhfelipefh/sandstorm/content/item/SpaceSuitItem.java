package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public class SpaceSuitItem extends Item {
    public static final int BASE_DURABILITY_FACTOR = 25;
    public static final Rarity SUIT_RARITY = Rarity.UNCOMMON;

    public static final ResourceKey<EquipmentAsset> SPACE_SUIT_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, SandStormMod.id("space_suit"));

    public static final ArmorMaterial SPACE_SUIT_MATERIAL = new ArmorMaterial(
            BASE_DURABILITY_FACTOR,
            Map.of(
                    ArmorType.BOOTS, 2,
                    ArmorType.LEGGINGS, 5,
                    ArmorType.CHESTPLATE, 6,
                    ArmorType.HELMET, 2
            ),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            1.0f,
            0.05f,
            ItemTags.REPAIRS_IRON_ARMOR,
            SPACE_SUIT_ASSET
    );

    private final ArmorType armorType;

    public SpaceSuitItem(ArmorType armorType, Properties properties) {
        super(properties
                .humanoidArmor(SPACE_SUIT_MATERIAL, armorType)
                .durability(armorType.getDurability(BASE_DURABILITY_FACTOR))
                .rarity(SUIT_RARITY)
                .component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.ENCHANTMENTS, true)));
        this.armorType = armorType;
    }

    public ArmorType getArmorType() {
        return armorType;
    }
}
