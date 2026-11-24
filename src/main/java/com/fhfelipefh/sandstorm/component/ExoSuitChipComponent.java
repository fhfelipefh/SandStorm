package com.fhfelipefh.sandstorm.component;

public enum ExoSuitChipComponent {
    SEISMIC_SONAR("seismic_sonar_chip", "item.sandstorm.seismic_sonar_chip", "tooltip.sandstorm.seismic_sonar_chip"),
    JETBOOT_THRUSTER("jetboot_module", "item.sandstorm.jetboot_module", "tooltip.sandstorm.jetboot_module"),
    GRAVITATIONAL_MAGNET("gravitational_magnet_chip", "item.sandstorm.gravitational_magnet_chip", "tooltip.sandstorm.gravitational_magnet_chip"),
    OSMOTIC_RECYCLER("osmotic_recycler_module", "item.sandstorm.osmotic_recycler_module", "tooltip.sandstorm.osmotic_recycler_module"),
    KINETIC_DEFLECTOR("kinetic_deflector_chip", "item.sandstorm.kinetic_deflector_chip", "tooltip.sandstorm.kinetic_deflector_chip"),
    GLIDER_MODULE("suit_glider_module", "item.sandstorm.suit_glider_module", "tooltip.sandstorm.suit_glider_module"),
    EVASIVE_REFLEX_THRUSTER("tactical_dodge_module", "item.sandstorm.tactical_dodge_module", "tooltip.sandstorm.tactical_dodge_module"),
    ASYMPTOTIC_INFINITY_BARRIER("mugen_infinity_module", "item.sandstorm.mugen_infinity_module", "tooltip.sandstorm.mugen_infinity_module");

    private final String id;
    private final String nameKey;
    private final String tooltipKey;

    ExoSuitChipComponent(String id, String nameKey, String tooltipKey) {
        this.id = id;
        this.nameKey = nameKey;
        this.tooltipKey = tooltipKey;
    }

    public String getId() {
        return id;
    }

    public String getNameKey() {
        return nameKey;
    }

    public String getTooltipKey() {
        return tooltipKey;
    }

    public ExoSuitChipComponent getIncompatibleChip() {
        if (this == ASYMPTOTIC_INFINITY_BARRIER) {
            return EVASIVE_REFLEX_THRUSTER;
        }
        if (this == EVASIVE_REFLEX_THRUSTER) {
            return ASYMPTOTIC_INFINITY_BARRIER;
        }
        return null;
    }

    public boolean isIncompatibleWith(ExoSuitChipComponent other) {
        return other != null && getIncompatibleChip() == other;
    }
}

