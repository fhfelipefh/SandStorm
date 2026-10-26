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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class NaniteWebSnareBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Block.box(4, 10, 4, 12, 16, 12);

    public NaniteWebSnareBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
            level.scheduleTick(pos, this, 10);
            return;
        }

        AABB scanBox = new AABB(pos.getX() + 0.1, pos.getY() - 5.0, pos.getZ() + 0.1, pos.getX() + 0.9, pos.getY(), pos.getZ() + 0.9);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, scanBox);
        if (!targets.isEmpty()) {
            triggerSnare(level, pos, state, targets.getFirst());
        } else {
            level.scheduleTick(pos, this, 10);
        }
    }

    public void triggerSnare(Level level, BlockPos pos, BlockState state, LivingEntity target) {
        level.setBlock(pos, state.setValue(ACTIVE, true), 3);
        level.playSound(null, pos, SoundEvents.CROSSBOW_SHOOT, SoundSource.BLOCKS, 1.0f, 1.6f);

        if (level instanceof ServerLevel serverLevel) {
            for (double y = pos.getY() - 0.2; y >= pos.getY() - 4.5; y -= 0.5) {
                serverLevel.sendParticles(ParticleTypes.WHITE_ASH, pos.getX() + 0.5, y, pos.getZ() + 0.5, 4, 0.1, 0.1, 0.1, 0.02);
                serverLevel.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, y, pos.getZ() + 0.5, 2, 0.05, 0.05, 0.05, 0.01);
            }

            if (target != null) {
                target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 35, 1, false, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 4, false, false, false));
                target.hurtServer(serverLevel, level.damageSources().stalagmite(), 4.0f);
            }
        }

        level.scheduleTick(pos, this, 100);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (entity instanceof LivingEntity living && !state.getValue(ACTIVE)) {
            triggerSnare(level, pos, state, living);
        }
    }
}
