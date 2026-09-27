package com.fhfelipefh.sandstorm.content.entity.ai;

public enum SandwormState {
    BURROWED,
    BREACHING,
    SURFACED_ASSAULT,
    SUBMERGING;

    public boolean isSubterranean() {
        return this == BURROWED || this == SUBMERGING;
    }

    public boolean isSurfaced() {
        return this == BREACHING || this == SURFACED_ASSAULT;
    }

    public static SandwormState fromOrdinal(int ordinal) {
        SandwormState[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return BURROWED;
        }
        return values[ordinal];
    }
}
