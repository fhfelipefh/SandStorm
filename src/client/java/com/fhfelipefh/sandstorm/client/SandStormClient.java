package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import com.fhfelipefh.sandstorm.client.gui.ChemicalRefineryScreen;
import com.fhfelipefh.sandstorm.client.gui.DesalinationFilterScreen;
import com.fhfelipefh.sandstorm.client.gui.HydroponicChamberScreen;
import com.fhfelipefh.sandstorm.client.gui.NaniteFabricatorScreen;
import com.fhfelipefh.sandstorm.client.gui.Printer3DScreen;
import com.fhfelipefh.sandstorm.client.gui.ThermalGeneratorScreen;
import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.client.mirage.DesertMirageHandler;
import com.fhfelipefh.sandstorm.client.particle.SandstormParticleHandler;
import com.fhfelipefh.sandstorm.client.renderer.DesalinationFilterBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.HydroponicChamberBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.NaniteFabricatorBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.Printer3DBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.SandwormRenderer;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.network.FlashlightTogglePayload;
import com.fhfelipefh.sandstorm.content.network.SandstormWeatherPayload;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EquipmentSlot;

public class SandStormClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SurvivalHudOverlay.initialize();
        DatapadClientHelper.initialize();
        SandstormParticleHandler.initialize();
        SandstormFlashlightKeys.initialize();
        DesertMirageHandler.initialize();

        MenuScreens.register(SandStormMenus.PRINTER_3D_MENU, Printer3DScreen::new);
        MenuScreens.register(SandStormMenus.NANITE_FABRICATOR_MENU, NaniteFabricatorScreen::new);
        MenuScreens.register(SandStormMenus.DESALINATION_FILTER_MENU, DesalinationFilterScreen::new);
        MenuScreens.register(SandStormMenus.THERMAL_GENERATOR_MENU, ThermalGeneratorScreen::new);
        MenuScreens.register(SandStormMenus.CHEMICAL_REFINERY_MENU, ChemicalRefineryScreen::new);
        MenuScreens.register(SandStormMenus.HYDROPONIC_CHAMBER_MENU, HydroponicChamberScreen::new);

        BlockEntityRendererRegistry.register(SandStormBlocks.PRINTER_3D_BE, Printer3DBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.NANITE_FABRICATOR_BE, NaniteFabricatorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.DESALINATION_FILTER_BE, DesalinationFilterBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.HYDROPONIC_CHAMBER_BE, HydroponicChamberBlockEntityRenderer::new);

        EntityRendererRegistry.register(SandStormEntities.NUTRIENT_BOMB, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SANDWORM, SandwormRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CARGO_DRONE, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.EXCAVATOR_VEHICLE, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.MEGAZORD, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SANDBOARD, NoopRenderer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (SandstormFlashlightKeys.FLASHLIGHT_KEY.consumeClick()) {
                if (client.player != null) {
                    boolean helmetOn = client.player.getItemBySlot(EquipmentSlot.HEAD).is(SandStormItems.SPACE_SUIT_HELMET);
                    FlashlightState.setHelmetEquipped(helmetOn);
                    if (helmetOn) {
                        boolean newState = !FlashlightState.isFlashlightOn();
                        FlashlightState.setFlashlightOn(newState);
                        ClientPlayNetworking.send(new FlashlightTogglePayload(newState));
                    }
                }
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                boolean helmetOn = mc.player.getItemBySlot(EquipmentSlot.HEAD).is(SandStormItems.SPACE_SUIT_HELMET);
                FlashlightState.setHelmetEquipped(helmetOn);
            }
        });

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
                if (payload.storedEnergy() == 0) {
                    FlashlightState.setFlashlightOn(false);
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
