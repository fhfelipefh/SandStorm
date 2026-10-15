package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ExcavationHiveClutterPass implements DungeonClutterPass {

    @Override
    public void apply(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, RandomSource random) {
        int totalX = gridWidth * roomSize;
        int totalZ = gridDepth * roomSize;

        for (int x = 0; x < totalX; x++) {
            for (int z = 0; z < totalZ; z++) {
                for (int y = 0; y <= 5; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.is(SandStormBlocks.REINFORCED_CONCRETE)) {
                        if (random.nextInt(10) < 3) {
                            level.setBlock(pos, SandStormBlocks.WEATHERED_REINFORCED_CONCRETE.defaultBlockState(), 2);
                        } else if (random.nextInt(10) == 3) {
                            level.setBlock(pos, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState(), 2);
                        }
                    } else if (state.is(Blocks.SANDSTONE) && random.nextInt(8) == 0) {
                        level.setBlock(pos, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 2);
                    } else if (state.is(Blocks.DEEPSLATE_BRICKS) && random.nextInt(6) == 0) {
                        level.setBlock(pos, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState(), 2);
                    } else if (state.isAir() && y == 4 && random.nextInt(20) == 0) {
                        BlockPos above = pos.above();
                        if (level.getBlockState(above).isSolid()) {
                            if (random.nextBoolean()) {
                                level.setBlock(pos, Blocks.IRON_CHAIN.defaultBlockState(), 2);
                            } else {
                                level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 2);
                            }
                        }
                    } else if (state.isAir() && y == 1 && random.nextInt(40) == 0) {
                        BlockPos below = pos.below();
                        if (level.getBlockState(below).isSolid()) {
                            boolean hasAdjacentSolid = level.getBlockState(pos.north()).isSolid()
                                    || level.getBlockState(pos.south()).isSolid()
                                    || level.getBlockState(pos.east()).isSolid()
                                    || level.getBlockState(pos.west()).isSolid();
                            if (hasAdjacentSolid) {
                                if (random.nextBoolean()) {
                                    level.setBlock(pos, Blocks.SCAFFOLDING.defaultBlockState(), 2);
                                } else {
                                    level.setBlock(pos, Blocks.GRAVEL.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
