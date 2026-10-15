package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SubglacialIceSpikeBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public SubglacialIceSpikeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (entity instanceof LivingEntity living && !level.isClientSide()) {
            living.hurtServer((ServerLevel) level, level.damageSources().freeze(), 2.5F);
            living.setTicksFrozen(Math.min(living.getTicksRequiredToFreeze() + 60, living.getTicksFrozen() + 80));
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        if (entity instanceof LivingEntity living && !level.isClientSide()) {
            living.hurtServer((ServerLevel) level, level.damageSources().stalagmite(), (float) (fallDistance * 2.5));
            living.setTicksFrozen(living.getTicksRequiredToFreeze() + 100);
        }
    }
}
