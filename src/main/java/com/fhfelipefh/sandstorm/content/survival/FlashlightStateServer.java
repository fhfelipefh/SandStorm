package com.fhfelipefh.sandstorm.content.survival;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlashlightStateServer {
    private static final Map<UUID, Integer> FLASHLIGHT_MAP = new ConcurrentHashMap<>();

    public static void setFlashlightMode(UUID playerUuid, int mode) {
        if (mode > 0) {
            FLASHLIGHT_MAP.put(playerUuid, mode);
        } else {
            FLASHLIGHT_MAP.remove(playerUuid);
        }
    }

    public static void setFlashlight(UUID playerUuid, boolean enabled) {
        setFlashlightMode(playerUuid, enabled ? 1 : 0);
    }

    public static int getMode(UUID playerUuid) {
        return FLASHLIGHT_MAP.getOrDefault(playerUuid, 0);
    }

    public static boolean isActive(UUID playerUuid) {
        return getMode(playerUuid) > 0;
    }

    public static void removePlayer(UUID playerUuid) {
        FLASHLIGHT_MAP.remove(playerUuid);
    }
}
