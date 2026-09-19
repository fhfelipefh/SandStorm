package com.fhfelipefh.sandstorm.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SeismicTrackerComponent {
    private final int safeZoneCenterBlockX;
    private final int safeZoneCenterBlockZ;
    private final double safeZoneRadiusBlocks;
    private final double safeZoneRadiusBlocksSq;
    private final Map<Long, Double> chunkVibrationMap;

    public SeismicTrackerComponent(int safeZoneCenterBlockX, int safeZoneCenterBlockZ, double safeZoneRadiusBlocks) {
        this.safeZoneCenterBlockX = safeZoneCenterBlockX;
        this.safeZoneCenterBlockZ = safeZoneCenterBlockZ;
        this.safeZoneRadiusBlocks = Math.max(0.0, safeZoneRadiusBlocks);
        this.safeZoneRadiusBlocksSq = this.safeZoneRadiusBlocks * this.safeZoneRadiusBlocks;
        this.chunkVibrationMap = new HashMap<>();
    }

    public SeismicTrackerComponent() {
        this(0, 0, 96.0);
    }

    public boolean isInsideSafeZone(int blockX, int blockZ) {
        double deltaX = blockX - safeZoneCenterBlockX;
        double deltaZ = blockZ - safeZoneCenterBlockZ;
        return (deltaX * deltaX + deltaZ * deltaZ) <= safeZoneRadiusBlocksSq;
    }

    public void addVibration(int chunkX, int chunkZ, double amount) {
        if (amount <= 0) {
            return;
        }
        long key = packChunkPos(chunkX, chunkZ);
        double current = chunkVibrationMap.getOrDefault(key, 0.0);
        chunkVibrationMap.put(key, current + amount);
    }

    public double getVibration(int chunkX, int chunkZ) {
        return chunkVibrationMap.getOrDefault(packChunkPos(chunkX, chunkZ), 0.0);
    }

    public void decayAll(double decayAmount) {
        if (decayAmount <= 0) {
            return;
        }
        chunkVibrationMap.entrySet().removeIf(entry -> {
            double updated = entry.getValue() - decayAmount;
            if (updated <= 0.001) {
                return true;
            }
            entry.setValue(updated);
            return false;
        });
    }

    public boolean isWormAttackTriggered(int chunkX, int chunkZ, double threshold) {
        int centerBlockX = (chunkX << 4) + 8;
        int centerBlockZ = (chunkZ << 4) + 8;
        if (isInsideSafeZone(centerBlockX, centerBlockZ)) {
            return false;
        }
        return getVibration(chunkX, chunkZ) >= threshold;
    }

    public void clearChunkVibration(int chunkX, int chunkZ) {
        chunkVibrationMap.remove(packChunkPos(chunkX, chunkZ));
    }

    public void reset() {
        chunkVibrationMap.clear();
    }

    public Map<Long, Double> getActiveVibrations() {
        return Collections.unmodifiableMap(chunkVibrationMap);
    }

    public static long packChunkPos(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    public int getSafeZoneCenterBlockX() {
        return safeZoneCenterBlockX;
    }

    public int getSafeZoneCenterBlockZ() {
        return safeZoneCenterBlockZ;
    }

    public double getSafeZoneRadiusBlocks() {
        return safeZoneRadiusBlocks;
    }
}
