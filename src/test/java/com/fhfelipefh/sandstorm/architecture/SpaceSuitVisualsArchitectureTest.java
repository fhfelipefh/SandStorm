package com.fhfelipefh.sandstorm.architecture;

import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceSuitVisualsArchitectureTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void spaceSuitEquipmentDefinitionAndTexturesMustExist() throws IOException {
        Path equipmentJson = Path.of("src", "main", "resources", "assets", "sandstorm", "equipment", "space_suit.json");
        assertTrue(Files.exists(equipmentJson));
        String json = Files.readString(equipmentJson);
        assertTrue(json.contains("sandstorm:space_suit"));

        Path baseTex = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "equipment", "humanoid", "space_suit.png");
        assertTrue(Files.exists(baseTex));

        Path glowTex = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "equipment", "humanoid", "space_suit_glow.png");
        assertTrue(Files.exists(glowTex));

        Path leggingsTex = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "entity", "equipment", "humanoid_leggings", "space_suit.png");
        assertTrue(Files.exists(leggingsTex));
    }

    @Test
    void spaceSuitIconsMustExist() {
        Path helmet = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "space_suit_helmet.png");
        Path chest = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "space_suit_chestplate.png");
        Path leggings = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "space_suit_leggings.png");
        Path boots = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "item", "space_suit_boots.png");

        assertTrue(Files.exists(helmet));
        assertTrue(Files.exists(chest));
        assertTrue(Files.exists(leggings));
        assertTrue(Files.exists(boots));
    }

    @Test
    void spaceSuitMaterialMustUseDedicatedAsset() {
        assertNotNull(SpaceSuitItem.SPACE_SUIT_MATERIAL);
        assertNotNull(SpaceSuitItem.SPACE_SUIT_ASSET);
        assertTrue(SpaceSuitItem.SPACE_SUIT_ASSET.identifier().getPath().equals("space_suit"));
    }

    @Test
    void spaceSuitClientMustRegisterArmorRenderer() throws IOException {
        Path clientJava = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "SandStormClient.java");
        assertTrue(Files.exists(clientJava));
        String clientContent = Files.readString(clientJava);
        assertTrue(clientContent.contains("ArmorRenderer.register(new SpaceSuitArmorRenderer()"));
    }

    @Test
    void spaceSuitItemMustHideEnchantmentsInTooltip() throws IOException {
        Path spaceSuitItemPath = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "item", "SpaceSuitItem.java");
        assertTrue(Files.exists(spaceSuitItemPath));
        String content = Files.readString(spaceSuitItemPath);
        assertTrue(content.contains("withHidden(DataComponents.ENCHANTMENTS, true)"));
    }
}
