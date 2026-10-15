package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BrittlePermafrostBlock extends Block {

    public BrittlePermafrostBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (entity.isCrouching()) {
            return;
        }

        if (entity.isSprinting() || !(entity instanceof Player)) {
            triggerFracture(level, pos);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        if (fallDistance > 0.4) {
            triggerImmediateFracture(level, pos);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.destroyBlock(pos, false);
    }

    private void triggerFracture(Level level, BlockPos pos) {
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.9F, 1.8F);
            level.scheduleTick(pos, this, 6);
        }
    }

    private void triggerImmediateFracture(Level level, BlockPos pos) {
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.6F);
            level.scheduleTick(pos, this, 2);
        }
    }
}
