package com.fhfelipefh.sandstorm.content.world.dungeon;

public enum DungeonSocket {
    WALL,
    CORRIDOR,
    DOOR,
    SOLID,
    VAULT_DOOR,
    VAULT_INTERIOR;

    public boolean matches(DungeonSocket other) {
        if (this == other) {
            return true;
        }
        if ((this == CORRIDOR && other == DOOR) || (this == DOOR && other == CORRIDOR)) {
            return true;
        }
        if ((this == VAULT_DOOR && other == VAULT_INTERIOR) || (this == VAULT_INTERIOR && other == VAULT_DOOR)) {
            return true;
        }
        return (this == WALL && other == SOLID) || (this == SOLID && other == WALL);
    }
}
