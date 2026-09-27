package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ThermalSpringStoneBlock extends Block {

    public ThermalSpringStoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (entity instanceof LivingEntity living) {
            living.setTicksFrozen(0);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.25f) {
            double px = pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double py = pos.getY() + 1.0;
            double pz = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 0.0, 0.03, 0.0);
        }
    }
}
