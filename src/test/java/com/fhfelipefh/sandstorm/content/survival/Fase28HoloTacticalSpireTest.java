package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSwarmManager;
import com.fhfelipefh.sandstorm.content.gui.HoloTacticalSpireMenu;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase28HoloTacticalSpireTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldRegisterHoloTacticalSpireBlockAndItemKeys() {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SandStormMod.id("holo_tactical_spire"));
        ResourceKey<Item> itemKey = SandStormMod.itemKey("holo_tactical_spire");

        assertNotNull(blockKey);
        assertNotNull(itemKey);
        assertEquals("sandstorm", blockKey.identifier().getNamespace());
        assertEquals("holo_tactical_spire", blockKey.identifier().getPath());
        assertEquals("sandstorm", itemKey.identifier().getNamespace());
        assertEquals("holo_tactical_spire", itemKey.identifier().getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "neural_synapse_link",
            "orbital_recon_probe"
    })
    void shouldRegisterPhase28ItemKeys(String itemPath) {
        ResourceKey<Item> key = SandStormMod.itemKey(itemPath);
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(itemPath, key.identifier().getPath());
    }

    @Test
    void shouldTestHoloTacticalSpireMenuDataChannels() {
        SimpleContainerData data = new SimpleContainerData(5);
        data.set(0, 75000);
        data.set(1, 100000);
        data.set(2, 1);
        data.set(3, 4);
        data.set(4, 1);

        SimpleContainer container = new SimpleContainer(1);
        Inventory dummyInv = new Inventory(null, null);
        HoloTacticalSpireMenu menu = new HoloTacticalSpireMenu(null, 1, dummyInv, null, container, data);

        assertEquals(75000, menu.getStoredEnergy());
        assertEquals(100000, menu.getMaxEnergy());
        assertEquals(1, menu.getActiveTacticalOrder());
        assertEquals(4, menu.getConnectedCyborgsCount());
        assertTrue(menu.isSeismicThreat());
        assertEquals(120, menu.getEnergyScaled(160));

        menu.clickMenuButton(null, 2);
        assertEquals(2, menu.getActiveTacticalOrder());
        assertEquals(2, CyborgSwarmManager.getInstance().getGlobalTacticalOrder());
    }

    @Test
    void shouldVerifyPhase28BlockstatesAndModelsExist() {
        File blockstate = new File("src/main/resources/assets/sandstorm/blockstates/holo_tactical_spire.json");
        File blockModel = new File("src/main/resources/assets/sandstorm/models/block/holo_tactical_spire.json");
        File blockModelActive = new File("src/main/resources/assets/sandstorm/models/block/holo_tactical_spire_active.json");
        File itemModel = new File("src/main/resources/assets/sandstorm/models/item/holo_tactical_spire.json");
        File itemDef = new File("src/main/resources/assets/sandstorm/items/holo_tactical_spire.json");

        assertTrue(blockstate.exists());
        assertTrue(blockModel.exists());
        assertTrue(blockModelActive.exists());
        assertTrue(itemModel.exists());
        assertTrue(itemDef.exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "neural_synapse_link",
            "orbital_recon_probe"
    })
    void shouldVerifyPhase28ItemModelsAndDefinitionsExist(String path) {
        File itemModel = new File("src/main/resources/assets/sandstorm/models/item/" + path + ".json");
        File itemDef = new File("src/main/resources/assets/sandstorm/items/" + path + ".json");

        assertTrue(itemModel.exists());
        assertTrue(itemDef.exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "textures/block/holo_tactical_spire_top.png",
            "textures/block/holo_tactical_spire_top_active.png",
            "textures/block/holo_tactical_spire_side.png",
            "textures/block/holo_tactical_spire_bottom.png",
            "textures/item/neural_synapse_link.png",
            "textures/item/orbital_recon_probe.png"
    })
    void shouldVerifyPhase28TexturesAreValidPNGs(String textureRelPath) throws IOException {
        File file = new File("src/main/resources/assets/sandstorm/" + textureRelPath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = fis.read(header);
            assertEquals(8, read);
            byte[] expectedPngHeader = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            assertArrayEquals(expectedPngHeader, header);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "holo_tactical_spire",
            "neural_synapse_link",
            "orbital_recon_probe"
    })
    void shouldVerifyPhase28RecipesExist(String recipeName) {
        File recipeFile = new File("src/main/resources/data/sandstorm/recipe/" + recipeName + ".json");
        assertTrue(recipeFile.exists());
        assertTrue(recipeFile.length() > 0);
    }

    @Test
    void shouldVerifyPhase28LootTableExists() {
        File lootTable = new File("src/main/resources/data/sandstorm/loot_table/blocks/holo_tactical_spire.json");
        assertTrue(lootTable.exists());
        assertTrue(lootTable.length() > 0);
    }
}
