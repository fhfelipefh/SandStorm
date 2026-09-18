package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SandstormWeatherHandler {
    private static final SandstormWeatherComponent WEATHER = new SandstormWeatherComponent();
    private static long nextSandstormGameTime = 6000;

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.overworld() != null) {
                handleServerTick(server.overworld(), server.overworld().getGameTime());
            }
        });
    }

    public static void handleServerTick(long gameTime) {
        WEATHER.tick();

        if (!WEATHER.isActive() && gameTime >= nextSandstormGameTime) {
            WEATHER.startSandstorm(4000, 0.85);
            nextSandstormGameTime = gameTime + 14000;
        }
    }

    public static void handleServerTick(ServerLevel level, long gameTime) {
        handleServerTick(gameTime);

        if (level != null && WEATHER.isActive() && gameTime % 80 == 0) {
            playWeatherWindSound(level);
        }
    }

    public static SoundEvent getWindSoundForIntensity(double intensity, float randomRoll) {
        if (randomRoll < 0.20f) {
            return SandStormSoundEvents.WEATHER_SANDSTORM_WIND_HOWL;
        }
        if (intensity < 0.35) {
            return SandStormSoundEvents.WEATHER_SANDSTORM_WIND_LIGHT;
        }
        if (intensity < 0.70) {
            return SandStormSoundEvents.WEATHER_SANDSTORM_WIND_MEDIUM;
        }
        return SandStormSoundEvents.WEATHER_SANDSTORM_WIND_HEAVY;
    }

    private static void playWeatherWindSound(ServerLevel level) {
        double intensity = WEATHER.getIntensity();
        float roll = level.getRandom().nextFloat();
        SoundEvent selectedSound = getWindSoundForIntensity(intensity, roll);
        float volume = (float) Math.clamp(0.4 + intensity * 0.6, 0.3, 1.0);
        float pitch = (float) (0.9 + (level.getRandom().nextFloat() * 0.2));

        for (ServerPlayer player : level.players()) {
            if (player.level().canSeeSky(player.blockPosition())) {
                level.playSound(null, player.blockPosition(), selectedSound, SoundSource.WEATHER, volume, pitch);
            }
        }
    }

    public static void triggerSandstorm(int durationTicks, double intensity) {
        WEATHER.startSandstorm(durationTicks, intensity);
    }

    public static void stopSandstorm() {
        WEATHER.stopSandstorm();
    }

    public static void resetWeather() {
        WEATHER.reset();
    }

    public static SandstormWeatherComponent getWeather() {
        return WEATHER;
    }

    public static void setNextSandstormGameTime(long time) {
        nextSandstormGameTime = time;
    }

    public static long getNextSandstormGameTime() {
        return nextSandstormGameTime;
    }
}
