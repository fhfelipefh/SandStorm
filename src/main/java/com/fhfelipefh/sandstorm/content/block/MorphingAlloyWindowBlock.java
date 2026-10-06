package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class MorphingAlloyWindowBlock extends TransparentBlock {

    public MorphingAlloyWindowBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .strength(50.0f, 1200.0f)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.05f) {
            double px = pos.getX() + random.nextDouble();
            double py = pos.getY() + random.nextDouble();
            double pz = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0.0, 0.02, 0.0);
        }
    }
}
