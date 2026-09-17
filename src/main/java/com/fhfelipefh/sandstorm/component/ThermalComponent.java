package com.fhfelipefh.sandstorm.component;

public class ThermalComponent {
    private final double optimalTemperature;
    private final double maxToleratedTemperature;
    private final double minToleratedTemperature;
    private double currentTemperature;

    public ThermalComponent(double optimalTemperature, double minToleratedTemperature, double maxToleratedTemperature) {
        if (minToleratedTemperature > optimalTemperature || optimalTemperature > maxToleratedTemperature) {
            throw new IllegalArgumentException("Temperature thresholds must satisfy: min <= optimal <= max");
        }
        this.optimalTemperature = optimalTemperature;
        this.minToleratedTemperature = minToleratedTemperature;
        this.maxToleratedTemperature = maxToleratedTemperature;
        this.currentTemperature = optimalTemperature;
    }

    public void adjustTemperature(double ambientTemperature, double insulationFactor) {
        double clampedInsulation = Math.clamp(insulationFactor, 0.0, 1.0);
        double delta = (ambientTemperature - currentTemperature) * (1.0 - clampedInsulation);
        this.currentTemperature += delta;
    }

    public double regulateTowardOptimal(double regulationPower) {
        double clampedPower = Math.max(0.0, regulationPower);
        double delta = optimalTemperature - currentTemperature;
        if (Math.abs(delta) <= clampedPower) {
            double applied = Math.abs(delta);
            this.currentTemperature = optimalTemperature;
            return applied;
        }
        double sign = Math.signum(delta);
        this.currentTemperature += sign * clampedPower;
        return clampedPower;
    }

    public boolean isSafe() {
        return currentTemperature >= minToleratedTemperature && currentTemperature <= maxToleratedTemperature;
    }

    public boolean isOverheating() {
        return currentTemperature > maxToleratedTemperature;
    }

    public boolean isFreezing() {
        return currentTemperature < minToleratedTemperature;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public double getOptimalTemperature() {
        return optimalTemperature;
    }

    public void setCurrentTemperature(double temperature) {
        this.currentTemperature = temperature;
    }
}
