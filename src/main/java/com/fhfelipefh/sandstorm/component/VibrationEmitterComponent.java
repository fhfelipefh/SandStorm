package com.fhfelipefh.sandstorm.component;

public class VibrationEmitterComponent {
    private final double baseFrequency;
    private final double maxIntensity;
    private double currentIntensity;

    public VibrationEmitterComponent(double baseFrequency, double maxIntensity) {
        if (baseFrequency < 0 || maxIntensity < 0) {
            throw new IllegalArgumentException("Frequency and intensity must be non-negative");
        }
        this.baseFrequency = baseFrequency;
        this.maxIntensity = maxIntensity;
        this.currentIntensity = 0.0;
    }

    public void emitVibration(double amount) {
        if (amount <= 0) {
            return;
        }
        this.currentIntensity = Math.min(maxIntensity, currentIntensity + amount);
    }

    public void decay(double decayRate) {
        double rate = Math.max(0.0, decayRate);
        this.currentIntensity = Math.max(0.0, currentIntensity - rate);
    }

    public double calculateEffectiveVibration(double dampeningFactor) {
        double clampedDampening = Math.clamp(dampeningFactor, 0.0, 1.0);
        return currentIntensity * (1.0 - clampedDampening);
    }

    public boolean exceedsThreshold(double threshold, double dampeningFactor) {
        return calculateEffectiveVibration(dampeningFactor) >= threshold;
    }

    public double getBaseFrequency() {
        return baseFrequency;
    }

    public double getMaxIntensity() {
        return maxIntensity;
    }

    public double getCurrentIntensity() {
        return currentIntensity;
    }

    public void reset() {
        this.currentIntensity = 0.0;
    }
}
