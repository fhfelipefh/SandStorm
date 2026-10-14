package com.fhfelipefh.sandstorm.content.world.dungeon;

public record RotatedComponent(DungeonComponent component, DungeonRotation rotation) {

    public DungeonSocket getSocket(DungeonDirection worldDirection) {
        int localIndex = (worldDirection.getIndex() - rotation.getSteps() + 4) % 4;
        return component.getSocket(DungeonDirection.fromIndex(localIndex));
    }

    public String id() {
        return component.id();
    }

    public int weight() {
        return component.weight();
    }

    public boolean isUnique() {
        return component.isUnique();
    }
}
