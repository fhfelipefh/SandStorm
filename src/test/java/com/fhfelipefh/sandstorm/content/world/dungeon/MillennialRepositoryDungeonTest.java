package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MillennialRepositoryDungeonTest {

    @Test
    void shouldProvideAllRepositoryComponents() {
        List<DungeonComponent> components = MillennialRepositoryCatalog.getComponents();
        assertNotNull(components);
        assertFalse(components.isEmpty());

        for (DungeonComponent component : components) {
            assertNotNull(component.id());
            assertTrue(component.weight() > 0);
            for (DungeonDirection dir : DungeonDirection.values()) {
                assertNotNull(component.getSocket(dir));
            }
        }
    }

    @Test
    void shouldGenerateValidConnectedFloorLayout() {
        List<DungeonComponent> components = MillennialRepositoryCatalog.getComponents();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(4, 4, components);
        RandomSource random = RandomSource.create(42L);

        RotatedComponent[][] solution = solver.solve(random, 20);
        assertNotNull(solution);

        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                RotatedComponent current = solution[x][z];
                assertNotNull(current);

                for (DungeonDirection dir : DungeonDirection.values()) {
                    int nx = x + dir.getStepX();
                    int nz = z + dir.getStepZ();
                    if (nx >= 0 && nx < 4 && nz >= 0 && nz < 4) {
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
    void shouldRespectBoundaryConstraintsOnRepositoryFloor() {
        List<DungeonComponent> components = MillennialRepositoryCatalog.getComponents();
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
    void shouldAlignVerticalTransitionsAcrossFloors() {
        int width = 4;
        int depth = 4;
        int floors = 3;
        List<DungeonComponent> components = MillennialRepositoryCatalog.getComponents();
        RandomSource random = RandomSource.create(777L);

        int prevTransX = -1;
        int prevTransZ = -1;

        for (int floor = 0; floor < floors; floor++) {
            WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(width, depth, components);
            int upX = prevTransX;
            int upZ = prevTransZ;
            int currDownX = -1;
            int currDownZ = -1;

            if (floor == 0) {
                int entryX = width / 2;
                int entryZ = 0;
                solver.pinAnchor(entryX, entryZ, MillennialRepositoryCatalog.getEntranceComponent(), DungeonRotation.NONE);

                currDownX = width - 1;
                currDownZ = depth - 1;
                solver.pinAnchor(currDownX, currDownZ, MillennialRepositoryCatalog.getStairwellDownComponent(), DungeonRotation.NONE);
                prevTransX = currDownX;
                prevTransZ = currDownZ;
            } else if (floor == floors - 1) {
                solver.pinAnchor(upX, upZ, MillennialRepositoryCatalog.getStairwellUpComponent(), DungeonRotation.NONE);

                int vaultX = (upX == 0) ? (width - 1) : 0;
                int vaultZ = (upZ == 0) ? (depth - 1) : 0;
                solver.pinAnchor(vaultX, vaultZ, MillennialRepositoryCatalog.getSpentFuelPoolVaultComponent(), DungeonRotation.NONE);
            } else {
                solver.pinAnchor(upX, upZ, MillennialRepositoryCatalog.getStairwellUpComponent(), DungeonRotation.NONE);

                currDownX = (upX == 0) ? (width - 1) : 0;
                currDownZ = (upZ == 0) ? (depth - 1) : 0;
                solver.pinAnchor(currDownX, currDownZ, MillennialRepositoryCatalog.getCargoHoistShaftComponent(), DungeonRotation.NONE);
                prevTransX = currDownX;
                prevTransZ = currDownZ;
            }

            RotatedComponent[][] solution = solver.solve(random, 20);
            assertNotNull(solution, "Solver failed to collapse floor " + floor);

            if (floor == 0) {
                assertEquals("repository_surface_access", solution[width / 2][0].id());
                assertEquals("repository_stairwell_down", solution[currDownX][currDownZ].id());
            } else if (floor == floors - 1) {
                assertEquals("repository_stairwell_up", solution[upX][upZ].id());
            } else {
                assertEquals("repository_stairwell_up", solution[upX][upZ].id());
                assertEquals("repository_cargo_hoist", solution[currDownX][currDownZ].id());
            }
        }
    }

    @Test
    void shouldRespectUniqueRepositoryComponents() {
        List<DungeonComponent> components = MillennialRepositoryCatalog.getComponents();
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

        assertTrue(counts.getOrDefault("repository_surface_access", 0) <= 1);
        assertTrue(counts.getOrDefault("repository_spent_fuel_vault", 0) <= 1);
        assertTrue(counts.getOrDefault("repository_workshop", 0) <= 1);
    }
}
