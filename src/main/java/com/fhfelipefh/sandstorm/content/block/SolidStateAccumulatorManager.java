package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SolidStateAccumulatorManager {
    public static final double DISCHARGE_RADIUS = 48.0;
    public static final long DISCHARGE_TRANSFER_RATE = 120L;

    public record AccumulatorState(int storedEnergy, int maxEnergy, boolean discharging, int mode) {
    }

    private static final Map<ResourceKey<Level>, Map<BlockPos, AccumulatorState>> ACCUMULATOR_MAP = new ConcurrentHashMap<>();

    public static void registerAccumulator(ResourceKey<Level> dim, BlockPos pos, int energy, int maxEnergy, boolean discharging, int mode) {
        ACCUMULATOR_MAP.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), new AccumulatorState(energy, maxEnergy, discharging, mode));
    }

    public static void unregisterAccumulator(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, AccumulatorState> map = ACCUMULATOR_MAP.get(dim);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static void clear() {
        ACCUMULATOR_MAP.clear();
    }

    public static Map<BlockPos, AccumulatorState> getAccumulators(ResourceKey<Level> dim) {
        return ACCUMULATOR_MAP.getOrDefault(dim, Map.of());
    }

    public static long getWptChargeAt(Level level, BlockPos pos) {
        return getWptChargeAt(level.dimension(), pos);
    }

    public static long getWptChargeAt(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, AccumulatorState> map = ACCUMULATOR_MAP.get(dim);
        if (map == null || map.isEmpty()) {
            return 0L;
        }

        double radiusSq = DISCHARGE_RADIUS * DISCHARGE_RADIUS;
        for (Map.Entry<BlockPos, AccumulatorState> entry : map.entrySet()) {
            AccumulatorState state = entry.getValue();
            if (state.discharging() && state.storedEnergy() > 0 && pos.distSqr(entry.getKey()) <= radiusSq) {
                return DISCHARGE_TRANSFER_RATE;
            }
        }
        return 0L;
    }

    public static long getTotalStoredEnergy(ResourceKey<Level> dim) {
        Map<BlockPos, AccumulatorState> map = ACCUMULATOR_MAP.get(dim);
        if (map == null || map.isEmpty()) {
            return 0L;
        }
        long total = 0L;
        for (AccumulatorState state : map.values()) {
            total += state.storedEnergy();
        }
        return total;
    }

    public static long getTotalCapacity(ResourceKey<Level> dim) {
        Map<BlockPos, AccumulatorState> map = ACCUMULATOR_MAP.get(dim);
        if (map == null || map.isEmpty()) {
            return 0L;
        }
        long total = 0L;
        for (AccumulatorState state : map.values()) {
            total += state.maxEnergy();
        }
        return total;
    }

    public static int getDischargingAccumulatorCount(ResourceKey<Level> dim) {
        Map<BlockPos, AccumulatorState> map = ACCUMULATOR_MAP.get(dim);
        if (map == null || map.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (AccumulatorState state : map.values()) {
            if (state.discharging() && state.storedEnergy() > 0) {
                count++;
            }
        }
        return count;
    }
}
