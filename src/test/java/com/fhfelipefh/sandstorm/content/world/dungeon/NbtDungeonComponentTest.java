package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NbtDungeonComponentTest {

    @Test
    void rotationMappingMatchesMinecraftRotations() {
        assertEquals(Rotation.NONE, NbtDungeonComponent.toMinecraftRotation(DungeonRotation.NONE));
        assertEquals(Rotation.CLOCKWISE_90, NbtDungeonComponent.toMinecraftRotation(DungeonRotation.CLOCKWISE_90));
        assertEquals(Rotation.CLOCKWISE_180, NbtDungeonComponent.toMinecraftRotation(DungeonRotation.CLOCKWISE_180));
        assertEquals(Rotation.COUNTERCLOCKWISE_90, NbtDungeonComponent.toMinecraftRotation(DungeonRotation.CLOCKWISE_270));
    }

    @Test
    void componentPropertiesAndSocketsArePreserved() {
        Identifier templateId = Identifier.fromNamespaceAndPath("sandstorm", "dungeon/vault_room");
        Map<DungeonDirection, DungeonSocket> sockets = Map.of(
                DungeonDirection.NORTH, DungeonSocket.DOOR,
                DungeonDirection.SOUTH, DungeonSocket.WALL,
                DungeonDirection.EAST, DungeonSocket.CORRIDOR,
                DungeonDirection.WEST, DungeonSocket.WALL
        );

        NbtDungeonComponent component = new NbtDungeonComponent("vault_room", templateId, 15, true, sockets);

        assertEquals("vault_room", component.id());
        assertEquals(templateId, component.templateId());
        assertEquals(15, component.weight());
        assertTrue(component.isUnique());
        assertEquals(DungeonSocket.DOOR, component.getSocket(DungeonDirection.NORTH));
        assertEquals(DungeonSocket.WALL, component.getSocket(DungeonDirection.SOUTH));
        assertEquals(DungeonSocket.CORRIDOR, component.getSocket(DungeonDirection.EAST));
        assertEquals(DungeonSocket.WALL, component.getSocket(DungeonDirection.WEST));
        assertNotNull(component);
    }

    @Test
    void fallbackComponentIsRetainedWhenProvided() {
        Identifier templateId = Identifier.fromNamespaceAndPath("sandstorm", "dungeon/corridor");
        DungeonComponent fallback = AncientBunkerCatalog.getEntranceComponent();
        NbtDungeonComponent component = new NbtDungeonComponent("corridor", templateId, 50, false, Map.of(), fallback);

        assertFalse(component.isUnique());
        assertEquals(fallback, component.fallback());
    }
}
