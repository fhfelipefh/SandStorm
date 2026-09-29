package com.fhfelipefh.sandstorm.content.storage;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Locale;
import java.util.function.Consumer;

public class QuantumStorageCartridgeItem extends Item {

    private final StorageCartridgeTier tier;

    public QuantumStorageCartridgeItem(StorageCartridgeTier tier, Item.Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public StorageCartridgeTier getTier() {
        return tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);

        long stored = QuantumDiskStorage.getTotalItemCount(stack);
        int capacity = tier.getCapacity();
        int types = QuantumDiskStorage.getStoredTypeCount(stack);
        int maxTypes = tier.getMaxTypes();

        int percentage = capacity > 0 ? (int) ((stored * 100) / capacity) : 0;
        ChatFormatting color = percentage >= 100 ? ChatFormatting.RED : (percentage >= 80 ? ChatFormatting.GOLD : ChatFormatting.AQUA);

        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.storage_cartridge.capacity",
                String.format(Locale.ROOT, "%,d", stored),
                String.format(Locale.ROOT, "%,d", capacity),
                percentage).withStyle(color));

        tooltipAdder.accept(Component.translatable("tooltip.sandstorm.storage_cartridge.types",
                types,
                maxTypes).withStyle(ChatFormatting.GRAY));

        int filledBars = Math.min(10, Math.max(0, percentage / 10));
        String bar = "█".repeat(filledBars) + "░".repeat(10 - filledBars);
        tooltipAdder.accept(Component.literal(String.format(Locale.ROOT, "[%s] %d%%", bar, percentage)).withStyle(color));
    }
}
