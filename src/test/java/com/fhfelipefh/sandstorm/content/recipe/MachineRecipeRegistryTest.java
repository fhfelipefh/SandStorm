package com.fhfelipefh.sandstorm.content.recipe;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MachineRecipeRegistryTest {

    @Test
    void testPrinter3DRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:printer_3d");
        assertFalse(recipes.isEmpty());
        assertEquals(9, recipes.size());

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

        MachineRecipe r4 = recipes.get(3);
        assertEquals(200, r4.getEnergyCost());
        assertEquals(300, r4.getProcessTicks());
        assertEquals(1, r4.getSlot0InputCount());
        assertEquals(1, r4.getSlot1InputCount());

        MachineRecipe r5 = recipes.get(4);
        assertEquals(200, r5.getEnergyCost());
        assertEquals(200, r5.getProcessTicks());
        assertEquals(1, r5.getSlot0InputCount());
        assertEquals(1, r5.getSlot1InputCount());

        MachineRecipe r6 = recipes.get(5);
        assertEquals(200, r6.getEnergyCost());
        assertEquals(200, r6.getProcessTicks());
        assertEquals(1, r6.getSlot0InputCount());
        assertEquals(1, r6.getSlot1InputCount());

        MachineRecipe r7 = recipes.get(6);
        assertEquals(150, r7.getEnergyCost());
        assertEquals(100, r7.getProcessTicks());
        assertEquals(1, r7.getSlot0InputCount());
        assertEquals(1, r7.getSlot1InputCount());

        MachineRecipe r8 = recipes.get(7);
        assertEquals(150, r8.getEnergyCost());
        assertEquals(150, r8.getProcessTicks());
        assertEquals(1, r8.getSlot0InputCount());
        assertEquals(1, r8.getSlot1InputCount());

        MachineRecipe r9 = recipes.get(8);
        assertEquals(150, r9.getEnergyCost());
        assertEquals(150, r9.getProcessTicks());
        assertEquals(1, r9.getSlot0InputCount());
        assertEquals(1, r9.getSlot1InputCount());
    }

    @Test
    void testNaniteFabricatorRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:nanite_fabricator");
        assertFalse(recipes.isEmpty());
        assertEquals(4, recipes.size());

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

        MachineRecipe r4 = recipes.get(3);
        assertEquals(200, r4.getEnergyCost());
        assertEquals(100, r4.getProcessTicks());
        assertEquals(1, r4.getSlot0InputCount());
        assertEquals(1, r4.getSlot1InputCount());
    }

    @Test
    void testMolecularModifierRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:molecular_modifier");
        assertFalse(recipes.isEmpty());
        assertEquals(12, recipes.size());

        for (MachineRecipe recipe : recipes) {
            assertEquals(500, recipe.getEnergyCost());
            assertEquals(50, recipe.getProcessTicks());
            assertTrue(recipe.getSlot0InputCount() > 0);
            assertEquals(1, recipe.getSlot1InputCount());
            assertNotNull(recipe.getTitle());
        }
    }

    @Test
    void testDesalinationFilterRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:desalination_filter");
        assertFalse(recipes.isEmpty());
        assertEquals(1, recipes.size());

        MachineRecipe r = recipes.get(0);
        assertEquals(80, r.getEnergyCost());
        assertEquals(80, r.getProcessTicks());
        assertEquals(2, r.getSlot0InputCount());
        assertEquals(1, r.getSlot1InputCount());
        assertNotNull(r.getTitle());
    }

    @Test
    void testUnknownMachineRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:unknown");
        assertTrue(recipes.isEmpty());
    }
}
