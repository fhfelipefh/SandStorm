package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CryogenicChillerManager {
    private static final Map<ResourceKey<Level>, Map<BlockPos, Integer>> ACTIVE_CHILLERS = new ConcurrentHashMap<>();

    public static void registerChiller(ResourceKey<Level> dim, BlockPos pos, int radius) {
        ACTIVE_CHILLERS.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos.immutable(), radius);
    }

    public static void unregisterChiller(ResourceKey<Level> dim, BlockPos pos) {
        Map<BlockPos, Integer> map = ACTIVE_CHILLERS.get(dim);
        if (map != null) {
            map.remove(pos);
        }
    }

    public static void clear() {
        ACTIVE_CHILLERS.clear();
    }

    public static boolean isPointChilled(ResourceKey<Level> dim, BlockPos targetPos) {
        if (dim == null || targetPos == null) {
            return false;
        }
        Map<BlockPos, Integer> chillers = ACTIVE_CHILLERS.get(dim);
        if (chillers == null || chillers.isEmpty()) {
            return false;
        }
        for (Map.Entry<BlockPos, Integer> entry : chillers.entrySet()) {
            BlockPos chillerPos = entry.getKey();
            int radius = entry.getValue();
            long dx = (long) targetPos.getX() - chillerPos.getX();
            long dy = (long) targetPos.getY() - chillerPos.getY();
            long dz = (long) targetPos.getZ() - chillerPos.getZ();
            if (dx * dx + dy * dy + dz * dz <= (long) radius * radius) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPointChilled(Level level, BlockPos targetPos) {
        if (level == null || targetPos == null) {
            return false;
        }
        return isPointChilled(level.dimension(), targetPos);
    }
}
