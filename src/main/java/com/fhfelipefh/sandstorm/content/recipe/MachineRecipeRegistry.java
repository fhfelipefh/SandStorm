package com.fhfelipefh.sandstorm.content.recipe;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineRecipeRegistry {
    private static final Map<String, List<MachineRecipe>> RECIPES = new HashMap<>();

    static {
        registerPrinter3DRecipes();
        registerNaniteFabricatorRecipes();
    }

    private static void registerPrinter3DRecipes() {
        List<MachineRecipe> list = List.of(
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.ELECTRIC_COMPONENT),
                        List.of(() -> new ItemStack(SandStormItems.RAW_SILICON)),
                        List.of(() -> new ItemStack(SandStormItems.RAW_SILICON)),
                        200,
                        100
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.CIRCUIT_BOARD),
                        List.of(() -> new ItemStack(SandStormItems.SILICON_WAFER)),
                        List.of(() -> new ItemStack(SandStormItems.RAW_SILICON), () -> new ItemStack(SandStormItems.SCRAP_METAL)),
                        200,
                        100
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.PLASMA_RIFLE),
                        List.of(() -> new ItemStack(SandStormItems.CIRCUIT_BOARD)),
                        List.of(() -> new ItemStack(SandStormItems.NANO_ACTUATOR)),
                        200,
                        100
                )
        );
        RECIPES.put("sandstorm:printer_3d", Collections.unmodifiableList(list));
    }

    private static void registerNaniteFabricatorRecipes() {
        List<MachineRecipe> list = List.of(
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.NANO_ACTUATOR),
                        List.of(() -> new ItemStack(SandStormItems.CIRCUIT_BOARD)),
                        List.of(() -> new ItemStack(SandStormItems.SANDWORM_CHITIN), () -> new ItemStack(SandStormItems.SCRAP_METAL)),
                        240,
                        120
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE),
                        List.of(() -> new ItemStack(SandStormItems.SANDWORM_CHITIN)),
                        List.of(() -> new ItemStack(SandStormItems.SCRAP_METAL), () -> new ItemStack(SandStormItems.NANO_ACTUATOR)),
                        240,
                        120
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.VIBRO_CRYSKNIFE),
                        List.of(() -> new ItemStack(SandStormItems.SANDWORM_TOOTH)),
                        List.of(() -> new ItemStack(SandStormItems.NANO_ACTUATOR)),
                        240,
                        1200
                )
        );
        RECIPES.put("sandstorm:nanite_fabricator", Collections.unmodifiableList(list));
    }

    public static List<MachineRecipe> getRecipes(String machineId) {
        return RECIPES.getOrDefault(machineId, Collections.emptyList());
    }
}
