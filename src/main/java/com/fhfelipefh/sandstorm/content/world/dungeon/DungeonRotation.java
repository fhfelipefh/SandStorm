package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public enum DungeonRotation {
    NONE(0),
    CLOCKWISE_90(1),
    CLOCKWISE_180(2),
    CLOCKWISE_270(3);

    private final int steps;

    DungeonRotation(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public BlockPos rotateOffset(int localX, int localY, int localZ, int size) {
        return switch (this) {
            case NONE -> new BlockPos(localX, localY, localZ);
            case CLOCKWISE_90 -> new BlockPos(size - 1 - localZ, localY, localX);
            case CLOCKWISE_180 -> new BlockPos(size - 1 - localX, localY, size - 1 - localZ);
            case CLOCKWISE_270 -> new BlockPos(localZ, localY, size - 1 - localX);
        };
    }

    public Direction rotateDirection(Direction direction) {
        if (direction.getAxis().isVertical()) {
            return direction;
        }
        Direction current = direction;
        for (int i = 0; i < steps; i++) {
            current = current.getClockWise();
        }
        return current;
    }
}
