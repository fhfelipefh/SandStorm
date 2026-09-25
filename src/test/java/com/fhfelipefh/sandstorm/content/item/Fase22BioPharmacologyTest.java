package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase22BioPharmacologyTest {

    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path RECIPES_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @EnumSource(PharmacologicalStimItem.StimType.class)
    void shouldVerifyAllStimTypesHaveValidIds(PharmacologicalStimItem.StimType stimType) {
        assertNotNull(stimType.getId());
        assertTrue(stimType.getId().length() > 0);
    }

    @Test
    void shouldHaveZeroNutritionAndDirectConsumptionForStimFood() {
        assertNotNull(PharmacologicalStimItem.STIM_FOOD);
        assertEquals(0, PharmacologicalStimItem.STIM_FOOD.nutrition());
        assertEquals(0.0f, PharmacologicalStimItem.STIM_FOOD.saturation(), 0.001f);
        assertTrue(PharmacologicalStimItem.STIM_FOOD.canAlwaysEat());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hypo_injector",
            "adrenal_stim",
            "biofoam_cartridge",
            "myomer_stim",
            "endothermic_serum",
            "grav_dampener_stim",
            "detox_ampoule",
            "stealth_nano_drape"
    })
    void shouldHaveValidAssetsForFase22Items(String itemName) {
        Path itemDef = ASSETS_DIR.resolve("items").resolve(itemName + ".json");
        Path modelDef = ASSETS_DIR.resolve("models").resolve("item").resolve(itemName + ".json");
        Path textureDef = ASSETS_DIR.resolve("textures").resolve("item").resolve(itemName + ".png");

        assertTrue(Files.exists(itemDef), "Item definition JSON must exist: " + itemDef);
        assertTrue(Files.exists(modelDef), "Item model JSON must exist: " + modelDef);
        assertTrue(Files.exists(textureDef), "Item texture PNG must exist: " + textureDef);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hypo_injector",
            "adrenal_stim",
            "biofoam_cartridge",
            "myomer_stim",
            "endothermic_serum",
            "grav_dampener_stim",
            "detox_ampoule",
            "stealth_nano_drape"
    })
    void shouldHaveValidCraftingRecipesForFase22Items(String itemName) {
        Path recipeFile = RECIPES_DIR.resolve(itemName + ".json");
        assertTrue(Files.exists(recipeFile), "Crafting recipe must exist: " + recipeFile);
    }

    @Test
    void shouldRegisterHypoInjectorUseSoundEvent() {
        assertNotNull(SandStormSoundEvents.HYPO_INJECTOR_USE);
        assertNotNull(SandStormSoundEvents.HYPO_INJECTOR_USE.location());
        assertEquals("sandstorm", SandStormSoundEvents.HYPO_INJECTOR_USE.location().getNamespace());
        assertEquals("item.hypo_injector.use", SandStormSoundEvents.HYPO_INJECTOR_USE.location().getPath());
    }

    @Test
    void shouldVerifyEmptyStackStimCountIsZero() {
        int count = HypoInjectorItem.countTotalStims(null);
        assertEquals(0, count);

        ItemStack empty = HypoInjectorItem.findStim(null);
        assertTrue(empty.isEmpty());
    }
}
