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

    private static <T extends MenuType<?>> T register(String name, T menuType) {
        return Registry.register(BuiltInRegistries.MENU, SandStormMod.id(name), menuType);
    }

    public static void initialize() {
    }
}
