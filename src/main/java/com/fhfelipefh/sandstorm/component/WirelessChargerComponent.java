package com.fhfelipefh.sandstorm.component;

public class WirelessChargerComponent {
    private final int tier;
    private final double baseRadius;
    private final long baseTransferRate;

    public WirelessChargerComponent(int tier) {
        this.tier = Math.max(1, tier);
        if (this.tier == 1) {
            this.baseRadius = 24.0;
            this.baseTransferRate = 50L;
        } else {
            this.baseRadius = 36.0;
            this.baseTransferRate = 100L;
        }
    }

    public WirelessChargerComponent() {
        this(1);
    }

    public int getTier() {
        return tier;
    }

    public double getBaseRadius() {
        return baseRadius;
    }

    public long getBaseTransferRate() {
        return baseTransferRate;
    }

    public boolean canHarvestSunlight(boolean canSeeSky, boolean isDay) {
        return canSeeSky && isDay;
    }

    public double calculateSunFactor(boolean canSeeSky, boolean isDay, int skyDarken, double weatherMultiplier) {
        if (!canHarvestSunlight(canSeeSky, isDay)) {
            return 0.0;
        }
        double peakFactor = Math.clamp(1.0 - ((double) Math.max(0, skyDarken) / 15.0), 0.2, 1.0);
        double weather = Math.clamp(weatherMultiplier, 0.0, 1.0);
        return peakFactor * weather;
    }

    public double calculateEffectiveRadius(boolean canSeeSky, boolean isDay, int skyDarken, double weatherMultiplier) {
        double sunFactor = calculateSunFactor(canSeeSky, isDay, skyDarken, weatherMultiplier);
        if (sunFactor <= 0.0) {
            return 0.0;
        }
        return baseRadius * (0.6 + 0.4 * sunFactor);
    }

    public long calculateTransferRate(boolean canSeeSky, boolean isDay, int skyDarken, double weatherMultiplier) {
        double sunFactor = calculateSunFactor(canSeeSky, isDay, skyDarken, weatherMultiplier);
        return Math.round(baseTransferRate * sunFactor);
    }

    public long chargeSuit(SuitPowerComponent suit, long amount) {
        if (suit == null || amount <= 0) {
            return 0;
        }
        return suit.getEnergyStorage().receiveEnergy(amount);
    }
}
