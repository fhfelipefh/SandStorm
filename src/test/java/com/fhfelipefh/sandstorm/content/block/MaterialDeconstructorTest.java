package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.MaterialDeconstructorBlockEntity;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipeRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialDeconstructorTest {

    @BeforeAll
    static void setup() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

        Class.forName(SandStormItems.class.getName());
        Class.forName(SandStormBlocks.class.getName());

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    @Test
    void shouldInitializeWithCorrectDefaults() {
        MaterialDeconstructorBlockEntity be = new MaterialDeconstructorBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState()
        );
        assertEquals(3, be.getContainerSize());
        assertEquals(10000, be.getMaxEnergy());
        assertEquals(0, be.getProgress());
    }

    @Test
    void shouldFindRegisteredDeconstructionRecipes() {
        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipes("sandstorm:material_deconstructor");
        assertNotNull(recipes);
        assertFalse(recipes.isEmpty());

        boolean hasCopper = false;
        boolean hasSandstone = false;
        for (MachineRecipe recipe : recipes) {
            ItemStack out = recipe.getOutput();
            if (out.is(Items.COPPER_INGOT)) {
                hasCopper = true;
            }
            if (out.is(Items.SANDSTONE)) {
                hasSandstone = true;
            }
        }
        assertTrue(hasCopper);
        assertTrue(hasSandstone);
    }
}
