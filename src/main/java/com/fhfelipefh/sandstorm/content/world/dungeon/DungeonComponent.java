package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public interface DungeonComponent {

    String id();

    int weight();

    boolean isUnique();

    DungeonSocket getSocket(DungeonDirection direction);

    void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size);
}
