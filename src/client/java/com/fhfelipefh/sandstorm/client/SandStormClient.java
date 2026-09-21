package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import com.fhfelipefh.sandstorm.client.gui.DesalinationFilterScreen;
import com.fhfelipefh.sandstorm.client.gui.NaniteFabricatorScreen;
import com.fhfelipefh.sandstorm.client.gui.Printer3DScreen;
import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.client.particle.SandstormParticleHandler;
import com.fhfelipefh.sandstorm.client.renderer.NaniteFabricatorBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.Printer3DBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.SandwormRenderer;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import com.fhfelipefh.sandstorm.content.network.SandstormWeatherPayload;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class SandStormClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SurvivalHudOverlay.initialize();
        DatapadClientHelper.initialize();
        SandstormParticleHandler.initialize();
        MenuScreens.register(SandStormMenus.PRINTER_3D_MENU, Printer3DScreen::new);
        MenuScreens.register(SandStormMenus.NANITE_FABRICATOR_MENU, NaniteFabricatorScreen::new);
        MenuScreens.register(SandStormMenus.DESALINATION_FILTER_MENU, DesalinationFilterScreen::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.PRINTER_3D_BE, Printer3DBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.NANITE_FABRICATOR_BE, NaniteFabricatorBlockEntityRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.NUTRIENT_BOMB, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SANDWORM, SandwormRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CARGO_DRONE, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.EXCAVATOR_VEHICLE, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.MEGAZORD, NoopRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(SuitSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                SurvivalHudOverlay.updateSuitData(
                        payload.storedEnergy(),
                        payload.capacity(),
                        payload.temperature(),
                        payload.armorCount()
                );
                if (payload.storedEnergy() >= payload.capacity() * 0.6) {
                    DatapadClientHelper.addCondition("sandstorm.battery_60");
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncPlayerQuestsPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                DatapadClientHelper.setQuests(payload.claimedQuestIds(), payload.completedConditions());
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SandstormWeatherPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (payload.active()) {
                    SandstormWeatherHandler.getWeather().startSandstorm(200, payload.intensity());
                } else {
                    SandstormWeatherHandler.getWeather().stopSandstorm();
                }
            });
        });
    }
}
