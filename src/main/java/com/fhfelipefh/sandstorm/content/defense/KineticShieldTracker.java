package com.fhfelipefh.sandstorm.content.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KineticShieldTracker {
    private static final Map<ResourceKey<Level>, Map<BlockPos, Double>> ACTIVE_SHIELDS = new ConcurrentHashMap<>();

    public static void registerShield(ResourceKey<Level> dimension, BlockPos pos, double radius) {
        ACTIVE_SHIELDS.computeIfAbsent(dimension, k -> new ConcurrentHashMap<>()).put(pos, radius * radius);
    }

    public static void unregisterShield(ResourceKey<Level> dimension, BlockPos pos) {
        Map<BlockPos, Double> map = ACTIVE_SHIELDS.get(dimension);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static boolean isInsideShield(ResourceKey<Level> dimension, BlockPos pos) {
        Map<BlockPos, Double> map = ACTIVE_SHIELDS.get(dimension);
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
        ACTIVE_SHIELDS.clear();
    }
}
