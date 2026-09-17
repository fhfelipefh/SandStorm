package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.component.SandstormWeatherComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class SandstormWeatherHandler {
    private static final SandstormWeatherComponent WEATHER = new SandstormWeatherComponent();
    private static long nextSandstormGameTime = 6000;

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.overworld() != null) {
                handleServerTick(server.overworld().getGameTime());
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

    public static void triggerSandstorm(int durationTicks, double intensity) {
        WEATHER.startSandstorm(durationTicks, intensity);
    }

    public static void stopSandstorm() {
        WEATHER.stopSandstorm();
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
