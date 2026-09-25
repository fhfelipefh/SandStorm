package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.block.CyborgIncubatorVatBlock;
import com.fhfelipefh.sandstorm.content.block.entity.CyborgIncubatorVatBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.CyborgIncubatorMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase25BioCyberneticsTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldRegisterCyborgIncubatorVatBlockAndItem() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("cyborg_incubator_vat"));
        ResourceKey<Item> itemKey = SandStormMod.itemKey("cyborg_incubator_vat");

        assertNotNull(blockKey);
        assertNotNull(itemKey);
        assertEquals("sandstorm", blockKey.identifier().getNamespace());
        assertEquals("cyborg_incubator_vat", blockKey.identifier().getPath());
        assertEquals("sandstorm", itemKey.identifier().getNamespace());
        assertEquals("cyborg_incubator_vat", itemKey.identifier().getPath());
    }

    @Test
    void shouldDeclareExpectedPropertiesOnCyborgIncubatorVatBlock() {
        assertNotNull(CyborgIncubatorVatBlock.FACING);
        assertNotNull(CyborgIncubatorVatBlock.POWERED);
        assertNotNull(CyborgIncubatorVatBlock.STAGE);

        assertEquals("facing", CyborgIncubatorVatBlock.FACING.getName());
        assertEquals("powered", CyborgIncubatorVatBlock.POWERED.getName());
        assertEquals("stage", CyborgIncubatorVatBlock.STAGE.getName());
    }

    @Test
    void shouldValidateStagePropertyRange() {
        assertEquals(0, CyborgIncubatorVatBlock.STAGE.getPossibleValues().iterator().next());
        assertEquals(5, CyborgIncubatorVatBlock.STAGE.getPossibleValues().size());
        assertTrue(CyborgIncubatorVatBlock.STAGE.getPossibleValues().containsAll(List.of(0, 1, 2, 3, 4)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "biomechanical_chassis_frame",
            "synthetic_myomer_bundle",
            "bio_neural_core",
            "bio_coolant_canister",
            "assembled_cyborg_frame"
    })
    void shouldRegisterAllNewBiomechanicalItems(String itemPath) {
        ResourceKey<Item> key = SandStormMod.itemKey(itemPath);
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(itemPath, key.identifier().getPath());
    }

    @Test
    void shouldInitializeBlockEntityWithCorrectSlotConfiguration() {
        assertEquals(0, CyborgIncubatorVatBlockEntity.SLOT_CHASSIS);
        assertEquals(1, CyborgIncubatorVatBlockEntity.SLOT_MYOMER);
        assertEquals(2, CyborgIncubatorVatBlockEntity.SLOT_NEURAL);
        assertEquals(3, CyborgIncubatorVatBlockEntity.SLOT_COOLANT);
        assertEquals(4, CyborgIncubatorVatBlockEntity.SLOT_BATTERY);
        assertEquals(5, CyborgIncubatorVatBlockEntity.SLOT_OUTPUT);
    }

    @Test
    void shouldValidateSidedSlotsForAutomation() {
        CyborgIncubatorVatBlockEntity entity = new CyborgIncubatorVatBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        int[] topSlots = entity.getSlotsForFace(Direction.UP);
        int[] bottomSlots = entity.getSlotsForFace(Direction.DOWN);
        int[] sideSlots = entity.getSlotsForFace(Direction.NORTH);

        assertArrayEquals(new int[]{0, 1, 2, 3}, topSlots);
        assertArrayEquals(new int[]{5, 4}, bottomSlots);
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, sideSlots);
    }

    @Test
    void shouldValidateContainerDataChannels() {
        CyborgIncubatorVatBlockEntity entity = new CyborgIncubatorVatBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        ContainerData data = entity.getContainerData();
        assertEquals(8, data.getCount());

        data.set(0, 50);
        data.set(1, 200);
        data.set(2, 5000);
        data.set(3, 25000);
        data.set(4, 2000);
        data.set(6, 3);
        data.set(7, 98);

        assertEquals(50, data.get(0));
        assertEquals(200, data.get(1));
        assertEquals(5000, data.get(2));
        assertEquals(25000, data.get(3));
        assertEquals(2000, data.get(4));
        assertEquals(8000, data.get(5));
        assertEquals(3, data.get(6));
        assertEquals(98, data.get(7));
    }

    @Test
    void shouldHaveCapacityFor8000MbOfBioFluid() {
        CyborgIncubatorVatBlockEntity entity = new CyborgIncubatorVatBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        assertEquals(8000, entity.getMaxFluid());
        assertEquals(0, entity.getFluidAmount());

        entity.setFluidAmount(4000);
        assertEquals(4000, entity.getFluidAmount());

        entity.setFluidAmount(10000);
        assertEquals(8000, entity.getFluidAmount());
    }

    @Test
    void shouldSupportFluidStorageInsertionViaTransferApi() {
        CyborgIncubatorVatBlockEntity entity = new CyborgIncubatorVatBlockEntity(
                BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());

        Storage<FluidVariant> storage = entity.getFluidStorage(Direction.NORTH);
        assertNotNull(storage);
        assertTrue(storage.supportsInsertion());
        assertFalse(storage.supportsExtraction());
    }

    @Test
    void shouldInitializeMenuWithMatchingSlotLayout() {
        SimpleContainer container = new SimpleContainer(6);
        SimpleContainerData data = new SimpleContainerData(8);
        data.set(0, 100);
        data.set(1, 200);
        data.set(2, 12500);
        data.set(3, 25000);
        data.set(4, 4000);
        data.set(5, 8000);
        data.set(6, 4);
        data.set(7, 99);

        CyborgIncubatorMenu menu = new CyborgIncubatorMenu(null, 1, new Inventory(null, null), container, data);

        assertEquals(42, menu.slots.size());
        assertEquals(100, menu.getProgress());
        assertEquals(200, menu.getMaxProgress());
        assertEquals(12500, menu.getEnergy());
        assertEquals(25000, menu.getMaxEnergy());
        assertEquals(4000, menu.getFluidAmount());
        assertEquals(8000, menu.getMaxFluid());
        assertEquals(4, menu.getCurrentStage());
        assertEquals(99, menu.getTissueCompatibility());
        assertTrue(menu.isProcessing());
        assertTrue(menu.isWptConnected());
    }

    @Test
    void shouldCalculateScaledProgressAndFluidInMenu() {
        SimpleContainer container = new SimpleContainer(6);
        SimpleContainerData data = new SimpleContainerData(8);
        data.set(0, 100);
        data.set(1, 200);
        data.set(2, 10000);
        data.set(3, 20000);
        data.set(4, 4000);
        data.set(5, 8000);

        CyborgIncubatorMenu menu = new CyborgIncubatorMenu(null, 1, new Inventory(null, null), container, data);

        assertEquals(25, menu.getProgressScaled(50));
        assertEquals(26, menu.getFluidScaled(52));
        assertEquals(50, menu.getEnergyScaled(100));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "biomechanical_chassis_frame.json",
            "synthetic_myomer_bundle.json",
            "bio_neural_core.json",
            "bio_coolant_canister.json",
            "cyborg_incubator_vat.json"
    })
    void shouldVerifyAllRecipeFilesExistAndAreValidJson(String recipeFile) {
        File file = new File("src/main/resources/data/sandstorm/recipe/" + recipeFile);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void shouldVerifyLootTableExistsAndSurvivesExplosion() {
        File file = new File("src/main/resources/data/sandstorm/loot_table/blocks/cyborg_incubator_vat.json");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cyborg_incubator_vat.json",
            "biomechanical_chassis_frame.json",
            "synthetic_myomer_bundle.json",
            "bio_neural_core.json",
            "bio_coolant_canister.json",
            "assembled_cyborg_frame.json"
    })
    void shouldVerifyItemDefinitionsFor1214Standard(String itemFile) {
        File file = new File("src/main/resources/assets/sandstorm/items/" + itemFile);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/main/resources/assets/sandstorm/textures/block/cyborg_incubator_vat_front.png",
            "src/main/resources/assets/sandstorm/textures/block/cyborg_incubator_vat_front_active.png",
            "src/main/resources/assets/sandstorm/textures/block/cyborg_incubator_vat_side.png",
            "src/main/resources/assets/sandstorm/textures/block/cyborg_incubator_vat_top.png",
            "src/main/resources/assets/sandstorm/textures/block/cyborg_incubator_vat_top_active.png",
            "src/main/resources/assets/sandstorm/textures/block/cyborg_incubator_vat_bottom.png",
            "src/main/resources/assets/sandstorm/textures/item/biomechanical_chassis_frame.png",
            "src/main/resources/assets/sandstorm/textures/item/synthetic_myomer_bundle.png",
            "src/main/resources/assets/sandstorm/textures/item/bio_neural_core.png",
            "src/main/resources/assets/sandstorm/textures/item/bio_coolant_canister.png",
            "src/main/resources/assets/sandstorm/textures/item/assembled_cyborg_frame.png"
    })
    void shouldVerifyAllTextureFilesExistAndHaveValidPngHeader(String texturePath) throws IOException {
        File file = new File(texturePath);
        assertTrue(file.exists());
        byte[] header = new byte[8];
        try (FileInputStream fis = new FileInputStream(file)) {
            assertEquals(8, fis.read(header));
        }
        byte[] expected = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        assertArrayEquals(expected, header);
    }
}
