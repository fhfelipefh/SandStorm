package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class MiniNukeItem extends Item {

    public MiniNukeItem(Properties properties) {
        super(properties
                .stacksTo(4)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.mini_nuke.desc").withStyle(ChatFormatting.GREEN));
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.mini_nuke.cataclysm").withStyle(ChatFormatting.RED));
    }
}
