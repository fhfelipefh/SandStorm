package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.FiberglassChemicalPipeBlock;
import com.fhfelipefh.sandstorm.content.block.SmartFluidPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;
import java.util.Map;

public class ModularDungeonBuilder {

    public static int build(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, List<DungeonComponent> components, Map<Long, RotatedComponent> anchors, DungeonClutterPass clutterPass, RandomSource random) {
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(gridWidth, gridDepth, components);
        if (anchors != null) {
            for (Map.Entry<Long, RotatedComponent> entry : anchors.entrySet()) {
                int ax = (int) (entry.getKey() >> 32);
                int az = (int) (long) entry.getKey();
                solver.pinAnchor(ax, az, entry.getValue().component(), entry.getValue().rotation());
            }
        }

        RotatedComponent[][] solution = solver.solve(random, 40);
        int roomCount = 0;

        for (int gx = 0; gx < gridWidth; gx++) {
            for (int gz = 0; gz < gridDepth; gz++) {
                RotatedComponent cell = solution[gx][gz];
                BlockPos cellOrigin = origin.offset(gx * roomSize, 0, gz * roomSize);
                cell.component().build(level, cellOrigin, cell.rotation(), random, roomSize);
                roomCount++;
            }
        }

        if (clutterPass != null) {
            clutterPass.apply(level, origin, gridWidth, gridDepth, roomSize, random);
        }

        connectDungeonPipes(level, origin, gridWidth, gridDepth, roomSize);

        return roomCount;
    }

    public static void connectDungeonPipes(LevelAccessor level, BlockPos origin, int gridWidth, int gridDepth, int roomSize) {
        int maxX = gridWidth * roomSize;
        int maxZ = gridDepth * roomSize;

        for (int x = 0; x < maxX; x++) {
            for (int z = 0; z < maxZ; z++) {
                for (int y = roomSize; y >= 1; y--) {
                    BlockPos worldPos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(worldPos);
                    if (state.getBlock() instanceof FiberglassChemicalPipeBlock || state.getBlock() instanceof SmartFluidPipeBlock) {
                        BlockPos checkPos = worldPos.below();
                        while (checkPos.getY() >= origin.getY() + 1) {
                            BlockState checkState = level.getBlockState(checkPos);
                            if (checkState.isAir()) {
                                level.setBlock(checkPos, state.getBlock().defaultBlockState(), 2);
                                checkPos = checkPos.below();
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (int x = 0; x < maxX; x++) {
            for (int z = 0; z < maxZ; z++) {
                for (int y = 1; y <= roomSize; y++) {
                    BlockPos worldPos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(worldPos);
                    if (state.getBlock() instanceof FiberglassChemicalPipeBlock) {
                        BlockState updated = state;
                        for (Map.Entry<Direction, BooleanProperty> entry : FiberglassChemicalPipeBlock.PROPERTY_BY_DIRECTION.entrySet()) {
                            Direction dir = entry.getKey();
                            BooleanProperty prop = entry.getValue();
                            BlockPos neighborPos = worldPos.relative(dir);
                            BlockState neighborState = level.getBlockState(neighborPos);
                            boolean connect = (neighborState.getBlock() instanceof FiberglassChemicalPipeBlock)
                                    || FiberglassChemicalPipeBlock.canConnect(level, worldPos, dir)
                                    || (dir == Direction.DOWN && neighborState.isSolid());
                            updated = updated.setValue(prop, connect);
                        }
                        if (updated != state) {
                            level.setBlock(worldPos, updated, 2);
                        }
                    } else if (state.getBlock() instanceof SmartFluidPipeBlock) {
                        BlockState updated = state;
                        for (Map.Entry<Direction, BooleanProperty> entry : SmartFluidPipeBlock.PROPERTY_BY_DIRECTION.entrySet()) {
                            Direction dir = entry.getKey();
                            BooleanProperty prop = entry.getValue();
                            BlockPos neighborPos = worldPos.relative(dir);
                            BlockState neighborState = level.getBlockState(neighborPos);
                            boolean connect = (neighborState.getBlock() instanceof SmartFluidPipeBlock)
                                    || (dir == Direction.DOWN && neighborState.isSolid());
                            updated = updated.setValue(prop, connect);
                        }
                        if (updated != state) {
                            level.setBlock(worldPos, updated, 2);
                        }
                    }
                }
            }
        }
    }
}
