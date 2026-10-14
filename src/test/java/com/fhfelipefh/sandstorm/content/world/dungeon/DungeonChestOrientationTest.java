package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonChestOrientationTest {

    private static final Path CATALOG_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "content", "world", "dungeon", "AncientBunkerCatalog.java"
    );

    private static final Path GENERATOR_PATH = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "content", "world", "structure", "AncientBunkerGenerator.java"
    );

    @Test
    void rotationCorrectlyMapsFacingDirectionClockwise() {
        assertEquals(Direction.NORTH, DungeonRotation.NONE.rotateDirection(Direction.NORTH));
        assertEquals(Direction.EAST, DungeonRotation.CLOCKWISE_90.rotateDirection(Direction.NORTH));
        assertEquals(Direction.SOUTH, DungeonRotation.CLOCKWISE_180.rotateDirection(Direction.NORTH));
        assertEquals(Direction.WEST, DungeonRotation.CLOCKWISE_270.rotateDirection(Direction.NORTH));

        assertEquals(Direction.UP, DungeonRotation.CLOCKWISE_90.rotateDirection(Direction.UP));
        assertEquals(Direction.DOWN, DungeonRotation.CLOCKWISE_180.rotateDirection(Direction.DOWN));
    }

    @Test
    void deadEndChestPointsTowardsEntranceInAllRotations() {
        DungeonComponent deadEnd = new AncientBunkerCatalog.DeadEnd();
        assertEquals(DungeonSocket.CORRIDOR, deadEnd.getSocket(DungeonDirection.NORTH));

        for (DungeonRotation rotation : DungeonRotation.values()) {
            Direction rotatedFacing = rotation.rotateDirection(Direction.NORTH);
            DungeonDirection entranceDirection = switch (rotation) {
                case NONE -> DungeonDirection.NORTH;
                case CLOCKWISE_90 -> DungeonDirection.EAST;
                case CLOCKWISE_180 -> DungeonDirection.SOUTH;
                case CLOCKWISE_270 -> DungeonDirection.WEST;
            };
            assertEquals(entranceDirection.name(), rotatedFacing.name());
        }
    }

    @Test
    void catalogMustNotPlaceChestsWithoutFacingOrientation() throws IOException {
        String content = Files.readString(CATALOG_PATH);
        assertTrue(content.contains("Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH)"));
        assertFalse(content.contains("level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3)"));
    }

    @Test
    void generatorMustOrientChestsTowardsRoomInterior() throws IOException {
        String content = Files.readString(GENERATOR_PATH);
        assertTrue(content.contains("Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing1)"));
        assertTrue(content.contains("Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing2)"));
    }
}
