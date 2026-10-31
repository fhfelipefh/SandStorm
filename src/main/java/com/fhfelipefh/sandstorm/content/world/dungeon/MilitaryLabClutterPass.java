package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.RustingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MilitaryLabClutterPass implements DungeonClutterPass {

    @Override
    public void apply(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, RandomSource random) {
        int totalX = gridWidth * roomSize;
        int totalZ = gridDepth * roomSize;

        for (int x = 0; x < totalX; x++) {
            for (int z = 0; z < totalZ; z++) {
                for (int y = 0; y <= 5; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    BlockState canonical = RustingBlock.convertLegacy(state);
                    if (canonical.getBlock() instanceof RustingBlock) {
                        level.setBlock(pos, canonical.setValue(RustingBlock.RUST, RustingBlock.selectRuinRust(random)), 2);
                    } else if (state.is(SandStormBlocks.MILITARY_MUNITIONS_CRATE)) {
                        if (random.nextInt(3) == 0) {
                            level.setBlock(pos, SandStormBlocks.DAMAGED_MUNITIONS_CRATE.defaultBlockState(), 2);
                        }
                    } else if (state.is(SandStormBlocks.TACTICAL_RADAR_CONSOLE)) {
                        if (random.nextInt(3) == 0) {
                            level.setBlock(pos, SandStormBlocks.BROKEN_RADAR_CONSOLE.defaultBlockState(), 2);
                        }
                    } else if (state.isAir() && y == 4 && random.nextInt(25) == 0) {
                        BlockPos above = pos.above();
                        if (level.getBlockState(above).isSolid()) {
                            level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 2);
                        }
                    } else if (state.isAir() && y == 1 && random.nextInt(35) == 0) {
                        BlockPos below = pos.below();
                        if (level.getBlockState(below).isSolid()) {
                            boolean nextToWall = level.getBlockState(pos.north()).isSolid()
                                    || level.getBlockState(pos.south()).isSolid()
                                    || level.getBlockState(pos.east()).isSolid()
                                    || level.getBlockState(pos.west()).isSolid();
                            if (nextToWall && random.nextBoolean()) {
                                BlockState crateState = random.nextBoolean()
                                        ? SandStormBlocks.MILITARY_MUNITIONS_CRATE.defaultBlockState()
                                        : SandStormBlocks.DAMAGED_MUNITIONS_CRATE.defaultBlockState();
                                level.setBlock(pos, crateState, 2);
                            } else {
                                level.setBlock(pos, Blocks.REDSTONE_WIRE.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }
}
