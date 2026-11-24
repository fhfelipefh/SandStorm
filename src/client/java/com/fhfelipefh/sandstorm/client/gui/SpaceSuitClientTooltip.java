package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.survival.MugenInfinityHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpaceSuitClientTooltip {

    public static List<Component> buildTooltip(ItemStack stack) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("tooltip.sandstorm.suit_fused_chassis").withStyle(ChatFormatting.DARK_AQUA));

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return list;
        }

        Set<String> tags = player.entityTags();
        list.add(Component.translatable("tooltip.sandstorm.suit_modules_title").withStyle(ChatFormatting.GOLD));

        boolean hasAnyModule = false;
        if (tags.contains("sandstorm.upgrade.jetpack")) {
            list.add(Component.literal("§a✔ §bJetpack §7(Propulsão Contínua)"));
            hasAnyModule = true;
        }
        if (tags.contains("sandstorm.upgrade.battery")) {
            list.add(Component.literal("§a✔ §bBateria §7(Capacidade Aumentada)"));
            hasAnyModule = true;
        }
        if (tags.contains("sandstorm.upgrade.thermal")) {
            list.add(Component.literal("§a✔ §bFiltro Térmico §7(Proteção Clima)"));
            hasAnyModule = true;
        }
        if (tags.contains("sandstorm.upgrade.visor")) {
            list.add(Component.literal("§a✔ §bVisor Tático §7(HUD Telemetria)"));
            hasAnyModule = true;
        }
        if (tags.contains("sandstorm.upgrade.seismic")) {
            list.add(Component.literal("§a✔ §bScanner Sísmico §7(Vibrações)"));
            hasAnyModule = true;
        }

        if (tags.contains("sandstorm.chip.glider")) {
            list.add(Component.literal("§e✦ §6Chip Planador §7(Voo Planado)"));
            hasAnyModule = true;
        }
        if (tags.contains("sandstorm.chip.capacity")) {
            list.add(Component.literal("§e✦ §6Chip Expansor §7(Carga Dobrada)"));
            hasAnyModule = true;
        }
        if (tags.contains("sandstorm.chip.evasive_reflex_thruster")) {
            list.add(Component.literal("§e✦ §6Esquiva Tática §7(Teleporte)"));
            hasAnyModule = true;
        }
        if (tags.contains("sandstorm.chip.asymptotic_infinity_barrier") || MugenInfinityHandler.isClientMugenActive(player.getId())) {
            list.add(Component.literal("§b∞ §3Mugen §7(Barreira Assintótica)"));
            hasAnyModule = true;
        }

        if (!hasAnyModule) {
            list.add(Component.translatable("tooltip.sandstorm.suit_no_modules").withStyle(ChatFormatting.DARK_GRAY));
        }

        return list;
    }
}
