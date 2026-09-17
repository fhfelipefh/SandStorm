package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SuitSurvivalHandler {
    private static final Map<UUID, SuitPowerComponent> PLAYER_SUIT_MAP = new ConcurrentHashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                handlePlayerTick(player);
            }
        });
    }

    public static void handlePlayerTick(ServerPlayer player) {
        SuitPowerComponent suit = getOrCreateSuit(player.getUUID());
        int armorCount = countEquippedSuitPieces(player);
        suit.updateEquippedArmorCount(armorCount);

        BlockPos pos = player.blockPosition();
        boolean canSeeSky = player.level().canSeeSkyFromBelowWater(pos);
        boolean isDay = player.level().getSkyDarken() < 4;
        boolean exposedToSunlight = isDay && canSeeSky;
        boolean underground = !canSeeSky || pos.getY() < 50;
        double ambientTemperature = calculateAmbientTemperature(player);

        suit.tick(exposedToSunlight, ambientTemperature, underground);
    }

    public static int countEquippedSuitPieces(ServerPlayer player) {
        int count = 0;
        if (isSuitItem(player.getItemBySlot(EquipmentSlot.HEAD), SandStormItems.SPACE_SUIT_HELMET)) {
            count++;
        }
        if (isSuitItem(player.getItemBySlot(EquipmentSlot.CHEST), SandStormItems.SPACE_SUIT_CHESTPLATE)) {
            count++;
        }
        if (isSuitItem(player.getItemBySlot(EquipmentSlot.LEGS), SandStormItems.SPACE_SUIT_LEGGINGS)) {
            count++;
        }
        if (isSuitItem(player.getItemBySlot(EquipmentSlot.FEET), SandStormItems.SPACE_SUIT_BOOTS)) {
            count++;
        }
        return count;
    }

    private static boolean isSuitItem(ItemStack stack, net.minecraft.world.item.Item expected) {
        return !stack.isEmpty() && stack.is(expected);
    }

    private static double calculateAmbientTemperature(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        if (player.level().getSkyDarken() < 4 && player.level().canSeeSkyFromBelowWater(pos)) {
            return 48.0;
        }
        return 22.0;
    }

    public static SuitPowerComponent getOrCreateSuit(UUID playerUuid) {
        return PLAYER_SUIT_MAP.computeIfAbsent(playerUuid, uuid -> new SuitPowerComponent());
    }

    public static void removePlayer(UUID playerUuid) {
        PLAYER_SUIT_MAP.remove(playerUuid);
    }
}
