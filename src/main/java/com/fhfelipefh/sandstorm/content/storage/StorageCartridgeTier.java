package com.fhfelipefh.sandstorm.content.storage;

public enum StorageCartridgeTier {
    TIER_1K(1000, 64, "1k"),
    TIER_4K(4000, 128, "4k"),
    TIER_16K(16000, 256, "16k"),
    TIER_64K(64000, 512, "64k"),
    DIMENSIONAL(256000, 1024, "dimensional");

    private final int capacity;
    private final int maxTypes;
    private final String id;

    StorageCartridgeTier(int capacity, int maxTypes, String id) {
        this.capacity = capacity;
        this.maxTypes = maxTypes;
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxTypes() {
        return maxTypes;
    }

    public String getId() {
        return id;
    }
}
