package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.MolecularModifierBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.MolecularModifierMenu;
import com.fhfelipefh.sandstorm.content.item.MolecularUpgradeItem;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase23MolecularModifierTest {

    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path RECIPES_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");
    private static final Path LOOT_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "blocks");

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldRegisterMolecularModifierBlockKeys() {
        ResourceKey<?> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("molecular_modifier"));
        ResourceKey<?> itemKey = SandStormMod.itemKey("molecular_modifier");
        assertNotNull(blockKey);
        assertNotNull(itemKey);
        assertEquals("sandstorm", blockKey.identifier().getNamespace());
        assertEquals("molecular_modifier", blockKey.identifier().getPath());
        assertEquals("molecular_modifier", itemKey.identifier().getPath());
    }

    @ParameterizedTest
    @EnumSource(MolecularUpgradeItem.UpgradeType.class)
    void shouldVerifyAllUpgradeTypesHaveValidAttributes(MolecularUpgradeItem.UpgradeType type) {
        assertNotNull(type.getId());
        assertFalse(type.getId().isEmpty());
        assertNotNull(type.getCategory());
        assertNotNull(type.getSlotType());
        assertNotNull(type.getEnchantmentKey());
        assertTrue(type.getLevel() >= 1);
        ResourceKey<Item> key = SandStormMod.itemKey(type.getId());
        assertNotNull(key);
        assertEquals(type.getId(), key.identifier().getPath());
    }

    @Test
    void shouldHaveTwelveUpgradeTypes() {
        assertEquals(12, MolecularUpgradeItem.UpgradeType.values().length);
    }

    @Test
    void shouldVerifyCategoryApplicability() {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack pickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
        ItemStack suit = new ItemStack(Items.IRON_CHESTPLATE);
        ItemStack boots = new ItemStack(Items.IRON_BOOTS);

        assertTrue(MolecularUpgradeItem.Category.WEAPON.isApplicableTo(sword));
        assertFalse(MolecularUpgradeItem.Category.WEAPON.isApplicableTo(pickaxe));

        assertTrue(MolecularUpgradeItem.Category.TOOL.isApplicableTo(pickaxe));
        assertFalse(MolecularUpgradeItem.Category.TOOL.isApplicableTo(sword));

        assertTrue(MolecularUpgradeItem.Category.ARMOR.isApplicableTo(suit));
        assertTrue(MolecularUpgradeItem.Category.ARMOR.isApplicableTo(boots));
        assertFalse(MolecularUpgradeItem.Category.ARMOR.isApplicableTo(sword));

        assertTrue(MolecularUpgradeItem.Category.BOOTS.isApplicableTo(boots));
        assertFalse(MolecularUpgradeItem.Category.BOOTS.isApplicableTo(suit));

        assertTrue(MolecularUpgradeItem.Category.UNIVERSAL.isApplicableTo(sword));
        assertTrue(MolecularUpgradeItem.Category.UNIVERSAL.isApplicableTo(pickaxe));
        assertTrue(MolecularUpgradeItem.Category.UNIVERSAL.isApplicableTo(suit));
        assertTrue(MolecularUpgradeItem.Category.UNIVERSAL.isApplicableTo(boots));
    }

    @Test
    void shouldInitializeMolecularModifierBlockEntityWithCorrectSlotsAndState() {
        MolecularModifierBlockEntity be = new MolecularModifierBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(7, be.getContainerSize());
        assertTrue(be.isEmpty());
        assertEquals(0, be.getEnergy());
        assertEquals(0, be.getProgress());
        assertFalse(be.isProcessing());
        assertNotNull(be.getDisplayName());
        assertFalse(be.canProcess());
    }

    @Test
    void shouldVerifySlotFacesAndAutomationRules() {
        MolecularModifierBlockEntity be = new MolecularModifierBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(1, be.getSlotsForFace(Direction.UP).length);
        assertEquals(2, be.getSlotsForFace(Direction.DOWN).length);
        assertEquals(5, be.getSlotsForFace(Direction.NORTH).length);

        assertFalse(be.canPlaceItemThroughFace(MolecularModifierBlockEntity.SLOT_OUTPUT, new ItemStack(Items.IRON_INGOT), Direction.UP));
        assertTrue(be.canTakeItemThroughFace(MolecularModifierBlockEntity.SLOT_OUTPUT, new ItemStack(Items.IRON_INGOT), Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(MolecularModifierBlockEntity.SLOT_TARGET, new ItemStack(Items.IRON_INGOT), Direction.DOWN));
    }

    @Test
    void shouldVerifyEnergyAndCanProcessConditions() {
        MolecularModifierBlockEntity be = new MolecularModifierBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertFalse(be.canProcess());

        be.setItem(MolecularModifierBlockEntity.SLOT_TARGET, new ItemStack(Items.DIAMOND_SWORD));
        assertFalse(be.canProcess());

        be.setEnergy(1000);
        assertFalse(be.canProcess());

        be.setItem(MolecularModifierBlockEntity.SLOT_OUTPUT, new ItemStack(Items.IRON_SWORD));
        assertFalse(be.canProcess());

        be.clearContent();
        assertTrue(be.isEmpty());
    }

    @Test
    void shouldInstantiateMenuAndVerifyContainerData() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(7);
        SimpleContainerData data = new SimpleContainerData(6);
        data.set(0, 500);
        data.set(1, 10000);
        data.set(2, 25);
        data.set(3, 50);
        data.set(4, 1);
        data.set(5, 1);

        MolecularModifierMenu menu = new MolecularModifierMenu(null, 1, playerInv, container, data);
        assertEquals(500, menu.getEnergy());
        assertEquals(10000, menu.getMaxEnergy());
        assertEquals(25, menu.getProgress());
        assertEquals(50, menu.getMaxProgress());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isProcessing());
        assertEquals(50, menu.getProgressScaled(100));
        assertFalse(menu.slots.get(6).mayPlace(ItemStack.EMPTY));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "molecular_modifier",
            "vibro_resonator_module",
            "thermal_plasma_emitter",
            "kinetic_focus_module",
            "cavitation_frequency_core",
            "atomic_phase_disrupter",
            "spectrometric_sifter",
            "self_healing_nanite_matrix",
            "titanium_lattice_coating",
            "ballistic_dampener_mesh",
            "ablative_thermal_plating",
            "pneumatic_fall_dampers",
            "reactive_shock_plating"
    })
    void shouldHaveValidAssetsForFase23Items(String itemName) {
        Path itemDef = ASSETS_DIR.resolve("items").resolve(itemName + ".json");
        Path modelDef = ASSETS_DIR.resolve("models").resolve("item").resolve(itemName + ".json");
        Path textureDef = ASSETS_DIR.resolve("textures").resolve("item").resolve(itemName + ".png");

        assertTrue(Files.exists(itemDef), "Item definition JSON must exist: " + itemDef);
        assertTrue(Files.exists(modelDef), "Item model JSON must exist: " + modelDef);
        assertTrue(Files.exists(textureDef), "Item texture PNG must exist: " + textureDef);
    }

    @Test
    void shouldHaveValidBlockstateAndModelForMolecularModifier() {
        Path blockstate = ASSETS_DIR.resolve("blockstates").resolve("molecular_modifier.json");
        Path blockModel = ASSETS_DIR.resolve("models").resolve("block").resolve("molecular_modifier.json");
        Path lootTable = LOOT_DIR.resolve("molecular_modifier.json");

        assertTrue(Files.exists(blockstate), "Blockstate JSON must exist");
        assertTrue(Files.exists(blockModel), "Block model JSON must exist");
        assertTrue(Files.exists(lootTable), "Loot table JSON must exist");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "molecular_modifier",
            "vibro_resonator_module",
            "thermal_plasma_emitter",
            "kinetic_focus_module",
            "cavitation_frequency_core",
            "atomic_phase_disrupter",
            "spectrometric_sifter",
            "self_healing_nanite_matrix",
            "titanium_lattice_coating",
            "ballistic_dampener_mesh",
            "ablative_thermal_plating",
            "pneumatic_fall_dampers",
            "reactive_shock_plating"
    })
    void shouldHaveValidCraftingRecipesForFase23Items(String itemName) {
        Path recipeFile = RECIPES_DIR.resolve(itemName + ".json");
        assertTrue(Files.exists(recipeFile), "Crafting recipe must exist: " + recipeFile);
    }
}
