package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Map;

public class ModularDungeonBuilder {

    public static int build(ServerLevel level, BlockPos origin, int gridWidth, int gridDepth, int roomSize, List<DungeonComponent> components, Map<Long, RotatedComponent> anchors, DungeonClutterPass clutterPass, RandomSource random) {
        WaveFunctionCollapseSolver solver = new WaveFunctionCollapseSolver(gridWidth, gridDepth, components);
        if (anchors != null) {
            for (Map.Entry<Long, RotatedComponent> entry : anchors.entrySet()) {
                int ax = (int) (entry.getKey() >> 32);
                int az = (int) (long) entry.getKey();
                solver.pinAnchor(ax, az, entry.getValue().component(), entry.getValue().rotation());
            }
        }

        RotatedComponent[][] solution = solver.solve(random, 40);
        int roomCount = 0;

        for (int gx = 0; gx < gridWidth; gx++) {
            for (int gz = 0; gz < gridDepth; gz++) {
                RotatedComponent cell = solution[gx][gz];
                BlockPos cellOrigin = origin.offset(gx * roomSize, 0, gz * roomSize);
                cell.component().build(level, cellOrigin, cell.rotation(), random, roomSize);
                roomCount++;
            }
        }

        if (clutterPass != null) {
            clutterPass.apply(level, origin, gridWidth, gridDepth, roomSize, random);
        }

        return roomCount;
    }
}
