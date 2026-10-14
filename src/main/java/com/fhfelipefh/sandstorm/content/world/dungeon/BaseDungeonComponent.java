package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public abstract class BaseDungeonComponent implements DungeonComponent {

    protected void setLocalBlock(ServerLevel level, BlockPos origin, DungeonRotation rotation, int lx, int ly, int lz, int size, BlockState state) {
        setLocalBlock(level, origin, rotation, lx, ly, lz, size, state, 2);
    }

    protected void setLocalBlock(ServerLevel level, BlockPos origin, DungeonRotation rotation, int lx, int ly, int lz, int size, BlockState state, int flags) {
        BlockPos rotated = rotation.rotateOffset(lx, ly, lz, size);
        BlockPos worldPos = origin.offset(rotated);
        BlockState finalState = state;
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction localFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction rotatedFacing = rotation.rotateDirection(localFacing);
            Direction openFacing = determineOpenFacing(level, worldPos, rotatedFacing);
            finalState = state.setValue(BlockStateProperties.HORIZONTAL_FACING, openFacing);
        } else if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction localFacing = state.getValue(BlockStateProperties.FACING);
            Direction rotatedFacing = rotation.rotateDirection(localFacing);
            finalState = state.setValue(BlockStateProperties.FACING, rotatedFacing);
        }
        level.setBlock(worldPos, finalState, flags);
    }

    protected Direction determineOpenFacing(ServerLevel level, BlockPos pos, Direction preferredFacing) {
        if (!level.getBlockState(pos.relative(preferredFacing)).isSolid()) {
            return preferredFacing;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(pos.relative(dir)).isSolid()) {
                return dir;
            }
        }
        return preferredFacing;
    }

    protected void buildBaseShell(ServerLevel level, BlockPos origin, DungeonRotation rotation, int size,
                                  DungeonSocket north, DungeonSocket east, DungeonSocket south, DungeonSocket west) {
        int midMin = size / 2 - 1;
        int midMax = size / 2 + 1;

        for (int lx = 0; lx < size; lx++) {
            for (int lz = 0; lz < size; lz++) {
                BlockState floorState = (lx + lz) % 2 == 0
                        ? Blocks.POLISHED_DEEPSLATE.defaultBlockState()
                        : Blocks.DEEPSLATE_BRICKS.defaultBlockState();
                setLocalBlock(level, origin, rotation, lx, 0, lz, size, floorState);

                BlockPos worldFloor = origin.offset(rotation.rotateOffset(lx, 0, lz, size));
                for (int fy = 1; fy <= 3; fy++) {
                    BlockPos foundationPos = worldFloor.below(fy);
                    if (level.getBlockState(foundationPos).isAir()) {
                        level.setBlock(foundationPos, Blocks.DEEPSLATE.defaultBlockState(), 2);
                    }
                }

                BlockState ceilingState = (lx + lz) % 2 == 0
                        ? Blocks.POLISHED_DEEPSLATE.defaultBlockState()
                        : SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState();
                setLocalBlock(level, origin, rotation, lx, 5, lz, size, ceilingState);

                for (int ly = 1; ly <= 4; ly++) {
                    boolean isNorthWall = (lz == 0);
                    boolean isSouthWall = (lz == size - 1);
                    boolean isWestWall = (lx == 0);
                    boolean isEastWall = (lx == size - 1);

                    if (isNorthWall || isSouthWall || isWestWall || isEastWall) {
                        boolean isDoorway = false;
                        if (isNorthWall && north == DungeonSocket.CORRIDOR && lx >= midMin && lx <= midMax && ly <= 3) {
                            isDoorway = true;
                        } else if (isSouthWall && south == DungeonSocket.CORRIDOR && lx >= midMin && lx <= midMax && ly <= 3) {
                            isDoorway = true;
                        } else if (isEastWall && east == DungeonSocket.CORRIDOR && lz >= midMin && lz <= midMax && ly <= 3) {
                            isDoorway = true;
                        } else if (isWestWall && west == DungeonSocket.CORRIDOR && lz >= midMin && lz <= midMax && ly <= 3) {
                            isDoorway = true;
                        }

                        if (isDoorway) {
                            setLocalBlock(level, origin, rotation, lx, ly, lz, size, Blocks.AIR.defaultBlockState());
                        } else {
                            int pattern = (lx * 3 + lz * 7 + ly) % 6;
                            BlockState wallState;
                            if (pattern == 0) {
                                wallState = SandStormBlocks.WEATHERED_REINFORCED_CONCRETE.defaultBlockState();
                            } else if (pattern == 1) {
                                wallState = SandStormBlocks.REINFORCED_CONCRETE.defaultBlockState();
                            } else if (pattern == 2) {
                                wallState = Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState();
                            } else {
                                wallState = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
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
