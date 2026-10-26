package com.fhfelipefh.sandstorm.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class MicroSingularityTrapBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 2, 15);

    public MicroSingularityTrapBlock(Properties properties) {
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
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (entity instanceof LivingEntity && !state.getValue(ACTIVE)) {
            triggerSingularity(level, pos, state);
        }
    }

    public void triggerSingularity(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(ACTIVE, true), 3);
        level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.8f, 1.8f);

        AABB pullArea = new AABB(pos).inflate(7.0);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, pullArea);
        for (LivingEntity target : targets) {
            Vec3 diff = new Vec3(pos.getX() + 0.5 - target.getX(), pos.getY() + 0.6 - target.getY(), pos.getZ() + 0.5 - target.getZ());
            double dist = diff.length();
            if (dist > 0.2) {
                Vec3 pull = diff.normalize().scale(Math.min(0.65, 2.5 / (dist + 0.5)));
                target.setDeltaMovement(pull.x, 0.28, pull.z);
            }
        }

        level.scheduleTick(pos, this, 30);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 30, 0.4, 0.4, 0.4, 0.2);
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.3, 0.3, 0.3, 0.1);

            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.0f, 1.8f);

            AABB blastArea = new AABB(pos).inflate(3.5);
            List<LivingEntity> blastTargets = level.getEntitiesOfClass(LivingEntity.class, blastArea);
            for (LivingEntity target : blastTargets) {
                target.hurtServer(level, level.damageSources().explosion(null, null), 8.0f);
            }

            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double y = pos.getY() + 0.2 + random.nextDouble() * 0.4;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            level.addParticle(ParticleTypes.PORTAL, x, y, z, 0.0, 0.05, 0.0);
        }
    }
}
