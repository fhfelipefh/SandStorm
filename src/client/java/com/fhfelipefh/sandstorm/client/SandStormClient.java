package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import com.fhfelipefh.sandstorm.client.gui.DesalinationFilterScreen;
import com.fhfelipefh.sandstorm.client.gui.NaniteFabricatorScreen;
import com.fhfelipefh.sandstorm.client.gui.Printer3DScreen;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;

public class SandStormClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SurvivalHudOverlay.initialize();
        DatapadClientHelper.initialize();
        MenuScreens.register(SandStormMenus.PRINTER_3D_MENU, Printer3DScreen::new);
        MenuScreens.register(SandStormMenus.NANITE_FABRICATOR_MENU, NaniteFabricatorScreen::new);
        MenuScreens.register(SandStormMenus.DESALINATION_FILTER_MENU, DesalinationFilterScreen::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                com.fhfelipefh.sandstorm.content.entity.SandStormEntities.NUTRIENT_BOMB,
                net.minecraft.client.renderer.entity.ThrownItemRenderer::new
        );
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
        ClientPlayNetworking.registerGlobalReceiver(SyncPlayerQuestsPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                DatapadClientHelper.setClaimedQuests(payload.claimedQuestIds());
            });
        });
    }
}
