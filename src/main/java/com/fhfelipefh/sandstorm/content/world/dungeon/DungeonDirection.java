package com.fhfelipefh.sandstorm.content.world.dungeon;

public enum DungeonDirection {
    NORTH(0, 0, -1),
    EAST(1, 1, 0),
    SOUTH(2, 0, 1),
    WEST(3, -1, 0);

    private final int index;
    private final int stepX;
    private final int stepZ;

    DungeonDirection(int index, int stepX, int stepZ) {
        this.index = index;
        this.stepX = stepX;
        this.stepZ = stepZ;
    }

    public int getIndex() {
        return index;
    }

    public int getStepX() {
        return stepX;
    }

    public int getStepZ() {
        return stepZ;
    }

    public DungeonDirection getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EAST;
        };
    }

    public static DungeonDirection fromIndex(int index) {
        return switch ((index % 4 + 4) % 4) {
            case 0 -> NORTH;
            case 1 -> EAST;
            case 2 -> SOUTH;
            case 3 -> WEST;
            default -> NORTH;
        };
    }
}
