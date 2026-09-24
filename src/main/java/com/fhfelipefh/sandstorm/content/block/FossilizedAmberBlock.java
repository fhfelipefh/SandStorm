package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FossilizedAmberBlock extends Block {

    public FossilizedAmberBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.08f) {
            double px = pos.getX() + random.nextDouble();
            double py = pos.getY() + 1.0;
            double pz = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.ENCHANT, px, py, pz, 0.0, 0.05, 0.0);
        }
    }
}
