package com.fhfelipefh.sandstorm.content.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class CompanionWalkNodeEvaluator extends WalkNodeEvaluator {
    @Override
    public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
        if (CompanionTrapHelper.isTamedCompanion(mob)) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = context.getBlockState(pos);
            if (CompanionTrapHelper.isTrapOrHazardBlock(state)) {
                return PathType.DAMAGING;
            }
            BlockState below = context.getBlockState(pos.below());
            if (CompanionTrapHelper.isTrapOrHazardBlock(below)) {
                return PathType.DAMAGING;
            }
        }
        return super.getPathTypeOfMob(context, x, y, z, mob);
    }

    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        if (CompanionTrapHelper.isTamedCompanion(this.mob)) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = context.getBlockState(pos);
            if (CompanionTrapHelper.isTrapOrHazardBlock(state)) {
                return PathType.DAMAGING;
            }
            BlockState below = context.getBlockState(pos.below());
            if (CompanionTrapHelper.isTrapOrHazardBlock(below)) {
                return PathType.DAMAGING;
            }
        }
        return super.getPathType(context, x, y, z);
    }
}

