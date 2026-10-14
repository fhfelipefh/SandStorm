package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaveFunctionCollapseSolverTest {

    @Test
    void shouldGenerateValidConnectedDungeonLayout() {
        List<DungeonComponent> components = AncientBunkerCatalog.getComponents();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(3, 3, components);
        RandomSource random = RandomSource.create(42L);

        RotatedComponent[][] solution = solver.solve(random, 20);
        assertNotNull(solution);

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                RotatedComponent current = solution[x][z];
                assertNotNull(current);

                for (DungeonDirection dir : DungeonDirection.values()) {
                    int nx = x + dir.getStepX();
                    int nz = z + dir.getStepZ();
                    if (nx >= 0 && nx < 3 && nz >= 0 && nz < 3) {
                        RotatedComponent neighbor = solution[nx][nz];
                        DungeonSocket currentSocket = current.getSocket(dir);
                        DungeonSocket neighborSocket = neighbor.getSocket(dir.getOpposite());
                        assertTrue(currentSocket.matches(neighborSocket),
                                "Socket mismatch between (" + x + "," + z + ") and (" + nx + "," + nz + "): "
                                        + currentSocket + " vs " + neighborSocket);
                    }
                }
            }
        }
    }

    @Test
    void shouldRespectBoundaryConstraints() {
        List<DungeonComponent> components = AncientBunkerCatalog.getComponents();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(4, 4, components);
        RandomSource random = RandomSource.create(999L);

        RotatedComponent[][] solution = solver.solve(random, 20);
        assertNotNull(solution);

        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                RotatedComponent cell = solution[x][z];
                if (z == 0) {
                    DungeonSocket north = cell.getSocket(DungeonDirection.NORTH);
                    assertTrue(north == DungeonSocket.WALL || north == DungeonSocket.SOLID);
                }
                if (x == 3) {
                    DungeonSocket east = cell.getSocket(DungeonDirection.EAST);
                    assertTrue(east == DungeonSocket.WALL || east == DungeonSocket.SOLID);
                }
                if (z == 3) {
                    DungeonSocket south = cell.getSocket(DungeonDirection.SOUTH);
                    assertTrue(south == DungeonSocket.WALL || south == DungeonSocket.SOLID);
                }
                if (x == 0) {
                    DungeonSocket west = cell.getSocket(DungeonDirection.WEST);
                    assertTrue(west == DungeonSocket.WALL || west == DungeonSocket.SOLID);
                }
            }
        }
    }

    @Test
    void shouldRespectUniqueComponents() {
        List<DungeonComponent> components = AncientBunkerCatalog.getComponents();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(3, 3, components);
        RandomSource random = RandomSource.create(12345L);

        RotatedComponent[][] solution = solver.solve(random, 20);
        assertNotNull(solution);

        Map<String, Integer> counts = new HashMap<>();
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                RotatedComponent cell = solution[x][z];
                counts.put(cell.id(), counts.getOrDefault(cell.id(), 0) + 1);
            }
        }

        assertTrue(counts.getOrDefault("bunker_data_core_vault", 0) <= 1);
        assertTrue(counts.getOrDefault("bunker_energy_chamber", 0) <= 1);
        assertTrue(counts.getOrDefault("bunker_entrance", 0) <= 1);
    }

    @Test
    void shouldRespectPinnedAnchor() {
        List<DungeonComponent> components = AncientBunkerCatalog.getComponents();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(3, 3, components);
        DungeonComponent entrance = AncientBunkerCatalog.getEntranceComponent();
        solver.pinAnchor(1, 0, entrance, DungeonRotation.NONE);
        RandomSource random = RandomSource.create(777L);

        RotatedComponent[][] solution = solver.solve(random, 20);
        assertNotNull(solution);
        assertEquals("bunker_entrance", solution[1][0].id());
        assertEquals(DungeonRotation.NONE, solution[1][0].rotation());
    }

    @Test
    void shouldRotateCoordinatesCorrectly() {
        int size = 7;
        BlockPos northEdge = new BlockPos(3, 1, 0);

        BlockPos rot90 = DungeonRotation.CLOCKWISE_90.rotateOffset(northEdge.getX(), northEdge.getY(), northEdge.getZ(), size);
        assertEquals(new BlockPos(6, 1, 3), rot90);

        BlockPos rot180 = DungeonRotation.CLOCKWISE_180.rotateOffset(northEdge.getX(), northEdge.getY(), northEdge.getZ(), size);
        assertEquals(new BlockPos(3, 1, 6), rot180);

        BlockPos rot270 = DungeonRotation.CLOCKWISE_270.rotateOffset(northEdge.getX(), northEdge.getY(), northEdge.getZ(), size);
        assertEquals(new BlockPos(0, 1, 3), rot270);
    }
}
