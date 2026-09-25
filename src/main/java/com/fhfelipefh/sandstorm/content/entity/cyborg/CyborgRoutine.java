package com.fhfelipefh.sandstorm.content.entity.cyborg;

public enum CyborgRoutine {
    AUTONOMOUS_WORK,
    FOLLOW_OPERATOR,
    PATROL_PERIMETER,
    RETURN_TO_DOCK;

    public static CyborgRoutine fromOrdinal(int ordinal) {
        CyborgRoutine[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return AUTONOMOUS_WORK;
        }
        return values[ordinal];
    }
}
