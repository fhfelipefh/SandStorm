package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class SandStormMenus {
    public static final MenuType<Printer3DMenu> PRINTER_3D_MENU = register(
            "printer_3d",
            new MenuType<>(Printer3DMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<NaniteFabricatorMenu> NANITE_FABRICATOR_MENU = register(
            "nanite_fabricator",
            new MenuType<>(NaniteFabricatorMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<DesalinationFilterMenu> DESALINATION_FILTER_MENU = register(
            "desalination_filter",
            new MenuType<>(DesalinationFilterMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<DewCondenserMenu> DEW_CONDENSER_MENU = register(
            "dew_condenser",
            new MenuType<>(DewCondenserMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<ThermalGeneratorMenu> THERMAL_GENERATOR_MENU = register(
            "thermal_generator",
            new MenuType<>(ThermalGeneratorMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<ChemicalRefineryMenu> CHEMICAL_REFINERY_MENU = register(
            "chemical_refinery",
            new MenuType<>(ChemicalRefineryMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<HydroponicChamberMenu> HYDROPONIC_CHAMBER_MENU = register(
            "hydroponic_chamber",
            new MenuType<>(HydroponicChamberMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<AutonomousSonicTurretMenu> AUTONOMOUS_SONIC_TURRET_MENU = register(
            "autonomous_sonic_turret",
            new MenuType<>(AutonomousSonicTurretMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<SolidStateAccumulatorMenu> SOLID_STATE_ACCUMULATOR_MENU = register(
            "solid_state_accumulator",
            new MenuType<>(SolidStateAccumulatorMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<GridMonitorConsoleMenu> GRID_MONITOR_CONSOLE_MENU = register(
            "grid_monitor_console",
            new MenuType<>(GridMonitorConsoleMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<DeepCoreDrillMenu> DEEP_CORE_DRILL_MENU = register(
            "deep_core_drill",
            new MenuType<>(DeepCoreDrillMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<AutoAssemblyLineMenu> AUTO_ASSEMBLY_LINE_MENU = register(
            "auto_assembly_line",
            new MenuType<>(AutoAssemblyLineMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<MegastructureConstructorMenu> MEGASTRUCTURE_CONSTRUCTOR_MENU = register(
            "megastructure_constructor",
            new MenuType<>(MegastructureConstructorMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<BioreactorVatMenu> BIOREACTOR_VAT_MENU = register(
            "bioreactor_vat",
            new MenuType<>(BioreactorVatMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<MolecularModifierMenu> MOLECULAR_MODIFIER_MENU = register(
            "molecular_modifier",
            new MenuType<>(MolecularModifierMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<BioRegenerationPodMenu> BIO_REGENERATION_POD_MENU = register(
            "bio_regeneration_pod",
            new MenuType<>(BioRegenerationPodMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<CyborgIncubatorMenu> CYBORG_INCUBATOR_MENU = register(
            "cyborg_incubator_vat",
            new MenuType<>(CyborgIncubatorMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<CyborgTelemetryMenu> CYBORG_TELEMETRY_MENU = register(
            "cyborg_telemetry",
            new MenuType<>(CyborgTelemetryMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<CyborgDockingStationMenu> CYBORG_DOCKING_STATION_MENU = register(
            "cyborg_docking_station",
            new MenuType<>(CyborgDockingStationMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<HoloTacticalSpireMenu> HOLO_TACTICAL_SPIRE_MENU = register(
            "holo_tactical_spire",
            new MenuType<>(HoloTacticalSpireMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<QuantumSleeperMenu> QUANTUM_SLEEPER_MENU = register(
            "quantum_sleeper_pod",
            new MenuType<>(QuantumSleeperMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<PlasmaShieldMenu> PLASMA_SHIELD_MENU = register(
            "plasma_shield_generator",
            new MenuType<>(PlasmaShieldMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<KineticShieldMenu> KINETIC_SHIELD_MENU = register(
            "kinetic_shield_generator",
            new MenuType<>(KineticShieldMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<KineticRailgunMenu> KINETIC_RAILGUN_MENU = register(
            "kinetic_railgun",
            new MenuType<>(KineticRailgunMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<DeepCoreBoreholeMenu> DEEP_CORE_BOREHOLE_MENU = register(
            "deep_core_borehole",
            new MenuType<>(DeepCoreBoreholeMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<LithoPlasmaExtractorMenu> LITHO_PLASMA_EXTRACTOR_MENU = register(
            "litho_plasma_extractor",
            new MenuType<>(LithoPlasmaExtractorMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<SupercriticalHeatExchangerMenu> SUPERCRITICAL_HEAT_EXCHANGER_MENU = register(
            "supercritical_heat_exchanger",
            new MenuType<>(SupercriticalHeatExchangerMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<OrbitalMassDriverMenu> ORBITAL_MASS_DRIVER_MENU = register(
            "orbital_mass_driver",
            new MenuType<>(OrbitalMassDriverMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<OrbitalGroundStationMenu> ORBITAL_GROUND_STATION_MENU = register(
            "orbital_ground_station",
            new MenuType<>(OrbitalGroundStationMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<AtmosphericTerraformerMenu> ATMOSPHERIC_TERRAFORMER_MENU = register(
            "atmospheric_terraformer",
            new MenuType<>(AtmosphericTerraformerMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<ElectricFencePylonMenu> ELECTRIC_FENCE_PYLON_MENU = register(
            "electric_fence_pylon",
            new MenuType<>(ElectricFencePylonMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<NomadScavengerMenu> NOMAD_SCAVENGER_MENU = register(
            "nomad_scavenger",
            new MenuType<>(NomadScavengerMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<QuantumControllerMenu> QUANTUM_NETWORK_CONTROLLER_MENU = register(
            "quantum_network_controller",
            new MenuType<>(QuantumControllerMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<QuantumDiskDriveMenu> QUANTUM_DISK_DRIVE_MENU = register(
            "quantum_disk_drive",
            new MenuType<>(QuantumDiskDriveMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<QuantumTerminalMenu> QUANTUM_ACCESS_TERMINAL_MENU = register(
            "quantum_access_terminal",
            new MenuType<>(QuantumTerminalMenu::new, FeatureFlags.VANILLA_SET)
    );

    private static <T extends MenuType<?>> T register(String name, T menuType) {
        return Registry.register(BuiltInRegistries.MENU, SandStormMod.id(name), menuType);
    }

    public static void initialize() {
    }
}
