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
        FusedSpaceSuitHandler.enforceFusedSuit(player);
        SuitPowerComponent suit = getOrCreateSuit(player.getUUID());
        int armorCount = countEquippedSuitPieces(player);
        suit.updateEquippedArmorCount(armorCount);

        BlockPos pos = player.blockPosition();
        boolean canSeeSky = (player.tickCount % 10 == 0)
                ? player.level().canSeeSkyFromBelowWater(pos)
                : player.level().canSeeSky(pos);
        boolean isDay = player.level().getSkyDarken() < 4;
        boolean exposedToSunlight = isDay && canSeeSky;
        boolean underground = !canSeeSky || pos.getY() < 50;
        double ambientTemperature = (isDay && canSeeSky) ? 48.0 : 22.0;
        com.fhfelipefh.sandstorm.component.SandstormWeatherComponent weather = com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler.getWeather();
        double solarMultiplier = weather.getSolarEfficiencyMultiplier();

        suit.tick(exposedToSunlight, ambientTemperature, underground, solarMultiplier);

        if (suit.isFullSuitEquipped()) {
            boolean lowBattery = suit.getEnergyStorage().getStoredEnergy() > 0 &&
                    suit.getEnergyStorage().getStoredEnergy() < suit.getEnergyStorage().getCapacity() * 0.15;
            if (lowBattery && player.tickCount % 120 == 0) {
                player.level().playSound(null, pos, com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents.SUIT_BATTERY_LOW, net.minecraft.sounds.SoundSource.PLAYERS, 0.8f, 1.0f);
            }
            if (exposedToSunlight && !underground && player.tickCount % 200 == 0 && suit.getEnergyStorage().getStoredEnergy() < suit.getEnergyStorage().getCapacity()) {
                player.level().playSound(null, pos, com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents.SUIT_SOLAR_CHARGE, net.minecraft.sounds.SoundSource.PLAYERS, 0.6f, 1.1f);
            }
        }

        if (weather.canCauseSandDamage() && canSeeSky && !suit.isFullSuitEquipped()) {
            if (player.tickCount % 40 == 0) {
                player.hurtServer(player.level(), player.damageSources().dryOut(), 1.0f);
            }
        }
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
        if (player.level().getSkyDarken() < 4 && player.level().canSeeSky(pos)) {
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
