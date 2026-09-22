package com.fhfelipefh.sandstorm.component;

public class SuitPowerComponent {
    private final EnergyStorageComponent energyStorage;
    private final ThermalComponent thermal;
    private final long idleConsumptionPerTick;
    private final long thermalRegulationCostPerTick;
    private final long solarRechargePerTick;
    private int equippedArmorCount;

    public SuitPowerComponent(long capacity, long solarRechargeRate, long idleDrain, long thermalDrain) {
        this.energyStorage = new EnergyStorageComponent(capacity, Math.max(solarRechargeRate, 500L), Math.max(idleDrain, thermalDrain) * 4);
        this.thermal = new ThermalComponent(37.0, 10.0, 50.0);
        this.solarRechargePerTick = Math.max(0, solarRechargeRate);
        this.idleConsumptionPerTick = Math.max(0, idleDrain);
        this.thermalRegulationCostPerTick = Math.max(0, thermalDrain);
        this.equippedArmorCount = 0;
    }

    public SuitPowerComponent() {
        this(100000, 5, 1, 1);
    }

    public void updateEquippedArmorCount(int count) {
        this.equippedArmorCount = Math.clamp(count, 0, 4);
    }

    public boolean isFullSuitEquipped() {
        return equippedArmorCount >= 4;
    }

    public boolean consumeEnergy(long amount) {
        if (amount <= 0) {
            return true;
        }
        if (energyStorage.getStoredEnergy() >= amount) {
            energyStorage.setStoredEnergy(energyStorage.getStoredEnergy() - amount);
            return true;
        }
        return false;
    }

    public long getStoredEnergy() {
        return energyStorage.getStoredEnergy();
    }

    public void setStoredEnergy(long storedEnergy) {
        energyStorage.setStoredEnergy(storedEnergy);
    }

    public void setCapacity(long capacity) {
        energyStorage.setCapacity(capacity);
    }

    public void tick(boolean exposedToSunlight, double ambientTemperature, boolean underground) {
        tick(exposedToSunlight, ambientTemperature, underground, 1.0);
    }

    public void tick(boolean exposedToSunlight, double ambientTemperature, boolean underground, double solarEfficiencyMultiplier) {
        if (exposedToSunlight && !underground) {
            long effectiveSolar = Math.round(solarRechargePerTick * Math.clamp(solarEfficiencyMultiplier, 0.0, 1.0));
            energyStorage.receiveEnergy(effectiveSolar);
        }

        if (equippedArmorCount > 0) {
            energyStorage.extractEnergy(idleConsumptionPerTick);
        }

        double target = ambientTemperature;
        double heatTransferRate;

        if (equippedArmorCount >= 4 && energyStorage.getStoredEnergy() > 0) {
            if (thermalRegulationCostPerTick > 0) {
                energyStorage.extractEnergy(thermalRegulationCostPerTick);
            }
            target = thermal.getOptimalTemperature();
            heatTransferRate = 0.05;
        } else {
            double insulation = calculateInsulationFactor();
            heatTransferRate = (1.0 - Math.clamp(insulation, 0.0, 1.0)) * 0.0075 + 0.004;
        }

        double current = thermal.getCurrentTemperature();
        double diff = target - current;
        if (Math.abs(diff) > 0.001) {
            double step = Math.signum(diff) * Math.min(Math.abs(diff), heatTransferRate);
            thermal.setCurrentTemperature(current + step);
        }
    }

    public double calculateInsulationFactor() {
        return equippedArmorCount * 0.22;
    }

    public boolean hasLifeSupportActive() {
        return isFullSuitEquipped() && energyStorage.getStoredEnergy() > 0 && thermal.isSafe();
    }

    public EnergyStorageComponent getEnergyStorage() {
        return energyStorage;
    }

    public ThermalComponent getThermal() {
        return thermal;
    }

    public int getEquippedArmorCount() {
        return equippedArmorCount;
    }
}
