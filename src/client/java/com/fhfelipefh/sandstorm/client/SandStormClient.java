package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.client.gui.DatapadClientHelper;
import com.fhfelipefh.sandstorm.client.gui.AmnioticIncubatorScreen;
import com.fhfelipefh.sandstorm.client.gui.AtmosphericTerraformerScreen;
import com.fhfelipefh.sandstorm.client.gui.AutoAssemblyLineScreen;
import com.fhfelipefh.sandstorm.client.gui.AutonomousSonicTurretScreen;
import com.fhfelipefh.sandstorm.client.gui.BioRegenerationPodScreen;
import com.fhfelipefh.sandstorm.client.gui.BioreactorVatScreen;
import com.fhfelipefh.sandstorm.client.gui.ChemicalRefineryScreen;
import com.fhfelipefh.sandstorm.client.gui.CyborgDockingStationScreen;
import com.fhfelipefh.sandstorm.client.gui.CyborgIncubatorScreen;
import com.fhfelipefh.sandstorm.client.gui.CyborgTelemetryScreen;
import com.fhfelipefh.sandstorm.client.gui.DeepCoreBoreholeScreen;
import com.fhfelipefh.sandstorm.client.gui.DeepCoreDrillScreen;
import com.fhfelipefh.sandstorm.client.gui.DesalinationFilterScreen;
import com.fhfelipefh.sandstorm.client.gui.DewCondenserScreen;
import com.fhfelipefh.sandstorm.client.gui.ElectricFencePylonScreen;
import com.fhfelipefh.sandstorm.client.gui.GridMonitorConsoleScreen;
import com.fhfelipefh.sandstorm.client.gui.HoloTacticalSpireScreen;
import com.fhfelipefh.sandstorm.client.gui.HydroponicChamberScreen;
import com.fhfelipefh.sandstorm.client.gui.KineticRailgunScreen;
import com.fhfelipefh.sandstorm.client.gui.KineticShieldScreen;
import com.fhfelipefh.sandstorm.client.gui.LithoPlasmaExtractorScreen;
import com.fhfelipefh.sandstorm.client.gui.MegastructureConstructorScreen;
import com.fhfelipefh.sandstorm.client.gui.MolecularModifierScreen;
import com.fhfelipefh.sandstorm.client.gui.MorphingMatrixCoreScreen;
import com.fhfelipefh.sandstorm.client.gui.NaniteFabricatorScreen;
import com.fhfelipefh.sandstorm.client.gui.NomadScavengerScreen;
import com.fhfelipefh.sandstorm.client.gui.OrbitalGroundStationScreen;
import com.fhfelipefh.sandstorm.client.gui.OrbitalMassDriverScreen;
import com.fhfelipefh.sandstorm.client.gui.PlasmaShieldScreen;
import com.fhfelipefh.sandstorm.client.gui.Printer3DScreen;
import com.fhfelipefh.sandstorm.client.gui.QuantumControllerScreen;
import com.fhfelipefh.sandstorm.client.gui.QuantumDiskDriveScreen;
import com.fhfelipefh.sandstorm.client.gui.QuantumSleeperScreen;
import com.fhfelipefh.sandstorm.client.gui.QuantumTerminalScreen;
import com.fhfelipefh.sandstorm.client.gui.SolidStateAccumulatorScreen;
import com.fhfelipefh.sandstorm.content.gui.QuantumTerminalMenu;
import com.fhfelipefh.sandstorm.content.network.SyncTerminalGridPayload;
import com.fhfelipefh.sandstorm.client.gui.SupercriticalHeatExchangerScreen;
import com.fhfelipefh.sandstorm.client.gui.ThermalGeneratorScreen;
import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.client.handler.EmpDeafenClientHandler;
import com.fhfelipefh.sandstorm.client.mirage.DesertMirageHandler;
import com.fhfelipefh.sandstorm.client.particle.SandstormParticleHandler;
import com.fhfelipefh.sandstorm.client.tooltip.SandStormTechnicalTooltipHandler;
import com.fhfelipefh.sandstorm.client.renderer.AquiferBeetleRenderer;
import com.fhfelipefh.sandstorm.client.renderer.AutonomousSonicTurretRenderer;
import com.fhfelipefh.sandstorm.client.renderer.BuilderDroneEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.CargoDroneRenderer;
import com.fhfelipefh.sandstorm.client.renderer.ClientUplinkRenderer;
import com.fhfelipefh.sandstorm.client.renderer.CrushingSpikeGateRenderer;
import com.fhfelipefh.sandstorm.client.renderer.CyberneticGolemRenderer;
import com.fhfelipefh.sandstorm.client.renderer.CyborgRenderer;
import com.fhfelipefh.sandstorm.client.renderer.DesalinationFilterBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.HydroponicChamberBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.MegastructureConstructorBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.NaniteFabricatorBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.Printer3DBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.QuantumDiskDriveBlockEntityRenderer;
import com.fhfelipefh.sandstorm.client.renderer.ExcavatorVehicleRenderer;
import com.fhfelipefh.sandstorm.client.renderer.MegazordRenderer;
import com.fhfelipefh.sandstorm.client.renderer.SandboardRenderer;
import com.fhfelipefh.sandstorm.client.renderer.SandwormRenderer;
import com.fhfelipefh.sandstorm.client.renderer.DerelictAutomatonRenderer;
import com.fhfelipefh.sandstorm.client.renderer.CyberHoundRenderer;
import com.fhfelipefh.sandstorm.client.renderer.LaborerUnitRenderer;
import com.fhfelipefh.sandstorm.client.renderer.ScoutDroneRenderer;
import com.fhfelipefh.sandstorm.client.renderer.CrawlerDroneRenderer;
import com.fhfelipefh.sandstorm.client.renderer.ScrapSentinelRenderer;
import com.fhfelipefh.sandstorm.client.renderer.NomadScavengerRenderer;
import com.fhfelipefh.sandstorm.client.renderer.SpaceSuitArmorRenderer;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.network.CallGolemPayload;
import com.fhfelipefh.sandstorm.content.network.EmpDeafenPayload;
import com.fhfelipefh.sandstorm.content.network.FlashlightTogglePayload;
import com.fhfelipefh.sandstorm.content.network.MagneticInterferencePayload;
import com.fhfelipefh.sandstorm.content.network.SandstormWeatherPayload;
import com.fhfelipefh.sandstorm.content.network.SuitSyncPayload;
import com.fhfelipefh.sandstorm.content.network.SyncPlayerQuestsPayload;
import com.fhfelipefh.sandstorm.content.network.SyncUplinkZonePayload;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
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
        EmpDeafenClientHandler.initialize();
        SandStormTechnicalTooltipHandler.initialize();

        MenuScreens.register(SandStormMenus.PRINTER_3D_MENU, Printer3DScreen::new);
        MenuScreens.register(SandStormMenus.NANITE_FABRICATOR_MENU, NaniteFabricatorScreen::new);
        MenuScreens.register(SandStormMenus.DESALINATION_FILTER_MENU, DesalinationFilterScreen::new);
        MenuScreens.register(SandStormMenus.DEW_CONDENSER_MENU, DewCondenserScreen::new);
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
        MenuScreens.register(SandStormMenus.MOLECULAR_MODIFIER_MENU, MolecularModifierScreen::new);
        MenuScreens.register(SandStormMenus.BIO_REGENERATION_POD_MENU, BioRegenerationPodScreen::new);
        MenuScreens.register(SandStormMenus.CYBORG_INCUBATOR_MENU, CyborgIncubatorScreen::new);
        MenuScreens.register(SandStormMenus.CYBORG_TELEMETRY_MENU, CyborgTelemetryScreen::new);
        MenuScreens.register(SandStormMenus.CYBORG_DOCKING_STATION_MENU, CyborgDockingStationScreen::new);
        MenuScreens.register(SandStormMenus.HOLO_TACTICAL_SPIRE_MENU, HoloTacticalSpireScreen::new);
        MenuScreens.register(SandStormMenus.QUANTUM_SLEEPER_MENU, QuantumSleeperScreen::new);
        MenuScreens.register(SandStormMenus.PLASMA_SHIELD_MENU, PlasmaShieldScreen::new);
        MenuScreens.register(SandStormMenus.KINETIC_SHIELD_MENU, KineticShieldScreen::new);
        MenuScreens.register(SandStormMenus.KINETIC_RAILGUN_MENU, KineticRailgunScreen::new);
        MenuScreens.register(SandStormMenus.DEEP_CORE_BOREHOLE_MENU, DeepCoreBoreholeScreen::new);
        MenuScreens.register(SandStormMenus.LITHO_PLASMA_EXTRACTOR_MENU, LithoPlasmaExtractorScreen::new);
        MenuScreens.register(SandStormMenus.SUPERCRITICAL_HEAT_EXCHANGER_MENU, SupercriticalHeatExchangerScreen::new);
        MenuScreens.register(SandStormMenus.ORBITAL_MASS_DRIVER_MENU, OrbitalMassDriverScreen::new);
        MenuScreens.register(SandStormMenus.ORBITAL_GROUND_STATION_MENU, OrbitalGroundStationScreen::new);
        MenuScreens.register(SandStormMenus.ATMOSPHERIC_TERRAFORMER_MENU, AtmosphericTerraformerScreen::new);
        MenuScreens.register(SandStormMenus.ELECTRIC_FENCE_PYLON_MENU, ElectricFencePylonScreen::new);
        MenuScreens.register(SandStormMenus.NOMAD_SCAVENGER_MENU, NomadScavengerScreen::new);
        MenuScreens.register(SandStormMenus.QUANTUM_NETWORK_CONTROLLER_MENU, QuantumControllerScreen::new);
        MenuScreens.register(SandStormMenus.QUANTUM_DISK_DRIVE_MENU, QuantumDiskDriveScreen::new);
        MenuScreens.register(SandStormMenus.QUANTUM_ACCESS_TERMINAL_MENU, QuantumTerminalScreen::new);
        MenuScreens.register(SandStormMenus.MORPHING_MATRIX_CORE_MENU, MorphingMatrixCoreScreen::new);
        MenuScreens.register(SandStormMenus.AMNIOTIC_INCUBATOR_MENU, AmnioticIncubatorScreen::new);

        BlockEntityRendererRegistry.register(SandStormBlocks.PRINTER_3D_BE, Printer3DBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.NANITE_FABRICATOR_BE, NaniteFabricatorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.DESALINATION_FILTER_BE, DesalinationFilterBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.HYDROPONIC_CHAMBER_BE, HydroponicChamberBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR_BE, MegastructureConstructorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.AUTONOMOUS_SONIC_TURRET_BE, AutonomousSonicTurretRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.CRUSHING_SPIKE_GATE_BE, CrushingSpikeGateRenderer::new);
        BlockEntityRendererRegistry.register(SandStormBlocks.QUANTUM_DISK_DRIVE_BE, QuantumDiskDriveBlockEntityRenderer::new);

        ArmorRenderer.register(new SpaceSuitArmorRenderer(),
                SandStormItems.SPACE_SUIT_HELMET,
                SandStormItems.SPACE_SUIT_CHESTPLATE,
                SandStormItems.SPACE_SUIT_LEGGINGS,
                SandStormItems.SPACE_SUIT_BOOTS);

        EntityRendererRegistry.register(SandStormEntities.NUTRIENT_BOMB, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SANDWORM, SandwormRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CARGO_DRONE, CargoDroneRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.EXCAVATOR_VEHICLE, ExcavatorVehicleRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.MEGAZORD, MegazordRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SANDBOARD, SandboardRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.BUILDER_DRONE, BuilderDroneEntityRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.MEDBAY_SEAT, NoopRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CYBORG_EXCAVATOR, CyborgRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CYBORG_BUILDER, CyborgRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CYBORG_HARVESTER, CyborgRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SCRAP_SENTINEL, ScrapSentinelRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.AQUIFER_BEETLE, AquiferBeetleRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.NOMAD_SCAVENGER, NomadScavengerRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.DERELICT_AUTOMATON, DerelictAutomatonRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CYBER_HOUND, CyberHoundRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.LABORER_UNIT, LaborerUnitRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.SCOUT_DRONE, ScoutDroneRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CRAWLER_DRONE, CrawlerDroneRenderer::new);
        EntityRendererRegistry.register(SandStormEntities.CYBERNETIC_GOLEM, CyberneticGolemRenderer::new);

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
            if (SandstormFlashlightKeys.CALL_GOLEM_KEY.consumeClick()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(new CallGolemPayload());
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
        ClientPlayNetworking.registerGlobalReceiver(MagneticInterferencePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                SurvivalHudOverlay.triggerMagneticInterference(payload.durationTicks());
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(EmpDeafenPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                EmpDeafenClientHandler.trigger(payload.durationTicks());
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncTerminalGridPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player != null && context.client().player.containerMenu instanceof QuantumTerminalMenu menu) {
                    menu.updateClientState(
                            payload.terminalPos(),
                            payload.items(),
                            payload.energyStored(),
                            payload.maxEnergy(),
                            payload.totalStored(),
                            payload.totalCapacity()
                    );
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncUplinkZonePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ClientUplinkRenderer.setClientZone(
                        payload.cornerA(),
                        payload.cornerB(),
                        payload.modeOrdinal(),
                        payload.moveTarget()
                );
            });
        });
        LevelRenderEvents.COLLECT_SUBMITS.register(ClientUplinkRenderer::render);
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
