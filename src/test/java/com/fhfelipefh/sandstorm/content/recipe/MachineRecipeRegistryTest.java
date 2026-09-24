package com.fhfelipefh.sandstorm.content.recipe;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MachineRecipeRegistryTest {

    @Test
    void testPrinter3DRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:printer_3d");
        assertFalse(recipes.isEmpty());
        assertEquals(3, recipes.size());

        MachineRecipe r1 = recipes.get(0);
        assertEquals(200, r1.getEnergyCost());
        assertEquals(100, r1.getProcessTicks());
        assertEquals(1, r1.getSlot0InputCount());
        assertEquals(1, r1.getSlot1InputCount());

        MachineRecipe r2 = recipes.get(1);
        assertEquals(200, r2.getEnergyCost());
        assertEquals(100, r2.getProcessTicks());
        assertEquals(1, r2.getSlot0InputCount());
        assertEquals(2, r2.getSlot1InputCount());

        MachineRecipe r3 = recipes.get(2);
        assertEquals(200, r3.getEnergyCost());
        assertEquals(100, r3.getProcessTicks());
        assertEquals(1, r3.getSlot0InputCount());
        assertEquals(1, r3.getSlot1InputCount());
    }

    @Test
    void testNaniteFabricatorRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:nanite_fabricator");
        assertFalse(recipes.isEmpty());
        assertEquals(3, recipes.size());

        MachineRecipe r1 = recipes.get(0);
        assertEquals(240, r1.getEnergyCost());
        assertEquals(120, r1.getProcessTicks());
        assertEquals(1, r1.getSlot0InputCount());
        assertEquals(2, r1.getSlot1InputCount());

        MachineRecipe r2 = recipes.get(1);
        assertEquals(240, r2.getEnergyCost());
        assertEquals(120, r2.getProcessTicks());
        assertEquals(1, r2.getSlot0InputCount());
        assertEquals(2, r2.getSlot1InputCount());

        MachineRecipe r3 = recipes.get(2);
        assertEquals(240, r3.getEnergyCost());
        assertEquals(1200, r3.getProcessTicks());
        assertEquals(1, r3.getSlot0InputCount());
        assertEquals(1, r3.getSlot1InputCount());
    }

    @Test
    void testUnknownMachineRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:unknown");
        assertTrue(recipes.isEmpty());
    }
}
