package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceshipStructureGeneratorTest {

    private static final int SIZE_X = 13;
    private static final int SIZE_Y = 7;
    private static final int SIZE_Z = 16;

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldGenerateAndValidateSpaceshipCrashSiteStructure() throws IOException {
        CompoundTag structureTag = buildSpaceshipStructureTag();

        Path targetDir = Path.of("src", "main", "resources", "data", "sandstorm", "structure");
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve("spaceship_crash_site.nbt");

        NbtIo.writeCompressed(structureTag, targetFile);
        assertTrue(Files.exists(targetFile));

        CompoundTag loadedTag = NbtIo.readCompressed(targetFile, NbtAccounter.unlimitedHeap());
        ListTag sizeTag = loadedTag.getListOrEmpty("size");
        assertEquals(3, sizeTag.size());
        assertEquals(SIZE_X, sizeTag.getInt(0).orElse(-1));
        assertEquals(SIZE_Y, sizeTag.getInt(1).orElse(-1));
        assertEquals(SIZE_Z, sizeTag.getInt(2).orElse(-1));

        ListTag paletteTag = loadedTag.getListOrEmpty("palette");
        assertFalse(paletteTag.isEmpty());

        for (int i = 0; i < paletteTag.size(); i++) {
            CompoundTag entry = paletteTag.getCompoundOrEmpty(i);
            String id = entry.getStringOr("id", "");
            assertFalse(id.contains("chest"), "Spaceship must not contain shared chests: " + id);
            assertFalse(id.contains("barrel"), "Spaceship must not contain shared barrels: " + id);
            assertFalse(id.contains("shulker"), "Spaceship must not contain shulker boxes: " + id);
            assertFalse(id.contains("hopper"), "Spaceship must not contain hoppers: " + id);
        }

        ListTag blocksTag = loadedTag.getListOrEmpty("blocks");
        assertEquals(SIZE_X * SIZE_Y * SIZE_Z, blocksTag.size());
    }

    private static CompoundTag buildSpaceshipStructureTag() {
        List<CompoundTag> palette = new ArrayList<>();
        Map<String, Integer> paletteIndices = new HashMap<>();

        paletteIndices.put("minecraft:air", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:air", null));
        paletteIndices.put("minecraft:iron_block", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:iron_block", null));
        paletteIndices.put("minecraft:polished_basalt", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:polished_basalt", Map.of("axis", "y")));
        paletteIndices.put("minecraft:smooth_stone_slab", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:smooth_stone_slab", Map.of("type", "bottom")));
        paletteIndices.put("minecraft:polished_deepslate", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:polished_deepslate", null));
        paletteIndices.put("minecraft:cut_copper", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:cut_copper", null));
        paletteIndices.put("minecraft:tinted_glass", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:tinted_glass", null));
        paletteIndices.put("minecraft:sea_lantern", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:sea_lantern", null));
        paletteIndices.put("minecraft:crying_obsidian", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:crying_obsidian", null));
        paletteIndices.put("minecraft:redstone_lamp", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:redstone_lamp", null));
        paletteIndices.put("sandstorm:printer_3d", getOrCreatePaletteIndex(palette, paletteIndices, "sandstorm:printer_3d", Map.of("facing", "south")));

        ListTag blocksList = new ListTag();

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                for (int z = 0; z < SIZE_Z; z++) {
                    int stateIndex = determineBlockState(x, y, z, paletteIndices);
                    CompoundTag blockTag = new CompoundTag();
                    ListTag posList = new ListTag();
                    posList.add(IntTag.valueOf(x));
                    posList.add(IntTag.valueOf(y));
                    posList.add(IntTag.valueOf(z));
                    blockTag.put("pos", posList);
                    blockTag.putInt("state", stateIndex);
                    blocksList.add(blockTag);
                }
            }
        }

        ListTag paletteList = new ListTag();
        for (CompoundTag entry : palette) {
            paletteList.add(entry);
        }

        ListTag sizeList = new ListTag();
        sizeList.add(IntTag.valueOf(SIZE_X));
        sizeList.add(IntTag.valueOf(SIZE_Y));
        sizeList.add(IntTag.valueOf(SIZE_Z));

        CompoundTag root = new CompoundTag();
        root.put("size", sizeList);
        root.put("palette", paletteList);
        root.put("blocks", blocksList);
        root.put("entities", new ListTag());
        root.putInt("DataVersion", 4249);
        return root;
    }

    private static int determineBlockState(int x, int y, int z, Map<String, Integer> paletteIndices) {
        int air = paletteIndices.get("minecraft:air");
        int iron = paletteIndices.get("minecraft:iron_block");
        int basalt = paletteIndices.get("minecraft:polished_basalt");
        int slab = paletteIndices.get("minecraft:smooth_stone_slab");
        int deepslate = paletteIndices.get("minecraft:polished_deepslate");
        int copper = paletteIndices.get("minecraft:cut_copper");
        int glass = paletteIndices.get("minecraft:tinted_glass");
        int light = paletteIndices.get("minecraft:sea_lantern");
        int reactor = paletteIndices.get("minecraft:crying_obsidian");
        int lamp = paletteIndices.get("minecraft:redstone_lamp");
        int printer = paletteIndices.get("sandstorm:printer_3d");

        if (z <= 3) {
            if (x >= 4 && x <= 7) {
                if (y == 0) {
                    return (z == 0) ? copper : slab;
                }
                return air;
            }

            if (x >= 1 && x <= 2 && y >= 1 && y <= 4) {
                if (z == 0 || z == 3 || y == 1 || y == 4) {
                    return iron;
                }
                if (y == 2 && z == 2) {
                    return light;
                }
                return (x == 2) ? copper : deepslate;
            }

            if (x >= 1 && x <= 2 && y == 0) {
                return iron;
            }

            if (x == 8 && y == 0) {
                return deepslate;
            }

            return air;
        }

        if (z == 4) {
            if (x >= 4 && x <= 7 && y >= 1 && y <= 4) {
                return air;
            }
            if (y == 0) {
                return (x >= 4 && x <= 7) ? iron : deepslate;
            }
            if (y == 5) {
                if (x == 5 || x == 6) {
                    return lamp;
                }
                if (x == 4 || x == 7) {
                    return basalt;
                }
                return deepslate;
            }
            if (y >= 1 && y <= 4) {
                if (x == 3) {
                    return (y == 3) ? light : deepslate;
                }
                if (x == 8) {
                    return (y == 3) ? lamp : deepslate;
                }
                if (x >= 1 && x <= 2) {
                    return iron;
                }
                if (x >= 9 && x <= 11) {
                    return deepslate;
                }
            }
            return (y == 6 && x >= 3 && x <= 9) ? basalt : air;
        }

        if (z >= 5 && z <= 13) {
            if (y == 0) {
                if (x >= 4 && x <= 7) {
                    if ((x == 5 || x == 6) && (z == 7 || z == 11)) {
                        return light;
                    }
                    return slab;
                }
                return iron;
            }

            if (x == 5 && z == 12 && y == 1) {
                return printer;
            }

            if (x >= 4 && x <= 7 && y >= 1 && y <= 4) {
                return air;
            }

            if ((x == 3 || x == 8) && y >= 1 && y <= 4) {
                if (y == 2 && (z == 7 || z == 10)) {
                    return light;
                }
                if (y == 1 && (z == 8 || z == 9)) {
                    return copper;
                }
                return air;
            }

            if (y >= 1 && y <= 4) {
                if (x <= 2 || x >= 9) {
                    if (y >= 2 && y <= 3 && z >= 7 && z <= 10) {
                        return glass;
                    }
                    if (z % 2 == 0) {
                        return basalt;
                    }
                    return iron;
                }
            }

            if (y == 5) {
                if (x >= 3 && x <= 8) {
                    if ((x == 5 || x == 6) && (z == 7 || z == 10)) {
                        return light;
                    }
                    return iron;
                }
                return basalt;
            }

            if (y == 6) {
                if (x >= 2 && x <= 10) {
                    return (x == 5 || x == 6) ? basalt : copper;
                }
                return air;
            }
        }

        if (z >= 14) {
            if (y == 0) {
                return iron;
            }
            if (z == 14 && y >= 1 && y <= 4) {
                if (y == 2 && (x == 5 || x == 7)) {
                    return reactor;
                }
                if (x >= 3 && x <= 9) {
                    return basalt;
                }
                return iron;
            }
            if (z == 15 && y >= 1 && y <= 3) {
                if (x >= 4 && x <= 8) {
                    return basalt;
                }
            }
            if (y == 5 && x >= 3 && x <= 9) {
                return basalt;
            }
            return air;
        }

        return air;
    }

    private static int getOrCreatePaletteIndex(List<CompoundTag> palette, Map<String, Integer> indices, String id, Map<String, String> properties) {
        String key = id + (properties != null ? properties.toString() : "");
        if (indices.containsKey(key)) {
            return indices.get(key);
        }
        CompoundTag entry = new CompoundTag();
        entry.putString("id", id);
        if (properties != null && !properties.isEmpty()) {
            CompoundTag propTag = new CompoundTag();
            for (Map.Entry<String, String> prop : properties.entrySet()) {
                propTag.putString(prop.getKey(), prop.getValue());
            }
            entry.put("properties", propTag);
        }
        int index = palette.size();
        palette.add(entry);
        indices.put(key, index);
        return index;
    }
}
