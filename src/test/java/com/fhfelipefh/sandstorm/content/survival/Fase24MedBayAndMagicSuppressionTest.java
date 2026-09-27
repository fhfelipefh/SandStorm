package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.block.BioRegenerationPodBlock;
import com.fhfelipefh.sandstorm.content.block.entity.BioRegenerationPodBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.BioRegenerationPodMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
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
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase24MedBayAndMagicSuppressionTest {

    private static final Path ASSETS_DIR = Path.of("src", "main", "resources", "assets", "sandstorm");
    private static final Path RECIPES_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "recipe");
    private static final Path MINECRAFT_RECIPES_DIR = Path.of("src", "main", "resources", "data", "minecraft", "recipe");
    private static final Path LOOT_DIR = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "blocks");
    private static final byte[] PNG_HEADER = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

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
    void shouldRegisterKeysForBioRegenerationPodAndMedBaySeat() {
        ResourceKey<?> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("bio_regeneration_pod"));
        ResourceKey<?> itemKey = SandStormMod.itemKey("bio_regeneration_pod");
        ResourceKey<?> entityKey = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id("medbay_seat"));
        ResourceKey<?> menuKey = ResourceKey.create(Registries.MENU, SandStormMod.id("bio_regeneration_pod_menu"));

        assertNotNull(blockKey);
        assertNotNull(itemKey);
        assertNotNull(entityKey);
        assertNotNull(menuKey);

        assertEquals("sandstorm", blockKey.identifier().getNamespace());
        assertEquals("bio_regeneration_pod", blockKey.identifier().getPath());
        assertEquals("bio_regeneration_pod", itemKey.identifier().getPath());
        assertEquals("medbay_seat", entityKey.identifier().getPath());
        assertEquals("bio_regeneration_pod_menu", menuKey.identifier().getPath());
    }

    @Test
    void shouldValidateBioRegenerationPodBlockProperties() {
        assertNotNull(BioRegenerationPodBlock.FACING);
        assertEquals("facing", BioRegenerationPodBlock.FACING.getName());
        assertNotNull(BioRegenerationPodBlock.OCCUPIED);
        assertEquals("occupied", BioRegenerationPodBlock.OCCUPIED.getName());
        assertNotNull(BioRegenerationPodBlock.POWERED);
        assertEquals("powered", BioRegenerationPodBlock.POWERED.getName());
    }

    @Test
    void shouldExposeFluidStorageForBioRegenerationPod() {
        BioRegenerationPodBlockEntity be = new BioRegenerationPodBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertNotNull(be.getFluidStorage(Direction.UP));
        assertNotNull(be.getFluidStorage(Direction.NORTH));
    }

    @Test
    void shouldInitializeBioRegenerationPodBlockEntityAndVerifyState() {
        BioRegenerationPodBlockEntity be = new BioRegenerationPodBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(4, be.getContainerSize());
        assertEquals(0, be.getFluidAmount());
        assertEquals(4000, be.getMaxFluid());
        assertFalse(be.isOccupied());

        be.setFluidAmount(2500);
        assertEquals(2500, be.getFluidAmount());

        be.setFluidAmount(99999);
        assertEquals(4000, be.getFluidAmount());

        be.setFluidAmount(-100);
        assertEquals(0, be.getFluidAmount());

        be.setOccupied(true);
        assertTrue(be.isOccupied());

        assertEquals(8, be.getPodData().getCount());
        be.getPodData().set(0, 1500);
        be.getPodData().set(1, 20000);
        be.getPodData().set(2, 3000);
        be.getPodData().set(5, 1);
        be.getPodData().set(6, 80);
        be.getPodData().set(7, 95);

        assertEquals(1500, be.getPodData().get(0));
        assertEquals(20000, be.getPodData().get(1));
        assertEquals(3000, be.getPodData().get(2));
        assertEquals(4000, be.getPodData().get(3));
        assertEquals(1, be.getPodData().get(5));
        assertEquals(80, be.getPodData().get(6));
        assertEquals(95, be.getPodData().get(7));
    }

    @Test
    void shouldVerifySlotAutomationRulesForBlockEntity() {
        BioRegenerationPodBlockEntity be = new BioRegenerationPodBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        int[] topSlots = be.getSlotsForFace(Direction.UP);
        int[] bottomSlots = be.getSlotsForFace(Direction.DOWN);
        int[] sideSlots = be.getSlotsForFace(Direction.NORTH);

        assertEquals(2, topSlots.length);
        assertEquals(2, bottomSlots.length);
        assertEquals(3, sideSlots.length);

        assertTrue(be.canTakeItemThroughFace(BioRegenerationPodBlockEntity.SLOT_OUTPUT, new ItemStack(Items.GLASS_BOTTLE), Direction.DOWN));
        assertFalse(be.canTakeItemThroughFace(BioRegenerationPodBlockEntity.SLOT_WATER, new ItemStack(Items.POTION), Direction.DOWN));
    }

    @Test
    void shouldVerifyBioRegenerationPodMenuInstantiationAndData() {
        Inventory playerInv = new Inventory(null, null);
        SimpleContainer container = new SimpleContainer(4);
        SimpleContainerData data = new SimpleContainerData(8);
        data.set(0, 1000);
        data.set(1, 20000);
        data.set(2, 2000);
        data.set(3, 4000);
        data.set(4, 1);
        data.set(5, 1);
        data.set(6, 75);
        data.set(7, 90);

        BioRegenerationPodMenu menu = new BioRegenerationPodMenu(null, 1, playerInv, container, data);
        assertEquals(40, menu.slots.size());
        assertEquals(1000, menu.getEnergy());
        assertEquals(20000, menu.getMaxEnergy());
        assertEquals(2000, menu.getFluidAmount());
        assertEquals(4000, menu.getMaxFluid());
        assertTrue(menu.isWptConnected());
        assertTrue(menu.isOccupied());
        assertEquals(75, menu.getHeartRate());
        assertEquals(90, menu.getHealthPercent());
        assertEquals(20, menu.getFluidScaled(40));
        assertFalse(menu.slots.get(BioRegenerationPodBlockEntity.SLOT_OUTPUT).mayPlace(ItemStack.EMPTY));
    }

    @Test
    void shouldVerifyMagicSuppressionHandlerBlockInterception() {
        assertTrue(MagicSuppressionHandler.shouldSuppressBlock(Blocks.ENCHANTING_TABLE));
        assertTrue(MagicSuppressionHandler.shouldSuppressBlock(Blocks.BREWING_STAND));
        assertTrue(MagicSuppressionHandler.shouldSuppressBlock(Blocks.ANVIL));
        assertTrue(MagicSuppressionHandler.shouldSuppressBlock(Blocks.CHIPPED_ANVIL));
        assertTrue(MagicSuppressionHandler.shouldSuppressBlock(Blocks.DAMAGED_ANVIL));
        assertFalse(MagicSuppressionHandler.shouldSuppressBlock(Blocks.CRAFTING_TABLE));
        assertFalse(MagicSuppressionHandler.shouldSuppressBlock(Blocks.FURNACE));
        assertFalse(MagicSuppressionHandler.shouldSuppressBlock(null));
    }

    @Test
    void shouldVerifyMagicSuppressionHandlerItemInterception() {
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.ENCHANTING_TABLE));
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.BREWING_STAND));
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.ENCHANTED_BOOK));
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.ANVIL));
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.CHIPPED_ANVIL));
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.DAMAGED_ANVIL));
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.SPLASH_POTION));
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.LINGERING_POTION));
        assertTrue(MagicSuppressionHandler.shouldSuppressItem(Items.EXPERIENCE_BOTTLE));
        assertFalse(MagicSuppressionHandler.shouldSuppressItem(Items.IRON_INGOT));
        assertFalse(MagicSuppressionHandler.shouldSuppressItem(null));
    }

    @Test
    void shouldVerifyMagicSuppressionHandlerEntityTypeInterception() {
        EntityType<?> witchType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "witch"));
        EntityType<?> zombieType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "zombie"));
        assertTrue(MagicSuppressionHandler.shouldSuppressEntityType(witchType));
        assertFalse(MagicSuppressionHandler.shouldSuppressEntityType(zombieType));
        assertFalse(MagicSuppressionHandler.shouldSuppressEntityType(null));
    }

    @Test
    void shouldVerifyAllOverriddenVanillaRecipesExist() {
        String[] suppressedRecipes = {
                "enchanting_table.json",
                "brewing_stand.json",
                "anvil.json",
                "chipped_anvil.json",
                "damaged_anvil.json"
        };

        for (String recipeName : suppressedRecipes) {
            Path recipePath = MINECRAFT_RECIPES_DIR.resolve(recipeName);
            assertTrue(Files.exists(recipePath), "Overridden vanilla recipe must exist: " + recipePath);
        }
    }

    @Test
    void shouldVerifyAssetsAndTexturesForBioRegenerationPod() throws IOException {
        Path blockstate = ASSETS_DIR.resolve("blockstates").resolve("bio_regeneration_pod.json");
        Path blockModel = ASSETS_DIR.resolve("models").resolve("block").resolve("bio_regeneration_pod.json");
        Path blockModelActive = ASSETS_DIR.resolve("models").resolve("block").resolve("bio_regeneration_pod_active.json");
        Path itemModel = ASSETS_DIR.resolve("models").resolve("item").resolve("bio_regeneration_pod.json");
        Path itemDef = ASSETS_DIR.resolve("items").resolve("bio_regeneration_pod.json");
        Path lootTable = LOOT_DIR.resolve("bio_regeneration_pod.json");
        Path recipe = RECIPES_DIR.resolve("bio_regeneration_pod.json");

        assertTrue(Files.exists(blockstate), "Blockstate JSON must exist");
        assertTrue(Files.exists(blockModel), "Block model JSON must exist");
        assertTrue(Files.exists(blockModelActive), "Block active model JSON must exist");
        assertTrue(Files.exists(itemModel), "Item model JSON must exist");
        assertTrue(Files.exists(itemDef), "Item def JSON must exist");
        assertTrue(Files.exists(lootTable), "Loot table JSON must exist");
        assertTrue(Files.exists(recipe), "Recipe JSON must exist");

        String[] textures = {
                "bio_regeneration_pod_front.png",
                "bio_regeneration_pod_front_active.png",
                "bio_regeneration_pod_top.png",
                "bio_regeneration_pod_top_active.png",
                "bio_regeneration_pod_side.png",
                "bio_regeneration_pod_bottom.png"
        };

        for (String texName : textures) {
            Path texPath = ASSETS_DIR.resolve("textures").resolve("block").resolve(texName);
            assertTrue(Files.exists(texPath), "Texture must exist: " + texName);
            byte[] header = new byte[8];
            try (FileInputStream fis = new FileInputStream(texPath.toFile())) {
                assertEquals(8, fis.read(header));
                assertArrayEquals(PNG_HEADER, header, "Valid PNG header required for " + texName);
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"pt_br", "en_us", "es_es"})
    void shouldVerifyLocalizationKeysPresentAcrossAllLanguages(String langCode) throws IOException {
        Path langPath = ASSETS_DIR.resolve("lang").resolve(langCode + ".json");
        assertTrue(Files.exists(langPath));

        try (FileReader reader = new FileReader(langPath.toFile())) {
            JsonElement parsed = JsonParser.parseReader(reader);
            assertTrue(parsed.isJsonObject());
            JsonObject json = parsed.getAsJsonObject();

            assertTrue(json.has("block.sandstorm.bio_regeneration_pod"));
            assertTrue(json.has("item.sandstorm.bio_regeneration_pod"));
            assertTrue(json.has("container.sandstorm.bio_regeneration_pod"));
            assertTrue(json.has("tooltip.sandstorm.bio_regeneration_pod_desc"));
            assertTrue(json.has("telemetry.sandstorm.medbay_entered"));
            assertTrue(json.has("telemetry.sandstorm.magic_suppressed"));
            assertTrue(json.has("telemetry.sandstorm.magic_suppressed_potions"));
            assertTrue(json.has("telemetry.sandstorm.anvil_redirect"));
        }
    }
}
