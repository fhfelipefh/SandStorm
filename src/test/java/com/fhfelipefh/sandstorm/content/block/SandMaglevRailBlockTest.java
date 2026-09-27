package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandMaglevRailBlockTest {

    private static final Path BLOCK_TAG = Path.of("src", "main", "resources", "data", "minecraft", "tags", "block", "rails.json");
    private static final Path ITEM_TAG = Path.of("src", "main", "resources", "data", "minecraft", "tags", "item", "rails.json");
    private static final Path BLOCKSTATE = Path.of("src", "main", "resources", "assets", "sandstorm", "blockstates", "sand_maglev_rail.json");
    private static final Path MODEL_FLAT = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "sand_maglev_rail.json");
    private static final Path MODEL_CORNER = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "block", "sand_maglev_rail_corner.json");
    private static final Path ITEM_MODEL = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item", "sand_maglev_rail.json");
    private static final Path TEXTURE_FLAT = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "sand_maglev_rail.png");
    private static final Path TEXTURE_CORNER = Path.of("src", "main", "resources", "assets", "sandstorm", "textures", "block", "sand_maglev_rail_corner.png");
    private static final Path RECIPE = Path.of("src", "main", "resources", "data", "sandstorm", "recipe", "sand_maglev_rail.json");
    private static final Path LOOT_TABLE = Path.of("src", "main", "resources", "data", "sandstorm", "loot_table", "blocks", "sand_maglev_rail.json");

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldValidateMaglevRailInheritanceAndProperties() {
        assertEquals(RailBlock.class, SandMaglevRailBlock.class.getSuperclass());
        assertEquals(BlockStateProperties.RAIL_SHAPE, SandMaglevRailBlock.SHAPE);
        assertEquals("shape", SandMaglevRailBlock.SHAPE.getName());

        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id("sand_maglev_rail"));
        assertNotNull(key);
        assertEquals(Registries.BLOCK, key.registryKey());
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals("sand_maglev_rail", key.identifier().getPath());
    }

    @Test
    void shouldSupportCurvedShapes() {
        assertTrue(SandMaglevRailBlock.SHAPE.getPossibleValues().contains(RailShape.SOUTH_EAST));
        assertTrue(SandMaglevRailBlock.SHAPE.getPossibleValues().contains(RailShape.SOUTH_WEST));
        assertTrue(SandMaglevRailBlock.SHAPE.getPossibleValues().contains(RailShape.NORTH_WEST));
        assertTrue(SandMaglevRailBlock.SHAPE.getPossibleValues().contains(RailShape.NORTH_EAST));
        assertTrue(SandMaglevRailBlock.SHAPE.getPossibleValues().contains(RailShape.EAST_WEST));
        assertTrue(SandMaglevRailBlock.SHAPE.getPossibleValues().contains(RailShape.NORTH_SOUTH));
    }

    @Test
    void shouldIncludeMaglevRailInMinecraftTags() throws IOException {
        assertTrue(Files.exists(BLOCK_TAG));
        String blockTagContent = Files.readString(BLOCK_TAG);
        assertTrue(blockTagContent.contains("sandstorm:sand_maglev_rail"));

        assertTrue(Files.exists(ITEM_TAG));
        String itemTagContent = Files.readString(ITEM_TAG);
        assertTrue(itemTagContent.contains("sandstorm:sand_maglev_rail"));
    }

    @Test
    void shouldHaveValidLootTableAndRecipe() throws IOException {
        assertTrue(Files.exists(LOOT_TABLE));
        String lootContent = Files.readString(LOOT_TABLE);
        assertTrue(lootContent.contains("sandstorm:sand_maglev_rail"));

        assertTrue(Files.exists(RECIPE));
        String recipeContent = Files.readString(RECIPE);
        assertTrue(recipeContent.contains("sandstorm:sand_maglev_rail"));
    }

    @Test
    void shouldHaveCompleteBlockstateVariants() throws IOException {
        assertTrue(Files.exists(BLOCKSTATE));
        JsonObject root = JsonParser.parseString(Files.readString(BLOCKSTATE)).getAsJsonObject();
        JsonObject variants = root.getAsJsonObject("variants");
        assertNotNull(variants);

        assertTrue(variants.has("shape=north_south"));
        assertTrue(variants.has("shape=east_west"));
        assertTrue(variants.has("shape=south_east"));
        assertTrue(variants.has("shape=south_west"));
        assertTrue(variants.has("shape=north_west"));
        assertTrue(variants.has("shape=north_east"));

        JsonObject southEast = variants.getAsJsonObject("shape=south_east");
        assertEquals("sandstorm:block/sand_maglev_rail_corner", southEast.get("model").getAsString());
    }

    @Test
    void shouldHaveValidBlockModels() throws IOException {
        assertTrue(Files.exists(MODEL_FLAT));
        JsonObject flatModel = JsonParser.parseString(Files.readString(MODEL_FLAT)).getAsJsonObject();
        assertEquals("minecraft:block/rail_flat", flatModel.get("parent").getAsString());

        assertTrue(Files.exists(MODEL_CORNER));
        JsonObject cornerModel = JsonParser.parseString(Files.readString(MODEL_CORNER)).getAsJsonObject();
        assertEquals("minecraft:block/rail_curved", cornerModel.get("parent").getAsString());
    }

    @Test
    void shouldHaveValidItemModelForCreativeSearch() throws IOException {
        assertTrue(Files.exists(ITEM_MODEL));
        JsonObject itemModel = JsonParser.parseString(Files.readString(ITEM_MODEL)).getAsJsonObject();
        assertEquals("minecraft:item/generated", itemModel.get("parent").getAsString());
        JsonObject textures = itemModel.getAsJsonObject("textures");
        assertNotNull(textures);
        assertEquals("sandstorm:block/sand_maglev_rail", textures.get("layer0").getAsString());
    }

    @Test
    void shouldHaveTransparent3DStraightRailTexture() throws IOException {
        assertTrue(Files.exists(TEXTURE_FLAT));
        byte[] bytes = Files.readAllBytes(TEXTURE_FLAT);
        assertTrue(bytes.length > 8);
        assertEquals((byte) 0x89, bytes[0]);
        assertEquals((byte) 0x50, bytes[1]);
        assertEquals((byte) 0x4E, bytes[2]);
        assertEquals((byte) 0x47, bytes[3]);

        BufferedImage img;
        try (InputStream in = Files.newInputStream(TEXTURE_FLAT)) {
            img = ImageIO.read(in);
        }
        assertNotNull(img);
        assertEquals(16, img.getWidth());
        assertEquals(16, img.getHeight());

        int transparentCount = 0;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int alpha = (img.getRGB(x, y) >> 24) & 0xFF;
                if (alpha == 0) {
                    transparentCount++;
                }
            }
        }
        assertTrue(transparentCount >= 60);
    }

    @Test
    void shouldHaveTransparent3DCornerRailTextureWithCorrectOrientation() throws IOException {
        assertTrue(Files.exists(TEXTURE_CORNER));
        byte[] bytes = Files.readAllBytes(TEXTURE_CORNER);
        assertTrue(bytes.length > 8);
        assertEquals((byte) 0x89, bytes[0]);
        assertEquals((byte) 0x50, bytes[1]);
        assertEquals((byte) 0x4E, bytes[2]);
        assertEquals((byte) 0x47, bytes[3]);

        BufferedImage img;
        try (InputStream in = Files.newInputStream(TEXTURE_CORNER)) {
            img = ImageIO.read(in);
        }
        assertNotNull(img);
        assertEquals(16, img.getWidth());
        assertEquals(16, img.getHeight());

        int transparentCount = 0;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int alpha = (img.getRGB(x, y) >> 24) & 0xFF;
                if (alpha == 0) {
                    transparentCount++;
                }
            }
        }
        assertTrue(transparentCount >= 100);

        int alphaTopLeft = (img.getRGB(0, 0) >> 24) & 0xFF;
        assertEquals(0, alphaTopLeft);

        int alphaSouthEnd = (img.getRGB(2, 15) >> 24) & 0xFF;
        assertTrue(alphaSouthEnd > 0);

        int alphaEastEnd = (img.getRGB(15, 2) >> 24) & 0xFF;
        assertTrue(alphaEastEnd > 0);
    }
}
