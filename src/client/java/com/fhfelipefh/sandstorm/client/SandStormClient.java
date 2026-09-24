package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import com.fhfelipefh.sandstorm.client.gui.AutoAssemblyLineScreen;
import com.fhfelipefh.sandstorm.client.gui.AutonomousSonicTurretScreen;
import com.fhfelipefh.sandstorm.client.gui.BioreactorVatScreen;
import com.fhfelipefh.sandstorm.client.gui.ChemicalRefineryScreen;
import com.fhfelipefh.sandstorm.client.gui.DeepCoreDrillScreen;
import com.fhfelipefh.sandstorm.client.gui.DesalinationFilterScreen;
import com.fhfelipefh.sandstorm.client.gui.GridMonitorConsoleScreen;
import com.fhfelipefh.sandstorm.client.gui.HydroponicChamberScreen;
import com.fhfelipefh.sandstorm.client.gui.MegastructureConstructorScreen;
import com.fhfelipefh.sandstorm.client.gui.NaniteFabricatorScreen;
import com.fhfelipefh.sandstorm.client.gui.Printer3DScreen;
import com.fhfelipefh.sandstorm.client.gui.SolidStateAccumulatorScreen;
import com.fhfelipefh.sandstorm.client.gui.ThermalGeneratorScreen;
import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.client.mirage.DesertMirageHandler;
import com.fhfelipefh.sandstorm.client.particle.SandstormParticleHandler;
import com.fhfelipefh.sandstorm.client.renderer.BuilderDroneEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.DesalinationFilterBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.HydroponicChamberBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.MegastructureConstructorBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.NaniteFabricatorBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.Printer3DBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.SandwormRenderer;
import com.fhfelipefh.sandstorm.client.renderer.SpaceSuitArmorRenderer;
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
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
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
        MenuScreens.register(SandStormMenus.AUTONOMOUS_SONIC_TURRET_MENU, AutonomousSonicTurretScreen::new);
        MenuScreens.register(SandStormMenus.SOLID_STATE_ACCUMULATOR_MENU, SolidStateAccumulatorScreen::new);
        MenuScreens.register(SandStormMenus.GRID_MONITOR_CONSOLE_MENU, GridMonitorConsoleScreen::new);
        MenuScreens.register(SandStormMenus.DEEP_CORE_DRILL_MENU, DeepCoreDrillScreen::new);
        MenuScreens.register(SandStormMenus.AUTO_ASSEMBLY_LINE_MENU, AutoAssemblyLineScreen::new);
        MenuScreens.register(SandStormMenus.MEGASTRUCTURE_CONSTRUCTOR_MENU, MegastructureConstructorScreen::new);
        MenuScreens.register(SandStormMenus.BIOREACTOR_VAT_MENU, BioreactorVatScreen::new);

        BlockEntityRendererRegistry.register(SandStormBlocks.PRINTER_3D_BE, Printer3DBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.NANITE_FABRICATOR_BE, NaniteFabricatorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.DESALINATION_FILTER_BE, DesalinationFilterBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.HYDROPONIC_CHAMBER_BE, HydroponicChamberBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR_BE, MegastructureConstructorBlockEntityRenderer::new);

        ArmorRenderer.register(new SpaceSuitArmorRenderer(),
                SandStormItems.SPACE_SUIT_HELMET,
                SandStormItems.SPACE_SUIT_CHESTPLATE,
                SandStormItems.SPACE_SUIT_LEGGINGS,
                SandStormItems.SPACE_SUIT_BOOTS);

        EntityRendererRegistry.register(SandStormEntities.NUTRIENT_BOMB, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SANDWORM, SandwormRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CARGO_DRONE, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.EXCAVATOR_VEHICLE, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.MEGAZORD, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SANDBOARD, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.BUILDER_DRONE, BuilderDroneEntityRenderer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (SandstormFlashlightKeys.FLASHLIGHT_KEY.consumeClick()) {
                if (client.player != null) {
                    boolean helmetOn = client.player.getItemBySlot(EquipmentSlot.HEAD).is(SandStormItems.SPACE_SUIT_HELMET);
                    FlashlightState.setHelmetEquipped(helmetOn);
                    if (helmetOn) {
                        int newMode = FlashlightState.cycleMode();
                        ClientPlayNetworking.send(new FlashlightTogglePayload(newMode));
                        playFlashlightSound(client, newMode);
                        client.player.sendOverlayMessage(getFlashlightFeedbackMessage(newMode));
                    } else {
                        client.player.sendOverlayMessage(Component.literal("§c[Lanterna] Requer Capacete do Traje Espacial!"));
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
                    FlashlightState.setMode(FlashlightState.MODE_OFF);
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

    private static void playFlashlightSound(Minecraft client, int mode) {
        float pitch = switch (mode) {
            case FlashlightState.MODE_LOW -> 0.90f;
            case FlashlightState.MODE_MEDIUM -> 1.15f;
            case FlashlightState.MODE_HIGH -> 1.40f;
            default -> 0.65f;
        };
        client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, pitch));
    }

    private static Component getFlashlightFeedbackMessage(int mode) {
        return switch (mode) {
            case FlashlightState.MODE_LOW -> Component.literal("§6[Lanterna] §fModo: §aFraco §7(1x Consumo)");
            case FlashlightState.MODE_MEDIUM -> Component.literal("§6[Lanterna] §fModo: §eMédio §7(2x Consumo)");
            case FlashlightState.MODE_HIGH -> Component.literal("§6[Lanterna] §fModo: §bForte §7(4x Consumo)");
            default -> Component.literal("§6[Lanterna] §7Desligada");
        };
    }
}
