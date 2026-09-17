package com.fhfelipefh.sandstorm.component;

public class SandstormWeatherComponent {
    private boolean active;
    private double intensity;
    private double targetIntensity;
    private int remainingTicks;

    public SandstormWeatherComponent() {
        this.active = false;
        this.intensity = 0.0;
        this.targetIntensity = 0.0;
        this.remainingTicks = 0;
    }

    public void startSandstorm(int durationTicks, double targetIntensity) {
        this.active = true;
        this.remainingTicks = Math.max(0, durationTicks);
        this.targetIntensity = Math.clamp(targetIntensity, 0.0, 1.0);
    }

    public void stopSandstorm() {
        this.active = false;
        this.targetIntensity = 0.0;
        this.remainingTicks = 0;
    }

    public void tick() {
        if (active) {
            if (remainingTicks > 0) {
                remainingTicks--;
            } else {
                active = false;
                targetIntensity = 0.0;
            }
        }

        if (intensity < targetIntensity) {
            intensity = Math.min(targetIntensity, intensity + 0.02);
        } else if (intensity > targetIntensity) {
            intensity = Math.max(targetIntensity, intensity - 0.02);
        }
    }

    public boolean isActive() {
        return active || intensity > 0.001;
    }

    public boolean isAtPeak() {
        return active && Math.abs(intensity - targetIntensity) < 0.01 && targetIntensity > 0.5;
    }

    public double getIntensity() {
        return intensity;
    }

    public double getTargetIntensity() {
        return targetIntensity;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public double getSolarEfficiencyMultiplier() {
        return Math.clamp(1.0 - (intensity * 0.85), 0.15, 1.0);
    }

    public double getFogDistanceMultiplier() {
        return Math.clamp(1.0 - (intensity * 0.80), 0.20, 1.0);
    }

    public double getVibrationDampingFactor() {
        return Math.clamp(1.0 - (intensity * 0.50), 0.50, 1.0);
    }

    public boolean canCauseSandDamage() {
        return intensity >= 0.75;
    }
}
