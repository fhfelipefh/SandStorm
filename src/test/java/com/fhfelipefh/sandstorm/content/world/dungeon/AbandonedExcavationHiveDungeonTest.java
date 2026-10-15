package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbandonedExcavationHiveDungeonTest {

    @Test
    void shouldProvideAllExcavationHiveComponents() {
        List<DungeonComponent> components = ExcavationHiveCatalog.getComponents();
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
    void shouldProvideValidMacroDrillQuadrants() {
        DungeonComponent nw = ExcavationHiveCatalog.getMacroDrillQuadrantNW();
        DungeonComponent ne = ExcavationHiveCatalog.getMacroDrillQuadrantNE();
        DungeonComponent sw = ExcavationHiveCatalog.getMacroDrillQuadrantSW();
        DungeonComponent se = ExcavationHiveCatalog.getMacroDrillQuadrantSE();

        assertNotNull(nw);
        assertNotNull(ne);
        assertNotNull(sw);
        assertNotNull(se);

        assertTrue(nw.getSocket(DungeonDirection.EAST).matches(ne.getSocket(DungeonDirection.WEST)));
        assertTrue(nw.getSocket(DungeonDirection.SOUTH).matches(sw.getSocket(DungeonDirection.NORTH)));
        assertTrue(ne.getSocket(DungeonDirection.SOUTH).matches(se.getSocket(DungeonDirection.NORTH)));
        assertTrue(sw.getSocket(DungeonDirection.EAST).matches(se.getSocket(DungeonDirection.WEST)));
    }

    @Test
    void shouldGenerateValidConnectedFloorLayout() {
        List<DungeonComponent> components = ExcavationHiveCatalog.getComponents();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(5, 5, components);
        RandomSource random = RandomSource.create(42L);

        RotatedComponent[][] solution = solver.solve(random, 20);
        assertNotNull(solution);

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                RotatedComponent current = solution[x][z];
                assertNotNull(current);

                for (DungeonDirection dir : DungeonDirection.values()) {
                    int nx = x + dir.getStepX();
                    int nz = z + dir.getStepZ();
                    if (nx >= 0 && nx < 5 && nz >= 0 && nz < 5) {
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
    void shouldRespectBoundaryConstraintsOnExcavationFloor() {
        List<DungeonComponent> components = ExcavationHiveCatalog.getComponents();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(5, 5, components);
        RandomSource random = RandomSource.create(999L);

        RotatedComponent[][] solution = solver.solve(random, 20);
        assertNotNull(solution);

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                RotatedComponent cell = solution[x][z];
                if (z == 0) {
                    DungeonSocket north = cell.getSocket(DungeonDirection.NORTH);
                    assertTrue(north == DungeonSocket.WALL || north == DungeonSocket.SOLID);
                }
                if (x == 4) {
                    DungeonSocket east = cell.getSocket(DungeonDirection.EAST);
                    assertTrue(east == DungeonSocket.WALL || east == DungeonSocket.SOLID);
                }
                if (z == 4) {
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
    void shouldSolveMiddleFloorWithMacroDrill2x2() {
        List<DungeonComponent> components = ExcavationHiveCatalog.getComponents().stream()
                .filter(c -> !c.id().equals("excavation_entrance_shaft") && !c.id().equals("excavation_queens_reliquary_vault"))
                .toList();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(5, 5, components);
        solver.pinAnchor(3, 1, ExcavationHiveCatalog.getStairwellUpComponent(), DungeonRotation.NONE);
        solver.pinAnchor(3, 3, ExcavationHiveCatalog.getStairwellDownComponent(), DungeonRotation.NONE);

        solver.pinAnchor(1, 1, ExcavationHiveCatalog.getMacroDrillQuadrantNW(), DungeonRotation.NONE);
        solver.pinAnchor(2, 1, ExcavationHiveCatalog.getMacroDrillQuadrantNE(), DungeonRotation.NONE);
        solver.pinAnchor(1, 2, ExcavationHiveCatalog.getMacroDrillQuadrantSW(), DungeonRotation.NONE);
        solver.pinAnchor(2, 2, ExcavationHiveCatalog.getMacroDrillQuadrantSE(), DungeonRotation.NONE);

        RandomSource random = RandomSource.create(5555L);
        RotatedComponent[][] solution = solver.solve(random, 30);
        assertNotNull(solution);

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                RotatedComponent current = solution[x][z];
                assertNotNull(current);

                for (DungeonDirection dir : DungeonDirection.values()) {
                    int nx = x + dir.getStepX();
                    int nz = z + dir.getStepZ();
                    if (nx >= 0 && nx < 5 && nz >= 0 && nz < 5) {
                        RotatedComponent neighbor = solution[nx][nz];
                        assertTrue(current.getSocket(dir).matches(neighbor.getSocket(dir.getOpposite())));
                    }
                }
            }
        }
    }

    @Test
    void shouldSolveBottomFloorWithQueensVault() {
        List<DungeonComponent> components = ExcavationHiveCatalog.getComponents().stream()
                .filter(c -> !c.id().equals("excavation_stairwell_down") && !c.id().equals("excavation_entrance_shaft"))
                .toList();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(5, 5, components);
        solver.pinAnchor(3, 3, ExcavationHiveCatalog.getStairwellUpComponent(), DungeonRotation.NONE);
        solver.pinAnchor(1, 1, ExcavationHiveCatalog.getQueensReliquaryComponent(), DungeonRotation.NONE);

        RandomSource random = RandomSource.create(7777L);
        RotatedComponent[][] solution = solver.solve(random, 30);
        assertNotNull(solution);

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                RotatedComponent current = solution[x][z];
                assertNotNull(current);

                for (DungeonDirection dir : DungeonDirection.values()) {
                    int nx = x + dir.getStepX();
                    int nz = z + dir.getStepZ();
                    if (nx >= 0 && nx < 5 && nz >= 0 && nz < 5) {
                        RotatedComponent neighbor = solution[nx][nz];
                        assertTrue(current.getSocket(dir).matches(neighbor.getSocket(dir.getOpposite())));
                    }
                }
            }
        }
    }
}
