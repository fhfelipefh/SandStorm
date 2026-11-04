package com.fhfelipefh.sandstorm.client.weather;

import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.fhfelipefh.sandstorm.content.world.ShelterDetectionHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public final class ClientStormTransitionHandler {
    private static float currentExposure = 0.0f;

    private ClientStormTransitionHandler() {
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientStormTransitionHandler::tick);
    }

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.level.dimension() != Level.OVERWORLD) {
            currentExposure = Mth.lerp(0.1f, currentExposure, 0.0f);
            return;
        }

        if (!SandstormWeatherHandler.getWeather().isActive()) {
            currentExposure = Mth.lerp(0.1f, currentExposure, 0.0f);
            return;
        }

        BlockPos playerPos = client.player.blockPosition();
        boolean sheltered = ShelterDetectionHelper.isSheltered(client.level, playerPos)
                || ShelterDetectionHelper.isSheltered(client.level, playerPos.above());

        float target = sheltered ? 0.0f : 1.0f;
        currentExposure = Mth.clamp(Mth.lerp(0.08f, currentExposure, target), 0.0f, 1.0f);
        if (Math.abs(currentExposure - target) < 0.005f) {
            currentExposure = target;
        }
    }

    public static float getExposure() {
        return currentExposure;
    }

    public static void setExposure(float exposure) {
        currentExposure = Mth.clamp(exposure, 0.0f, 1.0f);
    }
}

