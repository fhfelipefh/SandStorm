package com.fhfelipefh.sandstorm.client.handler;

import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class EmpDeafenClientHandler {
    private static int deafenTicks = 0;

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(EmpDeafenClientHandler::handleClientTick);
    }

    public static void trigger(int ticks) {
        deafenTicks = Math.max(deafenTicks, ticks);
        SurvivalHudOverlay.triggerMagneticInterference(ticks);
        Minecraft client = Minecraft.getInstance();
        if (client.getSoundManager() != null) {
            silenceWorldSounds(client);
            client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_DEACTIVATE, 2.0f));
        }
    }

    private static void handleClientTick(Minecraft client) {
        if (deafenTicks <= 0) {
            return;
        }
        deafenTicks--;
        if (client.player != null) {
            client.player.setSprinting(false);
        }
        if (client.getSoundManager() != null) {
            silenceWorldSounds(client);
            if (deafenTicks % 25 == 0 && deafenTicks > 10) {
                client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_AMBIENT, 2.0f));
            }
        }
    }

    private static void silenceWorldSounds(Minecraft client) {
        client.getSoundManager().stop(null, SoundSource.HOSTILE);
        client.getSoundManager().stop(null, SoundSource.NEUTRAL);
        client.getSoundManager().stop(null, SoundSource.BLOCKS);
        client.getSoundManager().stop(null, SoundSource.AMBIENT);
        client.getSoundManager().stop(null, SoundSource.WEATHER);
        client.getSoundManager().stop(null, SoundSource.PLAYERS);
    }

    public static int getDeafenTicks() {
        return deafenTicks;
    }
}
