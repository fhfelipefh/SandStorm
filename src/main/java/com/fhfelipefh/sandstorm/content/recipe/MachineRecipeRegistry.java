package com.fhfelipefh.sandstorm.content.recipe;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MachineRecipeRegistry {
    private static final Map<String, List<MachineRecipe>> RECIPES = new HashMap<>();

    static {
        registerPrinter3DRecipes();
        registerNaniteFabricatorRecipes();
        registerMolecularModifierRecipes();
        registerMegastructureConstructorRecipes();
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
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.GEOLOGICAL_SCANNER),
                        List.of(() -> new ItemStack(SandStormItems.SILICON_WAFER)),
                        List.of(() -> new ItemStack(SandStormItems.ELECTRIC_COMPONENT)),
                        200,
                        300
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.FIELD_PROBE),
                        List.of(() -> new ItemStack(SandStormItems.CIRCUIT_BOARD)),
                        List.of(() -> new ItemStack(SandStormItems.SILICON_WAFER)),
                        200,
                        200
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.REPAIR_TOOL),
                        List.of(() -> new ItemStack(SandStormItems.SCRAP_METAL)),
                        List.of(() -> new ItemStack(SandStormItems.ELECTRIC_COMPONENT)),
                        200,
                        200
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.STRUCTURAL_PLATE),
                        List.of(() -> new ItemStack(SandStormItems.SCRAP_METAL)),
                        List.of(() -> new ItemStack(SandStormItems.SCRAP_METAL)),
                        150,
                        100
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.CIRCUIT_MOUNT),
                        List.of(() -> new ItemStack(SandStormItems.SCRAP_METAL)),
                        List.of(() -> new ItemStack(SandStormItems.SILICON_WAFER)),
                        150,
                        150
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormItems.PRESSURE_SEAL),
                        List.of(() -> new ItemStack(SandStormItems.SCRAP_METAL)),
                        List.of(() -> new ItemStack(SandStormItems.MINERAL_SALT)),
                        150,
                        150
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

    private static void registerMolecularModifierRecipes() {
        List<Supplier<ItemStack>> weaponTargets = List.of(
                () -> new ItemStack(SandStormItems.VIBRO_CRYSKNIFE),
                () -> new ItemStack(SandStormItems.PLASMA_RIFLE),
                () -> new ItemStack(SandStormItems.HEAVY_PLASMA_CANNON),
                () -> new ItemStack(SandStormItems.SONIC_CANNON),
                () -> new ItemStack(Items.DIAMOND_SWORD),
                () -> new ItemStack(Items.NETHERITE_SWORD)
        );

        List<Supplier<ItemStack>> toolTargets = List.of(
                () -> new ItemStack(Items.DIAMOND_PICKAXE),
                () -> new ItemStack(Items.NETHERITE_PICKAXE),
                () -> new ItemStack(Items.DIAMOND_AXE),
                () -> new ItemStack(Items.DIAMOND_SHOVEL)
        );

        List<Supplier<ItemStack>> armorTargets = List.of(
                () -> new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                () -> new ItemStack(SandStormItems.SPACE_SUIT_HELMET),
                () -> new ItemStack(SandStormItems.SPACE_SUIT_LEGGINGS),
                () -> new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                () -> new ItemStack(Items.DIAMOND_CHESTPLATE),
                () -> new ItemStack(Items.NETHERITE_CHESTPLATE)
        );

        List<Supplier<ItemStack>> bootsTargets = List.of(
                () -> new ItemStack(SandStormItems.SPACE_SUIT_BOOTS),
                () -> new ItemStack(Items.DIAMOND_BOOTS),
                () -> new ItemStack(Items.NETHERITE_BOOTS),
                () -> new ItemStack(Items.IRON_BOOTS)
        );

        List<Supplier<ItemStack>> universalTargets = List.of(
                () -> new ItemStack(SandStormItems.SPACE_SUIT_CHESTPLATE),
                () -> new ItemStack(SandStormItems.VIBRO_CRYSKNIFE),
                () -> new ItemStack(SandStormItems.PLASMA_RIFLE),
                () -> new ItemStack(Items.DIAMOND_PICKAXE),
                () -> new ItemStack(Items.DIAMOND_SWORD)
        );

        List<MachineRecipe> list = List.of(
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.VIBRO_CRYSKNIFE),
                        () -> Component.translatable("item.sandstorm.vibro_resonator_module"),
                        weaponTargets,
                        List.of(() -> new ItemStack(SandStormItems.VIBRO_RESONATOR_MODULE)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.VIBRO_CRYSKNIFE),
                        () -> Component.translatable("item.sandstorm.thermal_plasma_emitter"),
                        weaponTargets,
                        List.of(() -> new ItemStack(SandStormItems.THERMAL_PLASMA_EMITTER)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.VIBRO_CRYSKNIFE),
                        () -> Component.translatable("item.sandstorm.kinetic_focus_module"),
                        weaponTargets,
                        List.of(() -> new ItemStack(SandStormItems.KINETIC_FOCUS_MODULE)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(Items.DIAMOND_PICKAXE),
                        () -> Component.translatable("item.sandstorm.cavitation_frequency_core"),
                        toolTargets,
                        List.of(() -> new ItemStack(SandStormItems.CAVITATION_FREQUENCY_CORE)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(Items.DIAMOND_PICKAXE),
                        () -> Component.translatable("item.sandstorm.atomic_phase_disrupter"),
                        toolTargets,
                        List.of(() -> new ItemStack(SandStormItems.ATOMIC_PHASE_DISRUPTER)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(Items.DIAMOND_PICKAXE),
                        () -> Component.translatable("item.sandstorm.spectrometric_sifter"),
                        toolTargets,
                        List.of(() -> new ItemStack(SandStormItems.SPECTROMETRIC_SIFTER)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.SPACE_SUIT_CHESTPLATE),
                        () -> Component.translatable("item.sandstorm.self_healing_nanite_matrix"),
                        universalTargets,
                        List.of(() -> new ItemStack(SandStormItems.SELF_HEALING_NANITE_MATRIX)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.SPACE_SUIT_CHESTPLATE),
                        () -> Component.translatable("item.sandstorm.titanium_lattice_coating"),
                        universalTargets,
                        List.of(() -> new ItemStack(SandStormItems.TITANIUM_LATTICE_COATING)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.SPACE_SUIT_CHESTPLATE),
                        () -> Component.translatable("item.sandstorm.ballistic_dampener_mesh"),
                        armorTargets,
                        List.of(() -> new ItemStack(SandStormItems.BALLISTIC_DAMPENER_MESH)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.SPACE_SUIT_CHESTPLATE),
                        () -> Component.translatable("item.sandstorm.ablative_thermal_plating"),
                        armorTargets,
                        List.of(() -> new ItemStack(SandStormItems.ABLATIVE_THERMAL_PLATING)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.SPACE_SUIT_BOOTS),
                        () -> Component.translatable("item.sandstorm.pneumatic_fall_dampers"),
                        bootsTargets,
                        List.of(() -> new ItemStack(SandStormItems.PNEUMATIC_FALL_DAMPERS)),
                        500,
                        50
                ),
                new MachineRecipe(
                        () -> createUpgradedPreview(SandStormItems.SPACE_SUIT_CHESTPLATE),
                        () -> Component.translatable("item.sandstorm.reactive_shock_plating"),
                        armorTargets,
                        List.of(() -> new ItemStack(SandStormItems.REACTIVE_SHOCK_PLATING)),
                        500,
                        50
                )
        );
        RECIPES.put("sandstorm:molecular_modifier", Collections.unmodifiableList(list));
    }

    private static void registerMegastructureConstructorRecipes() {
        List<MachineRecipe> list = List.of(
                new MachineRecipe(
                        () -> new ItemStack(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR),
                        () -> Component.translatable("megastructure.sandstorm.biosphere_dome"),
                        List.of(() -> new ItemStack(Blocks.IRON_BLOCK), () -> new ItemStack(Blocks.SEA_LANTERN)),
                        List.of(() -> new ItemStack(Blocks.TINTED_GLASS)),
                        50000,
                        2400
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR),
                        () -> Component.translatable("megastructure.sandstorm.planetary_citadel"),
                        List.of(() -> new ItemStack(Blocks.SMOOTH_SANDSTONE), () -> new ItemStack(Blocks.CUT_SANDSTONE)),
                        List.of(() -> new ItemStack(Blocks.IRON_BLOCK), () -> new ItemStack(Blocks.CHISELED_SANDSTONE), () -> new ItemStack(Blocks.IRON_BARS)),
                        100000,
                        3600
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR),
                        () -> Component.translatable("megastructure.sandstorm.orbital_launch_silo"),
                        List.of(() -> new ItemStack(Blocks.OBSIDIAN), () -> new ItemStack(Blocks.RAW_COPPER_BLOCK)),
                        List.of(() -> new ItemStack(Blocks.IRON_BLOCK), () -> new ItemStack(Blocks.IRON_BARS)),
                        150000,
                        4800
                ),
                new MachineRecipe(
                        () -> new ItemStack(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR),
                        () -> Component.translatable("megastructure.sandstorm.desert_tech_pyramid"),
                        List.of(() -> new ItemStack(Blocks.CUT_SANDSTONE), () -> new ItemStack(Blocks.CHISELED_SANDSTONE)),
                        List.of(() -> new ItemStack(Blocks.GOLD_BLOCK), () -> new ItemStack(Blocks.SEA_LANTERN)),
                        200000,
                        6000
                )
        );
        RECIPES.put("sandstorm:megastructure_constructor", Collections.unmodifiableList(list));
    }

    private static ItemStack createUpgradedPreview(ItemLike item) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    public static List<MachineRecipe> getRecipes(String machineId) {
        return RECIPES.getOrDefault(machineId, Collections.emptyList());
    }
}
