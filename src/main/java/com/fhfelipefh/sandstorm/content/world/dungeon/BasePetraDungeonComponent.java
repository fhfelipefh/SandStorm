package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BasePetraDungeonComponent extends BaseDungeonComponent {

    @Override
    public boolean isUnique() {
        return false;
    }

    protected void buildPetraShell(ServerLevel level, BlockPos origin, DungeonRotation rotation, int size,
                                   DungeonSocket north, DungeonSocket east, DungeonSocket south, DungeonSocket west) {
        int midMin = size / 2 - 1;
        int midMax = size / 2 + 1;

        for (int lx = 0; lx < size; lx++) {
            for (int lz = 0; lz < size; lz++) {
                BlockState floorState = (lx + lz) % 2 == 0
                        ? Blocks.CUT_RED_SANDSTONE.defaultBlockState()
                        : Blocks.SMOOTH_RED_SANDSTONE.defaultBlockState();
                setLocalBlock(level, origin, rotation, lx, 0, lz, size, floorState);

                BlockPos worldFloor = origin.offset(rotation.rotateOffset(lx, 0, lz, size));
                for (int fy = 1; fy <= 3; fy++) {
                    BlockPos foundationPos = worldFloor.below(fy);
                    if (level.getBlockState(foundationPos).isAir()) {
                        level.setBlock(foundationPos, Blocks.RED_SANDSTONE.defaultBlockState(), 2);
                    }
                }

                BlockState ceilingState = (lx == 0 || lx == size - 1 || lz == 0 || lz == size - 1)
                        ? Blocks.CUT_RED_SANDSTONE.defaultBlockState()
                        : Blocks.SMOOTH_RED_SANDSTONE.defaultBlockState();
                setLocalBlock(level, origin, rotation, lx, 5, lz, size, ceilingState);

                for (int ly = 1; ly <= 4; ly++) {
                    boolean isNorthWall = (lz == 0);
                    boolean isSouthWall = (lz == size - 1);
                    boolean isWestWall = (lx == 0);
                    boolean isEastWall = (lx == size - 1);

                    if (isNorthWall || isSouthWall || isWestWall || isEastWall) {
                        boolean isDoorway = false;
                        if (isNorthWall && (north == DungeonSocket.CORRIDOR || north == DungeonSocket.DOOR) && lx >= midMin && lx <= midMax && ly <= 3) {
                            isDoorway = true;
                        } else if (isSouthWall && (south == DungeonSocket.CORRIDOR || south == DungeonSocket.DOOR) && lx >= midMin && lx <= midMax && ly <= 3) {
                            isDoorway = true;
                        } else if (isEastWall && (east == DungeonSocket.CORRIDOR || east == DungeonSocket.DOOR) && lz >= midMin && lz <= midMax && ly <= 3) {
                            isDoorway = true;
                        } else if (isWestWall && (west == DungeonSocket.CORRIDOR || west == DungeonSocket.DOOR) && lz >= midMin && lz <= midMax && ly <= 3) {
                            isDoorway = true;
                        }

                        if (isDoorway) {
                            setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR.defaultBlockState());
                        } else {
                            int pattern = (lx * 3 + lz * 7 + ly) % 5;
                            BlockState wallState;
                            if (pattern == 0) {
                                wallState = Blocks.CHISELED_RED_SANDSTONE.defaultBlockState();
                            } else if (pattern == 1) {
                                wallState = Blocks.CUT_RED_SANDSTONE.defaultBlockState();
                            } else if (pattern == 2) {
                                wallState = Blocks.TERRACOTTA.defaultBlockState();
                            } else {
                                wallState = Blocks.RED_SANDSTONE.defaultBlockState();
                            }
                            setLocalBlock(level, origin, rotation, lx, ly, lz, size, wallState);
                        }
                    } else {
                        setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
    }
}
