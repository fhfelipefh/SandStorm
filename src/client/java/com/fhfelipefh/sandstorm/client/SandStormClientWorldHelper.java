package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.content.world.SandStormWorldHelper;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import com.fhfelipefh.sandstorm.content.world.ShowcaseAutomation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.server.MinecraftServer;

import java.util.Locale;

public final class SandStormClientWorldHelper {

    private static boolean connectedToSandStorm = false;
    private static Boolean forcedClientSandStormForTesting = null;

    private SandStormClientWorldHelper() {
    }

    public static void setConnectedToSandStorm(boolean connected) {
        connectedToSandStorm = connected;
    }

    public static boolean isConnectedToSandStorm() {
        return connectedToSandStorm;
    }

    public static void setForcedClientSandStormForTesting(Boolean forced) {
        forcedClientSandStormForTesting = forced;
    }

    public static boolean isSandStormWorld() {
        if (forcedClientSandStormForTesting != null) {
            return forcedClientSandStormForTesting;
        }
        if (connectedToSandStorm) {
            return true;
        }
        if (ShowcaseAutomation.isShowcaseActive()) {
            return true;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return false;
        }
        MinecraftServer server = mc.getSingleplayerServer();
        if (server != null) {
            if (SandStormWorldHelper.isSandStormWorld(server)) {
                return true;
            }
            if (server.getWorldData() != null && server.getWorldData().getLevelName() != null) {
                String lower = server.getWorldData().getLevelName().toLowerCase(Locale.ROOT);
                if (lower.contains("sandstorm") || lower.contains("showcase")) {
                    return true;
                }
            }
        }
        if (mc.level != null && SandStormWorldHelper.isSandStormWorld(mc.level)) {
            return true;
        }
        if (SurvivalHudOverlay.getClientSuit().getEquippedArmorCount() > 0
                || SurvivalHudOverlay.getClientSuit().getEnergyStorage().getCapacity() > 0) {
            return true;
        }
        return SandstormWeatherHandler.getWeather().isActive();
    }

    public static boolean isVanillaNotification(Toast toast) {
        return toast instanceof TutorialToast || toast instanceof RecipeToast || toast instanceof AdvancementToast;
    }
}

