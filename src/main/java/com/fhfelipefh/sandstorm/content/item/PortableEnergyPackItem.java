package com.fhfelipefh.sandstorm.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class PortableEnergyPackItem extends Item {
    public static final long MAX_CAPACITY = 50000L;

    public PortableEnergyPackItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static long getStoredEnergy(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag().getLong("stored_energy").orElse(0L);
    }

    public static void setStoredEnergy(ItemStack stack, long amount) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putLong("stored_energy", Math.clamp(amount, 0L, MAX_CAPACITY));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static long receiveEnergy(ItemStack stack, long maxReceive) {
        if (maxReceive <= 0L) {
            return 0L;
        }
        long current = getStoredEnergy(stack);
        long accepted = Math.min(maxReceive, MAX_CAPACITY - current);
        if (accepted > 0L) {
            setStoredEnergy(stack, current + accepted);
        }
        return accepted;
    }

    public static long extractEnergy(ItemStack stack, long maxExtract) {
        if (maxExtract <= 0L) {
            return 0L;
        }
        long current = getStoredEnergy(stack);
        long extracted = Math.min(maxExtract, current);
        if (extracted > 0L) {
            setStoredEnergy(stack, current - extracted);
        }
        return extracted;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipConsumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipConsumer, flag);
        long stored = getStoredEnergy(stack);
        tooltipConsumer.accept(Component.translatable("tooltip.sandstorm.portable_energy_pack.desc").withStyle(ChatFormatting.GRAY));
        tooltipConsumer.accept(Component.literal(stored + " / " + MAX_CAPACITY + " FE").withStyle(ChatFormatting.AQUA));
    }
}

