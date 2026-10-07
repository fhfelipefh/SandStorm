package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.world.biosphere.BiosphereType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class BiosphereCartridgeItem extends Item {

    private final BiosphereType biosphereType;

    public BiosphereCartridgeItem(BiosphereType biosphereType, Item.Properties properties) {
        super(properties);
        this.biosphereType = biosphereType;
    }

    public BiosphereType getBiosphereType() {
        return biosphereType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.biosphere_cartridge.type", biosphereType.getDisplayName()).withStyle(ChatFormatting.AQUA));
        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.biosphere_cartridge.desc." + biosphereType.getId()).withStyle(ChatFormatting.GRAY));
    }
}
