package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.ClayUrnBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetraCliffDungeon {

    public static final int ROOM_SIZE = 7;
    public static final int DEFAULT_GRID_WIDTH = 5;
    public static final int DEFAULT_GRID_DEPTH = 5;
    public static final int FACADE_WIDTH = 23;
    public static final int FACADE_HEIGHT = 27;

    public static int generate(ServerLevel level, BlockPos entranceOrigin) {
        return generate(level, entranceOrigin, Direction.SOUTH, DEFAULT_GRID_WIDTH, DEFAULT_GRID_DEPTH);
    }

    public static int generate(ServerLevel level, BlockPos entranceOrigin, Direction outwardFacing, int gridWidth, int gridDepth) {
        RandomSource random = level.getRandom();
        buildMonumentalFacade(level, entranceOrigin, outwardFacing);

        Direction inwardFacing = outwardFacing.getOpposite();
        Direction lateral = outwardFacing.getClockWise();

        int entryX = gridWidth / 2;
        int entryZ = 0;

        BlockPos dungeonOrigin;
        if (outwardFacing == Direction.SOUTH) {
            dungeonOrigin = entranceOrigin.offset(-entryX * ROOM_SIZE, 0, -gridDepth * ROOM_SIZE);
        } else if (outwardFacing == Direction.NORTH) {
            dungeonOrigin = entranceOrigin.offset(-entryX * ROOM_SIZE, 0, 2);
        } else if (outwardFacing == Direction.EAST) {
            dungeonOrigin = entranceOrigin.offset(-gridWidth * ROOM_SIZE, 0, -entryX * ROOM_SIZE);
        } else {
            dungeonOrigin = entranceOrigin.offset(2, 0, -entryX * ROOM_SIZE);
        }

        List<DungeonComponent> components = PetraDungeonCatalog.getComponents();
        Map<Long, RotatedComponent> anchors = new HashMap<>();

        long entryKey = (((long) entryX) << 32) | (entryZ & 0xFFFFFFFFL);
        anchors.put(entryKey, new RotatedComponent(PetraDungeonCatalog.getEntranceComponent(), DungeonRotation.NONE));

        int cryptX = 1 + random.nextInt(Math.max(1, gridWidth - 2));
        int cryptZ = gridDepth - 1;
        long cryptKey = (((long) cryptX) << 32) | (cryptZ & 0xFFFFFFFFL);
        anchors.put(cryptKey, new RotatedComponent(PetraDungeonCatalog.getRoyalCryptComponent(), DungeonRotation.NONE));

        int altarX = (cryptX <= gridWidth / 2) ? Math.max(1, gridWidth - 2) : 1;
        int altarZ = gridDepth - 2;
        long altarKey = (((long) altarX) << 32) | (altarZ & 0xFFFFFFFFL);
        anchors.put(altarKey, new RotatedComponent(PetraDungeonCatalog.getSunAltarComponent(), DungeonRotation.NONE));

        DungeonClutterPass clutterPass = new PetraDungeonClutterPass();
        int roomsBuilt = ModularDungeonBuilder.build(level, dungeonOrigin, gridWidth, gridDepth, ROOM_SIZE, components, anchors, clutterPass, random);

        connectPortalToVestibule(level, entranceOrigin, inwardFacing, 6);

        return roomsBuilt;
    }

    private static void connectPortalToVestibule(ServerLevel level, BlockPos entranceOrigin, Direction inwardFacing, int length) {
        for (int step = 1; step <= length; step++) {
            BlockPos stepOrigin = entranceOrigin.relative(inwardFacing, step);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 1; dy <= 3; dy++) {
                    Direction lateral = inwardFacing.getClockWise();
                    BlockPos corridorPos = stepOrigin.relative(lateral, dx).above(dy);
                    level.setBlock(corridorPos, Blocks.AIR.defaultBlockState(), 2);
                }
                Direction lateral = inwardFacing.getClockWise();
                BlockPos floorPos = stepOrigin.relative(lateral, dx);
                level.setBlock(floorPos, Blocks.CUT_RED_SANDSTONE.defaultBlockState(), 2);
                BlockPos ceilingPos = stepOrigin.relative(lateral, dx).above(4);
                level.setBlock(ceilingPos, Blocks.CUT_RED_SANDSTONE.defaultBlockState(), 2);
            }
        }
    }

    public static void buildMonumentalFacade(ServerLevel level, BlockPos entranceOrigin, Direction outwardFacing) {
        Direction inwardFacing = outwardFacing.getOpposite();
        Direction lateral = outwardFacing.getClockWise();
        int halfWidth = FACADE_WIDTH / 2;

        for (int lat = -halfWidth - 1; lat <= halfWidth + 1; lat++) {
            for (int front = 1; front <= 3; front++) {
                for (int y = 0; y <= FACADE_HEIGHT; y++) {
                    BlockPos airPos = entranceOrigin.relative(lateral, lat).relative(outwardFacing, front).above(y);
                    if (!level.getBlockState(airPos).isAir()) {
                        level.setBlock(airPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int lat = -halfWidth; lat <= halfWidth; lat++) {
            for (int y = 0; y <= FACADE_HEIGHT; y++) {
                for (int depth = 0; depth <= 3; depth++) {
                    BlockPos fillPos = entranceOrigin.relative(lateral, lat).relative(inwardFacing, depth).above(y);
                    BlockState fillState;
                    if (y == 0) {
                        fillState = Blocks.SMOOTH_RED_SANDSTONE.defaultBlockState();
                    } else if (depth == 3 || lat == -halfWidth || lat == halfWidth || y == FACADE_HEIGHT) {
                        fillState = Blocks.RED_SANDSTONE.defaultBlockState();
                    } else if (y % 4 == 0) {
                        fillState = Blocks.CHISELED_RED_SANDSTONE.defaultBlockState();
                    } else if (y % 3 == 0) {
                        fillState = Blocks.TERRACOTTA.defaultBlockState();
                    } else {
                        fillState = Blocks.CUT_RED_SANDSTONE.defaultBlockState();
                    }
                    level.setBlock(fillPos, fillState, 2);
                }
            }
        }

        for (int lat = -halfWidth + 1; lat <= halfWidth - 1; lat++) {
            for (int y = 1; y <= 12; y++) {
                for (int depth = 0; depth <= 1; depth++) {
                    BlockPos recessPos = entranceOrigin.relative(lateral, lat).relative(inwardFacing, depth).above(y);
                    level.setBlock(recessPos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }

        int[] columnLats = new int[]{-10, -6, -2, 2, 6, 10};
        for (int cLat : columnLats) {
            BlockPos colBase = entranceOrigin.relative(lateral, cLat).above(1);
            level.setBlock(colBase, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState(), 2);
            for (int cy = 2; cy <= 11; cy++) {
                level.setBlock(entranceOrigin.relative(lateral, cLat).above(cy), Blocks.CUT_RED_SANDSTONE.defaultBlockState(), 2);
            }
            level.setBlock(entranceOrigin.relative(lateral, cLat).above(12), Blocks.CHISELED_RED_SANDSTONE.defaultBlockState(), 2);
        }

        for (int px = -1; px <= 1; px++) {
            for (int py = 1; py <= 4; py++) {
                BlockPos portalPos = entranceOrigin.relative(lateral, px).above(py);
                level.setBlock(portalPos, Blocks.AIR.defaultBlockState(), 2);
                level.setBlock(portalPos.relative(inwardFacing, 1), Blocks.AIR.defaultBlockState(), 2);
                level.setBlock(portalPos.relative(inwardFacing, 2), Blocks.AIR.defaultBlockState(), 2);
            }
        }

        int[] nicheLats = new int[]{-8, 8};
        for (int nLat : nicheLats) {
            BlockPos nicheShelf = entranceOrigin.relative(lateral, nLat).relative(inwardFacing, 1).above(2);
            level.setBlock(nicheShelf, Blocks.CUT_RED_SANDSTONE.defaultBlockState(), 2);
            BlockPos statuePos = nicheShelf.above();
            level.setBlock(statuePos, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, outwardFacing), 3);
            level.setBlock(statuePos.above(), Blocks.AIR.defaultBlockState(), 2);
        }

        for (int lat = -halfWidth; lat <= halfWidth; lat++) {
            for (int depth = 0; depth <= 1; depth++) {
                BlockPos entablature = entranceOrigin.relative(lateral, lat).relative(inwardFacing, depth).above(13);
                level.setBlock(entablature, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState(), 2);
                BlockPos cornice = entranceOrigin.relative(lateral, lat).relative(inwardFacing, depth).above(14);
                level.setBlock(cornice, Blocks.SMOOTH_RED_SANDSTONE.defaultBlockState(), 2);
            }
        }

        for (int lat = -5; lat <= 5; lat++) {
            int pedimentHeight = 15 + (5 - Math.abs(lat));
            for (int y = 15; y <= pedimentHeight; y++) {
                BlockPos pedimentPos = entranceOrigin.relative(lateral, lat).above(y);
                level.setBlock(pedimentPos, Blocks.CUT_RED_SANDSTONE.defaultBlockState(), 2);
            }
        }

        for (int lat = -halfWidth + 2; lat <= halfWidth - 2; lat++) {
            for (int y = 15; y <= 24; y++) {
                if (Math.abs(lat) > 3) {
                    BlockPos upperRecess = entranceOrigin.relative(lateral, lat).relative(inwardFacing, 0).above(y);
                    level.setBlock(upperRecess, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }

        int[] upperStatueLats = new int[]{-7, 7};
        for (int uLat : upperStatueLats) {
            BlockPos statuePos = entranceOrigin.relative(lateral, uLat).relative(inwardFacing, 1).above(17);
            level.setBlock(statuePos, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, outwardFacing), 3);
        }

        for (int tLat = -2; tLat <= 2; tLat++) {
            for (int ty = 16; ty <= 23; ty++) {
                BlockPos tholosPos = entranceOrigin.relative(lateral, tLat).above(ty);
                BlockState tholosState = (ty == 16 || ty == 23)
                        ? Blocks.CHISELED_RED_SANDSTONE.defaultBlockState()
                        : Blocks.CUT_RED_SANDSTONE.defaultBlockState();
                level.setBlock(tholosPos, tholosState, 2);
            }
        }

        BlockPos tholosNiche = entranceOrigin.above(18);
        level.setBlock(tholosNiche, Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(tholosNiche.above(), Blocks.AIR.defaultBlockState(), 2);

        BlockPos urnPedestal = entranceOrigin.above(24);
        level.setBlock(urnPedestal, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState(), 2);
        BlockPos urnPos = entranceOrigin.above(25);
        level.setBlock(urnPos, SandStormBlocks.CLAY_URN.defaultBlockState().setValue(ClayUrnBlock.FACING, outwardFacing), 3);
    }
}
