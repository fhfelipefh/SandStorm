package com.fhfelipefh.sandstorm.content.survival;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnSafety {

    public static boolean isSafePosition(BlockGetter level, BlockPos pos) {
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());
        BlockState floor = level.getBlockState(pos.below());

        if (!feet.getCollisionShape(level, pos).isEmpty() || !head.getCollisionShape(level, pos.above()).isEmpty()) {
            return false;
        }

        if (feet.isSuffocating(level, pos) || head.isSuffocating(level, pos.above())) {
            return false;
        }

        if (feet.is(Blocks.LAVA) || head.is(Blocks.LAVA) || feet.is(Blocks.FIRE) || head.is(Blocks.FIRE)) {
            return false;
        }

        if (!feet.getFluidState().isEmpty() && feet.getFluidState().isSource()) {
            return false;
        }

        return !floor.getCollisionShape(level, pos.below()).isEmpty() || floor.isFaceSturdy(level, pos.below(), Direction.UP);
    }

    public static BlockPos findSafeSpawnPosition(ServerLevel level, BlockPos targetPos, int searchRadius) {
        if (isSafePosition(level, targetPos)) {
            return targetPos;
        }

        for (int r = 1; r <= searchRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    for (int dy = -2; dy <= 5; dy++) {
                        BlockPos candidate = targetPos.offset(dx, dy, dz);
                        if (isSafePosition(level, candidate)) {
                            return candidate;
                        }
                    }
                }
            }
        }

        ensureSafePocket(level, targetPos);
        return targetPos;
    }

    public static void ensureSafePocket(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        BlockState floor = level.getBlockState(pos.below());
        if (floor.isAir() || !floor.isFaceSturdy(level, pos.below(), Direction.UP)) {
            level.setBlock(pos.below(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3);
        }
    }

    public static void teleportSafely(ServerPlayer player, BlockPos targetPos) {
        ServerLevel level = player.level();
        BlockPos safePos = findSafeSpawnPosition(level, targetPos, 5);
        ensureSafePocket(level, safePos);
        player.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
        player.setAirSupply(player.getMaxAirSupply());
        player.heal(player.getMaxHealth());
        player.resetFallDistance();
        player.clearFire();
    }
}
