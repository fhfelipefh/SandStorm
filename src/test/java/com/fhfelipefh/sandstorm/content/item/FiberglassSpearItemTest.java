package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FiberglassSpearItemTest {

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

        assertNotNull(SandStormItems.FIBERGLASS_SPEAR);

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
    void shouldRegisterFiberglassSpearItemAndEntity() {
        assertNotNull(SandStormItems.FIBERGLASS_SPEAR);
        assertNotNull(SandStormEntities.FIBERGLASS_SPEAR);
        assertNotNull(SandStormSoundEvents.FIBERGLASS_SPEAR_THROW);
        assertNotNull(SandStormSoundEvents.FIBERGLASS_SPEAR_SHATTER);
    }

    @Test
    void shouldHaveSpearAnimationAndUseDuration() {
        FiberglassSpearItem spear = SandStormItems.FIBERGLASS_SPEAR;
        assertEquals(ItemUseAnimation.SPEAR, spear.getUseAnimation(ItemStack.EMPTY));
        assertEquals(72000, spear.getUseDuration(ItemStack.EMPTY, null));
    }

    @Test
    void shouldHaveAssetsAndRecipeFiles() {
        Path itemDef = Path.of("src", "main", "resources", "assets", "sandstorm", "items", "fiberglass_spear.json");
        Path itemModel = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "fiberglass_spear.json");
        Path texture = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "fiberglass_spear.png");
        Path recipe = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "fiberglass_spear.json");
        Path throwSound = Path.of("src", "main", "resources", "assets", "sandstorm", "sounds", "fiberglass_spear_throw.ogg");
        Path shatterSound = Path.of("src", "main", "resources", "assets", "sandstorm", "sounds", "fiberglass_spear_shatter.ogg");

        assertTrue(Files.exists(itemDef));
        assertTrue(Files.exists(itemModel));
        assertTrue(Files.exists(texture));
        assertTrue(Files.exists(recipe));
        assertTrue(Files.exists(throwSound));
        assertTrue(Files.exists(shatterSound));
    }
}
