package com.fhfelipefh.sandstorm.content.world.biosphere;

import net.minecraft.network.chat.Component;

public enum BiosphereType {
    TEMPERATE_PLAINS("plains", "minecraft:plains", "biosphere.sandstorm.plains"),
    TEMPERATE_FOREST("forest", "minecraft:forest", "biosphere.sandstorm.forest"),
    TROPICAL_JUNGLE("jungle", "minecraft:jungle", "biosphere.sandstorm.jungle"),
    CRYO_TUNDRA("cryo", "minecraft:snowy_plains", "biosphere.sandstorm.cryo"),
    XENO_FUNGAL("fungal", "sandstorm:xeno_fungal", "biosphere.sandstorm.fungal"),
    MAGNETIC_FOREST("magnetic", "sandstorm:magnetic_forest", "biosphere.sandstorm.magnetic"),
    PRIMORDIAL_OASIS("oasis", "sandstorm:primordial_oasis", "biosphere.sandstorm.oasis");

    private final String id;
    private final String targetBiomeId;
    private final String translationKey;

    BiosphereType(String id, String targetBiomeId, String translationKey) {
        this.id = id;
        this.targetBiomeId = targetBiomeId;
        this.translationKey = translationKey;
    }

    public String getId() {
        return id;
    }

    public String getTargetBiomeId() {
        return targetBiomeId;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public static BiosphereType fromId(String id) {
        for (BiosphereType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return TEMPERATE_PLAINS;
    }
}
