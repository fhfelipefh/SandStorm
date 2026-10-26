package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NaniteQuicksandBlock extends Block {
    private static final VoxelShape COLLISION_SHAPE = Block.box(0, 0, 0, 16, 14, 16);

    public NaniteQuicksandBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        if (type == PathComputationType.LAND) {
            return true;
        }
        return super.isPathfindable(state, type);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        entity.makeStuckInBlock(state, new Vec3(0.25, 0.05, 0.25));
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x * 0.4, -0.06, motion.z * 0.4);

        if (entity instanceof LivingEntity living && level instanceof ServerLevel serverLevel) {
            if (entity.tickCount % 20 == 0) {
                living.hurtServer(serverLevel, level.damageSources().inWall(), 2.0f);
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2, false, false, false));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, false, false, false));
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.25f) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 0.95;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0.0, 0.02, 0.0);
        }
    }
}
