package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.entity.MirageDecoyEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

public class MirageHologramTrapBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 2, 14);

    public MirageHologramTrapBlock(Properties properties) {
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
            triggerHologram(level, pos, state);
        }
    }

    public void triggerHologram(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(ACTIVE, true), 3);
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.8f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 25, 0.3, 0.4, 0.3, 0.1);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 15, 0.2, 0.3, 0.2, 0.05);

            MirageDecoyEntity decoy = new MirageDecoyEntity(SandStormEntities.MIRAGE_DECOY, level);
            decoy.setPos(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5);
            serverLevel.addFreshEntity(decoy);

            AABB aggroArea = new AABB(pos).inflate(18.0);
            List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, aggroArea);
            for (Mob mob : nearbyMobs) {
                if (mob != decoy) {
                    mob.setTarget(decoy);
                }
            }
        }

        level.scheduleTick(pos, this, 140);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
            level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.7f, 1.6f);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4, pos.getY() + 0.3, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4, 0, 0.1, 0);
        }
    }
}
