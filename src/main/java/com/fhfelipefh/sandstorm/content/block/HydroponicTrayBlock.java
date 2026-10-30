package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HydroponicTrayBlock extends Block {

    public HydroponicTrayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);

        if (aboveState.getBlock() instanceof CropBlock crop) {
            int age = crop.getAge(aboveState);
            if (age < crop.getMaxAge()) {
                level.setBlock(above, crop.getStateForAge(age + 1), 2);
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, above.getX() + 0.5, above.getY() + 0.3, above.getZ() + 0.5, 6, 0.3, 0.2, 0.3, 0.05);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            double px = pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double py = pos.getY() + 0.85;
            double pz = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            level.addParticle(ParticleTypes.SPLASH, px, py, pz, 0.0, 0.02, 0.0);
            level.addParticle(ParticleTypes.GLOW, px, py + 0.1, pz, 0.0, 0.01, 0.0);
        }
    }
}
