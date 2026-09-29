package com.fhfelipefh.sandstorm.content.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class QuantumNetworkHelper {

    public record NetworkCluster(
            QuantumNetworkControllerBlockEntity controller,
            List<QuantumDiskDriveBlockEntity> drives,
            int nodeCount
    ) {}

    public static NetworkCluster scanNetwork(Level level, BlockPos startPos) {
        if (level == null || startPos == null) {
            return new NetworkCluster(null, List.of(), 0);
        }

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);
        visited.add(startPos);

        QuantumNetworkControllerBlockEntity controller = null;
        List<QuantumDiskDriveBlockEntity> drives = new ArrayList<>();

        while (!queue.isEmpty() && visited.size() <= 256) {
            BlockPos current = queue.poll();
            BlockEntity be = level.getBlockEntity(current);

            if (be instanceof QuantumNetworkControllerBlockEntity cBe) {
                if (controller == null) {
                    controller = cBe;
                }
            } else if (be instanceof QuantumDiskDriveBlockEntity dBe) {
                drives.add(dBe);
            }

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (visited.contains(neighbor)) {
                    continue;
                }

                BlockEntity neighborBe = level.getBlockEntity(neighbor);
                boolean isNetworkBlock = neighborBe instanceof QuantumNetworkNode
                        || level.getBlockState(neighbor).getBlock() instanceof QuantumNetworkCableBlock;

                if (isNetworkBlock) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return new NetworkCluster(controller, drives, visited.size());
    }
}
