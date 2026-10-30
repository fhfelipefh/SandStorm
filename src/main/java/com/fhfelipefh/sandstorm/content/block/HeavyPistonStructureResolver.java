package com.fhfelipefh.sandstorm.content.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class HeavyPistonStructureResolver {
    public static final int MAX_PUSH_DEPTH = 24;
    private final Level level;
    private final BlockPos pistonPos;
    private final boolean extending;
    private final BlockPos startPos;
    private final Direction pushDirection;
    private final List<BlockPos> toPush = new ArrayList<>();
    private final List<BlockPos> toDestroy = new ArrayList<>();
    private final Direction pistonDirection;
    private final int maxDepth;
    private final boolean canPushObsidian;

    public HeavyPistonStructureResolver(Level level, BlockPos pistonPos, Direction pistonDirection, boolean extending) {
        this(level, pistonPos, pistonDirection, extending, MAX_PUSH_DEPTH, false);
    }

    public HeavyPistonStructureResolver(Level level, BlockPos pistonPos, Direction pistonDirection, boolean extending, int maxDepth, boolean canPushObsidian) {
        this.level = level;
        this.pistonPos = pistonPos;
        this.pistonDirection = pistonDirection;
        this.extending = extending;
        this.maxDepth = maxDepth;
        this.canPushObsidian = canPushObsidian;
        if (extending) {
            this.pushDirection = pistonDirection;
            this.startPos = pistonPos.relative(pistonDirection);
        } else {
            this.pushDirection = pistonDirection.getOpposite();
            this.startPos = pistonPos.relative(pistonDirection, 2);
        }
    }

    public boolean resolve() {
        this.toPush.clear();
        this.toDestroy.clear();
        BlockState startState = this.level.getBlockState(this.startPos);
        if (!HeavyKineticPistonBlock.isPushable(startState, this.level, this.startPos, this.pushDirection, false, this.pistonDirection, this.canPushObsidian)) {
            if (this.extending && startState.getPistonPushReaction() == PushReaction.POPPED) {
                this.toDestroy.add(this.startPos);
                return true;
            }
            return false;
        }
        if (!this.addBlockLine(this.startPos, this.pushDirection)) {
            return false;
        }
        for (int i = 0; i < this.toPush.size(); i++) {
            BlockPos currentPos = this.toPush.get(i);
            if (isSticky(this.level.getBlockState(currentPos)) && !this.addBranchingBlocks(currentPos)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSticky(BlockState state) {
        return state.is(Blocks.SLIME_BLOCK) || state.is(Blocks.HONEY_BLOCK);
    }

    private static boolean canStickToEachOther(BlockState a, BlockState b) {
        if (a.is(Blocks.HONEY_BLOCK) && b.is(Blocks.SLIME_BLOCK)) {
            return false;
        }
        if (a.is(Blocks.SLIME_BLOCK) && b.is(Blocks.HONEY_BLOCK)) {
            return false;
        }
        return isSticky(a) || isSticky(b);
    }

    private boolean addBlockLine(BlockPos origin, Direction fromDir) {
        BlockState state = this.level.getBlockState(origin);
        if (state.isAir()) {
            return true;
        }
        if (!HeavyKineticPistonBlock.isPushable(state, this.level, origin, this.pushDirection, false, fromDir, this.canPushObsidian)) {
            return true;
        }
        if (origin.equals(this.pistonPos)) {
            return true;
        }
        if (this.toPush.contains(origin)) {
            return true;
        }
        int count = 1;
        if (count + this.toPush.size() > this.maxDepth) {
            return false;
        }
        while (isSticky(state)) {
            BlockPos nextPos = origin.relative(this.pushDirection.getOpposite(), count);
            BlockState previousState = state;
            state = this.level.getBlockState(nextPos);
            if (state.isAir() || !canStickToEachOther(previousState, state) || !HeavyKineticPistonBlock.isPushable(state, this.level, nextPos, this.pushDirection, false, this.pushDirection.getOpposite(), this.canPushObsidian) || nextPos.equals(this.pistonPos)) {
                break;
            }
            count++;
            if (count + this.toPush.size() > this.maxDepth) {
                return false;
            }
        }
        int added = 0;
        for (int i = count - 1; i >= 0; i--) {
            this.toPush.add(origin.relative(this.pushDirection.getOpposite(), i));
            added++;
        }
        int forwardIndex = 1;
        while (true) {
            BlockPos forwardPos = origin.relative(this.pushDirection, forwardIndex);
            int collisionIndex = this.toPush.indexOf(forwardPos);
            if (collisionIndex > -1) {
                this.reorderListAtCollision(added, collisionIndex);
                for (int k = 0; k <= collisionIndex + added; k++) {
                    BlockPos p = this.toPush.get(k);
                    if (isSticky(this.level.getBlockState(p)) && !this.addBranchingBlocks(p)) {
                        return false;
                    }
                }
                return true;
            }
            state = this.level.getBlockState(forwardPos);
            if (state.isAir()) {
                return true;
            }
            if (!HeavyKineticPistonBlock.isPushable(state, this.level, forwardPos, this.pushDirection, true, this.pushDirection, this.canPushObsidian) || forwardPos.equals(this.pistonPos)) {
                return false;
            }
            if (state.getPistonPushReaction() == PushReaction.POPPED) {
                this.toDestroy.add(forwardPos);
                return true;
            }
            if (this.toPush.size() >= this.maxDepth) {
                return false;
            }
            this.toPush.add(forwardPos);
            added++;
            forwardIndex++;
        }
    }

    private void reorderListAtCollision(int count, int collisionIndex) {
        List<BlockPos> head = new ArrayList<>(this.toPush.subList(0, collisionIndex));
        List<BlockPos> tail = new ArrayList<>(this.toPush.subList(this.toPush.size() - count, this.toPush.size()));
        List<BlockPos> middle = new ArrayList<>(this.toPush.subList(collisionIndex, this.toPush.size() - count));
        this.toPush.clear();
        this.toPush.addAll(head);
        this.toPush.addAll(tail);
        this.toPush.addAll(middle);
    }

    private boolean addBranchingBlocks(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        for (Direction dir : Direction.values()) {
            if (dir.getAxis() != this.pushDirection.getAxis()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = this.level.getBlockState(neighborPos);
                if (canStickToEachOther(neighborState, state) && !this.addBlockLine(neighborPos, dir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Direction getPushDirection() {
        return this.pushDirection;
    }

    public List<BlockPos> getToPush() {
        return this.toPush;
    }

    public List<BlockPos> getToDestroy() {
        return this.toDestroy;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public boolean canPushObsidian() {
        return this.canPushObsidian;
    }
}
