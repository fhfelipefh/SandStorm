package com.fhfelipefh.sandstorm.content.entity;

public enum MegazordVariant {
    STANDARD("standard", 0),
    AERO_STRIKER("aero_striker", 1),
    ABYSSAL_SUB("abyssal_sub", 2),
    APEX_DOMINATOR("apex_dominator", 3);

    private final String id;
    private final int index;

    MegazordVariant(String id, int index) {
        this.id = id;
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public static MegazordVariant fromIndex(int index) {
        for (MegazordVariant variant : values()) {
            if (variant.index == index) {
                return variant;
            }
        }
        return STANDARD;
    }

    public static MegazordVariant resolve(boolean flight, boolean submersible) {
        if (flight && submersible) {
            return APEX_DOMINATOR;
        }
        if (flight) {
            return AERO_STRIKER;
        }
        if (submersible) {
            return ABYSSAL_SUB;
        }
        return STANDARD;
    }
}
