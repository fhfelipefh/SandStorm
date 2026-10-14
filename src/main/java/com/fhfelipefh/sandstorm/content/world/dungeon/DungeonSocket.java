package com.fhfelipefh.sandstorm.content.world.dungeon;

public enum DungeonSocket {
    WALL,
    CORRIDOR,
    DOOR,
    SOLID;

    public boolean matches(DungeonSocket other) {
        if (this == other) {
            return true;
        }
        if ((this == CORRIDOR && other == DOOR) || (this == DOOR && other == CORRIDOR)) {
            return true;
        }
        return (this == WALL && other == SOLID) || (this == SOLID && other == WALL);
    }
}
