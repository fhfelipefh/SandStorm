package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbandonedMilitaryLabDungeonTest {

    @Test
    void shouldProvideAllMilitaryLabComponents() {
        List<DungeonComponent> components = MilitaryLabCatalog.getComponents();
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
        List<DungeonComponent> components = MilitaryLabCatalog.getComponents();
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
    void shouldRespectBoundaryConstraintsOnMilitaryLabFloor() {
        List<DungeonComponent> components = MilitaryLabCatalog.getComponents();
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
    void shouldPinAnchorsCorrectly() {
        List<DungeonComponent> components = MilitaryLabCatalog.getComponents();
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(5, 5, components);
        solver.pinAnchor(2, 0, MilitaryLabCatalog.getEntranceComponent(), DungeonRotation.NONE);
        solver.pinAnchor(2, 2, MilitaryLabCatalog.getStairwellDownComponent(), DungeonRotation.NONE);

        RandomSource random = RandomSource.create(12345L);
        RotatedComponent[][] solution = solver.solve(random, 20);
        assertNotNull(solution);

        assertEquals("military_lab_entrance", solution[2][0].component().id());
        assertEquals("military_lab_stairwell_down", solution[2][2].component().id());
    }

    @Test
    void shouldFilterStairwellsPerFloor() {
        List<DungeonComponent> all = MilitaryLabCatalog.getComponents();

        List<DungeonComponent> floor0 = all.stream()
                .filter(c -> !c.id().equals("military_lab_stairwell_up"))
                .toList();
        assertTrue(floor0.stream().noneMatch(c -> c.id().equals("military_lab_stairwell_up")));
        assertTrue(floor0.stream().anyMatch(c -> c.id().equals("military_lab_stairwell_down")));

        List<DungeonComponent> bottomFloor = all.stream()
                .filter(c -> !c.id().equals("military_lab_stairwell_down"))
                .toList();
        assertTrue(bottomFloor.stream().noneMatch(c -> c.id().equals("military_lab_stairwell_down")));
        assertTrue(bottomFloor.stream().anyMatch(c -> c.id().equals("military_lab_stairwell_up")));
    }
}
