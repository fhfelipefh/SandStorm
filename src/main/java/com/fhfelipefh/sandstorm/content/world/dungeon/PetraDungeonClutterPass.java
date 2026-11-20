package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.ClayUrnBlock;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.TerracottaAmphoraBlock;
import com.fhfelipefh.sandstorm.content.entity.CrawlerDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CuratorSentinelEntity;
import com.fhfelipefh.sandstorm.content.entity.DerelictAutomatonEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PetraDungeonClutterPass implements DungeonClutterPass {

    @Override
    public void apply(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, RandomSource random) {
        int totalX = gridWidth * roomSize;
        int totalZ = gridDepth * roomSize;

        for (int x = 0; x < totalX; x++) {
            for (int z = 0; z < totalZ; z++) {
                for (int y = 0; y <= 5; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.is(Blocks.RED_SANDSTONE) && random.nextInt(8) == 0) {
                        level.setBlock(pos, Blocks.CUT_RED_SANDSTONE.defaultBlockState(), 2);
                    } else if (state.is(Blocks.CUT_RED_SANDSTONE) && random.nextInt(10) == 0) {
                        level.setBlock(pos, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState(), 2);
                    } else if (state.isAir() && y == 1) {
                        BlockPos floorPos = pos.below();
                        if (level.getBlockState(floorPos).isSolid()) {
                            int roll = random.nextInt(90);
                            if (roll < 3) {
                                level.setBlock(pos, Blocks.RED_SAND.defaultBlockState(), 2);
                            } else if (roll < 6) {
                                Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                                level.setBlock(pos, SandStormBlocks.TERRACOTTA_AMPHORA.defaultBlockState().setValue(TerracottaAmphoraBlock.FACING, facing), 3);
                            } else if (roll < 8) {
                                Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                                level.setBlock(pos, SandStormBlocks.CLAY_URN.defaultBlockState().setValue(ClayUrnBlock.FACING, facing), 3);
                            } else if (roll == 9) {
                                BlockPos wallCheck = pos.relative(Direction.NORTH);
                                if (level.getBlockState(wallCheck).isSolid()) {
                                    level.setBlock(pos, SandStormBlocks.SANDSTONE_STATUE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 3);
                                }
                            } else if (roll == 10) {
                                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 2);
                                level.setBlock(pos.above(), Blocks.COAL_BLOCK.defaultBlockState(), 2);
                            }
                        }
                    } else if (state.isAir() && y == 4 && random.nextInt(40) == 0) {
                        BlockPos ceilingPos = pos.above();
                        if (level.getBlockState(ceilingPos).isSolid()) {
                            level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        carveNaturalSkylightFissures(level, origin, gridWidth, gridDepth, roomSize, random);
        spawnEncounterEntities(level, origin, gridWidth, gridDepth, roomSize, random);
    }

    private void carveNaturalSkylightFissures(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, RandomSource random) {
        int fissures = 1 + random.nextInt(2);
        for (int i = 0; i < fissures; i++) {
            int gx = 1 + random.nextInt(Math.max(1, gridWidth - 2));
            int gz = 1 + random.nextInt(Math.max(1, gridDepth - 2));
            BlockPos centerRoom = origin.offset(gx * roomSize + roomSize / 2, 5, gz * roomSize + roomSize / 2);

            for (int dx = -1; dx <= 0; dx++) {
                for (int dz = -1; dz <= 0; dz++) {
                    BlockPos columnPos = centerRoom.offset(dx, 0, dz);
                    for (int y = columnPos.getY(); y < level.getMaxY(); y++) {
                        BlockPos carvePos = new BlockPos(columnPos.getX(), y, columnPos.getZ());
                        if (level.getBlockState(carvePos).isAir()) {
                            break;
                        }
                        level.setBlock(carvePos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private void spawnEncounterEntities(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, RandomSource random) {
        for (int gx = 0; gx < gridWidth; gx++) {
            for (int gz = 0; gz < gridDepth; gz++) {
                if (gx == gridWidth / 2 && gz == 0) {
                    continue;
                }
                BlockPos centerRoom = origin.offset(gx * roomSize + roomSize / 2, 1, gz * roomSize + roomSize / 2);
                if (!level.getBlockState(centerRoom).isAir() || !level.getBlockState(centerRoom.below()).isSolid()) {
                    continue;
                }

                int encounterRoll = random.nextInt(10);
                if (encounterRoll < 3) {
                    CuratorSentinelEntity curator = new CuratorSentinelEntity(SandStormEntities.CURATOR_SENTINEL, level);
                    curator.setPos(centerRoom.getX() + 0.5, centerRoom.getY(), centerRoom.getZ() + 0.5);
                    level.addFreshEntity(curator);
                } else if (encounterRoll < 6) {
                    DerelictAutomatonEntity automaton = new DerelictAutomatonEntity(SandStormEntities.DERELICT_AUTOMATON, level);
                    automaton.setPos(centerRoom.getX() + 0.5, centerRoom.getY(), centerRoom.getZ() + 0.5);
                    if (random.nextInt(3) == 0) {
                        automaton.setDeactivated(true);
                    }
                    level.addFreshEntity(automaton);
                } else if (encounterRoll < 8) {
                    CrawlerDroneEntity drone = new CrawlerDroneEntity(SandStormEntities.CRAWLER_DRONE, level);
                    drone.setPos(centerRoom.getX() + 0.5, centerRoom.getY(), centerRoom.getZ() + 0.5);
                    level.addFreshEntity(drone);
                }
            }
        }
    }
}
