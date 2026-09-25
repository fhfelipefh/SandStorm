package com.fhfelipefh.sandstorm.content.defense;

import com.fhfelipefh.sandstorm.content.block.entity.AcousticDefensePylonBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.KineticRailgunBlockEntity;
import com.fhfelipefh.sandstorm.content.block.entity.PlasmaShieldGeneratorBlockEntity;
import net.minecraft.world.item.Items;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase30PlasmaDefenseTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldRegisterPhase30BlockAndItemKeys() {
        ResourceKey<Block> shieldBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("plasma_shield_generator"));
        ResourceKey<Block> railgunBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("kinetic_railgun"));
        ResourceKey<Block> pylonBlock = ResourceKey.create(Registries.BLOCK, SandStormMod.id("acoustic_defense_pylon"));

        ResourceKey<Item> shieldItem = SandStormMod.itemKey("plasma_shield_generator");
        ResourceKey<Item> railgunItem = SandStormMod.itemKey("kinetic_railgun");
        ResourceKey<Item> pylonItem = SandStormMod.itemKey("acoustic_defense_pylon");
        ResourceKey<Item> slugItem = SandStormMod.itemKey("kinetic_slug");
        ResourceKey<Item> toroidItem = SandStormMod.itemKey("superconductor_toroid");
        ResourceKey<Item> crystalItem = SandStormMod.itemKey("plasma_focus_crystal");

        assertNotNull(shieldBlock);
        assertNotNull(railgunBlock);
        assertNotNull(pylonBlock);

        assertNotNull(shieldItem);
        assertNotNull(railgunItem);
        assertNotNull(pylonItem);
        assertNotNull(slugItem);
        assertNotNull(toroidItem);
        assertNotNull(crystalItem);

        assertEquals("plasma_shield_generator", shieldBlock.identifier().getPath());
        assertEquals("kinetic_railgun", railgunBlock.identifier().getPath());
        assertEquals("acoustic_defense_pylon", pylonBlock.identifier().getPath());

        assertEquals("kinetic_slug", slugItem.identifier().getPath());
        assertEquals("superconductor_toroid", toroidItem.identifier().getPath());
        assertEquals("plasma_focus_crystal", crystalItem.identifier().getPath());
    }

    @Test
    void shouldTestAcousticDefenseTracker() {
        ResourceKey<Level> overworld = Level.OVERWORLD;
        ResourceKey<Level> nether = Level.NETHER;
        BlockPos pylonPos = new BlockPos(100, 64, 100);

        AcousticDefenseTracker.registerPylon(overworld, pylonPos, 32.0);

        assertTrue(AcousticDefenseTracker.isInsideAcousticDamping(overworld, pylonPos));
        assertTrue(AcousticDefenseTracker.isInsideAcousticDamping(overworld, new BlockPos(120, 64, 100)));
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(overworld, new BlockPos(150, 64, 100)));
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(nether, pylonPos));

        AcousticDefenseTracker.unregisterPylon(overworld, pylonPos);
        assertFalse(AcousticDefenseTracker.isInsideAcousticDamping(overworld, pylonPos));
    }

    @Test
    void shouldTestPlasmaShieldBlockEntityState() {
        PlasmaShieldGeneratorBlockEntity be = new PlasmaShieldGeneratorBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(1000000, be.getStoredEnergy());
        assertEquals(1000000, be.getMaxEnergy());

        be.setStoredEnergy(500000);
        assertEquals(500000, be.getStoredEnergy());

        assertFalse(be.isShieldActive());
        be.setShieldActive(true);
        assertTrue(be.isShieldActive());

        assertEquals(48, be.getFieldRadius());
        be.setFieldRadius(32);
        assertEquals(32, be.getFieldRadius());
    }

    @Test
    void shouldTestKineticRailgunBlockEntityInventoryAndEnergy() {
        KineticRailgunBlockEntity be = new KineticRailgunBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(250000, be.getStoredEnergy());
        assertEquals(250000, be.getMaxEnergy());

        be.setStoredEnergy(150000);
        assertEquals(150000, be.getStoredEnergy());

        ItemStack slugStack = new ItemStack(Items.IRON_INGOT, 16);
        be.setItem(0, slugStack);
        assertEquals(16, be.getItem(0).getCount());
        assertTrue(be.getItem(0).is(Items.IRON_INGOT));

        be.removeItem(0, 4);
        assertEquals(12, be.getItem(0).getCount());
    }

    @Test
    void shouldTestAcousticPylonBlockEntityEnergy() {
        AcousticDefensePylonBlockEntity be = new AcousticDefensePylonBlockEntity(BlockEntityTypes.BARREL, BlockPos.ZERO, Blocks.BARREL.defaultBlockState());
        assertEquals(50000, be.getStoredEnergy());
        assertEquals(50000, be.getMaxEnergy());

        be.setStoredEnergy(25000);
        assertEquals(25000, be.getStoredEnergy());
    }

    @Test
    void shouldVerifyPhase30BlockstatesAndModelsExist() {
        File shieldState = new File("src/main/resources/assets/sandstorm/blockstates/plasma_shield_generator.json");
        File railgunState = new File("src/main/resources/assets/sandstorm/blockstates/kinetic_railgun.json");
        File pylonState = new File("src/main/resources/assets/sandstorm/blockstates/acoustic_defense_pylon.json");

        File shieldItemDef = new File("src/main/resources/assets/sandstorm/items/plasma_shield_generator.json");
        File railgunItemDef = new File("src/main/resources/assets/sandstorm/items/kinetic_railgun.json");
        File pylonItemDef = new File("src/main/resources/assets/sandstorm/items/acoustic_defense_pylon.json");
        File slugItemDef = new File("src/main/resources/assets/sandstorm/items/kinetic_slug.json");
        File toroidItemDef = new File("src/main/resources/assets/sandstorm/items/superconductor_toroid.json");
        File crystalItemDef = new File("src/main/resources/assets/sandstorm/items/plasma_focus_crystal.json");

        assertTrue(shieldState.exists());
        assertTrue(railgunState.exists());
        assertTrue(pylonState.exists());

        assertTrue(shieldItemDef.exists());
        assertTrue(railgunItemDef.exists());
        assertTrue(pylonItemDef.exists());
        assertTrue(slugItemDef.exists());
        assertTrue(toroidItemDef.exists());
        assertTrue(crystalItemDef.exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plasma_shield_generator_bottom",
            "plasma_shield_generator_side",
            "plasma_shield_generator_front",
            "plasma_shield_generator_front_active",
            "plasma_shield_generator_top",
            "kinetic_railgun_bottom",
            "kinetic_railgun_side",
            "kinetic_railgun_front",
            "kinetic_railgun_front_lit",
            "kinetic_railgun_top",
            "acoustic_defense_pylon_bottom",
            "acoustic_defense_pylon_side",
            "acoustic_defense_pylon_side_active",
            "acoustic_defense_pylon_top"
    })
    void shouldVerifyPhase30BlockTexturesHaveValidPngHeader(String textureName) throws IOException {
        File file = new File("src/main/resources/assets/sandstorm/textures/block/" + textureName + ".png");
        assertTrue(file.exists(), "Textura " + textureName + " deve existir");
        assertTrue(file.length() > 0, "Textura " + textureName + " não pode ser vazia");

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = fis.read(header);
            assertEquals(8, read);
            assertArrayEquals(new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}, header);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "kinetic_slug",
            "superconductor_toroid",
            "plasma_focus_crystal"
    })
    void shouldVerifyPhase30ItemTexturesHaveValidPngHeader(String textureName) throws IOException {
        File file = new File("src/main/resources/assets/sandstorm/textures/item/" + textureName + ".png");
        assertTrue(file.exists(), "Textura " + textureName + " deve existir");
        assertTrue(file.length() > 0, "Textura " + textureName + " não pode ser vazia");

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = fis.read(header);
            assertEquals(8, read);
            assertArrayEquals(new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}, header);
        }
    }
}
