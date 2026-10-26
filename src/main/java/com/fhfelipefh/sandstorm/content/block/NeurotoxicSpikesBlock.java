package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NeurotoxicSpikesBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 9, 15);

    public NeurotoxicSpikesBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        applyVenom(level, pos, state, entity);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        applyVenom(level, pos, state, entity);
    }

    private void applyVenom(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity living) {
            entity.makeStuckInBlock(state, new Vec3(0.8, 0.75, 0.8));
            if (level instanceof ServerLevel serverLevel && entity.tickCount % 20 == 0) {
                living.hurtServer(serverLevel, level.damageSources().cactus(), 3.5f);
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 140, 1, false, true, true));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 0, false, true, true));
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1, false, true, true));
                serverLevel.sendParticles(ParticleTypes.ITEM_SLIME, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 5, 0.2, 0.1, 0.2, 0.05);
                level.playSound(null, pos, SoundEvents.THORNS_HIT, SoundSource.BLOCKS, 0.8f, 1.4f);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.25f) {
            double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double y = pos.getY() + 0.15;
            double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            level.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 0, 0.02, 0);
        }
    }
}
