package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class XenoGrassBlock extends Block {
    public XenoGrassBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.is(Blocks.SAND) || belowState.is(Blocks.RED_SAND)) {
            level.setBlockAndUpdate(belowPos, Blocks.DIRT.defaultBlockState());
        }

        if (random.nextInt(4) == 0) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos targetPos = pos.relative(dir);
            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.is(Blocks.SAND) || targetState.is(Blocks.RED_SAND)) {
                if (level.canSeeSky(targetPos.above()) || level.getMaxLocalRawBrightness(targetPos.above()) >= 9) {
                    level.setBlockAndUpdate(targetPos, defaultBlockState());
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && (player == null || !player.getAbilities().instabuild)) {
            popResource(level, pos, new ItemStack(SandStormItems.XENO_GRASS_SEEDS, 1 + level.getRandom().nextInt(2)));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
