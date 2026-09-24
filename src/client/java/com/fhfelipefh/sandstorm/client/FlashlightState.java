package com.fhfelipefh.sandstorm.client;

public final class FlashlightState {
    public static final int MODE_OFF = 0;
    public static final int MODE_LOW = 1;
    public static final int MODE_MEDIUM = 2;
    public static final int MODE_HIGH = 3;

    private FlashlightState() {}

    private static int mode = MODE_OFF;
    private static boolean helmetEquipped = false;

    public static boolean isActive() {
        return mode > MODE_OFF && helmetEquipped;
    }

    public static int getMode() {
        return helmetEquipped ? mode : MODE_OFF;
    }

    public static void setMode(int newMode) {
        if (newMode >= MODE_OFF && newMode <= MODE_HIGH) {
            mode = newMode;
        } else {
            mode = MODE_OFF;
        }
    }

    public static int cycleMode() {
        if (!helmetEquipped) {
            mode = MODE_OFF;
            return MODE_OFF;
        }
        mode = (mode + 1) % 4;
        return mode;
    }

    public static boolean isFlashlightOn() {
        return mode > MODE_OFF;
    }

    public static void setFlashlightOn(boolean on) {
        mode = on ? MODE_LOW : MODE_OFF;
    }

    public static boolean isHelmetEquipped() {
        return helmetEquipped;
    }

    public static void setHelmetEquipped(boolean equipped) {
        helmetEquipped = equipped;
        if (!equipped) {
            mode = MODE_OFF;
        }
    }

    public static float getLightFactor() {
        if (!isActive()) {
            return 0.0f;
        }
        return switch (mode) {
            case MODE_LOW -> 0.40f;
            case MODE_MEDIUM -> 0.70f;
            case MODE_HIGH -> 1.0f;
            default -> 0.0f;
        };
    }
}
