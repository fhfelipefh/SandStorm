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

    private static final int SIZE_X = 9;
    private static final int SIZE_Y = 5;
    private static final int SIZE_Z = 11;

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
        assertFalse(blocksTag.isEmpty());
    }

    private static CompoundTag buildSpaceshipStructureTag() {
        List<CompoundTag> palette = new ArrayList<>();
        Map<String, Integer> paletteIndices = new HashMap<>();

        paletteIndices.put("minecraft:air", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:air", null));
        paletteIndices.put("minecraft:iron_block", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:iron_block", null));
        paletteIndices.put("minecraft:polished_basalt", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:polished_basalt", Map.of("axis", "y")));
        paletteIndices.put("minecraft:smooth_stone_slab", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:smooth_stone_slab", Map.of("type", "bottom")));
        paletteIndices.put("minecraft:tinted_glass", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:tinted_glass", null));
        paletteIndices.put("minecraft:sea_lantern", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:sea_lantern", null));
        paletteIndices.put("minecraft:crying_obsidian", getOrCreatePaletteIndex(palette, paletteIndices, "minecraft:crying_obsidian", null));
        paletteIndices.put("sandstorm:printer_3d", getOrCreatePaletteIndex(palette, paletteIndices, "sandstorm:printer_3d", Map.of("facing", "south")));

        ListTag blocksList = new ListTag();

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                for (int z = 0; z < SIZE_Z; z++) {
                    int stateIndex = determineBlockState(x, y, z, paletteIndices);
                    if (stateIndex != paletteIndices.get("minecraft:air")) {
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
        int glass = paletteIndices.get("minecraft:tinted_glass");
        int light = paletteIndices.get("minecraft:sea_lantern");
        int reactor = paletteIndices.get("minecraft:crying_obsidian");
        int printer = paletteIndices.get("sandstorm:printer_3d");

        if (y == 0) {
            if (x == 0 || x == SIZE_X - 1 || z == 0 || z == SIZE_Z - 1) {
                return iron;
            }
            return slab;
        }

        if (y == 1 && x == 4 && z == 9) {
            return printer;
        }

        if (y == 1 && (z == 0 && (x == 2 || x == 6))) {
            return reactor;
        }

        if (y == 1 && z == 1 && x == 4) {
            return air;
        }

        if (y == 2 && z == 1 && x == 4) {
            return air;
        }

        if (y == 1 || y == 2) {
            if (x == 1 || x == SIZE_X - 2) {
                if (z == 4 || z == 5 || z == 6) {
                    return glass;
                }
                if (z % 2 == 0) {
                    return basalt;
                }
                return iron;
            }
            if (z == SIZE_Z - 2) {
                if (x == 3 || x == 4 || x == 5) {
                    return (y == 2) ? glass : iron;
                }
                return iron;
            }
            if (z == 1) {
                if (x == 4) {
                    return air;
                }
                return iron;
            }
            return air;
        }

        if (y == 3) {
            if (x == 1 || x == SIZE_X - 2 || z == 1 || z == SIZE_Z - 2) {
                return iron;
            }
            if (x == 4 && (z == 4 || z == 7)) {
                return light;
            }
            return iron;
        }

        if (y == 4) {
            if (x >= 2 && x <= SIZE_X - 3 && z >= 2 && z <= SIZE_Z - 3) {
                if (x == 4) {
                    return basalt;
                }
                return iron;
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
