package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.client.renderer.NomadScavengerModel;
import com.fhfelipefh.sandstorm.content.gui.NomadScavengerMenu;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NomadScavengerTest {

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
        frozenField.set(BuiltInRegistries.MENU, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.MENU, new IdentityHashMap<>());

        assertNotNull(SandStormItems.SCRAP_METAL);

        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldBuildNomadScavengerAttributes() {
        AttributeSupplier.Builder builder = NomadScavengerEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(30.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.28, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(8.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(24.0, supplier.getBaseValue(Attributes.FOLLOW_RANGE), 0.001);
    }

    @Test
    void shouldGenerateValidModelLayerDefinition() {
        LayerDefinition layer = NomadScavengerModel.createBodyLayer();
        assertNotNull(layer);
        assertNotNull(layer.bakeRoot());
    }

    @Test
    void shouldVerifyEntityKeyAndIdentifier() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("nomad_scavenger"));
        assertNotNull(key);
        assertEquals("nomad_scavenger", key.identifier().getPath());
        assertEquals("sandstorm", key.identifier().getNamespace());
    }

    @Test
    void shouldVerifyTradeCostsAndRewards() {
        assertEquals(1, NomadScavengerMenu.getWaterCost(NomadScavengerMenu.TRADE_SCRAP_METAL));
        assertEquals(2, NomadScavengerMenu.getWaterCost(NomadScavengerMenu.TRADE_CIRCUIT_BOARD));
        assertEquals(3, NomadScavengerMenu.getWaterCost(NomadScavengerMenu.TRADE_ELECTRIC_COMPONENT));
        assertEquals(4, NomadScavengerMenu.getWaterCost(NomadScavengerMenu.TRADE_TECH_DISC));

        assertEquals(2, NomadScavengerMenu.getRewardItem(NomadScavengerMenu.TRADE_SCRAP_METAL).getCount());
        assertEquals(1, NomadScavengerMenu.getRewardItem(NomadScavengerMenu.TRADE_CIRCUIT_BOARD).getCount());
        assertEquals(1, NomadScavengerMenu.getRewardItem(NomadScavengerMenu.TRADE_ELECTRIC_COMPONENT).getCount());
        assertEquals(1, NomadScavengerMenu.getRewardItem(NomadScavengerMenu.TRADE_TECH_DISC).getCount());
    }

    @Test
    void shouldVerifyLootTableExists() {
        Path lootPath = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "entities", "nomad_scavenger.json");
        assertTrue(Files.exists(lootPath), "Nomad Scavenger loot table must exist");
    }

    @Test
    void shouldVerifyTextureExists() {
        Path texturePath = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "nomad_scavenger", "nomad_scavenger.png");
        assertTrue(Files.exists(texturePath), "Nomad Scavenger entity texture must exist");
    }

    @Test
    void shouldCreateNomadScavengerMenuAndVerifySlots() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(2);
        SimpleContainerData data = new SimpleContainerData(1);
        NomadScavengerMenu menu = new NomadScavengerMenu(null, 0, playerInv, container, data);

        assertEquals(38, menu.slots.size());
        assertEquals(0, menu.getSelectedTrade());
        menu.setSelectedTrade(1);
        assertEquals(1, menu.getSelectedTrade());
        assertFalse(menu.slots.get(1).mayPlace(ItemStack.EMPTY));
        assertFalse(menu.clickMenuButton(null, -1));
        assertFalse(menu.clickMenuButton(null, 99));
        assertTrue(menu.clickMenuButton(null, 2));
        assertEquals(2, menu.getSelectedTrade());
    }
}
