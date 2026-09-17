package com.fhfelipefh.sandstorm.component;

public class EnergyStorageComponent {
    private final long capacity;
    private final long maxReceiveRate;
    private final long maxExtractRate;
    private long storedEnergy;

    public EnergyStorageComponent(long capacity, long maxReceiveRate, long maxExtractRate) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative");
        }
        this.capacity = capacity;
        this.maxReceiveRate = Math.max(0, maxReceiveRate);
        this.maxExtractRate = Math.max(0, maxExtractRate);
        this.storedEnergy = 0;
    }

    public EnergyStorageComponent(long capacity, long maxTransferRate) {
        this(capacity, maxTransferRate, maxTransferRate);
    }

    public long receiveEnergy(long amount) {
        if (amount <= 0) {
            return 0;
        }
        long accepted = Math.min(amount, maxReceiveRate);
        long space = capacity - storedEnergy;
        long inserted = Math.min(accepted, space);
        this.storedEnergy += inserted;
        return inserted;
    }

    public long extractEnergy(long amount) {
        if (amount <= 0) {
            return 0;
        }
        long requested = Math.min(amount, maxExtractRate);
        long extracted = Math.min(requested, storedEnergy);
        this.storedEnergy -= extracted;
        return extracted;
    }

    public long transferTo(EnergyStorageComponent target, long maxTransfer) {
        if (target == null || maxTransfer <= 0) {
            return 0;
        }
        long potentialExtract = Math.min(maxTransfer, Math.min(maxExtractRate, storedEnergy));
        long accepted = target.receiveEnergy(potentialExtract);
        this.storedEnergy -= accepted;
        return accepted;
    }

    public long getStoredEnergy() {
        return storedEnergy;
    }

    public long getCapacity() {
        return capacity;
    }

    public double getEnergyRatio() {
        if (capacity == 0) {
            return 0.0;
        }
        return (double) storedEnergy / (double) capacity;
    }

    public boolean hasEnergy(long amount) {
        return storedEnergy >= amount;
    }

    public boolean isFull() {
        return storedEnergy >= capacity;
    }

    public boolean isEmpty() {
        return storedEnergy <= 0;
    }

    public void setStoredEnergy(long amount) {
        this.storedEnergy = Math.clamp(amount, 0, capacity);
    }
}
