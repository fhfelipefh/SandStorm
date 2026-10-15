package com.fhfelipefh.sandstorm.content.world.dungeon;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
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

                    if (state.is(SandStormBlocks.MILITARY_ARMOR_PLATE)) {
                        int r = random.nextInt(10);
                        if (r < 3) {
                            level.setBlock(pos, SandStormBlocks.WEATHERED_MILITARY_PLATE.defaultBlockState(), 2);
                        } else if (r < 5) {
                            level.setBlock(pos, SandStormBlocks.RUSTED_MILITARY_PLATE.defaultBlockState(), 2);
                        } else if (r == 5) {
                            level.setBlock(pos, SandStormBlocks.FRACTURED_MILITARY_PLATE.defaultBlockState(), 2);
                        }
                    } else if (state.is(SandStormBlocks.RUSTED_MILITARY_PLATE)) {
                        if (random.nextInt(5) == 0) {
                            level.setBlock(pos, SandStormBlocks.FRACTURED_MILITARY_PLATE.defaultBlockState(), 2);
                        }
                    } else if (state.is(SandStormBlocks.HAZARD_STRIPE_BLOCK)) {
                        int r = random.nextInt(6);
                        if (r < 2) {
                            level.setBlock(pos, SandStormBlocks.WEATHERED_HAZARD_STRIPE.defaultBlockState(), 2);
                        } else if (r == 2) {
                            level.setBlock(pos, SandStormBlocks.CORRODED_HAZARD_STRIPE.defaultBlockState(), 2);
                        }
                    } else if (state.is(SandStormBlocks.WEATHERED_HAZARD_STRIPE)) {
                        if (random.nextInt(4) == 0) {
                            level.setBlock(pos, SandStormBlocks.CORRODED_HAZARD_STRIPE.defaultBlockState(), 2);
                        }
                    } else if (state.is(SandStormBlocks.REINFORCED_VENT_DUCT)) {
                        int r = random.nextInt(6);
                        if (r < 2) {
                            level.setBlock(pos, SandStormBlocks.RUSTED_VENT_DUCT.defaultBlockState(), 2);
                        } else if (r == 2) {
                            level.setBlock(pos, SandStormBlocks.DAMAGED_VENT_DUCT.defaultBlockState(), 2);
                        }
                    } else if (state.is(SandStormBlocks.STEEL_VENT_GRATE)) {
                        int r = random.nextInt(6);
                        if (r < 2) {
                            level.setBlock(pos, SandStormBlocks.RUSTED_STEEL_VENT_GRATE.defaultBlockState(), 2);
                        } else if (r == 2) {
                            level.setBlock(pos, SandStormBlocks.BROKEN_STEEL_VENT_GRATE.defaultBlockState(), 2);
                        }
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
