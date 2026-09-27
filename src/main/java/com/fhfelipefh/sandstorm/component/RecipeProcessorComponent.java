package com.fhfelipefh.sandstorm.component;

public class RecipeProcessorComponent {
    private int totalOperationTicks;
    private int currentProgressTicks;
    private long energyCostPerTick;
    private boolean processing;

    public RecipeProcessorComponent(int defaultOperationTicks, long energyCostPerTick) {
        this.totalOperationTicks = Math.max(1, defaultOperationTicks);
        this.energyCostPerTick = Math.max(0, energyCostPerTick);
        this.currentProgressTicks = 0;
        this.processing = false;
    }

    public RecipeProcessorComponent() {
        this(100, 10);
    }

    public void startOperation(int totalTicks, long energyPerTick) {
        this.totalOperationTicks = Math.max(1, totalTicks);
        this.energyCostPerTick = Math.max(0, energyPerTick);
        this.currentProgressTicks = 0;
        this.processing = true;
    }

    public boolean canProcess(EnergyStorageComponent energyStorage) {
        return processing && energyStorage != null && energyStorage.hasEnergy(energyCostPerTick);
    }

    public void tick(EnergyStorageComponent energyStorage) {
        if (!canProcess(energyStorage)) {
            return;
        }

        energyStorage.extractEnergy(energyCostPerTick);
        currentProgressTicks++;
        if (currentProgressTicks >= totalOperationTicks) {
            processing = false;
        }
    }

    public boolean isComplete() {
        return !processing && currentProgressTicks >= totalOperationTicks;
    }

    public double getProgressPercentage() {
        if (totalOperationTicks <= 0) {
            return 0.0;
        }
        return Math.clamp((double) currentProgressTicks / totalOperationTicks * 100.0, 0.0, 100.0);
    }

    public void reset() {
        this.currentProgressTicks = 0;
        this.processing = false;
    }

    public boolean isProcessing() {
        return processing;
    }

    public int getCurrentProgressTicks() {
        return currentProgressTicks;
    }

    public int getTotalOperationTicks() {
        return totalOperationTicks;
    }

    public long getEnergyCostPerTick() {
        return energyCostPerTick;
    }
}
