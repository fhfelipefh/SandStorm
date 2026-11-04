package com.fhfelipefh.sandstorm.content.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;

public class CompanionGroundPathNavigation extends GroundPathNavigation {
    public CompanionGroundPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new CompanionWalkNodeEvaluator();
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }
}

