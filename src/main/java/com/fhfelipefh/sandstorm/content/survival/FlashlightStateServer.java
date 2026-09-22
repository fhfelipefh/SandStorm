package com.fhfelipefh.sandstorm.content.survival;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlashlightStateServer {
    private static final Map<UUID, Boolean> FLASHLIGHT_MAP = new ConcurrentHashMap<>();

    public static void setFlashlight(UUID playerUuid, boolean enabled) {
        if (enabled) {
            FLASHLIGHT_MAP.put(playerUuid, true);
        } else {
            FLASHLIGHT_MAP.remove(playerUuid);
        }
    }

    public static boolean isActive(UUID playerUuid) {
        return FLASHLIGHT_MAP.getOrDefault(playerUuid, false);
    }

    public static void removePlayer(UUID playerUuid) {
        FLASHLIGHT_MAP.remove(playerUuid);
    }
}
