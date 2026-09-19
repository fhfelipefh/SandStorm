package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SuitSurvivalHandler {
    private record LastSyncState(long energy, long capacity, double temperature, int armorCount, int tick) {}
    private static final Map<UUID, SuitPowerComponent> PLAYER_SUIT_MAP = new ConcurrentHashMap<>();
    private static final Map<UUID, LastSyncState> LAST_SYNC_MAP = new ConcurrentHashMap<>();

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
        boolean exposedToSunlight = isDay && canSeeSky && pos.getY() >= 50;
        boolean underground = pos.getY() < 50;
        double ambientTemperature = calculateAmbientTemperature(player, pos.getY(), canSeeSky, isDay);
        SandstormWeatherComponent weather = SandstormWeatherHandler.getWeather();
        double solarMultiplier = weather.getSolarEfficiencyMultiplier();

        suit.tick(exposedToSunlight, ambientTemperature, underground, solarMultiplier);

        if (suit.getEnergyStorage().getStoredEnergy() >= suit.getEnergyStorage().getCapacity() * 0.6) {
            if (!player.entityTags().contains("sandstorm.battery_60")) {
                player.addTag("sandstorm.battery_60");
                MinecraftServer server = player.level().getServer();
                if (server != null) {
                    PlayerQuestSavedData data = PlayerQuestSavedData.get(server);
                    QuestRewardHandler.syncPlayerQuests(player, data);
                    QuestRewardHandler.checkPlayerNotifications(player, data);
                }
            }
        }

        long currentEnergy = suit.getEnergyStorage().getStoredEnergy();
        long currentCapacity = suit.getEnergyStorage().getCapacity();
        double currentTemp = suit.getThermal().getCurrentTemperature();

        LastSyncState last = LAST_SYNC_MAP.get(player.getUUID());
        boolean shouldSync = last == null
                || (player.tickCount - last.tick >= 20)
                || Math.abs(currentEnergy - last.energy) >= 5
                || currentEnergy == 0
                || currentEnergy == currentCapacity
                || Math.abs(currentTemp - last.temperature) >= 0.1
                || armorCount != last.armorCount;

        if (shouldSync) {
            LAST_SYNC_MAP.put(player.getUUID(), new LastSyncState(currentEnergy, currentCapacity, currentTemp, armorCount, player.tickCount));
            ServerPlayNetworking.send(
                    player,
                    new SuitSyncPayload(
                            currentEnergy,
                            currentCapacity,
                            currentTemp,
                            armorCount
                    )
            );
        }

        if (suit.isFullSuitEquipped()) {
            boolean lowBattery = suit.getEnergyStorage().getStoredEnergy() > 0 &&
                    suit.getEnergyStorage().getStoredEnergy() < suit.getEnergyStorage().getCapacity() * 0.15;
            if (lowBattery && player.tickCount % 120 == 0) {
                player.level().playSound(null, pos, SandStormSoundEvents.SUIT_BATTERY_LOW, SoundSource.PLAYERS, 0.8f, 1.0f);
            }
            if (exposedToSunlight && !underground && player.tickCount % 200 == 0 && suit.getEnergyStorage().getStoredEnergy() < suit.getEnergyStorage().getCapacity()) {
                player.level().playSound(null, pos, SandStormSoundEvents.SUIT_SOLAR_CHARGE, SoundSource.PLAYERS, 0.6f, 1.1f);
            }
        }

        if (!player.isCreative() && !player.isSpectator()) {
            if (suit.getThermal().isOverheating() && player.tickCount % 30 == 0) {
                float excess = (float) (suit.getThermal().getCurrentTemperature() - 50.0);
                float damage = Math.max(1.0f, 1.0f + excess * 0.25f);
                player.hurtServer(player.level(), player.damageSources().dryOut(), damage);
            } else if (suit.getThermal().isFreezing() && player.tickCount % 30 == 0) {
                float deficit = (float) (10.0 - suit.getThermal().getCurrentTemperature());
                float damage = Math.max(1.0f, 1.0f + deficit * 0.25f);
                player.hurtServer(player.level(), player.damageSources().freeze(), damage);
                player.setTicksFrozen(Math.min(player.getTicksFrozen() + 40, 140));
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

    private static boolean isSuitItem(ItemStack stack, Item expected) {
        return !stack.isEmpty() && stack.is(expected);
    }

    public static double calculateAmbientTemperature(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        boolean canSeeSky = (player.tickCount % 10 == 0)
                ? player.level().canSeeSkyFromBelowWater(pos)
                : player.level().canSeeSky(pos);
        boolean isDay = player.level().getSkyDarken() < 4;
        return calculateAmbientTemperature(player, pos.getY(), canSeeSky, isDay);
    }

    public static double calculateAmbientTemperature(ServerPlayer player, int posY, boolean canSeeSky, boolean isDay) {
        if (posY < 50) {
            return Math.max(2.0, 20.0 - (50 - posY) * 0.25);
        }
        if (canSeeSky && isDay) {
            return 48.0 + 0.3 * Math.sin(player.tickCount * 0.05);
        } else if (canSeeSky) {
            return 8.0 + 0.2 * Math.cos(player.tickCount * 0.05);
        } else if (isDay) {
            return 24.0 + 0.2 * Math.sin(player.tickCount * 0.05);
        } else {
            return 16.0;
        }
    }

    public static SuitPowerComponent getOrCreateSuit(UUID playerUuid) {
        return PLAYER_SUIT_MAP.computeIfAbsent(playerUuid, uuid -> new SuitPowerComponent());
    }

    public static void removePlayer(UUID playerUuid) {
        PLAYER_SUIT_MAP.remove(playerUuid);
        LAST_SYNC_MAP.remove(playerUuid);
    }
}
