package com.fhfelipefh.sandstorm.content.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ShelterDetectionHelper {

    private ShelterDetectionHelper() {}

    public static boolean isSheltered(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        if (!level.canSeeSky(pos) || !level.canSeeSky(pos.above())) {
            return true;
        }
        int maxCheck = Math.min(level.getMaxY(), pos.getY() + 32);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = pos.getY() + 1; y <= maxCheck; y++) {
            cursor.set(pos.getX(), y, pos.getZ());
            BlockState state = level.getBlockState(cursor);
            if (!state.isAir() && !state.canBeReplaced()) {
                return true;
            }
        }
        int wallCount = 0;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            for (int dist = 1; dist <= 6; dist++) {
                cursor.set(pos.getX() + dir.getStepX() * dist, pos.getY(), pos.getZ() + dir.getStepZ() * dist);
                BlockState state = level.getBlockState(cursor);
                if (!state.isAir() && (state.isSolid() || !state.canBeReplaced())) {
                    wallCount++;
                    break;
                }
            }
        }
        if (wallCount >= 3) {
            int overheadCheck = Math.min(level.getMaxY(), pos.getY() + 48);
            for (int y = pos.getY() + 1; y <= overheadCheck; y++) {
                cursor.set(pos.getX(), y, pos.getZ());
                BlockState state = level.getBlockState(cursor);
                if (!state.isAir()) {
                    return true;
                }
            }
        }
        return false;
    }
}
