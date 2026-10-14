package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AncientBunkerComplexDungeon {

    public static final int ROOM_SIZE = 7;
    public static final int DEFAULT_GRID_WIDTH = 5;
    public static final int DEFAULT_GRID_DEPTH = 5;

    public static int generate(ServerLevel level, BlockPos origin) {
        return generate(level, origin, DEFAULT_GRID_WIDTH, DEFAULT_GRID_DEPTH);
    }

    public static int generate(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth) {
        RandomSource random = level.getRandom();
        List<DungeonComponent> components = AncientBunkerCatalog.getComponents();
        Map<Long, RotatedComponent> anchors = new HashMap<>();

        int entryX = gridWidth / 2;
        int entryZ = 0;
        long entryKey = (((long) entryX) << 32) | (entryZ & 0xFFFFFFFFL);
        anchors.put(entryKey, new RotatedComponent(AncientBunkerCatalog.getEntranceComponent(), DungeonRotation.NONE));

        DungeonClutterPass clutterPass = new AncientBunkerClutterPass();
        return ModularDungeonBuilder.build(level, origin, gridWidth, gridDepth, ROOM_SIZE, components, anchors, clutterPass, random);
    }
}
