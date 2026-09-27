package com.fhfelipefh.sandstorm.content.entity.cyborg;

public enum CyborgSpecialty {
    EXCAVATOR,
    BUILDER,
    HARVESTER;

    public static CyborgSpecialty fromOrdinal(int ordinal) {
        CyborgSpecialty[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return EXCAVATOR;
        }
        return values[ordinal];
    }
}
