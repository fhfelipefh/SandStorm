package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public class MountainCliffDetector {

    public record CliffSite(BlockPos entranceOrigin, Direction outwardFacing, int cliffHeight, int mountainDepth, int mountainWidth) {}

    @FunctionalInterface
    public interface HeightSampler {
        int getHeight(int x, int z);
    }

    public static CliffSite findSuitableCliff(ServerLevel level, ChunkAccess chunk, int minWidth, int minHeight, int minDepth, int minOverburden) {
        for (int localX = 2; localX <= 13; localX += 3) {
            for (int localZ = 2; localZ <= 13; localZ += 3) {
                BlockPos centerPos = chunk.getPos().getBlockAt(localX, 64, localZ);
                int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, localX, localZ);
                if (surfaceY < 80) {
                    continue;
                }

                for (Direction testFacing : Direction.Plane.HORIZONTAL) {
                    BlockPos frontCheckPos = centerPos.relative(testFacing, 10);
                    int frontSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, frontCheckPos.getX(), frontCheckPos.getZ());
                    int heightDifference = surfaceY - frontSurfaceY;

                    if (heightDifference >= minHeight) {
                        BlockPos entranceCandidate = new BlockPos(frontCheckPos.getX(), frontSurfaceY, frontCheckPos.getZ()).relative(testFacing.getOpposite(), 4);
                        if (validateEncasement(level, entranceCandidate, testFacing, minWidth, minHeight, minDepth, minOverburden)) {
                            return new CliffSite(entranceCandidate, testFacing, heightDifference, minDepth, minWidth);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean validateEncasement(LevelReader level, BlockPos entranceOrigin, Direction outwardFacing, int width, int height, int depth, int minOverburden) {
        return validateEncasement((x, z) -> level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), entranceOrigin, outwardFacing, width, height, depth, minOverburden);
    }

    public static boolean validateEncasement(HeightSampler sampler, BlockPos entranceOrigin, Direction outwardFacing, int width, int height, int depth, int minOverburden) {
        Direction inwardFacing = outwardFacing.getOpposite();
        Direction lateral = outwardFacing.getClockWise();

        int halfWidth = width / 2;
        int requiredCeilingY = entranceOrigin.getY() + height + minOverburden;

        int openFrontAirSamples = 0;
        int totalFrontSamples = 0;

        for (int lat = -halfWidth; lat <= halfWidth; lat += Math.max(1, width / 4)) {
            for (int frontOffset = 1; frontOffset <= 4; frontOffset++) {
                BlockPos frontPos = entranceOrigin.relative(lateral, lat).relative(outwardFacing, frontOffset);
                totalFrontSamples++;
                int frontHeight = sampler.getHeight(frontPos.getX(), frontPos.getZ());
                if (frontHeight <= entranceOrigin.getY() + 4) {
                    openFrontAirSamples++;
                }
            }
        }

        if (totalFrontSamples > 0 && ((double) openFrontAirSamples / totalFrontSamples) < 0.70) {
            return false;
        }

        for (int lat = -halfWidth; lat <= halfWidth; lat += Math.max(1, width / 6)) {
            for (int dep = 2; dep <= depth; dep += Math.max(1, depth / 6)) {
                BlockPos interiorSamplePos = entranceOrigin.relative(lateral, lat).relative(inwardFacing, dep);
                int terrainSurfaceY = sampler.getHeight(interiorSamplePos.getX(), interiorSamplePos.getZ());
                if (terrainSurfaceY < requiredCeilingY) {
                    return false;
                }
            }
        }

        for (int lat = -halfWidth; lat <= halfWidth; lat += Math.max(1, width / 4)) {
            BlockPos backPos = entranceOrigin.relative(lateral, lat).relative(inwardFacing, depth);
            int backTerrainY = sampler.getHeight(backPos.getX(), backPos.getZ());
            if (backTerrainY < requiredCeilingY) {
                return false;
            }
        }

        for (int dep = 2; dep <= depth; dep += Math.max(1, depth / 4)) {
            BlockPos leftPos = entranceOrigin.relative(lateral, -halfWidth).relative(inwardFacing, dep);
            BlockPos rightPos = entranceOrigin.relative(lateral, halfWidth).relative(inwardFacing, dep);
            if (sampler.getHeight(leftPos.getX(), leftPos.getZ()) < requiredCeilingY) {
                return false;
            }
            if (sampler.getHeight(rightPos.getX(), rightPos.getZ()) < requiredCeilingY) {
                return false;
            }
        }

        return true;
    }
}
