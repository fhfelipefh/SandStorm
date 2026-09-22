package com.fhfelipefh.sandstorm.client;

public final class FlashlightState {
    private FlashlightState() {}

    private static boolean flashlightOn = false;
    private static boolean helmetEquipped = false;

    public static boolean isActive() {
        return flashlightOn && helmetEquipped;
    }

    public static boolean isFlashlightOn() {
        return flashlightOn;
    }

    public static void setFlashlightOn(boolean on) {
        flashlightOn = on;
    }

    public static boolean isHelmetEquipped() {
        return helmetEquipped;
    }

    public static void setHelmetEquipped(boolean equipped) {
        helmetEquipped = equipped;
        if (!equipped) {
            flashlightOn = false;
        }
    }
}
