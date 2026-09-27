package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public final class SandStormWorldPresets {

    public static final ResourceKey<WorldPreset> DESERT_PLANET =
            ResourceKey.create(Registries.WORLD_PRESET, SandStormMod.id("desert_planet"));

    private SandStormWorldPresets() {
    }
}
