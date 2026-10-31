package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.RustingBlock;
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

        int numTransitions = clampedFloors - 1;
        int[][] shaftPositions = calculateShaftPositions(clampedWidth, clampedDepth, numTransitions);

        for (int floor = 0; floor < clampedFloors; floor++) {
            BlockPos floorOrigin = origin.offset(0, -floor * FLOOR_HEIGHT, 0);
            Map<Long, RotatedComponent> anchors = new HashMap<>();

            if (floor == 0) {
                int entryX = clampedWidth / 2;
                int entryZ = 0;
                long entryKey = (((long) entryX) << 32) | (entryZ & 0xFFFFFFFFL);
                anchors.put(entryKey, new RotatedComponent(MilitaryLabCatalog.getEntranceComponent(), DungeonRotation.NONE));

                int downX = shaftPositions[0][0];
                int downZ = shaftPositions[0][1];
                long downKey = (((long) downX) << 32) | (downZ & 0xFFFFFFFFL);
                anchors.put(downKey, new RotatedComponent(MilitaryLabCatalog.getStairwellDownComponent(), DungeonRotation.NONE));
            } else if (floor == clampedFloors - 1) {
                int upX = shaftPositions[floor - 1][0];
                int upZ = shaftPositions[floor - 1][1];
                long upKey = (((long) upX) << 32) | (upZ & 0xFFFFFFFFL);
                anchors.put(upKey, new RotatedComponent(MilitaryLabCatalog.getStairwellUpComponent(), DungeonRotation.NONE));

                int vaultX = (upX <= clampedWidth / 2) ? Math.max(1, clampedWidth - 2) : 1;
                int vaultZ = (upZ <= clampedDepth / 2) ? Math.max(1, clampedDepth - 2) : 1;
                long vaultKey = (((long) vaultX) << 32) | (vaultZ & 0xFFFFFFFFL);
                anchors.put(vaultKey, new RotatedComponent(MilitaryLabCatalog.getClassifiedVaultComponent(), DungeonRotation.NONE));
            } else {
                int upX = shaftPositions[floor - 1][0];
                int upZ = shaftPositions[floor - 1][1];
                long upKey = (((long) upX) << 32) | (upZ & 0xFFFFFFFFL);
                anchors.put(upKey, new RotatedComponent(MilitaryLabCatalog.getStairwellUpComponent(), DungeonRotation.NONE));

                int downX = shaftPositions[floor][0];
                int downZ = shaftPositions[floor][1];
                long downKey = (((long) downX) << 32) | (downZ & 0xFFFFFFFFL);
                anchors.put(downKey, new RotatedComponent(MilitaryLabCatalog.getStairwellDownComponent(), DungeonRotation.NONE));
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
        }

        connectAllVerticalShafts(level, origin, clampedFloors, shaftPositions, ROOM_SIZE, FLOOR_HEIGHT);

        return totalRoomsBuilt;
    }

    private static int[][] calculateShaftPositions(int width, int depth, int count) {
        int[][] result = new int[count][2];
        int midX = width / 2;
        int midZ = depth / 2;
        int minX = 1;
        int maxX = Math.max(1, width - 2);
        int minZ = 1;
        int maxZ = Math.max(1, depth - 2);

        int[][] candidates = new int[][]{
                {midX, midZ},
                {maxX, maxZ},
                {minX, minZ},
                {maxX, minZ},
                {minX, maxZ}
        };

        for (int i = 0; i < count; i++) {
            int[] cand = candidates[i % candidates.length];
            result[i][0] = Math.clamp(cand[0], 1, width - 2);
            result[i][1] = Math.clamp(cand[1], 1, depth - 2);
        }
        return result;
    }

    private static void connectAllVerticalShafts(ServerLevel level, BlockPos origin, int clampedFloors, int[][] shaftPositions, int roomSize, int floorHeight) {
        for (int floor = 0; floor < clampedFloors - 1; floor++) {
            int tx = shaftPositions[floor][0];
            int tz = shaftPositions[floor][1];

            BlockPos upperFloorOrigin = origin.offset(0, -floor * floorHeight, 0);
            BlockPos roomOrigin = upperFloorOrigin.offset(tx * roomSize, 0, tz * roomSize);

            for (int lx = 2; lx <= 4; lx++) {
                for (int lz = 2; lz <= 4; lz++) {
                    for (int ly = -floorHeight + 1; ly <= 4; ly++) {
                        if (lx == 2 && lz == 2) {
                            continue;
                        }
                        if (lx == 2 && lz == 3 && ly <= 2) {
                            continue;
                        }
                        BlockPos p = roomOrigin.offset(lx, ly, lz);
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }

            for (int ly = -floorHeight; ly <= 5; ly++) {
                BlockPos pillarPos = roomOrigin.offset(2, ly, 2);
                level.setBlock(pillarPos, SandStormBlocks.MILITARY_ARMOR_PLATE.defaultBlockState().setValue(RustingBlock.RUST, RustingBlock.selectRuinRust(level.getRandom())), 2);
            }

            for (int ly = -floorHeight + 1; ly <= 2; ly++) {
                BlockPos ladderPos = roomOrigin.offset(2, ly, 3);
                level.setBlock(ladderPos, Blocks.LADDER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 2);
            }

            level.setBlock(roomOrigin.offset(2, 1, 4), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(roomOrigin.offset(2, 2, 4), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(roomOrigin.offset(2, -floorHeight + 1, 4), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(roomOrigin.offset(2, -floorHeight + 2, 4), Blocks.AIR.defaultBlockState(), 2);

            level.setBlock(roomOrigin.offset(3, 1, 1), Blocks.IRON_BARS.defaultBlockState(), 2);
            level.setBlock(roomOrigin.offset(4, 1, 1), Blocks.IRON_BARS.defaultBlockState(), 2);
            level.setBlock(roomOrigin.offset(4, 1, 2), Blocks.IRON_BARS.defaultBlockState(), 2);
            level.setBlock(roomOrigin.offset(4, 1, 3), Blocks.IRON_BARS.defaultBlockState(), 2);
            level.setBlock(roomOrigin.offset(4, 1, 4), Blocks.IRON_BARS.defaultBlockState(), 2);
            level.setBlock(roomOrigin.offset(3, 1, 4), Blocks.IRON_BARS.defaultBlockState(), 2);

            ensureDoorwaysOpen(level, roomOrigin, roomSize);
            ensureDoorwaysOpen(level, roomOrigin.offset(0, -floorHeight, 0), roomSize);
        }
    }

    private static void ensureDoorwaysOpen(ServerLevel level, BlockPos roomOrigin, int roomSize) {
        int midMin = roomSize / 2 - 1;
        int midMax = roomSize / 2 + 1;

        for (int ly = 1; ly <= 3; ly++) {
            for (int lx = midMin; lx <= midMax; lx++) {
                level.setBlock(roomOrigin.offset(lx, ly, 0), Blocks.AIR.defaultBlockState(), 2);
                level.setBlock(roomOrigin.offset(lx, ly, roomSize - 1), Blocks.AIR.defaultBlockState(), 2);
            }
            for (int lz = midMin; lz <= midMax; lz++) {
                level.setBlock(roomOrigin.offset(0, ly, lz), Blocks.AIR.defaultBlockState(), 2);
                level.setBlock(roomOrigin.offset(roomSize - 1, ly, lz), Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }
}
