package com.fhfelipefh.sandstorm.content.satellite;

public enum SatelliteType {
    SURVEY("survey"),
    WEATHER_RECON("weather_recon"),
    SOLAR_REFLECTOR("solar_reflector"),
    SAR_GEOLOGICAL("sar_geological"),
    KINETIC_LANCE("kinetic_lance");

    private final String id;

    SatelliteType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static SatelliteType fromOrdinal(int ordinal) {
        SatelliteType[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return SURVEY;
        }
        return values[ordinal];
    }
}
