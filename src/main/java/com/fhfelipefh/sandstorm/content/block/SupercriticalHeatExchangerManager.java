package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.survival.SuitSurvivalHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SupercriticalHeatExchangerManager {
    private static final Map<ResourceKey<Level>, Map<BlockPos, Integer>> EXCHANGER_MAP = new ConcurrentHashMap<>();
    public static final double EFFECTIVE_RADIUS = 64.0;
    public static final long MAX_WPT_TRANSFER = 500L;

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

    public static void registerExchanger(ResourceKey<Level> dim, BlockPos pos, int genRate) {
        EXCHANGER_MAP.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), Math.max(0, genRate));
    }

    public static void unregisterExchanger(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Integer> map = EXCHANGER_MAP.get(dim);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static Map<BlockPos, Integer> getExchangers(ResourceKey<Level> dim) {
        return EXCHANGER_MAP.getOrDefault(dim, Map.of());
    }

    public static int getGenRate(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Integer> map = EXCHANGER_MAP.get(dim);
        if (map == null) {
            return 0;
        }
        return map.getOrDefault(pos, 0);
    }

    public static void clear() {
        EXCHANGER_MAP.clear();
    }

    public static long getWptChargeAt(Level level, BlockPos pos) {
        Map<BlockPos, Integer> map = EXCHANGER_MAP.get(level.dimension());
        if (map == null || map.isEmpty()) {
            return 0L;
        }
        double radiusSq = EFFECTIVE_RADIUS * EFFECTIVE_RADIUS;
        long maxCharge = 0L;
        for (Map.Entry<BlockPos, Integer> entry : map.entrySet()) {
            int rate = entry.getValue();
            if (rate > 0 && pos.distSqr(entry.getKey()) <= radiusSq) {
                long charge = Math.min(MAX_WPT_TRANSFER, (long) rate / 10L);
                if (charge > maxCharge) {
                    maxCharge = charge;
                }
            }
        }
        return maxCharge;
    }

    public static void tickLevel(ServerLevel level) {
        Map<BlockPos, Integer> map = EXCHANGER_MAP.get(level.dimension());
        if (map == null || map.isEmpty() || level.players().isEmpty()) {
            return;
        }

        double radiusSq = EFFECTIVE_RADIUS * EFFECTIVE_RADIUS;

        for (Map.Entry<BlockPos, Integer> entry : map.entrySet()) {
            BlockPos pos = entry.getKey();
            int genRate = entry.getValue();

            if (!level.isLoaded(pos) || genRate <= 0) {
                continue;
            }

            AABB searchBox = new AABB(
                    pos.getX() - EFFECTIVE_RADIUS, pos.getY() - EFFECTIVE_RADIUS, pos.getZ() - EFFECTIVE_RADIUS,
                    pos.getX() + EFFECTIVE_RADIUS + 1, pos.getY() + EFFECTIVE_RADIUS + 1, pos.getZ() + EFFECTIVE_RADIUS + 1
            );
            for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, searchBox)) {
                if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= radiusSq) {
                    if (SuitSurvivalHandler.countEquippedSuitPieces(player) > 0) {
                        SuitPowerComponent suit = SuitSurvivalHandler.getOrCreateSuit(player);
                        long transfer = Math.min(MAX_WPT_TRANSFER * 20L, (long) genRate * 2L);
                        long received = suit.getEnergyStorage().receiveEnergy(transfer);
                        boolean batteryNearlyFull = suit.getEnergyStorage().getStoredEnergy() >= suit.getEnergyStorage().getCapacity() * 0.98;
                        if (received > 0 && !batteryNearlyFull && player.tickCount % 200 == 0) {
                            level.playSound(null, player.blockPosition(), SandStormSoundEvents.SUIT_SOLAR_CHARGE, SoundSource.PLAYERS, 0.08f, 0.9f);
                        }
                    }
                }
            }
        }
    }
}
