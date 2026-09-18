package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.component.WirelessChargerComponent;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WirelessSolarReceiverManager {
    private static final Map<ResourceKey<Level>, Map<BlockPos, Integer>> RECEIVER_MAP = new ConcurrentHashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) {
                return;
            }
            for (ServerLevel level : server.getAllLevels()) {
                tickLevel(level);
            }
        });
    }

    public static void registerReceiver(ResourceKey<Level> dim, BlockPos pos, int tier) {
        RECEIVER_MAP.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), tier);
    }

    public static void unregisterReceiver(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Integer> map = RECEIVER_MAP.get(dim);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static void clear() {
        RECEIVER_MAP.clear();
    }

    public static Map<BlockPos, Integer> getReceivers(ResourceKey<Level> dim) {
        return RECEIVER_MAP.getOrDefault(dim, Map.of());
    }

    public static long getWptChargeAt(Level level, BlockPos pos) {
        Map<BlockPos, Integer> map = RECEIVER_MAP.get(level.dimension());
        if (map == null || map.isEmpty()) {
            return 0;
        }
        double weather = com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler.getWeather().getSolarEfficiencyMultiplier();
        boolean isDay = level.getSkyDarken() < 4;
        int skyDarken = level.getSkyDarken();
        long maxCharge = 0;

        for (Map.Entry<BlockPos, Integer> entry : map.entrySet()) {
            BlockPos rPos = entry.getKey();
            int tier = entry.getValue();
            if (!level.isLoaded(rPos)) {
                continue;
            }
            boolean canSeeSky = level.canSeeSky(rPos.above());
            WirelessChargerComponent charger = new WirelessChargerComponent(tier);
            double effectiveRadius = charger.calculateEffectiveRadius(canSeeSky, isDay, skyDarken, weather);
            long rate = charger.calculateTransferRate(canSeeSky, isDay, skyDarken, weather);
            if (effectiveRadius > 0 && rate > 0) {
                if (pos.distSqr(rPos) <= effectiveRadius * effectiveRadius) {
                    if (rate > maxCharge) {
                        maxCharge = rate;
                    }
                }
            }
        }
        return maxCharge;
    }

    public static void tickLevel(ServerLevel level) {
        Map<BlockPos, Integer> map = RECEIVER_MAP.get(level.dimension());
        if (map == null || map.isEmpty()) {
            return;
        }

        double weather = com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler.getWeather().getSolarEfficiencyMultiplier();
        boolean isDay = level.getSkyDarken() < 4;
        int skyDarken = level.getSkyDarken();

        for (Map.Entry<BlockPos, Integer> entry : map.entrySet()) {
            BlockPos pos = entry.getKey();
            int tier = entry.getValue();

            if (!level.isLoaded(pos)) {
                continue;
            }

            if (!(level.getBlockState(pos).getBlock() instanceof WirelessSolarReceiverBlock)) {
                map.remove(pos);
                continue;
            }

            boolean canSeeSky = level.canSeeSky(pos.above());
            WirelessChargerComponent charger = new WirelessChargerComponent(tier);
            double effectiveRadius = charger.calculateEffectiveRadius(canSeeSky, isDay, skyDarken, weather);
            long transferRatePerTick = charger.calculateTransferRate(canSeeSky, isDay, skyDarken, weather);

            if (effectiveRadius <= 0.0 || transferRatePerTick <= 0) {
                continue;
            }

            AABB searchBox = new AABB(
                    pos.getX() - effectiveRadius, pos.getY() - effectiveRadius, pos.getZ() - effectiveRadius,
                    pos.getX() + effectiveRadius + 1, pos.getY() + effectiveRadius + 1, pos.getZ() + effectiveRadius + 1
            );

            double radiusSq = effectiveRadius * effectiveRadius;
            for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, searchBox)) {
                if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= radiusSq) {
                    if (SuitSurvivalHandler.countEquippedSuitPieces(player) > 0) {
                        SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player.getUUID());
                        long totalTransfer = transferRatePerTick * 20;
                        long received = suit.getEnergyStorage().receiveEnergy(totalTransfer);
                        if (received > 0 && player.tickCount % 60 == 0) {
                            level.playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_SOLAR_CHARGE, SoundSource.PLAYERS, 0.5f, 1.2f);
                        }
                    }
                }
            }
        }
    }
}
