package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.item.SurvivalDatapadItem;
import net.minecraft.client.Minecraft;

public class DatapadClientHelper {
    public static void initialize() {
        SurvivalDatapadItem.setClientScreenOpener(() -> {
            Minecraft client = Minecraft.getInstance();
            if (client != null) {
                client.setScreenAndShow(new SurvivalDatapadScreen());
            }
        });
    }
}
