package com.fhfelipefh.sandstorm.content.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AcousticDefenseTracker {
    private static final Map<ResourceKey<Level>, Map<BlockPos, Double>> ACTIVE_PYLONS = new ConcurrentHashMap<>();

    public static void registerPylon(ResourceKey<Level> dimension, BlockPos pos, double radius) {
        ACTIVE_PYLONS.computeIfAbsent(dimension, k -> new ConcurrentHashMap<>()).put(pos, radius * radius);
    }

    public static void unregisterPylon(ResourceKey<Level> dimension, BlockPos pos) {
        Map<BlockPos, Double> map = ACTIVE_PYLONS.get(dimension);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static boolean isInsideAcousticDamping(ResourceKey<Level> dimension, BlockPos pos) {
        Map<BlockPos, Double> map = ACTIVE_PYLONS.get(dimension);
        if (map == null || map.isEmpty()) {
            return false;
        }
        for (Map.Entry<BlockPos, Double> entry : map.entrySet()) {
            if (entry.getKey().distSqr(pos) <= entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public static void clearAll() {
        ACTIVE_PYLONS.clear();
    }
}
