package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CryoVaultClutterPass implements DungeonClutterPass {

    @Override
    public void apply(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, RandomSource random) {
        int totalX = gridWidth * roomSize;
        int totalZ = gridDepth * roomSize;

        for (int x = 0; x < totalX; x++) {
            for (int z = 0; z < totalZ; z++) {
                for (int y = 0; y <= 5; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.is(SandStormBlocks.CRYO_INSULATION)) {
                        int roll = random.nextInt(20);
                        if (roll < 3) {
                            level.setBlock(pos, SandStormBlocks.CHISELED_CRYO_INSULATION.defaultBlockState(), 2);
                        } else if (roll == 3) {
                            level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 2);
                        } else if (roll == 4) {
                            level.setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 2);
                        }
                    } else if (state.is(Blocks.POLISHED_DEEPSLATE) && random.nextInt(8) == 0) {
                        level.setBlock(pos, Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState(), 2);
                    } else if (state.isAir() && y == 4 && random.nextInt(25) == 0) {
                        BlockPos above = pos.above();
                        if (level.getBlockState(above).isSolid()) {
                            level.setBlock(pos, SandStormBlocks.SUBGLACIAL_ICE_SPIKE.defaultBlockState(), 2);
                        }
                    } else if (state.isAir() && y == 1 && random.nextInt(35) == 0) {
                        BlockPos below = pos.below();
                        if (level.getBlockState(below).isSolid()) {
                            boolean hasAdjacentSolid = level.getBlockState(pos.north()).isSolid()
                                    || level.getBlockState(pos.south()).isSolid()
                                    || level.getBlockState(pos.east()).isSolid()
                                    || level.getBlockState(pos.west()).isSolid();
                            if (hasAdjacentSolid) {
                                if (random.nextBoolean()) {
                                    level.setBlock(pos, SandStormBlocks.SUBGLACIAL_ICE_SPIKE.defaultBlockState(), 2);
                                } else {
                                    level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 2);
                                }
                            }
                        }
                    } else if (state.isAir() && random.nextInt(25) == 0) {
                        boolean downSolid = level.getBlockState(pos.below()).isSolid();
                        boolean upSolid = level.getBlockState(pos.above()).isSolid();
                        boolean northSolid = level.getBlockState(pos.north()).isSolid();
                        boolean southSolid = level.getBlockState(pos.south()).isSolid();
                        boolean eastSolid = level.getBlockState(pos.east()).isSolid();
                        boolean westSolid = level.getBlockState(pos.west()).isSolid();

                        if (downSolid || upSolid || northSolid || southSolid || eastSolid || westSolid) {
                            BlockState lichenState = Blocks.GLOW_LICHEN.defaultBlockState()
                                    .setValue(BlockStateProperties.DOWN, downSolid)
                                    .setValue(BlockStateProperties.UP, upSolid)
                                    .setValue(BlockStateProperties.NORTH, northSolid)
                                    .setValue(BlockStateProperties.SOUTH, southSolid)
                                    .setValue(BlockStateProperties.EAST, eastSolid)
                                    .setValue(BlockStateProperties.WEST, westSolid);
                            level.setBlock(pos, lichenState, 2);
                        }
                    }
                }
            }
        }
    }
}
