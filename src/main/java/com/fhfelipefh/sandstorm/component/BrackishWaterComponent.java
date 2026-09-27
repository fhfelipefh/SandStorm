package com.fhfelipefh.sandstorm.component;

public class BrackishWaterComponent {
    public record FilterResult(int potableWaterOutputMb, double extractedSaltGrams, double residualSalinity) {}

    private final double salinityPercentage;
    private final double mineralSedimentLevel;
    private final int volumeMillibuckets;

    public BrackishWaterComponent(double salinityPercentage, double mineralSedimentLevel, int volumeMillibuckets) {
        this.salinityPercentage = Math.clamp(salinityPercentage, 0.0, 100.0);
        this.mineralSedimentLevel = Math.clamp(mineralSedimentLevel, 0.0, 1.0);
        this.volumeMillibuckets = Math.max(0, volumeMillibuckets);
    }

    public BrackishWaterComponent() {
        this(8.5, 0.45, 1000);
    }

    public boolean isSafeForDirectConsumption() {
        return salinityPercentage <= 0.5 && mineralSedimentLevel <= 0.05;
    }

    public int calculateToxicityDurationTicks() {
        if (isSafeForDirectConsumption()) {
            return 0;
        }
        return (int) Math.round((salinityPercentage * 30.0) + (mineralSedimentLevel * 200.0));
    }

    public FilterResult filter(double filterEfficiency) {
        double clampedEfficiency = Math.clamp(filterEfficiency, 0.0, 1.0);
        int waterOutput = (int) Math.round(volumeMillibuckets * clampedEfficiency * 0.95);
        double saltExtracted = (volumeMillibuckets / 1000.0) * salinityPercentage * 10.0 * clampedEfficiency;
        double residualSalinity = Math.max(0.0, salinityPercentage * (1.0 - clampedEfficiency));
        return new FilterResult(waterOutput, saltExtracted, residualSalinity);
    }

    public double getSalinityPercentage() {
        return salinityPercentage;
    }

    public double getMineralSedimentLevel() {
        return mineralSedimentLevel;
    }

    public int getVolumeMillibuckets() {
        return volumeMillibuckets;
    }
}
