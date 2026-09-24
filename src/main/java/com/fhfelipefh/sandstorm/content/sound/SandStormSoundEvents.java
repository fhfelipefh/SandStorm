package com.fhfelipefh.sandstorm.content.sound;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SandStormSoundEvents {
    public static final SoundEvent SONIC_CANNON_BLAST = register("item.sonic_cannon.blast");
    public static final SoundEvent PLASMA_RIFLE_FIRE = register("item.plasma_rifle.fire");
    public static final SoundEvent VIBRO_CRYSKNIFE_SWING = register("item.vibro_crysknife.swing");
    public static final SoundEvent VIBRO_CRYSKNIFE_HIT = register("item.vibro_crysknife.hit");
    public static final SoundEvent MEGAZORD_SHOCKWAVE = register("entity.megazord.shockwave");
    public static final SoundEvent MEGAZORD_STEP = register("entity.megazord.step");
    public static final SoundEvent SANDWORM_RUMBLE = register("entity.sandworm.rumble");
    public static final SoundEvent SANDWORM_EMERGE = register("entity.sandworm.emerge");
    public static final SoundEvent SANDWORM_ATTACK = register("entity.sandworm.attack");
    public static final SoundEvent THUMPER_THUMP = register("block.thumper.thump");
    public static final SoundEvent ANOMALY_RADAR_PING = register("item.anomaly_radar.ping");
    public static final SoundEvent ATMOSPHERIC_ANALYZER_SCAN = register("item.atmospheric_analyzer.scan");
    public static final SoundEvent PRINTER_3D_CRAFT = register("block.printer_3d.craft");
    public static final SoundEvent DESALINATION_PROCESS = register("block.desalination_filter.process");
    public static final SoundEvent NANITE_ACTIVATE = register("block.nanite_fabricator.activate");
    public static final SoundEvent TERRAFORMER_HUM = register("block.atmospheric_terraformer.hum");
    public static final SoundEvent ASSEMBLY_CONSTRUCT = register("block.assembly_bay.construct");
    public static final SoundEvent WPT_RELAY_HUM = register("block.wpt_relay.hum");
    public static final SoundEvent FLUID_PIPE_FLOW = register("block.smart_fluid_pipe.flow");
    public static final SoundEvent CARGO_DRONE_FLIGHT = register("entity.cargo_drone.flight");
    public static final SoundEvent EXCAVATOR_ENGINE = register("entity.excavator.engine");
    public static final SoundEvent SUIT_BATTERY_LOW = register("suit.battery.low");
    public static final SoundEvent SUIT_SOLAR_CHARGE = register("suit.solar.charge");
    public static final SoundEvent WEATHER_SANDSTORM_WIND = register("weather.sandstorm.wind");
    public static final SoundEvent WEATHER_SANDSTORM_WIND_LIGHT = register("weather.sandstorm.wind.light");
    public static final SoundEvent WEATHER_SANDSTORM_WIND_MEDIUM = register("weather.sandstorm.wind.medium");
    public static final SoundEvent WEATHER_SANDSTORM_WIND_HEAVY = register("weather.sandstorm.wind.heavy");
    public static final SoundEvent WEATHER_SANDSTORM_WIND_HOWL = register("weather.sandstorm.wind.howl");
    public static final SoundEvent MEGASTRUCTURE_CONSTRUCTOR_LASER = register("block.megastructure_constructor.laser");
    public static final SoundEvent MEGASTRUCTURE_LAYER_COMPLETE = register("block.megastructure_constructor.layer_complete");
    public static final SoundEvent MEGASTRUCTURE_COMPLETE = register("block.megastructure_constructor.complete");

    private static SoundEvent register(String path) {
        Identifier id = SandStormMod.id(path);
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(id);
        try {
            return Registry.register(BuiltInRegistries.SOUND_EVENT, id, soundEvent);
        } catch (IllegalStateException ignored) {
            return soundEvent;
        }
    }

    public static void initialize() {
    }
}
