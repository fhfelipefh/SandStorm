package com.fhfelipefh.sandstorm.client.compat.jei;

import com.fhfelipefh.sandstorm.client.compat.jei.category.AtmosphericCarbonExtractorCategory;
import com.fhfelipefh.sandstorm.client.compat.jei.category.ChemicalRefineryCategory;
import com.fhfelipefh.sandstorm.client.compat.jei.category.DesalinationFilterCategory;
import com.fhfelipefh.sandstorm.client.compat.jei.category.DewCondenserCategory;
import com.fhfelipefh.sandstorm.client.compat.jei.category.HydroponicChamberCategory;
import com.fhfelipefh.sandstorm.client.compat.jei.category.MaterialDeconstructorCategory;
import com.fhfelipefh.sandstorm.client.compat.jei.category.MegastructureConstructorCategory;
import com.fhfelipefh.sandstorm.client.gui.MaterialDeconstructorScreen;
import com.fhfelipefh.sandstorm.content.gui.MaterialDeconstructorMenu;
import com.fhfelipefh.sandstorm.client.compat.jei.category.MolecularModifierCategory;
import com.fhfelipefh.sandstorm.client.compat.jei.category.NaniteFabricatorCategory;
import com.fhfelipefh.sandstorm.client.compat.jei.category.Printer3DCategory;
import com.fhfelipefh.sandstorm.client.gui.AtmosphericCarbonExtractorScreen;
import com.fhfelipefh.sandstorm.client.gui.ChemicalRefineryScreen;
import com.fhfelipefh.sandstorm.client.gui.DesalinationFilterScreen;
import com.fhfelipefh.sandstorm.client.gui.DewCondenserScreen;
import com.fhfelipefh.sandstorm.client.gui.ElectricArcFurnaceScreen;
import com.fhfelipefh.sandstorm.client.gui.HydroponicChamberScreen;
import com.fhfelipefh.sandstorm.client.gui.MegastructureConstructorScreen;
import com.fhfelipefh.sandstorm.client.gui.MolecularModifierScreen;
import com.fhfelipefh.sandstorm.client.gui.NaniteFabricatorScreen;
import com.fhfelipefh.sandstorm.client.gui.Printer3DScreen;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.gui.ChemicalRefineryMenu;
import com.fhfelipefh.sandstorm.content.gui.DewCondenserMenu;
import com.fhfelipefh.sandstorm.content.gui.HydroponicChamberMenu;
import com.fhfelipefh.sandstorm.content.gui.NaniteFabricatorMenu;
import com.fhfelipefh.sandstorm.content.gui.Printer3DMenu;
import com.fhfelipefh.sandstorm.content.gui.SandStormMenus;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipeRegistry;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class SandStormJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_UID = Identifier.parse(SandStormMod.MOD_ID + ":jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new Printer3DCategory(guiHelper),
                new NaniteFabricatorCategory(guiHelper),
                new MolecularModifierCategory(guiHelper),
                new DesalinationFilterCategory(guiHelper),
                new AtmosphericCarbonExtractorCategory(guiHelper),
                new MegastructureConstructorCategory(guiHelper),
                new ChemicalRefineryCategory(guiHelper),
                new HydroponicChamberCategory(guiHelper),
                new DewCondenserCategory(guiHelper),
                new MaterialDeconstructorCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SandStormJeiRecipeTypes.PRINTER_3D, MachineRecipeRegistry.getRecipes("sandstorm:printer_3d"));
        registration.addRecipes(SandStormJeiRecipeTypes.NANITE_FABRICATOR, MachineRecipeRegistry.getRecipes("sandstorm:nanite_fabricator"));
        registration.addRecipes(SandStormJeiRecipeTypes.MOLECULAR_MODIFIER, MachineRecipeRegistry.getRecipes("sandstorm:molecular_modifier"));
        registration.addRecipes(SandStormJeiRecipeTypes.DESALINATION_FILTER, MachineRecipeRegistry.getRecipes("sandstorm:desalination_filter"));
        registration.addRecipes(SandStormJeiRecipeTypes.ATMOSPHERIC_CARBON_EXTRACTOR, MachineRecipeRegistry.getRecipes("sandstorm:atmospheric_carbon_extractor"));
        registration.addRecipes(SandStormJeiRecipeTypes.MEGASTRUCTURE_CONSTRUCTOR, MachineRecipeRegistry.getRecipes("sandstorm:megastructure_constructor"));
        registration.addRecipes(SandStormJeiRecipeTypes.CHEMICAL_REFINERY, MachineRecipeRegistry.getRecipes("sandstorm:chemical_refinery"));
        registration.addRecipes(SandStormJeiRecipeTypes.HYDROPONIC_CHAMBER, MachineRecipeRegistry.getRecipes("sandstorm:hydroponic_chamber"));
        registration.addRecipes(SandStormJeiRecipeTypes.DEW_CONDENSER, MachineRecipeRegistry.getRecipes("sandstorm:dew_condenser"));
        registration.addRecipes(SandStormJeiRecipeTypes.MATERIAL_DECONSTRUCTOR, MachineRecipeRegistry.getRecipes("sandstorm:material_deconstructor"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(SandStormJeiRecipeTypes.PRINTER_3D, SandStormBlocks.PRINTER_3D);
        registration.addCraftingStation(SandStormJeiRecipeTypes.NANITE_FABRICATOR, SandStormBlocks.NANITE_FABRICATOR);
        registration.addCraftingStation(SandStormJeiRecipeTypes.MOLECULAR_MODIFIER, SandStormBlocks.MOLECULAR_MODIFIER);
        registration.addCraftingStation(SandStormJeiRecipeTypes.DESALINATION_FILTER, SandStormBlocks.DESALINATION_FILTER);
        registration.addCraftingStation(SandStormJeiRecipeTypes.ATMOSPHERIC_CARBON_EXTRACTOR, SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR);
        registration.addCraftingStation(SandStormJeiRecipeTypes.MEGASTRUCTURE_CONSTRUCTOR, SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR);
        registration.addCraftingStation(SandStormJeiRecipeTypes.CHEMICAL_REFINERY, SandStormBlocks.CHEMICAL_REFINERY);
        registration.addCraftingStation(SandStormJeiRecipeTypes.HYDROPONIC_CHAMBER, SandStormBlocks.HYDROPONIC_CHAMBER);
        registration.addCraftingStation(SandStormJeiRecipeTypes.DEW_CONDENSER, SandStormBlocks.DEW_CONDENSER);
        registration.addCraftingStation(SandStormJeiRecipeTypes.MATERIAL_DECONSTRUCTOR, SandStormBlocks.MATERIAL_DECONSTRUCTOR);

        registration.addCraftingStation(RecipeTypes.SMELTING, SandStormBlocks.ELECTRIC_ARC_FURNACE);
        registration.addCraftingStation(RecipeTypes.BLASTING, SandStormBlocks.ELECTRIC_ARC_FURNACE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(Printer3DScreen.class, 66, 18, 44, 40, SandStormJeiRecipeTypes.PRINTER_3D);
        registration.addRecipeClickArea(NaniteFabricatorScreen.class, 69, 38, 38, 14, SandStormJeiRecipeTypes.NANITE_FABRICATOR);
        registration.addRecipeClickArea(MolecularModifierScreen.class, 102, 33, 34, 20, SandStormJeiRecipeTypes.MOLECULAR_MODIFIER);
        registration.addRecipeClickArea(DesalinationFilterScreen.class, 69, 38, 38, 14, SandStormJeiRecipeTypes.DESALINATION_FILTER);
        registration.addRecipeClickArea(AtmosphericCarbonExtractorScreen.class, 70, 26, 40, 28, SandStormJeiRecipeTypes.ATMOSPHERIC_CARBON_EXTRACTOR);
        registration.addRecipeClickArea(MegastructureConstructorScreen.class, 69, 38, 38, 14, SandStormJeiRecipeTypes.MEGASTRUCTURE_CONSTRUCTOR);
        registration.addRecipeClickArea(ChemicalRefineryScreen.class, 69, 38, 38, 14, SandStormJeiRecipeTypes.CHEMICAL_REFINERY);
        registration.addRecipeClickArea(HydroponicChamberScreen.class, 76, 24, 30, 40, SandStormJeiRecipeTypes.HYDROPONIC_CHAMBER);
        registration.addRecipeClickArea(DewCondenserScreen.class, 76, 42, 28, 5, SandStormJeiRecipeTypes.DEW_CONDENSER);
        registration.addRecipeClickArea(MaterialDeconstructorScreen.class, 76, 35, 34, 16, SandStormJeiRecipeTypes.MATERIAL_DECONSTRUCTOR);

        registration.addRecipeClickArea(ElectricArcFurnaceScreen.class, 76, 30, 28, 16, RecipeTypes.SMELTING, RecipeTypes.BLASTING);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                MaterialDeconstructorMenu.class,
                SandStormMenus.MATERIAL_DECONSTRUCTOR_MENU,
                SandStormJeiRecipeTypes.MATERIAL_DECONSTRUCTOR,
                0,
                1,
                3,
                36
        );
        registration.addRecipeTransferHandler(
                Printer3DMenu.class,
                SandStormMenus.PRINTER_3D_MENU,
                SandStormJeiRecipeTypes.PRINTER_3D,
                0,
                2,
                4,
                36
        );
        registration.addRecipeTransferHandler(
                NaniteFabricatorMenu.class,
                SandStormMenus.NANITE_FABRICATOR_MENU,
                SandStormJeiRecipeTypes.NANITE_FABRICATOR,
                0,
                2,
                4,
                36
        );
        registration.addRecipeTransferHandler(
                ChemicalRefineryMenu.class,
                SandStormMenus.CHEMICAL_REFINERY_MENU,
                SandStormJeiRecipeTypes.CHEMICAL_REFINERY,
                0,
                3,
                5,
                36
        );
        registration.addRecipeTransferHandler(
                HydroponicChamberMenu.class,
                SandStormMenus.HYDROPONIC_CHAMBER_MENU,
                SandStormJeiRecipeTypes.HYDROPONIC_CHAMBER,
                0,
                3,
                5,
                36
        );
        registration.addRecipeTransferHandler(
                DewCondenserMenu.class,
                SandStormMenus.DEW_CONDENSER_MENU,
                SandStormJeiRecipeTypes.DEW_CONDENSER,
                0,
                1,
                2,
                36
        );
    }
}
