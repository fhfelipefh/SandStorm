package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WptRelayTowerManager {
    public static final double EFFECTIVE_RADIUS = 128.0;
    public static final long DEFAULT_TRANSFER_RATE = 100L;
    private static final Map<ResourceKey<Level>, Map<BlockPos, Long>> TOWER_MAP = new ConcurrentHashMap<>();

    public static void registerTower(ResourceKey<Level> dim, BlockPos pos, long transferRate) {
        TOWER_MAP.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), transferRate);
    }

    public static void unregisterTower(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Long> map = TOWER_MAP.get(dim);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static void clear() {
        TOWER_MAP.clear();
    }

    public static Map<BlockPos, Long> getTowers(ResourceKey<Level> dim) {
        return TOWER_MAP.getOrDefault(dim, Map.of());
    }

    public static boolean isPositionCovered(ResourceKey<Level> dim, BlockPos pos) {
        return getWptChargeAt(dim, pos) > 0L;
    }

    public static boolean isPositionCovered(Level level, BlockPos pos) {
        return isPositionCovered(level.dimension(), pos);
    }

    public static long getWptChargeAt(Level level, BlockPos pos) {
        return getWptChargeAt(level.dimension(), pos);
    }

    public static long getWptChargeAt(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Long> map = TOWER_MAP.get(dim);
        if (map == null || map.isEmpty()) {
            return 0L;
        }

        double radiusSq = EFFECTIVE_RADIUS * EFFECTIVE_RADIUS;
        long maxCharge = 0L;

        for (Map.Entry<BlockPos, Long> entry : map.entrySet()) {
            BlockPos towerPos = entry.getKey();
            long rate = entry.getValue();

            if (rate > 0L && pos.distSqr(towerPos) <= radiusSq) {
                if (rate > maxCharge) {
                    maxCharge = rate;
                }
            }
        }

        return maxCharge;
    }
}
