package com.fhfelipefh.sandstorm.client.tooltip;

import com.fhfelipefh.sandstorm.content.technical.TechnicalSpecEntry;
import com.fhfelipefh.sandstorm.content.technical.TechnicalSpecificationRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class SandStormTechnicalTooltipHandler implements ItemTooltipCallback {

    public static void initialize() {
        ItemTooltipCallback.EVENT.register(new SandStormTechnicalTooltipHandler());
    }

    private static boolean isShiftDown() {
        return InputConstants.isKeyDown(InputConstants.KEY_LSHIFT) || InputConstants.isKeyDown(InputConstants.KEY_RSHIFT);
    }

    @Override
    public void getTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> lines) {
        if (stack.isEmpty()) {
            return;
        }
        TechnicalSpecEntry spec = TechnicalSpecificationRegistry.get(stack.getItem());
        if (spec == null || spec.details().isEmpty()) {
            return;
        }

        if (!isShiftDown()) {
            lines.add(Component.translatable("tooltip.sandstorm.technical.hint").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            lines.add(Component.literal("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
            lines.add(Component.translatable("tooltip.sandstorm.technical.header").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            for (Component detail : spec.details()) {
                lines.add(detail);
            }
            lines.add(Component.literal("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        }
    }
}
