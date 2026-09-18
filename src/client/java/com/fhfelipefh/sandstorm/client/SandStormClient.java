package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SandStormClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SurvivalHudOverlay.initialize();
        DatapadClientHelper.initialize();
        ClientPlayNetworking.registerGlobalReceiver(SuitSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                SurvivalHudOverlay.updateSuitData(
                        payload.storedEnergy(),
                        payload.capacity(),
                        payload.temperature(),
                        payload.armorCount()
                );
            });
        });
    }
}
