package com.fhfelipefh.sandstorm.client.compat.jei;

import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import mezz.jei.api.recipe.types.IRecipeType;

public final class SandStormJeiRecipeTypes {
    public static final IRecipeType<MachineRecipe> PRINTER_3D =
            IRecipeType.create(SandStormMod.MOD_ID, "printer_3d", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> NANITE_FABRICATOR =
            IRecipeType.create(SandStormMod.MOD_ID, "nanite_fabricator", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> MOLECULAR_MODIFIER =
            IRecipeType.create(SandStormMod.MOD_ID, "molecular_modifier", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> DESALINATION_FILTER =
            IRecipeType.create(SandStormMod.MOD_ID, "desalination_filter", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> ATMOSPHERIC_CARBON_EXTRACTOR =
            IRecipeType.create(SandStormMod.MOD_ID, "atmospheric_carbon_extractor", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> MEGASTRUCTURE_CONSTRUCTOR =
            IRecipeType.create(SandStormMod.MOD_ID, "megastructure_constructor", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> CHEMICAL_REFINERY =
            IRecipeType.create(SandStormMod.MOD_ID, "chemical_refinery", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> HYDROPONIC_CHAMBER =
            IRecipeType.create(SandStormMod.MOD_ID, "hydroponic_chamber", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> DEW_CONDENSER =
            IRecipeType.create(SandStormMod.MOD_ID, "dew_condenser", MachineRecipe.class);
    public static final IRecipeType<MachineRecipe> MATERIAL_DECONSTRUCTOR =
            IRecipeType.create(SandStormMod.MOD_ID, "material_deconstructor", MachineRecipe.class);

    private SandStormJeiRecipeTypes() {
    }
}

