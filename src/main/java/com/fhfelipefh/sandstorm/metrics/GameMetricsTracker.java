package com.fhfelipefh.sandstorm.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class GameMetricsTracker {
    private final AtomicLong totalEnergyGenerated = new AtomicLong(0);
    private final AtomicLong totalEnergyConsumed = new AtomicLong(0);
    private final AtomicLong totalWaterFilteredMillibuckets = new AtomicLong(0);
    private final AtomicLong totalSandBlocksTerraformed = new AtomicLong(0);
    private final AtomicLong wormAttacksEncountered = new AtomicLong(0);
    private final AtomicLong ancientRuinsDiscovered = new AtomicLong(0);

    public void recordEnergyGenerated(long amount) {
        if (amount > 0) {
            totalEnergyGenerated.addAndGet(amount);
        }
    }

    public void recordEnergyConsumed(long amount) {
        if (amount > 0) {
            totalEnergyConsumed.addAndGet(amount);
        }
    }

    public void recordWaterFiltered(long millibuckets) {
        if (millibuckets > 0) {
            totalWaterFilteredMillibuckets.addAndGet(millibuckets);
        }
    }

    public void recordTerraformedBlock() {
        totalSandBlocksTerraformed.incrementAndGet();
    }

    public void recordWormAttack() {
        wormAttacksEncountered.incrementAndGet();
    }

    public void recordRuinDiscovered() {
        ancientRuinsDiscovered.incrementAndGet();
    }

    public double calculateEnergyEfficiencyRatio() {
        long generated = totalEnergyGenerated.get();
        long consumed = totalEnergyConsumed.get();
        if (generated == 0) {
            return 0.0;
        }
        return (double) consumed / (double) generated;
    }

    public double calculateTerraformingProgress(long targetBlocks) {
        if (targetBlocks <= 0) {
            return 0.0;
        }
        return Math.min(1.0, (double) totalSandBlocksTerraformed.get() / (double) targetBlocks);
    }

    public long getTotalEnergyGenerated() {
        return totalEnergyGenerated.get();
    }

    public long getTotalEnergyConsumed() {
        return totalEnergyConsumed.get();
    }

    public long getTotalWaterFilteredMillibuckets() {
        return totalWaterFilteredMillibuckets.get();
    }

    public long getTotalSandBlocksTerraformed() {
        return totalSandBlocksTerraformed.get();
    }

    public long getWormAttacksEncountered() {
        return wormAttacksEncountered.get();
    }

    private final AtomicLong dronesDispatched = new AtomicLong(0);
    private final AtomicLong sonicShockwavesTriggered = new AtomicLong(0);

    public void recordDroneDispatched() {
        dronesDispatched.incrementAndGet();
    }

    public void recordSonicShockwave() {
        sonicShockwavesTriggered.incrementAndGet();
    }

    public long getDronesDispatched() {
        return dronesDispatched.get();
    }

    public long getSonicShockwavesTriggered() {
        return sonicShockwavesTriggered.get();
    }

    public long getAncientRuinsDiscovered() {
        return ancientRuinsDiscovered.get();
    }

    public void reset() {
        totalEnergyGenerated.set(0);
        totalEnergyConsumed.set(0);
        totalWaterFilteredMillibuckets.set(0);
        totalSandBlocksTerraformed.set(0);
        wormAttacksEncountered.set(0);
        ancientRuinsDiscovered.set(0);
        dronesDispatched.set(0);
        sonicShockwavesTriggered.set(0);
    }
}
