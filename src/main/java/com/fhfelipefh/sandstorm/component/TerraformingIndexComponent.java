package com.fhfelipefh.sandstorm.component;

public class TerraformingIndexComponent {
    public enum Stage {
        ARID_DESERT,
        CONDENSATION_INITIATED,
        PRECIPITATION_ACTIVE,
        LUSH_BIOSPHERE
    }

    private double progress;

    public TerraformingIndexComponent() {
        this.progress = 0.0;
    }

    public TerraformingIndexComponent(double initialProgress) {
        this.progress = Math.clamp(initialProgress, 0.0, 100.0);
    }

    public double getProgress() {
        return progress;
    }

    public void addProgress(double amount) {
        if (amount > 0.0) {
            this.progress = Math.clamp(this.progress + amount, 0.0, 100.0);
        }
    }

    public void setProgress(double progress) {
        this.progress = Math.clamp(progress, 0.0, 100.0);
    }

    public Stage getStage() {
        if (progress < 25.0) {
            return Stage.ARID_DESERT;
        }
        if (progress < 50.0) {
            return Stage.CONDENSATION_INITIATED;
        }
        if (progress < 75.0) {
            return Stage.PRECIPITATION_ACTIVE;
        }
        return Stage.LUSH_BIOSPHERE;
    }

    public int getDomeRadius() {
        if (progress < 25.0) {
            return 6;
        }
        if (progress < 50.0) {
            return 10;
        }
        if (progress < 75.0) {
            return 14;
        }
        return 18;
    }

    public double getHumidity() {
        return 5.0 + (progress / 100.0) * 80.0;
    }

    public double getTemperatureCelsius() {
        return 48.0 - (progress / 100.0) * 26.0;
    }

    public boolean isWormDispersalActive() {
        return progress >= 50.0;
    }

    public boolean isComplete() {
        return progress >= 100.0;
    }
}
