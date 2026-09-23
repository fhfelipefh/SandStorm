package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class PiezoQuartzBlock extends Block {

    public PiezoQuartzBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && entity instanceof Player && level.getRandom().nextFloat() < 0.25f) {
            triggerAcousticResonance(level, pos);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            triggerAcousticResonance(level, pos);
        }
        super.attack(state, level, pos, player);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide()) {
            triggerAcousticResonance(level, hit.getBlockPos());
        }
        super.onProjectileHit(level, state, hit, projectile);
    }

    public static void triggerAcousticResonance(Level level, BlockPos pos) {
        level.playSound(
                null,
                pos,
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.BLOCKS,
                1.0f,
                1.4f + level.getRandom().nextFloat() * 0.4f
        );
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    6,
                    0.3,
                    0.3,
                    0.3,
                    0.05
            );
        }
    }
}
