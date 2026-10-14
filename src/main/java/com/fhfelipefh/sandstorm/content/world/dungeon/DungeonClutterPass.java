package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public interface DungeonClutterPass {

    void apply(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, RandomSource random);
}
