package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class SubcriticalFissionCoreItem extends Item {

    public SubcriticalFissionCoreItem(Properties properties) {
        super(properties
                .stacksTo(16)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.subcritical_fission_core.desc").withStyle(ChatFormatting.GOLD));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.subcritical_fission_core.radiation").withStyle(ChatFormatting.DARK_GREEN));
    }
}
