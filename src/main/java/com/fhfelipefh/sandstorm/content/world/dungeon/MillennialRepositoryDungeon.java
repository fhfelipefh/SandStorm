package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MillennialRepositoryDungeon {

    public static final int ROOM_SIZE = 7;
    public static final int FLOOR_HEIGHT = 8;
    public static final int DEFAULT_FLOORS = 3;
    public static final int DEFAULT_GRID_WIDTH = 4;
    public static final int DEFAULT_GRID_DEPTH = 4;

    public static int generate(ServerLevel level, BlockPos origin) {
        return generate(level, origin, DEFAULT_FLOORS, DEFAULT_GRID_WIDTH, DEFAULT_GRID_DEPTH);
    }

    public static int generate(ServerLevel level, BlockPos origin, int totalFloors, int gridWidth, int gridDepth) {
        RandomSource random = level.getRandom();
        int clampedFloors = Math.clamp(totalFloors, 2, 6);
        int clampedWidth = Math.clamp(gridWidth, 3, 9);
        int clampedDepth = Math.clamp(gridDepth, 3, 9);

        List<DungeonComponent> components = MillennialRepositoryCatalog.getComponents();
        DungeonClutterPass clutterPass = new AncientBunkerClutterPass();
        int totalRoomsBuilt = 0;

        int prevTransX = -1;
        int prevTransZ = -1;

        for (int floor = 0; floor < clampedFloors; floor++) {
            BlockPos floorOrigin = origin.offset(0, -floor * FLOOR_HEIGHT, 0);
            Map<Long, RotatedComponent> anchors = new HashMap<>();

            int currDownX = -1;
            int currDownZ = -1;

            if (floor == 0) {
                int entryX = clampedWidth / 2;
                int entryZ = 0;
                long entryKey = (((long) entryX) << 32) | (entryZ & 0xFFFFFFFFL);
                anchors.put(entryKey, new RotatedComponent(MillennialRepositoryCatalog.getEntranceComponent(), DungeonRotation.NONE));

                currDownX = (entryX <= clampedWidth / 2) ? (clampedWidth - 1) : 0;
                currDownZ = clampedDepth - 1;
                long downKey = (((long) currDownX) << 32) | (currDownZ & 0xFFFFFFFFL);
                anchors.put(downKey, new RotatedComponent(MillennialRepositoryCatalog.getStairwellDownComponent(), DungeonRotation.NONE));
                prevTransX = currDownX;
                prevTransZ = currDownZ;
            } else if (floor == clampedFloors - 1) {
                long upKey = (((long) prevTransX) << 32) | (prevTransZ & 0xFFFFFFFFL);
                anchors.put(upKey, new RotatedComponent(MillennialRepositoryCatalog.getStairwellUpComponent(), DungeonRotation.NONE));

                int vaultX = (prevTransX == 0) ? (clampedWidth - 1) : 0;
                int vaultZ = (prevTransZ == 0) ? (clampedDepth - 1) : 0;
                long vaultKey = (((long) vaultX) << 32) | (vaultZ & 0xFFFFFFFFL);
                anchors.put(vaultKey, new RotatedComponent(MillennialRepositoryCatalog.getSpentFuelPoolVaultComponent(), DungeonRotation.NONE));
            } else {
                long upKey = (((long) prevTransX) << 32) | (prevTransZ & 0xFFFFFFFFL);
                anchors.put(upKey, new RotatedComponent(MillennialRepositoryCatalog.getStairwellUpComponent(), DungeonRotation.NONE));

                currDownX = (prevTransX == 0) ? (clampedWidth - 1) : 0;
                currDownZ = (prevTransZ == 0) ? (clampedDepth - 1) : 0;
                long downKey = (((long) currDownX) << 32) | (currDownZ & 0xFFFFFFFFL);

                DungeonComponent downComponent = (floor % 2 == 1)
                        ? MillennialRepositoryCatalog.getStairwellDownComponent()
                        : MillennialRepositoryCatalog.getCargoHoistShaftComponent();

                anchors.put(downKey, new RotatedComponent(downComponent, DungeonRotation.NONE));
                prevTransX = currDownX;
                prevTransZ = currDownZ;
            }

            int roomsOnFloor = ModularDungeonBuilder.build(
                    level,
                    floorOrigin,
                    clampedWidth,
                    clampedDepth,
                    ROOM_SIZE,
                    components,
                    anchors,
                    clutterPass,
                    random
            );
            totalRoomsBuilt += roomsOnFloor;
        }

        return totalRoomsBuilt;
    }
}
