package com.fhfelipefh.sandstorm.content.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThermalVentTrapBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    public ThermalVentTrapBlock(Properties properties) {
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            triggerThermalVentEruption(level, pos, state);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide() && level.hasNeighborSignal(pos) && !state.getValue(ACTIVE)) {
            triggerThermalVentEruption(level, pos, state);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (entity instanceof LivingEntity living) {
            triggerThermalVent(level, pos, state, living);
        }
    }

    public void triggerThermalVentEruption(Level level, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 14, 0.25, 0.6, 0.25, 0.08);
            serverLevel.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 8, 0.2, 0.3, 0.2, 0.05);

            AABB burnArea = new AABB(pos).inflate(0.5, 1.5, 0.5);
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, burnArea);
            for (LivingEntity living : victims) {
                living.igniteForSeconds(4.0f);
                living.hurtServer(serverLevel, level.damageSources().hotFloor(), 5.0f);
                living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
            }
        }
        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.3f);
        if (!state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, true), 3);
            level.scheduleTick(pos, this, 40);
        }
    }

    private void triggerThermalVent(Level level, BlockPos pos, BlockState state, LivingEntity entity) {
        entity.igniteForSeconds(4.0f);
        if (level instanceof ServerLevel serverLevel) {
            entity.hurtServer(serverLevel, level.damageSources().hotFloor(), 5.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));

            serverLevel.sendParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pos.getX() + 0.5,
                    pos.getY() + 0.2,
                    pos.getZ() + 0.5,
                    10,
                    0.2,
                    0.5,
                    0.2,
                    0.08
            );
            serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    pos.getX() + 0.5,
                    pos.getY() + 0.1,
                    pos.getZ() + 0.5,
                    6,
                    0.15,
                    0.2,
                    0.15,
                    0.04
            );
        }

        level.playSound(
                null,
                pos,
                SoundEvents.LAVA_EXTINGUISH,
                SoundSource.BLOCKS,
                1.0f,
                1.3f
        );

        if (!state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, true), 3);
            level.scheduleTick(pos, this, 40);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.35f) {
            double x = pos.getX() + 0.2 + (random.nextDouble() * 0.6);
            double y = pos.getY() + 0.15;
            double z = pos.getZ() + 0.2 + (random.nextDouble() * 0.6);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.04, 0.0);
            if (state.getValue(ACTIVE)) {
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0.0, 0.08, 0.0);
            }
        }
    }
}
