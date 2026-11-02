package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FiberglassSpikesBlock extends Block {
    public static final IntegerProperty WEAR = IntegerProperty.create("wear", 0, 4);

    private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 4.0, 15.0);
    private static final VoxelShape COLLISION_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);

    public FiberglassSpikesBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(WEAR, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WEAR);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        handleEntityInteraction(level, pos, state, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        handleEntityInteraction(level, pos, state, entity);
    }

    private void handleEntityInteraction(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Player player && player.isShiftKeyDown()) {
            return;
        }

        if (entity instanceof LivingEntity living) {
            living.makeStuckInBlock(state, new Vec3(0.2, 0.2, 0.2));
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2, false, false, false));

            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                if (living.hurtServer(serverLevel, serverLevel.damageSources().cactus(), 2.0f)) {
                    serverLevel.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 4, 0.15, 0.1, 0.15, 0.05);
                    int currentWear = state.getValue(WEAR);
                    if (currentWear >= 4) {
                        level.destroyBlock(pos, false);
                        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.2f);
                        serverLevel.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.05);
                    } else {
                        level.setBlock(pos, state.setValue(WEAR, currentWear + 1), 3);
                        level.playSound(null, pos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 0.8f, 1.4f);
                    }
                }
            }
        }
    }
}

