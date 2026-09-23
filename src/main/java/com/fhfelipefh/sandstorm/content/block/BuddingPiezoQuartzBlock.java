package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class BuddingPiezoQuartzBlock extends Block {

    public BuddingPiezoQuartzBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            Direction direction = Direction.getRandom(random);
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.isAir() || (targetState.is(Blocks.WATER) && targetState.getFluidState().isSource())) {
                boolean waterlogged = targetState.getFluidState().getType() == Fluids.WATER;
                BlockState clusterState = SandStormBlocks.PIEZO_QUARTZ_CLUSTER.defaultBlockState()
                        .setValue(PiezoQuartzClusterBlock.FACING, direction)
                        .setValue(PiezoQuartzClusterBlock.WATERLOGGED, waterlogged);
                level.setBlockAndUpdate(targetPos, clusterState);
            }
        }
    }
}
