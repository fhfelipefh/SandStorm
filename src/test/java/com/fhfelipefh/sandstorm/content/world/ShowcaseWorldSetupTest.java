package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowcaseWorldSetupTest {

    @Test
    void ensureVoidShowcaseWorldIsConfigured() throws IOException {
        Path showcaseDir = Path.of("run", "saves", "SandStorm_Showcase");
        Path templateDir = Path.of("run", "world");

        if (!Files.exists(templateDir.resolve("level.dat"))) {
            return;
        }

        if (!Files.exists(showcaseDir)) {
            Files.createDirectories(showcaseDir);
        }

        Path templateDataDir = templateDir.resolve("data");
        Path showcaseDataDir = showcaseDir.resolve("data");
        copyDirectoryRecursive(templateDataDir, showcaseDataDir);

        Path templateDimsDir = templateDir.resolve("dimensions");
        Path showcaseDimsDir = showcaseDir.resolve("dimensions");
        copyDirectoryRecursive(templateDimsDir, showcaseDimsDir);

        Path regionDir = showcaseDimsDir.resolve("minecraft").resolve("overworld").resolve("region");
        if (Files.exists(regionDir)) {
            try (Stream<Path> files = Files.walk(regionDir)) {
                files.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        if (!p.equals(regionDir)) {
                            Files.deleteIfExists(p);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        Files.deleteIfExists(showcaseDir.resolve("session.lock"));

        Path levelDat = showcaseDir.resolve("level.dat");
        CompoundTag levelRoot = NbtIo.readCompressed(templateDir.resolve("level.dat"), NbtAccounter.unlimitedHeap());
        CompoundTag data = levelRoot.getCompoundOrEmpty("Data");
        data.putString("LevelName", "SandStorm_Showcase");
        data.putInt("GameType", 1);
        data.putByte("allowCommands", (byte) 1);

        CompoundTag diff = data.getCompoundOrEmpty("difficulty_settings");
        diff.putString("difficulty", "peaceful");
        diff.putByte("locked", (byte) 1);
        diff.putByte("hardcore", (byte) 0);
        data.put("difficulty_settings", diff);

        CompoundTag spawn = data.getCompoundOrEmpty("spawn");
        spawn.putIntArray("pos", new int[]{0, 162, 0});
        spawn.putString("dimension", "minecraft:overworld");
        data.put("spawn", spawn);

        NbtIo.writeCompressed(levelRoot, levelDat);

        Path wgsPath = showcaseDataDir.resolve("minecraft").resolve("world_gen_settings.dat");
        if (Files.exists(wgsPath)) {
            CompoundTag wgsRoot = NbtIo.readCompressed(wgsPath, NbtAccounter.unlimitedHeap());
            CompoundTag wgsData = wgsRoot.getCompoundOrEmpty("data");
            wgsData.putByte("bonus_chest", (byte) 0);
            wgsData.putByte("generate_structures", (byte) 0);

            CompoundTag dims = wgsData.getCompoundOrEmpty("dimensions");
            CompoundTag overworld = dims.getCompoundOrEmpty("minecraft:overworld");
            CompoundTag generator = overworld.getCompoundOrEmpty("generator");

            generator.putString("type", "minecraft:flat");
            generator.remove("biome_source");

            CompoundTag flatSettings = new CompoundTag();
            flatSettings.putString("biome", "minecraft:the_void");
            flatSettings.putByte("features", (byte) 0);
            flatSettings.putByte("lakes", (byte) 0);

            ListTag layers = new ListTag();
            CompoundTag airLayer = new CompoundTag();
            airLayer.putString("block", "minecraft:air");
            airLayer.putInt("height", 1);
            layers.add(airLayer);
            flatSettings.put("layers", layers);
            flatSettings.put("structure_overrides", new ListTag());

            generator.put("settings", flatSettings);
            NbtIo.writeCompressed(wgsRoot, wgsPath);
        }

        assertTrue(Files.exists(levelDat));
        CompoundTag verifiedRoot = NbtIo.readCompressed(levelDat, NbtAccounter.unlimitedHeap());
        if (Files.exists(wgsPath)) {
            CompoundTag verifiedWgs = NbtIo.readCompressed(wgsPath, NbtAccounter.unlimitedHeap());
            assertEquals("minecraft:the_void", verifiedWgs.getCompoundOrEmpty("data").getCompoundOrEmpty("dimensions").getCompoundOrEmpty("minecraft:overworld").getCompoundOrEmpty("generator").getCompoundOrEmpty("settings").getStringOr("biome", ""));
        }
        assertEquals("SandStorm_Showcase", verifiedRoot.getCompoundOrEmpty("Data").getStringOr("LevelName", ""));
        assertEquals(1, verifiedRoot.getCompoundOrEmpty("Data").getIntOr("GameType", 0));
    }

    private void copyDirectoryRecursive(Path source, Path target) throws IOException {
        if (!Files.exists(source)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(source)) {
            paths.forEach(src -> {
                try {
                    Path rel = source.relativize(src);
                    Path dest = target.resolve(rel);
                    if (Files.isDirectory(src)) {
                        if (!Files.exists(dest)) {
                            Files.createDirectories(dest);
                        }
                    } else {
                        if (Files.exists(dest)) {
                            Files.delete(dest);
                        }
                        Files.copy(src, dest);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
