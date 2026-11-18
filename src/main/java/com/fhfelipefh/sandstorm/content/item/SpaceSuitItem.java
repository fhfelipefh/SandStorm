package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.util.Unit;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

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

    private static Function<ItemStack, List<Component>> clientTooltipProvider;

    private final ArmorType armorType;

    public SpaceSuitItem(ArmorType armorType, Properties properties) {
        super(properties
                .humanoidArmor(SPACE_SUIT_MATERIAL, armorType)
                .durability(armorType.getDurability(BASE_DURABILITY_FACTOR))
                .rarity(SUIT_RARITY)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT
                        .withHidden(DataComponents.ENCHANTMENTS, true)
                        .withHidden(DataComponents.UNBREAKABLE, true)));
        this.armorType = armorType;
    }

    public static void setClientTooltipProvider(Function<ItemStack, List<Component>> provider) {
        clientTooltipProvider = provider;
    }

    public ArmorType getArmorType() {
        return armorType;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        if (clientTooltipProvider != null) {
            List<Component> extra = clientTooltipProvider.apply(stack);
            if (extra != null) {
                for (Component comp : extra) {
                    tooltipConsumer.accept(comp);
                }
            }
        } else {
            tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.suit_fused_chassis").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
