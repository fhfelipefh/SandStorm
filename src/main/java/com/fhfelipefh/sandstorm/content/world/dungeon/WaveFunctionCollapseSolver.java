package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.util.RandomSource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WaveFunctionCollapseSolver {

    private final int width;
    private final int depth;
    private final List<RotatedComponent> allVariants;
    private final Map<Long, RotatedComponent> pinnedAnchors = new HashMap<>();

    public WaveFunctionCollapseSolver(int width, int depth, List<DungeonComponent> components) {
        this.width = width;
        this.depth = depth;
        List<RotatedComponent> variants = new ArrayList<>();
        for (DungeonComponent component : components) {
            for (DungeonRotation rotation : DungeonRotation.values()) {
                variants.add(new RotatedComponent(component, rotation));
            }
        }
        this.allVariants = Collections.unmodifiableList(variants);
    }

    public void pinAnchor(int x, int z, DungeonComponent component, DungeonRotation rotation) {
        pinnedAnchors.put(pack(x, z), new RotatedComponent(component, rotation));
    }

    public RotatedComponent[][] solve(RandomSource random, int maxAttempts) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            RotatedComponent[][] result = trySolve(random);
            if (result != null) {
                return result;
            }
        }
        return solveFallback();
    }

    @SuppressWarnings("unchecked")
    private RotatedComponent[][] trySolve(RandomSource random) {
        List<RotatedComponent>[][] grid = new List[width][depth];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                grid[x][z] = new ArrayList<>(allVariants);
            }
        }

        Queue<Long> propagationQueue = new ArrayDeque<>();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                boolean changed = false;
                List<RotatedComponent> candidates = grid[x][z];
                List<RotatedComponent> valid = new ArrayList<>(candidates.size());
                for (RotatedComponent candidate : candidates) {
                    if (isBoundaryValid(x, z, candidate)) {
                        valid.add(candidate);
                    }
                }
                if (valid.size() != candidates.size()) {
                    grid[x][z] = valid;
                    changed = true;
                }
                if (changed) {
                    propagationQueue.add(pack(x, z));
                }
            }
        }

        for (Map.Entry<Long, RotatedComponent> entry : pinnedAnchors.entrySet()) {
            int x = unpackX(entry.getKey());
            int z = unpackZ(entry.getKey());
            if (x >= 0 && x < width && z >= 0 && z < depth) {
                grid[x][z] = new ArrayList<>(List.of(entry.getValue()));
                propagationQueue.add(pack(x, z));
            }
        }

        if (!propagate(grid, propagationQueue)) {
            return null;
        }

        Set<String> placedUniqueIds = new HashSet<>();
        for (Map.Entry<Long, RotatedComponent> entry : pinnedAnchors.entrySet()) {
            if (entry.getValue().isUnique()) {
                placedUniqueIds.add(entry.getValue().id());
            }
        }

        while (true) {
            int bestX = -1;
            int bestZ = -1;
            int minEntropy = Integer.MAX_VALUE;

            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    int count = grid[x][z].size();
                    if (count > 1 && count < minEntropy) {
                        minEntropy = count;
                        bestX = x;
                        bestZ = z;
                    }
                }
            }

            if (bestX == -1) {
                break;
            }

            List<RotatedComponent> candidates = grid[bestX][bestZ];
            RotatedComponent chosen = selectWeighted(candidates, placedUniqueIds, random);
            if (chosen == null) {
                return null;
            }

            grid[bestX][bestZ] = new ArrayList<>(List.of(chosen));
            if (chosen.isUnique()) {
                placedUniqueIds.add(chosen.id());
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < depth; z++) {
                        if (x != bestX || z != bestZ) {
                            List<RotatedComponent> otherList = grid[x][z];
                            if (otherList.removeIf(c -> c.id().equals(chosen.id()))) {
                                propagationQueue.add(pack(x, z));
                            }
                        }
                    }
                }
            }

            propagationQueue.add(pack(bestX, bestZ));
            if (!propagate(grid, propagationQueue)) {
                return null;
            }
        }

        RotatedComponent[][] solution = new RotatedComponent[width][depth];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                if (grid[x][z].isEmpty()) {
                    return null;
                }
                solution[x][z] = grid[x][z].get(0);
            }
        }
        return solution;
    }

    private boolean propagate(List<RotatedComponent>[][] grid, Queue<Long> queue) {
        while (!queue.isEmpty()) {
            long currentPacked = queue.poll();
            int cx = unpackX(currentPacked);
            int cz = unpackZ(currentPacked);
            List<RotatedComponent> currentCandidates = grid[cx][cz];

            for (DungeonDirection dir : DungeonDirection.values()) {
                int nx = cx + dir.getStepX();
                int nz = cz + dir.getStepZ();

                if (nx < 0 || nx >= width || nz < 0 || nz >= depth) {
                    continue;
                }

                List<RotatedComponent> neighborCandidates = grid[nx][nz];
                List<RotatedComponent> remaining = new ArrayList<>(neighborCandidates.size());

                for (RotatedComponent nCand : neighborCandidates) {
                    DungeonSocket nSocket = nCand.getSocket(dir.getOpposite());
                    boolean hasMatch = false;
                    for (RotatedComponent cCand : currentCandidates) {
                        DungeonSocket cSocket = cCand.getSocket(dir);
                        if (cSocket.matches(nSocket)) {
                            hasMatch = true;
                            break;
                        }
                    }
                    if (hasMatch) {
                        remaining.add(nCand);
                    }
                }

                if (remaining.isEmpty()) {
                    return false;
                }

                if (remaining.size() < neighborCandidates.size()) {
                    grid[nx][nz] = remaining;
                    queue.add(pack(nx, nz));
                }
            }
        }
        return true;
    }

    private boolean isBoundaryValid(int x, int z, RotatedComponent candidate) {
        if (z == 0 && !isWallOrSolid(candidate.getSocket(DungeonDirection.NORTH))) {
            return false;
        }
        if (x == width - 1 && !isWallOrSolid(candidate.getSocket(DungeonDirection.EAST))) {
            return false;
        }
        if (z == depth - 1 && !isWallOrSolid(candidate.getSocket(DungeonDirection.SOUTH))) {
            return false;
        }
        return x != 0 || isWallOrSolid(candidate.getSocket(DungeonDirection.WEST));
    }

    private boolean isWallOrSolid(DungeonSocket socket) {
        return socket == DungeonSocket.WALL || socket == DungeonSocket.SOLID;
    }

    private RotatedComponent selectWeighted(List<RotatedComponent> candidates, Set<String> placedUniqueIds, RandomSource random) {
        List<RotatedComponent> filtered = new ArrayList<>();
        int totalWeight = 0;
        for (RotatedComponent candidate : candidates) {
            if (candidate.isUnique() && placedUniqueIds.contains(candidate.id())) {
                continue;
            }
            int weight = Math.max(1, candidate.weight());
            totalWeight += weight;
            filtered.add(candidate);
        }

        if (filtered.isEmpty()) {
            return null;
        }

        int roll = random.nextInt(totalWeight);
        int current = 0;
        for (RotatedComponent candidate : filtered) {
            current += Math.max(1, candidate.weight());
            if (roll < current) {
                return candidate;
            }
        }
        return filtered.get(filtered.size() - 1);
    }

    private RotatedComponent[][] solveFallback() {
        RotatedComponent fallback = null;
        for (RotatedComponent variant : allVariants) {
            boolean allWall = true;
            for (DungeonDirection dir : DungeonDirection.values()) {
                if (!isWallOrSolid(variant.getSocket(dir))) {
                    allWall = false;
                    break;
                }
            }
            if (allWall) {
                fallback = variant;
                break;
            }
        }

        if (fallback == null) {
            fallback = allVariants.get(0);
        }

        RotatedComponent[][] grid = new RotatedComponent[width][depth];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                grid[x][z] = fallback;
            }
        }
        return grid;
    }

    private static long pack(int x, int z) {
        return (((long) x) << 32) | (z & 0xFFFFFFFFL);
    }

    private static int unpackX(long packed) {
        return (int) (packed >> 32);
    }

    private static int unpackZ(long packed) {
        return (int) packed;
    }
}
