package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbandonedMilitaryLabDungeon {

    public static final int ROOM_SIZE = 7;
    public static final int FLOOR_HEIGHT = 8;
    public static final int DEFAULT_FLOORS = 3;
    public static final int DEFAULT_GRID_WIDTH = 5;
    public static final int DEFAULT_GRID_DEPTH = 5;

    public static int generate(ServerLevel level, BlockPos origin) {
        return generate(level, origin, DEFAULT_FLOORS, DEFAULT_GRID_WIDTH, DEFAULT_GRID_DEPTH);
    }

    public static int generate(ServerLevel level, BlockPos origin, int totalFloors, int gridWidth, int gridDepth) {
        RandomSource random = level.getRandom();
        int clampedFloors = Math.clamp(totalFloors, 2, 6);
        int clampedWidth = Math.clamp(gridWidth, 3, 9);
        int clampedDepth = Math.clamp(gridDepth, 3, 9);

        List<DungeonComponent> components = MilitaryLabCatalog.getComponents();
        DungeonClutterPass clutterPass = new MilitaryLabClutterPass();
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
                anchors.put(entryKey, new RotatedComponent(MilitaryLabCatalog.getEntranceComponent(), DungeonRotation.NONE));

                currDownX = (entryX <= clampedWidth / 2) ? (clampedWidth - 1) : 0;
                currDownZ = clampedDepth - 1;
                long downKey = (((long) currDownX) << 32) | (currDownZ & 0xFFFFFFFFL);
                anchors.put(downKey, new RotatedComponent(MilitaryLabCatalog.getStairwellDownComponent(), DungeonRotation.NONE));
                prevTransX = currDownX;
                prevTransZ = currDownZ;
            } else if (floor == clampedFloors - 1) {
                long upKey = (((long) prevTransX) << 32) | (prevTransZ & 0xFFFFFFFFL);
                anchors.put(upKey, new RotatedComponent(MilitaryLabCatalog.getStairwellUpComponent(), DungeonRotation.NONE));

                int vaultX = (prevTransX == 0) ? (clampedWidth - 1) : 0;
                int vaultZ = (prevTransZ == 0) ? (clampedDepth - 1) : 0;
                long vaultKey = (((long) vaultX) << 32) | (vaultZ & 0xFFFFFFFFL);
                anchors.put(vaultKey, new RotatedComponent(MilitaryLabCatalog.getClassifiedVaultComponent(), DungeonRotation.NONE));
            } else {
                long upKey = (((long) prevTransX) << 32) | (prevTransZ & 0xFFFFFFFFL);
                anchors.put(upKey, new RotatedComponent(MilitaryLabCatalog.getStairwellUpComponent(), DungeonRotation.NONE));

                currDownX = (prevTransX == 0) ? (clampedWidth - 1) : 0;
                currDownZ = (prevTransZ == 0) ? (clampedDepth - 1) : 0;
                long downKey = (((long) currDownX) << 32) | (currDownZ & 0xFFFFFFFFL);

                anchors.put(downKey, new RotatedComponent(MilitaryLabCatalog.getStairwellDownComponent(), DungeonRotation.NONE));
                prevTransX = currDownX;
                prevTransZ = currDownZ;
            }

            List<DungeonComponent> floorComponents = components;
            if (floor == 0) {
                floorComponents = components.stream()
                        .filter(c -> !c.id().equals("military_lab_stairwell_up"))
                        .toList();
            } else if (floor == clampedFloors - 1) {
                floorComponents = components.stream()
                        .filter(c -> !c.id().equals("military_lab_stairwell_down"))
                        .toList();
            }

            int roomsOnFloor = ModularDungeonBuilder.build(
                    level,
                    floorOrigin,
                    clampedWidth,
                    clampedDepth,
                    ROOM_SIZE,
                    floorComponents,
                    anchors,
                    clutterPass,
                    random
            );
            totalRoomsBuilt += roomsOnFloor;

            if (floor < clampedFloors - 1 && prevTransX >= 0 && prevTransZ >= 0) {
                connectVerticalShaft(level, floorOrigin, prevTransX, prevTransZ, ROOM_SIZE, FLOOR_HEIGHT);
            }
        }

        return totalRoomsBuilt;
    }

    private static void connectVerticalShaft(ServerLevel level, BlockPos upperFloorOrigin, int transX, int transZ, int roomSize, int floorHeight) {
        BlockPos shaftOrigin = upperFloorOrigin.offset(transX * roomSize, 0, transZ * roomSize);

        for (int lx = 2; lx <= 4; lx++) {
            for (int lz = 2; lz <= 4; lz++) {
                for (int ly = -floorHeight + 1; ly <= 0; ly++) {
                    if (lx == 2 && lz == 2) {
                        continue;
                    }
                    BlockPos p = shaftOrigin.offset(lx, ly, lz);
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }

        for (int ly = -floorHeight + 1; ly <= 4; ly++) {
            BlockPos pillarPos = shaftOrigin.offset(2, ly, 2);
            level.setBlock(pillarPos, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState(), 2);
        }

        for (int ly = -floorHeight + 1; ly <= 1; ly++) {
            BlockPos ladderPos = shaftOrigin.offset(2, ly, 3);
            level.setBlock(ladderPos, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 2);
        }

        level.setBlock(shaftOrigin.offset(3, 1, 1), Blocks.IRON_BARS.defaultBlockState(), 2);
        level.setBlock(shaftOrigin.offset(4, 1, 1), Blocks.IRON_BARS.defaultBlockState(), 2);
        level.setBlock(shaftOrigin.offset(4, 1, 2), Blocks.IRON_BARS.defaultBlockState(), 2);
        level.setBlock(shaftOrigin.offset(4, 1, 3), Blocks.IRON_BARS.defaultBlockState(), 2);
        level.setBlock(shaftOrigin.offset(4, 1, 4), Blocks.IRON_BARS.defaultBlockState(), 2);
        level.setBlock(shaftOrigin.offset(3, 1, 4), Blocks.IRON_BARS.defaultBlockState(), 2);
        level.setBlock(shaftOrigin.offset(2, 1, 4), Blocks.IRON_BARS.defaultBlockState(), 2);
    }
}
