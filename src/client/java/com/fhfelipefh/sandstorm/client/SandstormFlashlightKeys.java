package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public final class SandstormFlashlightKeys {
    private SandstormFlashlightKeys() {}

    private static final KeyMapping.Category SANDSTORM_CATEGORY =
            KeyMapping.Category.register(SandStormMod.id("sandstorm_category"));

    public static KeyMapping FLASHLIGHT_KEY;

    public static void initialize() {
        FLASHLIGHT_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.sandstorm.flashlight",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_F,
                SANDSTORM_CATEGORY
        ));
    }
}
